package com.example.retrospect.web.models.import_export.v1_0;

import com.example.retrospect.web.services.import_export.ImportableRetrospective;

import java.util.List;

public class V1_0_ImportableRetrospective implements ImportableRetrospective {
    private String id;
    private List<String> administrators;
    private List<String> members;
    private String readableId;
    private List<V1_0_ImportableAction> actions;
    private List<V1_0_ImportableObservation> observations;
    private String previousRetrospectiveId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setReadableId(String readableId) {
        this.readableId = readableId;
    }

    public String getReadableId() {
        return readableId;
    }

    public void setActions(List<V1_0_ImportableAction> actions) {
        this.actions = actions;
    }

    public List<V1_0_ImportableAction> getActions() {
        return actions;
    }

    public void setObservations(List<V1_0_ImportableObservation> observations) {
        this.observations = observations;
    }

    public List<V1_0_ImportableObservation> getObservations() {
        return observations;
    }

    public String getPreviousRetrospectiveId() {
        return previousRetrospectiveId;
    }

    public void setPreviousRetrospectiveId(String previousRetrospectiveId) {
        this.previousRetrospectiveId = previousRetrospectiveId;
    }
}
