package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhiying.entity.DocumentEntity;
import com.zhiying.entity.SiteConfigEntity;
import com.zhiying.mapper.DocumentMapper;
import com.zhiying.mapper.SiteConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;
    private final DocumentMapper documentMapper;

    public Optional<String> get(String key) {
        SiteConfigEntity e = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfigEntity>().eq(SiteConfigEntity::getConfigKey, key));
        return e != null ? Optional.ofNullable(e.getConfigValue()) : Optional.empty();
    }

    public void set(String key, String value) {
        SiteConfigEntity e = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfigEntity>().eq(SiteConfigEntity::getConfigKey, key));
        if (e == null) {
            e = new SiteConfigEntity();
            e.setConfigKey(key);
            e.setConfigValue(value);
            siteConfigMapper.insert(e);
        } else {
            siteConfigMapper.update(null, new LambdaUpdateWrapper<SiteConfigEntity>()
                    .eq(SiteConfigEntity::getConfigKey, key)
                    .set(SiteConfigEntity::getConfigValue, value));
        }
    }

    public long getSiteRunningHours() {
        return get("site_start_time")
                .map(s -> {
                    try {
                        s = s.replace("T", " ").trim();
                        LocalDateTime start = LocalDateTime.parse(s,
                                s.length() > 10 ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        : DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        return java.time.Duration.between(start, LocalDateTime.now()).toHours();
                    } catch (Exception ex) {
                        return 0L;
                    }
                })
                .orElse(0L);
    }

    public boolean verifyResumePassword(String password) {
        return get("resume_password").map(p -> p.equals(password)).orElse(false);
    }

    public boolean verifyLoveDiaryPassword(String password) {
        return get("love_diary_password").map(p -> p.equals(password)).orElse(false);
    }

    public long incrementVisitCount() {
        String val = get("visit_count").orElse("0");
        long count;
        try {
            count = Long.parseLong(val) + 1;
        } catch (NumberFormatException e) {
            count = 1;
        }
        set("visit_count", String.valueOf(count));
        return count;
    }

    public long getVisitCount() {
        return Long.parseLong(get("visit_count").orElse("0"));
    }

    public Map<String, Object> getSiteStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("runningHours", getSiteRunningHours());
        stats.put("visitCount", getVisitCount());
        long blogCount = documentMapper.selectCount(
                new LambdaQueryWrapper<DocumentEntity>().eq(DocumentEntity::getType, "blog"));
        long noteCount = documentMapper.selectCount(
                new LambdaQueryWrapper<DocumentEntity>().eq(DocumentEntity::getType, "notes"));
        stats.put("blogCount", blogCount);
        stats.put("noteCount", noteCount);
        return stats;
    }
}
