-- =============================================
-- 一站式创业者综合服务平台 - 建表脚本
-- MySQL 8.0，11张核心表 + 必要扩展表
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(128) NOT NULL COMMENT '密码(加密)',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(32) NOT NULL COMMENT '角色: entrepreneur/investor/provider/admin',
  `avatar` varchar(256) DEFAULT NULL COMMENT '头像本地路径',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态: 0禁用 1启用 2注销',
  `salt` varchar(32) DEFAULT NULL COMMENT '密码盐',
  `industry` varchar(64) DEFAULT NULL COMMENT '创业/投资领域(创业者/投资人)',
  `investment_amount` varchar(64) DEFAULT NULL COMMENT '投资额度(投资人)',
  `company_name` varchar(128) DEFAULT NULL COMMENT '企业名称(服务商)',
  `contact_person` varchar(64) DEFAULT NULL COMMENT '联系人(服务商)',
  `service_type` varchar(64) DEFAULT NULL COMMENT '服务类型(服务商)',
  `service_intro` text COMMENT '服务介绍(服务商)',
  `intro` text COMMENT '个人/企业简介',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 2. 项目表
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '项目名称',
  `description` text COMMENT '项目详情(富文本)',
  `founder_id` bigint NOT NULL COMMENT '创业者ID',
  `category` varchar(64) DEFAULT NULL COMMENT '分类',
  `industry` varchar(64) DEFAULT NULL COMMENT '所属行业',
  `stage` varchar(32) DEFAULT NULL COMMENT '阶段: idea/开发中/已上线',
  `funding_needed` decimal(20,2) DEFAULT NULL COMMENT '融资需求(万)',
  `equity_offered` decimal(5,2) DEFAULT NULL COMMENT '出让股权比例%',
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT 'pending/online/offline/rejected',
  `view_count` int NOT NULL DEFAULT 0,
  `like_count` int NOT NULL DEFAULT 0,
  `project_image` varchar(512) DEFAULT NULL COMMENT '封面图路径',
  `attachments` text COMMENT '附件路径JSON数组',
  `tags` varchar(255) DEFAULT NULL COMMENT '项目标签',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_founder` (`founder_id`),
  KEY `idx_status` (`status`),
  KEY `idx_industry` (`industry`),
  KEY `idx_stage` (`stage`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- ----------------------------
-- 3. 资源/服务表
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '服务名称',
  `description` text COMMENT '服务详情(富文本)',
  `provider_id` bigint NOT NULL COMMENT '服务商ID',
  `provider_name` varchar(128) DEFAULT NULL COMMENT '服务商名称',
  `category` varchar(64) DEFAULT NULL COMMENT '分类',
  `type` varchar(32) DEFAULT 'service' COMMENT 'service/tool/knowledge',
  `price` decimal(20,2) DEFAULT NULL COMMENT '价格',
  `contact_info` varchar(256) DEFAULT NULL,
  `view_count` int NOT NULL DEFAULT 0,
  `favorite_count` int NOT NULL DEFAULT 0,
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT 'pending/online/offline/rejected',
  `resource_image` varchar(512) DEFAULT NULL,
  `attachments` text COMMENT '附件JSON',
  `service_cycle` varchar(64) DEFAULT NULL COMMENT '服务周期',
  `rating` decimal(3,2) DEFAULT NULL COMMENT '好评率/评分',
  `tags` varchar(255) DEFAULT NULL COMMENT '服务标签',
  `version` varchar(50) DEFAULT NULL COMMENT '服务版本',
  `deployment_env` varchar(50) DEFAULT NULL COMMENT '部署环境',
  `summary` varchar(255) DEFAULT NULL COMMENT '服务简介',
  `api_definition` text COMMENT '接口定义',
  `dependencies` text COMMENT '服务依赖',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_provider` (`provider_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源/服务表';

-- ----------------------------
-- 4. 消息表(含私信、点赞、评论、业务消息等)
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint DEFAULT NULL COMMENT '发送者ID，系统消息为NULL',
  `to_user_id` bigint NOT NULL COMMENT '接收者ID',
  `target_id` bigint DEFAULT NULL COMMENT '关联目标ID(项目/服务/意向等)',
  `target_type` varchar(32) DEFAULT NULL COMMENT 'project/resource/investment/order',
  `title` varchar(200) DEFAULT NULL,
  `content` text COMMENT '内容',
  `type` varchar(32) NOT NULL COMMENT 'system/interaction/business/chat/like/comment',
  `is_read` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_to_user` (`to_user_id`),
  KEY `idx_from_user` (`from_user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ----------------------------
-- 5. 投资表(投资意向与记录)
-- ----------------------------
DROP TABLE IF EXISTS `investment`;
CREATE TABLE `investment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `investor_id` bigint NOT NULL,
  `amount` decimal(20,2) NOT NULL COMMENT '投资金额',
  `duration` varchar(64) DEFAULT NULL COMMENT '投资期限',
  `description` text COMMENT '投资条件/说明',
  `attachment_path` varchar(512) DEFAULT NULL COMMENT '投资方案附件',
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected/completed',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝理由',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`),
  KEY `idx_investor` (`investor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投资表';

-- ----------------------------
-- 6. 服务订单表
-- ----------------------------
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL COMMENT '创业者ID',
  `provider_id` bigint NOT NULL COMMENT '服务商ID',
  `amount` decimal(20,2) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT 'pending/confirmed/serving/completed/cancelled',
  `description` text COMMENT '需求说明',
  `expect_time` varchar(128) DEFAULT NULL COMMENT '期望时间',
  `reject_reason` varchar(500) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_resource` (`resource_id`),
  KEY `idx_buyer` (`buyer_id`),
  KEY `idx_provider` (`provider_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单表';

-- ----------------------------
-- 7. 通知表(系统通知)
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` text,
  `type` varchar(32) DEFAULT NULL COMMENT '注册/审核/状态变更等',
  `is_read` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ----------------------------
-- 8. 系统日志表
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `level` varchar(16) DEFAULT NULL COMMENT 'INFO/WARN/ERROR',
  `module` varchar(64) DEFAULT NULL,
  `username` varchar(64) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `action` varchar(128) DEFAULT NULL,
  `message` text,
  `ip` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_user` (`user_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- ----------------------------
-- 9. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(128) NOT NULL,
  `config_value` text,
  `description` varchar(256) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- 10. 权限表
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `code` varchar(128) NOT NULL COMMENT '权限代码，如 project:publish',
  `description` varchar(256) DEFAULT NULL,
  `module` varchar(64) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ----------------------------
-- 11. 用户权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_permission` (`user_id`,`permission_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限关联表';

-- 扩展：项目进展表(便于查询)
DROP TABLE IF EXISTS `project_progress`;
CREATE TABLE `project_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `content` text NOT NULL,
  `attachments` text COMMENT '附件JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目进展';

-- 扩展：收藏表(用户收藏项目/服务)
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_type` varchar(32) NOT NULL COMMENT 'project/resource',
  `target_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏';

-- 扩展：评价表(服务订单评价)
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `resource_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` tinyint NOT NULL COMMENT '1-5星',
  `content` text,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_resource` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价';

SET FOREIGN_KEY_CHECKS = 1;
