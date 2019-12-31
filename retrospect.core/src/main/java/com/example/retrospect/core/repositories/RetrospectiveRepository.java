package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.NotFoundUser;
import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import com.example.retrospect.core.serialisers.RetrospectiveSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RetrospectiveRepository {
    public static final LoggedInUser NO_TENANT = new LoggedInUser(
            new NotFoundUser("NO_TENANT"),
            "NO_TENANT");

    private final RetrospectiveSerialiser serialiser;
    private final DataStorage<SerialisableRetrospective> storage;

    @Autowired
    public RetrospectiveRepository(RetrospectiveSerialiser serialiser, DataStorageFactory storageFactory) {
        this.serialiser = serialiser;

        storage = storageFactory.getStorage(SerialisableRetrospective.class);
    }

    public Retrospective getRetrospective(LoggedInUser loggedInUser, String id){
        var serialisable = storage.getOne(loggedInUser.getTenantId(), id);
        return serialisable != null
                ? serialiser.deserialise(serialisable, loggedInUser)
                : null;
    }

    public void addOrReplace(LoggedInUser loggedInUser, Retrospective retrospective){
        storage.addOrUpdate(loggedInUser.getTenantId(), retrospective.getId(), serialiser.serialise(retrospective));
    }

    public Stream<Retrospective> getAll(LoggedInUser loggedInUser) {
        return storage.getAll(loggedInUser.getTenantId())
                .map(retrospective -> serialiser.deserialise(retrospective, loggedInUser));
    }

    public void remove(LoggedInUser loggedInUser, String retrospectiveId) {
        storage.remove(loggedInUser.getTenantId(), retrospectiveId);
    }

    public Set<Retrospective> removeAll(LoggedInUser loggedInUser) {
        var existing = getAll(loggedInUser).collect(Collectors.toSet());
        storage.clear(loggedInUser.getTenantId());
        return existing;
    }
}
