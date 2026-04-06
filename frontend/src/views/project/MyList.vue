<template>
  <div class="my-projects-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的项目</h1>
        <p class="page-subtitle">管理您发布的创业项目，跟踪项目状态和数据</p>
        <el-button type="primary" @click="$router.push('/project-publish')" class="primary-action-btn">
          <i class="el-icon-plus"></i>
          <span>发布新项目</span>
        </el-button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon project-icon">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalProjects }}</div>
          <div class="stat-label">总项目数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon online-icon">
          <i class="el-icon-check"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ onlineProjects }}</div>
          <div class="stat-label">已上线</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending-icon">
          <i class="el-icon-time"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ pendingProjects }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon like-icon">
          <i class="el-icon-star-on"></i>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalLikes }}</div>
          <div class="stat-label">总点赞数</div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-section">
      <div class="filter-header">
        <h3 class="filter-title">筛选与搜索</h3>
      </div>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width:140px" class="filter-select">
            <el-option label="待审核" value="pending" />
            <el-option label="已上线" value="online" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" @click="load" class="search-btn">
            <i class="el-icon-search"></i>
            <span>查询</span>
          </el-button>
          <el-button @click="resetFilters" class="reset-btn">
            <i class="el-icon-refresh"></i>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 项目列表 -->
    <div class="project-list-section">
      <div class="section-header">
        <h3 class="section-title">项目列表</h3>
        <span class="project-count">{{ displayList.length }} 个项目</span>
      </div>
      
      <div class="project-table-wrapper">
        <el-table 
          v-loading="loading" 
          :data="displayList" 
          border 
          stripe 
          class="project-table"
          :cell-style="{ backgroundColor: '#ffffff', color: '#4a5568' }"
          :header-cell-style="{ backgroundColor: '#3b82f6', color: 'white', textAlign: 'center' }"
          @sort-change="handleSortChange"
        >
          <el-table-column prop="title" label="项目名称" min-width="220" align="left" sortable="custom">
            <template slot-scope="scope">
              <div class="project-title-cell">
                <span class="project-title">{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small" class="status-tag">
                {{ statusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="likeCount" label="点赞" width="100" align="center" sortable="custom">
            <template slot-scope="scope">
              <div class="like-count">
                <i class="el-icon-star-on like-icon-small"></i>
                <span>{{ scope.row.likeCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewDetail(scope.row)"
                  class="action-btn detail-btn"
                >
                  详情
                </el-button>
                <el-button 
                  v-if="['pending','rejected'].includes(scope.row.status)" 
                  type="info" 
                  size="small" 
                  @click="edit(scope.row)"
                  class="action-btn edit-btn"
                >
                  编辑
                </el-button>
                <el-button 
                  v-if="scope.row.status === 'online'" 
                  type="warning" 
                  size="small" 
                  @click="viewPublic(scope.row)"
                  class="action-btn view-btn"
                >
                  查看
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空数据状态 -->
      <div v-if="!loading && !displayList.length" class="empty-state">
        <div class="empty-icon-large">
          <i class="el-icon-document"></i>
        </div>
        <h4 class="empty-title">暂无项目数据</h4>
        <p class="empty-description">您还没有发布任何项目，点击下方按钮开始发布</p>
        <el-button type="primary" @click="$router.push('/project-publish')" class="empty-action-btn">
          <i class="el-icon-plus"></i>
          <span>发布项目</span>
        </el-button>
      </div>

      <!-- 分页 -->
      <div v-if="page.total > 0" class="pagination-section">
        <el-pagination
          :current-page="page.current"
          :page-size="page.size"
          :page-sizes="[5, 10, 20, 50]"
          :total="page.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="onPage"
          @size-change="onSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { listMy } from '@/api/project'
const MOCK_LIST = [
  { id: 'm1', title: '智能硬件孵化项目', status: 'online', likeCount: 12 },
  { id: 'm2', title: '企业SaaS平台', status: 'pending', likeCount: 0 },
  { id: 'm3', title: '新能源出行方案', status: 'online', likeCount: 28 },
  { id: 'm4', title: '医疗健康数字化', status: 'rejected', likeCount: 0 },
  { id: 'm5', title: '教育科技产品', status: 'online', likeCount: 5 },
  { id: 'm6', title: '跨境电商服务', status: 'pending', likeCount: 0 },
  { id: 'm7', title: '本地生活O2O', status: 'online', likeCount: 19 },
  { id: 'm8', title: '金融科技风控', status: 'online', likeCount: 7 },
  { id: 'm9', title: 'AI内容生产工具', status: 'pending', likeCount: 0 },
  { id: 'm10', title: '供应链协同平台', status: 'online', likeCount: 3 }
]
export default {
  name: 'MyProjects',
  data() {
    return {
      loading: false,
      list: [],
      query: { status: '' },
      page: { current: 1, size: 10, total: 0 }
    }
  },
  computed: {
    displayList() {
      return this.list
    },
    totalProjects() {
      return this.list.length
    },
    onlineProjects() {
      return this.list.filter(item => item.status === 'online').length
    },
    pendingProjects() {
      return this.list.filter(item => item.status === 'pending').length
    },
    totalLikes() {
      return this.list.reduce((total, item) => total + (item.likeCount || 0), 0)
    }
  },
  created() {
    this.load()
  },
  methods: {
    statusText(s) {
      return { pending: '待审核', online: '已上线', rejected: '已拒绝' }[s] || s
    },
    getStatusType(status) {
      return status === 'online' ? 'success' : status === 'rejected' ? 'danger' : 'info'
    },
    async load() {
      this.loading = true
      try {
        const res = await listMy({ current: this.page.current, size: this.page.size, ...this.query })
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
    onSizeChange(size) {
      this.page.size = size
      this.page.current = 1
      this.load()
    },
    resetFilters() {
      this.query = { status: '' }
      this.load()
    },
    viewDetail(row) {
      if (row.id && String(row.id).indexOf('m') === 0) {
        this.$message.info('这是模拟数据，无法查看详细信息')
      } else {
        this.$router.push('/project/' + row.id)
      }
    },
    edit(row) {
      if (row.id && String(row.id).indexOf('m') === 0) {
        this.$message.info('这是模拟数据，无法编辑')
      } else {
        this.$router.push('/project/' + row.id + '?edit=1')
      }
    },
    viewPublic(row) {
      if (row.id && String(row.id).indexOf('m') === 0) {
        this.$message.info('这是模拟数据，无法查看公开页面')
      } else {
        this.$router.push('/project/' + row.id)
      }
    },
    handleSortChange({ prop, order }) {
      if (!prop || !order) {
        this.load()
        return
      }
      
      const sortedList = [...this.displayList].sort((a, b) => {
        if (prop === 'title') {
          const aVal = a[prop] || ''
          const bVal = b[prop] || ''
          return order === 'ascending' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
        } else if (prop === 'likeCount') {
          const aVal = a[prop] || 0
          const bVal = b[prop] || 0
          return order === 'ascending' ? aVal - bVal : bVal - aVal
        }
        return 0
      })
      
      this.list = sortedList
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.my-projects-page {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 16px;
  padding: 40px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-header:hover {
  box-shadow: 0 12px 48px rgba(59, 130, 246, 0.15);
  transform: translateY(-2px);
}

.header-content {
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 24px 0;
  line-height: 1.4;
}

.primary-action-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.primary-action-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}

/* 数据概览卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

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
  border-color: rgba(59, 130, 246, 0.2);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.project-icon {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  color: #3b82f6;
}

.online-icon {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #10b981;
}

.pending-icon {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #f59e0b;
}

.like-icon {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #ef4444;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 筛选栏 */
.filter-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.filter-header {
  margin-bottom: 20px;
}

.filter-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.filter-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-select {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.filter-select:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-actions {
  margin-left: auto;
  display: flex;
  gap: 12px;
}

.search-btn {
  background: #3b82f6;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.reset-btn {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 20px;
  color: #64748b;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f0f9ff;
}

/* 项目列表区域 */
.project-list-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.project-count {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  background: #f8fafc;
  padding: 6px 12px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

/* 表格样式 */
.project-table-wrapper {
  margin-bottom: 32px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.project-table {
  border-radius: 12px;
  overflow: hidden;
}

.project-table th {
  background-color: #3b82f6 !important;
  color: white !important;
  border-bottom: 2px solid #2563eb !important;
  border-top: 1px solid #3b82f6 !important;
  padding: 16px !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  text-align: center !important;
}

.project-table td {
  background-color: #ffffff !important;
  color: #4a5568 !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 16px !important;
  font-size: 14px !important;
}

.project-table tr:hover td {
  background-color: #f0f9ff !important;
  transition: background-color 0.3s ease;
}

.project-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #f8fafc !important;
}

.project-table.el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #f0f9ff !important;
}

/* 项目标题 */
.project-title-cell {
  display: flex;
  align-items: center;
}

.project-title {
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.project-title:hover {
  color: #3b82f6;
}

/* 状态标签 */
.status-tag {
  border-radius: 12px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 点赞计数 */
.like-count {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
}

.like-icon-small {
  color: #ef4444;
  font-size: 14px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.detail-btn {
  background: #3b82f6;
  border: none;
}

.detail-btn:hover {
  background: #2563eb;
}

.edit-btn {
  background: #ffffff;
  border: 1px solid #3b82f6;
  color: #3b82f6;
}

.edit-btn:hover {
  background: #f0f9ff;
}

.view-btn {
  background: #fbbf24;
  border: none;
  color: #ffffff;
}

.view-btn:hover {
  background: #f59e0b;
}

/* 空数据状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #f8fafc;
  border-radius: 16px;
  border: 2px dashed #e2e8f0;
  margin: 24px 0;
}

.empty-icon-large {
  font-size: 64px;
  color: #94a3b8;
  margin-bottom: 24px;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.empty-description {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 32px 0;
  line-height: 1.4;
}

.empty-action-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.empty-action-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.el-pagination {
  margin: 0;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .my-projects-page {
    padding: 24px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .page-header {
    padding: 32px;
  }
  
  .page-title {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .my-projects-page {
    padding: 16px;
  }
  
  .page-header {
    padding: 24px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .filter-section {
    padding: 20px;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-actions {
    margin-left: 0;
    justify-content: center;
  }
  
  .project-list-section {
    padding: 24px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .project-table th,
  .project-table td {
    padding: 12px !important;
    font-size: 13px !important;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .action-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
  
  .empty-state {
    padding: 60px 16px;
  }
  
  .empty-icon-large {
    font-size: 48px;
  }
  
  .empty-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    padding: 20px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .primary-action-btn {
    padding: 10px 20px;
    font-size: 14px;
  }
  
  .project-list-section {
    padding: 16px;
  }
  
  .filter-section {
    padding: 16px;
  }
  
  .empty-state {
    padding: 40px 12px;
  }
}
</style>
