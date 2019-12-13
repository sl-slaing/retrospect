package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.viewmodels.RetrospectiveOverview;
import com.example.retrospect.web.viewmodels.RetrospectiveOverviewsViewModel;
import com.example.retrospect.web.viewmodels.RetrospectiveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
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
    public String home(){
        var user = userSessionManager.getLoggedInUser();
        if (user != null){
            return "redirect:/list";
        }

        return "home";
    }
}
