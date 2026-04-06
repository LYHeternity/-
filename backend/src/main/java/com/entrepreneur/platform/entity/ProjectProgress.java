package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目进展表
 */
@Data
@TableName("project_progress")
public class ProjectProgress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String content;
    private String attachments;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
