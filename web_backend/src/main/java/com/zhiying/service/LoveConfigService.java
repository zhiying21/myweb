package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.entity.LoveConfigEntity;
import com.zhiying.mapper.LoveConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoveConfigService {

    private final LoveConfigMapper loveConfigMapper;

    public Map<String, Object> getConfig() {
        LoveConfigEntity e = loveConfigMapper.selectOne(
                new LambdaQueryWrapper<LoveConfigEntity>().last("LIMIT 1"));
        Map<String, Object> m = new HashMap<>();
        if (e != null) {
            m.put("startTime", e.getStartTime());
            m.put("name1", e.getName1() != null ? e.getName1() : "TA");
            m.put("name2", e.getName2() != null ? e.getName2() : "TA");
            m.put("avatar1", e.getAvatar1());
            m.put("avatar2", e.getAvatar2());
        } else {
            m.put("startTime", LocalDateTime.now());
            m.put("name1", "TA");
            m.put("name2", "TA");
            m.put("avatar1", null);
            m.put("avatar2", null);
        }
        return m;
    }

    public void saveConfig(LocalDateTime startTime, String name1, String name2, String avatar1, String avatar2) {
        LoveConfigEntity e = loveConfigMapper.selectOne(
                new LambdaQueryWrapper<LoveConfigEntity>().last("LIMIT 1"));
        if (e == null) {
            e = new LoveConfigEntity();
            e.setStartTime(startTime);
            e.setName1(name1);
            e.setName2(name2);
            e.setAvatar1(avatar1);
            e.setAvatar2(avatar2);
            loveConfigMapper.insert(e);
        } else {
            e.setStartTime(startTime);
            e.setName1(name1);
            e.setName2(name2);
            e.setAvatar1(avatar1);
            e.setAvatar2(avatar2);
            loveConfigMapper.updateById(e);
        }
    }
}
