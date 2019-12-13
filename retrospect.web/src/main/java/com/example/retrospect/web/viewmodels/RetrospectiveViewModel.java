package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Observation;
import com.example.retrospect.core.models.Retrospective;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RetrospectiveViewModel {
    private final Retrospective retrospective;
    private final Retrospective previousRetrospective;
    private final LoggedInUser loggedInUser;

    public RetrospectiveViewModel(Retrospective retrospective, Retrospective previousRetrospective, LoggedInUser loggedInUser) {
        this.retrospective = retrospective;
        this.previousRetrospective = previousRetrospective;
        this.loggedInUser = loggedInUser;
    }

    public String getCreatedOn(){
        return retrospective.getAudit().getCreatedOn().toString();
    }

    public String getId(){
        return retrospective.getId();
    }

    public String getReadableId() {
        return retrospective.getReadableId();
    }

    public String getPreviousRetrospectiveId(){
        return retrospective.getPreviousRetrospectiveId();
    }

    public String getPreviousRetrospectiveReadableId() {
        return previousRetrospective != null
                ? previousRetrospective.getReadableId()
                : getPreviousRetrospectiveId();
    }

    public String getPreviousRetrospectiveCreatedOn() {
        DateTimeFormatter format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        return previousRetrospective != null
                ? format.format(previousRetrospective.getAudit().getCreatedOn())
                : null;
    }

    public Map<String, ActionViewModel> getActions(){
        return retrospective.getActions(false)
                .stream()
                .map(ActionViewModel::new)
                .collect(Collectors.toMap(ActionViewModel::getId, a -> a));
    }

    public Map<String, ActionViewModel> getPreviousRetrospectiveActions(){
        if (previousRetrospective == null) {
            return Collections.emptyMap();
        }

        return previousRetrospective.getActions(false)
                .stream()
                .map(ActionViewModel::new)
                .collect(Collectors.toMap(ActionViewModel::getId, a -> a));
    }

    public Map<String, ObservationViewModel> getCouldBeBetter(){
        return retrospective.getCouldBeBetter(false)
                .stream()
                .map((Observation observation) -> new ObservationViewModel(observation, loggedInUser, Observation.COULD_BE_BETTER))
                .collect(Collectors.toMap(ObservationViewModel::getId, o -> o));
    }

    public Map<String, ObservationViewModel> getWentWell(){
        return retrospective.getWentWell(false)
                .stream()
                .map((Observation observation) -> new ObservationViewModel(observation, loggedInUser, Observation.WENT_WELL))
                .collect(Collectors.toMap(ObservationViewModel::getId, o -> o));
    }

    public Map<String, UserViewModel> getMembers(){
        return retrospective.getMembers().stream().map(UserViewModel::new).collect(Collectors.toMap(UserViewModel::getUsername, u -> u));
    }

    public Map<String, UserViewModel> getAdministrators(){
        return retrospective.getAdministrators().stream().map(UserViewModel::new).collect(Collectors.toMap(UserViewModel::getUsername, u -> u));
    }

    public boolean isAdministrator(){
        return retrospective.getAdministrators().stream().anyMatch(admin -> admin.equals(loggedInUser));
    }

    public UserViewModel getUser() {
        return new UserViewModel(loggedInUser);
    }
}
