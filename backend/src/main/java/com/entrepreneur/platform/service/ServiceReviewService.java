package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.ServiceReview;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.ServiceReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务评价
 */
@Service
@RequiredArgsConstructor
public class ServiceReviewService {

    private final ServiceReviewMapper serviceReviewMapper;
    private final ResourceMapper resourceMapper;

    public ServiceReview add(Long orderId, Long resourceId, Long userId, Integer rating, String content) {
        ServiceReview r = new ServiceReview();
        r.setOrderId(orderId);
        r.setResourceId(resourceId);
        r.setUserId(userId);
        r.setRating(rating);
        r.setContent(content);
        serviceReviewMapper.insert(r);
        updateResourceRating(resourceId);
        return r;
    }

    public List<ServiceReview> listByResource(Long resourceId) {
        return serviceReviewMapper.selectList(new LambdaQueryWrapper<ServiceReview>().eq(ServiceReview::getResourceId, resourceId).orderByDesc(ServiceReview::getCreateTime));
    }

    private void updateResourceRating(Long resourceId) {
        List<ServiceReview> list = serviceReviewMapper.selectList(new LambdaQueryWrapper<ServiceReview>().eq(ServiceReview::getResourceId, resourceId));
        if (list.isEmpty()) return;
        double avg = list.stream().mapToInt(ServiceReview::getRating).average().orElse(0);
        Resource r = resourceMapper.selectById(resourceId);
        if (r != null) {
            r.setRating(java.math.BigDecimal.valueOf(avg));
            resourceMapper.updateById(r);
        }
    }
}
