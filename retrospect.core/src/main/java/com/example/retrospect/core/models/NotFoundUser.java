package com.example.retrospect.core.models;

public class NotFoundUser extends User {
    public NotFoundUser(String userName) {
        super(userName, "NOT FOUND", "NOT FOUND", null);
    }
}
