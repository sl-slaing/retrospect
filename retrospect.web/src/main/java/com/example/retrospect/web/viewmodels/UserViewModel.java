package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.User;

public class UserViewModel {
    private final User user;

    public UserViewModel(User user) {
        this.user = user;
    }

    public String getUsername(){
        return user.getUsername();
    }

    public String getDisplayName(){
        return user.getDisplayName();
    }

    public String getProvider(){
        return user.getProvider();
    }

    public String getAvatarUrl(){
        return user.getAvatarUrl();
    }
}
