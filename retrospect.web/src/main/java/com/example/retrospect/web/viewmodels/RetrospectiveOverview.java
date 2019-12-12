package com.example.retrospect.web.viewmodels;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Retrospective;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RetrospectiveOverview {
    private final Retrospective retrospective;
    private final LoggedInUser user;

    public RetrospectiveOverview(Retrospective retrospective, LoggedInUser user) {
        this.retrospective = retrospective;
        this.user = user;
    }

    public String getCreatedOn(){
        DateTimeFormatter format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        return format.format(retrospective.getAudit().getCreatedOn());
    }

    public int getActionCount(){
        return retrospective.getActions(false).size();
    }

    public int getWentWellCount(){
        return retrospective.getWentWell(false).size();
    }

    public int getCouldBeBetterCount(){
        return retrospective.getCouldBeBetter(false).size();
    }

    public int getMemberCount(){
        return retrospective.getMembers().size();
    }

    public boolean getCreatedBySelf(){
        return retrospective.getAudit().getCreatedBy().equals(this.user);
    }

    public String getCreatedByAvatarUrl(){
        return retrospective.getAudit().getCreatedBy().getAvatarUrl();
    }

    public String getLastUpdatedByAvatarUrl(){
        return retrospective.getAudit().getLastUpdatedBy().getAvatarUrl();
    }

    public String getId(){
        return retrospective.getId();
    }
}
