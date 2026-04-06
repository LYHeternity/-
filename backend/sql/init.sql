-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `role_code` VARCHAR(50) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_code`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `permission_code` VARCHAR(50) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_code`, `permission_code`),
  INDEX `idx_role_code` (`role_code`),
  INDEX `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认角色权限数据
INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES
('admin', 'system'),
('admin', 'user_manage'),
('admin', 'role_manage'),
('admin', 'system_setting'),
('admin', 'content'),
('admin', 'project_manage'),
('admin', 'service_manage'),
('admin', 'resource_manage'),
('admin', 'audit'),
('admin', 'project_audit'),
('admin', 'service_audit'),
('admin', 'stats'),
('admin', 'user_stats'),
('admin', 'project_stats'),
('auditor', 'audit'),
('auditor', 'project_audit'),
('auditor', 'service_audit'),
('analyst', 'stats'),
('analyst', 'user_stats'),
('analyst', 'project_stats'),
('operator', 'content'),
('operator', 'project_manage'),
('operator', 'service_manage'),
('customer_service', 'content'),
('customer_service', 'service_manage');
