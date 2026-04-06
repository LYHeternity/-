package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Comment;
import com.entrepreneur.platform.service.CommentService;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/community/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    /**
     * 获取帖子评论列表
     */
    @GetMapping("/post/{postId}")
    public Result getCommentsByPostId(@PathVariable Long postId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.getCommentsByPostId(postId, page, size));
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result getRepliesByCommentId(@PathVariable Long commentId) {
        return Result.ok(commentService.getRepliesByCommentId(commentId));
    }

    /**
     * 发布评论
     */
    @PostMapping
    public Result createComment(@RequestBody Comment comment) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        comment.setUserId(userId);
        comment.setLikeCount(0);
        comment.setStatus(1); // 直接发布，实际项目中可能需要审核
        commentService.save(comment);
        // 更新帖子评论数
        postService.updateCommentCount(comment.getPostId());
        return Result.ok("评论成功");
    }

    /**
     * 更新评论
     */
    @PutMapping("/{id}")
    public Result updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Comment existingComment = commentService.getById(id);
        if (existingComment == null) {
            return Result.fail("评论不存在");
        }
        if (!existingComment.getUserId().equals(userId)) {
            return Result.fail("无权修改");
        }
        comment.setId(id);
        comment.setUserId(userId);
        commentService.updateById(comment);
        return Result.ok("更新成功");
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.fail("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            return Result.fail("无权删除");
        }
        commentService.removeById(id);
        // 更新帖子评论数
        postService.updateCommentCount(comment.getPostId());
        return Result.ok("删除成功");
    }
}