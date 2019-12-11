package com.example.retrospect.web.controllers;

import com.example.retrospect.web.managers.UserSessionManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class RetrospectiveController {
    private final UserSessionManager userSessionManager;

    public RetrospectiveController(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    @GetMapping("/")
    public ModelAndView index(){
        var user = userSessionManager.getLoggedInUser();

        if (user == null){
            return new ModelAndView("redirect:/login", "request", null);
        }

        return new ModelAndView("redirect:/data", "request", null);
    }
}
