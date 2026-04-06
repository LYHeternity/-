-- =============================================
-- 初始化数据：权限、默认管理员、系统配置、测试数据
-- 默认管理员：admin / Admin@1234
-- =============================================

SET NAMES utf8mb4;

-- 默认管理员账号由后端启动时自动创建（用户名 admin，密码 Admin@1234，BCrypt 加密）
-- 见：DataInitializer.java

-- 权限数据（按角色所需）
INSERT INTO `permission` (`id`,`name`,`code`,`description`,`module`,`status`) VALUES
(1,'用户管理','admin:user','用户列表、启用禁用、重置密码','admin',1),
(2,'内容审核','admin:audit','项目与服务审核','admin',1),
(3,'系统配置','admin:config','平台与业务参数配置','admin',1),
(4,'数据统计','admin:stats','运营数据与报表','admin',1),
(5,'日志管理','admin:log','操作与系统日志','admin',1),
(6,'权限管理','admin:permission','角色与管理员权限','admin',1),
(7,'项目发布','project:publish','发布与编辑项目','project',1),
(8,'项目管理','project:manage','下架、进展更新','project',1),
(9,'投资意向','investment:intent','提交与查看意向','investment',1),
(10,'投资审核','investment:audit','审核投资意向','investment',1),
(11,'服务发布','resource:publish','发布与编辑服务','resource',1),
(12,'服务管理','resource:manage','下架、订单管理','resource',1),
(13,'服务预约','resource:book','预约服务、订单、评价','resource',1);

-- 管理员账号由后端启动时创建并分配全部权限，此处不插入

-- 系统配置默认值
INSERT INTO `system_config` (`config_key`,`config_value`,`description`) VALUES
('site_name','创业者综合服务平台','网站名称'),
('site_logo','/static/logo.png','LOGO路径'),
('copyright','Copyright © 2025','版权信息'),
('project_audit_days','7','项目审核时效(天)'),
('resource_audit_days','7','服务审核时效(天)'),
('investment_valid_days','30','投资意向有效期(天)'),
('booking_valid_days','15','服务预约有效期(天)'),
('password_strength','8','密码最少位数'),
('jwt_expire_hours','2','JWT过期时间(小时)');

-- 测试用户（密码统一 Admin@1234，与 admin 相同加密结果）
INSERT INTO `user` (`username`,`password`,`email`,`phone`,`real_name`,`role`,`status`,`industry`,`company_name`,`contact_person`,`service_type`,`service_intro`,`create_time`,`update_time`,`deleted`) VALUES
('founder1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','f1@test.com','13800000001','张创业','entrepreneur',1,'互联网',NULL,NULL,NULL,NULL,NOW(),NOW(),0),
('investor1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','i1@test.com','13800000002','李投资','investor',1,'科技',NULL,NULL,NULL,NULL,NOW(),NOW(),0),
('provider1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','p1@test.com','13800000003','王服务','provider',1,NULL,'XX科技有限公司','王经理','技术开发','专业软件开发',NOW(),NOW(),0);

-- 为测试用户分配角色对应权限（founder1=1, investor1=2, provider1=3）
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 1, id FROM `permission` WHERE code IN ('project:publish','project:manage','investment:intent','investment:audit','resource:book');
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 2, id FROM `permission` WHERE code IN ('investment:intent','resource:book');
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 3, id FROM `permission` WHERE code IN ('resource:publish','resource:manage','resource:book');

-- 测试项目(创业者 founder1 id=1)
INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES
('智能教育平台','基于AI的个性化学习系统，面向K12。',1,'教育','开发中',500.00,10.00,'online',100,20,NOW(),NOW(),0),
('绿色能源方案','清洁能源技术应用与解决方案。',1,'能源','idea',1000.00,15.00,'pending',10,2,NOW(),NOW(),0);

-- 测试服务(服务商 provider1 id=3)
INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES
('企业官网开发','定制化企业官网与后台系统。',3,'XX科技有限公司','技术开发','service',30000.00,'online','30天',4.80,NOW(),NOW(),0),
('法律咨询','创业公司股权与合同法律咨询。',3,'XX科技有限公司','法律咨询','service',500.00,'online','按次',4.50,NOW(),NOW(),0);

-- 测试投资意向(投资人 investor1 id=2 对项目1)
INSERT INTO `investment` (`project_id`,`investor_id`,`amount`,`duration`,`description`,`status`,`create_time`,`update_time`,`deleted`) VALUES
(1,2,200.00,'3年','希望参与A轮，侧重产品与市场。','pending',NOW(),NOW(),0);

-- 测试服务订单(创业者1 预约服务1，服务商3)
INSERT INTO `service_order` (`resource_id`,`buyer_id`,`provider_id`,`amount`,`status`,`description`,`expect_time`,`create_time`,`update_time`,`deleted`) VALUES
(1,1,3,30000.00,'confirmed','需要官网+后台管理，期望3月上线。','2025-03',NOW(),NOW(),0);

-- 测试通知
INSERT INTO `notification` (`user_id`,`title`,`content`,`type`,`is_read`) VALUES
(1,'注册成功','欢迎加入创业者综合服务平台。', 'register',0),
(2,'投资意向提醒','您提交的投资意向已收到，请等待创业者审核。','business',0),
(1,'项目审核通过','您的项目「智能教育平台」已通过审核并上线。','audit',0),
(3,'服务审核通过','您的服务「企业官网开发」已通过审核。','audit',0);

-- ========== 扩充测试数据（列表 10+ 条） ==========
-- 更多测试用户（id 将为 4,5,6；密码由后端首次启动同步为 Admin@1234）
INSERT INTO `user` (`username`,`password`,`email`,`phone`,`real_name`,`role`,`status`,`industry`,`company_name`,`contact_person`,`service_type`,`create_time`,`update_time`,`deleted`) VALUES
('founder2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','f2@test.com','13800000011','刘创','entrepreneur',1,'医疗',NULL,NULL,NULL,NOW(),NOW(),0),
('investor2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','i2@test.com','13800000012','陈投','investor',1,'消费',NULL,NULL,NULL,NOW(),NOW(),0),
('provider2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','p2@test.com','13800000013','赵服务','provider',1,NULL,'YY咨询','赵经理','市场推广',NOW(),NOW(),0);
-- 为新用户分配权限（user_id 4=founder2, 5=investor2, 6=provider2）
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 4, id FROM `permission` WHERE code IN ('project:publish','project:manage','investment:intent','investment:audit','resource:book');
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 5, id FROM `permission` WHERE code IN ('investment:intent','resource:book');
INSERT INTO `user_permission` (`user_id`,`permission_id`) SELECT 6, id FROM `permission` WHERE code IN ('resource:publish','resource:manage','resource:book');

-- 更多项目（founder_id=1 或 4）
INSERT INTO `project` (`title`,`description`,`founder_id`,`industry`,`stage`,`funding_needed`,`equity_offered`,`status`,`view_count`,`like_count`,`create_time`,`update_time`,`deleted`) VALUES
('智慧医疗 SaaS','面向基层医疗机构的信息化解决方案。',1,'医疗','开发中',800.00,12.00,'online',88,15,NOW(),NOW(),0),
('新零售供应链','生鲜供应链数字化平台。',1,'消费','已上线',300.00,8.00,'online',220,45,NOW(),NOW(),0),
('企业协作工具','轻量级团队协作与任务管理。',1,'互联网','idea',200.00,5.00,'pending',5,0,NOW(),NOW(),0),
('新能源充电网络','城市充电桩运营与运维。',1,'能源','开发中',1500.00,20.00,'online',156,32,NOW(),NOW(),0),
('跨境电商 ERP','跨境卖家一站式管理。',1,'互联网','开发中',400.00,10.00,'online',92,18,NOW(),NOW(),0),
('在线职业教育','IT/设计类实战课程。',4,'教育','已上线',600.00,15.00,'online',310,67,NOW(),NOW(),0),
('智能硬件孵化','智能家居与可穿戴设备。',4,'科技','idea',500.00,10.00,'pending',12,3,NOW(),NOW(),0),
('本地生活服务','社区团购与到店服务。',4,'消费','开发中',1000.00,18.00,'online',178,41,NOW(),NOW(),0),
('农业物联网','种植/养殖环境监测与预警。',4,'农业','idea',300.00,8.00,'pending',8,1,NOW(),NOW(),0),
('文创 IP 平台','设计师与品牌授权对接。',4,'文化','开发中',250.00,6.00,'online',65,12,NOW(),NOW(),0);

-- 更多服务（provider_id=3 或 5）
INSERT INTO `resource` (`title`,`description`,`provider_id`,`provider_name`,`category`,`type`,`price`,`status`,`service_cycle`,`rating`,`create_time`,`update_time`,`deleted`) VALUES
('小程序开发','微信/支付宝小程序设计与开发。',3,'XX科技有限公司','技术开发','service',15000.00,'online','20天',4.60,NOW(),NOW(),0),
('APP 开发','iOS/Android 原生与跨端开发。',3,'XX科技有限公司','技术开发','service',80000.00,'online','60天',4.90,NOW(),NOW(),0),
('UI/UE 设计','产品界面与交互设计。',3,'XX科技有限公司','设计','service',8000.00,'online','15天',4.70,NOW(),NOW(),0),
('财务顾问','初创企业财务规范与融资辅导。',5,'YY咨询','财务','service',2000.00,'online','按次',4.50,NOW(),NOW(),0),
('市场推广','品牌策划与落地执行。',5,'YY咨询','市场推广','service',10000.00,'online','30天',4.40,NOW(),NOW(),0),
('人力资源','招聘与薪酬体系搭建。',5,'YY咨询','人力资源','service',5000.00,'online','按次',4.60,NOW(),NOW(),0),
('知识产权','商标、专利与著作权。',3,'XX科技有限公司','法律咨询','service',3000.00,'online','按次',4.80,NOW(),NOW(),0),
('股权设计','股权结构设计与期权方案。',5,'YY咨询','法律咨询','service',6000.00,'online','按次',4.70,NOW(),NOW(),0),
('云服务器部署','阿里云/腾讯云部署与运维。',3,'XX科技有限公司','技术开发','service',5000.00,'online','7天',4.50,NOW(),NOW(),0),
('数据分析','用户行为与经营数据分析。',5,'YY咨询','技术开发','service',12000.00,'online','20天',4.65,NOW(),NOW(),0);

-- 更多投资意向与订单
INSERT INTO `investment` (`project_id`,`investor_id`,`amount`,`duration`,`description`,`status`,`create_time`,`update_time`,`deleted`) VALUES
(2,2,300.00,'2年','关注医疗信息化赛道。','approved',NOW(),NOW(),0),
(3,2,100.00,'1年','试水新零售。','pending',NOW(),NOW(),0),
(1,5,150.00,'3年','跟投。','rejected',NOW(),NOW(),0);
INSERT INTO `service_order` (`resource_id`,`buyer_id`,`provider_id`,`amount`,`status`,`description`,`expect_time`,`create_time`,`update_time`,`deleted`) VALUES
(2,4,3,15000.00,'completed','小程序商城。','2025-04',NOW(),NOW(),0),
(3,1,3,8000.00,'serving','产品 UI 改版。','2025-05',NOW(),NOW(),0),
(5,1,5,10000.00,'pending','品牌推广方案。','2025-06',NOW(),NOW(),0);

-- 更多消息（便于消息中心列表）
INSERT INTO `message` (`from_user_id`,`to_user_id`,`target_id`,`target_type`,`title`,`content`,`type`,`is_read`,`create_time`,`deleted`) VALUES
(2,1,1,'project','投资意向','对您的项目「智能教育平台」提交了投资意向。','business',0,NOW(),0),
(NULL,1,2,'project','系统通知','您的项目「绿色能源方案」已进入审核队列。','system',0,NOW(),0),
(3,1,NULL,NULL,'服务咨询','请问企业官网开发支持响应式吗？','chat',0,NOW(),0);
