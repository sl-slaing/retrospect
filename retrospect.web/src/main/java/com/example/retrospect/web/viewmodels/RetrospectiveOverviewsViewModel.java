package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RetrospectiveOverviewsViewModel {
    private final List<RetrospectiveOverview> retrospectives;
    private final LoggedInUser user;

    public RetrospectiveOverviewsViewModel(List<RetrospectiveOverview> retrospectives, LoggedInUser user) {
        this.retrospectives = retrospectives;
        this.user = user;
    }

    public Map<String, RetrospectiveOverview> getRetrospectives() {
        return retrospectives
                .stream()
                .collect(Collectors.toMap(RetrospectiveOverview::getId, r -> r));
    }

    public UserViewModel getUser() {
        return new UserViewModel(user);
    }
}
