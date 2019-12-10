package serialisers;

import models.*;
import serialisable.SerialisableRetrospective;

import java.util.stream.Collectors;

public class RetrospectiveSerialiser {
    private final ActionSerialiser actionSerialiser;
    private final ObservationSerialiser observationSerialiser;
    private final AuditSerialiser auditSerialiser;
    private final UserSerialiser userSerialiser;

    public RetrospectiveSerialiser(ActionSerialiser actionSerialiser, ObservationSerialiser observationSerialiser, AuditSerialiser auditSerialiser, UserSerialiser userSerialiser) {
        this.actionSerialiser = actionSerialiser;
        this.observationSerialiser = observationSerialiser;
        this.auditSerialiser = auditSerialiser;
        this.userSerialiser = userSerialiser;
    }

    public SerialisableRetrospective serialise(Retrospective retrospective){
        var serialisable = new SerialisableRetrospective();

        serialisable.setId(retrospective.getId());
        serialisable.setAdministrators(retrospective.getAdministrators().stream().map(userSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setMembers(retrospective.getMembers().stream().map(userSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setActions(retrospective.getActions(true).stream().map(actionSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setCouldBeBetter(retrospective.getCouldBeBetter(true).stream().map(observationSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setWentWell(retrospective.getWentWell(true).stream().map(observationSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setAudit(auditSerialiser.serialise(retrospective.getAudit()));

        return serialisable;
    }

    public Retrospective deserialise(SerialisableRetrospective retrospective){
        return new Retrospective(
                retrospective.getId(),
                auditSerialiser.deserialise(retrospective.getAudit()),
                new ImmutableList<>(retrospective.getActions().stream().map(actionSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getWentWell().stream().map(observationSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getCouldBeBetter().stream().map(observationSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getAdministrators().stream().map(userSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getMembers().stream().map(userSerialiser::deserialise))
        );
    }
}
