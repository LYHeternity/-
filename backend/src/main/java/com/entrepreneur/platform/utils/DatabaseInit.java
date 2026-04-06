package com.entrepreneur.platform.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库初始化工具类
 * 用于创建用户角色和角色权限关联表，以及插入测试数据
 */
public class DatabaseInit {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/entrepreneur_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            // 创建用户角色关联表
            String createUserRoleTable = "CREATE TABLE IF NOT EXISTS `user_role` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT," +
                    "`user_id` BIGINT NOT NULL," +
                    "`role_code` VARCHAR(50) NOT NULL," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_user_role` (`user_id`, `role_code`)," +
                    "INDEX `idx_user_id` (`user_id`)," +
                    "INDEX `idx_role_code` (`role_code`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            // 创建角色权限关联表
            String createRolePermissionTable = "CREATE TABLE IF NOT EXISTS `role_permission` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT," +
                    "`role_code` VARCHAR(50) NOT NULL," +
                    "`permission_code` VARCHAR(50) NOT NULL," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_role_permission` (`role_code`, `permission_code`)," +
                    "INDEX `idx_role_code` (`role_code`)," +
                    "INDEX `idx_permission_code` (`permission_code`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

            // 执行创建表的SQL语句
            statement.executeUpdate(createUserRoleTable);
            System.out.println("Created user_role table successfully");

            statement.executeUpdate(createRolePermissionTable);
            System.out.println("Created role_permission table successfully");

            // 插入默认角色权限数据
            String[] permissionInserts = {
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'system')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'user_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'role_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'system_setting')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'service_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'resource_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'service_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'user_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('admin', 'project_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'project_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('auditor', 'service_audit')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'user_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('analyst', 'project_stats')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'project_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('operator', 'service_manage')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('customer_service', 'content')",
                    "INSERT IGNORE INTO `role_permission` (`role_code`, `permission_code`) VALUES ('customer_service', 'service_manage')"
            };

            for (String insert : permissionInserts) {
                statement.executeUpdate(insert);
            }
            System.out.println("Inserted default role permissions successfully");

            // 插入测试数据
            System.out.println("\nInserting test data for founder1...");
            insertTestData(connection, statement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertTestData(Connection connection, Statement statement) throws Exception {
        // 为founder1 (user_id=1) 插入测试项目
        String[] projectInserts = {
            "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('AI智能客服系统','基于大语言模型的企业级客服解决方案，支持多渠道接入和智能知识库管理。',1,'人工智能','开发中',800.00,15.00,'online',150,35,NOW(),NOW(),0)",
            "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('区块链供应链管理','利用区块链技术实现供应链透明化和溯源，降低企业运营成本。',1,'区块链','idea',1200.00,20.00,'pending',80,12,NOW(),NOW(),0)",
            "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('元宇宙教育平台','沉浸式虚拟学习环境，提供互动式课程和虚拟教室。',1,'教育','开发中',1500.00,18.00,'online',200,45,NOW(),NOW(),0)" ,
            "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('智能健康监测设备','可穿戴设备与AI健康分析系统，提供个性化健康建议。',1,'医疗健康','已上线',600.00,10.00,'online',250,60,NOW(),NOW(),0)" ,
            "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('环保材料研发','新型可降解材料的研发与应用，助力企业实现绿色转型。',1,'新材料','idea',900.00,12.00,'pending',60,8,NOW(),NOW(),0)"
        };

        for (String insert : projectInserts) {
            statement.executeUpdate(insert);
            System.out.println("Inserted project: " + insert.substring(insert.indexOf('(') + 1, insert.indexOf(',')));
        }

        // 为founder1 (user_id=1) 插入测试服务
        String[] serviceInserts = {
            "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('技术咨询服务','为初创企业提供技术架构设计和技术选型咨询。',1,'张创业','技术咨询','service',2000.00,'online','按次',4.70,NOW(),NOW(),0)" ,
            "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('商业计划书撰写','专业的商业计划书撰写与优化服务，助力融资。',1,'张创业','咨询服务','service',5000.00,'online','15天',4.90,NOW(),NOW(),0)" ,
            "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('市场调研分析','针对特定行业的市场调研和竞争分析服务。',1,'张创业','市场服务','service',3500.00,'online','20天',4.60,NOW(),NOW(),0)" ,
            "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('产品原型设计','基于用户需求的产品原型设计和用户体验优化。',1,'张创业','设计服务','service',4000.00,'online','10天',4.80,NOW(),NOW(),0)" ,
            "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('融资对接服务','为创业项目对接合适的投资机构和投资人。',1,'张创业','金融服务','service',8000.00,'online','按需',4.50,NOW(),NOW(),0)"
        };

        for (String insert : serviceInserts) {
            statement.executeUpdate(insert);
            System.out.println("Inserted service: " + insert.substring(insert.indexOf('(') + 1, insert.indexOf(',')));
        }

        // 为项目添加进展记录
        String[] progressInserts = {
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='AI智能客服系统' AND founder_id=1),'核心功能开发','完成了智能问答和多渠道接入功能的开发。',NOW(),NOW(),NOW(),0)" ,
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='AI智能客服系统' AND founder_id=1),'知识库搭建','完成了行业知识库的初步搭建和训练。',NOW(),NOW(),NOW(),0)" ,
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='元宇宙教育平台' AND founder_id=1),'虚拟教室开发','完成了基础虚拟教室环境的搭建。',NOW(),NOW(),NOW(),0)" ,
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='元宇宙教育平台' AND founder_id=1),'课程内容制作','完成了首批互动式课程的制作。',NOW(),NOW(),NOW(),0)" ,
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='智能健康监测设备' AND founder_id=1),'硬件原型','完成了第一代硬件原型的开发和测试。',NOW(),NOW(),NOW(),0)" ,
            "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='智能健康监测设备' AND founder_id=1),'软件系统','完成了健康数据分析系统的开发。',NOW(),NOW(),NOW(),0)"
        };

        for (String insert : progressInserts) {
            statement.executeUpdate(insert);
            System.out.println("Inserted project progress");
        }

        System.out.println("\nTest data inserted successfully!");
    }
}
