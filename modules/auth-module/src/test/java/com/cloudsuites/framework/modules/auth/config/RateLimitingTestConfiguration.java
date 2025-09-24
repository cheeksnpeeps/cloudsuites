package com.cloudsuites.framework.modules.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Test configuration for Redis-based components including rate limiting service.
 * Provides necessary beans for integration testing with Redis.
 */
@TestConfiguration
public class RateLimitingTestConfiguration {

    /**
     * Creates a RedisTemplate for String keys and Object values.
     * Uses the existing rateLimitingRedisConnectionFactory from RateLimitingConfiguration.
     * Configured with proper serializers for test compatibility.
     *
     * @param connectionFactory the Redis connection factory (injected by qualifier)
     * @return configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("rateLimitingRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serialization for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serialization for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
