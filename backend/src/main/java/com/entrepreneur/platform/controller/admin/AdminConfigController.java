package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.SystemConfig;
import com.entrepreneur.platform.mapper.SystemConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员-系统配置：查询、修改（即时生效）
 */
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminConfigController {

    private final SystemConfigMapper systemConfigMapper;

    @GetMapping("/list")
    public Result<List<SystemConfig>> list() {
        return Result.ok(systemConfigMapper.selectList(null));
    }

    @GetMapping("/key/{key}")
    public Result<String> get(@PathVariable String key) {
        SystemConfig c = systemConfigMapper.selectOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key));
        return Result.ok(c != null ? c.getConfigValue() : null);
    }

    @PutMapping("/key/{key}")
    public Result<String> set(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        SystemConfig c = systemConfigMapper.selectOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key));
        if (c != null) {
            c.setConfigValue(value);
            systemConfigMapper.updateById(c);
        } else {
            c = new SystemConfig();
            c.setConfigKey(key);
            c.setConfigValue(value);
            systemConfigMapper.insert(c);
        }
        return Result.ok();
    }
}
