package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Tag(name = "认证模块", description = "用户注册、登录、个人信息管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    @Value("${upload.path:./uploads}")
    private String uploadPath;

    private final AuthService authService;

    @Operation(summary = "用户注册", description = "使用邮箱和密码注册新账号，注册成功后自动返回 JWT Token")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Result.fail("邮箱和密码不能为空");
        }
        return Result.ok(authService.register(email.trim(), password));
    }

    @Operation(summary = "用户登录", description = "使用邮箱和密码登录，返回 JWT Token 及用户信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Result.fail("邮箱和密码不能为空");
        }
        return Result.ok(authService.login(email.trim(), password));
    }

    @Operation(summary = "获取当前登录用户信息", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return Result.fail("未登录");
        }
        return Result.ok(authService.getCurrentUser((Long) principal));
    }

    @Operation(summary = "更新个人资料（昵称/头像）", security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body,
                                                     @AuthenticationPrincipal Long userId) {
        if (userId == null) return Result.fail("未登录");
        String nickname = body.get("nickname");
        String avatar = body.get("avatar");
        authService.updateProfile(userId, nickname, avatar);
        return Result.ok(authService.getCurrentUser(userId));
    }

    @Operation(summary = "上传头像/文件", description = "上传图片文件，返回访问 URL",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/upload")
    public Result<String> upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
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
