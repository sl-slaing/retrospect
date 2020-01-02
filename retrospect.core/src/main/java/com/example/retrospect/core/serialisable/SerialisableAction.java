package com.example.retrospect.core.serialisable;

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
}
