package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.DocumentEntity;
import com.zhiying.entity.UserEntity;
import com.zhiying.mapper.DocumentMapper;
import com.zhiying.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final UserMapper userMapper;

    public List<Map<String, Object>> list(String type) {
        LambdaQueryWrapper<DocumentEntity> q = new LambdaQueryWrapper<DocumentEntity>()
                .orderByDesc(DocumentEntity::getCreateTime);
        if (type != null && !type.isBlank()) {
            q.eq(DocumentEntity::getType, type);
        }
        return documentMapper.selectList(q).stream().map(this::toMap).collect(Collectors.toList());
    }

    public Map<String, Object> getById(Long id) {
        DocumentEntity e = documentMapper.selectById(id);
        return e != null ? toMap(e) : null;
    }

    public Long upload(Long userId, String type, String title, String description, String content, String coverImage) {
        UserEntity user = userMapper.selectById(userId);
        String avatarUrl = user != null && user.getAvatar() != null ? user.getAvatar() : null;
        DocumentEntity d = new DocumentEntity();
        d.setType(type != null ? type : "notes");
        d.setTitle(title);
        d.setDescription(description != null ? description : "");
        d.setContent(content != null ? content : "");
        d.setCoverImage((coverImage != null && !coverImage.isBlank()) ? coverImage : avatarUrl);
        d.setUserId(userId);
        documentMapper.insert(d);
        return d.getId();
    }

    private Map<String, Object> toMap(DocumentEntity e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("type", e.getType());
        m.put("title", e.getTitle());
        m.put("description", e.getDescription());
        m.put("content", e.getContent());
        UserEntity publisher = e.getUserId() != null ? userMapper.selectById(e.getUserId()) : null;
        String avatar = e.getCoverImage() != null ? e.getCoverImage() : (publisher != null ? publisher.getAvatar() : null);
        m.put("coverImage", avatar);
        m.put("publisherNickname", publisher != null && publisher.getNickname() != null ? publisher.getNickname() : "匿名");
        m.put("publisherAvatar", publisher != null ? publisher.getAvatar() : null);
        m.put("createTime", e.getCreateTime());
        return m;
    }
}
