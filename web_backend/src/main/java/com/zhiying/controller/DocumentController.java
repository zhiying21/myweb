package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.entity.UserEntity;
import com.zhiying.mapper.UserMapper;
import com.zhiying.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "文档管理", description = "博客/笔记的增删改查、点赞、浏览、评论接口")
@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserMapper userMapper;

    @Operation(summary = "获取文档列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) String type) {
        return Result.ok(documentService.list(type));
    }

    @Operation(summary = "获取文档详情（同时增加浏览量）")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> doc = documentService.getByIdAndIncrementView(id);
        return doc != null ? Result.ok(doc) : Result.fail("文档不存在");
    }

    @Operation(summary = "上传文档（需要登录）")
    @PostMapping("/upload")
    public Result<Long> upload(@RequestBody Map<String, String> body,
                               @AuthenticationPrincipal Long userId) {
        String type = body.get("type");
        String title = body.get("title");
        String description = body.get("description");
        String content = body.get("content");
        String coverImage = body.get("coverImage");
        if (title == null || title.isBlank()) return Result.fail("标题不能为空");
        if (content == null || content.isBlank()) return Result.fail("内容不能为空");
        Long id = documentService.upload(userId, type, title, description, content, coverImage);
        return Result.ok(id);
    }

    @Operation(summary = "删除文档（作者或管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @AuthenticationPrincipal Long userId) {
        UserEntity u = userMapper.selectById(userId);
        String role = u != null ? u.getRole() : "USER";
        documentService.delete(id, userId, role);
        return Result.ok();
    }

    @Operation(summary = "切换点赞状态")
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> like(@PathVariable Long id,
                                            @AuthenticationPrincipal Long userId) {
        return Result.ok(documentService.toggleLike(id, userId));
    }

    @Operation(summary = "查询是否已点赞")
    @GetMapping("/{id}/liked")
    public Result<Boolean> hasLiked(@PathVariable Long id,
                                    @AuthenticationPrincipal Long userId) {
        return Result.ok(documentService.hasLiked(id, userId));
    }

    @Operation(summary = "获取文档评论列表")
    @GetMapping("/{id}/comments")
    public Result<List<Map<String, Object>>> getComments(@PathVariable Long id) {
        return Result.ok(documentService.getComments(id));
    }

    @Operation(summary = "添加评论（需要登录）")
    @PostMapping("/{id}/comments")
    public Result<Void> addComment(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   @AuthenticationPrincipal Long userId) {
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("评论内容不能为空");
        documentService.addComment(id, userId, content.trim());
        return Result.ok();
    }

    @Operation(summary = "删除评论（作者或管理员）")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId,
                                      @AuthenticationPrincipal Long userId) {
        UserEntity u = userMapper.selectById(userId);
        String role = u != null ? u.getRole() : "USER";
        documentService.deleteComment(commentId, userId, role);
        return Result.ok();
    }
}
