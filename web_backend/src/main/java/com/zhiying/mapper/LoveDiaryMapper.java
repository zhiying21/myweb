package com.zhiying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhiying.entity.LoveDiaryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 恋爱日记数据访问接口
 */
@Mapper
public interface LoveDiaryMapper extends BaseMapper<LoveDiaryEntity> {
}
