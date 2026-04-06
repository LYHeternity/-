package com.entrepreneur.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录成功返回：token、用户信息、权限列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String avatar;
    private String realName;
    private List<String> permissions;
}
