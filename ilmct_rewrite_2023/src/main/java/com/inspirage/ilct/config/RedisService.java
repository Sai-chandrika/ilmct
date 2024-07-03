package com.inspirage.ilct.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Service("redisService")
public class RedisService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private ObjectMapper mapper;

    private final RedisTemplate<String, Object> template;

    public void setValue(String key, Object value) {
        template.opsForValue().set(key, value);
    }

    public Object getValue(String key) {
        return template.opsForValue().get(key);
    }

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.template = redisTemplate;
    }

    public synchronized Object getValue(final String key, Class clazz) {
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        Object obj = template.opsForValue().get(key);

        return mapper.convertValue(obj, clazz);
    }

    public void setValue(final String key, final Object value, final long expireTimeInMinutes, boolean marshal) {
        if (marshal) {
            template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        } else {
            template.setHashValueSerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
        }
        template.opsForValue().set(key, value, expireTimeInMinutes, TimeUnit.MINUTES);
    }

    public void deleteKey(String key) {
        template.delete(key);
    }

}
