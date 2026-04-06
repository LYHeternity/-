package com.entrepreneur.platform.service;

import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.EntrepreneurPlatformApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户-角色-权限关系测试
 */
@SpringBootTest(classes = EntrepreneurPlatformApplication.class)
@ActiveProfiles("test")
public class UserRolePermissionTest {

    @Autowired
    private UserService userService;

    /**
     * 测试为用户分配角色
     */
    @Test
    public void testAssignRole() {
        // 为用户1分配admin角色
        userService.assignRole(1L, "admin");
        
        // 验证用户1是否拥有admin角色
        List<String> roles = userService.getUserRoles(1L);
        assertTrue(roles.contains("admin"), "用户1应该拥有admin角色");
    }

    /**
     * 测试批量为用户分配角色
     */
    @Test
    public void testBatchAssignRoles() {
        // 为用户2批量分配多个角色
        List<String> roleCodes = List.of("auditor", "analyst");
        userService.batchAssignRoles(2L, roleCodes);
        
        // 验证用户2是否拥有所有分配的角色
        List<String> roles = userService.getUserRoles(2L);
        assertTrue(roles.contains("auditor"), "用户2应该拥有auditor角色");
        assertTrue(roles.contains("analyst"), "用户2应该拥有analyst角色");
    }

    /**
     * 测试从用户移除角色
     */
    @Test
    public void testRemoveRole() {
        // 先为用户3分配角色
        userService.assignRole(3L, "operator");
        
        // 验证用户3是否拥有operator角色
        List<String> rolesBefore = userService.getUserRoles(3L);
        assertTrue(rolesBefore.contains("operator"), "用户3应该拥有operator角色");
        
        // 从用户3移除operator角色
        userService.removeRole(3L, "operator");
        
        // 验证用户3是否不再拥有operator角色
        List<String> rolesAfter = userService.getUserRoles(3L);
        assertFalse(rolesAfter.contains("operator"), "用户3应该不再拥有operator角色");
    }

    /**
     * 测试获取用户的角色列表
     */
    @Test
    public void testGetUserRoles() {
        // 为用户4分配多个角色
        userService.assignRole(4L, "admin");
        userService.assignRole(4L, "auditor");
        
        // 获取用户4的角色列表
        List<String> roles = userService.getUserRoles(4L);
        
        // 验证角色列表是否包含所有分配的角色
        assertTrue(roles.contains("admin"), "用户4应该拥有admin角色");
        assertTrue(roles.contains("auditor"), "用户4应该拥有auditor角色");
        assertFalse(roles.isEmpty(), "用户4的角色列表不应该为空");
    }

    /**
     * 测试获取角色关联的用户列表
     */
    @Test
    public void testGetUsersByRole() {
        // 为多个用户分配admin角色
        userService.assignRole(1L, "admin");
        userService.assignRole(2L, "admin");
        
        // 获取拥有admin角色的用户列表
        List<User> users = userService.getUsersByRole("admin");
        
        // 验证用户列表是否包含所有分配了admin角色的用户
        assertFalse(users.isEmpty(), "拥有admin角色的用户列表不应该为空");
        long adminUserCount = users.stream().filter(user -> user.getId() == 1L || user.getId() == 2L).count();
        assertTrue(adminUserCount >= 2, "拥有admin角色的用户列表应该包含用户1和用户2");
    }

    /**
     * 测试角色权限的继承与叠加机制
     */
    @Test
    public void testRolePermissionInheritance() {
        // 为用户5分配多个角色
        userService.assignRole(5L, "admin");
        userService.assignRole(5L, "auditor");
        
        // 获取用户5的所有权限
        List<String> permissions = userService.getPermissionCodes(5L);
        
        // 验证权限列表是否包含所有角色的权限
        assertFalse(permissions.isEmpty(), "用户5的权限列表不应该为空");
        // 这里可以根据实际的权限定义进行更具体的验证
    }
}
