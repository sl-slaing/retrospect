package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.serialisable.SerialisableAction;

@Service
public class ActionSerialiser {
    private final AuditSerialiser auditSerialiser;
    private final UserNameSerialiser userNameSerialiser;

    @Autowired
    public ActionSerialiser(AuditSerialiser auditSerialiser, UserNameSerialiser userNameSerialiser) {
        this.auditSerialiser = auditSerialiser;
        this.userNameSerialiser = userNameSerialiser;
    }

    public SerialisableAction serialise(Action action){
        var serialisable = new SerialisableAction();
        serialisable.setAssignedTo(userNameSerialiser.serialise(action.getAssignedTo()));
        serialisable.setAudit(auditSerialiser.serialise(action.getAudit()));
        serialisable.setId(action.getId());
        serialisable.setTicketAddress(action.getTicketAddress());
        serialisable.setTitle(action.getTitle());
        serialisable.setDeleted(action.isDeleted());
        serialisable.setFromActionId(action.getFromActionId());
        serialisable.setFromObservationId(action.getFromObservationId());
        serialisable.setComplete(action.getComplete());

        return serialisable;
    }

    public Action deserialise(SerialisableAction action){
        return new Action(
                action.getId(),
                action.getTitle(),
                auditSerialiser.deserialise(action.getAudit()),
                action.isDeleted(),
                action.getTicketAddress(),
                userNameSerialiser.deserialise(action.getAssignedTo()),
                action.getFromActionId(),
                action.getFromObservationId(),
                action.isComplete());
    }
}
