package com.cloudsuites.framework.modules.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jakarta.annotation.PostConstruct;

/**
 * Configuration for Redis-based rate limiting.
 * Provides Redis connection and rate limiting service configuration.
 * 
 * @author CloudSuites Development Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "cloudsuites.rate-limiting.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitingConfiguration {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:1}")
    private int redisDatabase;

    @Value("${cloudsuites.rate-limiting.redis.enabled:true}")
    private boolean redisEnabled;

    /**
     * Redis connection factory for rate limiting.
     * Uses database 1 to separate from other Redis usage.
     */
    @Bean
    @ConditionalOnProperty(name = "cloudsuites.rate-limiting.redis.enabled", havingValue = "true", matchIfMissing = true)
    public RedisConnectionFactory rateLimitingRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setDatabase(redisDatabase);
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(config);
    }

    /**
     * Redis template for rate limiting operations.
     * Configured with String serializers for keys and values.
     */
    @Bean
    @ConditionalOnProperty(name = "cloudsuites.rate-limiting.redis.enabled", havingValue = "true", matchIfMissing = true)
    public RedisTemplate<String, String> rateLimitingRedisTemplate(RedisConnectionFactory rateLimitingRedisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(rateLimitingRedisConnectionFactory);
        
        // Use String serializers for both keys and values
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Initialize default rate limiting configurations after bean creation.
     */
    @PostConstruct
    public void initializeRateLimiting() {
        // This will be called after all beans are created
        // The actual initialization is done in RedisRateLimitServiceImpl
    }
}
