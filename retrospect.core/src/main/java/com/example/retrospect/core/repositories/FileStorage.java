package com.example.retrospect.core.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileStorage<T> implements DataStorage<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;
    private final Function<Map<String, Object>, T> deserialiseValue;
    private final AtomicBoolean dataLoaded = new AtomicBoolean();
    private Map<String, T> data;

    public FileStorage(String filename, Function<Map<String, Object>, T> deserialiseValue) {
        this.deserialiseValue = deserialiseValue;
        var dataStoragePath = System.getProperty("DataStoragePath");
        file = Path.of(dataStoragePath).resolve(filename + ".json").toFile();
    }

    private Map<String, T> loadDataFromFile(){
        if (!file.exists()){
            return new HashMap<>();
        }

        try {
            Map<String, Map<String, Object>> mapOfMaps = objectMapper.readValue(file, HashMap.class);
            return mapOfMaps.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> deserialiseValue.apply(entry.getValue())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, T> getData(){
        if (dataLoaded.get()){
            return data;
        }

        data = loadDataFromFile();
        dataLoaded.set(true);
        return data;
    }

    private void saveData(){
        if (!dataLoaded.get()){
            return; //nothing changed
        }

        this.saveDataToFile(data);
    }

    private void updateData(Consumer<Map<String, T>> updateAction){
        updateAction.accept(getData());
        saveData();
    }

    private void saveDataToFile(Map<String, T> data){
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T getOne(String key){
        return getData().getOrDefault(key, null);
    }

    public void addOrUpdate(String key, T value){
        updateData(data -> data.put(key, value));
    }

    public Stream<T> getAll() {
        return getData().values().stream();
    }

    public void remove(String key){
        updateData(data -> data.remove(key));
    }
}
