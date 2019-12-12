package com.example.retrospect.core.models;

public class User implements Identifiable {
    public static final String TYPE_NAME = "USER";

    protected String userName;
    protected String displayName;
    protected String emailAddress;
    private final String avatarUrl;

    public User(String userName, String displayName, String emailAddress, String avatarUrl) {
        this.userName = userName;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}

