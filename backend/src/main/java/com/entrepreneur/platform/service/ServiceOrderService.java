package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.ServiceOrder;
import com.entrepreneur.platform.mapper.ServiceOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 服务订单：预约、审核、完成、列表
 */
@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderMapper serviceOrderMapper;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public ServiceOrder create(ServiceOrder order) {
        order.setStatus("pending");
        serviceOrderMapper.insert(order);
        // 发送消息通知服务提供商
        messageService.send(
                order.getBuyerId(),
                order.getProviderId(),
                order.getId(),
                "order",
                "新订单通知",
                "您收到了一笔新的服务订单，请及时处理。",
                "order"
        );
        return order;
    }

    public ServiceOrder getById(Long id) {
        return serviceOrderMapper.selectById(id);
    }

    public IPage<ServiceOrder> listByBuyer(Page<ServiceOrder> page, Long buyerId, String status) {
        LambdaQueryWrapper<ServiceOrder> q = new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getBuyerId, buyerId);
        if (StringUtils.hasText(status)) q.eq(ServiceOrder::getStatus, status);
        q.orderByDesc(ServiceOrder::getCreateTime);
        return serviceOrderMapper.selectPage(page, q);
    }

    public IPage<ServiceOrder> listByProvider(Page<ServiceOrder> page, Long providerId, String status) {
        LambdaQueryWrapper<ServiceOrder> q = new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getProviderId, providerId);
        if (StringUtils.hasText(status)) q.eq(ServiceOrder::getStatus, status);
        q.orderByDesc(ServiceOrder::getCreateTime);
        return serviceOrderMapper.selectPage(page, q);
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String rejectReason) {
        ServiceOrder o = serviceOrderMapper.selectById(id);
        if (o != null) {
            o.setStatus(approved ? "confirmed" : "cancelled");
            o.setRejectReason(rejectReason);
            serviceOrderMapper.updateById(o);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Long id, String status) {
        ServiceOrder o = serviceOrderMapper.selectById(id);
        if (o != null) {
            o.setStatus(status);
            serviceOrderMapper.updateById(o);
        }
    }
}
