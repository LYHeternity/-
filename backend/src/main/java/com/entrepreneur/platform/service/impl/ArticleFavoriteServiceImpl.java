package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.ArticleFavorite;
import com.entrepreneur.platform.mapper.ArticleFavoriteMapper;
import com.entrepreneur.platform.service.ArticleFavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章收藏服务实现
 */
@Service
public class ArticleFavoriteServiceImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteService {
    
    @Override
    public boolean toggleFavorite(Long userId, Long articleId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getUserId, userId)
                   .eq(ArticleFavorite::getArticleId, articleId);
        
        ArticleFavorite existing = baseMapper.selectOne(queryWrapper);
        
        if (existing != null) {
            // 已收藏，取消收藏
            baseMapper.delete(queryWrapper);
            return false;
        } else {
            // 未收藏，添加收藏
            ArticleFavorite favorite = new ArticleFavorite();
            favorite.setUserId(userId);
            favorite.setArticleId(articleId);
            favorite.setCreatedAt(LocalDateTime.now());
            baseMapper.insert(favorite);
            return true;
        }
    }
    
    @Override
    public boolean isFavorited(Long userId, Long articleId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getUserId, userId)
                   .eq(ArticleFavorite::getArticleId, articleId);
        return baseMapper.selectOne(queryWrapper) != null;
    }
    
    @Override
    public List<Long> getFavoriteArticleIds(Long userId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getUserId, userId);
        List<ArticleFavorite> favorites = baseMapper.selectList(queryWrapper);
        return favorites.stream()
                       .map(ArticleFavorite::getArticleId)
                       .collect(Collectors.toList());
    }
}
