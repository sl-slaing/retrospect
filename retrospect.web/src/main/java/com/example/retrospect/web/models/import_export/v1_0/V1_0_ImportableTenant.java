package com.example.retrospect.web.models.import_export.v1_0;

import com.example.retrospect.web.services.import_export.ImportableDataItem;

import java.util.List;

public class V1_0_ImportableTenant implements ImportableDataItem {
    private String id;
    private String name;
    private String state;
    private List<String> administrators;
    private List<String> users;

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

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
