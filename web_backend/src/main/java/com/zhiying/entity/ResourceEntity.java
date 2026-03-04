package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resource")
public class ResourceEntity extends BaseEntity {

    private String name;
    private String icon;
    private String link;
    private Integer sortOrder;
}
