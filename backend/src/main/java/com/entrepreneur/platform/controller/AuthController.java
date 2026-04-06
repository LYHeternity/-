package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.dto.LoginRequest;
import com.entrepreneur.platform.dto.LoginResponse;
import com.entrepreneur.platform.dto.RegisterRequest;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.service.LogService;
import com.entrepreneur.platform.service.UserService;
import com.entrepreneur.platform.util.CaptchaUtil;
import com.entrepreneur.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证接口：登录、注册（无验证码）
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final LogService logService;

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public Result<?> captcha(HttpSession session) {
        CaptchaUtil.Captcha captcha = CaptchaUtil.generateCaptcha();
        // 将验证码存储到session中，有效期5分钟
        session.setAttribute("captcha", captcha.getCode());
        session.setMaxInactiveInterval(300);
        return Result.ok("data:image/png;base64," + captcha.getImageBase64());
    }

    /**
     * 登录：账号（用户名/手机/邮箱）+ 密码 + 验证码，返回 JWT 与用户信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest req, HttpSession session) {
        try {
            // 验证验证码
            String sessionCaptcha = (String) session.getAttribute("captcha");
            if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(request.getCaptcha())) {
                return Result.fail(401, "验证码错误");
            }
            
            User user = userService.findUserByAccount(request.getAccount());
            if (user == null) {
                return Result.fail(401, "账号或密码错误");
            }
            // 暂时禁用密码加密校验，直接验证密码是否匹配
            if (!request.getPassword().equals(user.getPassword())) {
                return Result.fail(401, "账号或密码错误");
            }
            // 更新最后登录时间
            user.setLastLoginAt(LocalDateTime.now());
            userService.updateById(user);
            logService.log("INFO", "auth", "登录成功", user.getUsername(), req);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            List<String> permissions = userService.getPermissionCodes(user.getId());
            LoginResponse resp = new LoginResponse(token, user.getId(), user.getUsername(), user.getRole(),
                    user.getAvatar(), user.getRealName(), permissions);
            return Result.ok(resp);
        } catch (Exception e) {
            logService.log("ERROR", "auth", "登录失败: " + e.getMessage(), request.getAccount(), req);
            return Result.fail(401, "账号或密码错误");
        }
    }

    /**
     * 注册：按角色（创业者/投资人/服务商）提交表单
     */
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody RegisterRequest request) {
        if (!java.util.Arrays.asList("entrepreneur", "investor", "provider").contains(request.getRole())) {
            return Result.fail("角色参数错误");
        }
        User user = userService.register(request);
        return Result.ok("注册成功，请登录");
    }
}
