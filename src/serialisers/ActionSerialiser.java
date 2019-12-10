package serialisers;

import models.Action;
import serialisable.SerialisableAction;

public class ActionSerialiser {
    private final AuditSerialiser auditSerialiser;
    private final UserSerialiser userSerialiser;

    public ActionSerialiser(AuditSerialiser auditSerialiser, UserSerialiser userSerialiser) {
        this.auditSerialiser = auditSerialiser;
        this.userSerialiser = userSerialiser;
    }

    public SerialisableAction serialise(Action action){
        SerialisableAction serialisable = new SerialisableAction();
        serialisable.setAssignedTo(userSerialiser.serialise(action.getAssignedTo()));
        serialisable.setAudit(auditSerialiser.serialise(action.getAudit()));
        serialisable.setId(action.getId());
        serialisable.setTicketAddress(action.getTicketAddress());
        serialisable.setTitle(action.getTitle());
        serialisable.setDeleted(action.isDeleted());

        return serialisable;
    }

    public Action deserialise(SerialisableAction action){
        return new Action(
                action.getId(),
                action.getTitle(),
                auditSerialiser.deserialise(action.getAudit()),
                action.isDeleted(),
                action.getTicketAddress(),
                userSerialiser.deserialise(action.getAssignedTo())
        );
    }
}
