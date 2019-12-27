package com.example.retrospect.web.repositories;

import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.DataStorage;
import com.example.retrospect.core.repositories.FileStorage;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataStorageFactory implements com.example.retrospect.core.repositories.DataStorageFactory {
    @Override
    public <T> DataStorage<T> getStorage(Class<T> clazz) {
        if (clazz.equals(User.class)) {
            return (DataStorage<T>)getUserDataStorage();
        }

        if (clazz.equals(SerialisableRetrospective.class)) {
            return (DataStorage<T>)getRetrospectiveDataStorage();
        }

        throw new RuntimeException("Unknown data type");
    }

    private DataStorage<SerialisableRetrospective> getRetrospectiveDataStorage() {
        return new FileStorage<>("retrospectives", SerialisableRetrospective::deserialiseFromMap);
    }

    private DataStorage<User> getUserDataStorage() {
        return new FileStorage<>("users", DataStorageFactory::deserialiseUser);
    }

    private static User deserialiseUser(Map<String, Object> map) {
        return new User(
                (String)map.get("username"),
                (String)map.get("displayName"),
                (String)map.get("avatarUrl"),
                (String)map.get("provider")
        );
    }
}
