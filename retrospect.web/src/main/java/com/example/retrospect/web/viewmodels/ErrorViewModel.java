package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;

public class ErrorViewModel {
    private final LoggedInUser loggedInUser;

    public ErrorViewModel(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public UserViewModel getUser() {
        return new UserViewModel(loggedInUser);
    }
}
