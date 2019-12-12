package com.example.retrospect.web.controllers;

import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.viewmodels.RetrospectiveOverview;
import com.example.retrospect.web.viewmodels.RetrospectiveOverviewsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class RetrospectiveController {
    private final UserSessionManager userSessionManager;
    private final RetrospectiveService service;

    @Autowired
    public RetrospectiveController(UserSessionManager userSessionManager, RetrospectiveService service) {
        this.userSessionManager = userSessionManager;
        this.service = service;
    }

    @GetMapping("/")
    public ModelAndView index(){
        var user = userSessionManager.getLoggedInUser();

        if (user == null){
            return new ModelAndView("redirect:/login", "request", null);
        }

        var retrospectives = service.getAllRetrospectives(user)
                .map(retro -> new RetrospectiveOverview(retro, user))
                .collect(Collectors.toList());

        var viewModel = new RetrospectiveOverviewsViewModel(retrospectives, user);

        return new ModelAndView("index", "viewModel", viewModel);
    }
}
