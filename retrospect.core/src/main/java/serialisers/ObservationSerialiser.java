package serialisers;

import models.Observation;
import models.User;
import serialisable.SerialisableObservation;

import java.util.stream.Collectors;

public class ObservationSerialiser {
    private final AuditSerialiser auditSerialiser;
    private final UserSerialiser userSerialiser;

    public ObservationSerialiser(AuditSerialiser auditSerialiser, UserSerialiser userSerialiser) {
        this.auditSerialiser = auditSerialiser;
        this.userSerialiser = userSerialiser;
    }

    public SerialisableObservation serialise(Observation observation){
        var serialisable = new SerialisableObservation();
        serialisable.setId(observation.getId());
        serialisable.setTitle(observation.getTitle());
        serialisable.setVotes(observation.getVotes().stream().map(User::getId).collect(Collectors.toList()));
        serialisable.setDeleted(observation.isDeleted());
        serialisable.setAudit(auditSerialiser.serialise(observation.getAudit()));

        return serialisable;
    }

    public Observation deserialise(SerialisableObservation observation){
        return new Observation(
                observation.getId(),
                observation.getTitle(),
                auditSerialiser.deserialise(observation.getAudit()),
                observation.isDeleted(),
                observation.getVotes().stream().map(userSerialiser::deserialise).collect(Collectors.toList())
        );
    }
}
