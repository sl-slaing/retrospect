package com.example.retrospect.core.models;

public class SystemUser extends User {
    public static final SystemUser INSTANCE = new SystemUser();

    public SystemUser() {
        super("SYSTEM_USER", "SYSTEM_USER", "SYSTEM_USER");
    }
}
