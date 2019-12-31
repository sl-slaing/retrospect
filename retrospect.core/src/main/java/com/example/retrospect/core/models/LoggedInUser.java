package com.example.retrospect.core.models;

public class LoggedInUser extends User {
    private String tenantId;

    public LoggedInUser(User user, String tenantId) {
        super(user.getUsername(), user.getDisplayName(), user.getAvatarUrl(), user.getProvider());
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
