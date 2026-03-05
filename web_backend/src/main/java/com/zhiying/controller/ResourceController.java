package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "资源分享模块", description = "软件资源下载链接列表")
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "获取资源列表", description = "返回所有资源（名称、图标、下载链接），无需登录")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(resourceService.list());
    }
}
