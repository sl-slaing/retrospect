package com.example.retrospect.web.models.import_export.v1_0;

public class V1_0_ImportableAction {
    private String id;
    private String assignedTo;
    private boolean complete;
    private String fromActionId;
    private String fromObservationId;
    private String title;
    private String ticketAddress;
    private boolean deleted;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setFromActionId(String fromActionId) {
        this.fromActionId = fromActionId;
    }

    public String getFromActionId() {
        return fromActionId;
    }

    public void setFromObservationId(String fromObservationId) {
        this.fromObservationId = fromObservationId;
    }

    public String getFromObservationId() {
        return fromObservationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTicketAddress(String ticketAddress) {
        this.ticketAddress = ticketAddress;
    }

    public String getTicketAddress() {
        return ticketAddress;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
