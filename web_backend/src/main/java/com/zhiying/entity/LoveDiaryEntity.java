package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 恋爱日记实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("love_diary")
public class LoveDiaryEntity extends BaseEntity {

    /** 日记标题 */
    private String title;

    /** 日记内容 */
    private String content;

    /** 日记日期 */
    private LocalDateTime date;

    /** 情感指数（1-5） */
    private Integer emotionScore;

    /** 情感分析结果 */
    private String emotionAnalysis;

    /** 是否公开 */
    private Boolean isPublic;
}
