package com.example.retrospect.web.models;

public class UpdateObservationRequest {
    private String retrospectiveId;
    private String observationId;
    private String observationType;
    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
