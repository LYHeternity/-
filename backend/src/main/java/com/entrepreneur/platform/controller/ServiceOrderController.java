package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.ServiceOrder;
import com.entrepreneur.platform.entity.ServiceReview;
import com.entrepreneur.platform.service.ServiceOrderService;
import com.entrepreneur.platform.service.ServiceReviewService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 服务订单：预约、列表、审核、完成、评价
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;
    private final ServiceReviewService serviceReviewService;

    @PostMapping
    public Result<ServiceOrder> create(@RequestBody ServiceOrder order) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        order.setBuyerId(uid);
        return Result.ok(serviceOrderService.create(order));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") long current,
                            @RequestParam(defaultValue = "10") long size,
                            @RequestParam(required = false) String status) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(serviceOrderService.listByBuyer(new Page<>(current, size), uid, status));
    }

    @GetMapping("/provider")
    public Result<?> providerList(@RequestParam(defaultValue = "1") long current,
                                  @RequestParam(defaultValue = "10") long size,
                                  @RequestParam(required = false) String status) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(serviceOrderService.listByProvider(new Page<>(current, size), uid, status));
    }

    @GetMapping("/{id}")
    public Result<ServiceOrder> detail(@PathVariable Long id) {
        ServiceOrder o = serviceOrderService.getById(id);
        if (o == null) return Result.fail(404, "订单不存在");
        return Result.ok(o);
    }

    @PostMapping("/{id}/audit")
    public Result<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Boolean approved = (Boolean) body.get("approved");
        String rejectReason = (String) body.get("rejectReason");
        serviceOrderService.approve(id, Boolean.TRUE.equals(approved), rejectReason);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<String> setStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        serviceOrderService.setStatus(id, body.get("status"));
        return Result.ok();
    }

    @PostMapping("/{id}/review")
    public Result<ServiceReview> review(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        ServiceOrder o = serviceOrderService.getById(id);
        if (o == null || !o.getBuyerId().equals(uid)) return Result.fail(403, "无权限");
        Integer rating = (Integer) body.get("rating");
        String content = (String) body.get("content");
        if (rating == null || rating < 1 || rating > 5) return Result.fail("评分为1-5");
        return Result.ok(serviceReviewService.add(id, o.getResourceId(), uid, rating, content));
    }
}
