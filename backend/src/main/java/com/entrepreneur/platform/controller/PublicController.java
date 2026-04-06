package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.ServiceOrderMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公开接口：首页数据概览（无需登录）
 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class PublicController {

    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final ServiceOrderMapper serviceOrderMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userMapper.selectCount(null));
        map.put("projectCount", projectMapper.selectCount(new LambdaQueryWrapper<Project>().eq(Project::getDeleted, 0).eq(Project::getStatus, "online")));
        map.put("resourceCount", resourceMapper.selectCount(new LambdaQueryWrapper<Resource>().eq(Resource::getDeleted, 0).eq(Resource::getStatus, "online")));
        map.put("orderCount", serviceOrderMapper.selectCount(null));
        return Result.ok(map);
    }
}
