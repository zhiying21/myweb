package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 示例实体（用于建表与 MP/MPJ 演示，后续可删或改为业务表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo")
public class DemoEntity extends BaseEntity {

    private String name;
    private String remark;
}
