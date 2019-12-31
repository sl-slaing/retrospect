package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.*;
import com.example.retrospect.core.serialisable.SerialisableTenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TenantSerialiser {
    private final UserNameSerialiser userNameSerialiser;
    private final AuditSerialiser auditSerialiser;

    @Autowired
    public TenantSerialiser(UserNameSerialiser userNameSerialiser, AuditSerialiser auditSerialiser) {
        this.userNameSerialiser = userNameSerialiser;
        this.auditSerialiser = auditSerialiser;
    }

    public SerialisableTenant serialise(Tenant tenant) {
        var serialisable = new SerialisableTenant();
        serialisable.setId(tenant.getId());
        serialisable.setName(tenant.getName());
        serialisable.setState(tenant.getState().name());
        serialisable.setUsers(
                tenant.getUsers().stream().map(userNameSerialiser::serialise).collect(Collectors.toList())
        );
        serialisable.setAdministrators(
                tenant.getAdministrators().stream().map(userNameSerialiser::serialise).collect(Collectors.toList())
        );
        serialisable.setAudit(auditSerialiser.serialise(tenant.getAudit()));

        return serialisable;
    }

    public Tenant deserialise(SerialisableTenant serialisable) {
        var userForThisTennant = new LoggedInUser(
                new User("", "", "", ""),
                serialisable.getId());

        return new Tenant(
                serialisable.getId(),
                serialisable.getName(),
                new ImmutableList<>(serialisable.getUsers().stream().map(user -> userNameSerialiser.deserialise(userForThisTennant, user))),
                new ImmutableList<>(serialisable.getAdministrators().stream().map(user -> userNameSerialiser.deserialise(userForThisTennant, user))),
                TenantState.valueOf(serialisable.getState()),
                auditSerialiser.deserialise(userForThisTennant, serialisable.getAudit())
        );
    }
}
