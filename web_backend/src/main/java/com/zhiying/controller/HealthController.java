package com.zhiying.controller;

import com.zhiying.common.result.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 健康检查与环境探测（用于准备阶段验证）
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    private final RedisTemplate<String, Object> redisTemplate;

    public HealthController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("application", "zhiying-web-backend");
        return Result.ok(info);
    }

    @GetMapping("/redis")
    public Result<String> redis() {
        String key = "health:ping";
        redisTemplate.opsForValue().set(key, "pong", 10, TimeUnit.SECONDS);
        Object value = redisTemplate.opsForValue().get(key);
        return Result.ok(value != null ? value.toString() : "fail");
    }
}
