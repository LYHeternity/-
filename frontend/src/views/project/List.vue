<template>
  <div class="page-card project-list-page">
    <div class="list-header">
      <div class="header-content">
        <h1 class="page-title">项目大厅</h1>
        <p class="page-subtitle">发现优质创业项目，寻找合作机会</p>
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
        <p class="filter-subtitle">根据需求筛选合适的项目</p>
      </div>
      <el-form :inline="true" class="query-form">
        <el-form-item label="行业">
          <el-input v-model="query.industry" placeholder="请输入行业" clearable />
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="query.stage" placeholder="全部阶段" clearable>
            <el-option label="想法" value="idea" />
            <el-option label="开发中" value="开发中" />
            <el-option label="已上线" value="已上线" />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="load">
            <i class="el-icon-search"></i>
            <span>查询</span>
          </el-button>
          <el-button class="reset-button" @click="query = { industry: '', stage: '' }; load()">
            <i class="el-icon-refresh"></i>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <template v-if="viewMode === 'table'">
      <div class="frontend-table-container frontend-table-project-list-container">
        <el-table 
          v-loading="loading" 
          :data="list" 
          border 
          stripe 
          class="frontend-table frontend-table-project-list"
          row-key="id"
          :cell-style="{ backgroundColor: '#ffffff', color: '#4a5568' }"
          :header-cell-style="{ backgroundColor: '#f7fafc', color: '#2d3748' }"
          :row-style="{ backgroundColor: '#ffffff' }"
        >
          <el-table-column prop="title" label="项目名称" min-width="200" align="center">
            <template slot-scope="scope">
              <div class="project-title-cell">
                <span class="project-title">{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="industry" label="行业" width="100" align="center">
            <template slot-scope="scope">
              <el-tag size="small" class="industry-tag">{{ scope.row.industry }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="stage" label="阶段" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                size="small" 
                :type="getStageType(scope.row.stage)"
                class="stage-tag"
              >
                {{ scope.row.stage }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fundingNeeded" label="融资需求(万)" width="120" align="center">
            <template slot-scope="scope">
              <span class="funding-amount">{{ scope.row.fundingNeeded != null ? scope.row.fundingNeeded : '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="likeCount" label="点赞" width="80" align="center">
            <template slot-scope="scope">
              <div class="like-count-container">
                <i class="el-icon-star-on like-icon"></i>
                <span>{{ scope.row.likeCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" align="center">
            <template slot-scope="scope">
              <div class="view-count-container">
                <i class="el-icon-view view-icon"></i>
                <span>{{ scope.row.viewCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template slot-scope="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="$router.push('/project/' + scope.row.id)"
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
      <div v-loading="loading" class="project-waterfall">
        <div
          v-for="(item, idx) in sortedDisplayCards"
          :key="'card-' + (item.id || idx)"
          class="project-card hw-clickable"
          @click="goProject(item)"
        >
          <div class="project-card-img">
            <img 
              :src="getImageUrl(item.projectImage) || (item.coverImage || cardPlaceholder(idx))" 
              :alt="item.title" 
              loading="lazy"
              @error="$event.target.src = cardPlaceholder(idx)"
            />
            <div class="project-card-badges">
              <span class="badge industry">{{ item.industry }}</span>
              <span class="badge stage">{{ item.stage }}</span>
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
          <div class="project-card-body">
            <h4 class="project-title">{{ item.title }}</h4>
            <div class="project-meta">
              <div class="meta-item">
                <i class="el-icon-money"></i>
                <span>融资需求: {{ item.fundingNeeded != null ? item.fundingNeeded + ' 万' : '-' }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-view"></i>
                <span>{{ item.viewCount || 0 }} 浏览</span>
              </div>
            </div>
            <div class="project-actions">
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
      <div class="empty-icon"><i class="el-icon-document"></i></div>
      <p>暂无项目数据</p>
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
import { listPublic } from '@/api/project'
import { getImageUrl as getImageUrlUtil } from '@/utils/image'
const PLACEHOLDERS = [
  '/images/1.png',
  '/images/2.png',
  '/images/3.png'
]
const MOCK_TITLES = ['智能硬件孵化', '企业SaaS平台', '新能源出行', '医疗健康数字化', '教育科技产品', '跨境电商', '本地生活O2O', '金融科技', 'AI内容工具', '供应链协同', '智慧园区', '环保科技', '农业物联网', '文创IP', '企业培训SaaS', '智能客服', '数据安全', '区块链存证', '短视频营销', '工业互联网']
const MOCK_INDUSTRIES = ['科技', '消费', '医疗', '教育', '金融', '制造', '企业服务', '文娱']
const MOCK_STAGES = ['想法', '开发中', '已上线']
function getMockProjects(n) {
  return Array.from({ length: n }, (_, i) => ({
    id: 'mock-' + (i + 1),
    title: MOCK_TITLES[i % MOCK_TITLES.length] + (i >= MOCK_TITLES.length ? ' ' + (i + 1) : ''),
    industry: MOCK_INDUSTRIES[i % MOCK_INDUSTRIES.length],
    stage: MOCK_STAGES[i % 3],
    fundingNeeded: [50, 100, 200, 500, 1000][i % 5],
    likeCount: 10 + (i % 20),
    viewCount: 50 + (i % 100),
    projectImage: (i % 3 + 1) + '.png'
  }))
}
export default {
  name: 'ProjectList',
  data() {
    return {
      loading: false,
      list: [],
      query: { industry: '', stage: '' },
      page: { current: 1, size: 10, total: 0 },
      viewMode: 'card',
      mockList: getMockProjects(20)
    }
  },
  computed: {
    displayCards() {
      // 只使用实际API数据，不使用模拟数据
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
    goProject(item) {
      if (item.id && String(item.id).indexOf('mock') === -1)
        this.$router.push('/project/' + item.id)
    },
    cardPlaceholder(i) {
      return PLACEHOLDERS[i % PLACEHOLDERS.length]
    },
    getImageUrl(imageName) {
      return getImageUrlUtil(imageName)
    },
    async load() {
      this.loading = true
      try {
        const res = await listPublic({ current: this.page.current, size: this.page.size, ...this.query })
        console.log('项目列表API返回数据:', res)
        this.list = res.records || []
        this.page.total = res.total || 0
        console.log('项目列表设置总数:', this.page.total, '记录数:', this.list.length)
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
        color: '#2d3748',
        fontSize: '14px',
        fontWeight: '600',
        padding: '14px 16px',
        borderBottom: '2px solid #3b82f6',
        borderTop: '1px solid #e2e8f0'
      }
    },
    // 获取阶段标签类型
    getStageType(stage) {
      switch (stage) {
        case '想法':
          return 'info'
        case '开发中':
          return 'warning'
        case '已上线':
          return 'success'
        default:
          return ''
      }
    }
  }
}
</script>

<style scoped>
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.view-toggle {
  flex-shrink: 0;
}

.view-toggle-group {
  display: flex;
  background: #f3f4f6;
  border-radius: 8px;
  padding: 2px;
}

.view-toggle-button {
  border-radius: 6px !important;
  border: none !important;
  margin: 0 !important;
  transition: all 0.2s ease;
}

.view-toggle-button .el-radio-button__inner {
  border: none !important;
  border-radius: 6px !important;
  background: transparent;
  color: #6b7280;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
}

.view-toggle-button.is-active .el-radio-button__inner {
  background: #3b82f6;
  color: white;
}

.view-toggle-button:hover .el-radio-button__inner {
  color: #3b82f6;
}

.view-toggle-button.is-active:hover .el-radio-button__inner {
  background: #6d28d9;
  color: white;
}

.hw-filter-bar {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid #e5e7eb;
}

.filter-header {
  margin-bottom: 16px;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e1b4b;
  margin: 0 0 4px 0;
}

.filter-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.query-form {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  justify-content: space-between;
}

.query-form .el-form-item:last-child {
  margin-left: auto;
}

.button-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.reset-button {
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  width: auto !important;
  min-width: 80px;
  background-color: #ffffff !important;
  border: 1px solid #d1d5db !important;
  color: #374151 !important;
  transition: all 0.2s ease !important;
}

.reset-button:hover {
  background-color: #f3f4f6 !important;
  border-color: #3b82f6 !important;
  color: #3b82f6 !important;
}

.el-form--inline .el-form-item {
  margin-right: 16px;
  margin-bottom: 0;
}

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

.project-waterfall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.project-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  cursor: pointer;
}

.project-card:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.project-card-img {
  height: 180px;
  overflow: hidden;
  background: #f3f4f6;
  position: relative;
}

.project-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-card-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
  z-index: 2;
}

.badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.badge.industry {
  background: #3b82f6;
  color: white;
}

.badge.stage {
  background: #06b6d4;
  color: white;
}

.like-button {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 2;
  font-size: 12px;
  font-weight: 500;
}

.like-button:hover {
  background: #f3f4f6;
}

.like-button.liked {
  background: #fbbf24;
  color: white;
  border-color: #fbbf24;
}

.like-button i {
  font-size: 14px;
}

.like-count {
  font-size: 12px;
  font-weight: 600;
}

.project-card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.project-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e1b4b;
  margin: 0 0 12px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-card:hover .project-title {
  color: #3b82f6;
}

.project-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  flex: 1;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
}

.meta-item i {
  font-size: 14px;
  color: #3b82f6;
}

.project-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
}

.btn-detail {
  flex: 1;
  border-radius: 6px;
  font-weight: 500;
  padding: 8px 16px;
  background: #3b82f6;
  border-color: #3b82f6;
}

.btn-detail:hover {
  background: #2563eb;
  border-color: #2563eb;
}

.quick-like-button {
  padding: 8px 16px;
  border: 1px solid #3b82f6;
  border-radius: 6px;
  background: white;
  color: #3b82f6;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
}

.quick-like-button:hover {
  background: #eff6ff;
}

.quick-like-button.liked {
  background: #3b82f6;
  color: white;
}

.quick-like-button i {
  font-size: 12px;
}

.hw-empty {
  text-align: center;
  padding: 48px 20px;
  background: #f9fafb;
  border-radius: 12px;
  border: 2px dashed #e5e7eb;
  margin-bottom: 24px;
}

.hw-empty .empty-icon {
  font-size: 40px;
  color: #9ca3af;
  margin-bottom: 12px;
}

.hw-empty p {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 6px 0;
}

.hw-empty .empty-tip {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

.el-pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.el-pagination__sizes {
  margin-right: 16px;
}

.el-pagination button {
  border-radius: 6px;
  transition: all 0.2s ease;
}

.el-pagination button:hover {
  color: #3b82f6;
  border-color: #3b82f6;
}

.el-pagination .el-pager li {
  border-radius: 6px;
  transition: all 0.2s ease;
}

.el-pagination .el-pager li:hover {
  color: #3b82f6;
}

.el-pagination .el-pager li.active {
  background: #3b82f6;
  color: white;
}

.hw-table {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  border: 1px solid #e5e7eb;
  background-color: #ffffff;
}

.hw-table .even-row {
  background-color: #ffffff;
}

.hw-table .odd-row {
  background-color: #f9fafb;
}

.hw-table tr:hover {
  background-color: #eff6ff !important;
}

.table-cell-content {
  display: flex;
  align-items: center;
}

.cell-text {
  font-size: 14px;
  color: #374151;
  line-height: 1.4;
}

.industry-tag {
  background-color: #dbeafe;
  color: #3b82f6;
  border-color: #93c5fd;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
}

.stage-tag {
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
}

.funding-amount {
  font-weight: 600;
  color: #1e1b4b;
}

.like-count-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.like-count-container .like-icon {
  color: #fbbf24;
  font-size: 14px;
}

.view-count-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.view-count-container .view-icon {
  color: #06b6d4;
  font-size: 14px;
}

.project-title-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-title-cell .project-title {
  font-weight: 500;
}

.detail-button {
  background: #3b82f6;
  border-color: #3b82f6;
}

.detail-button:hover {
  background: #2563eb;
  border-color: #2563eb;
}

.project-table-container {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.project-table {
  border: none;
}

.project-table::before {
  display: none;
}

.project-table::after {
  display: none;
}

.hw-clickable {
  cursor: pointer;
}

@media (max-width: 768px) {
  .project-waterfall {
    grid-template-columns: 1fr;
  }
  
  .list-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
}
</style>
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

/* 点赞计数样式 */
.like-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-icon {
  color: #ff4d4f;
  font-size: 14px;
}

/* 浏览计数样式 */
.view-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-icon {
  color: #1890ff;
  font-size: 14px;
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

/* 项目表格样式 */
.project-table-container {
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
.unique-project-table td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 唯一表格项目标题单元格 - 特殊处理 */
.unique-project-table td .project-title-cell {
  justify-content: center;
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

/* 唯一表格项目标题单元格 */
.unique-project-table .project-title-cell {
  display: flex;
  align-items: center;
}

.unique-project-table .project-title {
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
}

/* 唯一表格行业标签 */
.unique-project-table .industry-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格阶段标签 */
.unique-project-table .stage-tag {
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格融资金额 */
.unique-project-table .funding-amount {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

/* 唯一表格点赞计数 */
.unique-project-table .like-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.unique-project-table .like-icon {
  color: #ff4d4f;
  font-size: 14px;
}

/* 唯一表格浏览计数 */
.unique-project-table .view-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

.unique-project-table .view-icon {
  color: #1890ff;
  font-size: 14px;
}

/* 唯一表格详情按钮 */
.unique-project-table .detail-button {
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.unique-project-table .detail-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* 表格响应式设计 */
@media (max-width: 1200px) {
  .project-table {
    font-size: 13px;
  }
  
  .project-table th,
  .project-table td {
    padding: 10px 12px;
  }
  
  .el-table-column {
    min-width: 80px;
  }
}

@media (max-width: 768px) {
  .project-table {
    font-size: 12px;
  }
  
  .project-table th,
  .project-table td {
    padding: 8px 10px;
  }
  
  .el-table-column {
    min-width: 60px;
  }
  
  .detail-button {
    padding: 2px 8px;
    font-size: 11px;
  }
}

/* 项目表格 - 覆盖深色主题 */
#app.dark-theme .project-table {
  background-color: #ffffff !important;
  background-image: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid #e2e8f0 !important;
}

/* 唯一表格容器 - 使用ID选择器 */
#unique-project-table-container {
  margin-bottom: 32px;
  position: relative;
  z-index: 9999;
}

/* 唯一表格 - 使用ID选择器 */
#unique-project-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  transition: all 0.3s ease;
  position: relative;
  z-index: 9999;
}

#unique-project-table:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

/* 唯一表格表头 - 使用ID选择器 */
#unique-project-table th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
  z-index: 9999 !important;
}

/* 唯一表格单元格 - 使用ID选择器 */
#unique-project-table td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
  z-index: 9999 !important;
}

/* 唯一表格行悬停效果 - 使用ID选择器 */
#unique-project-table tr:hover td {
  background-color: #f0f9ff !important;
  transition: background-color 0.3s ease;
}

/* 唯一表格条纹行 - 使用ID选择器 */
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

/* 唯一表格条纹行悬停效果 - 使用ID选择器 */
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
}

/* 唯一表格项目标题单元格 - 使用ID选择器 */
#unique-project-table .project-title-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

#unique-project-table .project-title {
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
}

/* 唯一表格行业标签 - 使用ID选择器 */
#unique-project-table .industry-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格阶段标签 - 使用ID选择器 */
#unique-project-table .stage-tag {
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
}

/* 唯一表格融资金额 - 使用ID选择器 */
#unique-project-table .funding-amount {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
}

/* 唯一表格点赞计数 - 使用ID选择器 */
#unique-project-table .like-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

#unique-project-table .like-icon {
  color: #ff4d4f;
  font-size: 14px;
}

/* 唯一表格浏览计数 - 使用ID选择器 */
#unique-project-table .view-count-container {
  display: flex;
  align-items: center;
  gap: 4px;
}

#unique-project-table .view-icon {
  color: #1890ff;
  font-size: 14px;
}

/* 唯一表格详情按钮 - 使用ID选择器 */
#unique-project-table .detail-button {
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

#unique-project-table .detail-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

/* 唯一表格响应式设计 - 使用ID选择器 */
@media (max-width: 1200px) {
  #unique-project-table {
    font-size: 13px;
  }
  
  #unique-project-table th,
  #unique-project-table td {
    padding: 10px 12px;
  }
}

@media (max-width: 768px) {
  #unique-project-table {
    font-size: 12px;
  }
  
  #unique-project-table th,
  #unique-project-table td {
    padding: 8px 10px;
  }
  
  #unique-project-table .detail-button {
    padding: 2px 8px;
    font-size: 11px;
  }
}

/* 唯一表格 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table {
  background-color: #ffffff !important;
  background-image: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid #e2e8f0 !important;
  position: relative;
  z-index: 9999 !important;
}

/* 唯一表格内部结构 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table [class*="el-table"] {
  background-color: #ffffff !important;
  background-image: none !important;
  z-index: 9999 !important;
}

/* 唯一表格表头 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table [class*="el-table"] th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
  z-index: 9999 !important;
}

/* 唯一表格单元格 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table [class*="el-table"] td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
  z-index: 9999 !important;
}

/* 唯一表格单元格内容 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 唯一表格表头单元格 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table th .cell {
  background-color: transparent !important;
  color: #2d3748 !important;
  z-index: 9999 !important;
}

/* 直接修改表格单元格元素 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table .el-table__cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 直接修改表格表头单元格元素 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table th.el-table__cell {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  z-index: 9999 !important;
}

/* 直接修改表格单元格内容元素 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table .el-table__cell .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 直接修改表格表头单元格内容元素 - 覆盖深色主题 - 使用ID选择器 */
#app.dark-theme #unique-project-table th.el-table__cell .cell {
  background-color: transparent !important;
  color: #2d3748 !important;
  z-index: 9999 !important;
}

/* 强制修改表格所有元素的背景颜色 - 最高优先级 */
#unique-project-table,
#unique-project-table .el-table,
#unique-project-table .el-table__inner-wrapper,
#unique-project-table .el-table__header-wrapper,
#unique-project-table .el-table__body-wrapper,
#unique-project-table .el-table__footer-wrapper,
#unique-project-table .el-table__header,
#unique-project-table .el-table__body,
#unique-project-table .el-table__footer,
#unique-project-table th,
#unique-project-table td,
#unique-project-table .el-table__cell,
#unique-project-table th.el-table__cell,
#unique-project-table .el-table__cell .cell,
#unique-project-table th.el-table__cell .cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 强制修改表格表头的背景颜色 */
#unique-project-table th,
#unique-project-table th.el-table__cell,
#unique-project-table th.el-table__cell .cell {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  z-index: 9999 !important;
}

/* 强制修改表格行悬停效果 */
#unique-project-table tr:hover td,
#unique-project-table tr:hover .el-table__cell {
  background-color: #f0f9ff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 强制修改表格条纹行 */
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped td,
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped .el-table__cell {
  background-color: #f8fafc !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 强制修改表格条纹行悬停效果 */
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td,
#unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped:hover .el-table__cell {
  background-color: #f0f9ff !important;
  color: #4a5568 !important;
  z-index: 9999 !important;
}

/* 项目大厅表格视图专用背景颜色样式 */
.project-list-page #unique-project-table {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__inner-wrapper {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body-wrapper {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body tr {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body tr td {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body tr .el-table__cell {
  background-color: #ffffff !important;
}

.project-list-page #unique-project-table .el-table__body tr .el-table__cell .cell {
  background-color: transparent !important;
}

/* 条纹行样式 */
.project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped {
  background-color: #f8fafc !important;
}

.project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

.project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped .el-table__cell {
  background-color: #f8fafc !important;
}

/* 悬停效果 */
.project-list-page #unique-project-table tr:hover {
  background-color: #f0f9ff !important;
}

.project-list-page #unique-project-table tr:hover td {
  background-color: #f0f9ff !important;
}

.project-list-page #unique-project-table tr:hover .el-table__cell {
  background-color: #f0f9ff !important;
}

/* 表头样式 */
.project-list-page #unique-project-table th {
  background-color: #f7fafc !important;
}

.project-list-page #unique-project-table th.el-table__cell {
  background-color: #f7fafc !important;
}

.project-list-page #unique-project-table th.el-table__cell .cell {
  background-color: transparent !important;
}

/* ElementUI项目表格内部结构 */
#app.dark-theme .project-table .el-table,
#app.dark-theme .project-table .el-table__inner-wrapper,
#app.dark-theme .project-table .el-table__header-wrapper,
#app.dark-theme .project-table .el-table__body-wrapper,
#app.dark-theme .project-table .el-table__footer-wrapper,
#app.dark-theme .project-table .el-table__header,
#app.dark-theme .project-table .el-table__body,
#app.dark-theme .project-table .el-table__footer {
  background-color: #ffffff !important;
  background-image: none !important;
}

/* ElementUI唯一表格内部结构 */
#app.dark-theme .unique-project-table .el-table,
#app.dark-theme .unique-project-table .el-table__inner-wrapper,
#app.dark-theme .unique-project-table .el-table__header-wrapper,
#app.dark-theme .unique-project-table .el-table__body-wrapper,
#app.dark-theme .unique-project-table .el-table__footer-wrapper,
#app.dark-theme .unique-project-table .el-table__header,
#app.dark-theme .unique-project-table .el-table__body,
#app.dark-theme .unique-project-table .el-table__footer {
  background-color: #ffffff !important;
  background-image: none !important;
}

/* 项目表格表头 */
#app.dark-theme .project-table .el-table th,
#app.dark-theme .project-table .el-table__header-wrapper th,
#app.dark-theme .project-table .el-table__header th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
}

/* 唯一表格表头 */
#app.dark-theme .unique-project-table .el-table th,
#app.dark-theme .unique-project-table .el-table__header-wrapper th,
#app.dark-theme .unique-project-table .el-table__header th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
  font-weight: 600 !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 项目表格单元格 */
#app.dark-theme .project-table .el-table td,
#app.dark-theme .project-table .el-table__body-wrapper td,
#app.dark-theme .project-table .el-table__body td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
}

/* 唯一表格单元格 */
#app.dark-theme .unique-project-table .el-table td,
#app.dark-theme .unique-project-table .el-table__body-wrapper td,
#app.dark-theme .unique-project-table .el-table__body td,
#app.dark-theme .unique-project-table .el-table__cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  background-image: none !important;
  padding: 14px 16px !important;
  font-size: 14px !important;
  text-align: center !important;
}

/* 直接修改表格单元格元素 - 最高优先级 */
#app.dark-theme #unique-project-table .el-table__cell {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  background-image: none !important;
  border-bottom: 1px solid #e2e8f0 !important;
}

/* 直接修改表格单元格内容元素 */
#app.dark-theme #unique-project-table .el-table__cell .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
}

/* 强制覆盖深色主题表格样式 - 最高优先级 */
#app.dark-theme .project-list-page #unique-project-table,
#app.dark-theme .project-list-page #unique-project-table .el-table,
#app.dark-theme .project-list-page #unique-project-table .el-table__inner-wrapper,
#app.dark-theme .project-list-page #unique-project-table .el-table__body-wrapper,
#app.dark-theme .project-list-page #unique-project-table .el-table__body,
#app.dark-theme .project-list-page #unique-project-table tr,
#app.dark-theme .project-list-page #unique-project-table td,
#app.dark-theme .project-list-page #unique-project-table .el-table__cell,
#app.dark-theme .project-list-page [class*="el-table"] {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  background-image: none !important;
  border-bottom: 1px solid #e2e8f0 !important;
}

/* 强制覆盖深色主题表格表头 */
#app.dark-theme .project-list-page #unique-project-table th,
#app.dark-theme .project-list-page #unique-project-table th.el-table__cell,
#app.dark-theme .project-list-page [class*="el-table"] th {
  background-color: #f7fafc !important;
  color: #2d3748 !important;
  border-bottom: 2px solid #3b82f6 !important;
  border-top: 1px solid #e2e8f0 !important;
  background-image: none !important;
}

/* 强制覆盖深色主题条纹行样式 */
#app.dark-theme .project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped,
#app.dark-theme .project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped td,
#app.dark-theme .project-list-page #unique-project-table.el-table--striped .el-table__body tr.el-table__row--striped .el-table__cell {
  background-color: #f8fafc !important;
  color: #4a5568 !important;
  background-image: none !important;
}

/* 强制覆盖深色主题悬停样式 */
#app.dark-theme .project-list-page #unique-project-table tr:hover,
#app.dark-theme .project-list-page #unique-project-table tr:hover td,
#app.dark-theme .project-list-page #unique-project-table tr:hover .el-table__cell,
#app.dark-theme .project-list-page .el-table__body-wrapper tr:hover td {
  background-color: #f0f9ff !important;
  color: #4a5568 !important;
  background-image: none !important;
}

/* 强制覆盖深色主题表格单元格内容 */
#app.dark-theme .project-list-page #unique-project-table .el-table__cell .cell,
#app.dark-theme .project-list-page #unique-project-table th.el-table__cell .cell {
  background-color: transparent !important;
  color: #4a5568 !important;
}

/* 项目表格行悬停效果 */
#app.dark-theme .project-table .el-table tr:hover td,
#app.dark-theme .project-table .el-table__body-wrapper tr:hover td,
#app.dark-theme .project-table .el-table__body tr:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
  transition: background-color 0.3s ease;
}

/* 唯一表格行悬停效果 */
#app.dark-theme .unique-project-table .el-table tr:hover td,
#app.dark-theme .unique-project-table .el-table__body-wrapper tr:hover td,
#app.dark-theme .unique-project-table .el-table__body tr:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
  transition: background-color 0.3s ease;
}

/* 项目表格条纹行 */
#app.dark-theme .project-table .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
  background-image: none !important;
}

/* 唯一表格条纹行 */
#app.dark-theme .unique-project-table .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
  background-image: none !important;
}

/* 项目表格条纹行悬停效果 */
#app.dark-theme .project-table .el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
}

/* 唯一表格条纹行悬停效果 */
#app.dark-theme .unique-project-table .el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
  background-image: none !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .project-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
  }
  
  .project-card-img {
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
  
  .hw-input,
  .hw-select {
    min-width: 100%;
  }
  
  .form-item {
    width: 100%;
  }
  
  .project-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
  }
  
  .project-card-img {
    height: 220px;
  }
  
  .project-actions {
    flex-direction: column;
  }
  
  .quick-like-button {
    justify-content: center;
  }
  
  .hw-filter-bar {
    padding: 20px;
  }
  
  .project-card-body {
    padding: 20px;
  }
  
  .project-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .project-card-img {
    height: 180px;
  }
  
  .hw-filter-bar {
    padding: 16px;
  }
  
  .project-card-body {
    padding: 16px;
  }
}
</style>
