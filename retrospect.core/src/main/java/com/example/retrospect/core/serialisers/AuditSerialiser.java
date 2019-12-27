package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.serialisable.SerialisableAudit;

import java.time.OffsetDateTime;

@Service
public class AuditSerialiser {
    private final UserNameSerialiser userNameSerialiser;

    @Autowired
    public AuditSerialiser(UserNameSerialiser userNameSerialiser) {
        this.userNameSerialiser = userNameSerialiser;
    }

    public SerialisableAudit serialise(Audit audit){
        var serialisable = new SerialisableAudit();
        serialisable.setCreatedOn(audit.getCreatedOn().toString());
        serialisable.setCreatedBy(audit.getCreatedBy().getUsername());
        serialisable.setLastUpdatedOn(audit.getLastUpdatedOn().toString());
        serialisable.setLastUpdatedBy(audit.getLastUpdatedBy().getUsername());
        serialisable.setLastChange(audit.getLastChange());

        return serialisable;
    }

    public Audit deserialise(SerialisableAudit audit){
        return new Audit(
                OffsetDateTime.parse(audit.getCreatedOn()),
                userNameSerialiser.deserialise(audit.getCreatedBy()),
                OffsetDateTime.parse(audit.getLastUpdatedOn()),
                userNameSerialiser.deserialise(audit.getLastUpdatedBy()),
                audit.getLastChange()
        );
    }
}
