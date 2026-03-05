package com.zhiying.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.common.result.Result;
import com.zhiying.entity.ResourceEntity;
import com.zhiying.mapper.ResourceMapper;
import com.zhiying.service.ResumeService;
import com.zhiying.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "管理员", description = "管理员专用接口：工单管理、资源管理、简历管理")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    private final TicketService ticketService;
    private final ResourceMapper resourceMapper;
    private final ResumeService resumeService;

    @Operation(summary = "获取所有工单列表（含回复）")
    @GetMapping("/tickets")
    public Result<List<Map<String, Object>>> listTickets() {
        return Result.ok(ticketService.listAll());
    }

    @Operation(summary = "回复工单")
    @PostMapping("/tickets/{ticketId}/reply")
    public Result<Void> replyTicket(@PathVariable Long ticketId,
                                    @RequestBody Map<String, String> body,
                                    @AuthenticationPrincipal Long adminId) {
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("回复内容不能为空");
        ticketService.reply(ticketId, adminId, content.trim());
        return Result.ok();
    }

    @Operation(summary = "添加资源")
    @PostMapping("/resource")
    public Result<Long> addResource(@RequestBody Map<String, Object> body) {
        ResourceEntity r = new ResourceEntity();
        r.setName(body.get("name") != null ? body.get("name").toString() : "");
        r.setIcon(body.get("icon") != null ? body.get("icon").toString() : null);
        r.setLink(body.get("link") != null ? body.get("link").toString() : "");
        r.setSortOrder(body.get("sortOrder") != null ? Integer.parseInt(body.get("sortOrder").toString()) : 0);
        resourceMapper.insert(r);
        return Result.ok(r.getId());
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/resource/{id}")
    public Result<Void> deleteResource(@PathVariable Long id) {
        ResourceEntity r = resourceMapper.selectById(id);
        if (r == null) return Result.fail("资源不存在");
        resourceMapper.deleteById(id);
        return Result.ok();
    }

    @Operation(summary = "获取资源列表（管理员视图）")
    @GetMapping("/resources")
    public Result<List<ResourceEntity>> listResources() {
        return Result.ok(resourceMapper.selectList(
                new LambdaQueryWrapper<ResourceEntity>().orderByAsc(ResourceEntity::getSortOrder)));
    }

    @Operation(summary = "获取简历内容")
    @GetMapping("/resume")
    public Result<String> getResume() {
        return Result.ok(resumeService.getContent());
    }

    @Operation(summary = "保存简历内容")
    @PutMapping("/resume")
    public Result<Void> saveResume(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("内容不能为空");
        resumeService.saveContent(content);
        return Result.ok();
    }

    @Operation(summary = "上传文件（图片等）")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) return Result.fail("请选择文件");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) return Result.fail("文件名无效");
        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) ext = originalFilename.substring(i);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename).toFile());
            return Result.ok(contextPath + "/uploads/" + filename);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}
