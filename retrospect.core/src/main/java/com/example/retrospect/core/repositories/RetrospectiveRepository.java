package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.serialisable.SerialisableAction;
import com.example.retrospect.core.serialisable.SerialisableAudit;
import com.example.retrospect.core.serialisable.SerialisableObservation;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import com.example.retrospect.core.serialisers.RetrospectiveSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RetrospectiveRepository extends PersistenceRepository<SerialisableRetrospective> {
    private final RetrospectiveSerialiser serialiser;
    private final DataStorage file;

    @Autowired
    public RetrospectiveRepository(RetrospectiveSerialiser serialiser) {
        this.serialiser = serialiser;

        file = new FileStorage("retrospectives.json");
    }

    @Override
    protected Map<String, SerialisableRetrospective> loadData() {
        return loadDataFromFile(file);
    }

    @Override
    protected SerialisableRetrospective deserialiseValue(Map<String, Object> map) {
        var retro = new SerialisableRetrospective();
        retro.setId((String)map.get("id"));
        retro.setReadableId((String)map.get("readableId"));
        retro.setPreviousRetrospectiveId((String)map.get("previousRetrospectiveId"));
        retro.setAdministrators((List<String>)map.get("administrators"));
        retro.setMembers((List<String>)map.get("members"));
        retro.setActions(deserialiseActions((List<Map>)map.get("actions")));
        retro.setWentWell(deserialiseObservations((List<Map>)map.get("wentWell")));
        retro.setCouldBeBetter(deserialiseObservations((List<Map>)map.get("couldBeBetter")));
        retro.setAudit(deserialiseAudit((Map<String, Object>)map.get("audit")));

        return retro;
    }

    private static SerialisableAudit deserialiseAudit(Map<String, Object> auditData) {
        var audit = new SerialisableAudit();
        audit.setCreatedBy((String)auditData.get("createdBy"));
        audit.setLastUpdatedBy((String)auditData.get("lastUpdatedBy"));
        audit.setLastChange((String)auditData.get("lastChange"));
        audit.setCreatedOn((String)auditData.get("createdOn"));
        audit.setLastUpdatedOn((String)auditData.get("lastUpdatedOn"));

        return audit;
    }

    private static List<SerialisableAction> deserialiseActions(List<Map> actions) {
        return actions.stream()
                .map(actionData -> {
                    var action = new SerialisableAction();
                    action.setId((String)actionData.get("id"));
                    action.setTitle((String)actionData.get("title"));
                    action.setTicketAddress((String)actionData.get("ticketAddress"));
                    action.setAssignedTo((String)actionData.get("assignedTo"));
                    action.setAudit(deserialiseAudit((Map<String, Object>)actionData.get("audit")));
                    action.setDeleted((boolean)actionData.get("deleted"));
                    action.setFromActionId((String)actionData.get("fromActionId"));
                    action.setFromObservationId((String)actionData.get("fromObservationId"));
                    action.setComplete((boolean)actionData.get("complete"));

                    return action;
                })
                .collect(Collectors.toList());
    }

    private static List<SerialisableObservation> deserialiseObservations(List<Map> observations) {
        return observations.stream()
                .map(observationData -> {
                    var observation = new SerialisableObservation();
                    observation.setId((String)observationData.get("id"));
                    observation.setTitle((String)observationData.get("title"));
                    observation.setAudit(deserialiseAudit((Map<String, Object>)observationData.get("audit")));
                    observation.setVotes((List<String>)observationData.get("votes"));
                    observation.setDeleted((boolean)observationData.get("deleted"));

                    return observation;
                })
                .collect(Collectors.toList());
    }

    @Override
    protected void saveData(Map<String, SerialisableRetrospective> data) {
        saveDataToFile(file, data);
    }

    public Retrospective getRetrospective(String id){
        if (!getData().containsKey(id)){
            return null;
        }

        var retrospective = getData().get(id);
        return serialiser.deserialise(retrospective);
    }

    public void addOrReplace(Retrospective retrospective){
        updateData(data ->
                data.put(
                        retrospective.getId(),
                        serialiser.serialise(retrospective)));
    }

    public Stream<Retrospective> getAll() {
        return getData().values()
                .stream()
                .map(serialiser::deserialise);
    }

    public void remove(String retrospectiveId) {
        updateData(data -> data.remove(retrospectiveId));
    }
}
