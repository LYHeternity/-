<template>
  <div class="page-card my-list-page">
    <div class="list-header">
      <div class="header-content">
        <h1 class="page-title">我的订单</h1>
        <p class="page-subtitle">查看和管理您的服务订单</p>
      </div>
      <!-- 订单类型切换 -->
      <div class="order-type-tabs">
        <el-radio-group v-model="orderType" @change="handleOrderTypeChange">
          <el-radio-button label="buy">我发起的订单</el-radio-button>
          <el-radio-button label="sell">我收到的订单</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <!-- 订单数据概览 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon order-icon">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalOrders }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending-icon">
          <i class="el-icon-time"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ pendingOrders }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon completed-icon">
          <i class="el-icon-finished"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ completedOrders }}</div>
          <div class="stat-label">已完成订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon amount-icon">
          <i class="el-icon-money"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">¥{{ totalAmount }}</div>
          <div class="stat-label">总交易金额</div>
        </div>
      </div>
    </div>
    
    <!-- 聊天对话窗口 -->
    <ChatDialog
      :visible.sync="showChat"
      title="与对方聊天"
      :user-id="chatUserId"
      :user-name="chatUserName"
      :user-avatar="chatUserAvatar"
    />
    <div class="filter-bar hw-filter-bar">
      <div class="filter-header">
        <h3 class="filter-title">筛选条件</h3>
        <p class="filter-subtitle">根据订单状态筛选</p>
      </div>
      <el-form :inline="true" class="query-form">
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部类型" clearable style="width:120px">
            <el-option label="项目" value="project" />
            <el-option label="服务" value="service" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width:140px">
            <el-option label="待支付" value="unpaid" />
            <el-option label="待确认" value="pending" />
            <el-option label="进行中" value="serving" />
            <el-option label="已完成" value="completed" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="load">
            <i class="el-icon-search"></i>
            <span>查询</span>
          </el-button>
          <el-button class="reset-button" @click="query = { type: '', status: '' }; load()">
            <i class="el-icon-refresh"></i>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="order-table-container unique-project-table-container" id="unique-project-table-container">
      <el-table 
        v-loading="loading" 
        :data="displayList" 
        border 
        stripe 
        class="order-table unique-project-table"
        id="my-orders-table"
        row-key="id"
        :cell-style="{ backgroundColor: '#ffffff', color: '#4a5568' }"
        :header-cell-style="{ backgroundColor: '#3b82f6', color: 'white', textAlign: 'center' }"
        :row-style="{ backgroundColor: '#ffffff' }"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="订单号" width="120" align="center" sortable="custom"/>
        <el-table-column prop="serviceName" label="服务名称" min-width="200" align="left" sortable="custom">
          <template slot-scope="scope">
            <span class="service-name">{{ scope.row.serviceName || '服务名称' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100" align="center" sortable="custom">
          <template slot-scope="scope">
            <span class="amount-value">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag 
              :type="getStatusType(scope.row.status)"
              size="small"
              class="status-tag"
            >
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable="custom" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewOrder(scope.row)"
              class="action-button detail-button"
            >
              查看详情
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="contactUser(scope.row)"
              class="action-button"
            >
              联系对方
            </el-button>
            <!-- 我发起的订单操作 -->
            <el-button 
              v-if="orderType === 'buy' && scope.row.status === 'unpaid'" 
              type="success" 
              size="small" 
              @click="payOrder(scope.row)"
              class="action-button"
            >
              支付
            </el-button>
            <!-- 我收到的订单操作 -->
            <el-button 
              v-if="orderType === 'sell' && scope.row.status === 'pending'" 
              type="warning" 
              size="small" 
              @click="confirmOrder(scope.row)"
              class="action-button"
            >
              确认订单
            </el-button>
            <el-button 
              v-if="orderType === 'sell' && scope.row.status === 'serving'" 
              type="success" 
              size="small" 
              @click="completeOrder(scope.row)"
              class="action-button"
            >
              标记完成
            </el-button>
            <!-- 通用操作 -->
            <el-button 
              v-if="['unpaid', 'pending'].includes(scope.row.status)" 
              type="danger" 
              size="small" 
              @click="cancelOrder(scope.row)"
              class="action-button"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="!loading && !displayList.length" class="data-empty hw-empty">
      <div class="empty-icon"><i class="el-icon-s-order"></i></div>
      <p>暂无订单</p>
      <p class="empty-tip">{{ orderType === 'buy' ? '在服务市场预约服务后将在此展示' : '暂无收到的订单' }}</p>
      <el-button v-if="orderType === 'buy'" type="primary" size="small" @click="$router.push('/services')" style="margin-top:12px">去预约</el-button>
    </div>
    <el-pagination
      v-if="page.total > 0"
      style="margin-top:16px"
      :current-page="page.current"
      :page-size="page.size"
      :page-sizes="[5, 10, 20, 50]"
      :total="page.total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="onPage"
      @size-change="onSizeChange"
    />
  </div>
</template>

<script>
import { listMy, listProvider, setStatus } from '@/api/order'
import ChatDialog from '@/components/ChatDialog.vue'
const MOCK_LIST = [
  { id: 1001, serviceName: '企业法律咨询服务', amount: 1999, status: 'completed', createTime: '2025-01-15 10:00', providerName: '法律专家团队' },
  { id: 1002, serviceName: '网站开发服务', amount: 2999, status: 'serving', createTime: '2025-01-18 14:30', providerName: '技术开发公司' },
  { id: 1003, serviceName: '市场调研服务', amount: 999, status: 'pending', createTime: '2025-01-20 09:00', providerName: '市场研究机构' },
  { id: 1004, serviceName: '品牌设计服务', amount: 4999, status: 'unpaid', createTime: '2025-01-21 16:00', providerName: '设计工作室' },
  { id: 1005, serviceName: '财务审计服务', amount: 1999, status: 'completed', createTime: '2025-01-10 11:20', providerName: '会计事务所' },
  { id: 1006, serviceName: '人力资源咨询', amount: 3999, status: 'completed', createTime: '2025-01-08 08:45', providerName: '人力资源公司' },
  { id: 1007, serviceName: '税务筹划服务', amount: 899, status: 'closed', createTime: '2025-01-05 13:00', providerName: '税务顾问' },
  { id: 1008, serviceName: '知识产权服务', amount: 2599, status: 'serving', createTime: '2025-01-19 10:30', providerName: '知识产权代理' },
  { id: 1009, serviceName: '市场营销服务', amount: 1500, status: 'pending', createTime: '2025-01-22 09:15', providerName: '营销策划公司' },
  { id: 1010, serviceName: '技术咨询服务', amount: 3200, status: 'completed', createTime: '2025-01-12 15:40', providerName: '技术顾问' }
]
export default {
  name: 'MyOrders',
  components: {
    ChatDialog
  },
  data() {
    return {
      loading: false,
      list: [],
      orderType: 'buy',
      query: { type: '', status: '' },
      page: { current: 1, size: 10, total: 0 },
      showChat: false,
      chatUserId: '',
      chatUserName: '',
      chatUserAvatar: ''
    }
  },
  computed: {
    displayList() {
      let orders = []
      if (this.list.length) {
        // 确保订单数据结构完整
        orders = this.list.map(order => ({
          id: order.id || '',
          serviceName: order.serviceName || order.title || '订单名称',
          amount: order.amount || 0,
          status: order.status || 'unknown',
          createTime: order.createTime || order.createdAt || '',
          providerName: order.providerName || order.provider || '未知',
          // 假设根据服务名称或其他字段判断订单类型
          orderType: this.getOrderType(order)
        }))
      } else {
        // 根据订单类型返回不同的模拟数据
        if (this.orderType === 'buy') {
          // 我发起的订单
          orders = MOCK_LIST.filter(item => item.id % 2 === 1)
        } else {
          // 我收到的订单
          orders = [
            { id: 2001, serviceName: '企业品牌设计', amount: 3500, status: 'pending', createTime: '2025-01-20 09:00', providerName: '创意设计工作室', orderType: 'project' },
            { id: 2002, serviceName: '网站开发项目', amount: 8000, status: 'serving', createTime: '2025-01-18 14:30', providerName: '技术开发公司', orderType: 'project' },
            { id: 2003, serviceName: '市场调研服务', amount: 1500, status: 'completed', createTime: '2025-01-15 10:00', providerName: '市场研究机构', orderType: 'service' },
            { id: 2004, serviceName: '财务咨询服务', amount: 2000, status: 'pending', createTime: '2025-01-22 11:00', providerName: '财务顾问公司', orderType: 'service' },
            { id: 2005, serviceName: '人力资源培训', amount: 2500, status: 'serving', createTime: '2025-01-19 16:00', providerName: '人力资源公司', orderType: 'service' }
          ]
        }
      }
      
      // 根据筛选条件过滤订单
      if (this.query.type) {
        orders = orders.filter(order => order.orderType === this.query.type)
      }
      
      return orders
    },
    totalOrders() {
      return this.displayList.length
    },
    pendingOrders() {
      return this.displayList.filter(item => ['unpaid', 'pending', 'serving'].includes(item.status)).length
    },
    completedOrders() {
      return this.displayList.filter(item => item.status === 'completed').length
    },
    totalAmount() {
      return this.displayList.reduce((total, item) => total + (item.amount || 0), 0)
    }
  },
  created() {
    this.load()
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
    viewOrder(order) {
      // 跳转到订单详情页面
      this.$router.push(`/order/${order.id}`)
    },
    contactUser(order) {
      // 打开聊天对话框
      this.chatUserId = order.id // 实际应用中应该使用真实的用户ID
      this.chatUserName = order.providerName || '对方用户'
      this.chatUserAvatar = '' // 实际应用中应该使用真实的用户头像
      this.showChat = true
    },
    async payOrder(order) {
      try {
        await setStatus(order.id, 'paid')
        this.$message.success('支付成功')
        this.load()
      } catch (error) {
        this.$message.error('支付失败')
      }
    },
    async confirmOrder(order) {
      try {
        await setStatus(order.id, 'serving')
        this.$message.success('订单已确认')
        this.load()
      } catch (error) {
        this.$message.error('确认失败')
      }
    },
    async completeOrder(order) {
      try {
        await setStatus(order.id, 'completed')
        this.$message.success('订单已完成')
        this.load()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async cancelOrder(order) {
      this.$confirm('确定要取消这个订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await setStatus(order.id, 'closed')
          this.$message.success('订单已取消')
          this.load()
        } catch (error) {
          this.$message.error('取消失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    async load() {
      this.loading = true
      try {
        // 尝试从API获取数据
        let res
        if (this.orderType === 'buy') {
          res = await listMy({ 
            current: this.page.current, 
            size: this.page.size, 
            status: this.query.status 
          })
        } else {
          // 对于"我收到的订单"，使用不同的API或处理逻辑
          res = await listProvider({ 
            current: this.page.current, 
            size: this.page.size,
            status: this.query.status 
          })
        }
        this.list = res.records || []
        this.page.total = res.total || 0
      } catch (error) {
        console.error('加载订单失败:', error)
        // 即使API调用失败，也要确保页面能正常显示
        this.list = []
        this.page.total = 0
      } finally {
        this.loading = false
      }
    },
    // 当订单类型变化时，重置页码和筛选条件
    handleOrderTypeChange() {
      this.page.current = 1
      this.query = { type: '', status: '' }
      this.load()
    },
    onPage(p) {
      this.page.current = p
      this.load()
    },
    onSizeChange(size) {
      this.page.size = size
      this.page.current = 1
      this.load()
    },
    handleSortChange({ prop, order }) {
      // 处理排序逻辑
      if (!prop || !order) {
        // 重置排序
        this.load()
        return
      }
      
      // 对显示数据进行排序
      const sortedList = [...this.displayList].sort((a, b) => {
        if (prop === 'id') {
          // 订单号排序
          const aVal = a[prop] || ''
          const bVal = b[prop] || ''
          return order === 'ascending' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
        } else if (prop === 'serviceName') {
          // 服务名称排序
          const aVal = a[prop] || ''
          const bVal = b[prop] || ''
          return order === 'ascending' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
        } else if (prop === 'amount') {
          // 金额排序
          const aVal = a[prop] || 0
          const bVal = b[prop] || 0
          return order === 'ascending' ? aVal - bVal : bVal - aVal
        } else if (prop === 'createTime') {
          // 时间排序
          const aVal = new Date(a[prop] || 0)
          const bVal = new Date(b[prop] || 0)
          return order === 'ascending' ? aVal - bVal : bVal - aVal
        }
        return 0
      })
      
      // 更新显示数据
      this.list = sortedList
    },
    getOrderType(order) {
      // 根据服务名称或其他字段判断订单类型
      // 这里简单实现，实际项目中可能需要根据resourceId查询资源类型
      const projectKeywords = ['项目', '开发', '设计', '系统']
      const serviceKeywords = ['服务', '咨询', '培训', '调研']
      
      const serviceName = order.serviceName || order.title || ''
      
      for (const keyword of projectKeywords) {
        if (serviceName.includes(keyword)) {
          return 'project'
        }
      }
      
      for (const keyword of serviceKeywords) {
        if (serviceName.includes(keyword)) {
          return 'service'
        }
      }
      
      return 'service' // 默认返回服务类型
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

/* 统计卡片容器 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

/* 统计卡片 */
.stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border-color: #3b82f6;
}

/* 统计图标 */
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.order-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.pending-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.completed-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.amount-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
}

/* 统计内容 */
.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-top: 4px;
}

/* 列表头部 */
.list-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #3b82f6;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 20px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 订单类型切换 */
.order-type-tabs {
  margin-bottom: 8px;
}

.order-type-tabs .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.order-type-tabs .el-radio-button__inner {
  border-radius: 8px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.order-type-tabs .el-radio-button:first-child .el-radio-button__inner {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}

.order-type-tabs .el-radio-button:last-child .el-radio-button__inner {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
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

/* 筛选栏 */
.filter-bar {
  margin-bottom: 24px;
}

.hw-filter-bar {
  background: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.filter-header {
  margin-bottom: 16px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.filter-subtitle {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.button-group {
  display: flex;
  gap: 12px;
}

/* 表格容器 */
.order-table-container {
  margin-bottom: 32px;
}

/* 唯一表格容器 */
.unique-project-table-container {
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

/* 唯一表格容器 */
.unique-project-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.unique-project-table:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

/* 唯一表格表头 */
.unique-project-table th {
  background-color: #3b82f6 !important;
  color: white !important;
  border-bottom: 2px solid #2563eb !important;
  border-top: 1px solid #3b82f6 !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 唯一表格单元格 */
.unique-project-table td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 唯一表格行悬停效果 */
.unique-project-table tr:hover td {
  background-color: #f0f9ff !important;
  transition: background-color 0.3s ease;
}

/* 唯一表格条纹行 */
.unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

/* 唯一表格条纹行悬停效果 */
.unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
}

/* 唯一表格服务名称单元格 */
.unique-project-table .service-name {
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
}

/* 唯一表格金额样式 */
.unique-project-table .amount-value {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

/* 唯一表格状态标签 */
.unique-project-table .status-tag {
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格操作按钮 */
.unique-project-table .action-button {
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
  margin-right: 8px;
  margin-bottom: 8px;
}

.unique-project-table .action-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.unique-project-table .detail-button {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.unique-project-table .detail-button:hover {
  background: #2563eb;
  border-color: #2563eb;
}

/* 唯一表格响应式设计 */
@media (max-width: 1200px) {
  .unique-project-table {
    font-size: 13px;
  }
  
  .unique-project-table th,
  .unique-project-table td {
    padding: 10px 12px;
  }
}

@media (max-width: 768px) {
  .unique-project-table {
    font-size: 12px;
  }
  
  .unique-project-table th,
  .unique-project-table td {
    padding: 8px 10px;
  }
  
  .unique-project-table .action-button {
    padding: 2px 8px;
    font-size: 11px;
  }
}

/* 订单表格专用样式 - 最高优先级 */
#my-orders-table,
#my-orders-table .el-table,
#my-orders-table .el-table__inner-wrapper,
#my-orders-table .el-table__header-wrapper,
#my-orders-table .el-table__body-wrapper,
#my-orders-table .el-table__footer-wrapper,
#my-orders-table .el-table__header,
#my-orders-table .el-table__body,
#my-orders-table .el-table__footer,
#my-orders-table th,
#my-orders-table td,
#my-orders-table .el-table__cell,
#my-orders-table th.el-table__cell,
#my-orders-table .el-table__cell .cell,
#my-orders-table th.el-table__cell .cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 订单表格表头样式 */
#my-orders-table th,
#my-orders-table th.el-table__cell,
#my-orders-table th.el-table__cell .cell {
  background-color: #3b82f6 !important;
  color: white !important;
  z-index: 9999 !important;
  text-align: center !important;
}

/* 订单表格行悬停效果 */
#my-orders-table tr:hover td,
#my-orders-table tr:hover .el-table__cell {
  background-color: #f0f9ff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 订单表格条纹行 */
#my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped td,
#my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped .el-table__cell {
  background-color: #f8fafc !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 订单表格条纹行悬停效果 */
#my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td,
#my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped:hover .el-table__cell {
  background-color: #f0f9ff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 订单列表页面表格专用背景颜色样式 */
.my-list-page #my-orders-table {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__inner-wrapper {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body-wrapper {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body tr {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body tr td {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body tr .el-table__cell {
  background-color: #ffffff !important;
}

.my-list-page #my-orders-table .el-table__body tr .el-table__cell .cell {
  background-color: transparent !important;
}

/* 订单表格条纹行样式 */
.my-list-page #my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped {
  background-color: #f8fafc !important;
}

.my-list-page #my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

.my-list-page #my-orders-table.el-table--striped .el-table__body tr.el-table__row--striped .el-table__cell {
  background-color: #f8fafc !important;
}

/* 订单表格悬停效果 */
.my-list-page #my-orders-table tr:hover {
  background-color: #f0f9ff !important;
}

.my-list-page #my-orders-table tr:hover td {
  background-color: #f0f9ff !important;
}

.my-list-page #my-orders-table tr:hover .el-table__cell {
  background-color: #f0f9ff !important;
}

/* 订单表格表头样式 */
.my-list-page #my-orders-table th {
  background-color: #f7fafc !important;
}

.my-list-page #my-orders-table th.el-table__cell {
  background-color: #f7fafc !important;
}

.my-list-page #my-orders-table th.el-table__cell .cell {
  background-color: transparent !important;
}

/* 金额样式 */
.amount-value {
  font-weight: 600;
  color: #3b82f6;
  font-size: 14px;
}

/* 状态标签 */
.status-tag {
  border-radius: 6px;
  font-size: 12px;
  padding: 2px 8px;
}

/* 操作按钮 */
.action-button {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  font-weight: 500;
  margin-right: 8px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-button {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.detail-button:hover {
  background: #2563eb;
  border-color: #2563eb;
}

/* 服务名称 */
.service-name {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

/* 空数据状态 */
.hw-empty {
  text-align: center;
  padding: 60px 20px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  margin: 20px 0;
}

.hw-empty .empty-icon {
  font-size: 64px;
  color: #94a3b8;
  margin-bottom: 20px;
}

.hw-empty p {
  font-size: 16px;
  color: #334155;
  margin: 8px 0;
  font-weight: 500;
}

.hw-empty .empty-tip {
  font-size: 14px;
  color: #64748b;
  margin-top: 8px;
  margin-bottom: 20px;
}

/* 分页组件样式 */
.el-pagination {
  margin-top: 24px;
  text-align: right;
}

.el-pagination__total {
  color: #64748b;
}

.el-pagination button,
.el-pagination span:not(.el-pagination__total) {
  color: #64748b;
}

.el-pagination button:hover {
  color: #3b82f6;
}

.el-pagination__active {
  background: #3b82f6;
  border-color: #3b82f6;
}

/* 表单样式 */
.el-form-item__label {
  color: #334155;
  font-weight: 500;
  font-size: 14px;
}

.el-input__inner {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.el-input__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 修复下拉框文字显示偏下问题 */
.el-select .el-input__inner {
  line-height: 32px;
  height: 32px;
  display: flex;
  align-items: center;
}

.el-select-dropdown__item {
  display: flex;
  align-items: center;
  height: 32px;
  line-height: 32px;
}

.el-select-dropdown__item span {
  vertical-align: middle;
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

.el-button {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.el-button:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-1px);
}

.reset-button {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.reset-button:hover {
  background: #f1f5f9;
  border-color: #3b82f6;
  color: #3b82f6;
}

/* 标签样式 */
.el-tag {
  border-radius: 6px;
  font-size: 12px;
  padding: 2px 8px;
  font-weight: 500;
}

.el-tag--success {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.el-tag--danger {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.el-tag--info {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.el-tag--warning {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.el-tag--primary {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

/* 加载动画 */
.el-loading-mask {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(2px);
}

.el-loading-spinner .path {
  stroke: #3b82f6;
}

.el-loading-spinner .el-loading-text {
  color: #334155;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-card {
    padding: 16px;
    margin: 12px 0;
  }
  
  .list-header {
    margin-bottom: 16px;
    padding-bottom: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  /* 统计卡片响应式 */
  .stats-cards {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .hw-filter-bar {
    padding: 16px;
  }
  
  .query-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .button-group {
    justify-content: center;
  }
  
  .order-table th,
  .order-table td {
    padding: 10px 8px;
    font-size: 12px;
  }
  
  .hw-empty {
    padding: 40px 16px;
  }
  
  .hw-empty .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .el-pagination {
    margin-top: 20px;
  }
}

@media (max-width: 480px) {
  .page-card {
    padding: 12px;
  }
  
  .list-header {
    margin-bottom: 12px;
    padding-bottom: 12px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .hw-filter-bar {
    padding: 12px;
  }
  
  .filter-title {
    font-size: 14px;
  }
  
  .order-table th,
  .order-table td {
    padding: 8px 6px;
    font-size: 11px;
  }
  
  .hw-empty {
    padding: 32px 12px;
  }
}
</style>
