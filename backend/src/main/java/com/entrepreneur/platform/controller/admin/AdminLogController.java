package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.SystemLog;
import com.entrepreneur.platform.mapper.SystemLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员-日志管理：操作日志列表、筛选
 */
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLogController {

    private final SystemLogMapper systemLogMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "20") long size,
                          @RequestParam(required = false) String level,
                          @RequestParam(required = false) String username) {
        LambdaQueryWrapper<SystemLog> q = new LambdaQueryWrapper<SystemLog>().orderByDesc(SystemLog::getTimestamp);
        if (level != null && !level.isEmpty()) q.eq(SystemLog::getLevel, level);
        if (username != null && !username.isEmpty()) q.eq(SystemLog::getUsername, username);
        return Result.ok(systemLogMapper.selectPage(new Page<>(current, size), q));
    }
}
