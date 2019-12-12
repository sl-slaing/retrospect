package com.example.retrospect.core.models;

public class LoggedInUser extends User {
    public LoggedInUser(User user) {
        super(user.userName, user.displayName, user.emailAddress);
    }
}
