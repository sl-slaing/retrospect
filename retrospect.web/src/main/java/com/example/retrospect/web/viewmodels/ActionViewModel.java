package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.Action;

public class ActionViewModel {
    private final Action action;

    public ActionViewModel(Action action) {
        this.action = action;
    }

    public String getId(){
        return action.getId();
    }

    public String getTitle(){
        return action.getTitle();
    }

    public String getFromActionId() {
        return action.getFromActionId();
    }

    public String getFromObservationId() {
        return action.getFromObservationId();
    }

    public String getTicketAddress() {
        return action.getTicketAddress();
    }

    public boolean getComplete() {
        return action.getComplete();
    }

    public long getSortIdentifier() {
        return action.getAudit().getCreatedOn().toEpochSecond();
    }

    public UserViewModel getAssignedTo() {
        return action.getAssignedTo() != null ? new UserViewModel(action.getAssignedTo()) : null;
    }
}
