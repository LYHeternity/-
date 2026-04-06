package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.Like;
import com.entrepreneur.platform.mapper.LikeMapper;
import com.entrepreneur.platform.service.LikeService;
import org.springframework.stereotype.Service;

/**
 * 点赞服务实现
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Override
    public boolean isLiked(Long userId, Long targetId, String targetType) {
        QueryWrapper<Like> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("target_id", targetId)
               .eq("target_type", targetType)
               .eq("deleted", 0);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void toggleLike(Long userId, Long targetId, String targetType) {
        QueryWrapper<Like> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("target_id", targetId)
               .eq("target_type", targetType);
        
        Like like = baseMapper.selectOne(wrapper);
        if (like != null) {
            // 取消点赞
            baseMapper.deleteById(like.getId());
        } else {
            // 添加点赞
            Like newLike = new Like();
            newLike.setUserId(userId);
            newLike.setTargetId(targetId);
            newLike.setTargetType(targetType);
            baseMapper.insert(newLike);
        }
    }
}