package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginProvidersViewModel {
    private final List<LoginProviderViewModel> loginProviders;
    private final LoggedInUser loggedInUser;
    private final List<TenantViewModel> tenantsForLoggedInUser;
    private final boolean showSystemAdministration;

    public LoginProvidersViewModel(
            List<LoginProviderViewModel> loginProviders,
            LoggedInUser loggedInUser,
            List<TenantViewModel> tenantsForLoggedInUser,
            boolean showSystemAdministration) {
        this.loginProviders = loginProviders;
        this.loggedInUser = loggedInUser;
        this.tenantsForLoggedInUser = tenantsForLoggedInUser;
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

    public Map<String, TenantViewModel> getTenantsForLoggedInUser() {
        return tenantsForLoggedInUser.stream().collect(Collectors.toMap(TenantViewModel::getId, t -> t));
    }
}
