package com.catch_ya_group.catch_ya.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisPing {
    private final StringRedisTemplate redis;

    @Bean
    ApplicationRunner pingRedisOnBoot() {
        return args -> {
            try {
                var conn = redis.getConnectionFactory().getConnection();
                String pong = conn.ping();
                log.info("Redis PING = {}", pong); // expect "PONG"
            } catch (Exception e) {
                log.warn("Redis unreachable: {}", e.toString());
            }
        };
    }
}
