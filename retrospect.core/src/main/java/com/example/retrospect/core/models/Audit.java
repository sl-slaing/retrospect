package com.example.retrospect.core.models;

import java.time.OffsetDateTime;

public class Audit {
    private OffsetDateTime createdOn;
    private User createdBy;
    private OffsetDateTime lastUpdatedOn;
    private User lastUpdatedBy;
    private String lastChange;

    public Audit(OffsetDateTime createdOn, LoggedInUser createdBy) {
        this.createdBy = createdBy;
        this.lastUpdatedBy = createdBy;
        this.createdOn = createdOn;
        this.lastUpdatedOn = createdOn;
        this.lastChange = "Created";
    }

    public Audit(OffsetDateTime createdOn, User createdBy, OffsetDateTime lastUpdatedOn, User lastUpdatedBy, String lastChange) {
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastUpdatedOn = lastUpdatedOn;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastChange = lastChange;
    }

    public void update(LoggedInUser user, String change){
        lastUpdatedBy = user;
        lastUpdatedOn = OffsetDateTime.now();
        lastChange = change;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public User getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getLastChange() {
        return lastChange;
    }
}
