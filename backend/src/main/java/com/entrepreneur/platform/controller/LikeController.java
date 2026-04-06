package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.LikeService;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.service.CommentService;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/community/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    /**
     * 检查是否已点赞
     */
    @GetMapping("/check")
    public Result checkLike(@RequestParam Long targetId,
                          @RequestParam String targetType) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        boolean isLiked = likeService.isLiked(userId, targetId, targetType);
        return Result.ok(isLiked);
    }

    /**
     * 切换点赞状态
     */
    @PostMapping("/toggle")
    public Result toggleLike(@RequestParam Long targetId,
                           @RequestParam String targetType) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        likeService.toggleLike(userId, targetId, targetType);
        
        // 更新点赞数
        if ("post".equals(targetType)) {
            postService.updateLikeCount(targetId);
        } else if ("comment".equals(targetType)) {
            commentService.updateLikeCount(targetId);
        }
        
        return Result.ok("操作成功");
    }
}