<template>
  <div class="admin-page">
    <div class="page-card">
      <div class="page-header">
        <h3 class="page-title">操作日志</h3>
        <span class="page-subtitle">查看平台用户操作记录和系统日志</span>
      </div>
      <el-form inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="操作内容/用户" clearable class="search-input" />
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" clearable placeholder="全部" class="filter-select">
            <el-option label="INFO" value="INFO" />
            <el-option label="WARN" value="WARN" />
            <el-option label="ERROR" value="ERROR" />
          </el-select>
        </el-form-item>
        <el-form-item label="模块">
          <el-select v-model="query.module" clearable placeholder="全部" class="filter-select">
            <el-option label="系统管理" value="system" />
            <el-option label="用户管理" value="user" />
            <el-option label="项目管理" value="project" />
            <el-option label="服务管理" value="service" />
            <el-option label="社区管理" value="community" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户">
          <el-input v-model="query.username" placeholder="用户名" clearable class="search-input" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            class="date-picker"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
          <el-button @click="doExport">导出 Excel</el-button>
        </el-form-item>
      </el-form>
      <el-table 
        v-loading="loading" 
        :data="list" 
        stripe 
        class="data-table"
        :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
        :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="timestamp" label="时间" width="180" sortable>
          <template slot-scope="scope">
            {{ scope.row.timestamp ? formatTime(scope.row.timestamp) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="90">
          <template slot-scope="scope">
            <el-tag 
              :type="scope.row.level === 'ERROR' ? 'danger' : scope.row.level === 'WARN' ? 'warning' : 'info'" 
              size="small" 
              effect="dark"
            >
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="action" label="操作" width="140" />
        <el-table-column prop="message" label="详情" min-width="240">
          <template slot-scope="scope">
            <span v-html="highlightKeyword(scope.row.message)"></span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="ip" label="IP" width="140" />
      </el-table>
      <div v-if="!loading && !list.length" class="data-empty">
        <div class="empty-icon"><i class="el-icon-document"></i></div>
        <p class="empty-text">暂无日志数据</p>
      </div>
      <el-pagination
        class="pagination"
        :current-page="page.current"
        :page-size="page.size"
        :total="page.total"
        layout="total, prev, pager, next"
        @current-change="p => { page.current = p; load() }"
      />
    </div>
  </div>
</template>

<script>
import { adminLog, exportLogs } from '@/api/admin'
export default {
  name: 'AdminLog',
  data() {
    return {
      loading: false,
      list: [],
      query: {
        level: '',
        keyword: '',
        module: '',
        username: ''
      },
      dateRange: [],
      sort: {
        prop: 'timestamp',
        order: 'desc'
      },
      page: { current: 1, size: 20, total: 0 }
    }
  },
  created() { this.load() },
  methods: {
    formatTime(t) {
      if (!t) return '-'
      if (typeof t === 'string') return t.length >= 19 ? t.substring(0, 19).replace('T', ' ') : t
      if (Array.isArray(t)) return t.slice(0, 3).join('-') + ' ' + (t[3] || 0) + ':' + (t[4] || 0)
      return String(t).substring(0, 19).replace('T', ' ')
    },
    async load() {
      this.loading = true
      try {
        const params = {
          current: this.page.current,
          size: this.page.size,
          level: this.query.level,
          keyword: this.query.keyword,
          module: this.query.module,
          username: this.query.username,
          sortField: this.sort.prop,
          sortOrder: this.sort.order
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await adminLog.list(params)
        this.list = (res && res.records) ? res.records : []
        this.page.total = (res && res.total) ? res.total : 0
      } finally {
        this.loading = false
      }
    },
    handleSortChange({ prop, order }) {
      this.sort.prop = prop
      this.sort.order = order
      this.load()
    },
    resetFilters() {
      this.query = {
        level: '',
        keyword: '',
        module: '',
        username: ''
      }
      this.dateRange = []
      this.page.current = 1
      this.load()
    },
    async doExport() {
      try {
        const blob = await exportLogs()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = '操作日志.xlsx'
        a.click()
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (e) {
        this.$message.error('导出失败')
      }
    },
    highlightKeyword(text) {
      if (!text || !this.query.keyword) {
        return text
      }
      const keyword = this.query.keyword
      const regex = new RegExp(keyword, 'gi')
      return text.replace(regex, match => `<span style="background-color: #3b82f6; color: white; padding: 2px 4px; border-radius: 4px;">${match}</span>`)
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.admin-page {
  padding: 20px;
  background: #0f172a;
  min-height: 100vh;
}

/* 页面卡片 */
.page-card {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #f8fafc;
  margin-bottom: 24px;
  overflow-x: auto;
  box-sizing: border-box;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
}

/* 搜索表单 */
.search-form {
  margin-bottom: 24px;
  padding: 20px;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

/* 表单样式 */
.el-form-item__label {
  color: #94a3b8;
  font-weight: 500;
}

.el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s ease;
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
  transition: all 0.2s ease;
}

.el-select-dropdown__item:hover {
  background: rgba(59, 130, 246, 0.1);
}

.el-select-dropdown__item.selected {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

/* 数据表格 */
.data-table {
  background: transparent !important;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
}

.data-table th {
  background: #334155 !important;
  color: #94a3b8 !important;
  font-weight: 600 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
}

.data-table td {
  background: #1e293b !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
  transition: all 0.2s ease;
}

.data-table tr:hover td {
  background: #334155 !important;
}

/* 空数据样式 */
.data-empty {
  text-align: center;
  padding: 40px 24px;
  color: #94a3b8;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 16px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
  color: #64748b;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: #94a3b8;
}

/* 标签样式 */
.el-tag {
  border-radius: 8px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
}

/* 分页样式 */
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.el-pagination__total {
  color: #94a3b8;
  margin-right: 16px;
}

.el-pager li {
  background: #334155;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #94a3b8;
  border-radius: 8px;
  margin: 0 4px;
  transition: all 0.2s ease;
}

.el-pager li:hover {
  background: #475569;
  color: #f8fafc;
  transform: translateY(-1px);
}

.el-pager li.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.el-pagination__prev,
.el-pagination__next {
  background: #334155;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #94a3b8;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.el-pagination__prev:hover,
.el-pagination__next:hover {
  background: #475569;
  color: #f8fafc;
  transform: translateY(-1px);
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
  transform: translateY(-1px);
}

.el-button {
  background: #334155;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.el-button:hover {
  background: #475569;
  border-color: rgba(59, 130, 246, 0.3);
  color: #f8fafc;
  transform: translateY(-1px);
}

/* 加载状态 */
.el-loading-mask {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
}

.el-loading-spinner .path {
  stroke: #3b82f6;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-page {
    padding: 16px;
  }
  
  .page-card {
    padding: 20px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .search-form {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 12px;
  }
  
  .page-card {
    padding: 16px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .el-form-item {
    margin-right: 0 !important;
    margin-bottom: 0 !important;
  }
  
  .el-form-item .el-select {
    width: 100% !important;
  }
  
  .pagination {
    justify-content: center;
  }
  
  .el-pagination__total {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .data-table th,
  .data-table td {
    padding: 12px !important;
  }
  
  .empty-icon {
    font-size: 48px;
  }
  
  .empty-text {
    font-size: 14px;
  }
}
</style>
