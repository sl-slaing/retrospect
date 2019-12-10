package com.example.retrospect.core.services;

import com.example.retrospect.core.managers.RetrospectiveSecurityManager;
import com.example.retrospect.core.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.repositories.RetrospectiveRepository;
import com.example.retrospect.core.repositories.UserRepository;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.function.Consumer;

@Service
public class RetrospectiveService {
    private final RetrospectiveRepository repository;
    private final RetrospectiveSecurityManager securityManager;
    private final UserRepository userRepository;

    @Autowired
    public RetrospectiveService(
            UserRepository userRepository,
            RetrospectiveSecurityManager securityManager,
            RetrospectiveRepository repository) {
        this.userRepository = userRepository;
        this.securityManager = securityManager;
        this.repository = repository;
    }

    public Retrospective getRetrospective(String retrospectiveId, LoggedInUser user){
        var retrospective = repository.getRetrospective(retrospectiveId);
        if (securityManager.canViewRetrospective(retrospective, user)) {
            return retrospective;
        } else {
            return null;
        }
    }

    public Retrospective addObservation(String retrospectiveId, String type, String title, LoggedInUser user) {
        return editRetrospective(retrospectiveId, user, retrospective -> {
            var audit = new Audit(OffsetDateTime.now(), user);
            var observation = new Observation(Guid.next(), title, audit, false, Collections.emptyList());

            if (type == null) {
                throw new RuntimeException("Invalid Observation type");
            } else if (type.equals(Observation.COULD_BE_BETTER)) {
                retrospective.addCouldBeBetter(observation, user);
            } else if (type.equals(Observation.WENT_WELL)) {
                retrospective.addWentWell(observation, user);
            } else {
                throw new RuntimeException("Invalid Observation type");
            }
        });
    }

    public Retrospective updateObservation(String retrospectiveId, String observationId, String type, LoggedInUser user, Consumer<Observation> command){
        return editRetrospective(retrospectiveId, user, retrospective -> {
            ImmutableList<Observation> observations;

            if (type == null) {
                throw new RuntimeException("Invalid Observation type");
            } else if (type.equals(Observation.COULD_BE_BETTER)) {
                observations = retrospective.getCouldBeBetter(false);
            } else if (type.equals(Observation.WENT_WELL)) {
                observations = retrospective.getWentWell(false);
            } else {
                throw new RuntimeException("Invalid Observation type");
            }

            var observation = observations.stream()
                    .filter(o -> o.getId().equals(observationId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Observation not found"));

            command.accept(observation);
        });
    }

    public Retrospective removeObservation(String retrospectiveId, String observationId, String type, LoggedInUser user){
        return editRetrospective(retrospectiveId, user, retrospective -> {
            if (type == null) {
                throw new RuntimeException("Invalid Observation type");
            } else if (type.equals(Observation.COULD_BE_BETTER)) {
                retrospective.removeCouldBeBetter(observationId, user);
            } else if (type.equals(Observation.WENT_WELL)) {
                retrospective.removeWentWell(observationId, user);
            } else {
                throw new RuntimeException("Invalid Observation type");
            }
        });
    }

    public Retrospective addAction(String retrospectiveId, String title, LoggedInUser user, String ticketAddress, Identifiable assignedTo) {
        return editRetrospective(retrospectiveId, user, retrospective -> {
            var audit = new Audit(OffsetDateTime.now(), user);
            var action = new Action(Guid.next(), title, audit, false, ticketAddress, assignedTo);

            retrospective.addAction(action, user);
        });
    }

    public Retrospective updateAction(String retrospectiveId, String actionId, LoggedInUser user, Consumer<Action> command){
        return editRetrospective(retrospectiveId, user, retrospective -> {
            var action = retrospective.getActions(false).stream()
                    .filter(o -> o.getId().equals(actionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Action not found"));

            command.accept(action);
        });
    }

    public Retrospective removeAction(String retrospectiveId, String actionId, LoggedInUser user) {
        return editRetrospective(retrospectiveId, user, retrospective -> retrospective.removeAction(actionId, user));
    }

    public Retrospective applyVote(String retrospectiveId, String observationId, String type, LoggedInUser user){
        return editRetrospective(retrospectiveId, user, retrospective -> {
            ImmutableList<Observation> observations;

            if (type == null) {
                throw new RuntimeException("Invalid Observation type");
            } else if (type.equals(Observation.COULD_BE_BETTER)) {
                observations = retrospective.getCouldBeBetter(false).only(ob -> ob.getId().equals(observationId));
            } else if (type.equals(Observation.WENT_WELL)) {
                observations = retrospective.getWentWell(false).only(ob -> ob.getId().equals(observationId));
            } else {
                throw new RuntimeException("Invalid Observation type");
            }

            if (observations.size() != 1) {
                throw new RuntimeException("Observation not found");
            }

            var observation = observations.stream().findFirst().get();
            observation.toggleVote(user);
        });
    }

    public Retrospective addMember(String retrospectiveId, String memberUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var alreadyAMember = retrospective.getMembers().stream().anyMatch(m -> m.getId().equals(memberUserId));

            if (alreadyAMember){
                return;
            }

            var member = this.userRepository.getUser(memberUserId);
            if (member == null){
                throw new RuntimeException("Unable to find member");
            }

            retrospective.addMember(member, user);
        });
    }

    public Retrospective removeMember(String retrospectiveId, String memberUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var membersToRemove = retrospective.getMembers().stream().filter(m -> m.getId().equals(memberUserId));

            membersToRemove.forEach(member -> retrospective.removeMember(member, user));
        });
    }

    public Retrospective addAdministrator(String retrospectiveId, String administratorUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var alreadyAMember = retrospective.getMembers().stream().anyMatch(m -> m.getId().equals(administratorUserId));

            if (alreadyAMember){
                return;
            }

            var administrator = this.userRepository.getUser(administratorUserId);
            if (administrator == null){
                throw new RuntimeException("Unable to find administrator");
            }

            retrospective.addAdministrator(administrator, user);
        });
    }

    public Retrospective removeAdministrator(String retrospectiveId, String administratorUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var membersToRemove = retrospective.getMembers().stream().filter(m -> m.getId().equals(administratorUserId));

            membersToRemove.forEach(administrator -> retrospective.removeAdministrator(administrator, user));
        });
    }

    private Retrospective editRetrospective(String retrospectiveId, LoggedInUser user, Consumer<Retrospective> edit){
        var retrospective = getRetrospective(retrospectiveId, user);
        if (retrospective == null) {
            throw new RuntimeException("Retrospective not found");
        }

        if (!securityManager.canEditRetrospective(retrospective, user)) {
            throw new RuntimeException("You're not permitted to edit this retrospective");
        }

        edit.accept(retrospective);

        repository.addOrReplace(retrospective);
        return retrospective;
    }

    private Retrospective administerRetrospective(String retrospectiveId, LoggedInUser user, Consumer<Retrospective> administer){
        var retrospective = getRetrospective(retrospectiveId, user);
        if (retrospective == null) {
            throw new RuntimeException("Retrospective not found");
        }

        if (!securityManager.canAdministerRetrospective(retrospective, user)) {
            throw new RuntimeException("You're not permitted to administer this retrospective");
        }

        administer.accept(retrospective);

        repository.addOrReplace(retrospective);
        return retrospective;
    }
}
