package com.example.retrospect.web.controllers;

import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class SessionController {
    private final UserSessionManager userSessionManager;

    @Autowired
    public SessionController(
            UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    @GetMapping("/")
    public ModelAndView session(@RequestParam(required = false) String returnUrl){

        var user = userSessionManager.getLoggedInUser();
        if (user != null){
            new ModelAndView("redirect:/retro", "request", null);
        }

        var request = new LoginRequest();
        request.setReturnUrl(returnUrl);

        return new ModelAndView("home", "request", request);
    }
}
