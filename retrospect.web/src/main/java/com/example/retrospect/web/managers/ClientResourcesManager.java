package com.example.retrospect.web.managers;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ClientResourcesManager {
    private final List<ClientResources> clientResources = new ArrayList<>();

    public ClientResources add(ClientResources clientResource) {
        clientResources.add(clientResource);
        return clientResource;
    }

    public Stream<ClientResources> getAllClientResources(){
        return clientResources.stream();
    }

    public ClientResources getClientResource(String clientId){
        return getAllClientResources()
                .filter(cr -> cr.getClient().getClientId().equals(clientId))
                .findFirst()
                .orElse(null);
    }
}
