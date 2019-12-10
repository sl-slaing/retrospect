package serialisers;

import models.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialisable.SerialisableAudit;

@Service
public class AuditSerialiser {
    private final UserSerialiser userSerialiser;

    @Autowired
    public AuditSerialiser(UserSerialiser userSerialiser) {
        this.userSerialiser = userSerialiser;
    }

    public SerialisableAudit serialise(Audit audit){
        var serialisable = new SerialisableAudit();
        serialisable.setCreatedOn(audit.getCreatedOn());
        serialisable.setCreatedBy(audit.getCreatedBy().getId());
        serialisable.setLastUpdatedOn(audit.getLastUpdatedOn());
        serialisable.setLastUpdatedBy(audit.getLastUpdatedBy().getId());
        serialisable.setLastChange(audit.getLastChange());

        return serialisable;
    }

    public Audit deserialise(SerialisableAudit audit){
        return new Audit(
                audit.getCreatedOn(),
                userSerialiser.deserialise(audit.getCreatedBy()),
                audit.getLastUpdatedOn(),
                userSerialiser.deserialise(audit.getLastUpdatedBy()),
                audit.getLastChange()
        );
    }
}
