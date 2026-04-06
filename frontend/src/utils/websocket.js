import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
  }

  connect() {
    if (this.connected) return

    // 尝试连接 WebSocket 服务
    try {
      // 只在生产环境中连接WebSocket，开发环境可跳过
      if (process.env.NODE_ENV === 'production') {
        this.stompClient = new Client({
          webSocketFactory: () => new SockJS('/api/ws'),
          onConnect: () => {
            this.connected = true

            // 订阅新文章通知
            this.stompClient.subscribe('/topic/articles/new', (message) => {
              const articleCount = parseInt(message.body)
              this.handleNewArticles(articleCount)
            })

            // 订阅文章更新通知
            this.stompClient.subscribe('/topic/articles/update', (message) => {
              const updateMessage = message.body
              this.handleArticleUpdate(updateMessage)
            })
          },
          onStompError: (error) => {
            this.connected = false
          },
          reconnectDelay: 10000 // 增加重连延迟，减少频繁重连
        })

        this.stompClient.activate()
      }
    } catch (error) {
      // 静默处理WebSocket错误，避免影响页面加载
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate()
      this.stompClient = null
      this.connected = false
      console.log('WebSocket disconnected')
    }
  }

  handleNewArticles(articleCount) {
    // 触发全局事件，通知组件有新文章
    window.dispatchEvent(new CustomEvent('newArticles', { detail: articleCount }))
  }

  handleArticleUpdate(message) {
    // 触发全局事件，通知组件文章更新
    window.dispatchEvent(new CustomEvent('articleUpdate', { detail: message }))
  }
}

// 导出单例
export default new WebSocketService()
