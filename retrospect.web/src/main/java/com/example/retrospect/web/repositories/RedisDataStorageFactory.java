package com.example.retrospect.web.repositories;

import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.DataStorage;
import com.example.retrospect.core.repositories.DataStorageFactory;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Service
@Primary
public class RedisDataStorageFactory implements DataStorageFactory {
    private final Jedis jedis;

    public RedisDataStorageFactory(Jedis jedis) {
        this.jedis = jedis;
    }

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
        return new RedisDataStorage<>(
                jedis,
                "retrospectives",
                SerialisableRetrospective::deserialiseFromMap);
    }

    private DataStorage<User> getUserDataStorage() {
        return new RedisDataStorage<>(
                jedis,
                "users",
                RedisDataStorageFactory::deserialiseUser);
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
