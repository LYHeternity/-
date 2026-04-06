-- 一站式创业者综合服务平台 - 数据库完整性检查与优化脚本
-- 执行顺序：在数据库初始化后执行此脚本

-- 切换到platform数据库
USE platform;

-- =============================================
-- 1. 数据一致性检查
-- =============================================

-- 1.1 检查外键约束
SELECT '=== 外键约束检查 ===' AS section;

-- 检查用户相关的外键约束
SELECT '用户ID存在性检查' AS check_type,
       COUNT(*) AS error_count
FROM post
WHERE user_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- 检查评论相关的外键约束
SELECT '评论用户ID存在性检查' AS check_type,
       COUNT(*) AS error_count
FROM comment
WHERE user_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- 检查点赞相关的外键约束
SELECT '点赞用户ID存在性检查' AS check_type,
       COUNT(*) AS error_count
FROM `like`
WHERE user_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- 检查关注相关的外键约束
SELECT '关注者ID存在性检查' AS check_type,
       COUNT(*) AS error_count
FROM follow
WHERE follower_id NOT IN (SELECT id FROM user WHERE deleted = 0);

SELECT '被关注者ID存在性检查' AS check_type,
       COUNT(*) AS error_count
FROM follow
WHERE followed_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- 1.2 检查唯一约束
SELECT '=== 唯一约束检查 ===' AS section;

-- 检查用户名唯一性
SELECT '用户名唯一性检查' AS check_type,
       username,
       COUNT(*) AS duplicate_count
FROM user
GROUP BY username
HAVING COUNT(*) > 1;

-- 检查权限代码唯一性
SELECT '权限代码唯一性检查' AS check_type,
       code,
       COUNT(*) AS duplicate_count
FROM permission
GROUP BY code
HAVING COUNT(*) > 1;

-- 检查系统配置键唯一性
SELECT '系统配置键唯一性检查' AS check_type,
       config_key,
       COUNT(*) AS duplicate_count
FROM system_config
GROUP BY config_key
HAVING COUNT(*) > 1;

-- 1.3 检查业务规则约束
SELECT '=== 业务规则约束检查 ===' AS section;

-- 检查用户状态有效性
SELECT '用户状态有效性检查' AS check_type,
       status,
       COUNT(*) AS invalid_count
FROM user
WHERE status NOT IN (0, 1);

-- 检查帖子状态有效性
SELECT '帖子状态有效性检查' AS check_type,
       status,
       COUNT(*) AS invalid_count
FROM post
WHERE status NOT IN (0, 1, 2);

-- 检查帖子类型有效性
SELECT '帖子类型有效性检查' AS check_type,
       type,
       COUNT(*) AS invalid_count
FROM post
WHERE type NOT IN ('article', 'question');

-- =============================================
-- 2. 索引使用情况分析
-- =============================================

SELECT '=== 索引使用情况分析 ===' AS section;

-- 2.1 查看所有索引
SHOW INDEX FROM user;
SHOW INDEX FROM post;
SHOW INDEX FROM comment;
SHOW INDEX FROM `like`;
SHOW INDEX FROM follow;

-- 2.2 分析索引使用情况（需要MySQL 5.6+）
-- 注意：此查询需要开启performance_schema
-- SET GLOBAL performance_schema = ON;

-- 查看索引使用统计
-- SELECT 
--     OBJECT_SCHEMA,
--     OBJECT_NAME,
--     INDEX_NAME,
--     COUNT_FETCHES AS fetch_count,
--     COUNT_SCANS AS scan_count,
--     COUNT_RANGE AS range_count,
--     COUNT_INDEX_SKIP_SCANS AS skip_scan_count
-- FROM performance_schema.table_io_waits_summary_by_index_usage
-- WHERE OBJECT_SCHEMA = 'platform'
-- ORDER BY COUNT_FETCHES + COUNT_SCANS + COUNT_RANGE DESC;

-- =============================================
-- 3. 数据冗余及异常值检查
-- =============================================

SELECT '=== 数据冗余及异常值检查 ===' AS section;

-- 3.1 检查重复数据
SELECT '用户表重复数据检查' AS check_type,
       username,
       email,
       phone,
       COUNT(*) AS duplicate_count
FROM user
GROUP BY username, email, phone
HAVING COUNT(*) > 1;

-- 3.2 检查缺失值
SELECT '用户表缺失值检查' AS check_type,
       '用户名' AS field,
       COUNT(*) AS missing_count
FROM user
WHERE username IS NULL OR username = ''
UNION
SELECT '用户表缺失值检查' AS check_type,
       '密码' AS field,
       COUNT(*) AS missing_count
FROM user
WHERE password IS NULL OR password = ''
UNION
SELECT '帖子表缺失值检查' AS check_type,
       '标题' AS field,
       COUNT(*) AS missing_count
FROM post
WHERE title IS NULL OR title = ''
UNION
SELECT '帖子表缺失值检查' AS check_type,
       '内容' AS field,
       COUNT(*) AS missing_count
FROM post
WHERE content IS NULL OR content = '';

-- 3.3 检查不合理数据
SELECT '用户表不合理数据检查' AS check_type,
       '密码长度' AS field,
       COUNT(*) AS invalid_count
FROM user
WHERE LENGTH(password) < 6
UNION
SELECT '帖子表不合理数据检查' AS check_type,
       '标题长度' AS field,
       COUNT(*) AS invalid_count
FROM post
WHERE LENGTH(title) < 2 OR LENGTH(title) > 255
UNION
SELECT '帖子表不合理数据检查' AS check_type,
       '内容长度' AS field,
       COUNT(*) AS invalid_count
FROM post
WHERE LENGTH(content) < 10;

-- =============================================
-- 4. 数据库性能评估
-- =============================================

SELECT '=== 数据库性能评估 ===' AS section;

-- 4.1 分析表状态
SHOW TABLE STATUS FROM platform;

-- 4.2 分析慢查询（需要开启慢查询日志）
-- 注意：此部分需要在MySQL配置中开启慢查询日志
-- 例如：
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 1;

-- 4.3 分析表大小
SELECT 
    table_name AS '表名',
    round(((data_length + index_length) / 1024 / 1024), 2) AS '总大小(MB)',
    round((data_length / 1024 / 1024), 2) AS '数据大小(MB)',
    round((index_length / 1024 / 1024), 2) AS '索引大小(MB)',
    table_rows AS '行数'
FROM information_schema.tables 
WHERE table_schema = 'platform'
ORDER BY (data_length + index_length) DESC;

-- =============================================
-- 5. 优化建议与修复脚本
-- =============================================

SELECT '=== 优化建议与修复脚本 ===' AS section;

-- 5.1 索引优化建议
-- 为经常查询的字段添加索引

-- 为帖子表的created_at字段添加索引（已在创建表时添加）
-- CREATE INDEX idx_created_at ON post(created_at);

-- 为评论表的target_id和target_type添加组合索引（已在创建表时添加）
-- CREATE INDEX idx_target ON comment(target_id, target_type);

-- 5.2 数据清理建议

-- 清理已删除的用户数据
DELIMITER //
CREATE PROCEDURE clean_deleted_users()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE user_id BIGINT;
    DECLARE cur CURSOR FOR SELECT id FROM user WHERE deleted = 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop:
    LOOP
        FETCH cur INTO user_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 删除相关数据
        DELETE FROM comment WHERE user_id = user_id;
        DELETE FROM `like` WHERE user_id = user_id;
        DELETE FROM follow WHERE follower_id = user_id OR followed_id = user_id;
        DELETE FROM message WHERE sender_id = user_id OR receiver_id = user_id;
        DELETE FROM notification WHERE user_id = user_id;
        DELETE FROM user_favorite WHERE user_id = user_id;
        
    END LOOP;
    
    CLOSE cur;
    
    -- 优化表
    OPTIMIZE TABLE user, comment, `like`, follow, message, notification, user_favorite;
    
    SELECT '清理完成' AS result;
END //
DELIMITER ;

-- 5.3 性能优化建议

-- 优化帖子查询
-- 为常用查询添加覆盖索引
CREATE INDEX idx_post_status_created ON post(status, created_at);

-- 优化用户查询
CREATE INDEX idx_user_role_status ON user(role, status);

-- 5.4 数据一致性修复

-- 修复帖子表中的无效用户ID
UPDATE post
SET user_id = 1 -- 默认管理员用户
WHERE user_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- 修复评论表中的无效用户ID
UPDATE comment
SET user_id = 1 -- 默认管理员用户
WHERE user_id NOT IN (SELECT id FROM user WHERE deleted = 0);

-- =============================================
-- 6. 执行前后的数据校验
-- =============================================

SELECT '=== 执行前后的数据校验 ===' AS section;

-- 6.1 执行前校验
SELECT '执行前 - 帖子总数' AS check_type,
       COUNT(*) AS count
FROM post;

SELECT '执行前 - 评论总数' AS check_type,
       COUNT(*) AS count
FROM comment;

-- 6.2 执行后校验（手动执行）
-- SELECT '执行后 - 帖子总数' AS check_type,
--        COUNT(*) AS count
-- FROM post;
-- 
-- SELECT '执行后 - 评论总数' AS check_type,
--        COUNT(*) AS count
-- FROM comment;

-- =============================================
-- 7. 执行清理与优化
-- =============================================

-- 执行清理存储过程
-- CALL clean_deleted_users();

-- 优化所有表
OPTIMIZE TABLE user, post, comment, `like`, follow, message, notification, user_favorite, system_log;

-- 分析所有表
ANALYZE TABLE user, post, comment, `like`, follow, message, notification, user_favorite, system_log;

-- 脚本执行完成
SELECT '数据库完整性检查与优化完成' AS result;