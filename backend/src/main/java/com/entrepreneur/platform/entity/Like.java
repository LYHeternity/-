package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞表
 */
@Data
<<<<<<< HEAD
@TableName("`like`")
=======
@TableName("like")
>>>>>>> 9b64148d98623b06683264281daf3bb34fea7cdd
public class Like {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId; // 目标ID（帖子ID或评论ID）
    private String targetType; // 目标类型（post/comment）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}