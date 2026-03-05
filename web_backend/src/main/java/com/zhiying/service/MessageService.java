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
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageCommentMapper commentMapper;
    private final CommentLikeMapper likeMapper;
    private final MessageLikeMapper messageLikeMapper;
    private final UserMapper userMapper;

    public List<Map<String, Object>> list() {
        List<MessageEntity> messages = messageMapper.selectList(
                new LambdaQueryWrapper<MessageEntity>().orderByDesc(MessageEntity::getCreateTime));
        List<Map<String, Object>> result = new ArrayList<>();
        for (MessageEntity m : messages) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("content", m.getContent());
            item.put("createTime", m.getCreateTime());
            item.put("likeCount", m.getLikeCount() != null ? m.getLikeCount() : 0);
            item.put("userId", m.getUserId());
            UserEntity u = userMapper.selectById(m.getUserId());
            item.put("userNickname", u != null && u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : "匿名");
            item.put("userAvatar", u != null ? u.getAvatar() : null);
            List<MessageCommentEntity> comments = commentMapper.selectList(
                    new LambdaQueryWrapper<MessageCommentEntity>()
                            .eq(MessageCommentEntity::getMessageId, m.getId())
                            .isNull(MessageCommentEntity::getParentId)
                            .orderByAsc(MessageCommentEntity::getCreateTime));
            item.put("comments", buildCommentTree(comments, m.getId()));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildCommentTree(List<MessageCommentEntity> topLevel, Long messageId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (MessageCommentEntity c : topLevel) {
            Map<String, Object> node = commentToMap(c);
            List<MessageCommentEntity> replies = commentMapper.selectList(
                    new LambdaQueryWrapper<MessageCommentEntity>()
                            .eq(MessageCommentEntity::getMessageId, messageId)
                            .eq(MessageCommentEntity::getParentId, c.getId())
                            .orderByAsc(MessageCommentEntity::getCreateTime));
            node.put("replies", buildCommentTree(replies, messageId));
            tree.add(node);
        }
        return tree;
    }

    private Map<String, Object> commentToMap(MessageCommentEntity c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", c.getId());
        m.put("content", c.getContent());
        m.put("likeCount", c.getLikeCount() != null ? c.getLikeCount() : 0);
        m.put("createTime", c.getCreateTime());
        m.put("replyToId", c.getReplyToId());
        m.put("userId", c.getUserId());
        UserEntity u = userMapper.selectById(c.getUserId());
        m.put("userNickname", u != null && u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : "匿名");
        m.put("userAvatar", u != null ? u.getAvatar() : null);
        return m;
    }

    public void addMessage(Long userId, String content) {
        MessageEntity m = new MessageEntity();
        m.setUserId(userId);
        m.setContent(content);
        m.setLikeCount(0);
        messageMapper.insert(m);
    }

    public void deleteMessage(Long messageId, Long userId, String role) {
        MessageEntity m = messageMapper.selectById(messageId);
        if (m == null) throw new BusinessException("留言不存在");
        boolean isAdmin = "ADMIN".equals(role);
        boolean isAuthor = m.getUserId() != null && m.getUserId().equals(userId);
        if (!isAdmin && !isAuthor) throw new BusinessException("无权限删除");
        messageMapper.deleteById(messageId);
    }

    public void addComment(Long userId, Long messageId, Long parentId, Long replyToId, String content) {
        MessageEntity msg = messageMapper.selectById(messageId);
        if (msg == null) throw new BusinessException("留言不存在");
        MessageCommentEntity c = new MessageCommentEntity();
        c.setUserId(userId);
        c.setMessageId(messageId);
        c.setParentId(parentId);
        c.setReplyToId(replyToId);
        c.setContent(content);
        c.setLikeCount(0);
        commentMapper.insert(c);
    }

    public void deleteComment(Long commentId, Long userId, String role) {
        MessageCommentEntity c = commentMapper.selectById(commentId);
        if (c == null) throw new BusinessException("评论不存在");
        boolean isAdmin = "ADMIN".equals(role);
        boolean isAuthor = c.getUserId() != null && c.getUserId().equals(userId);
        if (!isAdmin && !isAuthor) throw new BusinessException("无权限删除");
        commentMapper.deleteById(commentId);
    }

    @Transactional
    public Map<String, Object> toggleMessageLike(Long userId, Long messageId) {
        MessageEntity msg = messageMapper.selectById(messageId);
        if (msg == null) throw new BusinessException("留言不存在");
        MessageLikeEntity existing = messageLikeMapper.selectOne(
                new LambdaQueryWrapper<MessageLikeEntity>()
                        .eq(MessageLikeEntity::getMessageId, messageId)
                        .eq(MessageLikeEntity::getUserId, userId));
        boolean liked;
        int likeCount = msg.getLikeCount() != null ? msg.getLikeCount() : 0;
        if (existing != null) {
            messageLikeMapper.deleteById(existing.getId());
            likeCount = Math.max(0, likeCount - 1);
            liked = false;
        } else {
            MessageLikeEntity like = new MessageLikeEntity();
            like.setMessageId(messageId);
            like.setUserId(userId);
            messageLikeMapper.insert(like);
            likeCount++;
            liked = true;
        }
        messageMapper.update(null, new LambdaUpdateWrapper<MessageEntity>()
                .eq(MessageEntity::getId, messageId)
                .set(MessageEntity::getLikeCount, likeCount));
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return result;
    }

    public boolean hasLikedMessage(Long userId, Long messageId) {
        if (userId == null) return false;
        return messageLikeMapper.selectOne(
                new LambdaQueryWrapper<MessageLikeEntity>()
                        .eq(MessageLikeEntity::getMessageId, messageId)
                        .eq(MessageLikeEntity::getUserId, userId)) != null;
    }

    @Transactional
    public void toggleLike(Long userId, Long commentId) {
        CommentLikeEntity existing = likeMapper.selectOne(
                new LambdaQueryWrapper<CommentLikeEntity>()
                        .eq(CommentLikeEntity::getCommentId, commentId)
                        .eq(CommentLikeEntity::getUserId, userId));
        MessageCommentEntity comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException("评论不存在");
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            comment.setLikeCount(Math.max(0, (comment.getLikeCount() != null ? comment.getLikeCount() : 0) - 1));
        } else {
            CommentLikeEntity like = new CommentLikeEntity();
            like.setCommentId(commentId);
            like.setUserId(userId);
            likeMapper.insert(like);
            comment.setLikeCount((comment.getLikeCount() != null ? comment.getLikeCount() : 0) + 1);
        }
        commentMapper.updateById(comment);
    }

    public boolean hasLiked(Long userId, Long commentId) {
        return likeMapper.selectOne(
                new LambdaQueryWrapper<CommentLikeEntity>()
                        .eq(CommentLikeEntity::getCommentId, commentId)
                        .eq(CommentLikeEntity::getUserId, userId)) != null;
    }
}
