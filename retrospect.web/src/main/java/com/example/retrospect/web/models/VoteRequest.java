package com.example.retrospect.web.models;

public class VoteRequest {
    private String retrospectiveId;
    private String observationId;
    private String observationType;

    public String getRetrospectiveId() {
        return retrospectiveId;
    }

    public void setRetrospectiveId(String retrospectiveId) {
        this.retrospectiveId = retrospectiveId;
    }

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }
}
