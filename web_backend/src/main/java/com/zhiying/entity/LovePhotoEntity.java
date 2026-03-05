package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 恋爱照片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("love_photo")
public class LovePhotoEntity extends BaseEntity {

    /** 照片URL */
    private String url;

    /** 照片描述 */
    private String description;

    /** 拍摄日期 */
    private LocalDateTime takenDate;

    /** 照片分类标签 */
    private String tags;

    /** 是否精选照片 */
    private Boolean isFeatured;
}
