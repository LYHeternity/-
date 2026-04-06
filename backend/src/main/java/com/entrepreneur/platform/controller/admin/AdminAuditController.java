package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.service.MessageService;
import com.entrepreneur.platform.service.ProjectService;
import com.entrepreneur.platform.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员-内容审核：项目/服务待审核列表、通过、拒绝、下架
 */
@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditController {

    private final ProjectService projectService;
    private final ResourceService resourceService;
    private final MessageService messageService;

    @GetMapping("/project/list")
    public Result<?> projectList(@RequestParam(defaultValue = "1") long current,
                                 @RequestParam(defaultValue = "10") long size) {
        return Result.ok(projectService.listPending(new Page<>(current, size)));
    }

    @PutMapping("/project/{id}")
    public Result<String> projectAudit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status"); // online / rejected
        String rejectReason = body.get("rejectReason");
        Project project = projectService.getById(id);
        if (project != null) {
            projectService.setStatus(id, status, rejectReason);
            // 发送消息通知
            String title = status.equals("online") ? "项目审核通过" : "项目审核拒绝";
            String content = status.equals("online") ? "您的项目已通过审核，现在可以在项目大厅中查看。" : "您的项目未通过审核，原因：" + rejectReason;
            messageService.send(1L, project.getFounderId(), id, "project", title, content, "system");
        }
        return Result.ok();
    }

    @GetMapping("/resource/list")
    public Result<?> resourceList(@RequestParam(defaultValue = "1") long current,
                                @RequestParam(defaultValue = "10") long size) {
        return Result.ok(resourceService.listPending(new Page<>(current, size)));
    }

    @PutMapping("/resource/{id}")
    public Result<String> resourceAudit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String rejectReason = body.get("rejectReason");
        Resource resource = resourceService.getById(id);
        if (resource != null) {
            resourceService.setStatus(id, status, rejectReason);
            // 发送消息通知
            String title = status.equals("online") ? "服务审核通过" : "服务审核拒绝";
            String content = status.equals("online") ? "您的服务已通过审核，现在可以在服务市场中查看。" : "您的服务未通过审核，原因：" + rejectReason;
            messageService.send(1L, resource.getProviderId(), id, "resource", title, content, "system");
        }
        return Result.ok();
    }
}
