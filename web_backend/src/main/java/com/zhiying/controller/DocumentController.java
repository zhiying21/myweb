package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) String type) {
        return Result.ok(documentService.list(type));
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> doc = documentService.getById(id);
        return doc != null ? Result.ok(doc) : Result.fail("文档不存在");
    }

    @PostMapping("/upload")
    public Result<Long> upload(@RequestBody Map<String, String> body,
                               @AuthenticationPrincipal Long userId) {
        String type = body.get("type");
        String title = body.get("title");
        String description = body.get("description");
        String content = body.get("content");
        String coverImage = body.get("coverImage");
        if (title == null || title.isBlank()) {
            return Result.fail("标题不能为空");
        }
        if (content == null || content.isBlank()) {
            return Result.fail("内容不能为空");
        }
        Long id = documentService.upload(userId, type, title, description, content, coverImage);
        return Result.ok(id);
    }
}
