package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社区帖子表
 */
@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type; // article/question
    private String category;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status; // 0-待审核，1-已发布，2-已禁用
    private Long bestAnswerId; // 最佳答案ID（仅问题类型）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}