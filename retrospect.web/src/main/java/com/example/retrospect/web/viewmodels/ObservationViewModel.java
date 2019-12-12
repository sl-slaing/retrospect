package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Observation;

public class ObservationViewModel {
    private final Observation observation;
    private final LoggedInUser user;

    public ObservationViewModel(Observation observation, LoggedInUser user) {
        this.observation = observation;
        this.user = user;
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

    public boolean getHasVoted(){
        return observation.hasVoted(user);
    }
}
