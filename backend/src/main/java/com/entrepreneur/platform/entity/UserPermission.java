package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户权限关联表
 */
@Data
@TableName("user_permission")
public class UserPermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long permissionId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
