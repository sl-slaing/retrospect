package com.example.retrospect.core.models;

import java.util.Set;

public interface TenantDetails {
    String getId();
    String getName();
    TenantState getState();
    Set<String> getAdministrators();
    Set<String> getUsers();
}
