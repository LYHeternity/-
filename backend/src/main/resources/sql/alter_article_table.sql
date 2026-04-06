-- 为article表添加缺失的字段
ALTER TABLE article
ADD COLUMN author VARCHAR(255) DEFAULT '未知作者' COMMENT '作者',
ADD COLUMN tags VARCHAR(512) DEFAULT '' COMMENT '标签',
ADD COLUMN image_url VARCHAR(512) DEFAULT '' COMMENT '图片URL';

-- 查看表结构
DESCRIBE article;