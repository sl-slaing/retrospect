package com.example.retrospect.core.managers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActionPermissionManager {
    private final Set<String> administrators;
    private final Set<String> backupOperators;
    private final Set<String> dataManagers;

    public ActionPermissionManager() {
        this.administrators = getUsers("Administrators");
        this.backupOperators = getUsers("BackupOperators");
        this.dataManagers = getUsers("DataManagers");
    }

    private static Set<String> getUsers(String propertyName) {
        var property = System.getProperty(propertyName);
        return property != null
                ? Arrays.stream(property.split(",")).collect(Collectors.toSet())
                : Collections.emptySet();
    }

    public boolean canRestore(LoggedInUser user) {
        return backupOperators.contains(user.getUsername())
                || administrators.contains(user.getUsername());
    }

    public boolean canExport(LoggedInUser user) {
        return backupOperators.contains(user.getUsername())
                || administrators.contains(user.getUsername());
    }

    public boolean canImport(LoggedInUser user) {
        return dataManagers.contains(user.getUsername())
                || backupOperators.contains(user.getUsername())
                || administrators.contains(user.getUsername());
    }

    public boolean canAdministerTenants(User user) {
        return administrators.contains(user.getUsername());
    }
}
