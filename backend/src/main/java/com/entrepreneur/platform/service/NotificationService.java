package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.Notification;
import com.entrepreneur.platform.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统通知：列表、已读
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public void add(Long userId, String title, String content, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setType(type);
        n.setIsRead(0);
        notificationMapper.insert(n);
    }

    public IPage<Notification> listByUser(Page<Notification> page, Long userId, String type, Integer isRead) {
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, userId);
        if (StringUtils.hasText(type)) q.eq(Notification::getType, type);
        if (isRead != null) q.eq(Notification::getIsRead, isRead);
        q.orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(page, q);
    }

    public long countUnread(Long userId) {
        return notificationMapper.selectCount(new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0));
    }

    public void markRead(Long id) {
        Notification n = notificationMapper.selectById(id);
        if (n != null) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
    }
}
