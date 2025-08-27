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
            int maxAttempts = 10;      // try up to 10 times
            long delayMs = 2000;       // wait 2s between attempts
            Exception lastError = null;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try (RedisConnection conn = cf.getConnection()) {
                    System.out.println(">>> Attempt " + attempt + ": Redis connection info: " + conn.getNativeConnection());
                    String pong = conn.ping();
                    if ("PONG".equalsIgnoreCase(pong)) {
                        System.out.println("✅ Redis responded with PONG");
                        return; // success → stop runner
                    } else {
                        System.err.println("⚠️ Redis PING failed: " + pong);
                    }
                } catch (Exception e) {
                    lastError = e;
                    System.err.println("⚠️ Attempt " + attempt + " failed: " + e.getMessage());
                }

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Redis startup check interrupted", ie);
                }
            }

            // if we reach here → all attempts failed
            throw new IllegalStateException("❌ Redis is not available after " + maxAttempts + " attempts.", lastError);
        };
    }
}
