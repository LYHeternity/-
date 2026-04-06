package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.Comment;
import com.entrepreneur.platform.mapper.CommentMapper;
import com.entrepreneur.platform.service.CommentService;
import com.entrepreneur.platform.common.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务实现
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public PageResult<Comment> getCommentsByPostId(Long postId, int page, int size) {
        Page<Comment> commentPage = new Page<>(page, size);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId)
               .eq("parent_id", 0) // 只查询一级评论
               .eq("status", 1)
               .orderByDesc("created_at");
        
        Page<Comment> result = baseMapper.selectPage(commentPage, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public List<Comment> getRepliesByCommentId(Long commentId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", commentId)
               .eq("status", 1)
               .orderByAsc("created_at");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void updateLikeCount(Long commentId) {
        // 实际实现中，应该根据like表的统计结果更新
        // 这里简化处理
    }
}