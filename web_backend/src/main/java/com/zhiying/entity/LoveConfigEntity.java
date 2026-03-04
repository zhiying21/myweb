package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("love_config")
public class LoveConfigEntity extends BaseEntity {

    private LocalDateTime startTime;
    private String name1;
    private String name2;
    private String avatar1;
    private String avatar2;
}
