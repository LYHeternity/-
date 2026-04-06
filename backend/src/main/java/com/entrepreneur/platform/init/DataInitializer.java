package com.entrepreneur.platform.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.entity.Permission;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.entity.UserPermission;
import com.entrepreneur.platform.mapper.PermissionMapper;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.SystemConfigMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import com.entrepreneur.platform.mapper.UserPermissionMapper;
import com.entrepreneur.platform.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 启动时创建默认管理员：admin / Admin@1234，并分配全部权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final PasswordEncoder passwordEncoder;
    private final ArticleService articleService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "Admin@1234";

    @Override
    public void run(ApplicationArguments args) {
        // 只创建管理员用户，简化启动过程
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, ADMIN_USERNAME));
        if (admin == null) {
            admin = new User();
            admin.setUsername(ADMIN_USERNAME);
            admin.setEmail("admin@platform.com");
            admin.setPhone("13800000000");
            admin.setRealName("系统管理员");
            admin.setRole("admin");
            admin.setStatus(1);
            admin.setDeleted(0);
        }
        // 不加密，直接存储明文密码
        admin.setPassword(DEFAULT_PASSWORD);
        if (admin.getId() == null) {
            userMapper.insert(admin);
            log.info("默认管理员已创建: {} / {}", ADMIN_USERNAME, DEFAULT_PASSWORD);
        } else {
            userMapper.updateById(admin);
            log.info("默认管理员密码已更新: {} / {}", ADMIN_USERNAME, DEFAULT_PASSWORD);
        }

        // 暂时注释掉权限分配，简化启动过程
        // List<Permission> permissions = permissionMapper.selectList(null);
        // for (Permission p : permissions) {
        //     // 检查权限是否已经存在
        //     UserPermission existing = userPermissionMapper.selectOne(
        //             new LambdaQueryWrapper<UserPermission>()
        //                     .eq(UserPermission::getUserId, admin.getId())
        //                     .eq(UserPermission::getPermissionId, p.getId())
        //     );
        //     if (existing == null) {
        //         UserPermission up = new UserPermission();
        //         up.setUserId(admin.getId());
        //         up.setPermissionId(p.getId());
        //         userPermissionMapper.insert(up);
        //     }
        // }
        // log.info("已为管理员分配 {} 项权限", permissions.size());

        // 暂时注释掉测试用户密码更新，简化启动过程
        // for (String u : java.util.Arrays.asList("founder1", "investor1", "provider1", "founder2", "investor2", "provider2")) {
        //     User test = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, u));
        //     if (test != null) {
        //         test.setPassword(DEFAULT_PASSWORD); // 不加密，直接存储明文密码
        //         userMapper.updateById(test);
        //     }
        // }

        // 暂时注释掉测试数据插入，避免启动卡住
        // insertTestDataForFounder1();
        
        // 启动时自动爬取文章，确保文章列表有内容显示
        try {
            log.info("开始自动爬取创业资讯文章...");
            int articleCount = articleService.scrapeAndSaveArticles();
            log.info("自动爬取完成，共获取 {} 篇文章", articleCount);
        } catch (Exception e) {
            log.error("自动爬取文章失败", e);
        }
    }

    private void insertTestDataForFounder1() {
        // 检查是否已经插入过测试数据
        com.entrepreneur.platform.entity.SystemConfig config = systemConfigMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.entrepreneur.platform.entity.SystemConfig>()
                        .eq(com.entrepreneur.platform.entity.SystemConfig::getConfigKey, "test_data_inserted")
        );
        
        if (config != null && "true".equals(config.getConfigValue())) {
            log.info("测试数据已经插入过，跳过本次操作");
            return;
        }

        // 查找founder1用户
        User founder1 = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getUsername, "founder1"));
        if (founder1 == null) {
            log.warn("founder1用户不存在，跳过测试数据插入");
            return;
        }

        // 插入测试项目
        Project project1 = new Project();
        project1.setTitle("AI智能客服系统");
        project1.setDescription("基于大语言模型的企业级客服解决方案，支持多渠道接入和智能知识库管理。");
        project1.setFounderId(founder1.getId());
        project1.setIndustry("人工智能");
        project1.setStage("开发中");
        project1.setFundingNeeded(java.math.BigDecimal.valueOf(800.00));
        project1.setEquityOffered(java.math.BigDecimal.valueOf(15.00));
        project1.setStatus("pending"); // 设置为待审核状态
        project1.setViewCount(150);
        project1.setLikeCount(35);
        project1.setProjectImage("1.png");
        project1.setDeleted(0);
        projectMapper.insert(project1);

        Project project2 = new Project();
        project2.setTitle("区块链供应链管理");
        project2.setDescription("利用区块链技术实现供应链透明化和溯源，降低企业运营成本。");
        project2.setFounderId(founder1.getId());
        project2.setIndustry("区块链");
        project2.setStage("idea");
        project2.setFundingNeeded(java.math.BigDecimal.valueOf(1200.00));
        project2.setEquityOffered(java.math.BigDecimal.valueOf(20.00));
        project2.setStatus("pending"); // 设置为待审核状态
        project2.setViewCount(80);
        project2.setLikeCount(12);
        project2.setProjectImage("2.png");
        project2.setDeleted(0);
        projectMapper.insert(project2);

        Project project3 = new Project();
        project3.setTitle("元宇宙教育平台");
        project3.setDescription("沉浸式虚拟学习环境，提供互动式课程和虚拟教室。");
        project3.setFounderId(founder1.getId());
        project3.setIndustry("教育");
        project3.setStage("开发中");
        project3.setFundingNeeded(java.math.BigDecimal.valueOf(1500.00));
        project3.setEquityOffered(java.math.BigDecimal.valueOf(18.00));
        project3.setStatus("pending"); // 设置为待审核状态
        project3.setViewCount(200);
        project3.setLikeCount(45);
        project3.setProjectImage("3.png");
        project3.setDeleted(0);
        projectMapper.insert(project3);

        Project project4 = new Project();
        project4.setTitle("智能健康监测设备");
        project4.setDescription("可穿戴设备与AI健康分析系统，提供个性化健康建议。");
        project4.setFounderId(founder1.getId());
        project4.setIndustry("医疗健康");
        project4.setStage("已上线");
        project4.setFundingNeeded(java.math.BigDecimal.valueOf(600.00));
        project4.setEquityOffered(java.math.BigDecimal.valueOf(10.00));
        project4.setStatus("pending"); // 设置为待审核状态
        project4.setViewCount(250);
        project4.setLikeCount(60);
        project4.setProjectImage("1.png");
        project4.setDeleted(0);
        projectMapper.insert(project4);

        Project project5 = new Project();
        project5.setTitle("环保材料研发");
        project5.setDescription("新型可降解材料的研发与应用，助力企业实现绿色转型。");
        project5.setFounderId(founder1.getId());
        project5.setIndustry("新材料");
        project5.setStage("idea");
        project5.setFundingNeeded(java.math.BigDecimal.valueOf(900.00));
        project5.setEquityOffered(java.math.BigDecimal.valueOf(12.00));
        project5.setStatus("pending"); // 设置为待审核状态
        project5.setViewCount(60);
        project5.setLikeCount(8);
        project5.setProjectImage("2.png");
        project5.setDeleted(0);
        projectMapper.insert(project5);

        // 插入测试服务
        Resource service1 = new Resource();
        service1.setTitle("技术咨询服务");
        service1.setDescription("为初创企业提供技术架构设计和技术选型咨询。");
        service1.setProviderId(founder1.getId());
        service1.setProviderName("张创业");
        service1.setCategory("技术咨询");
        service1.setType("service");
        service1.setPrice(java.math.BigDecimal.valueOf(2000.00));
        service1.setStatus("pending"); // 设置为待审核状态
        service1.setServiceCycle("按次");
        service1.setRating(java.math.BigDecimal.valueOf(4.70));
        service1.setResourceImage("3.png");
        service1.setDeleted(0);
        resourceMapper.insert(service1);

        Resource service2 = new Resource();
        service2.setTitle("商业计划书撰写");
        service2.setDescription("专业的商业计划书撰写与优化服务，助力融资。");
        service2.setProviderId(founder1.getId());
        service2.setProviderName("张创业");
        service2.setCategory("咨询服务");
        service2.setType("service");
        service2.setPrice(java.math.BigDecimal.valueOf(5000.00));
        service2.setStatus("pending"); // 设置为待审核状态
        service2.setServiceCycle("15天");
        service2.setRating(java.math.BigDecimal.valueOf(4.90));
        service2.setResourceImage("1.png");
        service2.setDeleted(0);
        resourceMapper.insert(service2);

        Resource service3 = new Resource();
        service3.setTitle("市场调研分析");
        service3.setDescription("针对特定行业的市场调研和竞争分析服务。");
        service3.setProviderId(founder1.getId());
        service3.setProviderName("张创业");
        service3.setCategory("市场服务");
        service3.setType("service");
        service3.setPrice(java.math.BigDecimal.valueOf(3500.00));
        service3.setStatus("pending"); // 设置为待审核状态
        service3.setServiceCycle("20天");
        service3.setRating(java.math.BigDecimal.valueOf(4.60));
        service3.setResourceImage("1.png");
        service3.setDeleted(0);
        resourceMapper.insert(service3);

        Resource service4 = new Resource();
        service4.setTitle("产品原型设计");
        service4.setDescription("基于用户需求的产品原型设计和用户体验优化。");
        service4.setProviderId(founder1.getId());
        service4.setProviderName("张创业");
        service4.setCategory("设计服务");
        service4.setType("service");
        service4.setPrice(java.math.BigDecimal.valueOf(4000.00));
        service4.setStatus("pending"); // 设置为待审核状态
        service4.setServiceCycle("10天");
        service4.setRating(java.math.BigDecimal.valueOf(4.80));
        service4.setResourceImage("2.png");
        service4.setDeleted(0);
        resourceMapper.insert(service4);

        Resource service5 = new Resource();
        service5.setTitle("融资对接服务");
        service5.setDescription("为创业项目对接合适的投资机构和投资人。");
        service5.setProviderId(founder1.getId());
        service5.setProviderName("张创业");
        service5.setCategory("金融服务");
        service5.setType("service");
        service5.setPrice(java.math.BigDecimal.valueOf(8000.00));
        service5.setStatus("pending"); // 设置为待审核状态
        service5.setServiceCycle("按需");
        service5.setRating(java.math.BigDecimal.valueOf(4.50));
        service5.setResourceImage("3.png");
        service5.setDeleted(0);
        resourceMapper.insert(service5);

        // 标记测试数据已插入
        com.entrepreneur.platform.entity.SystemConfig newConfig = new com.entrepreneur.platform.entity.SystemConfig();
        newConfig.setConfigKey("test_data_inserted");
        newConfig.setConfigValue("true");
        newConfig.setDescription("标记测试数据是否已插入");
        systemConfigMapper.insert(newConfig);

        log.info("为founder1插入了5个测试项目和5个测试服务，并标记为待审核状态");
    }
}
