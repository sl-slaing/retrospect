package com.example.retrospect.core.models;

import java.util.List;

public class Observation {
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

    public String getId() {
        return id;
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
            votes = votes.except(voter -> voter.equals(user));
        } else {
            votes = votes.union(user);
        }
    }

    public boolean hasVoted(User user){
        return votes.stream().anyMatch(voter -> voter.equals(user));
    }
}

