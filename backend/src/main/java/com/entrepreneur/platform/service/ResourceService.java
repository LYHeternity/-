package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.entrepreneur.platform.service.RedisService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源/服务：发布、编辑、列表、详情
 */
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceMapper resourceMapper;
    private final UserMapper userMapper;
    // 声明Redis服务，用于缓存热门服务
    private final RedisService redisService;

    public Resource publish(Resource resource) {
        resource.setStatus("pending");
        resource.setViewCount(0);
        resource.setFavoriteCount(0);
        resourceMapper.insert(resource);
        return resource;
    }

    public Resource getById(Long id) {
        return resourceMapper.selectById(id);
    }

    public void incViewCount(Long id) {
        Resource r = resourceMapper.selectById(id);
        if (r != null) {
            r.setViewCount(r.getViewCount() + 1);
            resourceMapper.updateById(r);
        }
    }

    public IPage<Resource> listMy(Page<Resource> page, Long providerId, String status) {
        LambdaQueryWrapper<Resource> q = new LambdaQueryWrapper<Resource>().eq(Resource::getProviderId, providerId);
        if (StringUtils.hasText(status)) q.eq(Resource::getStatus, status);
        q.orderByDesc(Resource::getCreateTime);
        return resourceMapper.selectPage(page, q);
    }

    public IPage<Resource> listPublic(Page<Resource> page, String category, String type,
                                      BigDecimal priceMin, BigDecimal priceMax, String orderBy) {
        LambdaQueryWrapper<Resource> q = new LambdaQueryWrapper<Resource>().eq(Resource::getStatus, "online");
        if (StringUtils.hasText(category)) q.eq(Resource::getCategory, category);
        if (StringUtils.hasText(type)) q.eq(Resource::getType, type);
        if (priceMin != null) q.ge(Resource::getPrice, priceMin);
        if (priceMax != null) q.le(Resource::getPrice, priceMax);
        if ("rating".equals(orderBy)) q.orderByDesc(Resource::getRating);
        else if ("view".equals(orderBy)) q.orderByDesc(Resource::getViewCount);
        else q.orderByDesc(Resource::getCreateTime);
        return resourceMapper.selectPage(page, q);
    }

    public void update(Resource resource) {
        resourceMapper.updateById(resource);
    }

    public void setStatus(Long id, String status, String rejectReason) {
        Resource r = resourceMapper.selectById(id);
        if (r != null) {
            r.setStatus(status);
            if ("rejected".equals(status) && rejectReason != null) {
                // 这里可以添加保存拒绝原因的逻辑
                // 例如，r.setRejectReason(rejectReason);
            }
            resourceMapper.updateById(r);
        }
    }

    public void setStatus(Long id, String status) {
        setStatus(id, status, null);
    }

    public IPage<Resource> listPending(Page<Resource> page) {
        IPage<Resource> result = resourceMapper.selectPage(page,
                new LambdaQueryWrapper<Resource>().eq(Resource::getStatus, "pending").orderByDesc(Resource::getCreateTime));
        
        // 补充提交人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            List<Long> providerIds = result.getRecords().stream()
                    .map(Resource::getProviderId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!providerIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(providerIds);
                Map<Long, User> userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                
                for (Resource resource : result.getRecords()) {
                    if (resource.getProviderId() != null) {
                        User user = userMap.get(resource.getProviderId());
                        if (user != null) {
                            String submitter = user.getRealName() != null ? user.getRealName() : user.getUsername();
                            resource.setSubmitter(submitter);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    public IPage<Resource> listAll(Page<Resource> page, String keyword, String category, String status) {
        LambdaQueryWrapper<Resource> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            q.like(Resource::getTitle, keyword).or().like(Resource::getCategory, keyword);
        }
        if (StringUtils.hasText(category)) {
            q.eq(Resource::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            q.eq(Resource::getStatus, status);
        }
        q.orderByDesc(Resource::getCreateTime);
        IPage<Resource> result = resourceMapper.selectPage(page, q);
        
        // 补充提交人信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            // 收集所有提供商ID
            List<Long> providerIds = result.getRecords().stream()
                    .map(Resource::getProviderId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!providerIds.isEmpty()) {
                // 批量查询用户信息
                List<User> users = userMapper.selectBatchIds(providerIds);
                Map<Long, User> userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                
                // 为每个服务添加提交人信息
                for (Resource resource : result.getRecords()) {
                    if (resource.getProviderId() != null) {
                        User user = userMap.get(resource.getProviderId());
                        if (user != null) {
                            String submitter = user.getRealName() != null ? user.getRealName() : user.getUsername();
                            resource.setSubmitter(submitter);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    public void removeById(Long id) {
        resourceMapper.deleteById(id);
    }

    /**
     * 获取相关服务推荐
     * @param resourceId 当前服务ID
     * @param limit 推荐数量
     * @return 相关服务列表
     */
    public java.util.List<Resource> getRelatedResources(Long resourceId, int limit) {
        Resource currentResource = resourceMapper.selectById(resourceId);
        if (currentResource == null) {
            return Collections.emptyList();
        }

        // 根据类别和类型推荐相似服务，排除当前服务
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Resource> q = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Resource>()
                .eq(Resource::getStatus, "online")
                .ne(Resource::getId, resourceId);

        // 优先推荐同类别服务
        if (org.springframework.util.StringUtils.hasText(currentResource.getCategory())) {
            q.eq(Resource::getCategory, currentResource.getCategory());
        }

        // 如果同类别服务不足，再推荐同类型服务
        if (org.springframework.util.StringUtils.hasText(currentResource.getType())) {
            q.or().eq(Resource::getType, currentResource.getType());
        }

        q.orderByDesc(Resource::getRating)
         .orderByDesc(Resource::getViewCount)
         .orderByDesc(Resource::getCreateTime)
         .last("LIMIT " + limit);

        return resourceMapper.selectList(q);
    }

    /**
     * 获取热门服务列表
     * @param limit 数量限制
     * @return 热门服务列表
     */
    public java.util.List<Resource> getHotResources(int limit) {
        // 生成缓存键
        String cacheKey = "resources:hot:" + limit;
        
        // 尝试从Redis缓存中获取数据
        java.util.List<Resource> cachedResult = (java.util.List<Resource>) redisService.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        LambdaQueryWrapper<Resource> q = new LambdaQueryWrapper<Resource>()
                .eq(Resource::getStatus, "online")
                .orderByDesc(Resource::getRating)
                .orderByDesc(Resource::getViewCount)
                .last("LIMIT " + limit);
        
        java.util.List<Resource> result = resourceMapper.selectList(q);
        
        // 将结果存入Redis缓存，设置过期时间为15分钟
        if (result != null) {
            redisService.set(cacheKey, result, 900);
        }
        
        return result;
    }
}
