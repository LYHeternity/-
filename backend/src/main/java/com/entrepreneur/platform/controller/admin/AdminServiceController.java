package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-服务管理：服务列表、重新提交审核、删除
 */
@RestController
@RequestMapping("/admin/service")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceController {

    private final ResourceService resourceService;

    @GetMapping("/list")
    public Result<?> serviceList(@RequestParam(defaultValue = "1") long current,
                                @RequestParam(defaultValue = "10") long size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String category,
                                @RequestParam(required = false) String status) {
        Page<com.entrepreneur.platform.entity.Resource> page = new Page<>(current, size);
        return Result.ok(resourceService.listAll(page, keyword, category, status));
    }

    @PutMapping("/{id}/resubmit-audit")
    public Result<String> resubmitAudit(@PathVariable Long id) {
        resourceService.setStatus(id, "pending");
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteService(@PathVariable Long id) {
        resourceService.removeById(id);
        return Result.ok();
    }
}
