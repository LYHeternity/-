package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.entity.Follow;

/**
 * 关注服务
 */
public interface FollowService extends IService<Follow> {
    boolean isFollowing(Long userId, Long followUserId);
    void toggleFollow(Long userId, Long followUserId);
    int getFollowCount(Long userId);
    int getFollowerCount(Long userId);
}