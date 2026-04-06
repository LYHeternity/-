package com.entrepreneur.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket服务，用于发送实时通知
 */
@Service
public class WebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 发送新文章通知
     * @param articleCount 新文章数量
     */
    public void sendNewArticleNotification(int articleCount) {
        messagingTemplate.convertAndSend("/topic/articles/new", articleCount);
    }
    
    /**
     * 发送文章更新通知
     * @param message 通知消息
     */
    public void sendArticleUpdateNotification(String message) {
        messagingTemplate.convertAndSend("/topic/articles/update", message);
    }
}
