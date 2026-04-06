<template>
  <div class="admin-page dark-theme">
    <div class="page-header-section">
      <div class="page-header-content">
        <h1 class="page-title">内容审核</h1>
        <p class="page-subtitle">审核待通过的项目和服务</p>
      </div>
      <div class="page-header-actions">
        <el-button type="primary" icon="el-icon-refresh" @click="refreshData" class="primary-action-button">
          刷新数据
        </el-button>
      </div>
    </div>

    <div class="audit-management-section">
      <div class="section-header">
        <h2 class="section-title">审核管理</h2>
        <div class="section-actions">
          <el-button type="primary" icon="el-icon-download" @click="doExport" class="action-button">
            导出审核记录
          </el-button>
        </div>
      </div>

      <!-- 审核概览卡片 -->
      <div class="audit-stats-grid">
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">待审核项目</h3>
            <span class="stat-icon pending"><i class="el-icon-document"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ pendingProjects }}</div>
            <div class="stat-change warning">+{{ pendingProjects }} <span>个</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">待审核服务</h3>
            <span class="stat-icon pending"><i class="el-icon-goods"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ pendingResources }}</div>
            <div class="stat-change warning">+{{ pendingResources }} <span>个</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">今日通过</h3>
            <span class="stat-icon active"><i class="el-icon-check"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ todayApproved }}</div>
            <div class="stat-change positive">+{{ todayApproved }} <span>个</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">今日拒绝</h3>
            <span class="stat-icon disabled"><i class="el-icon-close"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ todayRejected }}</div>
            <div class="stat-change negative">{{ todayRejected }} <span>个</span></div>
          </div>
        </div>
      </div>

      <!-- 审核标签页 -->
      <el-tabs v-model="activeTab" class="audit-tabs">
        <el-tab-pane label="待审核项目" name="project">
          <div class="tab-content">
            <!-- 搜索筛选区域 -->
            <div class="search-filter-section">
              <el-form inline class="search-form">
                <el-form-item label="关键词">
                  <el-input v-model="projectQuery.keyword" placeholder="项目名称/行业" clearable class="search-input" />
                </el-form-item>
                <el-form-item label="行业">
                  <el-select v-model="projectQuery.industry" clearable placeholder="全部" class="filter-select">
                    <el-option label="互联网" value="internet" />
                    <el-option label="人工智能" value="ai" />
                    <el-option label="医疗健康" value="healthcare" />
                    <el-option label="教育培训" value="education" />
                    <el-option label="金融科技" value="fintech" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadProjects" class="search-button">
                    <i class="el-icon-search"></i> 查询
                  </el-button>
                  <el-button @click="resetProjectFilters" class="reset-button">
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
                style="width: 100%;"
                class="project-table"
                stripe
                :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
                :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
              >
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
                <el-table-column prop="createTime" label="提交时间" width="180">
                  <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
                </el-table-column>
                <el-table-column prop="submitter" label="提交人" width="120">
                  <template slot-scope="scope">{{ scope.row.submitter || '未知' }}</template>
                </el-table-column>
                <el-table-column label="操作" min-width="300" align="right">
                  <template slot-scope="scope">
                    <div class="audit-actions">
                      <el-button type="primary" size="small" @click="auditProject(scope.row.id, 'online')" class="action-button primary">
                        <i class="el-icon-check"></i> 通过
                      </el-button>
                      <el-button type="danger" size="small" @click="auditProject(scope.row.id, 'rejected')" class="action-button">
                        <i class="el-icon-close"></i> 拒绝
                      </el-button>
                      <el-button size="small" @click="viewProjectDetail(scope.row.id)" class="action-button">
                        <i class="el-icon-view"></i> 查看
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
                <h3 class="empty-title">暂无待审核项目</h3>
                <p class="empty-description">当前筛选条件下没有找到待审核项目</p>
                <el-button type="primary" @click="resetProjectFilters" class="empty-action-button">
                  重置筛选条件
                </el-button>
              </div>
            </div>

            <!-- 分页控件 -->
            <div class="pagination-section">
              <div class="pagination-info">
                共 {{ projectPage.total }} 条记录，当前第 {{ projectPage.current }} 页
              </div>
              <el-pagination
                v-model:current-page="projectPage.current"
                v-model:page-size="projectPage.size"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="projectPage.total"
                @size-change="handleProjectSizeChange"
                @current-change="handleProjectCurrentChange"
                class="pagination-controls"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="待审核服务" name="resource">
          <div class="tab-content">
            <!-- 搜索筛选区域 -->
            <div class="search-filter-section">
              <el-form inline class="search-form">
                <el-form-item label="关键词">
                  <el-input v-model="resourceQuery.keyword" placeholder="服务名称/分类" clearable class="search-input" />
                </el-form-item>
                <el-form-item label="分类">
                  <el-select v-model="resourceQuery.category" clearable placeholder="全部" class="filter-select">
                    <el-option label="技术服务" value="tech" />
                    <el-option label="法律咨询" value="legal" />
                    <el-option label="财务服务" value="finance" />
                    <el-option label="市场营销" value="marketing" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadResources" class="search-button">
                    <i class="el-icon-search"></i> 查询
                  </el-button>
                  <el-button @click="resetResourceFilters" class="reset-button">
                    <i class="el-icon-refresh"></i> 重置
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <!-- 服务表格 -->
            <div class="table-container">
              <el-table
                v-loading="loading"
                :data="resourceList"
                style="width: 100%;"
                class="resource-table"
                stripe
                :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
                :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
              >
                <el-table-column prop="title" label="服务名称" min-width="200">
                  <template slot-scope="scope">
                    <div class="resource-title">
                      <a href="javascript:void(0)" @click="viewResourceDetail(scope.row.id)" class="title-link">{{ scope.row.title }}</a>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="category" label="分类" width="120">
                  <template slot-scope="scope">
                    <el-tag :class="getCategoryTagClass(scope.row.category)" effect="light">
                      {{ scope.row.category }}
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
                    <div class="audit-actions">
                      <el-button type="primary" size="small" @click="auditResource(scope.row.id, 'online')" class="action-button primary">
                        <i class="el-icon-check"></i> 通过
                      </el-button>
                      <el-button type="danger" size="small" @click="auditResource(scope.row.id, 'rejected')" class="action-button">
                        <i class="el-icon-close"></i> 拒绝
                      </el-button>
                      <el-button size="small" @click="viewResourceDetail(scope.row.id)" class="action-button">
                        <i class="el-icon-view"></i> 查看
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 空数据状态 -->
              <div v-if="!loading && !resourceList.length" class="empty-state">
                <div class="empty-icon-container">
                  <i class="el-icon-goods"></i>
                </div>
                <h3 class="empty-title">暂无待审核服务</h3>
                <p class="empty-description">当前筛选条件下没有找到待审核服务</p>
                <el-button type="primary" @click="resetResourceFilters" class="empty-action-button">
                  重置筛选条件
                </el-button>
              </div>
            </div>

            <!-- 分页控件 -->
            <div class="pagination-section">
              <div class="pagination-info">
                共 {{ resourcePage.total }} 条记录，当前第 {{ resourcePage.current }} 页
              </div>
              <el-pagination
                v-model:current-page="resourcePage.current"
                v-model:page-size="resourcePage.size"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="resourcePage.total"
                @size-change="handleResourceSizeChange"
                @current-change="handleResourceCurrentChange"
                class="pagination-controls"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 审核详情弹窗 -->
    <!-- 审核详情弹窗 -->
    <el-dialog 
      :title="auditDialogTitle" 
      :visible.sync="showAuditDialog" 
      width="800px"
      class="custom-dialog"
      :modal="false"
      :z-index="1000"
    >
      <div v-if="activeTab === 'project'" class="audit-detail">
        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="项目名称">{{ currentAuditItem.title }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ currentAuditItem.industry }}</el-descriptions-item>
          <el-descriptions-item label="项目描述">{{ currentAuditItem.description }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatTime(currentAuditItem.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentAuditItem.submitter }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else class="audit-detail">
        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="服务名称">{{ currentAuditItem.title }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentAuditItem.category }}</el-descriptions-item>
          <el-descriptions-item label="服务描述">{{ currentAuditItem.description }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatTime(currentAuditItem.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentAuditItem.submitter }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAuditDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核不通过弹窗 -->
    <el-dialog 
      title="审核不通过" 
      :visible.sync="showRejectDialog" 
      width="600px"
      class="custom-dialog"
      :modal="false"
      :z-index="1001"
    >
      <div class="reject-form">
        <el-form :model="rejectForm" label-width="80px">
          <el-form-item label="不通过原因" required>
            <el-input 
              v-model="rejectForm.reason" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入具体的不通过原因" 
              class="reject-reason-input"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRejectDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmReject" :loading="rejectLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminAudit } from '@/api/admin'
import * as messageApi from '@/api/message'
export default {
  name: 'AdminAudit',
  data() {
    return {
      activeTab: 'project',
      loading: false,
      rejectLoading: false,
      projectList: [],
      resourceList: [],
      projectPage: {
        current: 1,
        size: 10,
        total: 0
      },
      resourcePage: {
        current: 1,
        size: 10,
        total: 0
      },
      projectQuery: {
        keyword: '',
        industry: ''
      },
      resourceQuery: {
        keyword: '',
        category: ''
      },
      showAuditDialog: false,
      showRejectDialog: false,
      currentAuditItem: {},
      auditDialogTitle: '',
      rejectForm: {
        reason: '',
        itemId: null,
        itemType: ''
      },
      // 统计数据
      pendingProjects: 5,
      pendingResources: 3,
      todayApproved: 8,
      todayRejected: 2
    }
  },
  created() {
    this.loadProjects()
    this.loadResources()
  },
  mounted() {
    // 动态修改表格样式，确保使用深色主题
    this.$nextTick(() => {
      this.updateTableStyles()
    })
  },
  updated() {
    // 当表格数据更新时，重新应用样式
    this.$nextTick(() => {
      this.updateTableStyles()
    })
  },
  methods: {
    updateTableStyles() {
      // 获取所有表格相关元素并设置深色背景
      const tables = document.querySelectorAll('.el-table, .el-table__header-wrapper, .el-table__body-wrapper, .el-table__footer-wrapper')
      tables.forEach(table => {
        table.style.backgroundColor = '#1e293b'
      })
      
      // 设置表格头部样式
      const tableHeaders = document.querySelectorAll('.el-table th, .el-table__header-wrapper th')
      tableHeaders.forEach(header => {
        header.style.backgroundColor = '#334155'
        header.style.color = '#94a3b8'
        header.style.borderBottom = '1px solid rgba(255, 255, 255, 0.08)'
      })
      
      // 设置表格单元格样式
      const tableCells = document.querySelectorAll('.el-table td, .el-table__body-wrapper td')
      tableCells.forEach(cell => {
        cell.style.backgroundColor = '#1e293b'
        cell.style.color = '#f8fafc'
        cell.style.borderBottom = '1px solid rgba(255, 255, 255, 0.08)'
      })
    },
    formatTime(t) {
      if (!t) return '-'
      const s = typeof t === 'string' ? t : (t + '').substring(0, 19)
      return s.replace('T', ' ')
    },
    async loadProjects() {
      this.loading = true
      try {
        // 实际API请求
        const res = await adminAudit.projectList({ 
          current: this.projectPage.current, 
          size: this.projectPage.size,
          keyword: this.projectQuery.keyword,
          industry: this.projectQuery.industry
        })
        this.projectList = (res && res.records) ? res.records : []
        this.projectPage.total = res.total || 0
      } catch (error) {
        console.error('Load projects error:', error)
        this.$message.error('加载项目列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadResources() {
      this.loading = true
      try {
        // 实际API请求
        const res = await adminAudit.resourceList({ 
          current: this.resourcePage.current, 
          size: this.resourcePage.size,
          keyword: this.resourceQuery.keyword,
          category: this.resourceQuery.category
        })
        this.resourceList = (res && res.records) ? res.records : []
        this.resourcePage.total = res.total || 0
      } catch (error) {
        console.error('Load resources error:', error)
        this.$message.error('加载服务列表失败')
      } finally {
        this.loading = false
      }
    },
    handleProjectSizeChange(size) {
      this.projectPage.size = size
      this.loadProjects()
    },
    handleProjectCurrentChange(current) {
      this.projectPage.current = current
      this.loadProjects()
    },
    handleResourceSizeChange(size) {
      this.resourcePage.size = size
      this.loadResources()
    },
    handleResourceCurrentChange(current) {
      this.resourcePage.current = current
      this.loadResources()
    },
    resetProjectFilters() {
      this.projectQuery = { keyword: '', industry: '' }
      this.projectPage.current = 1
      this.loadProjects()
    },
    resetResourceFilters() {
      this.resourceQuery = { keyword: '', category: '' }
      this.resourcePage.current = 1
      this.loadResources()
    },
    refreshData() {
      if (this.activeTab === 'project') {
        this.loadProjects()
      } else {
        this.loadResources()
      }
      this.$message.success('数据刷新成功')
    },
    async doExport() {
      try {
        this.$message.success('导出成功')
      } catch (error) {
        console.error('Export error:', error)
        this.$message.error('导出失败')
      }
    },
    async auditProject(id, status) {
      if (status === 'rejected') {
        // 打开审核不通过弹窗
        this.rejectForm.itemId = id
        this.rejectForm.itemType = 'project'
        this.rejectForm.reason = ''
        this.showRejectDialog = true
      } else {
        // 直接通过审核
        try {
          // 实际API请求
          await adminAudit.projectAudit(id, { status })
          this.$message.success('项目审核通过')
          this.loadProjects()
          // 更新统计数据
          this.pendingProjects--
          this.todayApproved++
          
          // 发送审核通过通知
          this.sendAuditNotification(id, 'project', 'online', '项目审核通过')
          
          // 通知Layout组件更新待审核数量
          this.refreshAuditCount()
        } catch (error) {
          console.error('Audit project error:', error)
          this.$message.error('审核操作失败')
        }
      }
    },
    async auditResource(id, status) {
      if (status === 'rejected') {
        // 打开审核不通过弹窗
        this.rejectForm.itemId = id
        this.rejectForm.itemType = 'resource'
        this.rejectForm.reason = ''
        this.showRejectDialog = true
      } else {
        // 直接通过审核
        try {
          // 实际API请求
          await adminAudit.resourceAudit(id, { status })
          this.$message.success('服务审核通过')
          this.loadResources()
          // 更新统计数据
          this.pendingResources--
          this.todayApproved++
          
          // 发送审核通过通知
          this.sendAuditNotification(id, 'resource', 'online', '服务审核通过')
          
          // 通知Layout组件更新待审核数量
          this.refreshAuditCount()
        } catch (error) {
          console.error('Audit resource error:', error)
          this.$message.error('审核操作失败')
        }
      }
    },
    async confirmReject() {
      if (!this.rejectForm.reason.trim()) {
        this.$message.error('请输入不通过原因')
        return
      }
      
      this.rejectLoading = true
      try {
        const { itemId, itemType, reason } = this.rejectForm
        // 实际API请求
        if (itemType === 'project') {
          await adminAudit.projectAudit(itemId, { status: 'rejected', rejectReason: reason })
        } else {
          await adminAudit.resourceAudit(itemId, { status: 'rejected', rejectReason: reason })
        }
        
        this.$message.success(`${itemType === 'project' ? '项目' : '服务'}审核不通过`)
        
        // 刷新数据
        if (itemType === 'project') {
          this.loadProjects()
          this.pendingProjects--
        } else {
          this.loadResources()
          this.pendingResources--
        }
        this.todayRejected++
        
        // 发送审核不通过通知
        this.sendAuditNotification(itemId, itemType, 'rejected', `${itemType === 'project' ? '项目' : '服务'}审核不通过: ${reason}`)
        
        // 关闭弹窗
        this.showRejectDialog = false
        
        // 通知Layout组件更新待审核数量
        this.refreshAuditCount()
      } catch (error) {
        console.error('Reject audit error:', error)
        this.$message.error('审核操作失败')
      } finally {
        this.rejectLoading = false
      }
    },
    refreshAuditCount() {
      // 通过事件总线通知Layout组件更新待审核数量
      // 避免使用路由推送导致的冗余导航错误
      this.$root.$emit('refreshPendingAudits')
    },
    async sendAuditNotification(itemId, itemType, status, content) {
      try {
        // 模拟发送通知
        // 实际应用中，应该通过后端API获取项目/服务的提交者ID
        // 这里使用模拟数据
        const submitterId = 1 // 模拟提交者ID
        
        await messageApi.send({
          toUserId: submitterId,
          content: content,
          type: 'audit',
          relatedId: itemId,
          relatedType: itemType
        })
        console.log('审核通知发送成功')
      } catch (error) {
        console.error('发送通知失败:', error)
        // 通知发送失败不影响审核流程
      }
    },
    viewProjectDetail(id) {
      const project = this.projectList.find(p => p.id === id)
      if (project) {
        this.currentAuditItem = project
        this.auditDialogTitle = '项目详情'
        this.showAuditDialog = true
      }
    },
    viewResourceDetail(id) {
      const resource = this.resourceList.find(r => r.id === id)
      if (resource) {
        this.currentAuditItem = resource
        this.auditDialogTitle = '服务详情'
        this.showAuditDialog = true
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
    getCategoryTagClass(category) {
      const classMap = {
        '技术服务': 'tag-tech',
        '法律咨询': 'tag-legal',
        '财务服务': 'tag-finance',
        '市场营销': 'tag-marketing',
        '其他': 'tag-default'
      }
      return classMap[category] || 'tag-default'
    }
  }
}
</script>

<style>
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

/* 审核管理区域 */
.audit-management-section {
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

/* 审核概览卡片网格 */
.audit-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(59, 130, 246, 0.3);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-title {
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
  margin: 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  transition: all 0.3s ease;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

.stat-icon.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.stat-icon.disabled {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #f8fafc;
  line-height: 1;
}

.stat-change {
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive {
  color: #10b981;
}

.stat-change.positive span {
  color: #94a3b8;
  font-weight: 400;
}

.stat-change.warning {
  color: #f59e0b;
}

.stat-change.warning span {
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

/* 标签页 */
.audit-tabs {
  margin-top: 16px;
}

.el-tabs__header {
  margin-bottom: 24px;
}

.el-tabs__nav {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.el-tabs__item {
  color: #94a3b8;
  font-size: 16px;
  font-weight: 500;
  padding: 12px 24px;
  margin-right: 24px;
  transition: all 0.3s ease;
}

.el-tabs__item:hover {
  color: #3b82f6;
}

.el-tabs__item.is-active {
  color: #f8fafc;
  font-weight: 600;
}

.el-tabs__active-bar {
  background: #3b82f6;
  height: 3px;
  border-radius: 3px;
}

.tab-content {
  padding: 16px;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

/* 搜索筛选区域 */
.search-filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #1e293b;
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

/* 为project-table和resource-table类添加更具体的深色主题样式 */
.admin-page .project-table,
.admin-page .resource-table {
  background-color: #1e293b !important;
}

.admin-page .project-table th,
.admin-page .resource-table th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
}

.admin-page .project-table td,
.admin-page .resource-table td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
}

.admin-page .project-table tr:hover td,
.admin-page .resource-table tr:hover td {
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

/* 项目和服务标题链接 */
.project-title,
.resource-title {
  line-height: 1.4;
}

.title-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.title-link:hover {
  color: #60a5fa;
  text-decoration: underline;
}

/* 审核操作按钮组 */
.audit-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
  min-height: 80px;
  padding: 8px 0;
  width: 100%;
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
  border-radius: 16px !important;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5) !important;
  z-index: 1000 !important;
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
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 24px !important;
  background: #334155 !important;
}

.custom-dialog .el-dialog__title {
  color: #f8fafc !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

.custom-dialog .el-dialog__body {
  padding: 24px !important;
  color: #f8fafc !important;
  background: #1e293b !important;
}

.custom-dialog .el-dialog__footer {
  border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 20px 24px !important;
  background: #334155 !important;
}

/* 确保弹窗内的表单元素使用深色主题 */
.custom-dialog .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.custom-dialog .el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.custom-dialog .el-select-dropdown {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 8px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
  z-index: 10000 !important;
}

.custom-dialog .el-select-dropdown__item {
  color: #f8fafc !important;
  padding: 12px 16px !important;
}

.custom-dialog .el-select-dropdown__item:hover {
  background: rgba(59, 130, 246, 0.1) !important;
}

.custom-dialog .el-select-dropdown__item.selected {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
}

/* 确保弹窗内的按钮使用深色主题 */
.custom-dialog .el-button {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  font-size: 14px !important;
}

.custom-dialog .el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  border: none !important;
}

.custom-dialog .el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%) !important;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4) !important;
  transform: translateY(-1px) !important;
}

.custom-dialog .el-button {
  background: #475569 !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.custom-dialog .el-button:hover {
  background: #64748b !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
  transform: translateY(-1px) !important;
}

/* 审核详情样式 */
.audit-detail {
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

/* 审核不通过表单样式 */
.reject-form {
  padding: 16px;
}

.reject-reason-input {
  background: #334155;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  color: #f8fafc;
  resize: vertical;
}

.reject-reason-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.reject-reason-input::placeholder {
  color: #64748b;
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

.tag-tech {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.3);
}

.tag-legal {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.3);
}

.tag-finance {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  border-color: rgba(245, 158, 11, 0.3);
}

.tag-marketing {
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
  
  .audit-stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
  
  .audit-management-section {
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
  
  .el-tabs__item {
    font-size: 14px;
    padding: 10px 20px;
    margin-right: 16px;
  }
  
  .tab-content {
    padding: 12px;
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
  
  .audit-stats-grid {
    grid-template-columns: 1fr;
  }
  
  .search-input {
    width: 100%;
    max-width: 300px;
  }
  
  .filter-select {
    width: 100%;
    max-width: 150px;
  }
  
  .audit-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
  }
  
  .audit-actions .el-button {
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