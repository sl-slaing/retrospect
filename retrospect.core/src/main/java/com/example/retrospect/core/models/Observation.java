package com.example.retrospect.core.models;

import java.util.List;

public class Observation implements Identifiable {
    public static final String TYPE_NAME = "OBSERVATION";
    public static final String WENT_WELL = "WENT_WELL";
    public static final String COULD_BE_BETTER = "COULD_BE_BETTER";

    private String id;
    private String title;
    private Audit audit;
    private boolean deleted;
    private ImmutableList<User> votes;

    public Observation(String id, String title, Audit audit, boolean deleted, List<User> votes) {
        if (audit == null){
            throw new RuntimeException("Cannot create an Observation without any audit");
        }

        this.id = id;
        this.title = title;
        this.audit = audit;
        this.deleted = deleted;
        this.votes = new ImmutableList<>(votes);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title, LoggedInUser user) {
        this.title = title;
        this.audit.update(user, "Update title");
    }

    public Audit getAudit() {
        return audit;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted, LoggedInUser user) {
        this.deleted = deleted;
        this.audit.update(user, "Update deleted");
    }

    public ImmutableList<User> getVotes() {
        return votes;
    }

    public void toggleVote(LoggedInUser user) {
        if (hasVoted(user)){
            votes = votes.except(v -> v.getId().equals(user.getId()));
        } else {
            votes = votes.union(user);
        }
    }

    public boolean hasVoted(Identifiable user){
        return votes.stream().anyMatch(v -> v.getId().equals(user.getId()));
    }
}

