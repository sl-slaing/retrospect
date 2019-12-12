package com.example.retrospect.web.viewmodels;

import java.util.List;

public class RetrospectiveOverviewsViewModel {
    private final List<RetrospectiveOverview> retrospectives;
    private final UserViewModel user;

    public RetrospectiveOverviewsViewModel(List<RetrospectiveOverview> retrospectives, UserViewModel user) {
        this.retrospectives = retrospectives;
        this.user = user;
    }

    public List<RetrospectiveOverview> getRetrospectives() {
        return retrospectives;
    }

    public UserViewModel getUser() {
        return user;
    }
}
