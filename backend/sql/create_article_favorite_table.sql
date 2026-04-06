-- 创建文章收藏表
CREATE TABLE IF NOT EXISTS `article_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`),
  CONSTRAINT `fk_article_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';
