package com.example.retrospect.web.models.import_export.v1_0;

import java.util.List;

public class V1_0_ImportableObservation {
    private String id;
    private String title;
    private List<String> votes;
    private String type;
    private boolean deleted;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

    public List<String> getVotes() {
        return votes;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }
}
