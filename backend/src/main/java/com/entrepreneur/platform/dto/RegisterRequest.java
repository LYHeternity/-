package com.entrepreneur.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册请求：按角色动态字段，此处包含所有角色可能字段
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "密码需包含大小写字母、数字和特殊字符")
    private String password;
    private String email;
    private String phone;
    private String realName;
    @NotBlank(message = "角色不能为空")
    private String role; // entrepreneur / investor / provider

    private String industry;       // 创业者/投资人
    private String investmentAmount; // 投资人
    private String companyName;   // 服务商
    private String contactPerson; // 服务商
    private String serviceType;   // 服务商
    private String serviceIntro;   // 服务商
}
