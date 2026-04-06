package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.ServiceReview;
import com.entrepreneur.platform.service.ResourceService;
import com.entrepreneur.platform.service.ServiceReviewService;
import com.entrepreneur.platform.service.UserFavoriteService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 资源/服务：发布、我的列表、公开列表、详情、评价、收藏
 */
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final UserFavoriteService userFavoriteService;
    private final ServiceReviewService serviceReviewService;

    @PostMapping
    public Result<Resource> publish(@RequestBody Resource resource) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        resource.setProviderId(uid);
        if (resource.getProviderName() == null) resource.setProviderName("服务商");
        return Result.ok(resourceService.publish(resource));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") long current,
                            @RequestParam(defaultValue = "10") long size,
                            @RequestParam(required = false) String status) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(resourceService.listMy(new Page<>(current, size), uid, status));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) BigDecimal priceMin,
                          @RequestParam(required = false) BigDecimal priceMax,
                          @RequestParam(required = false) String orderBy) {
        return Result.ok(resourceService.listPublic(new Page<>(current, size), category, type, priceMin, priceMax, orderBy));
    }

    @GetMapping("/{id}")
    public Result<Resource> detail(@PathVariable Long id) {
        Resource r = resourceService.getById(id);
        if (r == null) return Result.fail(404, "服务不存在");
        resourceService.incViewCount(id);
        r = resourceService.getById(id);
        return Result.ok(r);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Resource resource) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Resource existing = resourceService.getById(id);
        if (existing == null || !existing.getProviderId().equals(uid)) return Result.fail(403, "无权限");
        resource.setId(id);
        resource.setProviderId(uid);
        resourceService.update(resource);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<String> setStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Resource existing = resourceService.getById(id);
        if (existing == null || !existing.getProviderId().equals(uid)) return Result.fail(403, "无权限");
        resourceService.setStatus(id, body.get("status"));
        return Result.ok();
    }

    @GetMapping("/{id}/reviews")
    public Result<List<ServiceReview>> reviews(@PathVariable Long id) {
        return Result.ok(serviceReviewService.listByResource(id));
    }

    @PostMapping("/{id}/favorite")
    public Result<String> toggleFavorite(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        userFavoriteService.toggleFavorite(uid, "resource", id);
        return Result.ok();
    }

    @GetMapping("/{id}/favorite")
    public Result<Boolean> isFavorited(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.ok(false);
        return Result.ok(userFavoriteService.isFavorited(uid, "resource", id));
    }

    @GetMapping("/{id}/related")
    public Result<java.util.List<Resource>> relatedResources(@PathVariable Long id, @RequestParam(defaultValue = "3") int limit) {
        return Result.ok(resourceService.getRelatedResources(id, limit));
    }
}
