package com.example.retrospect.web.models;

import com.example.retrospect.core.models.ActionDetails;

public class UpdateActionRequest implements ActionDetails {
    private String retrospectiveId;
    private String actionId;
    private String title;
    private String assignedToUsername;
    private String ticketAddress;
    private String fromActionId;
    private String fromObservationId;
    private boolean complete;

    public String getRetrospectiveId() {
        return retrospectiveId;
    }

    public void setRetrospectiveId(String retrospectiveId) {
        this.retrospectiveId = retrospectiveId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
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

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    public void setTicketAddress(String ticketAddress) {
        this.ticketAddress = ticketAddress;
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

    public String getFromObservationId() {
        return fromObservationId;
    }

    public void setFromObservationId(String fromObservationId) {
        this.fromObservationId = fromObservationId;
    }
}
