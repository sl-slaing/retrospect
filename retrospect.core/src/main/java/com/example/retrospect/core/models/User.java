package com.example.retrospect.core.models;

public class User implements Identifiable {
    public static final String TYPE_NAME = "USER";

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

    @Override
    public String getId() {
        return userName;
    }

    @Override
    public String getType() {
        return TYPE_NAME;
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
}

