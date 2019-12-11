package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserRepository {
    public User getUser(String username){
        var displayName = username.substring(0, 1).toUpperCase() + " " + username.substring(1, 2).toUpperCase() + username.substring(2);

        return new User(username, displayName, username + "@scottlogic.com");
    }
}
