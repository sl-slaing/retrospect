package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.Action;

public class ActionViewModel {
    private final Action action;

    public ActionViewModel(Action action) {
        this.action = action;
    }

    public String getId(){
        return action.getId();
    }

    public String getTitle(){
        return action.getTitle();
    }
}
