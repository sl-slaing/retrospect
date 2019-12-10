package com.example.retrospect.web.controllers;

import com.example.retrospect.core.managers.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.retrospect.core.services.RetrospectiveService;

@RestController
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
        if (retrospective == null){
            throw new RuntimeException("Not found");
        }

        return retrospective; //JSON-ify
    }
}
