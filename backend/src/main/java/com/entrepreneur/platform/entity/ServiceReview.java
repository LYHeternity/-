package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务评价表
 */
@Data
@TableName("service_review")
public class ServiceReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long resourceId;
    private Long userId;
    private Integer rating;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
