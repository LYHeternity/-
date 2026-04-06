package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.ProjectProgress;
import com.entrepreneur.platform.entity.ProjectTeamMember;
import com.entrepreneur.platform.service.ProjectService;
import com.entrepreneur.platform.service.UserFavoriteService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 项目：发布、我的列表、公开列表、详情、进展、上下架、点赞/收藏
 */
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserFavoriteService userFavoriteService;

    @PostMapping
    public Result<Project> publish(@RequestBody Project project) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        project.setFounderId(uid);
        return Result.ok(projectService.publish(project));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") long current,
                            @RequestParam(defaultValue = "10") long size,
                            @RequestParam(required = false) String status) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(projectService.listMy(new Page<>(current, size), uid, status));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String industry,
                          @RequestParam(required = false) String stage,
                          @RequestParam(required = false) BigDecimal fundingMin,
                          @RequestParam(required = false) BigDecimal fundingMax,
                          @RequestParam(required = false) String orderBy) {
        return Result.ok(projectService.listPublic(new Page<>(current, size), industry, stage, fundingMin, fundingMax, orderBy));
    }

    @GetMapping("/{id}")
    public Result<Project> detail(@PathVariable Long id) {
        Project p = projectService.getById(id);
        if (p == null) return Result.fail(404, "项目不存在");
        projectService.incViewCount(id);
        p = projectService.getById(id);
        return Result.ok(p);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Project project) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Project existing = projectService.getById(id);
        if (existing == null || !existing.getFounderId().equals(uid)) return Result.fail(403, "无权限");
        project.setId(id);
        project.setFounderId(uid);
        projectService.update(project);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<String> setStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Project existing = projectService.getById(id);
        if (existing == null || !existing.getFounderId().equals(uid)) return Result.fail(403, "无权限");
        projectService.setStatus(id, body.get("status"));
        return Result.ok();
    }

    @PostMapping("/{id}/progress")
    public Result<String> addProgress(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Project existing = projectService.getById(id);
        if (existing == null || !existing.getFounderId().equals(uid)) return Result.fail(403, "无权限");
        projectService.addProgress(id, body.get("content"), body.get("attachments"));
        return Result.ok();
    }

    @GetMapping("/{id}/progress")
    public Result<List<ProjectProgress>> progress(@PathVariable Long id) {
        return Result.ok(projectService.listProgress(id));
    }

    @PostMapping("/{id}/favorite")
    public Result<String> toggleFavorite(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        userFavoriteService.toggleFavorite(uid, "project", id);
        return Result.ok();
    }

    @GetMapping("/{id}/favorite")
    public Result<Boolean> isFavorited(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.ok(false);
        return Result.ok(userFavoriteService.isFavorited(uid, "project", id));
    }

    @GetMapping("/{id}/related")
    public Result<List<Project>> relatedProjects(@PathVariable Long id, @RequestParam(defaultValue = "3") int limit) {
        return Result.ok(projectService.getRelatedProjects(id, limit));
    }

    @PostMapping("/{id}/team")
    public Result<String> addTeamMember(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Project existing = projectService.getById(id);
        if (existing == null || !existing.getFounderId().equals(uid)) return Result.fail(403, "无权限");
        projectService.addTeamMember(id, body.get("name"), body.get("role"), body.get("avatar"), body.get("bio"));
        return Result.ok();
    }

    @GetMapping("/{id}/team")
    public Result<List<ProjectTeamMember>> listTeamMembers(@PathVariable Long id) {
        return Result.ok(projectService.listTeamMembers(id));
    }

    @DeleteMapping("/team/{memberId}")
    public Result<String> removeTeamMember(@PathVariable Long memberId) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        projectService.removeTeamMember(memberId);
        return Result.ok();
    }

    /**
     * 获取热门项目列表
     * @param limit 数量限制
     * @return 热门项目列表
     */
    @GetMapping("/hot")
    public Result<List<Project>> getHotProjects(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(projectService.getHotProjects(limit));
    }
}
