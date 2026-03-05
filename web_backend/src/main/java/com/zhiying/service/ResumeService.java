package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.ResumeEntity;
import com.zhiying.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeMapper resumeMapper;

    public String getContent() {
        ResumeEntity e = resumeMapper.selectOne(
                new LambdaQueryWrapper<ResumeEntity>().last("LIMIT 1"));
        if (e != null && e.getContent() != null && !e.getContent().isBlank()) {
            return e.getContent();
        }
        // fallback: read from classpath
        try {
            ClassPathResource res = new ClassPathResource("resume/resume.md");
            if (res.exists()) {
                return new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}
        return "# 简历\n\n简历内容暂未添加，请管理员在后台编辑。";
    }

    public void saveContent(String content) {
        ResumeEntity e = resumeMapper.selectOne(
                new LambdaQueryWrapper<ResumeEntity>().last("LIMIT 1"));
        if (e == null) {
            e = new ResumeEntity();
            e.setContent(content);
            resumeMapper.insert(e);
        } else {
            e.setContent(content);
            resumeMapper.updateById(e);
        }
    }
}
