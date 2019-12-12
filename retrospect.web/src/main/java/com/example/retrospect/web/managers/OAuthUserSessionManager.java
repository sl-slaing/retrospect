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
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class OAuthUserSessionManager implements UserSessionManager {
    private final UserRepository repository;
    private final ClientResourcesManager clientResourcesManager;

    @Autowired
    public OAuthUserSessionManager(
            UserRepository repository,
            ClientResourcesManager clientResourcesManager) {
        this.repository = repository;
        this.clientResourcesManager = clientResourcesManager;
    }

    @Override
    public LoggedInUser getLoggedInUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)){
            return null;
        }

        var oAuthAuthentication = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Map<String, String>)oAuthAuthentication.getUserAuthentication().getDetails();
        var provider = getProvider(oAuthAuthentication);

        var user = provider.getUser(userDetails);
        repository.addOrUpdateUserDetails(user);

        return new LoggedInUser(user);
    }

    private ClientResources getProvider(OAuth2Authentication oAuthAuthentication) {
        var request = oAuthAuthentication.getOAuth2Request();
        var clientId = request.getClientId();
        return clientResourcesManager.getClientResource(clientId);
    }
}
