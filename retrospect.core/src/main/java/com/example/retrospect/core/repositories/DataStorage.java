package com.example.retrospect.core.repositories;

import java.util.stream.Stream;

public interface DataStorage<T> {
    T getOne(String tenantId, String key);
    void addOrUpdate(String tenantId, String key, T value);
    Stream<T> getAll(String tenantId);
    void remove(String tenantId, String key);
    void clear(String tenantId);
}
