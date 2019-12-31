package com.example.retrospect.web.services;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.managers.ActionPermissionManager;
import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.import_export.ExportRequest;
import com.example.retrospect.web.models.import_export.ExportSettings;
import com.example.retrospect.web.models.import_export.ImportRequest;
import com.example.retrospect.web.models.import_export.ImportableData;
import com.example.retrospect.web.services.import_export.ExportAdapter;
import com.example.retrospect.web.services.import_export.ImportAdapter;
import com.example.retrospect.web.services.import_export.ImportResult;
import com.example.retrospect.web.services.import_export.RetrospectiveImporterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportExportService {
    private final RetrospectiveService service;
    private final ExportAdapter exportAdapter;
    private final ImportAdapter importAdapter;
    private final UserSessionManager userSessionManager;
    private final RetrospectiveImporterFactory importerFactory;
    private final ActionPermissionManager actionPermissionManager;

    @Autowired
    public ImportExportService(
            RetrospectiveService service,
            ExportAdapter exportAdapter,
            ImportAdapter importAdapter,
            UserSessionManager userSessionManager,
            RetrospectiveImporterFactory importerFactory,
            ActionPermissionManager actionPermissionManager) {
        this.service = service;
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

        if (importRequest.getRetrospectives() == null){
            throw new RuntimeException("Invalid import request, no retrospectives supplied");
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

            var removedRetrospectives = settings.isDryRun()
                    ? service.getAllRetrospectives(loggedInUser).collect(Collectors.toSet())
                    : service.removeAllRetrospectives(loggedInUser);
            result.addMessage("All existing retrospectives (" + removedRetrospectives.size() + ") have been purged");
        } else if (!actionPermissionManager.canImport(loggedInUser)) {
            result.setSuccess(false);
            result.addMessage("You're not permitted to import data");
            return result;
        }

        var importer = importerFactory.getImporter(version);
        importRequest.getRetrospectives()
                .forEach(importableJson -> {
                    var importable = importAdapter.adaptSingleRetrospective(importableJson, version);
                    importer.importRetrospective(importable, result, settings);
                });

        if (result.isSuccess() && settings.isRestoreData()) {
            var purgedUsers = settings.isDryRun()
                    ? addLoggedInUser(service.getReferencedUsernames(loggedInUser, true), loggedInUser)
                    : service.removeAllUnreferencedUsers(true, loggedInUser)
                            .stream().map(User::getUsername).collect(Collectors.toSet());

            result.addMessage("All unreferenced users (" + purgedUsers.size() + ") have been purged");
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

    public ImportableData exportData(ExportRequest request) {
        var loggedInUser = userSessionManager.getLoggedInUser();
        if (!actionPermissionManager.canExport(loggedInUser)) {
            throw new NotPermittedException("You're not permitted to export data");
        }

        if (request.getIds() == null || request.getIds().isEmpty()){
            return exportAllData(request);
        }

        return exportRetrospectives(request);
    }

    private ImportableData exportRetrospectives(ExportRequest request) {
        var settings = request.getSettings();
        var version = request.getVersion();

        return new ImportableData(
                request.getIds().stream().map(id -> exportRetrospective(id, version, settings)).collect(Collectors.toList()),
                version);
    }

    private String exportRetrospective(String id, String version, ExportSettings settings) {
        var retrospective = this.service.getRetrospective(id, userSessionManager.getLoggedInUser());
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return exportAdapter.adaptSingleRetrospective(retrospective, version, settings);
    }

    private ImportableData exportAllData(ExportRequest request) {
        var retrospectives = this.service.getAllRetrospectives(userSessionManager.getLoggedInUser());
        var settings = request.getSettings();
        var version = request.getVersion();

        return new ImportableData(
                retrospectives.map(r -> exportAdapter.adaptSingleRetrospective(r, version, settings)).collect(Collectors.toList()),
                version);
    }
}
