package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.ResourceEntity;
import com.zhiying.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceMapper resourceMapper;

    public List<Map<String, Object>> list() {
        return resourceMapper.selectList(
                new LambdaQueryWrapper<ResourceEntity>().orderByAsc(ResourceEntity::getSortOrder))
                .stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", r.getId());
                    m.put("name", r.getName());
                    m.put("icon", r.getIcon());
                    m.put("link", r.getLink());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
