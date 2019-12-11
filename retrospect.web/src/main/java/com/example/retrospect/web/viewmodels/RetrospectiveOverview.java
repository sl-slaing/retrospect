package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.Retrospective;

public class RetrospectiveOverview {
    private final Retrospective retrospective;

    public RetrospectiveOverview(Retrospective retrospective) {
        this.retrospective = retrospective;
    }

    public String getCreatedOn(){
        return retrospective.getAudit().getCreatedOn().toString();
    }

    public String getId(){
        return retrospective.getId();
    }
}
