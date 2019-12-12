package com.example.retrospect.web.controllers;

import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.core.repositories.UserRepository;
import com.example.retrospect.web.models.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class SessionController {
    private final UserSessionManager userSessionManager;
    private final UserRepository userRepository;

    @Autowired
    public SessionController(
            UserSessionManager userSessionManager,
            UserRepository userRepository) {
        this.userSessionManager = userSessionManager;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ModelAndView session(@RequestParam(required = false) String returnUrl){

        var request = new LoginRequest();
        request.setReturnUrl(returnUrl);

        return new ModelAndView("login", "request", request);
    }

    @PostMapping("/session")
    public ModelAndView session(@ModelAttribute LoginRequest request){
        var user = userRepository.getUser(request.getUsername());
        if (user == null){
            request.setError("User not found");
            return new ModelAndView("login", "request", request);
        }

        var returnUrl = request.getReturnUrl();
        if (returnUrl == null || returnUrl.equals("")){
            returnUrl = "/retro";
        }

        userSessionManager.login(user, returnUrl);

        return null;
    }

    @GetMapping("/logout")
    public void logout(){
        userSessionManager.logout();
    }
}
