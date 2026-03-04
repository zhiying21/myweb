package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.SiteConfigEntity;
import com.zhiying.mapper.SiteConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    public Optional<String> get(String key) {
        SiteConfigEntity e = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfigEntity>().eq(SiteConfigEntity::getConfigKey, key));
        return e != null ? Optional.ofNullable(e.getConfigValue()) : Optional.empty();
    }

    public long getSiteRunningMinutes() {
        return get("site_start_time")
                .map(s -> {
                    try {
                        s = s.replace("T", " ").trim();
                        LocalDateTime start = LocalDateTime.parse(s,
                                s.length() > 10 ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        : DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        return java.time.Duration.between(start, LocalDateTime.now()).toMinutes();
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .orElse(0L);
    }

    public boolean verifyResumePassword(String password) {
        return get("resume_password").map(p -> p.equals(password)).orElse(false);
    }
}
