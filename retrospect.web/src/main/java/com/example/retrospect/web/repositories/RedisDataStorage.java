package com.example.retrospect.web.repositories;

import com.example.retrospect.core.repositories.DataStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class RedisDataStorage<T> implements DataStorage<T> {
    private final Jedis jedis;
    private final String typeName;
    private final Function<Map<String, Object>, T> deserialiseValue;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisDataStorage(
            Jedis jedis,
            String typeName,
            Function<Map<String, Object>, T> deserialiseValue) {
        this.jedis = jedis;
        this.typeName = typeName;
        this.deserialiseValue = deserialiseValue;
    }

    private String getKey(String key){
        return typeName + "/" + key;
    }

    @Override
    public T getOne(String key) {
        var redisKey = getKey(key);
        return getFromRedis(redisKey);
    }

    private T getFromRedis(String redisKey) {
        var json = jedis.get(redisKey);

        if (json == null){
            return null;
        }

        try {
            Map<String, Object> map = objectMapper.readValue(json, HashMap.class);
            return deserialiseValue.apply(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOrUpdate(String key, T value) {
        try {
            if (value == null){
                throw new IllegalArgumentException("Value cannot be null");
            }

            var json = objectMapper.writeValueAsString(value);
            var redisKey = getKey(key);

            jedis.set(redisKey, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<T> getAll() {
        var keys = jedis.keys(typeName + "*");
        return keys.stream().map(this::getFromRedis);
    }

    @Override
    public void remove(String key) {
        var redisKey = getKey(key);

        jedis.del(redisKey);
    }
}
