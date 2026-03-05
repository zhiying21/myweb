package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 恋爱纪念日实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("love_reminder")
public class LoveReminderEntity extends BaseEntity {

    /** 纪念日标题 */
    private String title;

    /** 纪念日日期 */
    private LocalDateTime date;

    /** 纪念日描述 */
    private String description;

    /** 提醒频率（once, yearly, monthly）*/
    private String frequency;

    /** 是否启用提醒 */
    private Boolean isEnabled;
}
