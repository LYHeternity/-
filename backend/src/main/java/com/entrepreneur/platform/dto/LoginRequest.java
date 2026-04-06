package com.entrepreneur.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求：账号（用户名/手机/邮箱）、密码
 */
@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String account;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String captcha;
    private boolean remember;
}
