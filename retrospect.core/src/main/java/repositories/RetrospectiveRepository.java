package repositories;

import models.Retrospective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialisable.SerialisableRetrospective;
import serialisers.RetrospectiveSerialiser;

import java.util.HashMap;
import java.util.Map;

@Service
public class RetrospectiveRepository {
    private static final Map<String, SerialisableRetrospective> retrospectives = new HashMap<>();

    private final RetrospectiveSerialiser serialiser;

    @Autowired
    public RetrospectiveRepository(RetrospectiveSerialiser serialiser) {
        this.serialiser = serialiser;
    }

    public Retrospective getRetrospective(String id){
        if (!retrospectives.containsKey(id)){
            return null;
        }

        var retrospective = retrospectives.get(id);
        return serialiser.deserialise(retrospective);
    }

    public void addOrReplace(Retrospective retrospective){
        var serialisable = serialiser.serialise(retrospective);

        retrospectives.put(serialisable.getId(), serialisable);
    }
}
