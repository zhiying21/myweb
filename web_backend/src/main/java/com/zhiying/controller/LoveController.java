package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.LoveConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/love")
@RequiredArgsConstructor
public class LoveController {

    private final LoveConfigService loveConfigService;

    private static final DateTimeFormatter[] PARSERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        return Result.ok(loveConfigService.getConfig());
    }

    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveConfig(@RequestBody Map<String, Object> body) {
        try {
            LocalDateTime startTime = parseStartTime(body.get("startTime"));
            String name1 = toStr(body.get("name1"), "TA");
            String name2 = toStr(body.get("name2"), "TA");
            String avatar1 = emptyToNull(toStr(body.get("avatar1"), null));
            String avatar2 = emptyToNull(toStr(body.get("avatar2"), null));
            loveConfigService.saveConfig(startTime, name1, name2, avatar1, avatar2);
            return Result.ok();
        } catch (Exception e) {
            log.error("恋爱配置保存失败", e);
            return Result.fail(500, e.getMessage() != null ? e.getMessage() : "保存失败");
        }
    }

    private LocalDateTime parseStartTime(Object val) {
        if (val == null) return LocalDateTime.now();
        String s = String.valueOf(val).replace("Z", "").trim();
        if (s.isBlank()) return LocalDateTime.now();
        for (DateTimeFormatter f : PARSERS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {}
        }
        return LocalDateTime.now();
    }

    private String toStr(Object o, String def) {
        if (o == null) return def;
        String s = o.toString().trim();
        return s.isEmpty() ? def : s;
    }

    private String emptyToNull(String s) {
        return s != null && !s.isBlank() ? s : null;
    }
}
