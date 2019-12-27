package com.example.retrospect.web.repositories;

import com.example.retrospect.core.repositories.DataStorage;
import com.example.retrospect.core.repositories.DataStorageFactory;
import com.example.retrospect.core.serialisable.SerialisableRetrospective;
import com.example.retrospect.core.serialisable.SerialisableUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@Primary
public class RedisDataStorageFactory implements DataStorageFactory {
    private final Jedis jedis;

    @Autowired
    public RedisDataStorageFactory(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public <T> DataStorage<T> getStorage(Class<T> clazz) {
        if (clazz.equals(SerialisableUser.class)) {
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
                SerialisableRetrospective.class);
    }

    private DataStorage<SerialisableUser> getUserDataStorage() {
        return new RedisDataStorage<>(
                jedis,
                "users",
                SerialisableUser.class);
    }
}
