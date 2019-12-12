package com.example.retrospect.web.managers;

import com.example.retrospect.core.models.LoggedInUser;

public interface UserSessionManager {
    LoggedInUser getLoggedInUser();
}
