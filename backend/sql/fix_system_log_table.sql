-- 修复系统日志表结构
USE `entrepreneur_platform`;

-- 修改system_log表结构，添加缺失的字段并调整字段名
ALTER TABLE `system_log` 
    ADD COLUMN `level` VARCHAR(20) DEFAULT 'INFO' AFTER `user_agent`,
    ADD COLUMN `module` VARCHAR(100) DEFAULT NULL AFTER `level`,
    ADD COLUMN `action` VARCHAR(255) DEFAULT NULL AFTER `module`,
    ADD COLUMN `message` TEXT DEFAULT NULL AFTER `action`,
    ADD COLUMN `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP AFTER `message`,
    CHANGE COLUMN `operation` `operation` VARCHAR(255) DEFAULT NULL,
    ADD INDEX `idx_timestamp` (`timestamp`);

-- 同步数据：将create_time的值复制到timestamp
UPDATE `system_log` SET `timestamp` = `create_time` WHERE `timestamp` IS NULL;

-- 同步数据：将operation的值复制到action
UPDATE `system_log` SET `action` = `operation` WHERE `action` IS NULL;

-- 执行完成
SELECT '系统日志表结构修复完成' AS message;