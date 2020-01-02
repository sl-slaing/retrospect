package com.example.retrospect.core.services;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.managers.ActionPermissionManager;
import com.example.retrospect.core.models.*;
import com.example.retrospect.core.repositories.TenantRepository;
import com.example.retrospect.core.repositories.UserRepository;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TenantService {
    private final TenantRepository repository;
    private final UserRepository userRepository;
    private final ActionPermissionManager actionPermissionManager;

    @Autowired
    public TenantService(TenantRepository repository, UserRepository userRepository, ActionPermissionManager actionPermissionManager) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.actionPermissionManager = actionPermissionManager;
    }

    public List<Tenant> getTenantsForLoggedInUser(User user) {
        var allTenants = this.repository.getAllTenants();
        return allTenants.stream().filter(t -> isRegistered(t, user) || isAdministrator(t, user)).collect(Collectors.toList());
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

        if (tenant.getAdministrators().isEmpty()) {
            throw new RuntimeException("Tenant must have at least one administrator");
        }

        if (tenant.getUsers().isEmpty()) {
            throw new RuntimeException("Tenant must have at least one user");
        }

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
            tenant.setName(details.getName(), loggedInUser, "Change name to " + details.getName());
        }
        setUsers(loggedInUser, tenant::getAdministrators, usrs -> tenant.setAdministrators(usrs, loggedInUser, "Add/remove administrator"), details.getAdministrators());
        setUsers(loggedInUser, tenant::getUsers, usrs -> tenant.setUsers(usrs, loggedInUser, "Add/remove user"), details.getUsers());

        if (tenant.getAdministrators().isEmpty()) {
            throw new RuntimeException("Tenant must have at least one administrator");
        }

        if (tenant.getUsers().isEmpty()) {
            throw new RuntimeException("Tenant must have at least one user");
        }

        repository.updateTenant(tenant);
        return tenant;
    }

    private void setUsers(LoggedInUser loggedInUser, Supplier<ImmutableList<User>> existingUsers, Consumer<ImmutableList<User>> setUsers, Set<String> requiredUsernames) {
        var requiredUsers = requiredUsernames.stream()
                .map(username -> {
                    var user = userRepository.getUser(loggedInUser, username);
                    return user != null ? user : new NotFoundUser(username);
                })
                .collect(Collectors.toMap(User::getUsername, u -> u));

        var currentUsers = existingUsers.get();
        var currentUserNames = currentUsers.stream().map(User::getUsername).collect(Collectors.toSet());

        var newUsers = currentUsers
                .except(user -> !requiredUsers.containsKey(user.getUsername()))
                .union(requiredUsers.values().stream().filter(u -> !currentUserNames.contains(u.getUsername())).collect(Collectors.toList()));

        setUsers.accept(newUsers);
    }

    private static boolean isRegistered(Tenant tenant, User user) {
        return tenant.getUsers().stream().anyMatch(u -> u.equals(user));
    }

    private boolean isAdministrator(Tenant tenant, User user) {
        var isSystemAdministrator = user instanceof LoggedInUser && actionPermissionManager.canAdministerTenants(user);
        if (isSystemAdministrator) {
            return true;
        }

        return tenant.getAdministrators().stream().anyMatch(u -> u.equals(user));
    }

    public void removeTenant(String id, LoggedInUser loggedInUser) {
        var tenant = this.repository.getTenant(id);
        if (tenant == null) {
            throw new NotFoundException("Tenant not found");
        }

        if (!isAdministrator(tenant, loggedInUser)) {
            throw new NotPermittedException("You're not permitted to edit this tenant");
        }

        this.repository.deleteTenant(id);
    }
}
