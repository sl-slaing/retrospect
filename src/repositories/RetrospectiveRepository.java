package repositories;

import models.Retrospective;
import serialisable.SerialisableRetrospective;
import serialisers.RetrospectiveSerialiser;

import java.util.HashMap;
import java.util.Map;

public class RetrospectiveRepository {
    private static final Map<String, SerialisableRetrospective> retrospectives = new HashMap<>();

    private final RetrospectiveSerialiser serialiser;

    public RetrospectiveRepository(RetrospectiveSerialiser serialiser) {
        this.serialiser = serialiser;
    }

    public Retrospective getRetrospective(String id){
        if (!retrospectives.containsKey(id)){
            return null;
        }

        SerialisableRetrospective retrospective = retrospectives.get(id);
        return serialiser.deserialise(retrospective);
    }

    public void addOrReplace(Retrospective retrospective){
        SerialisableRetrospective serialisable = serialiser.serialise(retrospective);

        retrospectives.put(serialisable.getId(), serialisable);
    }
}
