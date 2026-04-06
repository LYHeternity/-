package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.dto.RegisterRequest;
import com.entrepreneur.platform.entity.Permission;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.entity.UserPermission;
import com.entrepreneur.platform.entity.UserRole;
import com.entrepreneur.platform.entity.RolePermission;
import com.entrepreneur.platform.mapper.PermissionMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import com.entrepreneur.platform.mapper.UserPermissionMapper;
import com.entrepreneur.platform.mapper.UserRoleMapper;
import com.entrepreneur.platform.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务：登录加载、注册、个人信息、权限列表、管理员用户列表
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 初始化数据库表
     */
    @javax.annotation.PostConstruct
    public void initDatabase() {
        try {
            // 为user表添加last_login_at字段（安全方式）
            try {
                String checkColumnExists = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'last_login_at'";
                Integer columnExists = jdbcTemplate.queryForObject(checkColumnExists, Integer.class);
                if (columnExists != null && columnExists == 0) {
                    String addLastLoginAtColumn = "ALTER TABLE `user` ADD COLUMN `last_login_at` DATETIME DEFAULT NULL";
                    jdbcTemplate.execute(addLastLoginAtColumn);
                    System.out.println("Added last_login_at column to user table successfully");
                }
            } catch (Exception e) {
                System.err.println("Warning: Failed to add last_login_at column: " + e.getMessage());
                // 继续执行其他初始化逻辑
            }

            // 创建用户角色关联表
            String createUserRoleTable = "CREATE TABLE IF NOT EXISTS `user_role` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT," +
                    "`user_id` BIGINT NOT NULL," +
                    "`role_code` VARCHAR(50) NOT NULL," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_user_role` (`user_id`, `role_code`)," +
                    "INDEX `idx_user_id` (`user_id`)," +
                    "INDEX `idx_role_code` (`role_code`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            // 创建角色权限关联表
            String createRolePermissionTable = "CREATE TABLE IF NOT EXISTS `role_permission` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT," +
                    "`role_code` VARCHAR(50) NOT NULL," +
                    "`permission_code` VARCHAR(50) NOT NULL," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_role_permission` (`role_code`, `permission_code`)," +
                    "INDEX `idx_role_code` (`role_code`)," +
                    "INDEX `idx_permission_code` (`permission_code`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            // 执行创建表的SQL语句
            jdbcTemplate.execute(createUserRoleTable);
            jdbcTemplate.execute(createRolePermissionTable);

            // 插入默认角色权限数据
            String[] permissionInserts = {
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'system')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'user_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'role_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'system_setting')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'service_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'resource_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'service_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'user_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'project_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'service_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'user_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'project_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'project_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'service_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('customer_service', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('customer_service', 'service_manage')"
            };

            for (String insert : permissionInserts) {
                jdbcTemplate.execute(insert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .and(w -> w.eq(User::getUsername, username)
                        .or().eq(User::getPhone, username)
                        .or().eq(User::getEmail, username))
                .eq(User::getDeleted, 0));
        if (user == null) {
            throw new UsernameNotFoundException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new UsernameNotFoundException("账号已禁用或已注销");
        }
        List<SimpleGrantedAuthority> authorities = getPermissionCodes(user.getId()).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        // 添加用户的所有角色
        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(user.getId());
        if (roleCodes.isEmpty()) {
            // 如果没有关联角色，使用用户表中的默认角色
            if (user.getRole() != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
            }
        } else {
            // 添加所有关联的角色
            for (String roleCode : roleCodes) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode.toUpperCase()));
            }
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 根据账号（用户名/手机/邮箱）加载用户，用于登录
     */
    public User findUserByAccount(String account) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .and(w -> w.eq(User::getUsername, account)
                        .or().eq(User::getPhone, account)
                        .or().eq(User::getEmail, account))
                .eq(User::getDeleted, 0));
    }

    public List<String> getPermissionCodes(Long userId) {
        // 获取用户直接分配的权限
        List<Long> permIds = userPermissionMapper.selectList(
                new LambdaQueryWrapper<UserPermission>().eq(UserPermission::getUserId, userId))
                .stream().map(UserPermission::getPermissionId).collect(Collectors.toList());
        
        // 获取用户所有角色的权限（角色权限继承）
        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);
        List<String> rolePermCodes = new java.util.ArrayList<>();
        if (!roleCodes.isEmpty()) {
            for (String roleCode : roleCodes) {
                List<String> codes = rolePermissionMapper.selectPermissionCodesByRoleCode(roleCode);
                rolePermCodes.addAll(codes);
            }
        }
        
        // 去重
        permIds = permIds.stream().distinct().collect(Collectors.toList());
        rolePermCodes = rolePermCodes.stream().distinct().collect(Collectors.toList());
        
        // 合并直接权限和角色权限
        List<String> allPermissions = new java.util.ArrayList<>();
        if (!permIds.isEmpty()) {
            allPermissions.addAll(permissionMapper.selectBatchIds(permIds).stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toList()));
        }
        allPermissions.addAll(rolePermCodes);
        
        // 最终去重
        return allPermissions.stream().distinct().collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterRequest req) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername())) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (StringUtils.hasText(req.getPhone()) && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, req.getPhone())) > 0) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        if (StringUtils.hasText(req.getEmail()) && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, req.getEmail())) > 0) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRealName(req.getRealName());
        user.setRole(req.getRole());
        user.setStatus(1);
        user.setIndustry(req.getIndustry());
        user.setInvestmentAmount(req.getInvestmentAmount());
        user.setCompanyName(req.getCompanyName());
        user.setContactPerson(req.getContactPerson());
        user.setServiceType(req.getServiceType());
        user.setServiceIntro(req.getServiceIntro());
        userMapper.insert(user);

        // 按角色分配默认权限
        List<Permission> all = permissionMapper.selectList(null);
        List<String> rolePerms;
        String role = req.getRole();
        if ("entrepreneur".equals(role)) {
            rolePerms = java.util.Arrays.asList("project:publish", "project:manage", "investment:intent", "investment:audit", "resource:book");
        } else if ("investor".equals(role)) {
            rolePerms = java.util.Arrays.asList("investment:intent", "resource:book");
        } else if ("provider".equals(role)) {
            rolePerms = java.util.Arrays.asList("resource:publish", "resource:manage", "resource:book");
        } else {
            rolePerms = java.util.Collections.emptyList();
        }
        for (Permission p : all) {
            if (rolePerms.contains(p.getCode())) {
                UserPermission up = new UserPermission();
                up.setUserId(user.getId());
                up.setPermissionId(p.getId());
                userPermissionMapper.insert(up);
            }
        }
        return user;
    }

    public User getById(Long id) {
        User u = userMapper.selectById(id);
        if (u != null) u.setPassword(null);
        return u;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, String realName, String email, String phone, String intro,
                              String industry, String investmentAmount, String companyName, String contactPerson, String serviceType, String serviceIntro) {
        User u = userMapper.selectById(userId);
        if (u == null) return;
        if (StringUtils.hasText(realName)) u.setRealName(realName);
        if (email != null) u.setEmail(email);
        if (phone != null) u.setPhone(phone);
        if (intro != null) u.setIntro(intro);
        if (StringUtils.hasText(industry)) u.setIndustry(industry);
        if (StringUtils.hasText(investmentAmount)) u.setInvestmentAmount(investmentAmount);
        if (StringUtils.hasText(companyName)) u.setCompanyName(companyName);
        if (StringUtils.hasText(contactPerson)) u.setContactPerson(contactPerson);
        if (StringUtils.hasText(serviceType)) u.setServiceType(serviceType);
        if (StringUtils.hasText(serviceIntro)) u.setServiceIntro(serviceIntro);
        userMapper.updateById(u);
    }

    public void updateAvatar(Long userId, String avatarPath) {
        User u = userMapper.selectById(userId);
        if (u != null) {
            u.setAvatar(avatarPath);
            userMapper.updateById(u);
        }
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User u = userMapper.selectById(userId);
        if (u == null || !passwordEncoder.matches(oldPassword, u.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        u.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(u);
    }

    public IPage<User> listUsers(Page<User> page, String role, Integer status, String keyword) {
        LambdaQueryWrapper<User> q = new LambdaQueryWrapper<User>().eq(User::getDeleted, 0);
        if (StringUtils.hasText(role)) q.eq(User::getRole, role);
        if (status != null) q.eq(User::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(User::getUsername, keyword).or().like(User::getPhone, keyword).or().like(User::getEmail, keyword));
        }
        return userMapper.selectPage(page, q);
    }

    public void setStatus(Long userId, Integer status) {
        User u = userMapper.selectById(userId);
        if (u != null) {
            u.setStatus(status);
            userMapper.updateById(u);
        }
    }

    public void resetPassword(Long userId, String newPassword) {
        User u = userMapper.selectById(userId);
        if (u != null) {
            u.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateById(u);
        }
    }

    /** 管理员更新用户（不含密码） */
    public void updateUser(User user) {
        if (user == null || user.getId() == null) return;
        User u = userMapper.selectById(user.getId());
        if (u == null) return;
        if (user.getRealName() != null) u.setRealName(user.getRealName());
        if (user.getEmail() != null) u.setEmail(user.getEmail());
        if (user.getPhone() != null) u.setPhone(user.getPhone());
        if (user.getIntro() != null) u.setIntro(user.getIntro());
        if (user.getRole() != null) u.setRole(user.getRole());
        if (user.getIndustry() != null) u.setIndustry(user.getIndustry());
        if (user.getCompanyName() != null) u.setCompanyName(user.getCompanyName());
        if (user.getContactPerson() != null) u.setContactPerson(user.getContactPerson());
        if (user.getServiceType() != null) u.setServiceType(user.getServiceType());
        if (user.getServiceIntro() != null) u.setServiceIntro(user.getServiceIntro());
        userMapper.updateById(u);
    }

    /** 管理员新增用户 */
    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (StringUtils.hasText(user.getPhone()) && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, user.getPhone())) > 0) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        if (StringUtils.hasText(user.getEmail()) && userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail())) > 0) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setDeleted(0);
        userMapper.insert(user);

        // 按角色分配默认权限
        List<Permission> all = permissionMapper.selectList(null);
        List<String> rolePerms;
        String role = user.getRole();
        if ("entrepreneur".equals(role)) {
            rolePerms = java.util.Arrays.asList("project:publish", "project:manage", "investment:intent", "investment:audit", "resource:book");
        } else if ("investor".equals(role)) {
            rolePerms = java.util.Arrays.asList("investment:intent", "resource:book");
        } else if ("provider".equals(role)) {
            rolePerms = java.util.Arrays.asList("resource:publish", "resource:manage", "resource:book");
        } else if ("admin".equals(role)) {
            rolePerms = java.util.Collections.emptyList();
        } else {
            rolePerms = java.util.Collections.emptyList();
        }
        for (Permission p : all) {
            if (rolePerms.contains(p.getCode())) {
                UserPermission up = new UserPermission();
                up.setUserId(user.getId());
                up.setPermissionId(p.getId());
                userPermissionMapper.insert(up);
            }
        }
    }
    
    /**
     * 为用户分配角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(Long userId, String roleCode) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 检查是否已经分配了该角色
        UserRole existing = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleCode, roleCode));
        
        if (existing == null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleCode(roleCode);
            userRoleMapper.insert(userRole);
        }
    }
    
    /**
     * 批量为用户分配角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAssignRoles(Long userId, List<String> roleCodes) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 删除用户现有的所有角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        
        // 分配新的角色
        for (String roleCode : roleCodes) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleCode(roleCode);
            userRoleMapper.insert(userRole);
        }
    }
    
    /**
     * 从用户移除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRole(Long userId, String roleCode) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleCode, roleCode));
    }
    
    /**
     * 获取用户的角色列表
     */
    public List<String> getUserRoles(Long userId) {
        return userRoleMapper.selectRoleCodesByUserId(userId);
    }
    
    /**
     * 获取角色关联的用户列表
     */
    public List<User> getUsersByRole(String roleCode) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleCode(roleCode);
        if (userIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return userMapper.selectBatchIds(userIds);
    }

    /**
     * 根据ID更新用户信息
     */
    public void updateById(User user) {
        if (user == null || user.getId() == null) return;
        userMapper.updateById(user);
    }
}
