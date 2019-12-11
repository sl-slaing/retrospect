package com.example.retrospect.core.managers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;

public interface UserSessionManager {
    void login(User user, String redirectUrl);

    void logout();

    LoggedInUser getLoggedInUser();
}
