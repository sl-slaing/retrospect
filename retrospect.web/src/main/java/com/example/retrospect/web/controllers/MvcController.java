package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.LoginRequest;
import com.example.retrospect.web.viewmodels.RetrospectiveOverview;
import com.example.retrospect.web.viewmodels.RetrospectiveOverviewsViewModel;
import com.example.retrospect.web.viewmodels.RetrospectiveViewModel;
import com.example.retrospect.web.viewmodels.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class MvcController {
    private final UserSessionManager userSessionManager;
    private final RetrospectiveService service;

    @Autowired
    public MvcController(UserSessionManager userSessionManager, RetrospectiveService service) {
        this.userSessionManager = userSessionManager;
        this.service = service;
    }

    @GetMapping("/list")
    public ModelAndView list(){
        var user = userSessionManager.getLoggedInUser();

        var retrospectives = service.getAllRetrospectives(user)
                .map(retro -> new RetrospectiveOverview(retro, user))
                .collect(Collectors.toList());

        var viewModel = new RetrospectiveOverviewsViewModel(retrospectives, user);

        return new ModelAndView("list", "viewModel", viewModel);
    }

    @GetMapping("/retrospective/{id}")
    public ModelAndView retrospective(@PathVariable String id){
        var user = userSessionManager.getLoggedInUser();

        var retrospective = service.getRetrospective(id, user);
        if (retrospective == null){
            throw new NotFoundException("Retrospective not found");
        }

        var viewModel = new RetrospectiveViewModel(retrospective, user);
        return new ModelAndView("retrospective", "viewModel", viewModel);
    }

    @GetMapping("/")
    public ModelAndView home(@RequestParam(required = false) String returnUrl){
        var user = userSessionManager.getLoggedInUser();
        if (user != null){
            return new ModelAndView("redirect:/list", "request", null);
        }

        var request = new LoginRequest();
        request.setReturnUrl(returnUrl);

        return new ModelAndView("home", "request", request);
    }
}
