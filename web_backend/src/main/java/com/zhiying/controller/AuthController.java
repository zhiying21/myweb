package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.AuthService;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    @Value("${upload.path:./uploads}")
    private String uploadPath;

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Result.fail("邮箱和密码不能为空");
        }
        return Result.ok(authService.register(email.trim(), password));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return Result.fail("邮箱和密码不能为空");
        }
        return Result.ok(authService.login(email.trim(), password));
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(@AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return Result.fail("未登录");
        }
        return Result.ok(authService.getCurrentUser((Long) principal));
    }

    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body,
                                                     @AuthenticationPrincipal Long userId) {
        if (userId == null) return Result.fail("未登录");
        String nickname = body.get("nickname");
        String avatar = body.get("avatar");
        authService.updateProfile(userId, nickname, avatar);
        return Result.ok(authService.getCurrentUser(userId));
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
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
