package com.example.retrospect.web.models;

import com.example.retrospect.core.models.RetrospectiveAdministrationDetails;

import java.util.List;

public class AdministerRetrospectiveRequest implements RetrospectiveAdministrationDetails {
    private String id;
    private String readableId;
    private String previousRetrospectiveId;
    private List<String> members;
    private List<String> administrators;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getReadableId() {
        return readableId;
    }

    public void setReadableId(String readableId) {
        this.readableId = readableId;
    }

    @Override
    public String getPreviousRetrospectiveId() {
        return previousRetrospectiveId;
    }

    public void setPreviousRetrospectiveId(String previousRetrospectiveId) {
        this.previousRetrospectiveId = previousRetrospectiveId;
    }

    @Override
    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }
}
