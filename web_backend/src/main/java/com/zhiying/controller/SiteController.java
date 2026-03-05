package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "网站配置", description = "网站信息、统计数据接口")
@RestController
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteController {

    private final SiteConfigService siteConfigService;

    @Operation(summary = "获取网站运行小时数")
    @GetMapping("/running-hours")
    public Result<Long> runningHours() {
        return Result.ok(siteConfigService.getSiteRunningHours());
    }

    @Operation(summary = "获取网站综合统计（运行时间/访问量/博客数/笔记数）")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok(siteConfigService.getSiteStats());
    }

    @Operation(summary = "记录一次首页访问并返回总访问量")
    @PostMapping("/visit")
    public Result<Long> visit() {
        return Result.ok(siteConfigService.incrementVisitCount());
    }

    @Operation(summary = "验证简历密码")
    @PostMapping("/resume/verify")
    public Result<Boolean> verifyResumePassword(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        return Result.ok(siteConfigService.verifyResumePassword(password != null ? password : ""));
    }

    @Operation(summary = "验证恋爱日记密码")
    @PostMapping("/love-diary/verify")
    public Result<Boolean> verifyLoveDiaryPassword(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        return Result.ok(siteConfigService.verifyLoveDiaryPassword(password != null ? password : ""));
    }
}
