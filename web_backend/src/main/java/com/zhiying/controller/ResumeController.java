package com.zhiying.controller;

import com.zhiying.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    @GetMapping("/content")
    public Result<String> content() {
        try {
            ClassPathResource res = new ClassPathResource("resume/resume.md");
            if (res.exists()) {
                return Result.ok(new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // fallback
        }
        return Result.fail("简历内容暂不可用");
    }
}
