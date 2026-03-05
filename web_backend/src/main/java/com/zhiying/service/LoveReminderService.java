package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiying.entity.LoveReminderEntity;
import com.zhiying.mapper.LoveReminderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 恋爱纪念日业务服务
 */
@Service
@RequiredArgsConstructor
public class LoveReminderService {

    private final LoveReminderMapper loveReminderMapper;

    /**
     * 分页查询纪念日
     */
    public Page<LoveReminderEntity> queryPageList(int pageNum, int pageSize) {
        Page<LoveReminderEntity> page = new Page<>(pageNum, pageSize);
        return loveReminderMapper.selectPage(page,
                new LambdaQueryWrapper<LoveReminderEntity>()
                        .eq(LoveReminderEntity::getDeleted, 0)
                        .orderByAsc(LoveReminderEntity::getDate));
    }

    /**
     * 获取所有纪念日列表
     */
    public List<LoveReminderEntity> queryList() {
        return loveReminderMapper.selectList(
                new LambdaQueryWrapper<LoveReminderEntity>()
                        .eq(LoveReminderEntity::getDeleted, 0)
                        .orderByAsc(LoveReminderEntity::getDate));
    }

    /**
     * 获取即将到来的纪念日（30天内）
     */
    public List<LoveReminderEntity> queryUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(30);
        return loveReminderMapper.selectList(
                new LambdaQueryWrapper<LoveReminderEntity>()
                        .eq(LoveReminderEntity::getDeleted, 0)
                        .eq(LoveReminderEntity::getIsEnabled, true)
                        .between(LoveReminderEntity::getDate, now, future)
                        .orderByAsc(LoveReminderEntity::getDate));
    }

    /**
     * 根据ID查询纪念日
     */
    public LoveReminderEntity queryById(Long id) {
        return loveReminderMapper.selectById(id);
    }

    /**
     * 创建纪念日
     */
    public LoveReminderEntity create(LoveReminderEntity reminder) {
        if (reminder.getIsEnabled() == null) {
            reminder.setIsEnabled(true);
        }
        if (reminder.getFrequency() == null) {
            reminder.setFrequency("once");
        }
        loveReminderMapper.insert(reminder);
        return reminder;
    }

    /**
     * 更新纪念日
     */
    public LoveReminderEntity update(LoveReminderEntity reminder) {
        loveReminderMapper.updateById(reminder);
        return reminder;
    }

    /**
     * 删除纪念日（逻辑删除）
     */
    public void delete(Long id) {
        LoveReminderEntity reminder = new LoveReminderEntity();
        reminder.setId(id);
        reminder.setDeleted(1);
        loveReminderMapper.updateById(reminder);
    }

    /**
     * 设置提醒启用状态
     */
    public void setReminderEnabled(Long id, Boolean enabled) {
        LoveReminderEntity reminder = new LoveReminderEntity();
        reminder.setId(id);
        reminder.setIsEnabled(enabled);
        loveReminderMapper.updateById(reminder);
    }
}
