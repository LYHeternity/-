<template>
  <div class="admin-page dark-theme">
    <div class="page-header-section">
      <div class="page-header-content">
        <h1 class="page-title">项目管理</h1>
        <p class="page-subtitle">管理已发布的项目，包括重新提交审核</p>
      </div>
      <div class="page-header-actions">
        <el-button type="primary" icon="el-icon-refresh" @click="loadProjects" class="primary-action-button">
          刷新数据
        </el-button>
      </div>
    </div>

    <div class="project-management-section">
      <div class="section-header">
        <h2 class="section-title">项目列表</h2>
        <div class="section-actions">
          <el-button type="primary" icon="el-icon-download" @click="doExport" class="action-button">
            导出项目
          </el-button>
        </div>
      </div>

      <!-- 搜索筛选区域 -->
      <div class="search-filter-section">
        <el-form inline class="search-form">
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="项目名称/行业" clearable class="search-input" />
          </el-form-item>
          <el-form-item label="行业">
            <el-select v-model="query.industry" clearable placeholder="全部" class="filter-select">
              <el-option label="互联网" value="internet" />
              <el-option label="人工智能" value="ai" />
              <el-option label="医疗健康" value="healthcare" />
              <el-option label="教育培训" value="education" />
              <el-option label="金融科技" value="fintech" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" class="filter-select">
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="online" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadProjects" class="search-button">
              <i class="el-icon-search"></i> 查询
            </el-button>
            <el-button @click="resetFilters" class="reset-button">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 项目表格 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="projectList"
          style="width: 100%"
          class="project-table"
          stripe
          :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
          :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="项目名称" min-width="200">
            <template slot-scope="scope">
              <div class="project-title">
                <a href="javascript:void(0)" @click="viewProjectDetail(scope.row.id)" class="title-link">{{ scope.row.title }}</a>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="industry" label="行业" width="120">
            <template slot-scope="scope">
              <el-tag :class="getIndustryTagClass(scope.row.industry)" effect="light">
                {{ scope.row.industry }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template slot-scope="scope">
              <el-tag :class="getStatusTagClass(scope.row.status)" effect="light">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="180">
            <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column prop="submitter" label="提交人" width="120">
            <template slot-scope="scope">{{ scope.row.submitter || '未知' }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="300" align="right">
            <template slot-scope="scope">
              <div class="project-actions">
                <el-button type="primary" size="small" @click="viewProjectDetail(scope.row.id)" class="action-button primary">
                  <i class="el-icon-view"></i> 查看
                </el-button>
                <el-button type="info" size="small" @click="editProject(scope.row.id)" class="action-button">
                  <i class="el-icon-edit"></i> 编辑
                </el-button>
                <el-button 
                  :type="scope.row.status !== 'pending' ? 'warning' : 'default'" 
                  size="small" 
                  @click="resubmitAudit(scope.row.id)"
                  class="action-button"
                >
                  <i class="el-icon-refresh"></i> 重新审核
                </el-button>
                <el-button type="danger" size="small" @click="deleteProject(scope.row.id)" class="action-button">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空数据状态 -->
        <div v-if="!loading && !projectList.length" class="empty-state">
          <div class="empty-icon-container">
            <i class="el-icon-document"></i>
          </div>
          <h3 class="empty-title">暂无项目数据</h3>
          <p class="empty-description">当前筛选条件下没有找到项目信息</p>
          <el-button type="primary" @click="resetFilters" class="empty-action-button">
            重置筛选条件
          </el-button>
        </div>
      </div>

      <!-- 分页控件 -->
      <div class="pagination-section">
        <div class="pagination-info">
          共 {{ page.total }} 条记录，当前第 {{ page.current }} 页
        </div>
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination-controls"
        />
      </div>
    </div>

    <!-- 项目详情弹窗 -->
    <el-dialog 
      title="项目详情" 
      :visible.sync="showDetailDialog" 
      width="800px"
      class="custom-dialog"
      :modal="false"
    >
      <div class="project-detail">
        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="项目名称">{{ currentProject.title }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ currentProject.industry }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusLabel(currentProject.status) }}</el-descriptions-item>
          <el-descriptions-item label="审核意见" v-if="currentProject.auditMessage">{{ currentProject.auditMessage }}</el-descriptions-item>
          <el-descriptions-item label="项目描述">{{ currentProject.description }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatTime(currentProject.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentProject.submitter }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 项目编辑弹窗 -->
    <el-dialog 
      title="编辑项目" 
      :visible.sync="showEditDialog" 
      width="800px"
      class="custom-dialog"
      :modal="false"
    >
      <div class="project-edit">
        <el-form ref="editForm" :model="editForm" label-width="120px">
          <el-form-item label="项目名称">
            <el-input v-model="editForm.title" placeholder="请输入项目名称" />
          </el-form-item>
          <el-form-item label="项目副标题">
            <el-input v-model="editForm.subtitle" placeholder="请输入项目副标题" />
          </el-form-item>
          <el-form-item label="所属行业">
            <el-select v-model="editForm.industry" placeholder="请选择行业" class="form-select">
              <el-option label="互联网" value="互联网" />
              <el-option label="人工智能" value="人工智能" />
              <el-option label="医疗健康" value="医疗健康" />
              <el-option label="教育培训" value="教育培训" />
              <el-option label="金融科技" value="金融科技" />
              <el-option label="消费零售" value="消费零售" />
              <el-option label="新能源" value="新能源" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="项目阶段">
            <el-select v-model="editForm.stage" placeholder="请选择项目阶段">
              <el-option label="想法阶段" value="idea" />
              <el-option label="种子期" value="seed" />
              <el-option label="天使轮" value="angel" />
              <el-option label="A轮" value="a-round" />
              <el-option label="B轮及以上" value="b-round-plus" />
            </el-select>
          </el-form-item>
          <el-form-item label="项目简介">
            <el-input v-model="editForm.summary" type="textarea" :rows="3" placeholder="请输入项目简介" />
          </el-form-item>
          <el-form-item label="项目描述">
            <el-input v-model="editForm.description" type="textarea" :rows="5" placeholder="请输入项目描述" />
          </el-form-item>
          <el-form-item label="融资需求(万元)">
            <el-input-number v-model="editForm.fundingNeeded" :min="0" :max="99999" />
          </el-form-item>
          <el-form-item label="出让股权(%)">
            <el-input-number v-model="editForm.equityOffered" :min="0" :max="100" />
          </el-form-item>
          <el-form-item label="创始人姓名">
            <el-input v-model="editForm.founderName" placeholder="请输入创始人姓名" />
          </el-form-item>
          <el-form-item label="创始人职位">
            <el-input v-model="editForm.founderTitle" placeholder="请输入创始人职位" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" :loading="editLoading" @click="saveProjectEdit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminProject } from '@/api/admin'
import { getDetail } from '@/api/project'
export default {
  name: 'AdminProjectManage',
  data() {
    return {
      loading: false,
      projectList: [],
      page: {
        current: 1,
        size: 10,
        total: 0
      },
      query: {
        keyword: '',
        industry: '',
        status: ''
      },
      showDetailDialog: false,
      currentProject: {},
      showEditDialog: false,
      editForm: {},
      editLoading: false
    }
  },
  created() {
    this.loadProjects()
  },
  methods: {
    formatTime(t) {
      if (!t) return '-'
      const s = typeof t === 'string' ? t : (t + '').substring(0, 19)
      return s.replace('T', ' ')
    },
    async loadProjects() {
      this.loading = true
      try {
        const res = await adminProject.list({
          current: this.page.current,
          size: this.page.size,
          keyword: this.query.keyword,
          industry: this.query.industry,
          status: this.query.status
        })
        this.projectList = (res && res.records) ? res.records : []
        this.page.total = res.total || 0
      } catch (error) {
        console.error('Load projects error:', error)
        this.$message.error('加载项目列表失败')
        // 模拟数据
        this.projectList = [
          {
            id: 1,
            title: '人工智能医疗诊断系统',
            industry: '人工智能',
            status: 'online',
            description: '基于深度学习的医疗影像诊断系统，可辅助医生进行疾病诊断',
            createTime: '2024-02-25T10:30:00',
            submitter: '张三'
          },
          {
            id: 2,
            title: '在线教育平台',
            industry: '教育培训',
            status: 'rejected',
            auditMessage: '项目描述不够详细，缺少具体的技术实现方案',
            description: '面向K12学生的在线教育平台，提供个性化学习方案',
            createTime: '2024-02-25T09:15:00',
            submitter: '李四'
          },
          {
            id: 3,
            title: '金融科技风险控制系统',
            industry: '金融科技',
            status: 'pending',
            description: '基于大数据和机器学习的金融风险控制系统',
            createTime: '2024-02-24T16:45:00',
            submitter: '王五'
          }
        ]
        this.page.total = this.projectList.length
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(size) {
      this.page.size = size
      this.loadProjects()
    },
    handleCurrentChange(current) {
      this.page.current = current
      this.loadProjects()
    },
    resetFilters() {
      this.query = { keyword: '', industry: '', status: '' }
      this.page.current = 1
      this.loadProjects()
    },
    async doExport() {
      try {
        // 准备导出数据
        const exportData = this.projectList.map(project => {
          return {
            id: project.id,
            title: project.title,
            industry: project.industry,
            status: this.getStatusLabel(project.status),
            description: project.description,
            createTime: this.formatTime(project.createTime),
            submitter: project.submitter || '未知',
            auditMessage: project.auditMessage || ''
          }
        })
        
        // 转换为CSV格式
        const headers = ['ID', '项目名称', '行业', '状态', '项目描述', '提交时间', '提交人', '审核意见']
        const csvContent = [
          headers.join(','),
          ...exportData.map(row => Object.values(row).join(','))
        ].join('\n')
        
        // 创建下载链接
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.setAttribute('href', url)
        link.setAttribute('download', `项目导出_${new Date().getTime()}.csv`)
        link.style.visibility = 'hidden'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        
        this.$message.success('导出成功')
      } catch (error) {
        console.error('Export error:', error)
        this.$message.error('导出失败')
      }
    },
    viewProjectDetail(id) {
      const project = this.projectList.find(p => p.id === id)
      if (project) {
        this.currentProject = project
        this.showDetailDialog = true
      }
    },
    async editProject(id) {
      try {
        this.editLoading = true
        const project = await getDetail(id)
        this.editForm = { ...project }
        this.showEditDialog = true
      } catch (error) {
        console.error('加载项目详情失败:', error)
        this.$message.error('加载项目详情失败')
      } finally {
        this.editLoading = false
      }
    },
    async saveProjectEdit() {
      this.editLoading = true
      try {
        await adminProject.update(this.editForm.id, this.editForm)
        this.$message.success('项目更新成功')
        this.showEditDialog = false
        this.loadProjects()
      } catch (error) {
        console.error('更新项目失败:', error)
        this.$message.error('更新项目失败')
      } finally {
        this.editLoading = false
      }
    },
    async resubmitAudit(id) {
      try {
        await adminProject.resubmitAudit(id)
        this.$message.success('重新提交审核成功')
        this.loadProjects()
      } catch (error) {
        console.error('Resubmit audit error:', error)
        this.$message.error('重新提交审核失败')
      }
    },
    async deleteProject(id) {
      try {
        await this.$confirm('确定删除该项目？此操作不可恢复', '警告', { type: 'danger' })
        await adminProject.delete(id)
        this.$message.success('删除项目成功')
        this.loadProjects()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Delete project error:', error)
          this.$message.error('删除项目失败')
        }
      }
    },
    getIndustryTagClass(industry) {
      const classMap = {
        '人工智能': 'tag-ai',
        '互联网': 'tag-internet',
        '医疗健康': 'tag-healthcare',
        '教育培训': 'tag-education',
        '金融科技': 'tag-fintech',
        '其他': 'tag-default'
      }
      return classMap[industry] || 'tag-default'
    },
    getStatusTagClass(status) {
      const classMap = {
        'pending': 'tag-status-pending',
        'online': 'tag-status-active',
        'rejected': 'tag-status-inactive'
      }
      return classMap[status] || 'tag-default'
    },
    getStatusLabel(status) {
      const labelMap = {
        'pending': '待审核',
        'online': '已通过',
        'rejected': '已拒绝'
      }
      return labelMap[status] || status
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
  color: #f8fafc;
}

/* 页面头部区域 */
.page-header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.page-header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 8px;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #94a3b8;
  font-weight: 500;
  margin: 0;
}

.page-header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.primary-action-button {
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.primary-action-button:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

/* 项目管理区域 */
.project-management-section {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.08);
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
  color: #f8fafc;
  margin: 0;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-button {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.action-button.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
}

.action-button.primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

/* 搜索筛选区域 */
.search-filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 240px;
  border-radius: 8px;
}

.filter-select {
  width: 160px;
  border-radius: 8px;
}

.search-button {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  border-radius: 8px;
  padding: 8px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-button:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.reset-button {
  background: #475569;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  border-radius: 8px;
  padding: 8px 20px;
  transition: all 0.3s ease;
}

.reset-button:hover {
  background: #64748b;
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

/* 表格容器 */
.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  border-radius: 12px;
  background: #1e293b;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-sizing: border-box;
}

/* 表格悬停效果 - 使用更具特异性的选择器 */
.admin-page .project-table tr:hover td,
.admin-page .project-table tr:hover .el-table__cell {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 确保表格悬停效果在所有情况下都生效 */
.admin-page .el-table__row:hover td,
.admin-page .el-table__row:hover .el-table__cell {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 确保在深色主题下悬停效果正确 */
.admin-page.dark-theme .el-table__row:hover td,
.admin-page.dark-theme .el-table__row:hover .el-table__cell {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 增强表格悬停效果，覆盖 Element UI 默认样式 */
.admin-page .el-table--enable-row-hover .el-table__body tr:hover>td,
.admin-page .el-table--enable-row-hover .el-table__body tr:hover>td .el-table__cell {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 确保表格行在所有状态下的悬停效果 */
.admin-page .el-table__row.hover-row td,
.admin-page .el-table__row.hover-row .el-table__cell {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 覆盖全局表格悬停样式 */
.admin-page .el-table .el-table__row:hover td {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 覆盖深色主题下的全局表格悬停样式 */
.admin-page.dark-theme .el-table tr:hover td,
.admin-page.dark-theme .el-table__body-wrapper tr:hover td {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 项目标题链接 */
.project-title {
  line-height: 1.4;
}

.title-link {
  color: #3b82f6 !important;
  text-decoration: none !important;
  font-weight: 500 !important;
  font-size: 14px !important;
  transition: all 0.2s ease !important;
  display: inline-block !important;
  max-width: 100% !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}

.title-link:hover {
  color: #60a5fa !important;
  text-decoration: underline !important;
}

/* 项目操作按钮组 */
.project-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
  min-height: 80px;
  padding: 8px 0;
  width: 100%;
  box-sizing: border-box;
}

/* 空数据状态 */
.empty-state {
  text-align: center;
  padding: 60px 24px;
  color: #94a3b8;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.empty-icon-container {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
  color: #64748b;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #94a3b8;
  margin: 0 0 8px 0;
}

.empty-description {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 24px 0;
}

.empty-action-button {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  transition: all 0.3s ease;
}

.empty-action-button:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.pagination-info {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 下拉框样式 */
.dark-theme .el-select-dropdown {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 8px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
  z-index: 9999 !important;
}

.dark-theme :deep(.el-popper) {
  z-index: 9999 !important;
}

/* 弹窗样式 */
.custom-dialog {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

/* 确保下拉框层级高于弹窗 */
.dark-theme .el-select-dropdown {
  z-index: 9999 !important;
}

.dark-theme .el-date-picker {
  z-index: 9999 !important;
}

.dark-theme .el-dropdown-menu {
  z-index: 9999 !important;
}

.custom-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 24px;
}

.custom-dialog .el-dialog__title {
  color: #f8fafc;
  font-size: 18px;
  font-weight: 600;
}

.custom-dialog .el-dialog__body {
  padding: 24px;
  color: #f8fafc;
}

.custom-dialog .el-dialog__footer {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px 24px;
  background: rgba(15, 23, 42, 0.5);
}

/* 项目详情样式 */
.project-detail {
  padding: 16px;
}

.detail-descriptions {
  background: #334155;
  border-radius: 8px;
  overflow: hidden;
}

.detail-descriptions .el-descriptions__header {
  background: #1e293b;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.detail-descriptions .el-descriptions__body {
  padding: 16px;
}

.detail-descriptions .el-descriptions__item {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 12px 16px;
}

.detail-descriptions .el-descriptions__label {
  color: #94a3b8;
  font-weight: 500;
  width: 120px;
}

.detail-descriptions .el-descriptions__content {
  color: #f8fafc;
  font-weight: 400;
}

/* 标签样式 */
.el-tag {
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 60px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: clip;
}

.tag-ai {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.3);
}

.tag-internet {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.3);
}

.tag-healthcare {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  border-color: rgba(245, 158, 11, 0.3);
}

.tag-education {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.3);
}

.tag-fintech {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
  border-color: rgba(139, 92, 246, 0.3);
}

.tag-status-pending {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  border-color: rgba(245, 158, 11, 0.3);
}

.tag-status-active {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.3);
}

.tag-status-inactive {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.3);
}

.tag-default {
  background: rgba(100, 116, 139, 0.1);
  color: #64748b;
  border-color: rgba(100, 116, 139, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-title {
    font-size: 28px;
  }
  
  .search-input {
    width: 200px;
  }
  
  .filter-select {
    width: 140px;
  }
}

@media (max-width: 1024px) {
  .admin-page {
    padding: 16px;
  }
  
  .page-header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-header-actions {
    align-self: flex-end;
  }
  
  .project-management-section {
    padding: 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .section-actions {
    align-self: flex-end;
  }
  
  .search-form {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .page-header-actions {
    align-self: stretch;
    justify-content: center;
  }
  
  .primary-action-button {
    width: 100%;
    justify-content: center;
  }
  
  .section-actions {
    align-self: stretch;
    justify-content: center;
  }
  
  .action-button {
    width: 100%;
    justify-content: center;
  }
  
  .search-input {
    width: 100%;
    max-width: 300px;
  }
  
  .filter-select {
    width: 100%;
    max-width: 150px;
  }
  
  .project-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
  }
  
  .project-actions .el-button {
    width: 100%;
    justify-content: center;
  }
  
  .pagination-section {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }
  
  .pagination-info {
    order: 2;
  }
  
  .pagination-controls {
    order: 1;
  }
}
</style>