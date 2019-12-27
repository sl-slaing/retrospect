package com.example.retrospect.core.serialisable;

import java.util.List;
import java.util.Map;

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

    public static SerialisableRetrospective deserialiseFromMap(Map<String, Object> map) {
        var retro = new SerialisableRetrospective();
        retro.setId((String)map.get("id"));
        retro.setReadableId((String)map.get("readableId"));
        retro.setPreviousRetrospectiveId((String)map.get("previousRetrospectiveId"));
        retro.setAdministrators((List<String>)map.get("administrators"));
        retro.setMembers((List<String>)map.get("members"));
        retro.setActions(SerialisableAction.deserialiseFromListOfMaps((List<Map>)map.get("actions")));
        retro.setWentWell(SerialisableObservation.deserialiseFromListOfMaps((List<Map>)map.get("wentWell")));
        retro.setCouldBeBetter(SerialisableObservation.deserialiseFromListOfMaps((List<Map>)map.get("couldBeBetter")));
        retro.setAudit(SerialisableAudit.deserialiseFromMap((Map<String, Object>)map.get("audit")));

        return retro;
    }
}
