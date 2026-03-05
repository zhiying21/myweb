package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.entity.UserEntity;
import com.zhiying.mapper.UserMapper;
import com.zhiying.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "留言板", description = "留言发布、评论、点赞、删除接口")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserMapper userMapper;

    @Operation(summary = "获取留言列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(messageService.list());
    }

    @Operation(summary = "发布留言（需要登录）")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, String> body,
                            @AuthenticationPrincipal Long userId) {
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("留言内容不能为空");
        messageService.addMessage(userId, content.trim());
        return Result.ok();
    }

    @Operation(summary = "删除留言（作者或管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @AuthenticationPrincipal Long userId) {
        UserEntity u = userMapper.selectById(userId);
        String role = u != null ? u.getRole() : "USER";
        messageService.deleteMessage(id, userId, role);
        return Result.ok();
    }

    @Operation(summary = "切换留言点赞")
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleMessageLike(@PathVariable Long id,
                                                         @AuthenticationPrincipal Long userId) {
        return Result.ok(messageService.toggleMessageLike(userId, id));
    }

    @Operation(summary = "查询是否已点赞留言")
    @GetMapping("/{id}/liked")
    public Result<Boolean> hasLikedMessage(@PathVariable Long id,
                                           @AuthenticationPrincipal Long userId) {
        return Result.ok(messageService.hasLikedMessage(userId, id));
    }

    @Operation(summary = "添加评论（需要登录）")
    @PostMapping("/comment")
    public Result<Void> addComment(@RequestBody Map<String, Object> body,
                                   @AuthenticationPrincipal Long userId) {
        Long messageId = body.get("messageId") != null ? Long.valueOf(body.get("messageId").toString()) : null;
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        Long replyToId = body.get("replyToId") != null ? Long.valueOf(body.get("replyToId").toString()) : null;
        String content = body.get("content") != null ? body.get("content").toString() : "";
        if (messageId == null || content.isBlank()) return Result.fail("留言ID和评论内容不能为空");
        messageService.addComment(userId, messageId, parentId, replyToId, content.trim());
        return Result.ok();
    }

    @Operation(summary = "删除评论（作者或管理员）")
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId,
                                      @AuthenticationPrincipal Long userId) {
        UserEntity u = userMapper.selectById(userId);
        String role = u != null ? u.getRole() : "USER";
        messageService.deleteComment(commentId, userId, role);
        return Result.ok();
    }

    @Operation(summary = "切换评论点赞")
    @PostMapping("/comment/{commentId}/like")
    public Result<Void> toggleLike(@PathVariable Long commentId,
                                   @AuthenticationPrincipal Long userId) {
        messageService.toggleLike(userId, commentId);
        return Result.ok();
    }

    @Operation(summary = "查询是否已点赞评论")
    @GetMapping("/comment/{commentId}/liked")
    public Result<Boolean> hasLiked(@PathVariable Long commentId,
                                    @AuthenticationPrincipal Long userId) {
        return Result.ok(messageService.hasLiked(userId, commentId));
    }
}
