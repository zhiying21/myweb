package com.zhiying.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.zhiying.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例 Mapper（继承 MPJBaseMapper 以支持 MPJ 多表联查，同时具备 MP 单表能力）
 */
@Mapper
public interface DemoMapper extends MPJBaseMapper<DemoEntity> {
}
