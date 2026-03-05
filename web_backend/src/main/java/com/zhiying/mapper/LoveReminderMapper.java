package com.zhiying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhiying.entity.LoveReminderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 恋爱纪念日数据访问接口
 */
@Mapper
public interface LoveReminderMapper extends BaseMapper<LoveReminderEntity> {
}
