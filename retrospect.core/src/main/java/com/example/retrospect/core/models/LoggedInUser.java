package com.example.retrospect.core.models;

public class LoggedInUser extends User {
    public LoggedInUser(User user) {
        super(user.getId(), user.getDisplayName(), user.getAvatarUrl(), user.getProvider());
    }
}
