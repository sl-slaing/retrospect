package com.example.retrospect.web.models;

public class LoginRequest {
    private String username;
    private String returnUrl;
    private String message;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void setError(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }
}
