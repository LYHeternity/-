package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资源/服务表
 */
@Data
@TableName("resource")
public class Resource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long providerId;
    private String providerName;
    private String category;
    private String type;
    private BigDecimal price;
    private String contactInfo;
    private Integer viewCount;
    private Integer favoriteCount;
    private String status;
    private String resourceImage;
    private String attachments;
    private String serviceCycle;
    private BigDecimal rating;
    private String tags;
    private String version;
    private String deploymentEnv;
    private String summary;
    private String apiDefinition;
    private String dependencies;
    private String serviceProcess; // 服务流程，JSON格式
    private String deliveryStandards; // 交付标准，JSON格式
    private String serviceHighlights; // 服务亮点，JSON格式
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    @TableField(exist = false)
    private String submitter;
}
