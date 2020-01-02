package com.example.retrospect.core.serialisable;

import java.util.List;

public class SerialisableObservation {
    private String id;
    private String title;
    private SerialisableAudit audit;
    private List<String> votes;
    private boolean deleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SerialisableAudit getAudit() {
        return audit;
    }

    public void setAudit(SerialisableAudit audit) {
        this.audit = audit;
    }

    public List<String> getVotes() {
        return votes;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
