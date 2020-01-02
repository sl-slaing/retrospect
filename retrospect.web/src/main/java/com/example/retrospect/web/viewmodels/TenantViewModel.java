package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.*;

import java.util.Map;
import java.util.stream.Collectors;

public class TenantViewModel {
    private final Tenant tenant;

    public TenantViewModel(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getId() {
        return tenant.getId();
    }

    public String getName() {
        return tenant.getName();
    }

    public Map<String, UserViewModel> getUsers() {
        return tenant.getUsers().stream().collect(Collectors.toMap(User::getUsername, UserViewModel::new));
    }

    public TenantState getState() {
        return tenant.getState();
    }

    public Map<String, UserViewModel> getAdministrators() {
        return tenant.getAdministrators().stream().collect(Collectors.toMap(User::getUsername, UserViewModel::new));
    }

    public Audit getAudit() {
        return tenant.getAudit();
    }
}
