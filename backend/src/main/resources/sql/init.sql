-- 一站式创业者综合服务平台 - 数据库初始化脚本
-- 执行顺序：先创建数据库，再执行此脚本

-- 切换到platform数据库
USE platform;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `role` VARCHAR(20) COMMENT '角色',
  `avatar` VARCHAR(255) COMMENT '头像',
  `status` INT DEFAULT 1 COMMENT '状态',
  `salt` VARCHAR(50) COMMENT '盐值',
  `industry` VARCHAR(100) COMMENT '行业',
  `investment_amount` VARCHAR(50) COMMENT '投资金额',
  `company_name` VARCHAR(100) COMMENT '公司名称',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `service_type` VARCHAR(100) COMMENT '服务类型',
  `service_intro` TEXT COMMENT '服务介绍',
  `intro` TEXT COMMENT '个人介绍',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_at` DATETIME COMMENT '最后登录时间',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 2. 权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(50) NOT NULL COMMENT '权限代码',
  `description` VARCHAR(255) COMMENT '权限描述',
  `module` VARCHAR(50) COMMENT '模块',
  `status` INT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_module` (`module`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 3. 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 4. 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 5. 用户权限关联表
CREATE TABLE IF NOT EXISTS `user_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_permission` (`user_id`, `permission_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限关联表';

-- 6. 项目表
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL COMMENT '项目标题',
  `description` TEXT COMMENT '项目描述',
  `founder_id` BIGINT NOT NULL COMMENT '创始人ID',
  `category` VARCHAR(50) COMMENT '分类',
  `industry` VARCHAR(100) COMMENT '行业',
  `stage` VARCHAR(50) COMMENT '阶段',
  `funding_needed` DECIMAL(10,2) COMMENT '融资需求',
  `equity_offered` DECIMAL(5,2) COMMENT '提供股权',
  `status` VARCHAR(20) COMMENT '状态',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `project_image` VARCHAR(255) COMMENT '项目图片',
  `attachments` TEXT COMMENT '附件',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_founder_id` (`founder_id`),
  KEY `idx_industry` (`industry`),
  KEY `idx_stage` (`stage`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 7. 项目进度表
CREATE TABLE IF NOT EXISTS `project_progress` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `phase` VARCHAR(100) COMMENT '阶段',
  `content` TEXT COMMENT '内容',
  `progress_date` DATETIME COMMENT '进度日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目进度表';

-- 8. 项目团队成员表
CREATE TABLE IF NOT EXISTS `project_team_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(50) COMMENT '角色',
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目团队成员表';

-- 9. 文章表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `content` TEXT COMMENT '文章内容',
  `url` VARCHAR(500) COMMENT '原文链接',
  `source` VARCHAR(100) COMMENT '来源',
  `author` VARCHAR(100) COMMENT '作者',
  `publication_date` VARCHAR(50) COMMENT '发布日期',
  `content_summary` TEXT COMMENT '内容摘要',
  `category` VARCHAR(50) COMMENT '分类',
  `relevance_score` DOUBLE COMMENT '相关度评分',
  `tags` VARCHAR(255) COMMENT '标签',
  `image_url` VARCHAR(500) COMMENT '图片URL',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创业资讯文章表';

-- 10. 文章收藏表
CREATE TABLE IF NOT EXISTS `article_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- 11. 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT COMMENT '父评论ID',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_id`, `target_type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 12. 点赞表
CREATE TABLE IF NOT EXISTS `like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 13. 关注表
CREATE TABLE IF NOT EXISTS `follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
  `followed_id` BIGINT NOT NULL COMMENT '被关注者ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_id`, `followed_id`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_followed_id` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- 14. 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `status` VARCHAR(20) DEFAULT 'unread' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 15. 通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(50) COMMENT '通知类型',
  `title` VARCHAR(255) NOT NULL COMMENT '通知标题',
  `content` TEXT COMMENT '通知内容',
  `status` VARCHAR(20) DEFAULT 'unread' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 16. 服务订单表
CREATE TABLE IF NOT EXISTS `service_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `service_id` BIGINT NOT NULL COMMENT '服务ID',
  `provider_id` BIGINT NOT NULL COMMENT '服务提供者ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务订单表';

-- 17. 服务评价表
CREATE TABLE IF NOT EXISTS `service_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `service_id` BIGINT NOT NULL COMMENT '服务ID',
  `rating` DECIMAL(3,2) NOT NULL COMMENT '评分',
  `comment` TEXT COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- 18. 资源表
CREATE TABLE IF NOT EXISTS `resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '描述',
  `provider_id` BIGINT NOT NULL COMMENT '提供者ID',
  `provider_name` VARCHAR(100) COMMENT '提供者名称',
  `category` VARCHAR(50) COMMENT '分类',
  `type` VARCHAR(20) COMMENT '类型',
  `price` DECIMAL(10,2) COMMENT '价格',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `service_cycle` VARCHAR(50) COMMENT '服务周期',
  `rating` DECIMAL(3,2) DEFAULT 0 COMMENT '评分',
  `resource_image` VARCHAR(255) COMMENT '资源图片',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_category` (`category`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- 19. 投资表
CREATE TABLE IF NOT EXISTS `investment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `investor_id` BIGINT NOT NULL COMMENT '投资人ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '投资金额',
  `equity` DECIMAL(5,2) COMMENT '获得股权',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_investor_id` (`investor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投资表';

-- 20. 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(500) COMMENT '配置值',
  `description` VARCHAR(255) COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 21. 系统日志表
CREATE TABLE IF NOT EXISTS `system_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作',
  `module` VARCHAR(50) COMMENT '模块',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` TEXT COMMENT '用户代理',
  `content` TEXT COMMENT '操作内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_operation` (`operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 22. 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 插入默认数据
-- 1. 默认管理员用户
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`, `real_name`, `role`, `status`, `deleted`) 
VALUES ('admin', 'Admin@1234', 'admin@platform.com', '13800000000', '系统管理员', 'admin', 1, 0);

-- 2. 基础权限
INSERT IGNORE INTO `permission` (`name`, `code`, `description`, `module`, `status`) VALUES
('用户管理', 'user:manage', '用户管理权限', 'system', 1),
('项目管理', 'project:manage', '项目管理权限', 'project', 1),
('文章管理', 'article:manage', '文章管理权限', 'article', 1),
('服务管理', 'service:manage', '服务管理权限', 'service', 1),
('系统管理', 'system:manage', '系统管理权限', 'system', 1);

-- 3. 系统配置
INSERT IGNORE INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('test_data_inserted', 'false', '标记测试数据是否已插入'),
('system_name', '一站式创业者综合服务平台', '系统名称'),
('system_version', '1.0.0', '系统版本');

COMMIT;

-- 脚本执行完成
SELECT '数据库初始化完成' AS result;