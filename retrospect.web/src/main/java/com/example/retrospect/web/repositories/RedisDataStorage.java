package com.example.retrospect.web.repositories;

import com.example.retrospect.core.repositories.DataStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

import java.util.stream.Stream;

public class RedisDataStorage<T> implements DataStorage<T> {
    private final Jedis jedis;
    private final String typeName;
    private final Class<T> clazz;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisDataStorage(
            Jedis jedis,
            String typeName,
            Class<T> clazz) {
        this.jedis = jedis;
        this.typeName = typeName;
        this.clazz = clazz;
    }

    private String getKey(String tenantId, String id) {
        return tenantId + "/" + typeName + "/" + id;
    }

    @Override
    public T getOne(String tenantId, String key) {
        var redisKey = getKey(tenantId, key);
        return getFromRedis(redisKey);
    }

    private T getFromRedis(String redisKey) {
        var json = jedis.get(redisKey);

        if (json == null){
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOrUpdate(String tenantId, String key, T value) {
        try {
            if (value == null){
                throw new IllegalArgumentException("Value cannot be null");
            }

            var json = objectMapper.writeValueAsString(value);

            jedis.set(getKey(tenantId, key), json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<T> getAll(String tenantId) {
        var keys = jedis.keys(getKey(tenantId, "*"));
        return keys.stream().map(this::getFromRedis);
    }

    @Override
    public void remove(String tenantId, String key) {
        jedis.del(getKey(tenantId, key));
    }

    @Override
    public void clear(String tenantId) {
        var keys = jedis.keys(getKey(tenantId, "*"));
        keys.forEach(jedis::del);
    }
}
