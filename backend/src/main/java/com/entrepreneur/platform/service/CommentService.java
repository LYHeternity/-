package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.entity.Comment;
import com.entrepreneur.platform.common.PageResult;

import java.util.List;

/**
 * 评论服务
 */
public interface CommentService extends IService<Comment> {
    PageResult<Comment> getCommentsByPostId(Long postId, int page, int size);
    List<Comment> getRepliesByCommentId(Long commentId);
    void updateLikeCount(Long commentId);
}