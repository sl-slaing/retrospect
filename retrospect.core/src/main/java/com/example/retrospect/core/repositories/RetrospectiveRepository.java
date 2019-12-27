package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import com.example.retrospect.core.serialisers.RetrospectiveSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class RetrospectiveRepository {
    private final RetrospectiveSerialiser serialiser;
    private final DataStorage<SerialisableRetrospective> storage;

    @Autowired
    public RetrospectiveRepository(RetrospectiveSerialiser serialiser, DataStorageFactory storageFactory) {
        this.serialiser = serialiser;

        storage = storageFactory.getStorage(SerialisableRetrospective.class);
    }

    public Retrospective getRetrospective(String id){
        var serialisable = storage.getOne(id);
        return serialisable != null
                ? serialiser.deserialise(serialisable)
                : null;
    }

    public void addOrReplace(Retrospective retrospective){
        storage.addOrUpdate(retrospective.getId(), serialiser.serialise(retrospective));
    }

    public Stream<Retrospective> getAll() {
        return storage.getAll()
                .map(serialiser::deserialise);
    }

    public void remove(String retrospectiveId) {
        storage.remove(retrospectiveId);
    }
}
