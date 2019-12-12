package com.example.retrospect.web.managers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class OAuthUserSessionManager implements UserSessionManager {
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
        var emailAddress = userDetails.get("email");
        var avatar = userDetails.get("avatar_url");

        return new LoggedInUser(new User(username, displayName, emailAddress, avatar));
    }
}
