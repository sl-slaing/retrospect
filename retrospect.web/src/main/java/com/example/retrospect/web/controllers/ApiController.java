package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.managers.ActionPermissionManager;
import com.example.retrospect.core.models.TenantState;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.UserRepository;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.core.services.TenantService;
import com.example.retrospect.web.managers.ClientResourcesManager;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.*;
import com.example.retrospect.web.models.import_export.ExportRequest;
import com.example.retrospect.web.models.import_export.ImportRequest;
import com.example.retrospect.web.models.import_export.ImportableData;
import com.example.retrospect.web.services.ImportExportService;
import com.example.retrospect.web.services.import_export.ImportResult;
import com.example.retrospect.web.viewmodels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    private final RetrospectiveService retrospectiveService;
    private final TenantService tenantService;
    private final UserSessionManager userSessionManager;
    private final ClientResourcesManager clientResourcesManager;
    private final UserRepository userRepository;
    private final ImportExportService importExportService;
    private final ActionPermissionManager actionPermissionManager;

    @Autowired
    public ApiController(
            RetrospectiveService retrospectiveService,
            TenantService tenantService,
            UserSessionManager userSessionManager,
            ClientResourcesManager clientResourcesManager,
            UserRepository userRepository,
            ImportExportService importExportService,
            ActionPermissionManager actionPermissionManager) {
        this.retrospectiveService = retrospectiveService;
        this.tenantService = tenantService;
        this.userSessionManager = userSessionManager;
        this.clientResourcesManager = clientResourcesManager;
        this.userRepository = userRepository;
        this.importExportService = importExportService;
        this.actionPermissionManager = actionPermissionManager;
    }

    @GetMapping("/retrospective")
    public Map<String, RetrospectiveOverview> index(){
        var loggedInUser = userSessionManager.getLoggedInUser();

        return this.retrospectiveService.getAllRetrospectives(loggedInUser)
                .map(retro -> new RetrospectiveOverview(retro, loggedInUser))
                .collect(Collectors.toMap(RetrospectiveOverview::getId, r -> r));
    }

    @GetMapping("/retrospective/{id}")
    public RetrospectiveViewModel index(@PathVariable String id){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var retrospective = this.retrospectiveService.getRetrospective(id, loggedInUser);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        var previousRetrospective = retrospective.getPreviousRetrospectiveId() != null
                ? this.retrospectiveService.getRetrospective(retrospective.getPreviousRetrospectiveId(), loggedInUser)
                : null;

        return new RetrospectiveViewModel(retrospective, previousRetrospective, loggedInUser);
    }

    @PostMapping("/observation/vote")
    public ObservationViewModel vote(@RequestBody(required = false) VoteRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var observation = this.retrospectiveService.applyVote(
                request.getRetrospectiveId(),
                request.getObservationId(),
                request.getObservationType(),
                loggedInUser);

        return new ObservationViewModel(observation, loggedInUser, request.getObservationType());
    }

    @PostMapping("/observation/update")
    public ObservationViewModel updateObservation(@RequestBody(required = false) UpdateObservationRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var observation = this.retrospectiveService.updateObservation(
                request.getRetrospectiveId(),
                request.getObservationId(),
                request.getObservationType(),
                loggedInUser,
                ob -> ob.setTitle(request.getTitle(), loggedInUser));

        return new ObservationViewModel(observation, loggedInUser, request.getObservationType());
    }

    @PostMapping("/observation/create")
    public ObservationViewModel createObservation(@RequestBody(required = false) UpdateObservationRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var observation = this.retrospectiveService.addObservation(
                request.getRetrospectiveId(),
                request.getObservationType(),
                request.getTitle(),
                loggedInUser);

        return new ObservationViewModel(observation, loggedInUser, request.getObservationType());
    }

    @DeleteMapping("/observation")
    public void deleteObservation(@RequestBody(required = false) UpdateObservationRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        this.retrospectiveService.removeObservation(
                request.getRetrospectiveId(),
                request.getObservationId(),
                request.getObservationType(),
                loggedInUser);
    }

    @PostMapping("/retrospective/create")
    public RetrospectiveOverview createRetrospective(@RequestBody(required = false) UpdateRetrospectiveRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var retrospective = this.retrospectiveService.addRetrospective(
                request.getPreviousRetrospectiveId(),
                request.getReadableId(),
                request.getMembers(),
                request.getAdministrators(),
                loggedInUser);

        return new RetrospectiveOverview(retrospective, loggedInUser);
    }

    @PostMapping("/retrospective/administration")
    public RetrospectiveOverview administerRetrospective(@RequestBody(required = false) AdministerRetrospectiveRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var retrospective = this.retrospectiveService.applyAdministrationDetails(request, loggedInUser);

        return new RetrospectiveOverview(retrospective, loggedInUser);
    }

    @DeleteMapping("/retrospective")
    public void deleteRetrospective(@RequestBody(required = false) UpdateRetrospectiveRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        this.retrospectiveService.removeRetrospective(
                request.getId(),
                loggedInUser);
    }

    @GetMapping("/users")
    public Map<String, UserViewModel> users(){
        var loggedInUser = userSessionManager.getLoggedInUser();

        return userRepository.getAllUsers(loggedInUser)
                .collect(Collectors.toMap(User::getUsername, UserViewModel::new));
    }

    @GetMapping("/loginProviders")
    public LoginProvidersViewModel loginProviders(){
        var loggedInUser = userSessionManager.getLoggedInUser();

        return new LoginProvidersViewModel(
                clientResourcesManager.getAllClientResources()
                    .map(LoginProviderViewModel::new)
                    .collect(Collectors.toList()),
                loggedInUser,
                tenantService.getTenantsForLoggedInUser(loggedInUser)
                    .stream().map(TenantViewModel::new).collect(Collectors.toList()),
            loggedInUser != null &&
                    (actionPermissionManager.canRestore(loggedInUser)
                    || actionPermissionManager.canImport(loggedInUser)
                    || actionPermissionManager.canExport(loggedInUser)));
    }

    @PostMapping("/action/update")
    public ActionViewModel updateAction(@RequestBody(required = false) UpdateActionRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var action = this.retrospectiveService.updateAction(request, loggedInUser);

        return new ActionViewModel(action);
    }

    @PostMapping("/action/create")
    public ActionViewModel createAction(@RequestBody(required = false) UpdateActionRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var action = this.retrospectiveService.addAction(request, loggedInUser);

        return new ActionViewModel(action);
    }

    @DeleteMapping("/action")
    public void deleteAction(@RequestBody(required = false) UpdateActionRequest request){
        var loggedInUser = userSessionManager.getLoggedInUser();

        this.retrospectiveService.removeAction(
                request.getRetrospectiveId(),
                request.getActionId(),
                loggedInUser);
    }

    @PostMapping("/export")
    public ImportableData exportData(@RequestBody ExportRequest request) {
        return this.importExportService.exportData(request);
    }

    @PostMapping("/import")
    public ImportResult importData(@RequestBody ImportRequest request) {
        return this.importExportService.importData(request);
    }

    @PostMapping("/createTenant")
    public TenantViewModel createTenant(@RequestBody CreateTenantRequest request) {
        var loggedInUser = userSessionManager.getLoggedInUser();

        return new TenantViewModel(
                this.tenantService.addTenant(
                        request.getName(),
                        TenantState.ACTIVE,
                        loggedInUser));
    }
}
