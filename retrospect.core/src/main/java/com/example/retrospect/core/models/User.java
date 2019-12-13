package com.example.retrospect.core.models;

public class User {
    protected String userName;
    protected String displayName;
    private final String provider;
    private final String avatarUrl;

    public User(String userName, String displayName, String avatarUrl, String provider) {
        this.userName = userName;
        this.displayName = displayName;
        this.provider = provider;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProvider() {
        return provider;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof User)){
            return false;
        }

        var user = (User)other;
        return user.getUsername().equals(getUsername());
    }
}

