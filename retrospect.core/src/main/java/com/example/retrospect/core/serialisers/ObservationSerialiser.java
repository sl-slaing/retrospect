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
    private final UserNameSerialiser userNameSerialiser;

    @Autowired
    public ObservationSerialiser(AuditSerialiser auditSerialiser, UserNameSerialiser userNameSerialiser) {
        this.auditSerialiser = auditSerialiser;
        this.userNameSerialiser = userNameSerialiser;
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
                observation.getVotes().stream().map(userNameSerialiser::deserialise).collect(Collectors.toList())
        );
    }
}
