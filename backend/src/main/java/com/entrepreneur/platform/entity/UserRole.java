package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联表
 */
@Data
@TableName("user_role")
public class UserRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String roleCode;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}