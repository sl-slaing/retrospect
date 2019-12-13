package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class UserRepository {
    private static final Map<String, User> database = new HashMap<>();

    public User getUser(String username){
        return database.getOrDefault(username, null);
    }

    public void addOrUpdateUserDetails(User user) {
        database.put(user.getUsername(), user);
    }

    public Stream<User> getAllUsers(){
        return database.values().stream();
    }
}
