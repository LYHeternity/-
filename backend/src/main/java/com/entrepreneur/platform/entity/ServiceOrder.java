package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务订单表
 */
@Data
@TableName("service_order")
public class ServiceOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceId;
    private Long buyerId;
    private Long providerId;
    private String title;
    private Integer quantity;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime deliveryTime;
    private String rejectReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
