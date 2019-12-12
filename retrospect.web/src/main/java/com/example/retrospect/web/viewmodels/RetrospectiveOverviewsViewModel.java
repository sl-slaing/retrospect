package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;

import java.util.List;

public class RetrospectiveOverviewsViewModel {
    private final List<RetrospectiveOverview> retrospectives;
    private final LoggedInUser user;

    public RetrospectiveOverviewsViewModel(List<RetrospectiveOverview> retrospectives, LoggedInUser user) {
        this.retrospectives = retrospectives;
        this.user = user;
    }

    public List<RetrospectiveOverview> getRetrospectives() {
        return retrospectives;
    }

    public LoggedInUser getUser() {
        return user;
    }
}
