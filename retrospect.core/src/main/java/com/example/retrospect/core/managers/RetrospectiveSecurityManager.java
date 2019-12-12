package com.example.retrospect.core.managers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.Retrospective;
import org.springframework.stereotype.Service;

@Service
public class RetrospectiveSecurityManager {
    public boolean canViewRetrospective(Retrospective retrospective, LoggedInUser user){
        var isAdministrator = retrospective.getAdministrators().stream().anyMatch(admin -> admin.equals(user));
        if (isAdministrator){
            return true;
        }

        return retrospective.getMembers().stream().anyMatch(member -> member.equals(user));
    }

    public boolean canEditRetrospective(Retrospective retrospective, LoggedInUser user) {
        var isAdministrator = retrospective.getAdministrators().stream().anyMatch(admin -> admin.equals(user));
        if (isAdministrator){
            return true;
        }

        return retrospective.getMembers().stream().anyMatch(member -> member.equals(user));
    }

    public boolean canAdministerRetrospective(Retrospective retrospective, LoggedInUser user) {
        return retrospective.getAdministrators().stream().anyMatch(admin -> admin.equals(user));
    }
}
