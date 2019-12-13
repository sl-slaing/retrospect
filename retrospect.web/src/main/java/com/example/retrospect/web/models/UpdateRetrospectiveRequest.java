package com.example.retrospect.web.models;

import java.util.List;

public class UpdateRetrospectiveRequest {
    private String id;
    private String previousRetrospectiveId;
    private String readableId;
    private List<String> members;
    private List<String> administrators;

    public String getPreviousRetrospectiveId() {
        return previousRetrospectiveId;
    }

    public void setPreviousRetrospectiveId(String previousRetrospectiveId) {
        this.previousRetrospectiveId = previousRetrospectiveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public String getReadableId() {
        return readableId;
    }

    public void setReadableId(String readableId) {
        this.readableId = readableId;
    }
}
