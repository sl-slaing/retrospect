package com.example.retrospect.core.repositories;

public interface DataStorageFactory {
    <T> DataStorage<T> getStorage(Class<T> clazz);
}
