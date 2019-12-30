package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;

import java.util.List;

public class LoginProvidersViewModel {
    private final List<LoginProviderViewModel> loginProviders;
    private final LoggedInUser loggedInUser;
    private final boolean showSystemAdministration;

    public LoginProvidersViewModel(List<LoginProviderViewModel> loginProviders, LoggedInUser loggedInUser, boolean showSystemAdministration) {
        this.loginProviders = loginProviders;
        this.loggedInUser = loggedInUser;
        this.showSystemAdministration = showSystemAdministration;
    }

    public List<LoginProviderViewModel> getLoginProviders() {
        return loginProviders;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public boolean getShowSystemAdministration() {
        return showSystemAdministration;
    }
}
