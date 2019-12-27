package com.example.retrospect.core.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class PersistenceRepository<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicBoolean dataLoaded = new AtomicBoolean();

    private Map<String, T> data;

    protected abstract Map<String, T> loadData();
    protected abstract void saveData(Map<String, T> data);
    protected abstract T deserialiseValue(Map<String, Object> map);

    protected Map<String, T> getData(){
        if (dataLoaded.get()){
            return data;
        }

        data = loadData();
        dataLoaded.set(true);
        return data;
    }

    protected void saveData(){
        if (!dataLoaded.get()){
            return; //nothing changed
        }

        this.saveData(data);
    }

    protected void updateData(Consumer<Map<String, T>> updateAction){
        updateAction.accept(getData());
        saveData();
    }

    protected Map<String, T> loadDataFromFile(FileStorage storage){
        if (!storage.canRead()){
            return new HashMap<>();
        }

        try {
            Map<String, Map<String, Object>> mapOfMaps = objectMapper.readValue(storage.openRead(), HashMap.class);
            return mapOfMaps.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> deserialiseValue(entry.getValue())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveDataToFile(FileStorage storage, Map<String, T> data){
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storage.openWrite(), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
