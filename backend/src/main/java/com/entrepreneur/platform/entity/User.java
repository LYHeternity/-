package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息表
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String role;
    private String avatar;
    private Integer status;
    private String industry;
    private String investmentAmount;
    private String companyName;
    private String contactPerson;
    private String serviceType;
    private String serviceIntro;
    private String intro;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    private LocalDateTime lastLoginAt;
    @TableLogic
    private Integer deleted;
}
