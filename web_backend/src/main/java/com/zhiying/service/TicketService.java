package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.TicketEntity;
import com.zhiying.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;

    public void create(Long userId, String email, String subject, String content) {
        TicketEntity t = new TicketEntity();
        t.setUserId(userId);
        t.setEmail(email);
        t.setSubject(subject);
        t.setContent(content);
        t.setStatus("PENDING");
        ticketMapper.insert(t);
    }

    public List<Map<String, Object>> listAll() {
        return ticketMapper.selectList(
                new LambdaQueryWrapper<TicketEntity>().orderByDesc(TicketEntity::getCreateTime))
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());
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
