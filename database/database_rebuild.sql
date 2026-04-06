-- 数据库重建脚本 - 一站式创业者综合服务平台
-- 生成时间：2026-04-04

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `entrepreneur_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `entrepreneur_platform`;

-- 清空现有表（如果存在）
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `article_favorite`;
DROP TABLE IF EXISTS `article`;
DROP TABLE IF EXISTS `investment`;
DROP TABLE IF EXISTS `project_team_member`;
DROP TABLE IF EXISTS `project_progress`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `service_review`;
DROP TABLE IF EXISTS `service_order`;
DROP TABLE IF EXISTS `resource`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `like`;
DROP TABLE IF EXISTS `follow`;
DROP TABLE IF EXISTS `notification`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `user_favorite`;
DROP TABLE IF EXISTS `user_permission`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `system_log`;
DROP TABLE IF EXISTS `system_config`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- 创建用户表
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `real_name` VARCHAR(50) DEFAULT NULL,
  `role` VARCHAR(50) DEFAULT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `status` INT DEFAULT 1,
  `salt` VARCHAR(50) DEFAULT NULL,
  `industry` VARCHAR(100) DEFAULT NULL,
  `investment_amount` VARCHAR(100) DEFAULT NULL,
  `company_name` VARCHAR(255) DEFAULT NULL,
  `contact_person` VARCHAR(50) DEFAULT NULL,
  `service_type` VARCHAR(255) DEFAULT NULL,
  `service_intro` TEXT DEFAULT NULL,
  `intro` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login_at` DATETIME DEFAULT NULL,
  `follow_count` INT DEFAULT 0,
  `follower_count` INT DEFAULT 0,
  `post_count` INT DEFAULT 0,
  `comment_count` INT DEFAULT 0,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 创建文章表
CREATE TABLE `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `url` VARCHAR(500) NOT NULL,
  `source` VARCHAR(100) NOT NULL,
  `author` VARCHAR(100) DEFAULT NULL,
  `publication_date` VARCHAR(100) DEFAULT NULL,
  `content_summary` TEXT DEFAULT NULL,
  `category` VARCHAR(100) NOT NULL,
  `relevance_score` DOUBLE DEFAULT 0.0,
  `tags` VARCHAR(255) DEFAULT NULL,
  `image_url` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_url` (`url`),
  KEY `idx_category` (`category`),
  KEY `idx_relevance_score` (`relevance_score`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创业资讯文章表';

-- 创建文章收藏表
CREATE TABLE `article_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `article_id` BIGINT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`),
  CONSTRAINT `fk_article_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- 创建项目表
CREATE TABLE `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `founder_id` BIGINT NOT NULL,
  `category` VARCHAR(100) DEFAULT NULL,
  `industry` VARCHAR(100) DEFAULT NULL,
  `stage` VARCHAR(50) DEFAULT NULL,
  `funding_needed` DECIMAL(10,2) DEFAULT NULL,
  `equity_offered` DECIMAL(10,2) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `project_image` VARCHAR(255) DEFAULT NULL,
  `attachments` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_founder_id` (`founder_id`),
  KEY `idx_status` (`status`),
  KEY `idx_industry` (`industry`),
  CONSTRAINT `fk_project_founder` FOREIGN KEY (`founder_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 创建项目进度表
CREATE TABLE `project_progress` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `stage` VARCHAR(100) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  CONSTRAINT `fk_project_progress_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目进度表';

-- 创建项目团队成员表
CREATE TABLE `project_team_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` VARCHAR(100) DEFAULT NULL,
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_project_team_member_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_project_team_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目团队成员表';

-- 创建投资表
CREATE TABLE `investment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `investor_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `duration` VARCHAR(100) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `attachment_path` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `reject_reason` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_investor_id` (`investor_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_investment_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_investment_investor` FOREIGN KEY (`investor_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投资表';

-- 创建资源/服务表
CREATE TABLE `resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `provider_id` BIGINT NOT NULL,
  `provider_name` VARCHAR(100) DEFAULT NULL,
  `category` VARCHAR(100) DEFAULT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `price` DECIMAL(10,2) DEFAULT NULL,
  `contact_info` VARCHAR(255) DEFAULT NULL,
  `view_count` INT DEFAULT 0,
  `favorite_count` INT DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `resource_image` VARCHAR(255) DEFAULT NULL,
  `attachments` TEXT DEFAULT NULL,
  `service_cycle` VARCHAR(100) DEFAULT NULL,
  `rating` DECIMAL(3,2) DEFAULT 0.00,
  `tags` VARCHAR(255) DEFAULT NULL,
  `version` VARCHAR(50) DEFAULT NULL,
  `deployment_env` VARCHAR(255) DEFAULT NULL,
  `summary` TEXT DEFAULT NULL,
  `api_definition` TEXT DEFAULT NULL,
  `dependencies` TEXT DEFAULT NULL,
  `service_process` TEXT DEFAULT NULL,
  `delivery_standards` TEXT DEFAULT NULL,
  `service_highlights` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  CONSTRAINT `fk_resource_provider` FOREIGN KEY (`provider_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源/服务表';

-- 创建服务订单表
CREATE TABLE `service_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `resource_id` BIGINT NOT NULL,
  `buyer_id` BIGINT NOT NULL,
  `provider_id` BIGINT NOT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `quantity` INT DEFAULT 1,
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `delivery_time` DATETIME DEFAULT NULL,
  `reject_reason` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_resource_id` (`resource_id`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_service_order_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_service_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_service_order_provider` FOREIGN KEY (`provider_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务订单表';

-- 创建服务评价表
CREATE TABLE `service_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `rating` DECIMAL(3,2) NOT NULL,
  `comment` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_user` (`order_id`, `user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_service_review_order` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_service_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- 创建社区帖子表
CREATE TABLE `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `tags` VARCHAR(255) DEFAULT NULL,
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `comment_count` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `best_answer_id` BIGINT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_type` (`type`),
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';

-- 创建评论表
CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `parent_id` BIGINT DEFAULT 0,
  `content` TEXT NOT NULL,
  `like_count` INT DEFAULT 0,
  `is_answer` INT DEFAULT 0,
  `status` INT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 创建点赞表
CREATE TABLE `like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `target_id` BIGINT NOT NULL,
  `target_type` VARCHAR(50) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_id`, `target_type`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 创建关注表
CREATE TABLE `follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `follow_user_id` BIGINT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`),
  CONSTRAINT `fk_follow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_follow_user` FOREIGN KEY (`follow_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- 创建消息表
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `from_user_id` BIGINT NOT NULL,
  `to_user_id` BIGINT NOT NULL,
  `target_id` BIGINT DEFAULT NULL,
  `target_type` VARCHAR(50) DEFAULT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `content` TEXT DEFAULT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `is_read` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_from_user_id` (`from_user_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_is_read` (`is_read`),
  CONSTRAINT `fk_message_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_message_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 创建通知表
CREATE TABLE `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `type` VARCHAR(50) DEFAULT NULL,
  `is_read` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 创建用户收藏表
CREATE TABLE `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `target_type` VARCHAR(50) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 创建权限表
CREATE TABLE `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `module` VARCHAR(100) DEFAULT NULL,
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_module` (`module`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 创建用户角色关联表
CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `role_code` VARCHAR(50) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_code`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_code` (`role_code`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `permission_code` VARCHAR(50) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_code`, `permission_code`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 创建用户权限关联表
CREATE TABLE `user_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_permission` (`user_id`, `permission_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_user_permission_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限关联表';

-- 创建系统配置表
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL,
  `config_value` TEXT DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 创建系统日志表
CREATE TABLE `system_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `operation` VARCHAR(255) NOT NULL,
  `ip` VARCHAR(50) DEFAULT NULL,
  `user_agent` TEXT DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 插入默认权限数据
INSERT IGNORE INTO `permission` (`name`, `code`, `description`, `module`, `status`) VALUES
('系统管理', 'system', '系统相关管理权限', 'system', 1),
('用户管理', 'user_manage', '用户相关管理权限', 'user', 1),
('角色管理', 'role_manage', '角色相关管理权限', 'role', 1),
('系统设置', 'system_setting', '系统设置权限', 'system', 1),
('内容管理', 'content', '内容相关管理权限', 'content', 1),
('项目管理', 'project_manage', '项目相关管理权限', 'project', 1),
('服务管理', 'service_manage', '服务相关管理权限', 'service', 1),
('资源管理', 'resource_manage', '资源相关管理权限', 'resource', 1),
('审核管理', 'audit', '审核相关管理权限', 'audit', 1),
('项目审核', 'project_audit', '项目审核权限', 'audit', 1),
('服务审核', 'service_audit', '服务审核权限', 'audit', 1),
('统计分析', 'stats', '统计相关权限', 'stats', 1),
('用户统计', 'user_stats', '用户统计权限', 'stats', 1),
('项目统计', 'project_stats', '项目统计权限', 'stats', 1);

-- 插入默认角色权限关联数据
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

-- 插入默认管理员用户
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`, `real_name`, `role`, `status`, `deleted`) VALUES
('admin', 'Admin@1234', 'admin@platform.com', '13800000000', '系统管理员', 'admin', 1, 0);

-- 为管理员分配角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_code`) VALUES
((SELECT id FROM `user` WHERE username = 'admin'), 'admin');

-- 插入系统配置数据
INSERT IGNORE INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('system_name', '一站式创业者综合服务平台', '系统名称'),
('system_version', '1.0.0', '系统版本'),
('test_data_inserted', 'false', '标记测试数据是否已插入');

-- 插入测试用户
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`, `real_name`, `role`, `status`, `deleted`) VALUES
('founder1', 'Admin@1234', 'founder1@platform.com', '13800000001', '张创业', 'founder', 1, 0),
('investor1', 'Admin@1234', 'investor1@platform.com', '13800000002', '李投资', 'investor', 1, 0),
('provider1', 'Admin@1234', 'provider1@platform.com', '13800000003', '王服务', 'provider', 1, 0);

-- 为测试用户分配角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_code`) VALUES
((SELECT id FROM `user` WHERE username = 'founder1'), 'founder'),
((SELECT id FROM `user` WHERE username = 'investor1'), 'investor'),
((SELECT id FROM `user` WHERE username = 'provider1'), 'provider');

-- 插入测试项目数据
INSERT IGNORE INTO `project` (`title`, `description`, `founder_id`, `industry`, `stage`, `funding_needed`, `equity_offered`, `status`, `view_count`, `like_count`, `project_image`, `deleted`) VALUES
('AI智能客服系统', '基于大语言模型的企业级客服解决方案，支持多渠道接入和智能知识库管理。', (SELECT id FROM `user` WHERE username = 'founder1'), '人工智能', '开发中', 800.00, 15.00, 'online', 150, 35, '1.png', 0),
('区块链供应链管理', '利用区块链技术实现供应链透明化和溯源，降低企业运营成本。', (SELECT id FROM `user` WHERE username = 'founder1'), '区块链', 'idea', 1200.00, 20.00, 'online', 80, 12, '2.png', 0),
('元宇宙教育平台', '沉浸式虚拟学习环境，提供互动式课程和虚拟教室。', (SELECT id FROM `user` WHERE username = 'founder1'), '教育', '开发中', 1500.00, 18.00, 'online', 200, 45, '3.png', 0),
('智能健康监测设备', '可穿戴设备与AI健康分析系统，提供个性化健康建议。', (SELECT id FROM `user` WHERE username = 'founder1'), '医疗健康', '已上线', 600.00, 10.00, 'online', 250, 60, '1.png', 0),
('环保材料研发', '新型可降解材料的研发与应用，助力企业实现绿色转型。', (SELECT id FROM `user` WHERE username = 'founder1'), '新材料', 'idea', 900.00, 12.00, 'online', 60, 8, '2.png', 0);

-- 插入测试服务数据
INSERT IGNORE INTO `resource` (`title`, `description`, `provider_id`, `provider_name`, `category`, `type`, `price`, `status`, `service_cycle`, `rating`, `resource_image`, `deleted`) VALUES
('技术咨询服务', '为初创企业提供技术架构设计和技术选型咨询。', (SELECT id FROM `user` WHERE username = 'provider1'), '王服务', '技术咨询', 'service', 2000.00, 'online', '按次', 4.70, '3.png', 0),
('商业计划书撰写', '专业的商业计划书撰写与优化服务，助力融资。', (SELECT id FROM `user` WHERE username = 'provider1'), '王服务', '咨询服务', 'service', 5000.00, 'online', '15天', 4.90, '1.png', 0),
('市场调研分析', '针对特定行业的市场调研和竞争分析服务。', (SELECT id FROM `user` WHERE username = 'provider1'), '王服务', '市场服务', 'service', 3500.00, 'online', '20天', 4.60, '1.png', 0),
('产品原型设计', '基于用户需求的产品原型设计和用户体验优化。', (SELECT id FROM `user` WHERE username = 'provider1'), '王服务', '设计服务', 'service', 4000.00, 'online', '10天', 4.80, '2.png', 0),
('融资对接服务', '为创业项目对接合适的投资机构和投资人。', (SELECT id FROM `user` WHERE username = 'provider1'), '王服务', '金融服务', 'service', 8000.00, 'online', '按需', 4.50, '3.png', 0);

-- 插入测试社区帖子数据
INSERT IGNORE INTO `post` (`user_id`, `title`, `content`, `type`, `category`, `tags`, `view_count`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`, `deleted`) VALUES
((SELECT id FROM `user` WHERE username = 'founder1'), '如何撰写一份优秀的商业计划书？', '撰写商业计划书是创业的重要步骤，需要包含市场分析、商业模式、财务规划等内容。大家有什么好的建议吗？', 'question', '创业指导', '商业计划,融资', 120, 15, 8, 1, NOW(), NOW(), 0),
((SELECT id FROM `user` WHERE username = 'investor1'), '2026年最值得投资的创业方向', '根据市场趋势，人工智能、清洁能源、健康科技等领域具有巨大潜力。大家怎么看？', 'article', '投资分析', '投资趋势,创业方向', 200, 25, 12, 1, NOW(), NOW(), 0),
((SELECT id FROM `user` WHERE username = 'provider1'), '技术咨询服务的重要性', '专业的技术咨询可以帮助初创企业避免许多技术陷阱，加速产品开发。', 'article', '技术服务', '技术咨询,创业支持', 80, 8, 5, 1, NOW(), NOW(), 0),
((SELECT id FROM `user` WHERE username = 'founder1'), '寻找技术合伙人的经验分享', '我在寻找技术合伙人的过程中遇到了很多挑战，最终找到了合适的伙伴。分享一些经验...', 'article', '团队建设', '合伙人,团队', 150, 20, 10, 1, NOW(), NOW(), 0),
((SELECT id FROM `user` WHERE username = 'investor1'), '早期项目评估的关键因素', '作为投资人，我关注的不仅是商业模式，还有团队执行力、市场潜力等因素。', 'article', '投资分析', '项目评估,投资标准', 180, 22, 15, 1, NOW(), NOW(), 0);

-- 插入测试文章数据
INSERT IGNORE INTO `article` (`title`, `content`, `url`, `source`, `author`, `publication_date`, `content_summary`, `category`, `relevance_score`, `tags`, `image_url`, `created_at`, `updated_at`) VALUES
('2026年创业趋势分析', '2026年，人工智能、绿色科技、数字健康等领域将成为创业热点...', 'https://example.com/article1', '创业邦', '张三', '2026-03-01', '2026年创业趋势分析，人工智能、绿色科技、数字健康等领域将成为热点', '创业趋势', 0.95, '创业,趋势,2026', 'https://example.com/image1.jpg', NOW(), NOW()),
('如何获得天使投资', '获得天使投资需要准备充分的商业计划书，找到合适的投资人...', 'https://example.com/article2', '投资界', '李四', '2026-03-15', '获得天使投资的关键步骤和注意事项', '融资指南', 0.90, '融资,天使投资,创业', 'https://example.com/image2.jpg', NOW(), NOW()),
('技术创业的成功要素', '技术创业需要技术创新、市场需求、团队执行力等多个要素...', 'https://example.com/article3', '科技日报', '王五', '2026-03-20', '技术创业的成功要素分析', '技术创业', 0.88, '技术,创业,成功', 'https://example.com/image3.jpg', NOW(), NOW()),
('创业公司的团队建设', '优秀的团队是创业成功的关键，需要合理的分工和有效的沟通...', 'https://example.com/article4', '管理界', '赵六', '2026-03-25', '创业公司团队建设的重要性和方法', '团队管理', 0.85, '团队,管理,创业', 'https://example.com/image4.jpg', NOW(), NOW()),
('数字营销的新趋势', '2026年，内容营销、短视频、AI营销等将成为数字营销的新趋势...', 'https://example.com/article5', '营销周报', '孙七', '2026-04-01', '2026年数字营销的新趋势分析', '营销策略', 0.82, '营销,数字营销,趋势', 'https://example.com/image5.jpg', NOW(), NOW());

-- 标记测试数据已插入
UPDATE `system_config` SET `config_value` = 'true' WHERE `config_key` = 'test_data_inserted';

-- 脚本执行完成
SELECT '数据库重建脚本执行完成' AS message;