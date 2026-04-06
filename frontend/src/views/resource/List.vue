<template>
  <div class="page-card service-list-page">
    <div class="list-header">
      <div class="header-content">
        <h1 class="page-title">服务市场</h1>
        <p class="page-subtitle">寻找专业服务，助力企业发展</p>
      </div>
      <div class="view-toggle">
        <el-radio-group v-model="viewMode" size="small" class="view-toggle-group">
          <el-radio-button label="table" class="view-toggle-button">
            <i class="el-icon-s-grid"></i>
            <span>表格</span>
          </el-radio-button>
          <el-radio-button label="card" class="view-toggle-button">
            <i class="el-icon-s-unfold"></i>
            <span>卡片</span>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div class="filter-bar hw-filter-bar">
      <div class="filter-header">
        <h3 class="filter-title">筛选条件</h3>
        <p class="filter-subtitle">根据需求筛选合适的服务</p>
      </div>
      <el-form :inline="true" class="query-form">
        <el-form-item label="分类">
          <el-input v-model="query.category" placeholder="请输入分类" clearable />
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="load">
            <i class="el-icon-search"></i>
            <span>查询</span>
          </el-button>
          <el-button class="reset-button" @click="query = { category: '' }; load()">
            <i class="el-icon-refresh"></i>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <template v-if="viewMode === 'table'">
      <div class="frontend-table-container frontend-table-service-list-container">
        <el-table 
          v-loading="loading" 
          :data="list" 
          border 
          stripe 
          class="frontend-table frontend-table-service-list"
          row-key="id"
          :cell-style="{ backgroundColor: '#ffffff', color: '#4a5568' }"
          :header-cell-style="{ backgroundColor: '#f7fafc', color: '#2d3748' }"
          :row-style="{ backgroundColor: '#ffffff' }"
        >
          <el-table-column prop="title" label="服务名称" min-width="200" align="center">
            <template slot-scope="scope">
              <div class="service-title-cell">
                <span class="service-title">{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="120" align="center">
            <template slot-scope="scope">
              <el-tag size="small" class="category-tag">{{ scope.row.category }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="100" align="center">
            <template slot-scope="scope">
              <span class="price-amount">¥{{ scope.row.price != null ? scope.row.price : '0' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="80" align="center">
            <template slot-scope="scope">
              <div class="rating-container">
                <i class="el-icon-star-on rating-icon"></i>
                <span>{{ scope.row.rating || '0.0' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="providerName" label="服务商" width="120" align="center">
            <template slot-scope="scope">
              <span class="provider-name">{{ scope.row.providerName || '专业服务商' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template slot-scope="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="$router.push('/service/' + scope.row.id)"
                class="detail-button"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
    <template v-else>
      <div v-loading="loading" class="service-waterfall">
        <div
          v-for="(item, idx) in sortedDisplayCards"
          :key="'card-' + (item.id || idx)"
          class="service-card hw-clickable"
          @click="goService(item)"
        >
          <div class="service-card-img">
            <img :src="cardPlaceholder(idx)" :alt="item.title" loading="lazy" />
            <div class="service-card-badges">
              <span class="badge category">{{ item.category }}</span>
              <span class="badge rating" v-if="item.rating">{{ item.rating }}</span>
            </div>
            <button 
              class="like-button" 
              @click.stop="toggleLike(item)"
              :class="{ 'liked': item.liked }"
            >
              <i :class="item.liked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
              <span class="like-count">{{ item.likeCount || 0 }}</span>
            </button>
          </div>
          <div class="service-card-body">
            <h4 class="service-title">{{ item.title }}</h4>
            <div class="service-meta">
              <div class="meta-item">
                <i class="el-icon-user"></i>
                <span>{{ item.providerName || '专业服务商' }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-view"></i>
                <span>{{ item.viewCount || 0 }} 浏览</span>
              </div>
            </div>
            <div class="service-price">¥{{ item.price != null ? item.price : '0' }}</div>
            <div class="service-actions">
              <el-button type="primary" size="small" class="btn-detail">查看详情</el-button>
              <button 
                class="quick-like-button" 
                @click.stop="toggleLike(item)"
                :class="{ 'liked': item.liked }"
              >
                <i :class="item.liked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                点赞
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>
    <div v-if="!loading && !displayCards.length" class="data-empty hw-empty">
      <div class="empty-icon"><i class="el-icon-goods"></i></div>
      <p>暂无服务数据</p>
      <p class="empty-tip">请尝试调整筛选条件或稍后再试</p>
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
import { listPublic } from '@/api/resource'
const PLACEHOLDERS = [
  '/images/1.png',
  '/images/2.png',
  '/images/3.png'
]
const MOCK_TITLES = ['市场推广', '法律咨询', '财务顾问', '技术开发', '品牌设计', '人力资源', '知识产权', '融资辅导', '战略规划', '数字化咨询', '供应链管理', '营销策划', '公关传播', '数据分析', 'UI/UX设计', '运维支持', '安全审计', '培训服务', '会务策划', '翻译服务']
const MOCK_CATEGORIES = ['市场', '法律', '财务', '技术', '设计', '咨询', '培训', '其他']
function getMockServices(n) {
  return Array.from({ length: n }, (_, i) => ({
    id: 'mock-' + (i + 1),
    title: MOCK_TITLES[i % MOCK_TITLES.length] + (i >= MOCK_TITLES.length ? ' ' + (i + 1) : ''),
    category: MOCK_CATEGORIES[i % MOCK_CATEGORIES.length],
    price: [999, 1999, 2999, 4999, 9999][i % 5],
    rating: (4 + (i % 10) / 10).toFixed(1),
    providerName: '服务商' + (i % 5 + 1)
  }))
}
export default {
  name: 'ServiceList',
  data() {
    return {
      loading: false,
      list: [],
      query: { category: '' },
      page: { current: 1, size: 10, total: 0 },
      viewMode: 'card',
      mockList: getMockServices(20)
    }
  },
  computed: {
    displayCards() {
      // 优先使用实际API数据，即使为空也不使用模拟数据，这样分页功能才能正常工作
      return this.list
    },
    sortedDisplayCards() {
      return [...this.displayCards].sort((a, b) => {
        const likeA = a.likeCount || 0
        const likeB = b.likeCount || 0
        return likeB - likeA
      })
    }
  },
  created() {
    this.load()
  },
  methods: {
    goService(item) {
      if (item.id && String(item.id).indexOf('mock') === -1)
        this.$router.push('/service/' + item.id)
    },
    cardPlaceholder(i) {
      return PLACEHOLDERS[i % PLACEHOLDERS.length]
    },
    async load() {
      this.loading = true
      try {
        const res = await listPublic({ current: this.page.current, size: this.page.size, ...this.query })
        console.log('服务市场API返回数据:', res)
        this.list = res.records || []
        this.page.total = res.total || 0
        console.log('服务市场设置总数:', this.page.total, '记录数:', this.list.length)
      } finally {
        this.loading = false
      }
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
    toggleLike(item) {
      item.liked = !item.liked
      item.likeCount = (item.likeCount || 0) + (item.liked ? 1 : -1)
      // 这里可以添加实际的点赞API调用
      this.$message.success(item.liked ? '点赞成功' : '取消点赞')
    },
    // 表格行样式
    tableRowClassName({ row, rowIndex }) {
      return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
    },
    // 表格单元格样式
    tableCellStyle() {
      return {
        padding: '14px 16px',
        fontSize: '14px',
        color: '#4a5568',
        borderBottom: '1px solid #e2e8f0'
      }
    },
    // 表格表头样式
    tableHeaderCellStyle() {
      return {
        backgroundColor: '#f7fafc',
        color: '#ffffff',
        fontSize: '14px',
        fontWeight: '600',
        padding: '14px 16px',
        borderBottom: '2px solid #3b82f6',
        borderTop: '1px solid #e2e8f0'
      }
    }
  }
}
</script>

<style scoped>
/* 列表头部样式 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #333333;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #666666;
  margin: 0;
  line-height: 1.4;
}

.view-toggle {
  flex-shrink: 0;
}

.view-toggle-group {
  display: flex;
  background: #f8fafc;
  border-radius: 12px;
  padding: 4px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.view-toggle-button {
  border-radius: 8px !important;
  border: none !important;
  margin: 0 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.view-toggle-button .el-radio-button__inner {
  border: none !important;
  border-radius: 8px !important;
  background: transparent;
  color: #666666;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
}

.view-toggle-button.is-active .el-radio-button__inner {
  background: #3b82f6;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.view-toggle-button:hover .el-radio-button__inner {
  background: rgba(59, 130, 246, 0.05);
  color: #3b82f6;
}

.view-toggle-button.is-active:hover .el-radio-button__inner {
  background: #2563eb;
  color: white;
}

/* 筛选栏样式 */
.hw-filter-bar {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.hw-filter-bar:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.filter-header {
  margin-bottom: 20px;
}

.filter-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
  margin: 0 0 4px 0;
}

.filter-subtitle {
  font-size: 14px;
  color: #666666;
  margin: 0;
}

.query-form {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  justify-content: space-between;
}

/* 确保按钮在右侧 */
.query-form .el-form-item:last-child {
  margin-left: auto;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 重置按钮样式 */
.reset-button {
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  width: auto !important;
  min-width: 80px;
  background-color: #ffffff !important;
  border: 1px solid #d9d9d9 !important;
  color: #333333 !important;
  transition: all 0.3s ease !important;
}

.reset-button:hover {
  background-color: #f5f5f5 !important;
  border-color: #1890ff !important;
  color: #1890ff !important;
}

.reset-button:active {
  background-color: #e6f7ff !important;
  border-color: #1890ff !important;
  color: #1890ff !important;
}

/* 调整表单项间距 */
.el-form--inline .el-form-item {
  margin-right: 16px;
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .query-form {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .query-form .el-form-item {
    margin-right: 0;
    width: 100%;
  }
  
  .query-form .el-form-item:last-child {
    margin-left: 0;
    display: flex;
    gap: 8px;
  }
  
  .query-form .el-form-item:last-child .el-button {
    flex: 1;
  }
}

/* 服务卡片瀑布流 */
.service-waterfall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 28px;
  margin-bottom: 32px;
}

/* 服务卡片样式 */
.service-card {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.service-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-10px);
  border-color: #3b82f6;
}

.service-card-img {
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  position: relative;
}

.service-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.service-card:hover .service-card-img img {
  transform: scale(1.1);
}

.service-card-badges {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
  z-index: 2;
}

.badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.badge.category {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.badge.rating {
  background: rgba(251, 191, 36, 0.2);
  color: #f59e0b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.badge.rating::before {
  content: '★';
  font-size: 10px;
}

.badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.like-button {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(12px);
  z-index: 2;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.like-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.05) translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.like-button.liked {
  background: rgba(251, 191, 36, 0.95);
  color: white;
  border-color: rgba(251, 191, 36, 0.3);
  box-shadow: 0 2px 8px rgba(251, 191, 36, 0.3);
}

.like-button.liked:hover {
  background: rgba(251, 191, 36, 1);
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.4);
}

.like-button i {
  font-size: 16px;
  transition: all 0.3s ease;
}

.like-button.liked i {
  animation: pulse 0.6s ease;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.like-count {
  font-size: 12px;
  font-weight: 700;
  transition: all 0.3s ease;
}

.service-card-body {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.service-title {
  font-size: 20px;
  font-weight: 600;
  color: #333333;
  margin: 0 0 16px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.3s ease;
}

.service-card:hover .service-title {
  color: #3b82f6;
}

.service-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 16px;
  flex: 1;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666666;
  transition: all 0.3s ease;
}

.meta-item i {
  font-size: 16px;
  color: #3b82f6;
  transition: all 0.3s ease;
}

.service-card:hover .meta-item i {
  transform: scale(1.1);
}

.service-price {
  font-size: 28px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 20px;
  line-height: 1;
  transition: all 0.3s ease;
}

.service-card:hover .service-price {
  transform: scale(1.05);
  text-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.service-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.btn-detail {
  flex: 1;
  border-radius: 8px;
  font-weight: 600;
  padding: 10px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-detail:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.quick-like-button {
  padding: 10px 20px;
  border: 1px solid #3b82f6;
  border-radius: 8px;
  background: white;
  color: #3b82f6;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.quick-like-button:hover {
  background: rgba(59, 130, 246, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.quick-like-button.liked {
  background: #3b82f6;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.quick-like-button.liked:hover {
  background: #2563eb;
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.quick-like-button i {
  font-size: 14px;
  transition: all 0.3s ease;
}

.quick-like-button.liked i {
  animation: pulse 0.6s ease;
}

/* 空数据状态 */
.hw-empty {
  text-align: center;
  padding: 60px 20px;
  background: #f8fafc;
  border-radius: 16px;
  border: 2px dashed rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.hw-empty .empty-icon {
  font-size: 48px;
  color: #94a3b8;
  margin-bottom: 16px;
}

.hw-empty p {
  font-size: 16px;
  color: #666666;
  margin: 0 0 8px 0;
}

.hw-empty .empty-tip {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* 分页样式 */
.el-pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.el-pagination__sizes {
  margin-right: 16px;
}

.el-pagination button {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-pagination button:hover {
  color: #3b82f6;
  border-color: #3b82f6;
}

.el-pagination .el-pager li {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-pagination .el-pager li:hover {
  color: #3b82f6;
}

.el-pagination .el-pager li.active {
  background: #3b82f6;
  color: white;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

/* 表格样式 */
.hw-table {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  transition: all 0.3s ease;
}

.hw-table:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

/* 表格行样式 */
.hw-table .even-row {
  background-color: #ffffff;
}

.hw-table .odd-row {
  background-color: #f8fafc;
}

.hw-table tr {
  transition: all 0.3s ease;
}

.hw-table tr:hover {
  background-color: #f0f9ff !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

/* 表格单元格样式 */
.table-cell-content {
  display: flex;
  align-items: center;
}

.cell-text {
  font-size: 14px;
  color: #333333;
  line-height: 1.4;
}

/* 分类标签样式 */
.category-tag {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 价格样式 */
.price-amount {
  font-size: 14px;
  font-weight: 500;
  color: #ff4d4f;
}

/* 评分样式 */
.rating-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating-icon {
  color: #faad14;
  font-size: 14px;
}

/* 服务商名称样式 */
.provider-name {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

/* 操作按钮样式 */
.table-action-button {
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.table-action-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* 服务表格样式 */
.service-table-container {
  margin-bottom: 32px;
}

/* 唯一表格容器 */
.unique-service-table-container {
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

/* 唯一表格容器 */
.unique-service-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.unique-service-table:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

/* 唯一表格表头 */
.unique-service-table th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 唯一表格单元格 */
.unique-service-table td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 唯一表格服务标题单元格 - 特殊处理 */
.unique-service-table td .service-title-cell {
  justify-content: center;
}

/* 唯一表格行悬停效果 */
.unique-service-table tr:hover td {
  background-color: #f0f9ff !important;
  transition: background-color 0.3s ease;
}

/* 唯一表格条纹行 */
.unique-service-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

/* 唯一表格条纹行悬停效果 */
.unique-service-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
}

/* 唯一表格服务标题单元格 */
.unique-service-table .service-title-cell {
  display: flex;
  align-items: center;
}

.unique-service-table .service-title {
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
}

/* 唯一表格分类标签 */
.unique-service-table .category-tag {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格价格 */
.unique-service-table .price-amount {
  font-size: 14px;
  font-weight: 500;
  color: #ff4d4f;
}

/* 唯一表格评分计数 */
.unique-service-table .rating-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.unique-service-table .rating-icon {
  color: #faad14;
  font-size: 14px;
}

/* 唯一表格服务商名称 */
.unique-service-table .provider-name {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

/* 唯一表格详情按钮 */
.unique-service-table .detail-button {
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.unique-service-table .detail-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* 表格响应式设计 */
@media (max-width: 1200px) {
  .service-table {
    font-size: 13px;
  }
  
  .service-table th,
  .service-table td {
    padding: 10px 12px;
  }
  
  .el-table-column {
    min-width: 80px;
  }
  
  /* 唯一表格响应式设计 */
  .unique-service-table {
    font-size: 13px;
  }
  
  .unique-service-table th,
  .unique-service-table td {
    padding: 10px 12px;
  }
}

@media (max-width: 768px) {
  .service-table {
    font-size: 12px;
  }
  
  .service-table th,
  .service-table td {
    padding: 8px 10px;
  }
  
  .el-table-column {
    min-width: 60px;
  }
  
  .detail-button {
    padding: 2px 8px;
    font-size: 11px;
  }
  
  /* 唯一表格响应式设计 */
  .unique-service-table {
    font-size: 12px;
  }
  
  .unique-service-table th,
  .unique-service-table td {
    padding: 8px 10px;
  }
  
  .unique-service-table .detail-button {
    padding: 2px 8px;
    font-size: 11px;
  }
}

/* 浅色表格样式 - 覆盖深色主题 */
/* 服务表格 - 覆盖深色主题 */
#app.dark-theme .service-table {
  background-color: #ffffff !important;
  background-image: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid #e2e8f0 !important;
}

/* 唯一表格 - 覆盖深色主题 */
#app.dark-theme .unique-service-table {
  background-color: #ffffff !important;
  background-image: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid #e2e8f0 !important;
  position: relative;
  z-index: 2;
}

/* 唯一表格内部结构 - 覆盖深色主题 */
#app.dark-theme .unique-service-table [class*="el-table"] {
  background-color: #ffffff !important;
  background-image: none !important;
}

/* 唯一表格表头 - 覆盖深色主题 */
#app.dark-theme .unique-service-table [class*="el-table"] th {
  background-color: #f7fafc !important;
  color: #ffffff !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
}

/* 唯一表格单元格 - 覆盖深色主题 */
#app.dark-theme .unique-service-table [class*="el-table"] td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
}

/* 唯一表格单元格内容 - 覆盖深色主题 */
#app.dark-theme .unique-service-table .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
}

/* 唯一表格表头单元格 - 覆盖深色主题 */
#app.dark-theme .unique-service-table th .cell {
  background-color: transparent !important;
  color: #ffffff !important;
}

/* 直接修改表格单元格元素 - 覆盖深色主题 */
#app.dark-theme .unique-service-table .el-table__cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
}

/* 直接修改表格表头单元格元素 - 覆盖深色主题 */
#app.dark-theme .unique-service-table th.el-table__cell {
  background-color: #f7fafc !important;
  color: #ffffff !important;
}

/* 直接修改表格单元格内容元素 - 覆盖深色主题 */
#app.dark-theme .unique-service-table .el-table__cell .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
}

/* 直接修改表格表头单元格内容元素 - 覆盖深色主题 */
#app.dark-theme .unique-service-table th.el-table__cell .cell {
  background-color: transparent !important;
  color: #ffffff !important;
}

/* ElementUI服务表格内部结构 */
#app.dark-theme .service-table .el-table,
#app.dark-theme .service-table .el-table__inner-wrapper,
#app.dark-theme .service-table .el-table__header-wrapper,
#app.dark-theme .service-table .el-table__body-wrapper,
#app.dark-theme .service-table .el-table__footer-wrapper,
#app.dark-theme .service-table .el-table__header,
#app.dark-theme .service-table .el-table__body,
#app.dark-theme .service-table .el-table__footer {
  background-color: #ffffff !important;
  background-image: none !important;
}

/* ElementUI唯一表格内部结构 */
#app.dark-theme .unique-service-table .el-table,
#app.dark-theme .unique-service-table .el-table__inner-wrapper,
#app.dark-theme .unique-service-table .el-table__header-wrapper,
#app.dark-theme .unique-service-table .el-table__body-wrapper,
#app.dark-theme .unique-service-table .el-table__footer-wrapper,
#app.dark-theme .unique-service-table .el-table__header,
#app.dark-theme .unique-service-table .el-table__body,
#app.dark-theme .unique-service-table .el-table__footer {
  background-color: #ffffff !important;
  background-image: none !important;
}

/* 服务表格表头 */
#app.dark-theme .service-table .el-table th,
#app.dark-theme .service-table .el-table__header-wrapper th,
#app.dark-theme .service-table .el-table__header th {
  background-color: #f7fafc !important;
  color: #ffffff !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
}

/* 唯一表格表头 */
#app.dark-theme .unique-service-table .el-table th,
#app.dark-theme .unique-service-table .el-table__header-wrapper th,
#app.dark-theme .unique-service-table .el-table__header th {
  background-color: #f7fafc !important;
  color: #ffffff !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 服务表格单元格 */
#app.dark-theme .service-table .el-table td,
#app.dark-theme .service-table .el-table__body-wrapper td,
#app.dark-theme .service-table .el-table__body td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
}

/* 唯一表格单元格 */
#app.dark-theme .unique-service-table .el-table td,
#app.dark-theme .unique-service-table .el-table__body-wrapper td,
#app.dark-theme .unique-service-table .el-table__body td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 服务表格行悬停效果 */
#app.dark-theme .service-table .el-table tr:hover td,
#app.dark-theme .service-table .el-table__body-wrapper tr:hover td,
#app.dark-theme .service-table .el-table__body tr:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
  transition: background-color 0.3s ease;
}

/* 唯一表格行悬停效果 */
#app.dark-theme .unique-service-table .el-table tr:hover td,
#app.dark-theme .unique-service-table .el-table__body-wrapper tr:hover td,
#app.dark-theme .unique-service-table .el-table__body tr:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
  transition: background-color 0.3s ease;
}

/* 服务表格条纹行 */
#app.dark-theme .service-table .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
  background-image: none !important;
}

/* 唯一表格条纹行 */
#app.dark-theme .unique-service-table .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
  background-image: none !important;
}

/* 服务表格条纹行悬停效果 */
#app.dark-theme .service-table .el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
}

/* 唯一表格条纹行悬停效果 */
#app.dark-theme .unique-service-table .el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
}

/* 表格响应式设计 */
@media (max-width: 1200px) {
  .hw-table {
    font-size: 13px;
  }
  
  .hw-table th,
  .hw-table td {
    padding: 10px 12px;
  }
  
  .el-table-column {
    min-width: 80px;
  }
}

@media (max-width: 768px) {
  .hw-table {
    font-size: 12px;
  }
  
  .hw-table th,
  .hw-table td {
    padding: 8px 10px;
  }
  
  .el-table-column {
    min-width: 60px;
  }
  
  .table-action-button {
    padding: 2px 8px;
    font-size: 11px;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .service-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
  }
  
  .service-card-img {
    height: 180px;
  }
}

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .query-form {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .hw-input {
    min-width: 100%;
  }
  
  .form-item {
    width: 100%;
  }
  
  .service-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
  }
  
  .service-card-img {
    height: 220px;
  }
  
  .service-actions {
    flex-direction: column;
  }
  
  .quick-like-button {
    justify-content: center;
  }
  
  .hw-filter-bar {
    padding: 20px;
  }
  
  .service-card-body {
    padding: 20px;
  }
  
  .service-title {
    font-size: 18px;
  }
  
  .service-price {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .service-card-img {
    height: 180px;
  }
  
  .hw-filter-bar {
    padding: 16px;
  }
  
  .service-card-body {
    padding: 16px;
  }
}
</style>
