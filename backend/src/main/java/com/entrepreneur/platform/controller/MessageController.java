package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Message;
import com.entrepreneur.platform.service.MessageService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息：列表、未读数、已读、删除
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "20") long size,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) Integer isRead) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(messageService.listByUser(new Page<>(current, size), uid, type, isRead));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.ok(0L);
        return Result.ok(messageService.countUnread(uid));
    }

    @PutMapping("/{id}/read")
    public Result<String> markRead(@PathVariable Long id) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        messageService.markRead(id);
        return Result.ok();
    }

    @PutMapping("/read-batch")
    public Result<String> markReadBatch(@RequestBody Map<String, List<Long>> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        List<Long> ids = body.get("ids");
        if (ids != null) messageService.markReadBatch(ids);
        return Result.ok();
    }

    @GetMapping("/chat")
    public Result<List<Message>> chat(@RequestParam Long withUserId, @RequestParam(defaultValue = "50") int limit) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(messageService.listChat(uid, withUserId, limit));
    }

    /** 发送一条聊天消息（在线沟通） */
    @PostMapping("/send")
    public Result<String> send(@RequestBody Map<String, Object> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Long toUserId = body.get("toUserId") != null ? Long.valueOf(body.get("toUserId").toString()) : null;
        String content = (String) body.get("content");
        if (toUserId == null || content == null) return Result.fail("参数不完整");
        messageService.send(uid, toUserId, null, null, "私信", content, "chat");
        return Result.ok();
    }
}
