package com.example.retrospect.web.services.import_export.v1_0;

import com.example.retrospect.core.models.Action;
import com.example.retrospect.core.models.Observation;
import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.models.User;
import com.example.retrospect.web.models.import_export.ExportSettings;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableRetrospective;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableAction;
import com.example.retrospect.web.models.import_export.v1_0.V1_0_ImportableObservation;
import com.example.retrospect.web.services.import_export.ImportableRetrospective;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class V1_0ImportExportAdapter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ImportableRetrospective adaptRetrospective(String json){
        try {
            return mapper.readValue(json, V1_0_ImportableRetrospective.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String exportRetrospective(Retrospective retrospective, ExportSettings settings) {
        var importable = new V1_0_ImportableRetrospective();
        importable.setId(retrospective.getId());
        importable.setReadableId(retrospective.getReadableId());
        importable.setPreviousRetrospectiveId(retrospective.getPreviousRetrospectiveId());
        importable.setMembers(retrospective.getMembers().stream().map(User::getUsername).collect(Collectors.toList()));
        importable.setAdministrators(retrospective.getAdministrators().stream().map(User::getUsername).collect(Collectors.toList()));
        importable.setActions(retrospective.getActions(settings.isIncludeDeleted()).stream().map(V1_0ImportExportAdapter::exportAction).collect(Collectors.toList()));

        var observations = Stream.concat(
                retrospective.getWentWell(settings.isIncludeDeleted()).stream().map(V1_0ImportExportAdapter::exportWentWell),
                retrospective.getCouldBeBetter(settings.isIncludeDeleted()).stream().map(V1_0ImportExportAdapter::exportCouldBeBetter));
        importable.setObservations(observations.collect(Collectors.toList()));

        return serialise(importable);
    }

    private static String serialise(V1_0_ImportableRetrospective importable) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(importable);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static V1_0_ImportableObservation exportCouldBeBetter(Observation observation) {
        return exportObservation(observation, Observation.COULD_BE_BETTER);
    }

    private static V1_0_ImportableObservation exportWentWell(Observation observation) {
        return exportObservation(observation, Observation.WENT_WELL);
    }

    private static V1_0_ImportableObservation exportObservation(Observation observation, String type) {
        var importable = new V1_0_ImportableObservation();
        importable.setId(observation.getId());
        importable.setTitle(observation.getTitle());
        importable.setVotes(observation.getVotes().stream().map(User::getUsername).collect(Collectors.toList()));
        importable.setType(type);
        importable.setDeleted(observation.isDeleted());

        return importable;
    }

    private static V1_0_ImportableAction exportAction(Action action) {
        var importable = new V1_0_ImportableAction();
        importable.setId(action.getId());
        importable.setAssignedTo(action.getAssignedTo() != null ? action.getAssignedTo().getUsername() : null);
        importable.setComplete(action.getComplete());
        importable.setFromActionId(action.getFromActionId());
        importable.setFromObservationId(action.getFromObservationId());
        importable.setTitle(action.getTitle());
        importable.setTicketAddress(action.getTicketAddress());
        importable.setDeleted(action.isDeleted());

        return importable;
    }
}
