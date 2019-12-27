package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;

import java.util.stream.Collectors;

@Service
public class RetrospectiveSerialiser {
    private final ActionSerialiser actionSerialiser;
    private final ObservationSerialiser observationSerialiser;
    private final AuditSerialiser auditSerialiser;
    private final UserNameSerialiser userNameSerialiser;

    @Autowired
    public RetrospectiveSerialiser(ActionSerialiser actionSerialiser, ObservationSerialiser observationSerialiser, AuditSerialiser auditSerialiser, UserNameSerialiser userNameSerialiser) {
        this.actionSerialiser = actionSerialiser;
        this.observationSerialiser = observationSerialiser;
        this.auditSerialiser = auditSerialiser;
        this.userNameSerialiser = userNameSerialiser;
    }

    public SerialisableRetrospective serialise(Retrospective retrospective){
        var serialisable = new SerialisableRetrospective();

        serialisable.setId(retrospective.getId());
        serialisable.setReadableId(retrospective.getReadableId());
        serialisable.setPreviousRetrospectiveId(retrospective.getPreviousRetrospectiveId());
        serialisable.setAdministrators(retrospective.getAdministrators().stream().map(userNameSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setMembers(retrospective.getMembers().stream().map(userNameSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setActions(retrospective.getActions(true).stream().map(actionSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setCouldBeBetter(retrospective.getCouldBeBetter(true).stream().map(observationSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setWentWell(retrospective.getWentWell(true).stream().map(observationSerialiser::serialise).collect(Collectors.toList()));
        serialisable.setAudit(auditSerialiser.serialise(retrospective.getAudit()));

        return serialisable;
    }

    public Retrospective deserialise(SerialisableRetrospective retrospective){
        return new Retrospective(
                retrospective.getId(),
                retrospective.getReadableId(),
                retrospective.getPreviousRetrospectiveId(),
                auditSerialiser.deserialise(retrospective.getAudit()),
                new ImmutableList<>(retrospective.getActions().stream().map(actionSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getWentWell().stream().map(observationSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getCouldBeBetter().stream().map(observationSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getAdministrators().stream().map(userNameSerialiser::deserialise)),
                new ImmutableList<>(retrospective.getMembers().stream().map(userNameSerialiser::deserialise))
        );
    }
}
