import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 插入测试数据工具类
 * 为founder1用户插入测试项目和服务数据
 */
public class InsertTestData {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/entrepreneur_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root";

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 Statement statement = connection.createStatement()) {

                connection.setAutoCommit(false);

                // 为founder1 (user_id=1) 插入测试项目
                String project1 = "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('AI智能客服系统','基于大语言模型的企业级客服解决方案，支持多渠道接入和智能知识库管理。',1,'人工智能','开发中',800.00,15.00,'online',150,35,NOW(),NOW(),0)";
                String project2 = "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('区块链供应链管理','利用区块链技术实现供应链透明化和溯源，降低企业运营成本。',1,'区块链','idea',1200.00,20.00,'pending',80,12,NOW(),NOW(),0)";
                String project3 = "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('元宇宙教育平台','沉浸式虚拟学习环境，提供互动式课程和虚拟教室。',1,'教育','开发中',1500.00,18.00,'online',200,45,NOW(),NOW(),0)";
                String project4 = "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('智能健康监测设备','可穿戴设备与AI健康分析系统，提供个性化健康建议。',1,'医疗健康','已上线',600.00,10.00,'online',250,60,NOW(),NOW(),0)";
                String project5 = "INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES ('环保材料研发','新型可降解材料的研发与应用，助力企业实现绿色转型。',1,'新材料','idea',900.00,12.00,'pending',60,8,NOW(),NOW(),0)";

                statement.executeUpdate(project1);
                System.out.println("Inserted project: AI智能客服系统");
                statement.executeUpdate(project2);
                System.out.println("Inserted project: 区块链供应链管理");
                statement.executeUpdate(project3);
                System.out.println("Inserted project: 元宇宙教育平台");
                statement.executeUpdate(project4);
                System.out.println("Inserted project: 智能健康监测设备");
                statement.executeUpdate(project5);
                System.out.println("Inserted project: 环保材料研发");

                // 为founder1 (user_id=1) 插入测试服务
                String service1 = "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('技术咨询服务','为初创企业提供技术架构设计和技术选型咨询。',1,'张创业','技术咨询','service',2000.00,'online','按次',4.70,NOW(),NOW(),0)";
                String service2 = "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('商业计划书撰写','专业的商业计划书撰写与优化服务，助力融资。',1,'张创业','咨询服务','service',5000.00,'online','15天',4.90,NOW(),NOW(),0)";
                String service3 = "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('市场调研分析','针对特定行业的市场调研和竞争分析服务。',1,'张创业','市场服务','service',3500.00,'online','20天',4.60,NOW(),NOW(),0)";
                String service4 = "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('产品原型设计','基于用户需求的产品原型设计和用户体验优化。',1,'张创业','设计服务','service',4000.00,'online','10天',4.80,NOW(),NOW(),0)";
                String service5 = "INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES ('融资对接服务','为创业项目对接合适的投资机构和投资人。',1,'张创业','金融服务','service',8000.00,'online','按需',4.50,NOW(),NOW(),0)";

                statement.executeUpdate(service1);
                System.out.println("Inserted service: 技术咨询服务");
                statement.executeUpdate(service2);
                System.out.println("Inserted service: 商业计划书撰写");
                statement.executeUpdate(service3);
                System.out.println("Inserted service: 市场调研分析");
                statement.executeUpdate(service4);
                System.out.println("Inserted service: 产品原型设计");
                statement.executeUpdate(service5);
                System.out.println("Inserted service: 融资对接服务");

                // 为项目添加进展记录
                String progress1 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='AI智能客服系统' AND founder_id=1),'核心功能开发','完成了智能问答和多渠道接入功能的开发。',NOW(),NOW(),NOW(),0)";
                String progress2 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='AI智能客服系统' AND founder_id=1),'知识库搭建','完成了行业知识库的初步搭建和训练。',NOW(),NOW(),NOW(),0)";
                String progress3 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='元宇宙教育平台' AND founder_id=1),'虚拟教室开发','完成了基础虚拟教室环境的搭建。',NOW(),NOW(),NOW(),0)";
                String progress4 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='元宇宙教育平台' AND founder_id=1),'课程内容制作','完成了首批互动式课程的制作。',NOW(),NOW(),NOW(),0)";
                String progress5 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='智能健康监测设备' AND founder_id=1),'硬件原型','完成了第一代硬件原型的开发和测试。',NOW(),NOW(),NOW(),0)";
                String progress6 = "INSERT INTO `project_progress` (`project_id`,`title`,`description`,`progress_date`,`create_time`,`update_time`,`deleted`) VALUES ((SELECT id FROM `project` WHERE title='智能健康监测设备' AND founder_id=1),'软件系统','完成了健康数据分析系统的开发。',NOW(),NOW(),NOW(),0)";

                statement.executeUpdate(progress1);
                statement.executeUpdate(progress2);
                statement.executeUpdate(progress3);
                statement.executeUpdate(progress4);
                statement.executeUpdate(progress5);
                statement.executeUpdate(progress6);
                System.out.println("Inserted project progress records");

                connection.commit();
                System.out.println("\nTest data inserted successfully!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found. Please make sure the driver is in the classpath.");
            e.printStackTrace();
        }
    }
}
