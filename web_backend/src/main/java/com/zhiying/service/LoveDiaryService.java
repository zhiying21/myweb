package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiying.entity.LoveDiaryEntity;
import com.zhiying.mapper.LoveDiaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 恋爱日记业务服务
 */
@Service
@RequiredArgsConstructor
public class LoveDiaryService {

    private final LoveDiaryMapper loveDiaryMapper;

    /**
     * 分页查询日记
     */
    public Page<LoveDiaryEntity> queryPageList(int pageNum, int pageSize) {
        Page<LoveDiaryEntity> page = new Page<>(pageNum, pageSize);
        return loveDiaryMapper.selectPage(page,
                new LambdaQueryWrapper<LoveDiaryEntity>()
                        .eq(LoveDiaryEntity::getDeleted, 0)
                        .orderByDesc(LoveDiaryEntity::getDate));
    }

    /**
     * 获取所有日记列表
     */
    public List<LoveDiaryEntity> queryList() {
        return loveDiaryMapper.selectList(
                new LambdaQueryWrapper<LoveDiaryEntity>()
                        .eq(LoveDiaryEntity::getDeleted, 0)
                        .orderByDesc(LoveDiaryEntity::getDate));
    }

    /**
     * 根据ID查询日记
     */
    public LoveDiaryEntity queryById(Long id) {
        return loveDiaryMapper.selectById(id);
    }

    /**
     * 创建日记
     */
    public LoveDiaryEntity create(LoveDiaryEntity diary) {
        if (diary.getDate() == null) {
            diary.setDate(LocalDateTime.now());
        }
        if (diary.getIsPublic() == null) {
            diary.setIsPublic(false);
        }
        loveDiaryMapper.insert(diary);
        return diary;
    }

    /**
     * 更新日记
     */
    public LoveDiaryEntity update(LoveDiaryEntity diary) {
        loveDiaryMapper.updateById(diary);
        return diary;
    }

    /**
     * 删除日记（逻辑删除）
     */
    public void delete(Long id) {
        LoveDiaryEntity diary = new LoveDiaryEntity();
        diary.setId(id);
        diary.setDeleted(1);
        loveDiaryMapper.updateById(diary);
    }

    /**
     * 分析日记情感并保存
     */
    public void saveEmotionAnalysis(Long id, Integer emotionScore, String analysis) {
        LoveDiaryEntity diary = new LoveDiaryEntity();
        diary.setId(id);
        diary.setEmotionScore(emotionScore);
        diary.setEmotionAnalysis(analysis);
        loveDiaryMapper.updateById(diary);
    }
}
