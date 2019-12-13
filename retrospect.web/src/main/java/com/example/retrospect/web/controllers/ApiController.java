package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.ClientResourcesManager;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.viewmodels.LoginProviderViewModel;
import com.example.retrospect.web.viewmodels.RetrospectiveOverview;
import com.example.retrospect.web.viewmodels.RetrospectiveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    private final RetrospectiveService service;
    private final UserSessionManager userSessionManager;
    private final ClientResourcesManager clientResourcesManager;

    @Autowired
    public ApiController(
            RetrospectiveService service,
            UserSessionManager userSessionManager,
            ClientResourcesManager clientResourcesManager) {
        this.service = service;
        this.userSessionManager = userSessionManager;
        this.clientResourcesManager = clientResourcesManager;
    }

    @GetMapping("/data")
    public List<RetrospectiveOverview> index(){
        var loggedInUser = userSessionManager.getLoggedInUser();

        return this.service.getAllRetrospectives(loggedInUser)
                .map(retro -> new RetrospectiveOverview(retro, loggedInUser))
                .collect(Collectors.toList());
    }

    @GetMapping("/data/{id}")
    public RetrospectiveViewModel index(@PathVariable String id){
        var loggedInUser = userSessionManager.getLoggedInUser();

        var retrospective = this.service.getRetrospective(id, loggedInUser);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return new RetrospectiveViewModel(retrospective, loggedInUser);
    }

    @GetMapping("/data/loginProviders")
    public List<LoginProviderViewModel> loginProviders(){
        return clientResourcesManager.getAllClientResources()
                .map(LoginProviderViewModel::new)
                .collect(Collectors.toList());
    }
}
