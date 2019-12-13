package com.example.retrospect.core.models;

public interface ActionDetails {
    String getRetrospectiveId();
    String getActionId();
    String getTitle();
    String getTicketAddress();
    String getAssignedToUsername();
    String getFromActionId();
    String getFromObservationId();
    boolean isComplete();
}
