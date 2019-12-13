package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;

import java.util.List;

public class LoginProvidersViewModel {
    private final List<LoginProviderViewModel> loginProviders;
    private final LoggedInUser loggedInUser;

    public LoginProvidersViewModel(List<LoginProviderViewModel> loginProviders, LoggedInUser loggedInUser) {
        this.loginProviders = loginProviders;
        this.loggedInUser = loggedInUser;
    }

    public List<LoginProviderViewModel> getLoginProviders() {
        return loginProviders;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }
}
