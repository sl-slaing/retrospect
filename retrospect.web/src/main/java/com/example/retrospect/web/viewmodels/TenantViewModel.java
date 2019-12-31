package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.*;

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

    public ImmutableList<User> getUsers() {
        return tenant.getUsers();
    }

    public TenantState getState() {
        return tenant.getState();
    }

    public ImmutableList<User> getAdministrators() {
        return tenant.getAdministrators();
    }

    public Audit getAudit() {
        return tenant.getAudit();
    }
}
