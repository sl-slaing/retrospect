package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.viewmodels.RetrospectiveOverview;
import com.example.retrospect.web.viewmodels.RetrospectiveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class DataController {
    private final RetrospectiveService service;
    private final UserSessionManager userSessionManager;

    @Autowired
    public DataController(
            RetrospectiveService service,
            UserSessionManager userSessionManager) {
        this.service = service;
        this.userSessionManager = userSessionManager;
    }

    @GetMapping("/data")
    public List<RetrospectiveOverview> index(){
        return this.service.getAllRetrospectives(userSessionManager.getLoggedInUser())
                .map(RetrospectiveOverview::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/data/{id}")
    public RetrospectiveViewModel index(@PathVariable String id){
        if (id == null){
            return null;
        }

        var loggedInUser = userSessionManager.getLoggedInUser();
        var retrospective = this.service.getRetrospective(id, loggedInUser);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return new RetrospectiveViewModel(retrospective, loggedInUser);
    }
}
