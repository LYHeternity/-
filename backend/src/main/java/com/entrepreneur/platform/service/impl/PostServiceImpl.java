package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.mapper.PostMapper;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.common.PageResult;
import org.springframework.stereotype.Service;
import com.entrepreneur.platform.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 帖子服务实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private RedisService redisService;

    @Override
    public PageResult<Post> getPostList(int page, int size, String category, String type, String status, String keyword) {
        Page<Post> postPage = new Page<>(page, size);
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        } else {
            // 默认只查询正常状态的帖子
            wrapper.eq("status", 1);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword).or().like("content", keyword);
        }
        
        wrapper.orderByDesc("created_at");
        
        Page<Post> result = baseMapper.selectPage(postPage, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public Post getPostById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void incrementViewCount(Long postId) {
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            baseMapper.updateById(post);
        }
    }

    @Override
    public void updateLikeCount(Long postId) {
        // 实际实现中，应该根据like表的统计结果更新
        // 这里简化处理，假设每次调用增加1个点赞
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setLikeCount(post.getLikeCount() + 1);
            baseMapper.updateById(post);
        }
    }

    @Override
    public void updateCommentCount(Long postId) {
        // 实际实现中，应该根据comment表的统计结果更新
        // 这里简化处理，假设每次调用增加1个评论
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            baseMapper.updateById(post);
        }
    }

    @Override
    public void setBestAnswer(Long postId, Long commentId) {
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setBestAnswerId(commentId);
            baseMapper.updateById(post);
        }
    }

    @Override
    public java.util.List<Post> getHotPosts(int limit) {
        // 生成缓存键
        String cacheKey = "posts:hot:" + limit;
        
        // 尝试从Redis缓存中获取数据
        java.util.List<Post> cachedResult = (java.util.List<Post>) redisService.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
               .orderByDesc("like_count")
               .orderByDesc("view_count")
               .orderByDesc("comment_count")
               .last("LIMIT " + limit);
        
        java.util.List<Post> result = baseMapper.selectList(wrapper);
        
        // 将结果存入Redis缓存，设置过期时间为15分钟
        if (result != null) {
            redisService.set(cacheKey, result, 900);
        }
        
        return result;
    }
}