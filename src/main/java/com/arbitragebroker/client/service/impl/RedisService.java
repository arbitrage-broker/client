package com.arbitragebroker.client.service.impl;

import com.arbitragebroker.client.dto.PagedResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Set;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public <T> void savePage(String key, Page<T> page) {
        PagedResponse<T> pageResponse = PagedResponse.fromPage(page);
        redisTemplate.opsForValue().set(key, pageResponse);
    }

    public <T> Page<T> getPage(String key, Class<T> type) {
        Object data = redisTemplate.opsForValue().get(key);
        if (data instanceof LinkedHashMap) {
            // Convert LinkedHashMap to PagedResponse<T>
            PagedResponse<T> PagedResponse = objectMapper.convertValue(data, objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, type));
            return PagedResponse.toPage();
        }
        return Page.empty();
    }

    public <T> Page<T> getPage(String key, TypeReference<PagedResponse<T>> typeRef) {
        Object data = redisTemplate.opsForValue().get(key);

        if (data != null) {
            PagedResponse<T> pagedResponse = objectMapper.convertValue(data, typeRef);
            return pagedResponse.toPage();
        }
        return Page.empty();
    }

    public void clearCache(String allKey) {
        try {
            // Convert the pattern to Redis format
            String redisPattern = "client::" + allKey.replace("*", "") + "*";

            // Find all keys matching the pattern
            Set<String> keys = redisTemplate.keys(redisPattern);

            if (keys != null && !keys.isEmpty()) {
                // Delete all matching keys
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            // Log the error but don't throw it to prevent cache issues from breaking the application
            System.err.println("Error clearing cache: " + e.getMessage());
        }
    }
}
