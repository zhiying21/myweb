package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document")
public class DocumentEntity extends BaseEntity {

    private String type;
    private String title;
    private String description;
    private String content;
    private String coverImage;
    private Long userId;
}
