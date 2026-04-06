package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.FollowService;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 关注控制器
 */
@RestController
@RequestMapping("/community/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 检查是否已关注
     */
    @GetMapping("/check")
    public Result checkFollow(@RequestParam Long followUserId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        boolean isFollowing = followService.isFollowing(userId, followUserId);
        return Result.ok(isFollowing);
    }

    /**
     * 切换关注状态
     */
    @PostMapping("/toggle")
    public Result toggleFollow(@RequestParam Long followUserId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        followService.toggleFollow(userId, followUserId);
        return Result.ok("操作成功");
    }

    /**
     * 获取用户关注数
     */
    @GetMapping("/count/following/{userId}")
    public Result getFollowCount(@PathVariable Long userId) {
        int count = followService.getFollowCount(userId);
        return Result.ok(count);
    }

    /**
     * 获取用户粉丝数
     */
    @GetMapping("/count/followers/{userId}")
    public Result getFollowerCount(@PathVariable Long userId) {
        int count = followService.getFollowerCount(userId);
        return Result.ok(count);
    }
}