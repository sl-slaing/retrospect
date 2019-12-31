package com.example.retrospect.web.managers;

import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.UserRepository;
import com.example.retrospect.core.services.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class OAuthUserSessionManager implements UserSessionManager {
    private final UserRepository repository;
    private final ClientResourcesManager clientResourcesManager;
    private final HttpServletRequest request;
    private final TenantService tenantService;

    @Autowired
    public OAuthUserSessionManager(
            UserRepository repository,
            ClientResourcesManager clientResourcesManager,
            HttpServletRequest request,
            TenantService tenantService) {
        this.repository = repository;
        this.clientResourcesManager = clientResourcesManager;
        this.request = request;
        this.tenantService = tenantService;
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
        var tenantId = getTenantId(user);
        repository.addOrUpdateUserDetails(tenantId, user);

        return new LoggedInUser(user, tenantId);
    }

    private String getTenantId(User user) {
        var tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.equals("")){
            return null;
        }

        var tenants = tenantService.getTenantsForLoggedInUser(user);
        var tenantValid = tenants.stream().anyMatch(t -> t.getId().equals(tenantId));
        if (tenantValid) {
            return tenantId;
        }

        throw new NotPermittedException("Tenant not accessible");
    }

    private ClientResources getProvider(OAuth2Authentication oAuthAuthentication) {
        var request = oAuthAuthentication.getOAuth2Request();
        var clientId = request.getClientId();
        return clientResourcesManager.getClientResource(clientId);
    }
}
