package com.example.retrospect.core.models;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class Retrospective {
    private String id;
    private String previousRetrospectiveId;
    private String readableId;
    private Audit audit;
    private ImmutableList<Action> actions;
    private ImmutableList<Observation> wentWell;
    private ImmutableList<Observation> couldBeBetter;
    private ImmutableList<User> administrators;
    private ImmutableList<User> members;

    public Retrospective(
            String id,
            String readableId,
            String previousRetrospectiveId,
            Audit audit,
            ImmutableList<Action> actions,
            ImmutableList<Observation> wentWell,
            ImmutableList<Observation> couldBeBetter,
            ImmutableList<User> administrators,
            ImmutableList<User> members) {
        this.readableId = readableId;
        if (audit == null){
            throw new RuntimeException("Cannot create a Retrospective without any audit");
        }

        this.id = id;
        this.previousRetrospectiveId = previousRetrospectiveId;
        this.audit = audit;
        this.actions = actions;
        this.wentWell = wentWell;
        this.couldBeBetter = couldBeBetter;
        this.administrators = administrators;
        this.members = members;

        if (this.administrators == null){
            this.administrators = ImmutableList.empty();
        }
        if (this.members == null){
            this.members = ImmutableList.empty();
        }
        if (this.actions == null){
            this.actions = ImmutableList.empty();
        }
        if (this.wentWell == null){
            this.wentWell = ImmutableList.empty();
        }
        if (this.couldBeBetter == null){
            this.couldBeBetter = ImmutableList.empty();
        }
    }

    public String getId() {
        return id;
    }

    public String getPreviousRetrospectiveId() {
        return this.previousRetrospectiveId;
    }

    public String getReadableId() {
        return readableId;
    }

    public Audit getAudit() {
        return audit;
    }

    public ImmutableList<Action> getActions(boolean includeDeleted) {
        return includeDeleted
                ? actions
                : actions.except(Action::isDeleted);
    }

    public ImmutableList<Observation> getWentWell(boolean includeDeleted) {
        return includeDeleted
                ? wentWell
                : wentWell.except(Observation::isDeleted);
    }

    public ImmutableList<Observation> getCouldBeBetter(boolean includeDeleted) {
        return includeDeleted
                ? couldBeBetter
                : couldBeBetter.except(Observation::isDeleted);
    }

    public ImmutableList<User> getAdministrators() {
        return administrators;
    }

    public ImmutableList<User> getMembers() {
        return members;
    }

    public void addAdministrator(User administrator, LoggedInUser user) {
        this.administrators = this.getMembers().union(administrator);
        this.audit.update(user, "Administrator added");
    }

    public void removeAdministrator(User administrator, LoggedInUser user){
        this.administrators = this.getMembers().except(administrator);
        this.audit.update(user, "Administrator removed");
    }

    public void addMember(User member, LoggedInUser user) {
        this.members = this.getMembers().union(member);
        this.audit.update(user, "Member added");
    }

    public void removeMember(User member, LoggedInUser user) {
        this.members = this.getMembers().except(member);
        this.audit.update(user, "Member removed");
    }

    public void addCouldBeBetter(Observation observation, LoggedInUser user) {
        this.couldBeBetter = this.couldBeBetter.concat(observation);
        this.audit.update(user, "Add 'could be better' observation");
    }

    public void addWentWell(Observation observation, LoggedInUser user) {
        this.wentWell = this.wentWell.concat(observation);
        this.audit.update(user, "Add 'went well' observation");
    }

    public void addAction(Action action, LoggedInUser user) {
        this.actions = this.actions.concat(action);
        this.audit.update(user, "Add action");
    }

    public void removeCouldBeBetter(String observationId, LoggedInUser user) {
        this.couldBeBetter = removeObservation(this.couldBeBetter, observationId, user, "could be better");
    }

    public void removeWentWell(String observationId, LoggedInUser user) {
        this.wentWell = removeObservation(this.wentWell, observationId, user, "went well");
    }

    public void removeAction(String actionId, LoggedInUser user) {
        var actionDeleted = new AtomicBoolean(false);

        Function<Action, Action> setDeleted = action -> {
            if (action.isDeleted()){
                return action;
            }

            action.setDeleted(true, user);
            actionDeleted.set(true);
            return action;
        };

        var newActions = new ImmutableList<>(actions.stream().map(action ->
                action.getId().equals(actionId)
                        ? setDeleted.apply(action)
                        : action));

        if (actionDeleted.get()) {
            this.audit.update(user, "Removed action");
            this.actions = newActions;
        }
    }

    private ImmutableList<Observation> removeObservation(ImmutableList<Observation> originalObservations, String observationId, LoggedInUser user, String typeOfObservation) {
        var observationDeleted = new AtomicBoolean(false);

        Function<Observation, Observation> setDeleted = observation -> {
            if (observation.isDeleted()){
                return observation;
            }

            observation.setDeleted(true, user);
            observationDeleted.set(true);
            return observation;
        };

        var newObservations = new ImmutableList<>(originalObservations.stream().map(observation ->
                observation.getId().equals(observationId)
                        ? setDeleted.apply(observation)
                        : observation));

        if (observationDeleted.get()) {
            this.audit.update(user, "Removed '" + typeOfObservation + "' observation");
            return newObservations;
        }

        return originalObservations;
    }

    public Observation getObservation(String observationId, String type) {
        ImmutableList<Observation> observations;

        switch (type) {
            case Observation.COULD_BE_BETTER:
                observations = this.couldBeBetter;
                break;
            case Observation.WENT_WELL:
                observations = this.wentWell;
                break;
            default:
                throw new RuntimeException("Unrecognised observation type");
        }

        return observations.stream().filter(o -> o.getId().equals(observationId)).findFirst().orElse(null);
    }

    public Action getAction(String actionId) {
        return actions.stream().filter(o -> o.getId().equals(actionId)).findFirst().orElse(null);
    }

    public void setAdministrators(List<User> administrators) {
        this.administrators = new ImmutableList<>(administrators.stream());
    }

    public void setMembers(List<User> members) {
        this.members = new ImmutableList<>(members.stream());
    }

    public void setReadableId(String readableId) {
        this.readableId = readableId;
    }

    public void setPreviousRetrospectiveId(String previousRetrospectiveId) {
        this.previousRetrospectiveId = previousRetrospectiveId;
    }
}

