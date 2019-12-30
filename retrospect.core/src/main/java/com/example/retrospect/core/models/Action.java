package com.example.retrospect.core.models;

public class Action {
    private String id;
    private String title;
    private Audit audit;
    private boolean deleted;
    private String ticketAddress;
    private User assignedTo;
    private String fromActionId;
    private String fromObservationId;
    private boolean complete;

    public Action(String id, String title, Audit audit, boolean deleted, String ticketAddress, User assignedTo, String fromActionId, String fromObservationId, boolean complete) {
        if (audit == null){
            throw new RuntimeException("Cannot create an Action without any audit");
        }

        this.id = id;
        this.title = title;
        this.audit = audit;
        this.deleted = deleted;
        this.ticketAddress = ticketAddress;
        this.assignedTo = assignedTo;
        this.fromActionId = fromActionId;
        this.fromObservationId = fromObservationId;
        this.complete = complete;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title, LoggedInUser user) {
        this.title = title;
        this.audit.update(user, "Update title");
    }

    public Audit getAudit() {
        return audit;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted, LoggedInUser user) {
        this.deleted = deleted;
        this.audit.update(user, "Update deleted");
    }

    public String getTicketAddress() {
        return ticketAddress;
    }

    public void setTicketAddress(String ticketAddress, LoggedInUser user) {
        this.ticketAddress = ticketAddress;
        this.audit.update(user, "Update ticket address");
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo, LoggedInUser user) {
        this.assignedTo = assignedTo;
        this.audit.update(user, "Update assignee");
    }

    public void setFromActionId(String fromActionId) {
        this.fromActionId = fromActionId;
    }

    public String getFromActionId() {
        return fromActionId;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete, LoggedInUser user) {
        if (this.complete == complete) {
            return;
        }
        this.complete = complete;
        this.audit.update(user, complete ? "Set as complete" : "Uncompleted");
    }

    public String getFromObservationId() {
        return fromObservationId;
    }

    public void setFromObservationId(String fromObservationId) {
        this.fromObservationId = fromObservationId;
    }

    public void setData(String title, User assignedTo, String ticketAddress, String fromObservationId, String fromActionId, boolean complete, LoggedInUser loggedInUser) {
        this.title = title;
        this.assignedTo = assignedTo;
        this.ticketAddress = ticketAddress;
        this.fromObservationId = fromObservationId;
        this.fromActionId = fromActionId;
        this.complete = complete;
        this.audit.update(loggedInUser, "Bulk update data");
    }
}

