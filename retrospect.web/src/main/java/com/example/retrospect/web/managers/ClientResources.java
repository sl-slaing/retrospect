package com.example.retrospect.web.managers;

import com.example.retrospect.core.models.User;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import java.util.Map;
import java.util.function.Function;

public class ClientResources {
    private final String name;
    private final Function<Map<String, String>, User> createUser;

    public ClientResources(String loginPathName, Function<Map<String, String>, User> createUser) {
        this.name = loginPathName;
        this.createUser = createUser;
    }

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }

    public String getLoginPath() {
        return "/login/" + name;
    }

    public User getUser(Map<String, String> userDetails) {
        return createUser.apply(userDetails);
    }

    public boolean exists(){
        return client != null && client.getId() != null;
    }
}
