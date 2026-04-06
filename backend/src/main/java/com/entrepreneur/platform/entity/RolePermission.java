package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联表
 */
@Data
@TableName("role_permission")
public class RolePermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleCode;
    private String permissionCode;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}