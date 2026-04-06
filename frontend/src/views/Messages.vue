<template>
  <div class="page-card messages-page">
    <div class="messages-header">
      <h3>消息中心</h3>
      <div class="header-actions">
        <el-button 
          type="primary" 
          size="small" 
          :disabled="!hasUnreadMessages"
          @click="markAllRead"
        >
          全部标已读
        </el-button>
        <el-button 
          size="small"
          @click="refreshMessages"
        >
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
    </div>
    <div class="filter-bar hw-filter-bar">
      <div class="filter-section">
        <el-radio-group v-model="filterType" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="read">已读</el-radio-button>
          <el-radio-button label="system">系统通知</el-radio-button>
          <el-radio-button label="project">项目消息</el-radio-button>
          <el-radio-button label="investment">投资消息</el-radio-button>
          <el-radio-button label="order">订单消息</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索消息内容"
          size="small"
          clearable
          style="width: 200px;"
        >
          <el-button slot="append" icon="el-icon-search"></el-button>
        </el-input>
      </div>
    </div>
    <div class="message-list" v-loading="loading">
      <div
        v-for="(item, idx) in displayList"
        :key="item.id || idx"
        class="message-card"
        :class="{ unread: !item.isRead, read: item.isRead }"
      >
        <div class="message-icon">
          <i :class="getMessageIcon(item.type)"></i>
          <span v-if="!item.isRead" class="red-dot"></span>
        </div>
        <div class="msg-body">
          <div class="msg-header">
            <div class="msg-title">{{ item.title }}</div>
            <div class="msg-actions">
              <el-button v-if="!item.isRead" type="text" size="small" @click.stop="markRead(item.id)">
                标已读
              </el-button>
              <el-button type="text" size="small" @click.stop="showMessageDetail(item)">
                详情
              </el-button>
            </div>
          </div>
          <div class="msg-content">{{ item.content }}</div>
          <div class="msg-meta">
            <el-tag size="mini" :type="getMessageTypeColor(item.type)">{{ item.type || '系统' }}</el-tag>
            <span class="msg-time">{{ formatTime(item.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!loading && !displayList.length" class="data-empty hw-empty">
      <div class="empty-icon"><i class="el-icon-message"></i></div>
      <p>暂无消息</p>
      <p class="empty-tip">系统通知与业务消息将在此展示</p>
    </div>
    <el-pagination v-if="page.total > 0" style="margin-top:16px" :current-page="page.current" :page-size="page.size" :total="page.total" layout="total, prev, pager, next" @current-change="onPage" />
    
    <!-- 消息详情对话框 -->
    <el-dialog
      :title="selectedMessage ? selectedMessage.title : '消息详情'"
      :visible.sync="messageDetailVisible"
      width="500px"
    >
      <div v-if="selectedMessage" class="message-detail">
        <div class="detail-meta">
          <el-tag size="small" :type="getMessageTypeColor(selectedMessage.type)">{{ selectedMessage.type || '系统' }}</el-tag>
          <span class="detail-time">{{ formatTime(selectedMessage.createTime) }}</span>
        </div>
        <div class="detail-content">{{ selectedMessage.content }}</div>
        <div class="detail-actions" v-if="!selectedMessage.isRead">
          <el-button type="primary" size="small" @click="markRead(selectedMessage.id)">
            标记为已读
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { list, markRead as apiMarkRead } from '@/api/message'
import { mapState, mapActions } from 'vuex'
const MOCK_LIST = [
  { id: 1, title: '项目审核通过', content: '您发布的「智能硬件孵化项目」已通过审核并上线。现在可以在项目大厅查看您的项目详情。', type: '项目', isRead: false, createTime: '2025-01-22 10:00' },
  { id: 2, title: '新投资意向', content: '有投资人对您的项目「企业SaaS平台」提交了意向，请及时查看并回复。', type: '投资', isRead: false, createTime: '2025-01-21 15:30' },
  { id: 3, title: '订单确认', content: '您的服务订单 #1003 已被服务商确认，预计将在3个工作日内完成。', type: '订单', isRead: true, createTime: '2025-01-21 09:20' },
  { id: 4, title: '系统维护通知', content: '平台将于本周六 02:00-04:00 进行例行维护，请提前保存数据。维护期间部分功能可能暂时不可用。', type: '系统通知', isRead: true, createTime: '2025-01-20 18:00' },
  { id: 5, title: '服务订单待确认', content: '您预约的「市场推广」服务待服务商确认，请耐心等待。', type: '订单', isRead: false, createTime: '2025-01-20 14:00' },
  { id: 6, title: '项目被拒绝', content: '您发布的「医疗健康数字化」未通过审核，原因：资料不完整。请补充相关材料后重新提交。', type: '项目', isRead: true, createTime: '2025-01-19 11:00' },
  { id: 7, title: '欢迎使用平台', content: '欢迎注册创业者综合服务平台，祝您创业顺利。平台提供项目发布、融资对接、服务预约等多种功能。', type: '系统通知', isRead: true, createTime: '2025-01-18 08:00' },
  { id: 8, title: '投资意向回复', content: '创业者已回复您对「新能源出行」的投资意向，对方表示愿意进一步沟通。', type: '投资', isRead: false, createTime: '2025-01-17 16:45' },
  { id: 9, title: '订单完成', content: '您的订单 #1001 已完成，欢迎评价服务质量。您的反馈对我们非常重要。', type: '订单', isRead: true, createTime: '2025-01-16 12:00' },
  { id: 10, title: '新版本说明', content: '平台已更新至 v1.1，新增消息分类与筛选功能，优化了用户体验。', type: '系统通知', isRead: true, createTime: '2025-01-15 10:00' },
  { id: 11, title: '项目浏览提醒', content: '您的项目「智能硬件孵化」在过去24小时内获得了5次浏览，1个收藏。', type: '项目', isRead: false, createTime: '2025-01-14 09:30' },
  { id: 12, title: '服务推荐', content: '根据您的项目需求，我们为您推荐了3个相关的专业服务，点击查看详情。', type: '系统通知', isRead: false, createTime: '2025-01-13 14:20' },
  { id: 13, title: '活动邀请', content: '平台将于下周三举办线上创业沙龙，主题为「早期项目融资策略」，邀请您参加。', type: '系统通知', isRead: true, createTime: '2025-01-12 16:00' },
  { id: 14, title: '账户安全提醒', content: '您的账户在新设备上登录，如非本人操作，请及时修改密码。', type: '系统通知', isRead: true, createTime: '2025-01-11 20:15' },
  { id: 15, title: '融资机会', content: '有机构投资者正在寻找您所在行业的优质项目，您的项目符合初步筛选条件。', type: '投资', isRead: false, createTime: '2025-01-10 11:45' }
]
export default {
  name: 'Messages',
  data() {
    return {
      loading: false,
      list: [],
      filterType: '',
      searchKeyword: '',
      page: { current: 1, size: 20, total: 0 },
      messageDetailVisible: false,
      selectedMessage: null
    }
  },
  computed: {
    ...mapState('message', ['unreadCount']),
    displayList() {
      let list = this.list.length ? this.list.map(item => ({
        ...item,
        isRead: Boolean(item.isRead)
      })) : MOCK_LIST
      
      // 应用筛选类型
      if (this.filterType) {
        if (this.filterType === 'unread') {
          list = list.filter(item => !item.isRead)
        } else if (this.filterType === 'read') {
          list = list.filter(item => item.isRead)
        } else if (this.filterType === 'system') {
          list = list.filter(item => item.type === '系统通知' || item.type === 'system')
        } else if (this.filterType === 'project') {
          list = list.filter(item => item.type === '项目' || item.type === 'project')
        } else if (this.filterType === 'investment') {
          list = list.filter(item => item.type === '投资' || item.type === 'investment')
        } else if (this.filterType === 'order') {
          list = list.filter(item => item.type === '订单' || item.type === 'order')
        }
      }
      
      // 应用搜索关键词
      if (this.searchKeyword) {
        list = list.filter(item => 
          item.title.includes(this.searchKeyword) || 
          item.content.includes(this.searchKeyword)
        )
      }
      
      return list
    },
    hasUnreadMessages() {
      return this.displayList.some(item => !item.isRead)
    }
  },
  created() {
    this.load()
  },
  methods: {
    ...mapActions('message', ['updateUnreadCount']),

    async load() {
      this.loading = true
      try {
        const res = await list({ 
          current: this.page.current, 
          size: this.page.size, 
          type: this.filterType || undefined 
        })
        this.list = res.records || []
        this.page.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    onPage(p) {
      this.page.current = p
      this.load()
    },
    async markRead(id) {
      if (this.list.length) {
        await apiMarkRead(id)
        await this.load()
      } else {
        const item = this.displayList.find((m) => m.id === id)
        if (item) item.isRead = true
      }
      if (this.selectedMessage && this.selectedMessage.id === id) {
        this.selectedMessage.isRead = true
      }
      
      // 更新 Vuex 中的未读消息数量
      const allMessages = this.list.length ? this.list.map(item => ({ ...item, isRead: Boolean(item.isRead) })) : MOCK_LIST
      const newUnreadCount = allMessages.filter(item => !item.isRead).length
      this.updateUnreadCount(newUnreadCount)
    },
    async markAllRead() {
      // 获取所有未读消息（包括不在当前显示列表中的）
      let allMessages = this.list.length ? this.list.map(item => ({ ...item, isRead: Boolean(item.isRead) })) : MOCK_LIST
      const unreadMessages = allMessages.filter(item => !item.isRead)
      
      for (const item of unreadMessages) {
        await this.markRead(item.id)
      }
      
      // 重新加载消息列表，确保数据更新
      if (this.list.length) {
        await this.load()
      }
      
      // 更新 Vuex 中的未读消息数量为 0
      this.updateUnreadCount(0)
      
      this.$message.success('已全部标记为已读')
    },
    refreshMessages() {
      this.load()
      this.$message.success('消息已刷新')
    },
    async showMessageDetail(item) {
      this.selectedMessage = item
      this.messageDetailVisible = true
      if (!item.isRead) {
        await this.markRead(item.id)
      }
    },
    getMessageIcon(type) {
      const iconMap = {
        '系统通知': 'el-icon-info',
        '项目': 'el-icon-document',
        '投资': 'el-icon-coin',
        '订单': 'el-icon-s-order',
        'system': 'el-icon-info',
        'project': 'el-icon-document',
        'investment': 'el-icon-coin',
        'order': 'el-icon-s-order'
      }
      return iconMap[type] || 'el-icon-message'
    },
    getMessageTypeColor(type) {
      const colorMap = {
        '系统通知': 'info',
        '项目': 'primary',
        '投资': 'warning',
        '订单': 'success',
        'system': 'info',
        'project': 'primary',
        'investment': 'warning',
        'order': 'success'
      }
      return colorMap[type] || 'info'
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.messages-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.messages-header h3 {
  margin-bottom: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--hw-text);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-bar {
  margin-bottom: 20px;
}

.hw-filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--hw-border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.filter-section {
  margin-bottom: 12px;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid var(--hw-border);
  position: relative;
  transition: all 0.3s ease;
}

.message-card:hover {
  border-color: var(--hw-primary-light);
  box-shadow: 0 4px 20px rgba(0, 125, 255, 0.1);
  transform: translateY(-2px);
}

.message-card.unread {
  background: #f8fafc;
  border-left: 4px solid var(--hw-primary);
}

.message-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--hw-primary-light);
  color: var(--hw-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  position: relative;
  flex-shrink: 0;
}

.message-icon .red-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 10px;
  height: 10px;
  background: #ef4444;
  border-radius: 50%;
  border: 2px solid white;
}

.msg-body {
  flex: 1;
  min-width: 0;
}

.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.msg-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--hw-text);
  margin-bottom: 4px;
}

.message-card.unread .msg-title {
  font-weight: 700;
}

.msg-actions {
  display: flex;
  gap: 8px;
}

.msg-content {
  font-size: 14px;
  color: var(--hw-text-secondary);
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.msg-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--hw-text-secondary);
}

.msg-time {
  flex: 1;
  text-align: right;
}

.message-detail {
  padding: 20px 0;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--hw-text-secondary);
  margin-bottom: 24px;
  white-space: pre-line;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
}

.hw-empty .empty-tip {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .filter-section .el-input {
    width: 100% !important;
  }
  
  .message-card {
    padding: 16px;
  }
  
  .msg-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .msg-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .msg-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .msg-time {
    text-align: left;
  }
}
</style>
