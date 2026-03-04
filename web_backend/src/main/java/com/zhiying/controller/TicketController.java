package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create")
    public Result<Void> create(@RequestBody Map<String, String> body,
                              @AuthenticationPrincipal Object principal) {
        String email = body.get("email");
        String subject = body.get("subject");
        String content = body.get("content");
        if (email == null || subject == null || content == null ||
                email.isBlank() || subject.isBlank() || content.isBlank()) {
            return Result.fail("邮箱、主题和内容不能为空");
        }
        Long userId = principal instanceof Long ? (Long) principal : null;
        ticketService.create(userId, email.trim(), subject.trim(), content.trim());
        return Result.ok();
    }
}
