package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteController {

    private final SiteConfigService siteConfigService;

    @GetMapping("/running-minutes")
    public Result<Long> runningMinutes() {
        return Result.ok(siteConfigService.getSiteRunningMinutes());
    }

    @PostMapping("/resume/verify")
    public Result<Boolean> verifyResumePassword(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        return Result.ok(siteConfigService.verifyResumePassword(password != null ? password : ""));
    }
}
