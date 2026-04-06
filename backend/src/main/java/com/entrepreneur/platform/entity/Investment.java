package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投资表
 */
@Data
@TableName("investment")
public class Investment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long investorId;
    private BigDecimal amount;
    private String duration;
    private String description;
    private String attachmentPath;
    private String status;
    private String rejectReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
