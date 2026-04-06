package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注表
 */
@Data
@TableName("follow")
public class Follow {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId; // 关注者ID
    private Long followUserId; // 被关注者ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}