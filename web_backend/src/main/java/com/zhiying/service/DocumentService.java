package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhiying.common.exception.BusinessException;
import com.zhiying.entity.*;
import com.zhiying.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final UserMapper userMapper;
    private final DocumentLikeMapper documentLikeMapper;
    private final DocumentCommentMapper documentCommentMapper;

    public List<Map<String, Object>> list(String type) {
        LambdaQueryWrapper<DocumentEntity> q = new LambdaQueryWrapper<DocumentEntity>()
                .orderByDesc(DocumentEntity::getCreateTime);
        if (type != null && !type.isBlank()) {
            q.eq(DocumentEntity::getType, type);
        }
        return documentMapper.selectList(q).stream().map(e -> toListMap(e)).collect(Collectors.toList());
    }

    public Map<String, Object> getById(Long id) {
        DocumentEntity e = documentMapper.selectById(id);
        return e != null ? toDetailMap(e) : null;
    }

    public Map<String, Object> getByIdAndIncrementView(Long id) {
        DocumentEntity e = documentMapper.selectById(id);
        if (e == null) return null;
        int viewCount = (e.getViewCount() != null ? e.getViewCount() : 0) + 1;
        documentMapper.update(null, new LambdaUpdateWrapper<DocumentEntity>()
                .eq(DocumentEntity::getId, id)
                .set(DocumentEntity::getViewCount, viewCount));
        e.setViewCount(viewCount);
        return toDetailMap(e);
    }

    public Long upload(Long userId, String type, String title, String description, String content, String coverImage) {
        DocumentEntity d = new DocumentEntity();
        d.setType(type != null ? type : "notes");
        d.setTitle(title);
        d.setDescription(description != null ? description : "");
        d.setContent(content != null ? content : "");
        d.setCoverImage((coverImage != null && !coverImage.isBlank()) ? coverImage : null);
        d.setUserId(userId);
        d.setLikeCount(0);
        d.setViewCount(0);
        documentMapper.insert(d);
        return d.getId();
    }

    public void delete(Long id, Long userId, String role) {
        DocumentEntity d = documentMapper.selectById(id);
        if (d == null) throw new BusinessException("文档不存在");
        boolean isAdmin = "ADMIN".equals(role);
        boolean isAuthor = d.getUserId() != null && d.getUserId().equals(userId);
        if (!isAdmin && !isAuthor) throw new BusinessException("无权限删除");
        documentMapper.deleteById(id);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long documentId, Long userId) {
        DocumentEntity doc = documentMapper.selectById(documentId);
        if (doc == null) throw new BusinessException("文档不存在");

        DocumentLikeEntity existing = documentLikeMapper.selectOne(
                new LambdaQueryWrapper<DocumentLikeEntity>()
                        .eq(DocumentLikeEntity::getDocumentId, documentId)
                        .eq(DocumentLikeEntity::getUserId, userId));

        boolean liked;
        int likeCount = doc.getLikeCount() != null ? doc.getLikeCount() : 0;
        if (existing != null) {
            documentLikeMapper.deleteById(existing.getId());
            likeCount = Math.max(0, likeCount - 1);
            liked = false;
        } else {
            DocumentLikeEntity like = new DocumentLikeEntity();
            like.setDocumentId(documentId);
            like.setUserId(userId);
            documentLikeMapper.insert(like);
            likeCount++;
            liked = true;
        }
        documentMapper.update(null, new LambdaUpdateWrapper<DocumentEntity>()
                .eq(DocumentEntity::getId, documentId)
                .set(DocumentEntity::getLikeCount, likeCount));

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return result;
    }

    public boolean hasLiked(Long documentId, Long userId) {
        if (userId == null) return false;
        return documentLikeMapper.selectOne(
                new LambdaQueryWrapper<DocumentLikeEntity>()
                        .eq(DocumentLikeEntity::getDocumentId, documentId)
                        .eq(DocumentLikeEntity::getUserId, userId)) != null;
    }

    public List<Map<String, Object>> getComments(Long documentId) {
        List<DocumentCommentEntity> comments = documentCommentMapper.selectList(
                new LambdaQueryWrapper<DocumentCommentEntity>()
                        .eq(DocumentCommentEntity::getDocumentId, documentId)
                        .orderByAsc(DocumentCommentEntity::getCreateTime));
        return comments.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("content", c.getContent());
            m.put("createTime", c.getCreateTime());
            UserEntity u = userMapper.selectById(c.getUserId());
            m.put("userNickname", u != null && u.getNickname() != null ? u.getNickname() : "匿名");
            m.put("userAvatar", u != null ? u.getAvatar() : null);
            m.put("userId", c.getUserId());
            return m;
        }).collect(Collectors.toList());
    }

    public void addComment(Long documentId, Long userId, String content) {
        DocumentEntity doc = documentMapper.selectById(documentId);
        if (doc == null) throw new BusinessException("文档不存在");
        DocumentCommentEntity c = new DocumentCommentEntity();
        c.setDocumentId(documentId);
        c.setUserId(userId);
        c.setContent(content);
        c.setLikeCount(0);
        documentCommentMapper.insert(c);
    }

    public void deleteComment(Long commentId, Long userId, String role) {
        DocumentCommentEntity c = documentCommentMapper.selectById(commentId);
        if (c == null) throw new BusinessException("评论不存在");
        boolean isAdmin = "ADMIN".equals(role);
        boolean isAuthor = c.getUserId() != null && c.getUserId().equals(userId);
        if (!isAdmin && !isAuthor) throw new BusinessException("无权限删除");
        documentCommentMapper.deleteById(commentId);
    }

    private Map<String, Object> toListMap(DocumentEntity e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("type", e.getType());
        m.put("title", e.getTitle());
        m.put("description", e.getDescription());
        m.put("coverImage", e.getCoverImage());
        m.put("likeCount", e.getLikeCount() != null ? e.getLikeCount() : 0);
        m.put("viewCount", e.getViewCount() != null ? e.getViewCount() : 0);
        UserEntity publisher = e.getUserId() != null ? userMapper.selectById(e.getUserId()) : null;
        m.put("publisherNickname", publisher != null && publisher.getNickname() != null ? publisher.getNickname() : "匿名");
        m.put("publisherAvatar", publisher != null ? publisher.getAvatar() : null);
        m.put("userId", e.getUserId());
        m.put("createTime", e.getCreateTime());
        return m;
    }

    private Map<String, Object> toDetailMap(DocumentEntity e) {
        Map<String, Object> m = toListMap(e);
        m.put("content", e.getContent());
        return m;
    }
}
