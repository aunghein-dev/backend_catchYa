// src/main/java/.../presence/RedisHealthController.java
package com.catch_ya_group.catch_ya.controller.presence;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/presence")
@RequiredArgsConstructor
@Hidden
public class RedisHealthController {
    private final StringRedisTemplate redis;

    @GetMapping("/redis-health")
    public String health() {
        try {
            redis.opsForValue().increment("presence:health:probe");
            return "REDIS OK";
        } catch (Exception e) {
            return "REDIS ERR: " + e.getMessage();
        }
    }
}
