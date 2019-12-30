package com.example.retrospect.core.repositories;

import java.util.stream.Stream;

public interface DataStorage<T> {
    T getOne(String key);
    void addOrUpdate(String key, T value);
    Stream<T> getAll();
    void remove(String key);
    void clear();
}
