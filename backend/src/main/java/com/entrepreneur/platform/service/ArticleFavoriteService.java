package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.entity.ArticleFavorite;

import java.util.List;

/**
 * 文章收藏服务接口
 */
public interface ArticleFavoriteService extends IService<ArticleFavorite> {
    
    /**
     * 切换文章收藏状态
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 是否收藏
     */
    boolean toggleFavorite(Long userId, Long articleId);
    
    /**
     * 检查文章是否被用户收藏
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 是否收藏
     */
    boolean isFavorited(Long userId, Long articleId);
    
    /**
     * 获取用户收藏的文章ID列表
     * @param userId 用户ID
     * @return 文章ID列表
     */
    List<Long> getFavoriteArticleIds(Long userId);
}
