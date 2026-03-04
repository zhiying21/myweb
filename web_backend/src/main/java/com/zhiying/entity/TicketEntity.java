package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket")
public class TicketEntity extends BaseEntity {

    private Long userId;
    private String email;
    private String subject;
    private String content;
    private String status;
}
