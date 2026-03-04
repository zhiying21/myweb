package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.entity.ResourceEntity;
import com.zhiying.mapper.ResourceMapper;
import com.zhiying.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @GetMapping("/tickets")
    public Result<List<Map<String, Object>>> listTickets() {
        return Result.ok(ticketService.listAll());
    }

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

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择文件");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return Result.fail("文件名无效");
        }
        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            ext = originalFilename.substring(i);
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            String url = contextPath + "/uploads/" + filename;
            return Result.ok(url);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}
