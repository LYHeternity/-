-- 创建创业资讯文章表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `content` TEXT NOT NULL COMMENT '文章内容',
  `url` VARCHAR(500) NOT NULL COMMENT '文章链接',
  `source` VARCHAR(100) NOT NULL COMMENT '文章来源',
  `publication_date` VARCHAR(100) COMMENT '发布日期',
  `content_summary` TEXT COMMENT '内容摘要',
  `category` VARCHAR(100) NOT NULL COMMENT '文章分类',
  `relevance_score` DOUBLE DEFAULT 0.0 COMMENT '相关度分数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_url` (`url`),
  KEY `idx_category` (`category`),
  KEY `idx_relevance_score` (`relevance_score`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创业资讯文章表';

-- 创建文章收藏表
CREATE TABLE IF NOT EXISTS `article_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';