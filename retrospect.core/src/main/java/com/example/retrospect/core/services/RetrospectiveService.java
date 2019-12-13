package com.example.retrospect.core.services;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.exceptions.ValidationException;
import com.example.retrospect.core.managers.RetrospectiveSecurityManager;
import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.RetrospectiveRepository;
import com.example.retrospect.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (retrospective != null && securityManager.canViewRetrospective(retrospective, user)) {
            return retrospective;
        } else {
            return null;
        }
    }

    public Retrospective addRetrospective(String previousRetrospectiveId, String readableId, List<String> members, List<String> administrators, LoggedInUser user) {
        var newId = Guid.next();
        Stream<User> administratorsSet = administrators == null
                ? Stream.empty()
                : administrators.stream().map(this::getUserOrNotFound);
        administratorsSet = Stream.concat(
                administratorsSet,
                administrators == null || administrators.stream().anyMatch(username -> user.getUsername().equals(username))
                    ? Stream.empty()
                    : Stream.of(user));

        Stream<User> memberSet = members == null
                ? Stream.empty()
                : members.stream().map(this::getUserOrNotFound);

        var retrospective = new Retrospective(
                newId,
                readableId == null || readableId.equals("")
                        ? getReadableId(newId, 3)
                        : readableId,
                previousRetrospectiveId,
                new Audit(OffsetDateTime.now(), user),
                new ImmutableList<>(Stream.empty()),
                new ImmutableList<>(Stream.empty()),
                new ImmutableList<>(Stream.empty()),
                new ImmutableList<>(administratorsSet),
                new ImmutableList<>(memberSet));
        repository.addOrReplace(retrospective);

        return retrospective;
    }

    private String getReadableId(String fullId, int minLength) {
        var reservedIds = repository.getAll()
                .filter(r -> !r.getId().equals(fullId))
                .map(r -> r.getReadableId() != null ? r.getReadableId() : r.getId())
                .collect(Collectors.toSet());

        for (var length = minLength; length <= fullId.length(); length++){
            var candidateId = fullId.substring(0, length);
            if (!reservedIds.contains(candidateId)){
                return candidateId;
            }
        }

        return fullId;
    }

    public void removeRetrospective(String retrospectiveId, LoggedInUser loggedInUser) {
        var retrospective = getRetrospective(retrospectiveId, loggedInUser);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        if (!securityManager.canAdministerRetrospective(retrospective, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to administer this retrospective");
        }

        repository.remove(retrospectiveId);
    }

    public Observation addObservation(String retrospectiveId, String type, String title, LoggedInUser user) {
        var audit = new Audit(OffsetDateTime.now(), user);
        var observation = new Observation(Guid.next(), title, audit, false, Collections.emptyList());

        return editRetrospective(retrospectiveId, user, retrospective -> {
            if (type == null) {
                throw new RuntimeException("Invalid Observation type");
            } else if (type.equals(Observation.COULD_BE_BETTER)) {
                retrospective.addCouldBeBetter(observation, user);
            } else if (type.equals(Observation.WENT_WELL)) {
                retrospective.addWentWell(observation, user);
            } else {
                throw new RuntimeException("Invalid Observation type");
            }
        }).getObservation(observation.getId(), type);
    }

    public Observation updateObservation(String retrospectiveId, String observationId, String type, LoggedInUser user, Consumer<Observation> command){
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
        }).getObservation(observationId, type);
    }

    public void removeObservation(String retrospectiveId, String observationId, String type, LoggedInUser user){
        editRetrospective(retrospectiveId, user, retrospective -> {
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

    public Action addAction(ActionDetails actionDetails, LoggedInUser user) {
        var audit = new Audit(OffsetDateTime.now(), user);
        var assignedTo = actionDetails.getAssignedToUsername() != null
                ? userRepository.getUser(actionDetails.getAssignedToUsername())
                : null;
        var action = new Action(
                Guid.next(),
                actionDetails.getTitle(),
                audit,
                false,
                actionDetails.getTicketAddress(),
                assignedTo,
                actionDetails.getFromActionId(),
                actionDetails.getFromObservationId(),
                actionDetails.isComplete());

        return editRetrospective(actionDetails.getRetrospectiveId(), user, retrospective -> {
            retrospective.addAction(action, user);
        }).getAction(action.getId());
    }

    public Action updateAction(ActionDetails actionDetails, LoggedInUser user){
        return editRetrospective(actionDetails.getRetrospectiveId(), user, retrospective -> {
            var action = retrospective.getActions(false).stream()
                    .filter(o -> o.getId().equals(actionDetails.getActionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Action not found"));

            action.setTitle(actionDetails.getTitle(), user);
            action.setTicketAddress(actionDetails.getTicketAddress(), user);
            var assignedTo = actionDetails.getAssignedToUsername() != null
                    ? userRepository.getUser(actionDetails.getAssignedToUsername())
                    : null;
            action.setAssignedTo(assignedTo, user);
            action.setComplete(actionDetails.isComplete(), user);

            action.setFromActionId(actionDetails.getFromActionId());
            action.setFromObservationId(actionDetails.getFromObservationId());
        }).getAction(actionDetails.getActionId());
    }

    public void removeAction(String retrospectiveId, String actionId, LoggedInUser user) {
        editRetrospective(retrospectiveId, user, retrospective -> retrospective.removeAction(actionId, user));
    }

    public Observation applyVote(String retrospectiveId, String observationId, String type, LoggedInUser user){
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
                throw new NotFoundException("Observation not found");
            }

            var observation = observations.stream().findFirst().get();
            observation.toggleVote(user);
        }).getObservation(observationId, type);
    }

    public Retrospective addMember(String retrospectiveId, String memberUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var alreadyAMember = retrospective.getMembers().stream().anyMatch(m -> m.getUsername().equals(memberUserId));

            if (alreadyAMember){
                return;
            }

            var member = this.userRepository.getUser(memberUserId);
            if (member == null){
                throw new NotFoundException("Unable to find member");
            }

            retrospective.addMember(member, user);
        });
    }

    public Retrospective removeMember(String retrospectiveId, String memberUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var membersToRemove = retrospective.getMembers().stream().filter(m -> m.getUsername().equals(memberUserId));

            membersToRemove.forEach(member -> retrospective.removeMember(member, user));
        });
    }

    public Retrospective addAdministrator(String retrospectiveId, String administratorUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var alreadyAMember = retrospective.getMembers().stream().anyMatch(m -> m.getUsername().equals(administratorUserId));

            if (alreadyAMember){
                return;
            }

            var administrator = this.userRepository.getUser(administratorUserId);
            if (administrator == null){
                throw new NotFoundException("Unable to find administrator");
            }

            retrospective.addAdministrator(administrator, user);
        });
    }

    public Retrospective removeAdministrator(String retrospectiveId, String administratorUserId, LoggedInUser user){
        return administerRetrospective(retrospectiveId, user, retrospective -> {
            var membersToRemove = retrospective.getMembers().stream().filter(m -> m.getUsername().equals(administratorUserId));

            membersToRemove.forEach(administrator -> retrospective.removeAdministrator(administrator, user));
        });
    }

    private Retrospective editRetrospective(String retrospectiveId, LoggedInUser user, Consumer<Retrospective> edit){
        var retrospective = getRetrospective(retrospectiveId, user);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        if (!securityManager.canEditRetrospective(retrospective, user)) {
            throw new NotPermittedException("You're not permitted to edit this retrospective");
        }

        edit.accept(retrospective);

        repository.addOrReplace(retrospective);
        return retrospective;
    }

    private Retrospective administerRetrospective(String retrospectiveId, LoggedInUser user, Consumer<Retrospective> administer){
        var retrospective = getRetrospective(retrospectiveId, user);
        if (retrospective == null) {
            throw new NotFoundException("Retrospective not found");
        }

        if (!securityManager.canAdministerRetrospective(retrospective, user)) {
            throw new NotPermittedException("You're not permitted to administer this retrospective");
        }

        administer.accept(retrospective);

        repository.addOrReplace(retrospective);
        return retrospective;
    }

    public Stream<Retrospective> getAllRetrospectives(LoggedInUser user) {
        return repository.getAll()
                .filter(retro -> this.securityManager.canViewRetrospective(retro, user));
    }

    public Retrospective applyAdministrationDetails(RetrospectiveAdministrationDetails request, LoggedInUser loggedInUser) {
        return administerRetrospective(request.getId(), loggedInUser, retrospective -> {
                if (request.getAdministrators().isEmpty()) {
                    throw new ValidationException("A retrospective must have some administrators");
                }

                if (request.getAdministrators().stream().noneMatch(admin -> admin.equalsIgnoreCase(loggedInUser.getUsername()))) {
                    throw new ValidationException("You cannot remove yourself from the list of administrators");
                }

                if (request.getReadableId().equals("") || request.getReadableId() == null) {
                    throw new ValidationException("A retrospective cannot have a null/empty readable id");
                }

                if (this.repository.getAll().anyMatch(r -> r.getReadableId().equalsIgnoreCase(request.getReadableId()) && !r.getId().equals(request.getId()))) {
                    throw new ValidationException("Readable id is already in-use");
                }

                if (request.getPreviousRetrospectiveId() != null && this.repository.getAll().noneMatch(r -> r.getId().equals(request.getPreviousRetrospectiveId()))) {
                    throw new ValidationException("Unable to find previous retrospective");
                }

                if (request.getPreviousRetrospectiveId() != null && request.getPreviousRetrospectiveId().equals(retrospective.getId())) {
                    throw new ValidationException("Retrospective cannot have itself as a previous retrospective");
                }

                retrospective.setAdministrators(request.getAdministrators().stream().map(this::getUserOrNotFound).collect(Collectors.toList()));
                retrospective.setMembers(request.getMembers().stream().map(this::getUserOrNotFound).collect(Collectors.toList()));
                retrospective.setReadableId(request.getReadableId());
                retrospective.setPreviousRetrospectiveId(request.getPreviousRetrospectiveId());
        });
    }

    private User getUserOrNotFound(String username) {
        var user = this.userRepository.getUser(username);
        if (user != null){
            return user;
        }

        return new NotFoundUser(username);
    }
}
