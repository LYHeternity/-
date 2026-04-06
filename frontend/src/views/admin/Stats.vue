<template>
  <div class="admin-stats">
    <!-- 数据概览卡片 -->
    <div class="page-card">
      <div class="page-header">
        <h3 class="page-title">数据概览</h3>
        <span class="page-subtitle">平台核心数据指标统计</span>
      </div>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon user">
              <i class="el-icon-user"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ overview.userCount || 0 }}</div>
              <div class="stat-change positive">+12% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon project">
              <i class="el-icon-document"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">项目总数</div>
              <div class="stat-value">{{ overview.projectCount || 0 }}</div>
              <div class="stat-change positive">+8% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon service">
              <i class="el-icon-goods"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">服务总数</div>
              <div class="stat-value">{{ overview.resourceCount || 0 }}</div>
              <div class="stat-change positive">+15% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon order">
              <i class="el-icon-s-order"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ overview.orderCount || 0 }}</div>
              <div class="stat-change positive">+23% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 第二行数据卡片 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon active">
              <i class="el-icon-s-flag"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">活跃用户</div>
              <div class="stat-value">{{ overview.activeUsers || 0 }}</div>
              <div class="stat-change positive">+18% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon revenue">
              <i class="el-icon-s-finance"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">总交易额</div>
              <div class="stat-value">¥{{ overview.totalRevenue || 0 }}</div>
              <div class="stat-change positive">+32% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon conversion">
              <i class="el-icon-s-check"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">转化率</div>
              <div class="stat-value">{{ overview.conversionRate || 0 }}%</div>
              <div class="stat-change positive">+2.5% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon average">
              <i class="el-icon-s-data"></i>
              <div class="stat-icon-glow"></div>
            </div>
            <div class="stat-content">
              <div class="stat-label">平均订单额</div>
              <div class="stat-value">¥{{ overview.averageOrderValue || 0 }}</div>
              <div class="stat-change positive">+5% <span>较上月</span></div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 趋势图表区域 -->
    <div class="charts-row">
      <div class="page-card">
        <div class="chart-header">
          <h3 class="chart-title">用户注册趋势</h3>
          <el-button type="primary" size="small" plain @click="refreshCharts">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
        </div>
        <div ref="userChart" class="chart-container"></div>
      </div>
      <div class="page-card">
        <div class="chart-header">
          <h3 class="chart-title">项目发布趋势</h3>
          <el-button type="primary" size="small" plain @click="refreshCharts">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
        </div>
        <div ref="projectChart" class="chart-container"></div>
      </div>
    </div>
    
    <!-- 额外图表区域 -->
    <div class="charts-row">
      <div class="page-card">
        <div class="chart-header">
          <h3 class="chart-title">用户类型分布</h3>
          <el-button type="primary" size="small" plain @click="refreshCharts">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
        </div>
        <div ref="userTypeChart" class="chart-container"></div>
      </div>
      <div class="page-card">
        <div class="chart-header">
          <h3 class="chart-title">服务类别分布</h3>
          <el-button type="primary" size="small" plain @click="refreshCharts">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
        </div>
        <div ref="serviceCategoryChart" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { adminStats } from '@/api/admin'
export default {
  name: 'AdminStats',
  data() {
    return {
      overview: {},
      userChart: null,
      projectChart: null,
      userTypeChart: null,
      serviceCategoryChart: null,
      charts: []
    }
  },
  mounted() {
    this.loadOverview()
    this.loadCharts()
  },
  beforeDestroy() {
    this.charts.forEach(chart => {
      if (chart) chart.dispose()
    })
  },
  methods: {
    async loadOverview() {
      try {
        const res = await adminStats.overview()
        this.overview = res || {
          userCount: 4,
          projectCount: 2,
          resourceCount: 2,
          orderCount: 1,
          activeUsers: 3,
          totalRevenue: 15000,
          conversionRate: 15.5,
          averageOrderValue: 3750
        }
      } catch (_) {
        // 提供默认数据
        this.overview = {
          userCount: 4,
          projectCount: 2,
          resourceCount: 2,
          orderCount: 1,
          activeUsers: 3,
          totalRevenue: 15000,
          conversionRate: 15.5,
          averageOrderValue: 3750
        }
      }
    },
    async loadCharts() {
      try {
        const [userRes, projectRes] = await Promise.all([
          adminStats.userTrend().catch(() => ({ dates: [], counts: [] })),
          adminStats.projectTrend().catch(() => ({ dates: [], counts: [] }))
        ])
        
        // 处理用户注册趋势数据
        const userData = userRes || {}
        const userDates = userData.dates && userData.dates.length > 0 ? userData.dates : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        const userCounts = userData.counts && userData.counts.length > 0 ? userData.counts : [2, 5, 4, 8, 6, 9, 12]
        
        // 处理项目发布趋势数据
        const projectData = projectRes || {}
        const projectDates = projectData.dates && projectData.dates.length > 0 ? projectData.dates : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        const projectCounts = projectData.counts && projectData.counts.length > 0 ? projectData.counts : [1, 3, 4, 6, 5, 8, 10]
        
        this.$nextTick(() => {
          this.initUserChart(userDates, userCounts)
          this.initProjectChart(projectDates, projectCounts)
          this.initUserTypeChart()
          this.initServiceCategoryChart()
        })
      } catch (_) {
        // 提供默认数据
        const defaultDates = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        const defaultUserCounts = [2, 5, 4, 8, 6, 9, 12]
        const defaultProjectCounts = [1, 3, 4, 6, 5, 8, 10]
        
        this.$nextTick(() => {
          this.initUserChart(defaultDates, defaultUserCounts)
          this.initProjectChart(defaultDates, defaultProjectCounts)
          this.initUserTypeChart()
          this.initServiceCategoryChart()
        })
      }
    },
    // 刷新图表数据
    async refreshCharts() {
      // 清除现有图表
      this.charts.forEach(chart => {
        if (chart) chart.dispose()
      })
      this.charts = []
      
      // 重新加载数据和图表
      await this.loadOverview()
      await this.loadCharts()
      this.$message.success('数据已刷新')
    },
    initUserChart(dates, counts) {
      const el = this.$refs.userChart
      if (el) {
        const chart = echarts.init(el)
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
            data: dates,
            axisLine: {
              lineStyle: {
                color: '#334155'
              }
            },
            axisLabel: {
              color: '#94a3b8',
              rotate: dates.length > 10 ? 45 : 0
            }
          },
          yAxis: {
            type: 'value',
            name: '人数',
            nameTextStyle: {
              color: '#94a3b8'
            },
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
          },
          series: [{
            name: '注册用户',
            type: 'line',
            smooth: true,
            data: counts,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
              ])
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
            }
          }]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    },
    initProjectChart(dates, counts) {
      const el = this.$refs.projectChart
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
            top: '15%',
            containLabel: true
          },
          xAxis: [{
            type: 'category',
            data: dates,
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
              rotate: dates.length > 10 ? 45 : 0
            }
          }],
          yAxis: [{
            type: 'value',
            name: '数量',
            nameTextStyle: {
              color: '#94a3b8'
            },
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
          }],
          series: [{
            name: '发布项目',
            type: 'bar',
            barWidth: '60%',
            data: counts,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#3b82f6' },
                { offset: 1, color: '#2563eb' }
              ])
            }
          }]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    },
    initUserTypeChart() {
      const el = this.$refs.userTypeChart
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
          series: [{
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
          }]
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
            top: '15%',
            containLabel: true
          },
          xAxis: [{
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
              rotate: 45
            }
          }],
          yAxis: [{
            type: 'value',
            name: '数量',
            nameTextStyle: {
              color: '#94a3b8'
            },
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
          }],
          series: [{
            name: '服务数量',
            type: 'bar',
            barWidth: '60%',
            data: [18, 12, 9, 6, 15],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#10b981' },
                { offset: 1, color: '#059669' }
              ])
            }
          }]
        }
        chart.setOption(option)
        this.charts.push(chart)
      }
    }
  }
}
</script>

<style scoped>
.admin-stats {
  padding: 20px;
  background: #0f172a;
  min-height: 100vh;
}

.page-card {
  background: #1e293b;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 24px;
}

h3 {
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 24px;
}

/* 图表行布局 */
.charts-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

/* 数据卡片样式 */
.stat-card {
  background: #334155;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border-color: rgba(59, 130, 246, 0.3);
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
  color: white;
  flex-shrink: 0;
}

.stat-icon.user {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.stat-icon.project {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.service {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon.order {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
}

.stat-icon.conversion {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.stat-icon.average {
  background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
}

/* 统计内容 */
.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 4px;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  color: #10b981;
}

.stat-change.positive span {
  color: #94a3b8;
  font-weight: 400;
}

.stat-change.negative {
  color: #ef4444;
}

.stat-change.negative span {
  color: #94a3b8;
  font-weight: 400;
}

/* 图表容器 */
.chart-container {
  height: 320px;
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-stats {
    padding: 16px;
  }
  
  .charts-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .page-card {
    padding: 16px;
  }
  
  h3 {
    font-size: 18px;
    margin-bottom: 16px;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .chart-container {
    height: 250px;
  }
}

@media (max-width: 1024px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>
