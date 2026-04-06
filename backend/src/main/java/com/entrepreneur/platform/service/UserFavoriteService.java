package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.entity.Article;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.UserFavorite;
import com.entrepreneur.platform.mapper.ArticleMapper;
import com.entrepreneur.platform.mapper.PostMapper;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.UserFavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏与点赞（通过收藏表或消息表实现；此处用收藏表，点赞用 project.like_count 递增 + 防重复需单独表，简化为仅收藏）
 */
@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final ArticleMapper articleMapper;
    private final PostMapper postMapper;

    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long userId, String targetType, Long targetId) {
        UserFavorite existing = userFavoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, targetType).eq(UserFavorite::getTargetId, targetId));
        if (existing != null) {
            userFavoriteMapper.deleteById(existing.getId());
            if ("project".equals(targetType)) {
                Project p = projectMapper.selectById(targetId);
                if (p != null && p.getLikeCount() > 0) p.setLikeCount(p.getLikeCount() - 1);
                if (p != null) projectMapper.updateById(p);
            } else if ("resource".equals(targetType)) {
                Resource r = resourceMapper.selectById(targetId);
                if (r != null && r.getFavoriteCount() > 0) r.setFavoriteCount(r.getFavoriteCount() - 1);
                if (r != null) resourceMapper.updateById(r);
            } else if ("article".equals(targetType)) {
                Article a = articleMapper.selectById(targetId);
                // Article实体类没有favoriteCount字段，暂时不更新计数
                if (a != null) articleMapper.updateById(a);
            } else if ("post".equals(targetType)) {
                Post p = postMapper.selectById(targetId);
                if (p != null && p.getLikeCount() > 0) p.setLikeCount(p.getLikeCount() - 1);
                if (p != null) postMapper.updateById(p);
            }
        } else {
            UserFavorite uf = new UserFavorite();
            uf.setUserId(userId);
            uf.setTargetType(targetType);
            uf.setTargetId(targetId);
            userFavoriteMapper.insert(uf);
            if ("project".equals(targetType)) {
                Project p = projectMapper.selectById(targetId);
                if (p != null) {
                    p.setLikeCount(p.getLikeCount() + 1);
                    projectMapper.updateById(p);
                }
            } else if ("resource".equals(targetType)) {
                Resource r = resourceMapper.selectById(targetId);
                if (r != null) {
                    r.setFavoriteCount(r.getFavoriteCount() + 1);
                    resourceMapper.updateById(r);
                }
            } else if ("article".equals(targetType)) {
                Article a = articleMapper.selectById(targetId);
                // Article实体类没有favoriteCount字段，暂时不更新计数
                if (a != null) {
                    articleMapper.updateById(a);
                }
            } else if ("post".equals(targetType)) {
                Post p = postMapper.selectById(targetId);
                if (p != null) {
                    p.setLikeCount(p.getLikeCount() + 1);
                    postMapper.updateById(p);
                }
            }
        }
    }

    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        return userFavoriteMapper.selectCount(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, targetType).eq(UserFavorite::getTargetId, targetId)) > 0;
    }

    public List<Long> myFavoriteProjectIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "project"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Project> myFavoriteProjects(Long userId) {
        List<Long> projectIds = myFavoriteProjectIds(userId);
        if (projectIds.isEmpty()) return Collections.emptyList();
        return projectMapper.selectBatchIds(projectIds);
    }

    public List<Project> myFavoriteProjects(Long userId, Integer page, Integer size) {
        List<Long> projectIds = myFavoriteProjectIds(userId);
        if (projectIds.isEmpty()) return Collections.emptyList();
        
        // 计算分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, projectIds.size());
        if (start >= projectIds.size()) return Collections.emptyList();
        
        List<Long> pagedIds = projectIds.subList(start, end);
        return projectMapper.selectBatchIds(pagedIds);
    }

    public List<Long> myFavoriteResourceIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "resource"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Resource> myFavoriteResources(Long userId) {
        List<Long> resourceIds = myFavoriteResourceIds(userId);
        if (resourceIds.isEmpty()) return Collections.emptyList();
        return resourceMapper.selectBatchIds(resourceIds);
    }

    public List<Resource> myFavoriteResources(Long userId, Integer page, Integer size) {
        List<Long> resourceIds = myFavoriteResourceIds(userId);
        if (resourceIds.isEmpty()) return Collections.emptyList();
        
        // 计算分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, resourceIds.size());
        if (start >= resourceIds.size()) return Collections.emptyList();
        
        List<Long> pagedIds = resourceIds.subList(start, end);
        return resourceMapper.selectBatchIds(pagedIds);
    }

    public List<Long> myFavoriteArticleIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "article"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Article> myFavoriteArticles(Long userId) {
        List<Long> articleIds = myFavoriteArticleIds(userId);
        if (articleIds.isEmpty()) return Collections.emptyList();
        return articleMapper.selectBatchIds(articleIds);
    }

    public List<Article> myFavoriteArticles(Long userId, Integer page, Integer size) {
        List<Long> articleIds = myFavoriteArticleIds(userId);
        if (articleIds.isEmpty()) return Collections.emptyList();
        
        // 计算分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, articleIds.size());
        if (start >= articleIds.size()) return Collections.emptyList();
        
        List<Long> pagedIds = articleIds.subList(start, end);
        return articleMapper.selectBatchIds(pagedIds);
    }

    public List<Long> myFavoritePostIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "post"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Post> myFavoritePosts(Long userId) {
        List<Long> postIds = myFavoritePostIds(userId);
        if (postIds.isEmpty()) return Collections.emptyList();
        return postMapper.selectBatchIds(postIds);
    }

    public List<Post> myFavoritePosts(Long userId, Integer page, Integer size) {
        List<Long> postIds = myFavoritePostIds(userId);
        if (postIds.isEmpty()) return Collections.emptyList();
        
        // 计算分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, postIds.size());
        if (start >= postIds.size()) return Collections.emptyList();
        
        List<Long> pagedIds = postIds.subList(start, end);
        return postMapper.selectBatchIds(pagedIds);
    }
}
