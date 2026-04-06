package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息表
 */
@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Long targetId;
    private String targetType;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
