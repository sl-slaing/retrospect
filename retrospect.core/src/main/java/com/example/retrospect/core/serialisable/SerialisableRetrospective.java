package com.example.retrospect.core.serialisable;

import java.util.List;

public class SerialisableRetrospective {
    private String id;
    private String readableId;
    private String previousRetrospectiveId;
    private List<String> administrators;
    private List<String> members;
    private List<SerialisableAction> actions;
    private List<SerialisableObservation> wentWell;
    private List<SerialisableObservation> couldBeBetter;
    private SerialisableAudit audit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadableId() {
        return readableId;
    }

    public void setReadableId(String readableId) {
        this.readableId = readableId;
    }

    public String getPreviousRetrospectiveId() {
        return previousRetrospectiveId;
    }

    public void setPreviousRetrospectiveId(String id) {
        this.previousRetrospectiveId = id;
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<SerialisableAction> getActions() {
        return actions;
    }

    public void setActions(List<SerialisableAction> actions) {
        this.actions = actions;
    }

    public List<SerialisableObservation> getWentWell() {
        return wentWell;
    }

    public void setWentWell(List<SerialisableObservation> wentWell) {
        this.wentWell = wentWell;
    }

    public List<SerialisableObservation> getCouldBeBetter() {
        return couldBeBetter;
    }

    public void setCouldBeBetter(List<SerialisableObservation> doBetter) {
        this.couldBeBetter = doBetter;
    }

    public SerialisableAudit getAudit() {
        return audit;
    }

    public void setAudit(SerialisableAudit audit) {
        this.audit = audit;
    }
}
