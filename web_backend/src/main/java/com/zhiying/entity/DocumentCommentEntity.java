package com.zhiying.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhiying.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_comment")
public class DocumentCommentEntity extends BaseEntity {
    private Long documentId;
    private Long userId;
    private String content;
    private Integer likeCount;
}
