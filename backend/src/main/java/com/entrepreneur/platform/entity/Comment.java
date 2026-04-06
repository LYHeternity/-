package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论表
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId; // 父评论ID（用于回复）
    private String content;
    private Integer likeCount;
    private Integer isAnswer; // 是否为回答（仅问题类型帖子）
    private Integer status; // 0-待审核，1-已发布，2-已禁用
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}