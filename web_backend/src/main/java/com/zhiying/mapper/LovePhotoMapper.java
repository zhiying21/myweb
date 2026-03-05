package com.zhiying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhiying.entity.LovePhotoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 恋爱照片数据访问接口
 */
@Mapper
public interface LovePhotoMapper extends BaseMapper<LovePhotoEntity> {
}
