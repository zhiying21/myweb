package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "简历", description = "简历内容的查看与管理接口")
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "获取简历Markdown内容（需要先验证密码）")
    @GetMapping("/content")
    public Result<String> content() {
        String c = resumeService.getContent();
        return Result.ok(c);
    }

    @Operation(summary = "管理员保存/更新简历内容")
    @PutMapping("/content")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveContent(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("内容不能为空");
        resumeService.saveContent(content);
        return Result.ok();
    }
}
