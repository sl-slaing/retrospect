package com.example.retrospect.core.services;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantService {
    private final TenantRepository repository;

    @Autowired
    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    public List<Tenant> getTenantsForLoggedInUser(User user) {
        var allTenants = this.repository.getAllTenants();
        return allTenants.stream().filter(t -> isRegistered(t, user)).collect(Collectors.toList());
    }

    public void addUserToTenant(String tenantId, User user, LoggedInUser loggedInUser) {
        var tenant = this.repository.getTenant(tenantId);
        if (tenant == null) {
            throw new NotFoundException("Tenant not found");
        }

        if (!isAdministrator(tenant, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to edit this tenant");
        }

        if (isRegistered(tenant, user)) {
            return; //already registered
        }

        tenant.setUsers(tenant.getUsers().concat(user), loggedInUser, "Add " + user.getUsername());

        repository.updateTenant(tenant);
    }

    public void removeUserFromTenant(String tenantId, User user, LoggedInUser loggedInUser) {
        var tenant = this.repository.getTenant(tenantId);
        if (tenant == null) {
            throw new NotFoundException("Tenant not found");
        }

        if (!isAdministrator(tenant, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to edit this tenant");
        }

        if (!isRegistered(tenant, user)) {
            return; //already registered
        }

        tenant.setUsers(tenant.getUsers().except(user), loggedInUser, "Remove " + user.getUsername());

        repository.updateTenant(tenant);
    }

    public Tenant addTenant(String name, TenantState state, LoggedInUser loggedInUser) {
        var tenant = new Tenant(
                Guid.next(),
                name,
                new ImmutableList<>(Collections.singletonList(loggedInUser)),
                new ImmutableList<>(Collections.singletonList(loggedInUser)),
                state,
                new Audit(OffsetDateTime.now(), loggedInUser));

        this.repository.add(tenant);
        return tenant;
    }

    public void setTenantState(String tenantId, TenantState state, LoggedInUser loggedInUser) {
        var tenant = this.repository.getTenant(tenantId);
        if (tenant == null) {
            throw new NotFoundException("Tenant not found");
        }

        if (!isAdministrator(tenant, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to edit this tenant");
        }

        tenant.setState(state, loggedInUser, "Update state to " + state.name());

        repository.updateTenant(tenant);
    }

    public Tenant updateTenant(TenantDetails details, LoggedInUser loggedInUser) {
        var tenant = this.repository.getTenant(details.getId());
        if (tenant == null) {
            throw new NotFoundException("Tenant not found");
        }

        if (!isAdministrator(tenant, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to edit this tenant");
        }

        if (!tenant.getState().equals(details.getState())) {
            tenant.setState(details.getState(), loggedInUser, "Change state to " + details.getState().name());
        }
        if (!tenant.getName().equals(details.getName())) {
            tenant.setName(details.getName(), loggedInUser, "Change state to " + details.getState().name());
        }

        repository.updateTenant(tenant);
        return tenant;
    }

    private static boolean isRegistered(Tenant tenant, User user) {
        return tenant.getUsers().stream().anyMatch(u -> u.equals(user));
    }

    private static boolean isAdministrator(Tenant tenant, User user) {
        return tenant.getAdministrators().stream().anyMatch(u -> u.equals(user));
    }
}
