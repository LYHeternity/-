package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员-项目管理：项目列表、重新提交审核、删除
 */
@RestController
@RequestMapping("/admin/project")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProjectController {

    private final ProjectService projectService;

    @GetMapping("/list")
    public Result<?> projectList(@RequestParam(defaultValue = "1") long current,
                                @RequestParam(defaultValue = "10") long size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String industry,
                                @RequestParam(required = false) String status) {
        Page<com.entrepreneur.platform.entity.Project> page = new Page<>(current, size);
        return Result.ok(projectService.listAll(page, keyword, industry, status));
    }

    @PutMapping("/{id}/resubmit-audit")
    public Result<String> resubmitAudit(@PathVariable Long id) {
        projectService.setStatus(id, "pending");
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteProject(@PathVariable Long id) {
        projectService.removeById(id);
        return Result.ok();
    }
}
