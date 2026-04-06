<template>
  <div class="page-card order-detail-page">
    <div class="detail-header">
      <div class="header-content">
        <h1 class="page-title">订单详情</h1>
        <p class="page-subtitle">查看订单的详细信息</p>
      </div>
      <el-button type="primary" @click="goBack">
        <i class="el-icon-back"></i>
        <span>返回列表</span>
      </el-button>
    </div>
    
    <!-- 聊天对话窗口 -->
    <ChatDialog
      :visible.sync="showChat"
      title="与对方聊天"
      :user-id="chatUserId"
      :user-name="chatUserName"
      :user-avatar="chatUserAvatar"
    />
    
    <div v-loading="loading" class="detail-content">
      <!-- 订单基本信息 -->
      <div class="info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">订单号：</span>
            <span class="info-value">{{ order.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">服务名称：</span>
            <span class="info-value">{{ order.serviceName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">订单金额：</span>
            <span class="info-value amount">{{ order.amount }} 元</span>
          </div>
          <div class="info-item">
            <span class="info-label">订单状态：</span>
            <el-tag :type="getStatusType(order.status)" class="status-tag">
              {{ statusText(order.status) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="info-label">创建时间：</span>
            <span class="info-value">{{ order.createTime }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">交付时间：</span>
            <span class="info-value">{{ order.deliveryTime || '未设置' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 服务信息 -->
      <div class="info-section">
        <h3 class="section-title">服务信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">服务ID：</span>
            <span class="info-value">{{ order.resourceId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">数量：</span>
            <span class="info-value">{{ order.quantity || 1 }}</span>
          </div>
        </div>
      </div>
      
      <!-- 用户信息 -->
      <div class="info-section">
        <h3 class="section-title">用户信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">购买方：</span>
            <span class="info-value">{{ order.buyerName || '未知' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">提供方：</span>
            <span class="info-value">{{ order.providerName || '未知' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 订单描述 -->
      <div class="info-section" v-if="order.description">
        <h3 class="section-title">订单描述</h3>
        <div class="description-content">
          {{ order.description }}
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-section">
        <h3 class="section-title">操作</h3>
        <div class="action-buttons">
          <el-button type="primary" @click="contactUser">
            <i class="el-icon-chat-dot-square"></i>
            <span>联系对方</span>
          </el-button>
          
          <el-button 
            v-if="order.status === 'unpaid'" 
            type="success" 
            @click="payOrder"
          >
            <i class="el-icon-money"></i>
            <span>支付</span>
          </el-button>
          
          <el-button 
            v-if="order.status === 'pending'" 
            type="warning" 
            @click="confirmOrder"
          >
            <i class="el-icon-check"></i>
            <span>确认订单</span>
          </el-button>
          
          <el-button 
            v-if="order.status === 'serving'" 
            type="success" 
            @click="completeOrder"
          >
            <i class="el-icon-finished"></i>
            <span>确认完成</span>
          </el-button>
          
          <el-button 
            v-if="['unpaid', 'pending'].includes(order.status)" 
            type="danger" 
            @click="cancelOrder"
          >
            <i class="el-icon-close"></i>
            <span>取消订单</span>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getDetail, setStatus } from '@/api/order'
import ChatDialog from '@/components/ChatDialog.vue'

export default {
  name: 'OrderDetail',
  components: {
    ChatDialog
  },
  data() {
    return {
      loading: false,
      order: {},
      showChat: false,
      chatUserId: '',
      chatUserName: '',
      chatUserAvatar: ''
    }
  },
  created() {
    this.loadOrderDetail()
  },
  methods: {
    statusText(s) {
      return { 
        unpaid: '待支付',
        pending: '待确认', 
        serving: '进行中', 
        completed: '已完成', 
        closed: '已关闭' 
      }[s] || s
    },
    getStatusType(status) {
      const typeMap = {
        unpaid: 'warning',
        pending: 'info',
        serving: 'primary',
        completed: 'success',
        closed: 'danger'
      }
      return typeMap[status] || 'info'
    },
    async loadOrderDetail() {
      this.loading = true
      try {
        const { id } = this.$route.params
        const res = await getDetail(id)
        this.order = res
      } catch (error) {
        this.$message.error('获取订单详情失败')
      } finally {
        this.loading = false
      }
    },
    goBack() {
      this.$router.back()
    },
    contactUser() {
      // 打开聊天对话框
      this.chatUserId = this.order.id // 实际应用中应该使用真实的用户ID
      this.chatUserName = this.order.providerName || '对方用户'
      this.chatUserAvatar = '' // 实际应用中应该使用真实的用户头像
      this.showChat = true
    },
    async payOrder() {
      try {
        await setStatus(this.order.id, 'paid')
        this.$message.success('支付成功')
        this.loadOrderDetail()
      } catch (error) {
        this.$message.error('支付失败')
      }
    },
    async confirmOrder() {
      try {
        await setStatus(this.order.id, 'serving')
        this.$message.success('订单已确认')
        this.loadOrderDetail()
      } catch (error) {
        this.$message.error('确认失败')
      }
    },
    async completeOrder() {
      try {
        await setStatus(this.order.id, 'completed')
        this.$message.success('订单已完成')
        this.loadOrderDetail()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async cancelOrder() {
      this.$confirm('确定要取消这个订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await setStatus(this.order.id, 'closed')
          this.$message.success('订单已取消')
          this.loadOrderDetail()
        } catch (error) {
          this.$message.error('取消失败')
        }
      }).catch(() => {
        // 取消操作
      })
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.page-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  margin: 20px 0;
}

/* 详情头部 */
.detail-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #3b82f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 信息区块 */
.info-section {
  background: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  min-width: 100px;
}

.info-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

/* 金额样式 */
.amount {
  font-size: 16px;
  font-weight: 600;
  color: #3b82f6;
}

/* 状态标签 */
.status-tag {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  font-weight: 500;
}

/* 描述内容 */
.description-content {
  font-size: 14px;
  line-height: 1.6;
  color: #4a5568;
  background: #ffffff;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

/* 操作区块 */
.action-section {
  background: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  padding: 8px 16px;
}

.el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: #ffffff;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-card {
    padding: 16px;
    margin: 12px 0;
  }
  
  .detail-header {
    margin-bottom: 16px;
    padding-bottom: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .info-section {
    padding: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .page-card {
    padding: 12px;
  }
  
  .detail-header {
    margin-bottom: 12px;
    padding-bottom: 12px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .info-section {
    padding: 12px;
  }
  
  .section-title {
    font-size: 14px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-label {
    min-width: auto;
  }
}
</style>