package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.managers.CookieUserSessionManager;
import com.example.retrospect.core.managers.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.retrospect.core.services.RetrospectiveService;
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

    @GetMapping("/data")
    public Object index(String id){
        var retrospective = this.service.getRetrospective(id, userSessionManager.getLoggedInUser());
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        return retrospective; //JSON-ify
    }
}
