package com.example.retrospect.web.managers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class OAuthUserSessionManager implements UserSessionManager {
    private final UserRepository repository;

    @Autowired
    public OAuthUserSessionManager(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoggedInUser getLoggedInUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)){
            return null;
        }

        var oAuthAuthentication = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Map<String, String>)oAuthAuthentication.getUserAuthentication().getDetails();
        var username = userDetails.get("login");
        var displayName = userDetails.get("name");
        var avatar = userDetails.get("avatar_url");

        var user = new User(username, displayName, avatar, "github");
        repository.addOrUpdateUserDetails(user);

        return new LoggedInUser(user);
    }
}
