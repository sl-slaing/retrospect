package com.example.retrospect.core.models;

public class Tenant {
    private String id;
    private String name;
    private ImmutableList<User> users;
    private ImmutableList<User> administrators;
    private TenantState state = TenantState.ACTIVE;
    private Audit audit;

    public Tenant(String id, String name, ImmutableList<User> users, ImmutableList<User> administrators, TenantState state, Audit audit) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.administrators = administrators;
        this.state = state;
        this.audit = audit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ImmutableList<User> getUsers() {
        return users;
    }

    public void setUsers(ImmutableList<User> users, LoggedInUser user, String change) {
        this.users = users;
        this.audit.update(user, change);
    }

    public TenantState getState() {
        return state;
    }

    public void setState(TenantState state, LoggedInUser user, String change) {
        this.state = state;
        this.audit.update(user, change);
    }

    public ImmutableList<User> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(ImmutableList<User> administrators, LoggedInUser user, String change) {
        this.administrators = administrators;
        this.audit.update(user, change);
    }

    public Audit getAudit() {
        return audit;
    }

    public void setName(String name, LoggedInUser loggedInUser, String change) {
        this.name = name;
        this.audit.update(loggedInUser, change);
    }
}

