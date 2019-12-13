package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.Observation;
import com.example.retrospect.core.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.serialisable.SerialisableObservation;

import java.util.stream.Collectors;

@Service
public class ObservationSerialiser {
    private final AuditSerialiser auditSerialiser;
    private final UserSerialiser userSerialiser;

    @Autowired
    public ObservationSerialiser(AuditSerialiser auditSerialiser, UserSerialiser userSerialiser) {
        this.auditSerialiser = auditSerialiser;
        this.userSerialiser = userSerialiser;
    }

    public SerialisableObservation serialise(Observation observation){
        var serialisable = new SerialisableObservation();
        serialisable.setId(observation.getId());
        serialisable.setTitle(observation.getTitle());
        serialisable.setVotes(observation.getVotes().stream().map(User::getUsername).collect(Collectors.toList()));
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
