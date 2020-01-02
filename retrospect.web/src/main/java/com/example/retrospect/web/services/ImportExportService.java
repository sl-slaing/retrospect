package com.example.retrospect.web.services;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.managers.ActionPermissionManager;
import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.TenantState;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.core.services.TenantService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.import_export.ExportRequest;
import com.example.retrospect.web.models.import_export.ExportSettings;
import com.example.retrospect.web.models.import_export.ImportRequest;
import com.example.retrospect.web.models.import_export.DataExport;
import com.example.retrospect.web.services.import_export.ExportAdapter;
import com.example.retrospect.web.services.import_export.ImportAdapter;
import com.example.retrospect.web.services.import_export.ImportResult;
import com.example.retrospect.web.services.import_export.RetrospectiveImporterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportExportService {
    private final RetrospectiveService retrospectiveService;
    private final TenantService tenantService;
    private final ExportAdapter exportAdapter;
    private final ImportAdapter importAdapter;
    private final UserSessionManager userSessionManager;
    private final RetrospectiveImporterFactory importerFactory;
    private final ActionPermissionManager actionPermissionManager;

    @Autowired
    public ImportExportService(
            RetrospectiveService retrospectiveService,
            TenantService tenantService,
            ExportAdapter exportAdapter,
            ImportAdapter importAdapter,
            UserSessionManager userSessionManager,
            RetrospectiveImporterFactory importerFactory,
            ActionPermissionManager actionPermissionManager) {
        this.retrospectiveService = retrospectiveService;
        this.tenantService = tenantService;
        this.exportAdapter = exportAdapter;
        this.importAdapter = importAdapter;
        this.userSessionManager = userSessionManager;
        this.importerFactory = importerFactory;
        this.actionPermissionManager = actionPermissionManager;
    }

    public ImportResult importData(ImportRequest importRequest) {
        var loggedInUser = userSessionManager.getLoggedInUser();

        var version = importRequest.getVersion();
        if (version == null) {
            throw new RuntimeException("Invalid import request, no version supplied");
        }

        if (importRequest.getDataItems() == null){
            throw new RuntimeException("Invalid import request, no data items supplied");
        }

        var result = new ImportResult();
        var settings = importRequest.getSettings();
        if (settings.isDryRun()) {
            result.addMessage("This is a dry run import; no changes will be applied");
        }

        if (settings.isRestoreData()) {
            if (!actionPermissionManager.canRestore(loggedInUser)) {
                result.setSuccess(false);
                result.addMessage("You're not permitted to restore data");
                return result;
            }

            switch (importRequest.getType()) {
                case RETROSPECTIVE:
                    var removedRetrospectives = settings.isDryRun()
                            ? retrospectiveService.getAllRetrospectives(loggedInUser).collect(Collectors.toSet())
                            : retrospectiveService.removeAllRetrospectives(loggedInUser);
                    result.addMessage("All existing retrospectives (" + removedRetrospectives.size() + ") have been purged");
                    break;
                case TENANT:
                    result.addMessage("Not implemented");
                    break;
            }
        } else if (!actionPermissionManager.canImport(loggedInUser)) {
            result.setSuccess(false);
            result.addMessage("You're not permitted to import data");
            return result;
        }

        var importer = importerFactory.getImporter(version);
        importRequest.getDataItems()
                .forEach(importableJson -> {
                    switch (importRequest.getType()){
                        case RETROSPECTIVE:
                            var importableRetrospective = importAdapter.adaptSingleRetrospective(importableJson, version);
                            importer.importRetrospective(importableRetrospective, result, settings);
                            break;
                        case TENANT:
                            var importableTenant = importAdapter.adaptSingleTenant(importableJson, version);
                            importer.importTenant(importableTenant, result, settings);
                            break;
                    }
                });

        if (result.isSuccess() && settings.isRestoreData()) {
            switch (importRequest.getType()) {
                case RETROSPECTIVE:
                    var purgedUsers = settings.isDryRun()
                            ? addLoggedInUser(retrospectiveService.getReferencedUsernames(loggedInUser, true), loggedInUser)
                            : retrospectiveService.removeAllUnreferencedUsers(true, loggedInUser)
                            .stream().map(User::getUsername).collect(Collectors.toSet());

                    result.addMessage("All unreferenced users (" + purgedUsers.size() + ") have been purged");
                    break;
                case TENANT:
                    break;
            }
        }

        if (settings.isDryRun()) {
            result.addMessage("No changes have been made, import is in dryRun mode");
        }

        return result;
    }

    private static Set<String> addLoggedInUser(Set<String> usernames, LoggedInUser user) {
        var users = new HashSet<>(usernames);
        users.add(user.getUsername());
        return users;
    }

    public DataExport exportData(ExportRequest request) {
        var loggedInUser = userSessionManager.getLoggedInUser();
        if (!actionPermissionManager.canExport(loggedInUser)) {
            throw new NotPermittedException("You're not permitted to export data");
        }

        if (request.getIds() == null || request.getIds().isEmpty()){
            return exportAllData(request);
        }

        return exportIds(request);
    }

    private DataExport exportIds(ExportRequest request) {
        var settings = request.getSettings();
        var version = request.getVersion();

        switch (request.getType()) {
            case RETROSPECTIVE:
                return new DataExport(
                        request.getIds().stream().map(id -> exportRetrospective(id, version, settings)).collect(Collectors.toList()),
                        version);
            case TENANT:
                return new DataExport(
                        request.getIds().stream()
                                .map(id -> exportTenant(id, version, settings))
                                .filter(Objects::nonNull).collect(Collectors.toList()),
                        version);
        }

        throw new RuntimeException("Unsupported data type");
    }

    private String exportRetrospective(String id, String version, ExportSettings settings) {
        var retrospective = this.retrospectiveService.getRetrospective(id, userSessionManager.getLoggedInUser());
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return exportAdapter.adaptSingleRetrospective(retrospective, version, settings);
    }

    private String exportTenant(String id, String version, ExportSettings settings) {
        var tenant = this.tenantService.getTenant(id, userSessionManager.getLoggedInUser());
        if (tenant == null) {
            throw new NotFoundException("Retrospective not found");
        }

        if (!settings.isIncludeDeleted() && tenant.getState() == TenantState.DELETED){
            return null;
        }

        return exportAdapter.adaptSingleTenant(tenant, version, settings);
    }

    private DataExport exportAllData(ExportRequest request) {
        var settings = request.getSettings();
        var version = request.getVersion();

        switch (request.getType()) {
            case RETROSPECTIVE:
                var retrospectives = this.retrospectiveService.getAllRetrospectives(userSessionManager.getLoggedInUser());

                return new DataExport(
                        retrospectives.map(r -> exportAdapter.adaptSingleRetrospective(r, version, settings)).collect(Collectors.toList()),
                        version);
            case TENANT:
                var tenants = this.tenantService.getTenantsForLoggedInUser(userSessionManager.getLoggedInUser()).stream();

                if (!request.getSettings().isIncludeDeleted()) {
                    tenants = tenants.filter(t -> t.getState() != TenantState.DELETED);
                }

                return new DataExport(
                        tenants.map(t -> exportAdapter.adaptSingleTenant(t, version, settings)).collect(Collectors.toList()),
                        version);
        }

        throw new RuntimeException("Unsupported data type");
    }
}
