package com.example.retrospect.web.managers;

import com.example.retrospect.core.models.User;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import java.util.Map;
import java.util.function.BiFunction;

public class ClientResources {
    private final BiFunction<Map<String, String>, String, User> createUser;

    public ClientResources(BiFunction<Map<String, String>, String, User> createUser) {
        this.createUser = createUser;
    }

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    @NestedConfigurationProperty
    private NameConfiguration name = new NameConfiguration();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }

    public NameConfiguration getName(){
        return name;
    }

    public String getLoginPath() {
        return "/login/" + name.getProvider();
    }

    public String getDisplayName() {
        return name.getDisplay();
    }

    public User getUser(Map<String, String> userDetails) {
        return createUser.apply(userDetails, getDisplayName());
    }

    public boolean exists(){
        return client != null && client.getClientId() != null;
    }
}
