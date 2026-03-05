package com.zhiying.controller;

import com.zhiying.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "健康检查", description = "服务存活探测、Redis 连通性验证")
@RestController
@RequestMapping("/health")
public class HealthController {

    private final RedisTemplate<String, Object> redisTemplate;

    public HealthController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Operation(summary = "服务健康检查", description = "返回服务运行状态，无需登录")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("application", "zhiying-web-backend");
        return Result.ok(info);
    }

    @Operation(summary = "Redis 连通性测试", description = "向 Redis 写入并读取一个临时键值，无需登录")
    @GetMapping("/redis")
    public Result<String> redis() {
        String key = "health:ping";
        redisTemplate.opsForValue().set(key, "pong", 10, TimeUnit.SECONDS);
        Object value = redisTemplate.opsForValue().get(key);
        return Result.ok(value != null ? value.toString() : "fail");
    }
}
