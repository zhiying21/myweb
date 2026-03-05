package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhiying.common.exception.BusinessException;
import com.zhiying.entity.TicketEntity;
import com.zhiying.entity.TicketReplyEntity;
import com.zhiying.entity.UserEntity;
import com.zhiying.mapper.TicketMapper;
import com.zhiying.mapper.TicketReplyMapper;
import com.zhiying.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketReplyMapper replyMapper;
    private final UserMapper userMapper;

    public void create(Long userId, String email, String subject, String content) {
        TicketEntity t = new TicketEntity();
        t.setUserId(userId);
        t.setEmail(email);
        t.setSubject(subject);
        t.setContent(content);
        t.setStatus("PENDING");
        ticketMapper.insert(t);
    }

    public void reply(Long ticketId, Long adminId, String content) {
        TicketEntity ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) throw new BusinessException("工单不存在");
        TicketReplyEntity reply = new TicketReplyEntity();
        reply.setTicketId(ticketId);
        reply.setAdminId(adminId);
        reply.setContent(content);
        replyMapper.insert(reply);
        ticketMapper.update(null, new LambdaUpdateWrapper<TicketEntity>()
                .eq(TicketEntity::getId, ticketId)
                .set(TicketEntity::getStatus, "REPLIED"));
    }

    public List<Map<String, Object>> listAll() {
        List<TicketEntity> tickets = ticketMapper.selectList(
                new LambdaQueryWrapper<TicketEntity>().orderByDesc(TicketEntity::getCreateTime));
        return tickets.stream().map(t -> {
            Map<String, Object> m = toMap(t);
            List<TicketReplyEntity> replies = replyMapper.selectList(
                    new LambdaQueryWrapper<TicketReplyEntity>()
                            .eq(TicketReplyEntity::getTicketId, t.getId())
                            .orderByAsc(TicketReplyEntity::getCreateTime));
            m.put("replies", replies.stream().map(r -> {
                Map<String, Object> rm = new HashMap<>();
                rm.put("id", r.getId());
                rm.put("content", r.getContent());
                rm.put("createTime", r.getCreateTime());
                UserEntity admin = userMapper.selectById(r.getAdminId());
                rm.put("adminNickname", admin != null ? admin.getNickname() : "管理员");
                return rm;
            }).collect(Collectors.toList()));
            return m;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> listByUserId(Long userId) {
        List<TicketEntity> tickets = ticketMapper.selectList(
                new LambdaQueryWrapper<TicketEntity>()
                        .eq(TicketEntity::getUserId, userId)
                        .orderByDesc(TicketEntity::getCreateTime));
        return tickets.stream().map(t -> {
            Map<String, Object> m = toMap(t);
            List<TicketReplyEntity> replies = replyMapper.selectList(
                    new LambdaQueryWrapper<TicketReplyEntity>()
                            .eq(TicketReplyEntity::getTicketId, t.getId())
                            .orderByAsc(TicketReplyEntity::getCreateTime));
            m.put("replies", replies.stream().map(r -> {
                Map<String, Object> rm = new HashMap<>();
                rm.put("id", r.getId());
                rm.put("content", r.getContent());
                rm.put("createTime", r.getCreateTime());
                return rm;
            }).collect(Collectors.toList()));
            return m;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> toMap(TicketEntity t) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", t.getId());
        m.put("userId", t.getUserId());
        m.put("email", t.getEmail());
        m.put("subject", t.getSubject());
        m.put("content", t.getContent());
        m.put("status", t.getStatus());
        m.put("createTime", t.getCreateTime());
        return m;
    }
}
