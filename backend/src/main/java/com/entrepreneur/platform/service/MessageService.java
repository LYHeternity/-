package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.Message;
import com.entrepreneur.platform.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 消息：发送、列表、已读、删除
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public void send(Long fromUserId, Long toUserId, Long targetId, String targetType, String title, String content, String type) {
        Message m = new Message();
        m.setFromUserId(fromUserId);
        m.setToUserId(toUserId);
        m.setTargetId(targetId);
        m.setTargetType(targetType);
        m.setTitle(title);
        m.setContent(content);
        m.setType(type);
        m.setIsRead(0);
        messageMapper.insert(m);
    }

    public IPage<Message> listByUser(Page<Message> page, Long toUserId, String type, Integer isRead) {
        LambdaQueryWrapper<Message> q = new LambdaQueryWrapper<Message>().eq(Message::getToUserId, toUserId);
        if (StringUtils.hasText(type)) q.eq(Message::getType, type);
        if (isRead != null) q.eq(Message::getIsRead, isRead);
        q.orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(page, q);
    }

    public long countUnread(Long toUserId) {
        return messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getToUserId, toUserId).eq(Message::getIsRead, 0));
    }

    public void markRead(Long id) {
        Message m = messageMapper.selectById(id);
        if (m != null) {
            m.setIsRead(1);
            messageMapper.updateById(m);
        }
    }

    public void markReadBatch(List<Long> ids) {
        for (Long id : ids) markRead(id);
    }

    public List<Message> listChat(Long user1, Long user2, int limit) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getType, "chat")
                        .and(w -> w.and(a -> a.eq(Message::getFromUserId, user1).eq(Message::getToUserId, user2))
                                .or(a -> a.eq(Message::getFromUserId, user2).eq(Message::getToUserId, user1)))
                        .orderByDesc(Message::getCreateTime)
                        .last("LIMIT " + limit));
    }
}
