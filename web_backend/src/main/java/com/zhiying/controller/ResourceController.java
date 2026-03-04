package com.zhiying.controller;

import com.zhiying.common.result.Result;
import com.zhiying.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(resourceService.list());
    }
}
