package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Observation;

public class ObservationViewModel {
    private final Observation observation;
    private final LoggedInUser user;
    private final String type;

    public ObservationViewModel(Observation observation, LoggedInUser user, String type) {
        this.observation = observation;
        this.user = user;
        this.type = type;
    }

    public String getId(){
        return observation.getId();
    }

    public String getTitle(){
        return observation.getTitle();
    }

    public int getVotes(){
        return observation.getVotes().size();
    }

    public String getType() {
        return type;
    }

    public boolean getHasVoted(){
        return observation.hasVoted(user);
    }

    public long getSortIdentifier() {
        return observation.getAudit().getCreatedOn().toEpochSecond();
    }
}
