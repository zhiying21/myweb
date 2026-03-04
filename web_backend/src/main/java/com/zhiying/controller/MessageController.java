package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(messageService.list());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, String> body,
                            @AuthenticationPrincipal Long userId) {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return Result.fail("留言内容不能为空");
        }
        messageService.addMessage(userId, content.trim());
        return Result.ok();
    }

    @PostMapping("/comment")
    public Result<Void> addComment(@RequestBody Map<String, Object> body,
                                   @AuthenticationPrincipal Long userId) {
        Long messageId = body.get("messageId") != null ? Long.valueOf(body.get("messageId").toString()) : null;
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        Long replyToId = body.get("replyToId") != null ? Long.valueOf(body.get("replyToId").toString()) : null;
        String content = body.get("content") != null ? body.get("content").toString() : "";
        if (messageId == null || content.isBlank()) {
            return Result.fail("留言ID和评论内容不能为空");
        }
        messageService.addComment(userId, messageId, parentId, replyToId, content.trim());
        return Result.ok();
    }

    @PostMapping("/comment/{commentId}/like")
    public Result<Void> toggleLike(@PathVariable Long commentId,
                                   @AuthenticationPrincipal Long userId) {
        messageService.toggleLike(userId, commentId);
        return Result.ok();
    }

    @GetMapping("/comment/{commentId}/liked")
    public Result<Boolean> hasLiked(@PathVariable Long commentId,
                                    @AuthenticationPrincipal Long userId) {
        return Result.ok(messageService.hasLiked(userId, commentId));
    }
}
