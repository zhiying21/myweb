package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "工单", description = "联系我/工单提交接口")
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "提交工单")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody Map<String, String> body,
                               @AuthenticationPrincipal Long userId) {
        String email = body.get("email");
        String subject = body.get("subject");
        String content = body.get("content");
        if (email == null || email.isBlank()) return Result.fail("邮箱不能为空");
        if (subject == null || subject.isBlank()) return Result.fail("主题不能为空");
        if (content == null || content.isBlank()) return Result.fail("内容不能为空");
        ticketService.create(userId, email.trim(), subject.trim(), content.trim());
        return Result.ok();
    }

    @Operation(summary = "获取当前用户的工单列表（需要登录）")
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myTickets(@AuthenticationPrincipal Long userId) {
        return Result.ok(ticketService.listByUserId(userId));
    }
}
