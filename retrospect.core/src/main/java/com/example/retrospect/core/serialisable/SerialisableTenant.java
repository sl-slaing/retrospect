package com.example.retrospect.core.serialisable;

import java.util.List;

public class SerialisableTenant {
    private String id;
    private String name;
    private String state;
    private List<String> users;
    private List<String> administrators;
    private SerialisableAudit audit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public SerialisableAudit getAudit() {
        return audit;
    }

    public void setAudit(SerialisableAudit audit) {
        this.audit = audit;
    }
}
