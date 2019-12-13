package com.example.retrospect.web.viewmodels;

import com.example.retrospect.web.managers.ClientResources;

public class LoginProviderViewModel {
    private final ClientResources clientResources;

    public LoginProviderViewModel(ClientResources clientResources) {
        this.clientResources = clientResources;
    }

    public String getDisplayName(){
        return clientResources.getDisplayName();
    }

    public String getLoginPath(){
        return clientResources.getLoginPath();
    }

    public String getClassName(){
        return clientResources.getName().getProvider();
    }
}
