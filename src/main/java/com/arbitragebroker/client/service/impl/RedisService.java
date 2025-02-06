package com.arbitragebroker.client.service.impl;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void clearCache(String allKey) {
        try {
            String pattern = "client::" + allKey.replace("*", "") + "*";
            Set<byte[]> keys = new HashSet<>();

            redisTemplate.execute((RedisConnection connection) -> {
                try (var cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build())) {
                    cursor.forEachRemaining(keys::add);
                }
                if (!keys.isEmpty()) {
                    connection.del(keys.toArray(new byte[keys.size()][]));
                }
                return null;
            });
        } catch (Exception e) {
            System.err.println("Error clearing cache: " + e.getMessage());
        }
    }
}
