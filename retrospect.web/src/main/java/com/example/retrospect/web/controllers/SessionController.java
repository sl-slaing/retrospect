package com.example.retrospect.web.controllers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@RestController
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

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/login/{username}")
    public void login(@PathVariable String username){
        User user = userRepository.getUser(username);
        if (user == null){
            throw new NotFoundException("User not found");
        }

        userSessionManager.login(user, "/data");
    }
}
