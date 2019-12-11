package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.Observation;

public class ObservationViewModel {
    private final Observation observation;

    public ObservationViewModel(Observation observation) {
        this.observation = observation;
    }

    public String getId(){
        return observation.getId();
    }

    public String getTitle(){
        return observation.getTitle();
    }
}
