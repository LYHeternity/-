package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目表
 */
@Data
@TableName("project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long founderId;
    private String category;
    private String industry;
    private String stage;
    private BigDecimal fundingNeeded;
    private BigDecimal equityOffered;
    private String status;
    private Integer viewCount;
    private Integer likeCount;
    private String projectImage;
    private String attachments;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    @TableField(exist = false)
    private String submitter;
}
