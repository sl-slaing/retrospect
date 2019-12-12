package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Retrospective;

import java.util.List;
import java.util.stream.Collectors;

public class RetrospectiveViewModel {
    private final Retrospective retrospective;
    private final LoggedInUser loggedInUser;

    public RetrospectiveViewModel(Retrospective retrospective, LoggedInUser loggedInUser) {
        this.retrospective = retrospective;
        this.loggedInUser = loggedInUser;
    }

    public String getCreatedOn(){
        return retrospective.getAudit().getCreatedOn().toString();
    }

    public String getId(){
        return retrospective.getId();
    }

    public List<ActionViewModel> getActions(){
        return retrospective.getActions(false).stream().map(ActionViewModel::new).collect(Collectors.toList());
    }

    public List<ObservationViewModel> getCouldBeBetter(){
        return retrospective.getCouldBeBetter(false).stream().map(ObservationViewModel::new).collect(Collectors.toList());
    }

    public List<ObservationViewModel> getWentWell(){
        return retrospective.getWentWell(false).stream().map(ObservationViewModel::new).collect(Collectors.toList());
    }

    public List<UserViewModel> getMembers(){
        return retrospective.getMembers().stream().map(UserViewModel::new).collect(Collectors.toList());
    }

    public boolean isAdministrator(){
        return retrospective.getAdministrators().stream().anyMatch(admin -> admin.equals(loggedInUser));
    }
}
