package com.example.retrospect.web.repositories;

import com.example.retrospect.core.repositories.DataStorage;
import com.example.retrospect.core.repositories.DataStorageFactory;
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
        var typeName = clazz.getSimpleName().toLowerCase();
        if (typeName.startsWith("serialisable")){
            typeName = typeName.replaceFirst("serialisable", "");
        }

        return new RedisDataStorage<>(
                jedis,
                pluralise(typeName),
                clazz
        );
    }

    private static String pluralise(String typeName) {
        return typeName + "s";
    }
}
