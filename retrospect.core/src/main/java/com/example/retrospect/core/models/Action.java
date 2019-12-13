package com.example.retrospect.core.models;

public class Action {
    private String id;
    private String title;
    private Audit audit;
    private boolean deleted;
    private String ticketAddress;
    private User assignedTo;

    public Action(String id, String title, Audit audit, boolean deleted, String ticketAddress, User assignedTo) {
        if (audit == null){
            throw new RuntimeException("Cannot create an Action without any audit");
        }

        this.id = id;
        this.title = title;
        this.audit = audit;
        this.deleted = deleted;
        this.ticketAddress = ticketAddress;
        this.assignedTo = assignedTo;
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
}

