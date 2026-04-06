-- 添加文章图片URL列
ALTER TABLE `article` ADD COLUMN `image_url` VARCHAR(500) COMMENT '文章图片URL' AFTER `relevance_score`;
