package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.entity.Like;

/**
 * 点赞服务
 */
public interface LikeService extends IService<Like> {
    boolean isLiked(Long userId, Long targetId, String targetType);
    void toggleLike(Long userId, Long targetId, String targetType);
}