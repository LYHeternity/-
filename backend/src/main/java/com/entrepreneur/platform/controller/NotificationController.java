package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.NotificationService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统通知：列表、未读数、已读
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                         @RequestParam(defaultValue = "20") long size,
                         @RequestParam(required = false) String type,
                         @RequestParam(required = false) Integer isRead) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(notificationService.listByUser(new Page<>(current, size), uid, type, isRead));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.ok(0L);
        return Result.ok(notificationService.countUnread(uid));
    }

    @PutMapping("/{id}/read")
    public Result<String> markRead(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        notificationService.markRead(id);
        return Result.ok();
    }
}
