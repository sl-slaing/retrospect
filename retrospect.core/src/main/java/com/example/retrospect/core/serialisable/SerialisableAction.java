package com.example.retrospect.core.serialisable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SerialisableAction {
    private String id;
    private String title;
    private String ticketAddress;
    private String assignedTo;
    private SerialisableAudit audit;
    private boolean deleted;
    private String fromActionId;
    private boolean complete;
    private String fromObservationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTicketAddress() {
        return ticketAddress;
    }

    public void setTicketAddress(String ticketAddress) {
        this.ticketAddress = ticketAddress;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public SerialisableAudit getAudit() {
        return audit;
    }

    public void setAudit(SerialisableAudit audit) {
        this.audit = audit;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getFromActionId() {
        return fromActionId;
    }

    public void setFromActionId(String fromActionId) {
        this.fromActionId = fromActionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setFromObservationId(String fromObservationId) {
        this.fromObservationId = fromObservationId;
    }

    public String getFromObservationId() {
        return fromObservationId;
    }

    public static List<SerialisableAction> deserialiseFromListOfMaps(List<Map> actions) {
        return actions.stream()
                .map(SerialisableAction::deserialiseFromMap)
                .collect(Collectors.toList());
    }

    private static SerialisableAction deserialiseFromMap(Map actionData) {
        var action = new SerialisableAction();
        action.setId((String)actionData.get("id"));
        action.setTitle((String)actionData.get("title"));
        action.setTicketAddress((String)actionData.get("ticketAddress"));
        action.setAssignedTo((String)actionData.get("assignedTo"));
        action.setAudit(SerialisableAudit.deserialiseFromMap((Map<String, Object>)actionData.get("audit")));
        action.setDeleted((boolean)actionData.get("deleted"));
        action.setFromActionId((String)actionData.get("fromActionId"));
        action.setFromObservationId((String)actionData.get("fromObservationId"));
        action.setComplete((boolean)actionData.get("complete"));

        return action;
    }
}
