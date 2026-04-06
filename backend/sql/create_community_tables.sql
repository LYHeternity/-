-- 创建帖子表
CREATE TABLE `post` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `type` VARCHAR(50) NOT NULL COMMENT 'article/question',
  `category` VARCHAR(100) NOT NULL,
  `tags` VARCHAR(255) DEFAULT NULL,
  `view_count` INT(11) DEFAULT 0,
  `like_count` INT(11) DEFAULT 0,
  `comment_count` INT(11) DEFAULT 0,
  `status` INT(11) DEFAULT 1 COMMENT '0-待审核，1-已发布，2-已禁用',
  `best_answer_id` BIGINT(20) DEFAULT NULL COMMENT '最佳答案ID（仅问题类型）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- 创建评论表
CREATE TABLE `comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT(20) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父评论ID（用于回复）',
  `content` TEXT NOT NULL,
  `like_count` INT(11) DEFAULT 0,
  `is_answer` INT(11) DEFAULT 0 COMMENT '是否为回答（仅问题类型帖子）',
  `status` INT(11) DEFAULT 1 COMMENT '0-待审核，1-已发布，2-已禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 创建点赞表
CREATE TABLE `like` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `target_id` BIGINT(20) NOT NULL COMMENT '目标ID（帖子ID或评论ID）',
  `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型（post/comment）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`,`deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_id`,`target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 创建关注表
CREATE TABLE `follow` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL COMMENT '关注者ID',
  `follow_user_id` BIGINT(20) NOT NULL COMMENT '被关注者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deleted` INT(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`,`deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 为user表添加社区相关字段
ALTER TABLE `user` ADD COLUMN `follow_count` INT(11) DEFAULT 0 COMMENT '关注数';
ALTER TABLE `user` ADD COLUMN `follower_count` INT(11) DEFAULT 0 COMMENT '粉丝数';
ALTER TABLE `user` ADD COLUMN `post_count` INT(11) DEFAULT 0 COMMENT '发帖数';
ALTER TABLE `user` ADD COLUMN `comment_count` INT(11) DEFAULT 0 COMMENT '评论数';
