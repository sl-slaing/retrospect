package com.example.retrospect.web.services.import_export.v1_0;

import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.UserRepository;
import com.example.retrospect.core.services.RetrospectiveService;
import com.example.retrospect.web.managers.UserSessionManager;
import com.example.retrospect.web.models.import_export.ImportSettings;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableAction;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableObservation;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableRetrospective;
import com.example.retrospect.web.services.import_export.ImportResult;
import com.example.retrospect.web.services.import_export.ImportableRetrospective;
import com.example.retrospect.web.services.import_export.RetrospectiveImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class V1_0_RetrospectiveImporter implements RetrospectiveImporter {
    private final RetrospectiveService retrospectiveService;
    private final UserSessionManager userSessionManager;
    private final UserRepository userRepositority;

    @Autowired
    public V1_0_RetrospectiveImporter(
            RetrospectiveService retrospectiveService,
            UserSessionManager userSessionManager,
            UserRepository userRepositority) {
        this.retrospectiveService = retrospectiveService;
        this.userSessionManager = userSessionManager;
        this.userRepositority = userRepositority;
    }

    @Override
    public void importRetrospective(ImportableRetrospective importable, ImportResult result, ImportSettings settings) {
        importRetrospective((V1_0_ImportableRetrospective)importable, result, settings);
    }

    private void importRetrospective(V1_0_ImportableRetrospective importable, ImportResult result, ImportSettings settings) {
        var loggedInUser = userSessionManager.getLoggedInUser();

        var existingRetrospective = retrospectiveService.getRetrospective(importable.getId(), loggedInUser);

        if (existingRetrospective == null && retrospectiveService.retrospectiveExists(importable.getId())) {
            result.addMessage(importable.getId(), "Unable to import retrospective, data exists but is inaccessible");
            result.setSuccess(false);
            return;
        }

        try {
            if (existingRetrospective == null || (settings.isDryRun() && settings.isRestoreData())) {
                createRetrospective(importable, result, loggedInUser, settings);
            } else if (settings.isPermitMerge()) {
                mergeRetrospective(importable, existingRetrospective, result, loggedInUser, settings);
            } else {
                result.addMessage(importable.getId(), "Unable to import retrospective, data exists and not permitted to update");
                result.setSuccess(false);
            }
        } catch (RuntimeException e){
            result.addMessage(importable.getId(), e.getMessage());
            result.setSuccess(false);
        }
    }

    private void mergeRetrospective(V1_0_ImportableRetrospective importable, Retrospective existingRetrospective, ImportResult result, LoggedInUser loggedInUser, ImportSettings settings) {
        var importableRetrospective = adaptToRetrospective(importable, loggedInUser);

        existingRetrospective.setReadableId(importableRetrospective.getReadableId());
        existingRetrospective.setPreviousRetrospectiveId(importableRetrospective.getPreviousRetrospectiveId());
        existingRetrospective.setAdministrators(
                unionLists(existingRetrospective.getAdministrators(), importableRetrospective.getAdministrators(), User::getUsername)
        );
        existingRetrospective.setMembers(
                unionLists(existingRetrospective.getMembers(), importableRetrospective.getMembers(), User::getUsername)
        );
        importableRetrospective.getActions(settings.isRestoreDeleted())
                .stream()
                .forEach(action -> {
                    var existingAction = existingRetrospective.getAction(action.getId());
                    if (existingAction != null) {
                        existingAction.setData(
                                action.getTitle(),
                                action.getAssignedTo(),
                                action.getTicketAddress(),
                                action.getFromObservationId(),
                                action.getFromActionId(),
                                action.getComplete(),
                                loggedInUser
                        );
                    } else {
                        existingRetrospective.addAction(action, loggedInUser);
                    }
                });
        mergeObservations(
                existingRetrospective.getWentWell(true),
                importableRetrospective.getWentWell(settings.isRestoreDeleted()),
                loggedInUser,
                existingRetrospective::addWentWell);
        mergeObservations(
                existingRetrospective.getCouldBeBetter(true),
                importableRetrospective.getCouldBeBetter(settings.isRestoreDeleted()),
                loggedInUser,
                existingRetrospective::addCouldBeBetter);

        if (settings.applyChanges()) {
            retrospectiveService.updateRetrospective(existingRetrospective, loggedInUser);
        }
        result.addMessage(existingRetrospective.getId(), "Retrospective updated");
    }

    private void mergeObservations(
            ImmutableList<Observation> existing,
            ImmutableList<Observation> other,
            LoggedInUser loggedInUser,
            BiConsumer<Observation, LoggedInUser> addObservation) {
        var existingObservations = existing.stream().collect(Collectors.toMap(Observation::getId, ob -> ob));

        other.stream().forEach(ob -> {
            var existingObservation = existingObservations.getOrDefault(ob.getId(), null);
            if (existingObservation != null) {
                existingObservation.setData(
                        ob.getTitle(),
                        ob.getVotes(),
                        ob.isDeleted(),
                        loggedInUser
                );
            } else {
                addObservation.accept(ob, loggedInUser);
            }
        });
    }

    private static <T> List<T> unionLists(ImmutableList<T> existing, ImmutableList<T> other, Function<T, String> identifierFunc) {
        var existingMap = existing.stream().collect(Collectors.toMap(identifierFunc, item -> item));

        var resultList = new ArrayList<>(existingMap.values());
        other.stream().forEach(item -> {
            if (!existingMap.containsKey(identifierFunc.apply(item))) {
                resultList.add(item);
            }
        });

        return resultList;
    }

    private void createRetrospective(V1_0_ImportableRetrospective importable, ImportResult result, LoggedInUser loggedInUser, ImportSettings settings) {
        var retrospective = adaptToRetrospective(importable, loggedInUser);

        if (settings.applyChanges()) {
            retrospectiveService.restoreRetrospective(retrospective, loggedInUser);
        }
        result.addMessage(
                retrospective.getId(),
                settings.isRestoreData()
                        ? "Retrospective restored"
                        : "Retrospective created");
    }

    private Retrospective adaptToRetrospective(V1_0_ImportableRetrospective importable, LoggedInUser loggedInUser) {
        return new Retrospective(
                importable.getId(),
                importable.getReadableId(),
                importable.getPreviousRetrospectiveId(),
                new Audit(OffsetDateTime.now(), loggedInUser),
                adaptToActions(importable.getActions(), loggedInUser),
                adaptToObservations(importable.getObservations(), loggedInUser, Observation.WENT_WELL),
                adaptToObservations(importable.getObservations(), loggedInUser, Observation.COULD_BE_BETTER),
                adaptToUsers(importable.getAdministrators(), loggedInUser),
                adaptToUsers(importable.getMembers(), loggedInUser)
        );
    }

    private ImmutableList<User> adaptToUsers(List<String> users, LoggedInUser loggedInUser) {
        return new ImmutableList<>(
                users.stream().map(user -> getUserOrNotFound(loggedInUser, user))
        );
    }

    private ImmutableList<Observation> adaptToObservations(List<V1_0_ImportableObservation> observations, LoggedInUser loggedInUser, String observationType) {
        return new ImmutableList<>(
                observations.stream()
                        .filter(ob -> ob.getType().equals(observationType))
                        .map(ob -> new Observation(
                                ob.getId(),
                                ob.getTitle(),
                                new Audit(OffsetDateTime.now(), loggedInUser),
                                ob.getDeleted(),
                                ob.getVotes().stream().map(user -> getUserOrNotFound(loggedInUser, user)).collect(Collectors.toList())))
        );
    }

    private ImmutableList<Action> adaptToActions(List<V1_0_ImportableAction> actions, LoggedInUser loggedInUser) {
        return new ImmutableList<>(
                actions.stream().map(action -> new Action(
                        action.getId(),
                        action.getTitle(),
                        new Audit(OffsetDateTime.now(), loggedInUser),
                        action.isDeleted(),
                        action.getTicketAddress(),
                        action.getAssignedTo() != null
                                ? getUserOrNotFound(loggedInUser, action.getAssignedTo())
                                : null,
                        action.getFromActionId(),
                        action.getFromObservationId(),
                        action.getComplete()
                ))
        );
    }

    private User getUserOrNotFound(LoggedInUser loggedInUser, String username) {
        var user = userRepositority.getUser(loggedInUser, username);
        return user != null ? user : new NotFoundUser(username);
    }
}
