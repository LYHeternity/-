package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员-用户管理：列表、启用/禁用、重置密码、编辑
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                         @RequestParam(defaultValue = "10") long size,
                         @RequestParam(required = false) String role,
                         @RequestParam(required = false) Integer status,
                         @RequestParam(required = false) String keyword) {
        return Result.ok(userService.listUsers(new Page<>(current, size), role, status, keyword));
    }

    @PutMapping("/{id}/status")
    public Result<String> setStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.setStatus(id, body.get("status"));
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null) newPassword = "Admin@1234";
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setPassword(null);
        userService.updateUser(user);
        return Result.ok();
    }

    @PostMapping
    public Result<String> create(@RequestBody User user) {
        userService.createUser(user);
        return Result.ok();
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<String> assignRoles(@PathVariable Long id, @RequestBody List<String> roleCodes) {
        userService.batchAssignRoles(id, roleCodes);
        return Result.ok();
    }
    
    /**
     * 获取用户的角色列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<String>> getUserRoles(@PathVariable Long id) {
        return Result.ok(userService.getUserRoles(id));
    }
    
    /**
     * 从用户移除角色
     */
    @DeleteMapping("/{id}/roles/{roleCode}")
    public Result<String> removeRole(@PathVariable Long id, @PathVariable String roleCode) {
        userService.removeRole(id, roleCode);
        return Result.ok();
    }
    
    /**
     * 获取角色关联的用户列表
     */
    @GetMapping("/by-role/{roleCode}")
    public Result<List<User>> getUsersByRole(@PathVariable String roleCode) {
        return Result.ok(userService.getUsersByRole(roleCode));
    }
    
    /**
     * 批量设置用户状态
     */
    @PutMapping("/batch-status")
    public Result<String> batchSetStatus(@RequestBody Map<String, Object> body) {
        List<Long> ids = (List<Long>) body.get("ids");
        Integer status = (Integer) body.get("status");
        for (Long id : ids) {
            userService.setStatus(id, status);
        }
        return Result.ok();
    }
    
    /**
     * 批量重置密码
     */
    @PutMapping("/batch-reset-password")
    public Result<String> batchResetPassword(@RequestBody Map<String, Object> body) {
        List<Long> ids = (List<Long>) body.get("ids");
        String password = (String) body.get("password");
        for (Long id : ids) {
            userService.resetPassword(id, password);
        }
        return Result.ok();
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch-delete")
    public Result<String> batchDelete(@RequestBody Map<String, Object> body) {
        List<Long> ids = (List<Long>) body.get("ids");
        for (Long id : ids) {
            User user = new User();
            user.setId(id);
            user.setDeleted(1);
            userService.updateById(user);
        }
        return Result.ok();
    }
}
