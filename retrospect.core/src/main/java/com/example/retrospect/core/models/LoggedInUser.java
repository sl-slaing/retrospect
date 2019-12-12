package com.example.retrospect.core.models;

import java.security.Principal;

public class LoggedInUser extends User {
    public LoggedInUser(User user) {
        super(user.userName, user.displayName, user.emailAddress, null);
    }

    public LoggedInUser(Principal loggedInUser) {
        super(loggedInUser.getName(), loggedInUser.getName(), loggedInUser.getName(), null);
    }
}
