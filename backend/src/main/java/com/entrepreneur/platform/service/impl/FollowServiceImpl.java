package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.Follow;
import com.entrepreneur.platform.mapper.FollowMapper;
import com.entrepreneur.platform.service.FollowService;
import org.springframework.stereotype.Service;

/**
 * 关注服务实现
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Override
    public boolean isFollowing(Long userId, Long followUserId) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("follow_user_id", followUserId)
               .eq("deleted", 0);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void toggleFollow(Long userId, Long followUserId) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("follow_user_id", followUserId);
        
        Follow follow = baseMapper.selectOne(wrapper);
        if (follow != null) {
            // 取消关注
            baseMapper.deleteById(follow.getId());
        } else {
            // 添加关注
            Follow newFollow = new Follow();
            newFollow.setUserId(userId);
            newFollow.setFollowUserId(followUserId);
            baseMapper.insert(newFollow);
        }
    }

    @Override
    public int getFollowCount(Long userId) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("deleted", 0);
        return baseMapper.selectCount(wrapper).intValue();
    }

    @Override
    public int getFollowerCount(Long userId) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("follow_user_id", userId)
               .eq("deleted", 0);
        return baseMapper.selectCount(wrapper).intValue();
    }
}