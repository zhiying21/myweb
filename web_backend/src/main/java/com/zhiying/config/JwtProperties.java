package com.zhiying.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "zhiying-web-secret-key-change-in-production-2024";
    private long expirationMs = 7 * 24 * 60 * 60 * 1000L; // 7 days
}
