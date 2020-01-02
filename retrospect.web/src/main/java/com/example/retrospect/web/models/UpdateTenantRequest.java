package com.example.retrospect.web.models;

import com.example.retrospect.core.models.TenantDetails;
import com.example.retrospect.core.models.TenantState;

import java.util.Set;

public class UpdateTenantRequest implements TenantDetails {
    private String id;
    private String name;
    private TenantState state;
    private Set<String> administrators;
    private Set<String> users;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public TenantState getState() {
        return state;
    }

    public void setState(TenantState state) {
        this.state = state;
    }

    public Set<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<String> administrators) {
        this.administrators = administrators;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
