package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.RedisService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisServiceImpl implements RedisService {

    private final ValueCommands<String, String> valueCommands;
    private final KeyCommands<String> keyCommands;

    public RedisServiceImpl(RedisDataSource ds) {
        this.valueCommands = ds.value(String.class);
        this.keyCommands = ds.key();
    }

    @Override
    public String get(String key) {
        return valueCommands.get(key);
    }

    @Override
    public void set(String key, String value) {
        valueCommands.set(key, value);
    }

    @Override
    public void set(String key, String value, long seconds) {
        valueCommands.setex(key, seconds, value);
    }

    @Override
    public void delete(String key) {
        keyCommands.del(key);
    }

    @Override
    public void expire(String key, long seconds) {
        keyCommands.expire(key, seconds);
    }
}
