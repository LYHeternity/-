<template>
  <div class="admin-dashboard">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <p class="loading-text">数据加载中...</p>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-overlay">
      <div class="error-content">
        <i class="el-icon-warning-outline error-icon"></i>
        <h3 class="error-title">加载失败</h3>
        <p class="error-message">{{ error }}</p>
        <el-button type="primary" @click="fetchData">重试</el-button>
      </div>
    </div>

    <!-- 顶部卡片 -->
    <div class="top-cards">
      <div class="welcome-card">
        <div class="welcome-content">
          <h1 class="welcome-title">欢迎回来，{{ $store.state.user.realName || $store.state.user.username }}</h1>
          <p class="welcome-subtitle">今天是 {{ new Date().toLocaleDateString('zh-CN') }}，以下是平台的核心数据概览</p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" plain size="small" @click="fetchData" :loading="refreshing">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
        </div>
        <div class="welcome-avatar">
          <img v-if="$store.state.user.avatar" :src="$store.state.user.avatar" class="welcome-avatar-image" alt="用户头像" />
          <span v-else>{{ ($store.state.user.realName || $store.state.user.username || 'A').charAt(0) }}</span>
        </div>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <div class="stat-card user" @click="viewDetail('user')">
        <div class="stat-info">
          <h3 class="stat-title">用户总数</h3>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i>
            <span>12%</span>
            <span class="change-label">较上月</span>
          </div>
        </div>
        <div class="stat-value">{{ stats.userCount || 0 }}</div>
      </div>
      
      <div class="stat-card project" @click="viewDetail('project')">
        <div class="stat-info">
          <h3 class="stat-title">项目总数</h3>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i>
            <span>8%</span>
            <span class="change-label">较上月</span>
          </div>
        </div>
        <div class="stat-value">{{ stats.projectCount || 0 }}</div>
      </div>
      
      <div class="stat-card service" @click="viewDetail('resource')">
        <div class="stat-info">
          <h3 class="stat-title">服务总数</h3>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i>
            <span>15%</span>
            <span class="change-label">较上月</span>
          </div>
        </div>
        <div class="stat-value">{{ stats.resourceCount || 0 }}</div>
      </div>
      
      <div class="stat-card order" @click="viewDetail('order')">
        <div class="stat-info">
          <h3 class="stat-title">订单总数</h3>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i>
            <span>23%</span>
            <span class="change-label">较上月</span>
          </div>
        </div>
        <div class="stat-value">{{ stats.orderCount || 0 }}</div>
      </div>
    </div>

    <!-- 系统状态监控 -->
    <div class="system-status-section">
      <div class="section-header">
        <h2 class="section-title">系统状态监控</h2>
        <el-button size="small" type="primary" plain @click="refreshSystemStatus">
          <i class="el-icon-refresh"></i> 刷新状态
        </el-button>
      </div>
      <div class="system-status-grid">
        <div class="status-card">
          <div class="status-header">
            <h3 class="status-title">服务器状态</h3>
            <span class="status-indicator online"></span>
          </div>
          <div class="status-body">
            <div class="status-item">
              <span class="status-label">CPU负载</span>
              <span class="status-value">28%</span>
            </div>
            <div class="status-item">
              <span class="status-label">内存使用</span>
              <span class="status-value">45%</span>
            </div>
            <div class="status-item">
              <span class="status-label">磁盘空间</span>
              <span class="status-value">62%</span>
            </div>
          </div>
        </div>
        
        <div class="status-card">
          <div class="status-header">
            <h3 class="status-title">数据库状态</h3>
            <span class="status-indicator online"></span>
          </div>
          <div class="status-body">
            <div class="status-item">
              <span class="status-label">连接数</span>
              <span class="status-value">12</span>
            </div>
            <div class="status-item">
              <span class="status-label">查询响应</span>
              <span class="status-value">120ms</span>
            </div>
            <div class="status-item">
              <span class="status-label">备份状态</span>
              <span class="status-value">今日已备份</span>
            </div>
          </div>
        </div>
        
        <div class="status-card">
          <div class="status-header">
            <h3 class="status-title">API状态</h3>
            <span class="status-indicator online"></span>
          </div>
          <div class="status-body">
            <div class="status-item">
              <span class="status-label">响应时间</span>
              <span class="status-value">85ms</span>
            </div>
            <div class="status-item">
              <span class="status-label">成功率</span>
              <span class="status-value">99.8%</span>
            </div>
            <div class="status-item">
              <span class="status-label">今日请求</span>
              <span class="status-value">1,248</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 顶部：折线图区域 -->
      <div class="chart-section">
        <div class="section-header">
          <h2 class="section-title">平台活跃度趋势</h2>
          <div class="section-actions">
            <el-button size="small" type="primary" :plain="timeRange !== 'week'" @click="setTimeRange('week')">周</el-button>
            <el-button size="small" type="primary" :plain="timeRange !== 'month'" @click="setTimeRange('month')">月</el-button>
            <el-button size="small" type="primary" :plain="timeRange !== 'year'" @click="setTimeRange('year')">年</el-button>
          </div>
        </div>
        <div class="chart-container" ref="mainChart"></div>
      </div>

      <!-- 中部：补充图表区域 -->
      <div class="supplementary-charts">
        <!-- 左侧：用户分布饼图 -->
        <div class="chart-card">
          <h3 class="chart-card-title">用户分布</h3>
          <div class="chart-container" ref="userDistributionChart"></div>
        </div>
        
        <!-- 右侧：服务类别柱状图 -->
        <div class="chart-card">
          <h3 class="chart-card-title">服务类别分布</h3>
          <div class="chart-container" ref="serviceCategoryChart"></div>
        </div>
      </div>

      <!-- 底部：快捷功能和待办事项 -->
      <div class="bottom-content">
        <!-- 左侧：快捷功能和新增功能组件 -->
        <div class="panel-section quick-actions-section">
          <div class="section-header">
            <h3 class="panel-title">快捷功能</h3>
            <span class="section-subtitle">快速访问常用管理功能</span>
          </div>
          <div class="quick-actions-container">
            <!-- 快捷功能按钮组 -->
            <div class="quick-actions-grid">
              <div class="quick-action-item compact" @click="$router.push('/admin-users')">
                <div class="action-icon user">
                  <i class="el-icon-user"></i>
                </div>
                <span class="action-text">用户管理</span>
                <span class="action-badge">4</span>
              </div>
              <div class="quick-action-item compact" @click="$router.push('/admin-roles')">
                <div class="action-icon role">
                  <i class="el-icon-s-custom"></i>
                </div>
                <span class="action-text">角色权限</span>
              </div>
              <div class="quick-action-item compact" @click="$router.push('/admin-audit')">
                <div class="action-icon audit">
                  <i class="el-icon-document-checked"></i>
                </div>
                <span class="action-text">内容审核</span>
                <span class="action-badge">2</span>
              </div>
              <div class="quick-action-item compact" @click="$router.push('/admin-stats')">
                <div class="action-icon stats">
                  <i class="el-icon-data-analysis"></i>
                </div>
                <span class="action-text">数据统计</span>
              </div>
              <div class="quick-action-item compact" @click="$router.push('/admin-config')">
                <div class="action-icon config">
                  <i class="el-icon-setting"></i>
                </div>
                <span class="action-text">系统配置</span>
              </div>
              <div class="quick-action-item compact" @click="$router.push('/admin-log')">
                <div class="action-icon log">
                  <i class="el-icon-document"></i>
                </div>
                <span class="action-text">操作日志</span>
              </div>
            </div>
            
            <!-- 系统快捷操作 -->
            <div class="system-quick-actions">
              <div class="action-section-header">
                <h4 class="action-section-title">系统快捷操作</h4>
                <span class="action-section-subtitle">常用系统功能快速入口</span>
              </div>
              <div class="system-actions-grid">
                <div class="system-action-item" @click="createNewProject">
                  <div class="system-action-icon create">
                    <i class="el-icon-plus"></i>
                  </div>
                  <span class="system-action-text">创建新项目</span>
                </div>
                <div class="system-action-item" @click="exportDataReport">
                  <div class="system-action-icon export">
                    <i class="el-icon-download"></i>
                  </div>
                  <span class="system-action-text">导出数据报表</span>
                </div>
                <div class="system-action-item" @click="clearSystemCache">
                  <div class="system-action-icon cache">
                    <i class="el-icon-refresh"></i>
                  </div>
                  <span class="system-action-text">清理系统缓存</span>
                </div>
                <div class="system-action-item" @click="checkSystemStatus">
                  <div class="system-action-icon status">
                    <i class="el-icon-info"></i>
                  </div>
                  <span class="system-action-text">系统状态检查</span>
                </div>
              </div>
            </div>
            
            <!-- 快捷访问统计 -->
            <div class="quick-access-stats">
              <div class="action-section-header">
                <h4 class="action-section-title">快捷访问统计</h4>
                <span class="action-section-subtitle">最近7天功能使用情况</span>
              </div>
              <div class="stats-grid">
                <div class="stat-card">
                  <div class="stat-icon user">
                    <i class="el-icon-user"></i>
                  </div>
                  <div class="stat-content">
                    <div class="stat-value">128</div>
                    <div class="stat-label">用户管理</div>
                  </div>
                  <div class="stat-trend up">+12%</div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon audit">
                    <i class="el-icon-document-checked"></i>
                  </div>
                  <div class="stat-content">
                    <div class="stat-value">86</div>
                    <div class="stat-label">内容审核</div>
                  </div>
                  <div class="stat-trend up">+8%</div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon config">
                    <i class="el-icon-setting"></i>
                  </div>
                  <div class="stat-content">
                    <div class="stat-value">42</div>
                    <div class="stat-label">系统配置</div>
                  </div>
                  <div class="stat-trend down">-3%</div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon log">
                    <i class="el-icon-document"></i>
                  </div>
                  <div class="stat-content">
                    <div class="stat-value">215</div>
                    <div class="stat-label">操作日志</div>
                  </div>
                  <div class="stat-trend up">+15%</div>
                </div>
              </div>
            </div>
            
            <!-- 最近操作记录 -->
            <div class="recent-activities">
              <div class="action-section-header">
                <h4 class="action-section-title">最近操作记录</h4>
                <span class="action-section-subtitle">您的最近系统操作</span>
              </div>
              <div class="activity-list">
                <div class="activity-item">
                  <div class="activity-icon success">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">审核通过项目</div>
                    <div class="activity-time">10分钟前</div>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon info">
                    <i class="el-icon-info"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">导出数据报表</div>
                    <div class="activity-time">30分钟前</div>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon warning">
                    <i class="el-icon-warning"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">系统配置更新</div>
                    <div class="activity-time">1小时前</div>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon success">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">创建新项目</div>
                    <div class="activity-time">2小时前</div>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon info">
                    <i class="el-icon-info"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">查看用户统计</div>
                    <div class="activity-time">3小时前</div>
                  </div>
                </div>
                <div class="activity-item">
                  <div class="activity-icon success">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">审核服务申请</div>
                    <div class="activity-time">4小时前</div>
                  </div>
                </div>
              </div>
            </div>
            
          </div>
        </div>

        <!-- 右侧：待办事项和系统通知 -->
        <div class="right-panel">
          <!-- 待办事项 -->
          <div class="panel-section">
            <div class="panel-header">
              <h3 class="panel-title">待办事项</h3>
              <el-button type="text" size="small" @click="addTodoItem">
                <i class="el-icon-plus"></i> 添加
              </el-button>
            </div>
            <div class="todo-list">
              <div 
                v-for="(item, index) in todoList" 
                :key="index" 
                class="todo-item"
                :class="{ 'completed': item.done }"
              >
                <div class="todo-checkbox" @click="toggleTodo(index)">
                  <i v-if="item.done" class="el-icon-check"></i>
                </div>
                <div class="todo-content">
                  <h4 class="todo-title">{{ item.title }}</h4>
                  <p class="todo-due">{{ item.due }}</p>
                </div>
                <div class="todo-actions">
                  <el-button type="text" size="mini" @click="removeTodo(index)">
                    <i class="el-icon-delete"></i>
                  </el-button>
                </div>
                <div class="todo-priority" :class="'priority-' + item.priority"></div>
              </div>
            </div>
            <div v-if="todoList.length === 0" class="empty-todo">
              <i class="el-icon-info"></i>
              <p>暂无待办事项</p>
            </div>
          </div>

          <!-- 系统通知 -->
          <div class="panel-section">
            <div class="panel-header">
              <h3 class="panel-title">系统通知</h3>
              <el-button type="text" size="small" @click="markAllAsRead">
                <i class="el-icon-check"></i> 全部已读
              </el-button>
            </div>
            <div class="notification-list">
              <div 
                v-for="(notification, index) in notifications" 
                :key="index" 
                class="notification-item"
                :class="{ 'unread': !notification.read }"
              >
                <div class="notification-icon" :class="notification.type">
                  <i :class="notification.icon"></i>
                </div>
                <div class="notification-content">
                  <h4 class="notification-title">{{ notification.title }}</h4>
                  <p class="notification-time">{{ notification.time }}</p>
                </div>
                <div class="notification-actions">
                  <el-button type="text" size="mini" @click="markAsRead(index)">
                    <i class="el-icon-check"></i>
                  </el-button>
                </div>
              </div>
            </div>
            <div v-if="notifications.length === 0" class="empty-notification">
              <i class="el-icon-bell"></i>
              <p>暂无系统通知</p>
            </div>
          </div>
        </div>
      </div>
    </div>



    <!-- 添加待办事项弹窗 -->
    <el-dialog title="添加待办事项" :visible.sync="showAddTodoDialog" width="500px">
      <el-form :model="newTodo" label-width="80px">
        <el-form-item label="任务名称">
          <el-input v-model="newTodo.title" placeholder="请输入任务名称"></el-input>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-input v-model="newTodo.due" placeholder="例如：今日、明日、本周"></el-input>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="newTodo.priority" placeholder="请选择优先级">
            <el-option label="高" value="1"></el-option>
            <el-option label="中" value="2"></el-option>
            <el-option label="低" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showAddTodoDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTodo">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { adminStats } from '@/api/admin'
export default {
  name: 'AdminDashboard',
  data() {
    return {
      loading: false,
      refreshing: false,
      error: '',
      stats: {},
      timeRange: 'month',
      currentPage: 1,
      totalPages: 5,
      userTrendData: [],
      projectTrendData: [],
      serviceTrendData: [],
      todoList: [
        { title: '审核待通过项目 3 条', due: '今日', priority: 1, done: false },
        { title: '新用户注册待查看 5 人', due: '今日', priority: 2, done: false },
        { title: '服务订单待确认 2 单', due: '明日', priority: 2, done: false },
        { title: '系统配置备份', due: '本周', priority: 3, done: false },
        { title: '数据统计报表导出', due: '本周', priority: 3, done: true },
        { title: '操作日志归档', due: '本月', priority: 3, done: false }
      ],

      showAddTodoDialog: false,
      newTodo: {
        title: '',
        due: '',
        priority: 2,
        done: false
      },
      notifications: [
        { id: 1, title: '系统备份已完成', time: '10分钟前', type: 'success', icon: 'el-icon-success', read: false },
        { id: 2, title: '有3个新用户注册', time: '2小时前', type: 'info', icon: 'el-icon-user', read: false },
        { id: 3, title: '服务器CPU负载过高', time: '4小时前', type: 'warning', icon: 'el-icon-warning', read: true },
        { id: 4, title: '数据库备份失败', time: '昨天', type: 'error', icon: 'el-icon-error', read: true }
      ],
      charts: []
    }
  },
  created() {
    this.fetchData()
  },
  mounted() {
    this.$nextTick(() => {
      this.initMiniCharts()
      this.initMainChart()
      this.initUserDistributionChart()
      this.initServiceCategoryChart()
    })
  },
  beforeDestroy() {
    this.charts.forEach((c) => c && c.dispose())
  },
  methods: {
    fetchData() {
      this.loading = true
      this.error = ''
      Promise.all([
        adminStats.overview(),
        adminStats.userTrend(),
        adminStats.projectTrend()
      ]).then(([overviewRes, userTrendRes, projectTrendRes]) => {
        this.stats = overviewRes || {}
        this.userTrendData = userTrendRes || { dates: [], counts: [] }
        this.projectTrendData = projectTrendRes || { dates: [], counts: [] }
        this.serviceTrendData = { dates: userTrendRes?.dates || [], counts: userTrendRes?.counts?.map(c => Math.floor(c * 0.7)) || [] }
        this.loading = false
        this.$nextTick(() => {
          this.initMainChart()
          this.initUserDistributionChart()
          this.initServiceCategoryChart()
        })
      }).catch((err) => {
        console.error('Data fetch error:', err)
        this.error = '数据加载失败，请稍后重试'
        this.stats = { userCount: 12, projectCount: 8, resourceCount: 6, orderCount: 28 }
        this.userTrendData = { dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], counts: [2, 5, 4, 8, 6, 9, 12] }
        this.projectTrendData = { dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], counts: [1, 3, 4, 6, 5, 8, 10] }
        this.serviceTrendData = { dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], counts: [1, 2, 3, 4, 3, 5, 7] }
        this.loading = false
        this.$nextTick(() => {
          this.initMainChart()
          this.initUserDistributionChart()
          this.initServiceCategoryChart()
        })
      })
    },
    setTimeRange(range) {
      this.timeRange = range
      this.initMainChart()
    },
    initMiniCharts() {
      const opts = {
        grid: { left: 0, right: 0, top: 4, bottom: 0 },
        xAxis: { show: false, type: 'category', data: ['1', '2', '3', '4', '5', '6', '7'] },
        yAxis: { show: false, type: 'value' },
        series: [{ type: 'line', data: [2, 5, 4, 8, 6, 9, 12], smooth: true, symbol: 'none', lineStyle: { width: 2, color: '#3b82f6' }, areaStyle: { color: 'rgba(59, 130, 246, 0.15)' } }]
      }
      ;['chartUser', 'chartProject', 'chartResource', 'chartOrder'].forEach((ref, i) => {
        const el = this.$refs[ref]
        if (el) {
          const chart = echarts.init(el)
          chart.setOption({ ...opts, series: [{ ...opts.series[0], data: [1, 3, 4, 6, 5, 8, 10].map((v) => v + i * 2) }] })
          this.charts.push(chart)
        }
      })
    },
    initMainChart() {
      const el = this.$refs.mainChart
      if (el) {
        const chart = echarts.init(el)
        
        // 确保有足够的数据点
        let xAxisData = this.userTrendData.dates && this.userTrendData.dates.length > 1 
          ? this.userTrendData.dates 
          : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        let userData = this.userTrendData.counts && this.userTrendData.counts.length > 1 
          ? this.userTrendData.counts 
          : [12, 19, 3, 5, 2, 3, 7]
        let projectData = this.projectTrendData.counts && this.projectTrendData.counts.length > 1 
          ? this.projectTrendData.counts 
          : [22, 18, 19, 14, 9, 13, 17]
        let serviceData = this.serviceTrendData.counts && this.serviceTrendData.counts.length > 1 
          ? this.serviceTrendData.counts 
          : [15, 23, 20, 15, 19, 33, 41]
        
        // 根据时间范围筛选数据
        if (this.timeRange === 'week' && xAxisData.length > 7) {
          xAxisData = xAxisData.slice(-7)
          userData = userData.slice(-7)
          projectData = projectData.slice(-7)
          serviceData = serviceData.slice(-7)
        } else if (this.timeRange === 'month' && xAxisData.length > 30) {
          xAxisData = xAxisData.slice(-30)
          userData = userData.slice(-30)
          projectData = projectData.slice(-30)
          serviceData = serviceData.slice(-30)
        } else if (this.timeRange === 'year' && xAxisData.length > 365) {
          xAxisData = xAxisData.slice(-365)
          userData = userData.slice(-365)
          projectData = projectData.slice(-365)
          serviceData = serviceData.slice(-365)
        }
        
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              label: {
                backgroundColor: '#6a7985'
              }
            }
          },
          legend: {
            data: ['用户注册', '项目发布', '服务订单'],
            top: 0,
            textStyle: {
              color: '#94a3b8'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: xAxisData,
            axisLine: {
              lineStyle: {
                color: '#334155'
              }
            },
            axisLabel: {
              color: '#94a3b8',
              rotate: xAxisData.length > 10 ? 45 : 0
            },
            axisTick: {
              show: true,
              lineStyle: {
                color: '#334155'
              }
            }
          },
          yAxis: {
            type: 'value',
            axisLine: {
              lineStyle: {
                color: '#334155'
              }
            },
            axisLabel: {
              color: '#94a3b8'
            },
            axisTick: {
              show: true,
              lineStyle: {
                color: '#334155'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#1e293b'
              }
            }
          },
          series: [
            {
              name: '用户注册',
              type: 'line',
              stack: 'Total',
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
                  { offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              lineStyle: {
                width: 3,
                color: '#3b82f6'
              },
              symbol: 'circle',
              symbolSize: 8,
              itemStyle: {
                color: '#3b82f6',
                borderWidth: 2,
                borderColor: '#fff'
              },
              data: userData
            },
            {
              name: '项目发布',
              type: 'line',
              stack: 'Total',
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(16, 185, 129, 0.5)' },
                  { offset: 1, color: 'rgba(16, 185, 129, 0.1)' }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              lineStyle: {
                width: 3,
                color: '#10b981'
              },
              symbol: 'circle',
              symbolSize: 8,
              itemStyle: {
                color: '#10b981',
                borderWidth: 2,
                borderColor: '#fff'
              },
              data: projectData
            },
            {
              name: '服务订单',
              type: 'line',
              stack: 'Total',
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(245, 158, 11, 0.5)' },
                  { offset: 1, color: 'rgba(245, 158, 11, 0.1)' }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              lineStyle: {
                width: 3,
                color: '#f59e0b'
              },
              symbol: 'circle',
              symbolSize: 8,
              itemStyle: {
                color: '#f59e0b',
                borderWidth: 2,
                borderColor: '#fff'
              },
              data: serviceData
            }
          ]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    },
    toggleTodo(index) {
      this.todoList[index].done = !this.todoList[index].done
      this.saveTodoList()
    },
    removeTodo(index) {
      this.$confirm('确定要删除这个待办事项吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.todoList.splice(index, 1)
        this.saveTodoList()
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    addTodoItem() {
      this.newTodo = {
        title: '',
        due: '',
        priority: 2,
        done: false
      }
      this.showAddTodoDialog = true
    },
    saveTodo() {
      if (!this.newTodo.title) {
        this.$message.error('请输入任务名称')
        return
      }
      this.todoList.push(this.newTodo)
      this.saveTodoList()
      this.showAddTodoDialog = false
      this.$message.success('添加成功')
    },
    saveTodoList() {
      localStorage.setItem('adminTodoList', JSON.stringify(this.todoList))
    },
    loadTodoList() {
      const saved = localStorage.getItem('adminTodoList')
      if (saved) {
        try {
          this.todoList = JSON.parse(saved)
        } catch (e) {
          console.error('Failed to load todo list:', e)
        }
      }
    },
    changePage(page) {
      if (page < 1 || page > this.totalPages) return
      this.currentPage = page
    },
    refreshSystemStatus() {
      this.$message.success('系统状态已刷新')
    },
    markAllAsRead() {
      this.notifications.forEach(notification => {
        notification.read = true
      })
      this.$message.success('所有通知已标记为已读')
    },
    markAsRead(index) {
      this.notifications[index].read = true
      this.$message.success('通知已标记为已读')
    },
    initUserDistributionChart() {
      const el = this.$refs.userDistributionChart
      if (el) {
        const chart = echarts.init(el)
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 10,
            top: 'center',
            textStyle: {
              color: '#94a3b8'
            }
          },
          series: [
            {
              name: '用户类型',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#334155',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '16',
                  fontWeight: 'bold',
                  color: '#f8fafc'
                }
              },
              labelLine: {
                show: false
              },
              data: [
                { value: 40, name: '创业者' },
                { value: 30, name: '投资者' },
                { value: 20, name: '服务提供商' },
                { value: 10, name: '管理员' }
              ]
            }
          ]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    },
    initServiceCategoryChart() {
      const el = this.$refs.serviceCategoryChart
      if (el) {
        const chart = echarts.init(el)
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              type: 'category',
              data: ['技术服务', '咨询服务', '金融服务', '法律服务', '营销服务'],
              axisTick: {
                alignWithLabel: true
              },
              axisLine: {
                lineStyle: {
                  color: '#334155'
                }
              },
              axisLabel: {
                color: '#94a3b8',
                rotate: 0,
                align: 'center'
              }
            }
          ],
          yAxis: [
            {
              type: 'value',
              axisLine: {
                lineStyle: {
                  color: '#334155'
                }
              },
              axisLabel: {
                color: '#94a3b8'
              },
              splitLine: {
                lineStyle: {
                  color: '#1e293b'
                }
              }
            }
          ],
          series: [
            {
              name: '服务数量',
              type: 'bar',
              barWidth: '60%',
              data: [18, 12, 9, 6, 15],
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#3b82f6' },
                  { offset: 1, color: '#2563eb' }
                ])
              }
            }
          ]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    },
    // 系统快捷操作方法
    createNewProject() {
      this.$message.success('跳转到创建新项目页面')
      this.$router.push('/project-publish')
    },
    exportDataReport() {
      this.$message.success('开始导出数据报表，请稍候...')
      // 模拟导出过程
      setTimeout(() => {
        this.$message.success('数据报表导出成功')
      }, 1500)
    },
    clearSystemCache() {
      this.$confirm('确定要清理系统缓存吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('系统缓存清理中，请稍候...')
        // 模拟清理过程
        setTimeout(() => {
          this.$message.success('系统缓存清理成功')
        }, 1000)
      }).catch(() => {})
    },
    checkSystemStatus() {
      this.$message.success('系统状态检查中，请稍候...')
      // 模拟检查过程
      setTimeout(() => {
        this.$message.success('系统状态正常')
      }, 800)
    },
    // 查看详情方法
    viewDetail(type) {
      switch(type) {
        case 'user':
          this.$router.push('/admin-users')
          break
        case 'project':
          this.$router.push('/admin-audit')
          break
        case 'resource':
          this.$router.push('/admin-audit')
          break
        case 'order':
          this.$message.info('订单详情功能开发中')
          break
        default:
          break
      }
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.admin-dashboard {
  width: 100%;
  margin: 0;
  padding: 12px 16px;
  background: #0f172a;
  min-height: 100vh;
  color: #f8fafc;
  box-sizing: border-box;
}

/* 顶部欢迎卡片 */
.top-cards {
  margin-bottom: 24px;
}

.welcome-card {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 6px;
}

.welcome-subtitle {
  font-size: 14px;
  color: #94a3b8;
}

.welcome-avatar {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  overflow: hidden;
}

.welcome-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.welcome-actions {
  display: flex;
  align-items: center;
  margin-right: 16px;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  transition: all 0.3s ease;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(59, 130, 246, 0.2);
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 16px;
  color: #f8fafc;
  font-weight: 500;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误提示 */
.error-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  transition: all 0.3s ease;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(239, 68, 68, 0.2);
  text-align: center;
  max-width: 400px;
  width: 90%;
}

.error-icon {
  font-size: 48px;
  color: #ef4444;
}

.error-title {
  font-size: 20px;
  font-weight: 600;
  color: #f8fafc;
  margin: 0;
}

.error-message {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.5;
}

/* 快捷功能扩展 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-icon.config {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
}

.action-icon.log {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
}

/* 最近操作记录 */
.recent-activities {
  margin-top: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.activity-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: rgba(255, 255, 255, 0.06);
  transform: translateX(4px);
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.activity-icon.success {
  background: rgba(16, 185, 129, 0.15);
  color: #10b981;
}

.activity-icon.info {
  background: rgba(59, 130, 246, 0.15);
  color: #3b82f6;
}

.activity-icon.warning {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #f8fafc;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 系统公告 */
.system-announcements {
  margin-top: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.announcement-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  transition: all 0.3s ease;
  border-left: 3px solid #3b82f6;
}

.announcement-item:hover {
  background: rgba(255, 255, 255, 0.06);
  transform: translateX(4px);
}

.announcement-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: rgba(59, 130, 246, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #3b82f6;
  flex-shrink: 0;
}

.announcement-content {
  flex: 1;
  min-width: 0;
}

.announcement-title {
  font-size: 14px;
  font-weight: 500;
  color: #f8fafc;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 待办事项管理 */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 通知列表 */
.notification-list {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 待办事项列表 */
.todo-list {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.todo-item:hover .todo-actions {
  opacity: 1;
}

.empty-todo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-todo i {
  font-size: 32px;
  opacity: 0.5;
}

/* 分页功能 */
.pagination-controls button {
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-controls button:hover:not(.disabled) {
  background: #475569;
  color: #f8fafc;
  border-color: rgba(59, 130, 246, 0.3);
}

.pagination-controls button.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.pagination-controls button.disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 弹窗样式 */
.el-dialog {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
}

.el-dialog__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 24px;
}

.el-dialog__title {
  color: #f8fafc;
  font-size: 18px;
  font-weight: 600;
}

.el-dialog__body {
  padding: 24px;
  color: #f8fafc;
}

.el-dialog__footer {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px 24px;
  background: rgba(15, 23, 42, 0.5);
}

.el-form-item__label {
  color: #94a3b8;
}

.el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  border-radius: 8px;
}

.el-input__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.el-select-dropdown {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

.el-select-dropdown__item {
  color: #f8fafc;
  padding: 12px 16px;
}

.el-select-dropdown__item:hover {
  background: rgba(59, 130, 246, 0.1);
}

.el-select-dropdown__item.selected {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
}

.el-button--primary:focus {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.4);
}

.el-button--primary.is-plain {
  background: transparent;
  border: 1px solid #3b82f6;
  color: #3b82f6;
}

.el-button--primary.is-plain:hover {
  background: rgba(59, 130, 246, 0.1);
  border-color: #3b82f6;
  color: #3b82f6;
}

.el-button--text {
  color: #94a3b8;
}

.el-button--text:hover {
  color: #3b82f6;
}

/* 消息提示 */
.el-message {
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.el-message--success {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.el-message--error {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.el-message--warning {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.el-message--info {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

/* 确认对话框 */
.el-dialog__wrapper {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
}

.el-confirm {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
}

.el-confirm__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.el-confirm__title {
  color: #f8fafc;
}

.el-confirm__content {
  color: #94a3b8;
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .welcome-actions {
    margin-right: 12px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .todo-actions {
    opacity: 1;
  }
  
  .el-dialog {
    width: 90% !important;
    margin: 20px auto;
  }
  
  .el-dialog__header,
  .el-dialog__body,
  .el-dialog__footer {
    padding: 16px;
  }
}

/* 数据概览卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600px, 1fr));
  gap: 12px;
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 80px;
  position: relative;
  overflow: hidden;
}



.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  border-radius: 4px 4px 0 0;
}

.stat-card.user {
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
  color: white;
}

.stat-card.user::before {
  background: #2563eb;
}

.stat-card.project {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  color: white;
}

.stat-card.project::before {
  background: #059669;
}

.stat-card.service {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  color: white;
}

.stat-card.service::before {
  background: #d97706;
}

.stat-card.order {
  background: linear-gradient(135deg, #ef4444 0%, #f87171 100%);
  color: white;
}

.stat-card.order::before {
  background: #dc2626;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-info {
  flex: 1;
  min-width: 0;
  padding-right: 12px;
}

.stat-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  color: rgba(255, 255, 255, 1);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.stat-change {
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  line-height: 1.3;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  inline-size: fit-content;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.stat-change.positive {
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
}

.stat-change .change-label {
  font-size: 13px;
  opacity: 1;
  color: #ffffff;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 20px;
  min-width: 40px;
  text-align: right;
  color: rgba(255, 255, 255, 1);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.stat-value::after {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  margin-left: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}




/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .stat-title {
    font-size: 14px;
  }
  
  .stat-change {
    font-size: 13px;
    padding: 6px 14px;
  }
  
  .stat-info {
    padding-right: 10px;
  }
  
  .stat-value {
    margin-left: 18px;
  }
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 26px;
  }
  
  .stat-title {
    font-size: 14px;
  }
  
  .stat-change {
    font-size: 13px;
    padding: 5px 12px;
  }
  
  .stat-info {
    padding-right: 10px;
  }
  
  .stat-value {
    margin-left: 16px;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .stat-card {
    padding: 18px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .stat-title {
    font-size: 15px;
  }
  
  .stat-change {
    font-size: 13px;
    padding: 6px 14px;
  }
  
  .stat-info {
    padding-right: 12px;
  }
  
  .stat-value {
    margin-left: 20px;
  }
}

/* 主要内容区域 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 24px;
}

/* 补充图表区域 */
.supplementary-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

/* 图表卡片 */
.chart-card {
  background: #1e293b;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

/* 图表卡片标题 */
.chart-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 16px;
}

/* 底部内容区域 */
.bottom-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  align-items: stretch;
}

/* 快捷功能区域 */
.quick-actions-section {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.quick-actions-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

/* 最近操作记录 */
.recent-activities {
  margin-top: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.activity-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-grow: 1;
}

/* 右侧面板 */
.right-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.right-panel .panel-section {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

/* 确保所有图表容器都有合适的高度 */
.chart-container {
  height: 300px;
  width: 100%;
}

/* 补充图表的容器高度 */
.supplementary-charts .chart-container {
  height: 250px;
}

/* 图表区域 */
.chart-section {
  background: #1e293b;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #f8fafc;
}

.section-actions {
  display: flex;
  gap: 6px;
}

.chart-container {
  height: 350px;
}

/* 系统状态监控 */
.system-status-section {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.system-status-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.5), transparent);
}

.system-status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.status-card {
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.status-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.status-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  border-color: rgba(59, 130, 246, 0.4);
}

.status-card:hover::before {
  opacity: 1;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.status-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-title::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.6);
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transition: all 0.3s ease;
  position: relative;
}

.status-indicator.online {
  background: #10b981;
  box-shadow: 0 0 12px rgba(16, 185, 129, 0.6);
  animation: status-pulse 2s infinite;
}

.status-indicator.offline {
  background: #ef4444;
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.6);
}

.status-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.status-item:hover {
  background: rgba(59, 130, 246, 0.1);
  transform: translateX(4px);
}

.status-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-value {
  font-size: 14px;
  color: #f8fafc;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-value::after {
  content: '';
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #3b82f6;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.status-item:hover .status-value::after {
  opacity: 1;
}

/* 状态指示器动画 */
@keyframes status-pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 12px rgba(16, 185, 129, 0.6);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 20px rgba(16, 185, 129, 0.8);
  }
}

/* 系统通知 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #334155;
  border-radius: 10px;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.notification-item:hover {
  background: #475569;
}

.notification-item.unread {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.notification-icon.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.notification-icon.info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.notification-icon.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.notification-icon.error {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 13px;
  font-weight: 500;
  color: #f8fafc;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 11px;
  color: #94a3b8;
}

.notification-actions {
  flex-shrink: 0;
}

.empty-notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 32px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-notification i {
  font-size: 24px;
  opacity: 0.5;
}

/* 右侧面板 */
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-section {
  background: #1e293b;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 16px;
}

/* 快捷功能区域 */
.quick-actions-section {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.quick-actions-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.quick-actions-section .section-subtitle {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* 快捷功能容器 */
.quick-actions-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 快捷功能网格 */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

/* 快捷功能项 */
.quick-action-item {
  background: #334155;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  overflow: hidden;
}

/* 紧凑版快捷功能项 */
.quick-action-item.compact {
  padding: 12px;
  gap: 6px;
}

.quick-action-item:hover {
  background: #475569;
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.quick-action-item:active {
  transform: translateY(0);
}

/* 操作图标 */
.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.action-icon:hover {
  transform: scale(1.05);
}

.action-icon.user {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.action-icon.role {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.4);
}

.action-icon.audit {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.action-icon.stats {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.4);
}

.action-icon.config {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.4);
}

.action-icon.log {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  box-shadow: 0 2px 8px rgba(236, 72, 153, 0.4);
}

.action-text {
  font-size: 12px;
  font-weight: 600;
  color: #f8fafc;
  text-align: center;
  transition: color 0.3s ease;
}

.quick-action-item:hover .action-text {
  color: #3b82f6;
}

/* 操作徽章 */
.action-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  font-size: 10px;
  font-weight: 600;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
  animation: badge-pulse 2s infinite;
}


/* 动画效果 */
@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

@keyframes badge-pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
  }
  50% {
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.6);
  }
}

/* 系统快捷操作 */
.system-quick-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* 快捷访问统计 */
.quick-access-stats {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.action-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.action-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  margin: 0;
}

.action-section-subtitle {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.system-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.system-action-item {
  background: #334155;
  border-radius: 10px;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  overflow: hidden;
}

.system-action-item:hover {
  background: #475569;
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.system-action-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  transition: all 0.3s ease;
}

.system-action-icon.create {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.system-action-icon.export {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.4);
}

.system-action-icon.cache {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.4);
}

.system-action-icon.status {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.system-action-item:hover .system-action-icon {
  transform: scale(1.05);
}

.system-action-text {
  font-size: 12px;
  font-weight: 600;
  color: #f8fafc;
  text-align: center;
  transition: color 0.3s ease;
  line-height: 1.3;
}

.system-action-item:hover .system-action-text {
  color: #3b82f6;
}

/* 快捷访问统计样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-card {
  background: #334155;
  border-radius: 10px;
  padding: 16px 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  background: #475569;
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.stat-icon.user {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.stat-icon.audit {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.stat-icon.config {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.4);
}

.stat-icon.log {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  box-shadow: 0 2px 8px rgba(236, 72, 153, 0.4);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 2px;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-trend {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 6px;
  flex-shrink: 0;
}

.stat-trend.up {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.stat-trend.down {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.stat-card:hover .stat-value {
  color: #3b82f6;
}

.stat-card:hover .stat-label {
  color: #93c5fd;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .system-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .system-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-action-item {
    padding: 12px;
  }
  
  .system-action-item {
    padding: 12px 10px;
  }
  
  .stat-card {
    padding: 12px 10px;
  }
  
  .action-icon {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  
  .stat-icon {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .action-text {
    font-size: 11px;
  }
  
  .system-action-text {
    font-size: 11px;
  }
  
  .stat-value {
    font-size: 16px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .action-section-title {
    font-size: 13px;
  }
  
  .action-section-subtitle {
    font-size: 11px;
  }
}

/* 待办事项 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  overflow: hidden;
}

.todo-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #f59e0b, #ef4444);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.todo-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(245, 158, 11, 0.3);
}

.todo-item:hover::before {
  opacity: 1;
}

.todo-item.completed {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  opacity: 0.8;
}

.todo-item.completed::before {
  background: linear-gradient(180deg, #10b981, #3b82f6);
}

.todo-checkbox {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.todo-checkbox:hover {
  border-color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

.todo-item.completed .todo-checkbox {
  background: linear-gradient(135deg, #10b981, #3b82f6);
  border-color: #10b981;
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 4px;
  transition: color 0.2s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.todo-item:hover .todo-title {
  color: #f59e0b;
}

.todo-item.completed .todo-title {
  color: #94a3b8;
  text-decoration: line-through;
}

.todo-due {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  transition: color 0.2s ease;
}

.todo-item:hover .todo-due {
  color: #d1d5db;
}

.todo-priority {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.todo-priority.priority-1 {
  background: #ef4444;
  box-shadow: 0 0 8px rgba(239, 68, 68, 0.4);
}

.todo-priority.priority-2 {
  background: #f59e0b;
  box-shadow: 0 0 8px rgba(245, 158, 11, 0.4);
}

.todo-priority.priority-3 {
  background: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.todo-item:hover .todo-priority {
  transform: scale(1.2);
}

/* 系统通知 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #334155;
  border-radius: 10px;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.notification-item:hover {
  background: #475569;
}

.notification-item.unread {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.notification-icon.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.notification-icon.info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.notification-icon.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.4);
}

.notification-icon.error {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 13px;
  font-weight: 500;
  color: #f8fafc;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 11px;
  color: #94a3b8;
}

.notification-actions {
  flex-shrink: 0;
}

.empty-notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 32px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-notification i {
  font-size: 24px;
  opacity: 0.5;
}

/* 通知卡片优化 */
.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  overflow: hidden;
}

.notification-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.notification-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(59, 130, 246, 0.3);
}

.notification-item:hover::before {
  opacity: 1;
}

.notification-item.unread {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
}

.notification-item.unread::before {
  background: linear-gradient(180deg, #f59e0b, #ef4444);
  opacity: 1;
}

.notification-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  flex-shrink: 0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.notification-icon::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.notification-item:hover .notification-icon::after {
  left: 100%;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 4px;
  transition: color 0.2s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-item:hover .notification-title {
  color: #3b82f6;
}

.notification-time {
  font-size: 12px;
  color: #94a3b8;
  transition: color 0.2s ease;
}

.notification-item:hover .notification-time {
  color: #93c5fd;
}

.notification-actions {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.notification-item:hover .notification-actions {
  opacity: 1;
}

.empty-notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
}

.empty-notification i {
  font-size: 32px;
  opacity: 0.5;
  transition: all 0.3s ease;
}

.empty-notification:hover i {
  opacity: 0.8;
  transform: scale(1.1);
}

/* 响应式设计优化 */
@media (max-width: 1024px) {
  .system-status-grid {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 16px;
  }
  
  .status-card {
    padding: 16px;
  }
  
  .todo-item {
    padding: 14px;
  }
  
  .notification-item {
    padding: 14px;
  }
}

@media (max-width: 768px) {
  .system-status-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .system-status-section {
    padding: 20px;
  }
  
  .status-card {
    padding: 16px;
  }
  
  .status-item {
    padding: 8px 10px;
  }
  
  .todo-item {
    padding: 12px;
    gap: 10px;
  }
  
  .todo-title {
    font-size: 13px;
  }
  
  .notification-item {
    padding: 12px;
    gap: 10px;
  }
  
  .notification-title {
    font-size: 13px;
  }
  
  .notification-icon {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }
  
  .empty-notification {
    padding: 32px 16px;
  }
  
  .empty-notification i {
    font-size: 28px;
  }
}

/* 待办事项复选框优化 */
.todo-checkbox {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.todo-checkbox:hover {
  border-color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

.todo-item.completed .todo-checkbox {
  background: linear-gradient(135deg, #10b981, #3b82f6);
  border-color: #10b981;
}

/* 最终响应式调整 */
@media (max-width: 480px) {
  .status-title {
    font-size: 14px;
  }
  
  .status-value {
    font-size: 13px;
  }
  
  .todo-content {
    flex: 1;
  }
  
  .notification-content {
    flex: 1;
  }
}

/* 全局卡片动画效果 */
@keyframes card-hover {
  0% {
    transform: translateY(0);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
  100% {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  }
}

/* 确保所有卡片元素的一致性 */
.status-card,
.todo-item,
.notification-item {
  animation-duration: 0.3s;
  animation-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

/* 加载状态优化 */
.loading-content {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

/* 错误提示优化 */
.error-content {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  border: 1px solid rgba(239, 68, 68, 0.2);
}

/* 卡片布局美化完成 */

</style>
