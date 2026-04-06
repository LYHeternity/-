<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="800px"
    custom-class="chat-dialog"
    :close-on-click-modal="false"
    :modal="true"
  >
    <!-- 聊天内容区域 -->
    <div class="chat-container">
      <!-- 连接状态提示 -->
      <div class="connection-status" :class="connectionStatus">
        <span v-if="connectionStatus === 'connecting'" class="status-indicator connecting"></span>
        <span v-else-if="connectionStatus === 'connected'" class="status-indicator connected"></span>
        <span v-else class="status-indicator disconnected"></span>
        <span class="status-text">
          {{ connectionStatus === 'connecting' ? '连接中...' : connectionStatus === 'connected' ? '已连接' : '连接断开' }}
        </span>
        <el-button 
          v-if="connectionStatus === 'disconnected'" 
          type="text" 
          size="small" 
          @click="reconnectManually"
          class="reconnect-btn"
        >
          重试
        </el-button>
      </div>
      <!-- 快捷功能按钮 -->
      <div class="quick-actions">
        <el-button
          type="primary"
          size="small"
          @click="requestPhoneExchange"
          class="quick-action-btn"
        >
          <i class="el-icon-phone"></i>
          请求交换手机号
        </el-button>
        <el-button
          type="success"
          size="small"
          @click="showFileUpload = true"
          class="quick-action-btn"
        >
          <i class="el-icon-document"></i>
          发送创业计划书
        </el-button>
        <el-button
          type="info"
          size="small"
          @click="requestWechatExchange"
          class="quick-action-btn"
        >
          <i class="el-icon-chat-dot-round"></i>
          交换微信号
        </el-button>
      </div>

      <!-- 聊天记录 -->
      <div class="chat-messages" ref="chatMessages">
        <div
          v-for="(message, index) in messages"
          :key="message.id"
          :class="['message-item', message.isSelf ? 'self-message' : 'other-message']"
        >
          <div class="message-avatar">
            <img v-if="message.avatar" :src="message.avatar" alt="头像" />
            <span v-else>{{ message.name.charAt(0) }}</span>
          </div>
          <div class="message-content">
            <div class="message-header">
              <span class="message-name">{{ message.name }}</span>
              <span class="message-time">{{ message.time }} <span v-if="message.isSelf && message.status" class="message-status">{{ message.status === 'read' ? '已读' : '未读' }}</span></span>
            </div>
            <div :class="['message-bubble', message.isSelf ? 'self-bubble' : 'other-bubble']">
              <div v-if="message.type === 'text'" class="message-text">{{ message.content }}</div>
              <div v-else-if="message.type === 'file'" class="message-file">
                <div class="file-icon"><i class="el-icon-document"></i></div>
                <div class="file-info">
                  <div class="file-name">{{ message.filename }}</div>
                  <div class="file-size">{{ message.filesize }}</div>
                </div>
                <el-button type="text" size="small" @click="downloadFile(message.fileUrl)">
                  <i class="el-icon-download"></i>
                </el-button>
              </div>
              <div v-else-if="message.type === 'system'" class="message-system">{{ message.content }}</div>
              <div v-else-if="message.type === 'contact'" class="message-contact">
                <div class="contact-label">{{ message.contactType }}：</div>
                <div class="contact-value">{{ message.contactValue }}</div>
              </div>
            </div>
          </div>
        </div>
        <!-- 正在输入指示器 -->
        <div v-if="showTypingIndicator" class="message-item other-message">
          <div class="message-avatar">
            <img v-if="userAvatar" :src="userAvatar" alt="头像" />
            <span v-else>{{ userName.charAt(0) }}</span>
          </div>
          <div class="message-content">
            <div class="message-bubble other-bubble">
              <div class="typing-indicator">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息输入区域 -->
      <div class="chat-input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入消息..."
          @keyup.enter.native.ctrl="sendMessage"
          @input="checkMessage"
          class="message-input"
        />
        <div class="input-actions">
          <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">
            发送
          </el-button>
        </div>
      </div>
    </div>

    <!-- 文件上传对话框 -->
    <el-dialog
      title="上传创业计划书"
      :visible.sync="showFileUpload"
      width="500px"
      custom-class="file-upload-dialog"
      :modal="false"
    >
      <el-upload
        class="file-uploader"
        action="/api/upload"
        :on-success="handleFileUpload"
        :on-error="handleFileError"
        :before-upload="beforeFileUpload"
        :auto-upload="false"
        ref="upload"
      >
        <el-button type="primary">
          <i class="el-icon-upload"></i>
          选择文件
        </el-button>
        <div slot="tip" class="upload-tip">
          支持上传 Word、PDF 等常见文档格式，单个文件不超过 10MB
        </div>
      </el-upload>
      <div class="selected-files" v-if="selectedFiles.length">
        <h4>已选择文件：</h4>
        <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
          <el-button type="text" size="small" @click="removeFile(index)">
            <i class="el-icon-delete"></i>
          </el-button>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showFileUpload = false">取消</el-button>
        <el-button type="primary" @click="submitFileUpload">上传</el-button>
      </span>
    </el-dialog>

    <!-- 交换申请对话框 -->
    <el-dialog
      :title="exchangeDialogTitle"
      :visible.sync="showExchangeDialog"
      width="400px"
      custom-class="exchange-dialog"
      :modal="false"
    >
      <p class="exchange-message">{{ exchangeDialogMessage }}</p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="rejectExchange">拒绝</el-button>
        <el-button type="primary" @click="acceptExchange">接受</el-button>
      </span>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { send, chat } from '@/api/message'

export default {
  name: 'ChatDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '聊天对话'
    },
    userId: {
      type: [String, Number],
      required: true
    },
    userName: {
      type: String,
      required: true
    },
    userAvatar: {
      type: String,
      default: ''
    },
    wsUrl: {
      type: String,
      default: 'ws://localhost:8081/ws/chat'
    }
  },
  data() {
    return {
      messages: [
        {
          id: 1,
          content: '您好，我对您的项目很感兴趣，想了解更多详情。',
          time: '10:00',
          isSelf: false,
          name: this.userName,
          avatar: this.userAvatar,
          type: 'text',
          status: null
        },
        {
          id: 2,
          content: '您好，很高兴收到您的消息！请问您对项目的哪些方面感兴趣？',
          time: '10:05',
          isSelf: true,
          name: '我',
          avatar: '',
          type: 'text',
          status: 'read'
        }
      ],
      inputMessage: '',
      showFileUpload: false,
      selectedFiles: [],
      showExchangeDialog: false,
      exchangeType: '',
      exchangeDialogTitle: '',
      exchangeDialogMessage: '',
      currentExchangeRequest: null,
      mockResponses: {
        '你好': '您好！很高兴为您服务，请问有什么可以帮助您的？',
        '项目': '我们的项目主要专注于人工智能领域，目前处于快速发展阶段。',
        '融资': '我们正在寻求A轮融资，目标金额为1000万。',
        '团队': '我们的团队由来自顶尖科技公司的专业人士组成。',
        '技术': '我们拥有多项核心技术专利，在行业内处于领先地位。',
        '市场': '我们的目标市场规模超过1000亿，增长潜力巨大。',
        '合作': '我们非常欢迎与您探讨合作机会。',
        '价格': '我们的服务价格根据具体需求定制，欢迎咨询详情。',
        '时间': '我们的服务周期通常为2-4周，具体取决于项目复杂度。',
        '质量': '我们严格控制服务质量，确保为客户提供最佳体验。'
      },
      showTypingIndicator: false,
      // WebSocket相关
      ws: null,
      connectionStatus: 'disconnected', // connected, disconnected, connecting
      reconnectAttempts: 0,
      maxReconnectAttempts: 5,
      reconnectDelay: 3000
    }
  },
  watch: {
    visible(val) {
      if (val) {
        // 加载历史消息
        this.loadHistoryMessages()
        this.$nextTick(() => {
          this.scrollToBottom()
        })
        // 建立WebSocket连接
        this.connectWebSocket()
      } else {
        // 关闭WebSocket连接
        this.disconnectWebSocket()
      }
    }
  },
  methods: {
    async loadHistoryMessages() {
      try {
        const history = await chat(this.userId, 50)
        if (history && history.length) {
          this.messages = history.map(msg => ({
            id: msg.id,
            content: msg.content,
            time: this.formatMessageTime(msg.createdAt),
            isSelf: msg.fromUserId !== this.userId,
            name: msg.fromUserId !== this.userId ? '我' : this.userName,
            avatar: msg.fromUserId !== this.userId ? '' : this.userAvatar,
            type: 'text',
            status: msg.isRead ? 'read' : 'unread'
          }))
        }
      } catch (e) {
        console.error('加载历史消息失败:', e)
        // 保留默认消息作为降级方案
      }
    },
    formatMessageTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },
    async sendMessage() {
      if (!this.inputMessage.trim()) return

      // 检查是否包含隐私内容
      if (this.containsPrivacyInfo(this.inputMessage)) {
        this.$message.warning('消息中包含隐私内容，请使用快捷功能按钮进行交换');
        return
      }

      const newMessage = {
        id: Date.now(),
        content: this.inputMessage,
        time: this.getCurrentTime(),
        isSelf: true,
        name: '我',
        avatar: '',
        type: 'text',
        status: 'sending'
      }

      this.messages.push(newMessage)
      this.inputMessage = ''
      this.scrollToBottom()

      // 尝试通过后端API发送消息
      try {
        await send({
          toUserId: this.userId,
          title: '聊天消息',
          content: newMessage.content,
          type: 'chat'
        })
        
        newMessage.status = 'unread'
        this.$message.success('消息发送成功')
        
        // 模拟对方回复
        this.showTypingIndicator = true
        setTimeout(() => {
          this.showTypingIndicator = false
          newMessage.status = 'read'
          
          const replyContent = this.generateIntelligentResponse(newMessage.content)
          const replyMessage = {
            id: Date.now() + 1,
            content: replyContent,
            time: this.getCurrentTime(),
            isSelf: false,
            name: this.userName,
            avatar: this.userAvatar,
            type: 'text',
            status: null
          }
          this.messages.push(replyMessage)
          this.scrollToBottom()
        }, 1500)
      } catch (e) {
        console.error('发送消息失败:', e)
        newMessage.status = 'failed'
        this.$message.error(e.response?.data?.message || '消息发送失败，请重试')
        
        // 如果API失败，回退到模拟模式
        this.sendMockMessage()
      }
    },

    // 模拟消息发送，当WebSocket不可用时使用
    sendMockMessage() {
      const newMessage = {
        id: Date.now(),
        content: this.inputMessage,
        time: this.getCurrentTime(),
        isSelf: true,
        name: '我',
        avatar: '',
        type: 'text',
        status: 'sending'
      }

      this.messages.push(newMessage)
      this.inputMessage = ''
      this.scrollToBottom()

      // 模拟消息发送成功
      setTimeout(() => {
        newMessage.status = 'unread'
        // 模拟对方正在输入
        this.showTypingIndicator = true
        
        // 模拟对方回复，实现智能响应
        setTimeout(() => {
          this.showTypingIndicator = false
          // 标记用户消息为已读
          newMessage.status = 'read'
          
          // 生成智能回复
          const replyContent = this.generateIntelligentResponse(newMessage.content)
          const replyMessage = {
            id: Date.now() + 1,
            content: replyContent,
            time: this.getCurrentTime(),
            isSelf: false,
            name: this.userName,
            avatar: this.userAvatar,
            type: 'text',
            status: null
          }
          this.messages.push(replyMessage)
          this.scrollToBottom()
        }, 1500)
      }, 500)
    },

    // WebSocket连接相关方法
    connectWebSocket() {
      if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
        return
      }

      this.connectionStatus = 'connecting'
      
      try {
        // 确保userId是字符串类型
        const userIdStr = String(this.userId)
        this.ws = new WebSocket(`${this.wsUrl}?userId=${userIdStr}`)
        
        this.ws.onopen = () => {
          console.log('WebSocket连接已建立')
          this.connectionStatus = 'connected'
          this.reconnectAttempts = 0
          this.$message.success('已连接到聊天服务器')
        }
        
        this.ws.onmessage = (event) => {
          this.handleWebSocketMessage(event)
        }
        
        this.ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          this.connectionStatus = 'disconnected'
        }
        
        this.ws.onclose = (event) => {
          console.log('WebSocket连接已关闭:', event.code, event.reason)
          this.connectionStatus = 'disconnected'
          // 如果不是主动关闭，尝试重连
          if (!event.wasClean && this.visible) {
            this.attemptReconnect()
          }
        }
      } catch (error) {
        console.error('WebSocket连接失败:', error)
        this.connectionStatus = 'disconnected'
        this.attemptReconnect()
      }
    },

    disconnectWebSocket() {
      if (this.ws) {
        this.ws.close(1000, '用户主动关闭')
        this.ws = null
        this.connectionStatus = 'disconnected'
      }
    },

    handleWebSocketMessage(event) {
      try {
        const messageData = JSON.parse(event.data)
        
        switch (messageData.type) {
          case 'chat':
            this.handleChatMessage(messageData)
            break
          case 'typing':
            this.handleTypingMessage(messageData)
            break
          case 'read':
            this.handleReadMessage(messageData)
            break
          default:
            console.log('未知消息类型:', messageData.type)
        }
      } catch (error) {
        console.error('解析WebSocket消息失败:', error)
      }
    },

    handleChatMessage(messageData) {
      const newMessage = {
        id: messageData.id || Date.now(),
        content: messageData.content,
        time: this.getCurrentTime(),
        isSelf: false,
        name: this.userName,
        avatar: this.userAvatar,
        type: 'text',
        status: null
      }
      this.messages.push(newMessage)
      this.scrollToBottom()
    },

    handleTypingMessage(messageData) {
      if (messageData.isTyping) {
        this.showTypingIndicator = true
        // 3秒后自动隐藏
        setTimeout(() => {
          this.showTypingIndicator = false
        }, 3000)
      } else {
        this.showTypingIndicator = false
      }
    },

    handleReadMessage(messageData) {
      // 更新消息状态为已读
      const message = this.messages.find(msg => msg.id === messageData.messageId)
      if (message) {
        message.status = 'read'
      }
    },

    attemptReconnect() {
      if (this.reconnectAttempts < this.maxReconnectAttempts) {
        this.reconnectAttempts++
        console.log(`尝试重连... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
        
        setTimeout(() => {
          if (this.visible) {
            this.connectWebSocket()
          }
        }, this.reconnectDelay)
      } else {
        console.error('重连失败，已达到最大尝试次数')
        this.$message({ 
          message: '网络连接失败，可能是服务器未启动', 
          type: 'warning',
          duration: 5000,
          showClose: true
        })
      }
    },

    // 手动重连
    reconnectManually() {
      this.reconnectAttempts = 0
      this.connectWebSocket()
    },

    generateIntelligentResponse(userInput) {
      // 遍历mockResponses，寻找匹配的关键词
      for (const [keyword, response] of Object.entries(this.mockResponses)) {
        if (userInput.includes(keyword)) {
          return response
        }
      }
      // 如果没有匹配的关键词，返回默认回复
      return '感谢您的消息，我会尽快为您提供详细信息。'
    },

    checkMessage() {
      if (this.containsPrivacyInfo(this.inputMessage)) {
        this.$message.warning('消息中包含隐私内容，请使用快捷功能按钮进行交换');
      }
    },

    containsPrivacyInfo(message) {
      // 简单的隐私内容检测
      const phoneRegex = /1[3-9]\d{9}/
      const wechatRegex = /[a-zA-Z0-9_-]{6,20}/
      return phoneRegex.test(message) || wechatRegex.test(message)
    },

    requestPhoneExchange() {
      this.exchangeType = 'phone'
      this.exchangeDialogTitle = '交换手机号申请'
      this.exchangeDialogMessage = `${this.userName} 请求与您交换手机号，是否同意？`
      this.showExchangeDialog = true
      this.currentExchangeRequest = {
        type: 'phone',
        from: this.userName
      }
    },

    requestWechatExchange() {
      this.exchangeType = 'wechat'
      this.exchangeDialogTitle = '交换微信号申请'
      this.exchangeDialogMessage = `${this.userName} 请求与您交换微信号，是否同意？`
      this.showExchangeDialog = true
      this.currentExchangeRequest = {
        type: 'wechat',
        from: this.userName
      }
    },

    acceptExchange() {
      this.showExchangeDialog = false
      
      // 模拟系统消息
      const systemMessage = {
        id: Date.now(),
        content: `您已同意与 ${this.userName} 交换${this.exchangeType === 'phone' ? '手机号' : '微信号'}`,
        time: this.getCurrentTime(),
        isSelf: false,
        name: '系统',
        avatar: '',
        type: 'system'
      }
      this.messages.push(systemMessage)

      // 模拟显示联系方式
      const contactMessage = {
        id: Date.now() + 1,
        contactType: this.exchangeType === 'phone' ? '手机号' : '微信号',
        contactValue: this.exchangeType === 'phone' ? '138****1234' : 'wechat_id',
        time: this.getCurrentTime(),
        isSelf: false,
        name: '系统',
        avatar: '',
        type: 'contact'
      }
      this.messages.push(contactMessage)

      // 模拟对方联系方式
      const otherContactMessage = {
        id: Date.now() + 2,
        contactType: this.exchangeType === 'phone' ? `${this.userName}的手机号` : `${this.userName}的微信号`,
        contactValue: this.exchangeType === 'phone' ? '139****5678' : 'other_wechat_id',
        time: this.getCurrentTime(),
        isSelf: false,
        name: '系统',
        avatar: '',
        type: 'contact'
      }
      this.messages.push(otherContactMessage)

      this.scrollToBottom()
    },

    rejectExchange() {
      this.showExchangeDialog = false
      
      // 模拟系统消息
      const systemMessage = {
        id: Date.now(),
        content: `您已拒绝与 ${this.userName} 交换${this.exchangeType === 'phone' ? '手机号' : '微信号'}`,
        time: this.getCurrentTime(),
        isSelf: false,
        name: '系统',
        avatar: '',
        type: 'system'
      }
      this.messages.push(systemMessage)
      this.scrollToBottom()
    },

    beforeFileUpload(file) {
      const allowedTypes = [
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/pdf'
      ]
      const maxSize = 10 * 1024 * 1024 // 10MB

      if (!allowedTypes.includes(file.type)) {
        this.$message.error('只支持 Word 和 PDF 格式的文件');
        return false
      }

      if (file.size > maxSize) {
        this.$message.error('文件大小不能超过 10MB');
        return false
      }

      this.selectedFiles.push(file)
      return false // 阻止自动上传，手动触发
    },

    handleFileUpload(response) {
      // 模拟文件上传成功
      const fileMessage = {
        id: Date.now(),
        filename: this.selectedFiles[0].name,
        filesize: this.formatFileSize(this.selectedFiles[0].size),
        fileUrl: '/api/download/' + response.fileId,
        time: this.getCurrentTime(),
        isSelf: true,
        name: '我',
        avatar: '',
        type: 'file'
      }
      this.messages.push(fileMessage)
      this.showFileUpload = false
      this.selectedFiles = []
      this.scrollToBottom()
    },

    handleFileError() {
      this.$message.error('文件上传失败');
    },

    submitFileUpload() {
      if (this.selectedFiles.length === 0) {
        this.$message.warning('请选择文件');
        return
      }
      // 模拟文件上传
      this.handleFileUpload({ fileId: 'mock-file-id' })
    },

    removeFile(index) {
      this.selectedFiles.splice(index, 1)
    },

    downloadFile(url) {
      // 模拟文件下载
      this.$message.info('文件下载中...');
    },

    formatFileSize(size) {
      if (size < 1024) {
        return size + ' B'
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + ' KB'
      } else {
        return (size / (1024 * 1024)).toFixed(2) + ' MB'
      }
    },

    getCurrentTime() {
      const now = new Date()
      const hours = now.getHours().toString().padStart(2, '0')
      const minutes = now.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const chatMessages = this.$refs.chatMessages
        if (chatMessages) {
          chatMessages.scrollTop = chatMessages.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped>
.chat-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.chat-dialog .el-dialog__header {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 24px;
}

.chat-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.chat-dialog .el-dialog__body {
  padding: 0;
  height: 700px;
  display: flex;
  flex-direction: column;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 连接状态提示 */
.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 24px;
  font-size: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-indicator.connecting {
  background-color: #f59e0b;
  animation: pulse 1.5s infinite;
}

.status-indicator.connected {
  background-color: #10b981;
}

.status-indicator.disconnected {
  background-color: #ef4444;
}

.status-text {
  color: #64748b;
}

.connection-status.connecting .status-text {
  color: #f59e0b;
}

.connection-status.connected .status-text {
  color: #10b981;
}

.connection-status.disconnected .status-text {
  color: #ef4444;
}

.reconnect-btn {
  margin-left: auto;
  font-size: 12px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 快捷功能按钮 */
.quick-actions {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.quick-action-btn {
  flex: 1;
  justify-content: center;
  gap: 6px;
  border-radius: 8px;
  font-size: 14px;
  padding: 8px 16px;
}

/* 聊天记录 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #ffffff;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: flex-start;
}

.self-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007dff, #0052a3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  flex-shrink: 0;
  overflow: hidden;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.message-content {
  max-width: 70%;
}

.self-message .message-content {
  text-align: right;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.self-message .message-header {
  flex-direction: row-reverse;
}

.message-name {
  font-weight: 600;
  color: #1e293b;
  margin-right: 8px;
}

.self-message .message-name {
  margin-right: 0;
  margin-left: 8px;
}

.message-time {
  color: #94a3b8;
}

/* 消息状态 */
.message-status {
  font-size: 11px;
  color: #94a3b8;
  margin-left: 4px;
}

/* 正在输入指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #94a3b8;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  display: inline-block;
  text-align: left;
}

.other-bubble {
  background: #f1f5f9;
  color: #1e293b;
  border-bottom-left-radius: 4px;
}

.self-bubble {
  background: #3b82f6;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-text {
  line-height: 1.6;
  font-size: 15px;
  font-weight: 400;
}

.other-bubble .message-text {
  color: #1e293b;
  font-weight: 500;
}

.self-bubble .message-text {
  color: #ffffff;
  font-weight: 500;
}

.message-file {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
}

.file-icon {
  font-size: 24px;
  color: #64748b;
}

.self-bubble .file-icon {
  color: rgba(255, 255, 255, 0.8);
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #64748b;
}

.self-bubble .file-size {
  color: rgba(255, 255, 255, 0.7);
}

.message-system {
  background: #fffbeb;
  color: #f59e0b;
  font-size: 12px;
  text-align: center;
  padding: 6px 12px;
  border-radius: 12px;
  margin: 10px 0;
}

.message-contact {
  background: #e0f2fe;
  color: #0284c7;
  padding: 12px 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-label {
  font-weight: 600;
}

.contact-value {
  font-weight: 500;
}

/* 消息输入区域 */
.chat-input-area {
  border-top: 1px solid #e2e8f0;
  padding: 16px 24px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-input {
  border-radius: 8px;
  resize: none;
}

.message-input textarea {
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.message-input textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
}

.input-actions .el-button {
  border-radius: 8px;
  padding: 8px 24px;
  font-weight: 500;
}

/* 文件上传对话框 */
.file-upload-dialog .el-dialog__header {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.file-upload-dialog .el-dialog__body {
  padding: 24px;
}

.file-uploader {
  margin-bottom: 24px;
}

.upload-tip {
  font-size: 14px;
  color: #64748b;
  margin-top: 12px;
}

.selected-files {
  margin-top: 24px;
}

.selected-files h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 8px;
}

.file-item .file-name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 12px;
}

.file-item .file-size {
  font-size: 12px;
  color: #64748b;
  margin-right: 12px;
}

/* 交换申请对话框 */
.exchange-dialog .el-dialog__header {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.exchange-dialog .el-dialog__body {
  padding: 24px;
  text-align: center;
}

.exchange-message {
  font-size: 16px;
  color: #1e293b;
  line-height: 1.5;
  margin: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 8px 24px;
  font-weight: 500;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-dialog {
    width: 90% !important;
  }

  .chat-dialog .el-dialog__body {
    height: 500px;
  }

  .quick-actions {
    flex-direction: column;
    gap: 8px;
  }

  .quick-action-btn {
    width: 100%;
  }

  .chat-messages {
    padding: 16px;
  }

  .message-content {
    max-width: 80%;
  }

  .chat-input-area {
    padding: 12px 16px;
  }
}
</style>