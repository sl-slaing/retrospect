package models;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class Retrospective implements Identifiable {
    public static final String TYPE_NAME = "RETROSPECTIVE";

    private String id;
    private Audit audit;
    private ImmutableList<Action> actions;
    private ImmutableList<Observation> wentWell;
    private ImmutableList<Observation> couldBeBetter;
    private ImmutableList<Identifiable> administrators;
    private ImmutableList<Identifiable> members;

    public Retrospective(
            String id,
            Audit audit,
            ImmutableList<Action> actions,
            ImmutableList<Observation> wentWell,
            ImmutableList<Observation> couldBeBetter,
            ImmutableList<Identifiable> administrators,
            ImmutableList<Identifiable> members) {
        if (audit == null){
            throw new RuntimeException("Cannot create a Retrospective without any audit");
        }

        this.id = id;
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return TYPE_NAME;
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

    public ImmutableList<Identifiable> getAdministrators() {
        return administrators;
    }

    public ImmutableList<Identifiable> getMembers() {
        return members;
    }

    public void addAdministrator(Identifiable administrator, LoggedInUser user) {
        this.administrators = this.getMembers().union(administrator);
        this.audit.update(user, "Administrator added");
    }

    public void removeAdministrator(Identifiable administrator, LoggedInUser user){
        this.administrators = this.getMembers().except(administrator);
        this.audit.update(user, "Administrator removed");
    }

    public void addMember(Identifiable member, LoggedInUser user) {
        this.members = this.getMembers().union(member);
        this.audit.update(user, "Member added");
    }

    public void removeMember(Identifiable member, LoggedInUser user) {
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
        this.wentWell = removeObservation(this.couldBeBetter, observationId, user, "could be better");
    }

    public void removeWentWell(String observationId, LoggedInUser user) {
        this.wentWell = removeObservation(this.wentWell, observationId, user, "went well");
    }

    public void removeAction(String actionId, LoggedInUser user) {
        AtomicBoolean actionDeleted = new AtomicBoolean(false);

        Function<Action, Action> setDeleted = action -> {
            if (action.isDeleted()){
                return action;
            }

            action.setDeleted(true, user);
            actionDeleted.set(true);
            return action;
        };

        ImmutableList<Action> newActions = new ImmutableList<>(actions.stream().map(action ->
                action.getId().equals(actionId)
                        ? setDeleted.apply(action)
                        : action));

        if (actionDeleted.get()) {
            this.audit.update(user, "Removed action");
            this.actions = newActions;
        }
    }

    private ImmutableList<Observation> removeObservation(ImmutableList<Observation> originalObservations, String observationId, LoggedInUser user, String typeOfObservation) {
        AtomicBoolean observationDeleted = new AtomicBoolean(false);

        Function<Observation, Observation> setDeleted = observation -> {
            if (observation.isDeleted()){
                return observation;
            }

            observation.setDeleted(true, user);
            observationDeleted.set(true);
            return observation;
        };

        ImmutableList<Observation> newObservations = new ImmutableList<>(originalObservations.stream().map(observation ->
                observation.getId().equals(observationId)
                        ? setDeleted.apply(observation)
                        : observation));

        if (observationDeleted.get()) {
            this.audit.update(user, "Removed '" + typeOfObservation + "' observation");
            return newObservations;
        }

        return originalObservations;
    }
}

