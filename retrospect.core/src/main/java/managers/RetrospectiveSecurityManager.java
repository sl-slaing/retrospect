package managers;

import models.LoggedInUser;
import models.Retrospective;

public class RetrospectiveSecurityManager {
    public boolean canViewRetrospective(Retrospective retrospective, LoggedInUser user){
        var isAdministrator = retrospective.getAdministrators().stream().anyMatch(id -> id.getId().equals(user.getId()));
        if (isAdministrator){
            return true;
        }

        return retrospective.getMembers().stream().anyMatch(id -> id.getId().equals(user.getId()));
    }

    public boolean canEditRetrospective(Retrospective retrospective, LoggedInUser user) {
        var isAdministrator = retrospective.getAdministrators().stream().anyMatch(id -> id.getId().equals(user.getId()));
        if (isAdministrator){
            return true;
        }

        return retrospective.getMembers().stream().anyMatch(id -> id.getId().equals(user.getId()));
    }

    public boolean canAdministerRetrospective(Retrospective retrospective, LoggedInUser user) {
        return retrospective.getAdministrators().stream().anyMatch(id -> id.getId().equals(user.getId()));
    }
}
