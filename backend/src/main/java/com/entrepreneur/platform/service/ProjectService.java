package com.entrepreneur.platform.service;

// 导入 MyBatis-Plus 的条件查询包装器，用于构建类型安全的数据库查询条件
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 导入 MyBatis-Plus 的分页结果接口，用于接收分页查询的结果
import com.baomidou.mybatisplus.core.metadata.IPage;
// 导入 MyBatis-Plus 的分页对象，用于指定页码和每页数量
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// 导入项目实体类，对应数据库中的 project 表
import com.entrepreneur.platform.entity.Project;
// 导入用户实体类
import com.entrepreneur.platform.entity.User;
// 导入项目进展实体类，对应数据库中的 project_progress 表
import com.entrepreneur.platform.entity.ProjectProgress;
// 导入项目团队成员实体类，对应数据库中的 project_team_member 表
import com.entrepreneur.platform.entity.ProjectTeamMember;
// 导入项目数据访问接口，用于操作 project 表
import com.entrepreneur.platform.mapper.ProjectMapper;
// 导入用户数据访问接口
import com.entrepreneur.platform.mapper.UserMapper;
// 导入项目进展数据访问接口，用于操作 project_progress 表
import com.entrepreneur.platform.mapper.ProjectProgressMapper;
// 导入项目团队成员数据访问接口，用于操作 project_team_member 表
import com.entrepreneur.platform.mapper.ProjectTeamMemberMapper;
// 导入 Lombok 注解，自动生成只包含 final 字段的构造函数，避免手写样板代码
import lombok.RequiredArgsConstructor;
// 导入 Spring 的服务注解，将此类标记为 Spring Bean，由 Spring 容器管理
import org.springframework.stereotype.Service;
// 导入 Spring 的事务注解，确保方法内多个数据库操作要么全部成功，要么全部回滚
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 的工具类，用于判断字符串是否有内容（非 null 且非空串）
import org.springframework.util.StringUtils;
import com.entrepreneur.platform.service.RedisService;

// 导入 BigDecimal 类，用于精确表示融资金额等金额数值，避免浮点数精度丢失
import java.math.BigDecimal;
// 导入 Collections 工具类，用于返回空列表
import java.util.Collections;
// 导入 List 接口，用于接收查询到的列表数据
import java.util.List;
// 导入 Map 接口，用于存储用户信息
import java.util.Map;
// 导入 Stream 接口，用于集合操作
import java.util.stream.Collectors;

/**
 * 项目服务类：负责处理所有与项目相关的业务逻辑
 * 使用 @Service 注解标记为 Spring 服务组件
 * 使用 @RequiredArgsConstructor 自动生成构造函数，注入 final 修饰的依赖
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    // 声明项目数据访问接口，final 确保必须初始化，由 Lombok 生成的构造函数自动注入
    private final ProjectMapper projectMapper;
    // 声明用户数据访问接口
    private final UserMapper userMapper;
    // 声明项目进展数据访问接口，用于操作项目进展表
    private final ProjectProgressMapper projectProgressMapper;
    // 声明项目团队成员数据访问接口，用于操作团队成员表
    private final ProjectTeamMemberMapper projectTeamMemberMapper;
    // 声明Redis服务，用于缓存热门项目
    private final RedisService redisService;

    /**
     * 发布新项目：将创业项目信息保存到数据库
     * 使用 @Transactional 保证事务性，如果保存失败会回滚所有操作
     * @param project 项目对象，包含标题、行业、阶段、融资金额等信息
     * @return 保存后的项目对象（包含自动生成的 ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public Project publish(Project project) {
        // 设置项目初始状态为"pending"（待审核），等待管理员审核通过才能上线
        project.setStatus("pending");
        // 初始化浏览量为 0，每次用户查看项目时会增加
        project.setViewCount(0);
        // 初始化点赞数为 0，用户对项目点赞时会增加
        project.setLikeCount(0);
        // 调用 MyBatis-Plus 的 insert 方法，将项目对象插入到 project 表
        projectMapper.insert(project);
        // 返回已保存的项目对象（此时已包含数据库自动生成的主键 ID）
        return project;
    }

    /**
     * 根据 ID 获取项目详情
     * @param id 项目主键 ID
     * @return 项目对象，如果不存在则返回 null
     */
    public Project getById(Long id) {
        // 调用 MyBatis-Plus 的 selectById 方法，根据主键 ID 查询项目
        return projectMapper.selectById(id);
    }

    /**
     * 增加项目浏览量：每次用户查看项目详情时调用
     * @param id 项目 ID
     */
    public void incViewCount(Long id) {
        // 先根据 ID 查询项目对象
        Project p = projectMapper.selectById(id);
        // 判断项目是否存在，避免空指针异常
        if (p != null) {
            // 将当前浏览量加 1
            p.setViewCount(p.getViewCount() + 1);
            // 调用 updateById 更新项目对象，保存新的浏览量
            projectMapper.updateById(p);
        }
    }

    /**
     * 查询我发布的項目列表（创始人专用）
     * @param page 分页对象，包含页码和每页数量
     * @param founderId 创始人（发布者）的用户 ID
     * @param status 可选的状态筛选条件（如 pending、online、rejected 等）
     * @return 分页结果，包含项目列表和总记录数
     */
    public IPage<Project> listMy(Page<Project> page, Long founderId, String status) {
        // 创建 LambdaQueryWrapper 查询条件，eq 表示等于条件，筛选等于指定创始人 ID 的项目
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>().eq(Project::getFounderId, founderId);
        // 如果状态参数有值（非 null 且非空串），则添加状态等于条件
        if (StringUtils.hasText(status)) q.eq(Project::getStatus, status);
        // 按创建时间倒序排列，最新创建的项目排在最前面
        q.orderByDesc(Project::getCreateTime);
        // 执行分页查询，返回 IPage 对象（包含项目列表和分页信息）
        return projectMapper.selectPage(page, q);
    }

    /**
     * 查询公开可投资的项目列表（投资人可见）
     * @param page 分页对象
     * @param industry 可选的行业筛选（如"餐饮"、"科技"、"医疗"等）
     * @param stage 可选的发展阶段筛选（如"种子轮"、"天使轮"、"A 轮"等）
     * @param fundingMin 融资金额下限，筛选融资需求大于此值的项目
     * @param fundingMax 融资金额上限，筛选融资需求小于此值的项目
     * @param orderBy 排序方式："hot"按热度（点赞数）、"view"按浏览量、其他按创建时间
     * @return 分页结果，只包含状态为"online"（已上线）的项目
     */
    public IPage<Project> listPublic(Page<Project> page, String industry, String stage,
                                      BigDecimal fundingMin, BigDecimal fundingMax, String orderBy) {
        // 创建查询条件，eq 表示等于，只查询状态为"online"的项目（已通过审核可公开展示）
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>().eq(Project::getStatus, "online");
        // 如果行业参数有值，添加行业等于条件
        if (StringUtils.hasText(industry)) q.eq(Project::getIndustry, industry);
        // 如果阶段参数有值，添加阶段等于条件
        if (StringUtils.hasText(stage)) q.eq(Project::getStage, stage);
        // 如果融资金额下限不为 null，添加大于等于条件（ge = greater or equal）
        if (fundingMin != null) q.ge(Project::getFundingNeeded, fundingMin);
        // 如果融资金额上限不为 null，添加小于等于条件（le = less or equal）
        if (fundingMax != null) q.le(Project::getFundingNeeded, fundingMax);
        // 根据 orderBy 参数决定排序方式
        if ("hot".equals(orderBy)) q.orderByDesc(Project::getLikeCount);  // 按点赞数降序（最热）
        else if ("view".equals(orderBy)) q.orderByDesc(Project::getViewCount);  // 按浏览量降序（最火）
        else q.orderByDesc(Project::getCreateTime);  // 默认按创建时间降序（最新）
        // 执行分页查询并返回结果
        return projectMapper.selectPage(page, q);
    }

    /**
     * 更新项目信息（创始人编辑自己的项目）
     * @param project 项目对象，必须包含 ID 和要更新的字段
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Project project) {
        // 调用 MyBatis-Plus 的 updateById 方法，根据项目 ID 更新对应字段
        projectMapper.updateById(project);
    }

    /**
     * 设置项目状态（管理员审核用）
     * @param id 项目 ID
     * @param status 新状态（"online"通过审核、"rejected"拒绝）
     * @param rejectReason 拒绝原因（当状态为 rejected 时使用）
     */
    public void setStatus(Long id, String status, String rejectReason) {
        // 先根据 ID 查询项目对象
        Project p = projectMapper.selectById(id);
        // 判断项目是否存在
        if (p != null) {
            // 设置项目的新状态
            p.setStatus(status);
            // 如果状态是"rejected"（被拒绝）并且提供了拒绝原因
            if ("rejected".equals(status) && rejectReason != null) {
                // 这里可以添加保存拒绝原因的逻辑
                // 例如，p.setRejectReason(rejectReason); 但当前实体类可能没有这个字段
                // 注释说明这是一个待实现的功能
            }
            // 调用 updateById 更新项目状态
            projectMapper.updateById(p);
        }
    }

    /**
     * 设置项目状态（简化版，不传拒绝原因）
     * @param id 项目 ID
     * @param status 新状态
     */
    public void setStatus(Long id, String status) {
        // 调用上面的重载方法，传入 null 作为拒绝原因
        setStatus(id, status, null);
    }

    /**
     * 添加项目进展（创始人发布项目最新动态）
     * @param projectId 项目 ID
     * @param content 进展内容（文本描述）
     * @param attachments 附件（图片 URL、文档链接等，可能是 JSON 或逗号分隔的字符串）
     */
    public void addProgress(Long projectId, String content, String attachments) {
        // 创建新的项目进展对象
        ProjectProgress pp = new ProjectProgress();
        // 设置进展所属的项目 ID
        pp.setProjectId(projectId);
        // 设置进展的文本内容
        pp.setContent(content);
        // 设置进展的附件信息（可选）
        pp.setAttachments(attachments);
        // 调用 insert 方法将进展对象插入到 project_progress 表
        projectProgressMapper.insert(pp);
    }

    /**
     * 查询项目的所有进展动态
     * @param projectId 项目 ID
     * @return 项目进展列表，按创建时间倒序（最新的在前）
     */
    public List<ProjectProgress> listProgress(Long projectId) {
        // 调用 selectList 方法查询进展列表
        return projectProgressMapper.selectList(
                // 创建查询条件，eq 表示等于项目 ID
                new LambdaQueryWrapper<ProjectProgress>().eq(ProjectProgress::getProjectId, projectId)
                        // 按创建时间降序排列，确保最新的进展显示在最前面
                        .orderByDesc(ProjectProgress::getCreateTime));
    }

    /**
     * 查询所有待审核的项目列表（管理员专用）
     * @param page 分页对象
     * @return 分页结果，包含所有状态为"pending"的项目
     */
    public IPage<Project> listPending(Page<Project> page) {
        // 调用 selectPage 进行分页查询
        IPage<Project> result = projectMapper.selectPage(page,
                // 创建查询条件，eq 表示等于，只查状态为"pending"（待审核）的项目
                new LambdaQueryWrapper<Project>().eq(Project::getStatus, "pending").orderByDesc(Project::getCreateTime));
        
        // 补充提交人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            List<Long> founderIds = result.getRecords().stream()
                    .map(Project::getFounderId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!founderIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(founderIds);
                Map<Long, User> userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                
                for (Project project : result.getRecords()) {
                    if (project.getFounderId() != null) {
                        User user = userMap.get(project.getFounderId());
                        if (user != null) {
                            String submitter = user.getRealName() != null ? user.getRealName() : user.getUsername();
                            project.setSubmitter(submitter);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * 后台管理用的全量项目查询
     * @param page 分页对象
     * @param keyword 搜索关键词，可匹配项目标题或行业
     * @param industry 行业筛选条件
     * @param status 状态筛选条件
     * @return 分页结果
     */
    public IPage<Project> listAll(Page<Project> page, String keyword, String industry, String status) {
        // 创建空的查询包装器
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<>();
        // 如果有关键词，添加模糊查询条件（like），可匹配标题或行业
        if (StringUtils.hasText(keyword)) {
            // like 表示模糊匹配，or 表示或的关系，即满足任一条件即可
            q.like(Project::getTitle, keyword).or().like(Project::getIndustry, keyword);
        }
        // 如果有行业参数，添加行业等于条件
        if (StringUtils.hasText(industry)) {
            q.eq(Project::getIndustry, industry);
        }
        // 如果有状态参数，添加状态等于条件
        if (StringUtils.hasText(status)) {
            q.eq(Project::getStatus, status);
        }
        // 按创建时间降序排列
        q.orderByDesc(Project::getCreateTime);
        // 执行分页查询
        IPage<Project> result = projectMapper.selectPage(page, q);
        
        // 补充提交人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            // 收集所有创始人ID
            List<Long> founderIds = result.getRecords().stream()
                    .map(Project::getFounderId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!founderIds.isEmpty()) {
                // 批量查询用户信息
                List<User> users = userMapper.selectBatchIds(founderIds);
                Map<Long, User> userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                
                // 为每个项目添加提交人信息
                for (Project project : result.getRecords()) {
                    if (project.getFounderId() != null) {
                        User user = userMap.get(project.getFounderId());
                        if (user != null) {
                            String submitter = user.getRealName() != null ? user.getRealName() : user.getUsername();
                            project.setSubmitter(submitter);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * 根据 ID 删除项目
     * @param id 项目主键 ID
     */
    public void removeById(Long id) {
        // 调用 MyBatis-Plus 的 deleteById 方法，从 project 表中删除对应记录
        projectMapper.deleteById(id);
    }

    /**
     * 获取相关项目推荐（用于项目详情页的"猜你喜欢"或"相似项目"功能）
     * @param projectId 当前项目 ID
     * @param limit 推荐数量上限
     * @return 相关项目列表
     */
    public List<Project> getRelatedProjects(Long projectId, int limit) {
        // 先根据 ID 查询当前项目信息，用于获取行业和阶段作为推荐依据
        Project currentProject = projectMapper.selectById(projectId);
        // 判断当前项目是否存在，不存在则返回空列表
        if (currentProject == null) {
            // Collections.emptyList() 返回一个不可变的空列表，比 new ArrayList<>() 性能更好
            return Collections.emptyList();
        }
    
        // 创建查询条件，筛选状态为"online"的项目，并且排除当前项目本身（ne = not equal）
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "online")
                .ne(Project::getId, projectId);
    
        // 优先推荐同行业的项目（如果当前项目有行业信息）
        if (StringUtils.hasText(currentProject.getIndustry())) {
            // eq 添加行业等于条件
            q.eq(Project::getIndustry, currentProject.getIndustry());
        }
    
        // 如果同行业项目不足，再推荐同阶段的项目（使用 or 扩大范围）
        if (StringUtils.hasText(currentProject.getStage())) {
            // or 表示或的关系，即使行业不匹配，只要阶段相同也可以推荐
            q.or().eq(Project::getStage, currentProject.getStage());
        }
    
        // 多级排序：优先按点赞数降序（受欢迎程度），其次按浏览量降序（热度），最后按创建时间降序（新旧程度）
        q.orderByDesc(Project::getLikeCount)
         .orderByDesc(Project::getViewCount)
         .orderByDesc(Project::getCreateTime)
         // 使用原生 SQL 的 LIMIT 子句限制返回数量，提高查询效率
         .last("LIMIT " + limit);
    
        // 执行查询，返回符合条件的项目列表
        return projectMapper.selectList(q);
    }

    /**
     * 添加项目团队成员（创始人完善团队信息）
     * @param projectId 项目 ID
     * @param name 成员姓名
     * @param role 成员角色（如"CTO"、"市场总监"等）
     * @param avatar 成员头像图片 URL
     * @param bio 成员简介（教育背景、工作经历等）
     */
    public void addTeamMember(Long projectId, String name, String role, String avatar, String bio) {
        // 创建新的团队成员对象
        ProjectTeamMember member = new ProjectTeamMember();
        // 设置该成员所属的项目 ID
        member.setProjectId(projectId);
        // 设置成员姓名
        member.setName(name);
        // 设置成员在团队中的角色
        member.setRole(role);
        // 设置成员头像 URL
        member.setAvatar(avatar);
        // 设置成员简介
        member.setBio(bio);
        // 调用 insert 方法将团队成员信息插入到 project_team_member 表
        projectTeamMemberMapper.insert(member);
    }

    /**
     * 获取项目团队成员列表（展示在项目详情页）
     * @param projectId 项目 ID
     * @return 团队成员列表，按创建时间升序（最早加入的成员在前）
     */
    public List<ProjectTeamMember> listTeamMembers(Long projectId) {
        // 调用 selectList 查询团队成员列表
        return projectTeamMemberMapper.selectList(
                // 创建查询条件，筛选属于指定项目的成员
                new LambdaQueryWrapper<ProjectTeamMember>().eq(ProjectTeamMember::getProjectId, projectId)
                        // 按创建时间升序排列，确保创始人或最早加入的成员显示在前面
                        .orderByAsc(ProjectTeamMember::getCreateTime));
    }

    /**
     * 删除项目团队成员（移除某个成员）
     * @param memberId 团队成员的主键 ID
     */
    public void removeTeamMember(Long memberId) {
        // 调用 MyBatis-Plus 的 deleteById 方法，根据主键 ID 删除团队成员记录
        projectTeamMemberMapper.deleteById(memberId);
    }

    /**
     * 获取热门项目列表
     * @param limit 数量限制
     * @return 热门项目列表
     */
    public List<Project> getHotProjects(int limit) {
        // 生成缓存键
        String cacheKey = "projects:hot:" + limit;
        
        // 尝试从Redis缓存中获取数据
        List<Project> cachedResult = (List<Project>) redisService.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "online")
                .orderByDesc(Project::getLikeCount)
                .orderByDesc(Project::getViewCount)
                .last("LIMIT " + limit);
        
        List<Project> result = projectMapper.selectList(q);
        
        // 将结果存入Redis缓存，设置过期时间为15分钟
        if (result != null) {
            redisService.set(cacheKey, result, 900);
        }
        
        return result;
    }
}
