// src/main/java/com/catch_ya_group/catch_ya/config/RedisStartupVerifier.java
package com.catch_ya_group.catch_ya.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisStartupVerifier {

    @Bean(name = "redisFailFastRunner")
    @ConditionalOnProperty(name = "app.redis.required", havingValue = "true", matchIfMissing = false)
    ApplicationRunner redisFailFastRunner(RedisConnectionFactory cf) {
        return args -> {
            try (RedisConnection conn = cf.getConnection()) {
                String pong = conn.ping();
                if (pong == null || !"PONG".equalsIgnoreCase(pong)) {
                    throw new IllegalStateException("Redis PING failed: " + pong);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Redis is not available at startup.", e);
            }
        };
    }
}
