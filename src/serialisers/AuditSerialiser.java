package serialisers;

import models.Audit;
import serialisable.SerialisableAudit;

public class AuditSerialiser {
    private final UserSerialiser userSerialiser;

    public AuditSerialiser(UserSerialiser userSerialiser) {
        this.userSerialiser = userSerialiser;
    }

    public SerialisableAudit serialise(Audit audit){
        SerialisableAudit serialisable = new SerialisableAudit();
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
