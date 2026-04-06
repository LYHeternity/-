package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Investment;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.ServiceOrder;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.mapper.InvestmentMapper;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.ServiceOrderMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员-数据统计：总用户/项目/服务/意向/订单；趋势数据供 ECharts
 */
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final InvestmentMapper investmentMapper;
    private final ServiceOrderMapper serviceOrderMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userMapper.selectCount(null));
        map.put("projectCount", projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getDeleted, 0)));
        map.put("resourceCount", resourceMapper.selectCount(new LambdaQueryWrapper<Resource>().eq(Resource::getDeleted, 0)));
        map.put("investmentCount", investmentMapper.selectCount(null));
        map.put("orderCount", serviceOrderMapper.selectCount(null));
        return Result.ok(map);
    }

    @GetMapping("/user-trend")
    public Result<Map<String, Object>> userTrend() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getCreateTime));
        Map<String, Long> byDate = new HashMap<>();
        for (User u : users) {
            if (u.getCreateTime() == null) continue;
            String d = u.getCreateTime().toLocalDate().toString();
            byDate.put(d, byDate.getOrDefault(d, 0L) + 1);
        }
        List<String> dates = byDate.keySet().stream().sorted().collect(Collectors.toList());
        List<Long> counts = dates.stream().map(byDate::get).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        return Result.ok(result);
    }

    @GetMapping("/project-trend")
    public Result<Map<String, Object>> projectTrend() {
        List<Project> list = projectMapper.selectList(new LambdaQueryWrapper<Project>().eq(Project::getDeleted, 0).orderByAsc(Project::getCreateTime));
        Map<String, Long> byDate = new HashMap<>();
        for (Project p : list) {
            if (p.getCreateTime() == null) continue;
            String d = p.getCreateTime().toLocalDate().toString();
            byDate.put(d, byDate.getOrDefault(d, 0L) + 1);
        }
        List<String> dates = byDate.keySet().stream().sorted().collect(Collectors.toList());
        List<Long> counts = dates.stream().map(byDate::get).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        return Result.ok(result);
    }
}
