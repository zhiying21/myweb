package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ticket_reply")
public class TicketReplyEntity extends BaseEntity {
    private Long ticketId;
    private Long adminId;
    private String content;
}
