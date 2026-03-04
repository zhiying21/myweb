package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message_comment")
public class MessageCommentEntity extends BaseEntity {

    private Long messageId;
    private Long userId;
    private Long parentId;
    private Long replyToId;
    private String content;
    private Integer likeCount;
}
