package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.common.PageResult;

import java.util.Map;

/**
 * 帖子服务
 */
public interface PostService extends IService<Post> {
    PageResult<Post> getPostList(int page, int size, String category, String type, String status, String keyword);
    Post getPostById(Long id);
    void incrementViewCount(Long postId);
    void updateLikeCount(Long postId);
    void updateCommentCount(Long postId);
    void setBestAnswer(Long postId, Long commentId);
    java.util.List<Post> getHotPosts(int limit);
}