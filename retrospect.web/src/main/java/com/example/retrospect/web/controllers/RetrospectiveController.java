package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.services.RetrospectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@RestController
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class RetrospectiveController {
    private final RetrospectiveService service;
    private final UserSessionManager userSessionManager;

    @Autowired
    public RetrospectiveController(
            RetrospectiveService service,
            UserSessionManager userSessionManager) {
        this.service = service;
        this.userSessionManager = userSessionManager;
    }

    @GetMapping("/data/{id}")
    public Retrospective index(@PathVariable String id){
        if (id == null){
            return null;
        }

        var retrospective = this.service.getRetrospective(id, userSessionManager.getLoggedInUser());
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return retrospective; //JSON-ify
    }
}
