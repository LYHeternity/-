<template>
  <div class="admin-page dark-theme">
    <div class="page-header-section">
      <div class="page-header-content">
        <h1 class="page-title">社区管理</h1>
        <p class="page-subtitle">管理社区帖子，包括编辑、删除和状态管理</p>
      </div>
      <div class="page-header-actions">
        <el-button type="primary" icon="el-icon-refresh" @click="loadPosts" class="primary-action-button">
          刷新数据
        </el-button>
        <el-button type="info" @click="testDialog">
          测试对话框
        </el-button>
      </div>
    </div>

    <div class="project-management-section">
      <div class="section-header">
        <h2 class="section-title">帖子列表</h2>
        <div class="section-actions">
          <el-button type="primary" icon="el-icon-download" @click="doExport" class="action-button">
            导出帖子
          </el-button>
        </div>
      </div>

      <!-- 搜索筛选区域 -->
      <div class="search-filter-section">
        <el-form inline class="search-form">
          <el-form-item label="关键词">
            <el-input v-model="searchKeyword" placeholder="标题/内容" clearable class="search-input" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="searchCategory" clearable placeholder="全部" class="filter-select">
              <el-option label="全部" value=""></el-option>
              <el-option label="创业经验" value="创业经验" />
              <el-option label="融资技巧" value="融资技巧" />
              <el-option label="技术分享" value="技术分享" />
              <el-option label="行业动态" value="行业动态" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="searchType" clearable placeholder="全部" class="filter-select">
              <el-option label="全部" value=""></el-option>
              <el-option label="文章" value="article" />
              <el-option label="问题" value="question" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchStatus" clearable placeholder="全部" class="filter-select">
              <el-option label="全部" value=""></el-option>
              <el-option label="正常" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadPosts" class="search-button">
              <i class="el-icon-search"></i> 查询
            </el-button>
            <el-button @click="resetFilters" class="reset-button">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 帖子表格 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="posts"
          style="width: 100%"
          class="admin-table admin-table-community"
          stripe
          :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
          :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200">
            <template slot-scope="scope">
              <div class="project-title">
                <a :href="`/community/post/${scope.row.id}`" target="_blank" class="title-link">{{ scope.row.title }}</a>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100">
            <template slot-scope="scope">
              <el-tag :class="getTypeTagClass(scope.row.type)" effect="light">
                {{ scope.row.type === 'article' ? '文章' : '问题' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="userId" label="作者ID" width="80" />
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="commentCount" label="评论" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :class="getStatusTagClass(scope.row.status)" effect="light">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" min-width="300" align="right">
            <template slot-scope="scope">
              <div class="project-actions">
                <el-button type="primary" size="small" @click.stop="viewPost(scope.row.id)" class="action-button primary">
                  <i class="el-icon-view"></i> 查看
                </el-button>
                <el-button type="info" size="small" @click.stop="editPost(scope.row.id)" class="action-button">
                  <i class="el-icon-edit"></i> 编辑
                </el-button>
                <el-button 
                  :type="scope.row.status === 1 ? 'danger' : 'success'" 
                  size="small" 
                  @click.stop="toggleStatus(scope.row)"
                  class="action-button"
                >
                  <i :class="scope.row.status === 1 ? 'el-icon-close' : 'el-icon-check'">
                  </i> {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button type="danger" size="small" @click.stop="deletePost(scope.row.id)" class="action-button">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空数据状态 -->
        <div v-if="!loading && !posts.length" class="empty-state">
          <div class="empty-icon-container">
            <i class="el-icon-document"></i>
          </div>
          <h3 class="empty-title">暂无帖子数据</h3>
          <p class="empty-description">当前筛选条件下没有找到帖子信息</p>
          <el-button type="primary" @click="resetFilters" class="empty-action-button">
            重置筛选条件
          </el-button>
        </div>
      </div>

      <!-- 分页控件 -->
      <div class="pagination-section" v-if="total > 0">
        <div class="pagination-info">
          共 {{ total }} 条记录，当前第 {{ currentPage }} 页
        </div>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="pagination-controls"
        />
      </div>
    </div>

    <!-- 编辑帖子对话框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      title="编辑帖子"
      width="80%"
      :close-on-click-modal="false"
      :z-index="9999"
      :modal="false"
    >
      <el-form ref="postForm" :model="postForm" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" style="width: 100%" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="postForm.type" style="width: 100%">
            <el-option label="文章" value="article" />
            <el-option label="问题" value="question" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="postForm.category" style="width: 100%">
            <el-option label="创业经验" value="创业经验" />
            <el-option label="融资技巧" value="融资技巧" />
            <el-option label="技术分享" value="技术分享" />
            <el-option label="行业动态" value="行业动态" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="postForm.status" active-text="正常" inactive-text="禁用" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入帖子内容"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePost" :loading="saving">保存</el-button>
      </span>
    </el-dialog>

    <!-- 查看帖子对话框 -->
    <el-dialog
      :visible.sync="viewDialogVisible"
      title="查看帖子"
      width="80%"
      :close-on-click-modal="false"
      :z-index="9999"
      :modal="false"
    >
      <div v-loading="loadingDetail">
        <h3 style="margin-bottom: 16px;">{{ postForm.title }}</h3>
        <div style="margin-bottom: 16px;">
          <span style="margin-right: 16px;">类型：{{ postForm.type === 'article' ? '文章' : '问题' }}</span>
          <span style="margin-right: 16px;">分类：{{ postForm.category }}</span>
          <span>状态：{{ postForm.status === 1 ? '正常' : '禁用' }}</span>
        </div>
        <div style="line-height: 1.6; white-space: pre-wrap;">{{ postForm.content }}</div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { postAPI } from '@/api/community'

export default {
  name: 'CommunityManage',
  data() {
    return {
      posts: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchKeyword: '',
      searchCategory: '',
      searchType: '',
      searchStatus: '',
      selectedPosts: [],
      // 编辑对话框
      dialogVisible: false,
      viewDialogVisible: false,
      currentPostId: null,
      loadingDetail: false,
      saving: false,
      postForm: {
        title: '',
        content: '',
        type: 'article',
        category: '创业经验',
        status: 1
      },
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择类型', trigger: 'change' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadPosts()
  },
  methods: {
    loadPosts() {
      this.loading = true
      postAPI.getPostList({
        page: this.currentPage,
        size: this.pageSize,
        category: this.searchCategory,
        type: this.searchType,
        status: this.searchStatus,
        keyword: this.searchKeyword
      }).then(res => {
        if (res.data && res.data.records) {
          this.posts = res.data.records
          this.total = res.data.total || 0
        } else {
          this.posts = []
          this.total = 0
        }
      }).catch(err => {
        console.error('加载帖子失败:', err)
        this.$message.error('加载帖子失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadPosts()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadPosts()
    },
    resetFilters() {
      this.searchKeyword = ''
      this.searchCategory = ''
      this.searchType = ''
      this.searchStatus = ''
      this.currentPage = 1
      this.loadPosts()
    },
    handleSelectionChange(selection) {
      this.selectedPosts = selection
    },
    doExport() {
      if (this.selectedPosts.length === 0) {
        this.$message.warning('请先选择要导出的帖子')
        return
      }
      try {
        const postIds = this.selectedPosts.map(post => post.id)
        console.log('导出帖子ID:', postIds)
        
        // 准备导出数据
        const exportData = this.selectedPosts.map(post => {
          return {
            id: post.id,
            title: post.title,
            type: post.type === 'article' ? '文章' : '问题',
            category: post.category,
            userId: post.userId,
            viewCount: post.viewCount,
            likeCount: post.likeCount,
            commentCount: post.commentCount,
            status: post.status === 1 ? '正常' : '禁用',
            createdAt: post.createdAt
          }
        })
        
        // 转换为CSV格式
        const headers = ['ID', '标题', '类型', '分类', '作者ID', '浏览量', '点赞数', '评论数', '状态', '创建时间']
        const csvContent = [
          headers.join(','),
          ...exportData.map(row => Object.values(row).join(','))
        ].join('\n')
        
        // 创建下载链接
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.setAttribute('href', url)
        link.setAttribute('download', `帖子导出_${new Date().getTime()}.csv`)
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
    editPost(id) {
      console.log('编辑帖子:', id)
      this.currentPostId = id
      this.dialogVisible = true
      this.viewDialogVisible = false
      this.loadPostDetail(id)
    },
    viewPost(id) {
      console.log('查看帖子:', id)
      this.currentPostId = id
      this.viewDialogVisible = true
      this.dialogVisible = false
      this.loadPostDetail(id)
    },
    loadPostDetail(id) {
      console.log('开始加载帖子详情:', id)
      this.loadingDetail = true
      console.log('调用postAPI.getPostDetail')
      postAPI.getPostDetail(id).then(res => {
        console.log('获取帖子详情响应:', res)
        // 修复响应处理，确保正确检查code和data
        if (res && (res.code === 200 || res.code === '200') && res.data) {
          console.log('原始响应数据:', res.data)
          // 处理响应数据，确保字段类型正确
          const postData = res.data
          this.postForm = {
            title: postData.title || '',
            content: postData.content || '',
            type: postData.type || 'article',
            category: postData.category || '创业经验',
            status: postData.status || 1,
            userId: postData.userId || null,
            bestAnswerId: postData.bestAnswerId || null,
            viewCount: postData.viewCount || 0,
            likeCount: postData.likeCount || 0,
            commentCount: postData.commentCount || 0
          }
          console.log('设置postForm:', this.postForm)
        } else {
          console.error('加载帖子详情失败，响应:', res)
          this.$message.error('加载帖子详情失败')
        }
      }).catch(err => {
        console.error('加载帖子详情失败:', err)
        this.$message.error('加载帖子详情失败')
      }).finally(() => {
        console.log('加载帖子详情完成')
        this.loadingDetail = false
      })
    },
    savePost() {
      console.log('开始保存帖子:', this.currentPostId, this.postForm)
      this.$refs.postForm.validate(valid => {
        if (valid) {
          console.log('表单验证通过，开始调用API')
          this.saving = true
          
          // 准备更新数据，处理类型转换，确保没有"null"字符串
          const updateData = {
            title: this.postForm.title || '',
            content: this.postForm.content || '',
            type: this.postForm.type || 'article',
            category: this.postForm.category || '创业经验',
            status: Number(this.postForm.status) || 1,
            // 处理Long类型字段，确保不会传递"null"字符串
            userId: this.postForm.userId && this.postForm.userId !== 'null' ? Number(this.postForm.userId) : null,
            bestAnswerId: this.postForm.bestAnswerId && this.postForm.bestAnswerId !== 'null' ? Number(this.postForm.bestAnswerId) : null,
            viewCount: Number(this.postForm.viewCount) || 0,
            likeCount: Number(this.postForm.likeCount) || 0,
            commentCount: Number(this.postForm.commentCount) || 0
          }
          
          // 移除所有可能导致问题的字段
          Object.keys(updateData).forEach(key => {
            if (updateData[key] === 'null' || updateData[key] === null || updateData[key] === undefined) {
              delete updateData[key]
            }
          })
          
          console.log('准备更新的数据:', updateData)
          
          postAPI.updatePost(this.currentPostId, updateData).then(res => {
            console.log('更新帖子响应:', res)
            if (res && (res.code === 200 || res.code === '200')) {
              console.log('更新成功')
              this.$message.success('更新成功')
              this.dialogVisible = false
              this.loadPosts()
            } else {
              console.error('更新失败，响应:', res)
              this.$message.error(res.message || '更新失败')
            }
          }).catch(err => {
            console.error('更新帖子失败:', err)
            this.$message.error('更新失败，请稍后重试')
          }).finally(() => {
            console.log('更新帖子完成')
            this.saving = false
          })
        } else {
          console.error('表单验证失败')
        }
      })
    },
    deletePost(id) {
      console.log('开始删除帖子:', id)
      this.$confirm('确定要删除这篇帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log('用户确认删除，调用API')
        postAPI.deletePost(id).then(res => {
          console.log('删除帖子响应:', res)
          if (res && (res.code === 200 || res.code === '200')) {
            console.log('删除成功')
            this.$message.success('删除成功')
            this.loadPosts()
          } else {
            console.error('删除失败，响应:', res)
            this.$message.error(res.message || '删除失败')
          }
        }).catch(err => {
          console.error('删除帖子失败:', err)
          this.$message.error('删除失败，请稍后重试')
        })
      }).catch(() => {
        console.log('用户取消删除')
        // 取消删除
      })
    },
    toggleStatus(post) {
      console.log('开始切换状态:', post.id, post.status)
      const newStatus = post.status === 1 ? 0 : 1
      console.log('新状态:', newStatus)
      postAPI.updatePost(post.id, { status: newStatus }).then(res => {
        console.log('更新状态响应:', res)
        if (res && (res.code === 200 || res.code === '200')) {
          console.log('状态更新成功')
          post.status = newStatus
          this.$message.success('状态更新成功')
        } else {
          console.error('状态更新失败，响应:', res)
          this.$message.error(res.message || '状态更新失败')
        }
      }).catch(err => {
        console.error('更新状态失败:', err)
        this.$message.error('状态更新失败，请稍后重试')
      })
    },
    getTypeTagClass(type) {
      const classMap = {
        'article': 'tag-ai',
        'question': 'tag-internet'
      }
      return classMap[type] || 'tag-default'
    },
    getStatusTagClass(status) {
      const classMap = {
        1: 'tag-status-active',
        0: 'tag-status-inactive'
      }
      return classMap[status] || 'tag-default'
    },
    testDialog() {
      console.log('测试对话框')
      this.dialogVisible = true
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
.admin-page .admin-table-community tr:hover td,
.admin-page .admin-table-community tr:hover .el-table__cell {
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
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.title-link:hover {
  color: #60a5fa;
  text-decoration: underline;
}

/* 项目操作按钮组 - 2x2网格布局 */
.project-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  padding: 6px 0;
  width: 100%;
  box-sizing: border-box;
  position: relative;
  z-index: 9999;
  min-height: 90px;
  pointer-events: auto !important;
}

/* 确保按钮可点击 */
.project-actions .el-button {
  position: relative;
  z-index: 9999;
  cursor: pointer;
  pointer-events: auto !important;
  width: 100%;
  justify-content: center;
  font-size: 12px;
  padding: 6px 12px;
  margin: 0;
  border-radius: 6px;
  background: #1e293b;
  border: 1px solid rgba(59, 130, 246, 0.3);
  color: #f8fafc;
  transition: all 0.3s ease;
}

/* 修复按钮悬停效果 */
.project-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  background: #3b82f6;
  border-color: #3b82f6;
}

/* 确保表格单元格内容不被遮挡 */
.el-table__cell {
  overflow: visible !important;
  z-index: 100 !important;
  pointer-events: auto !important;
  position: relative;
}

/* 确保表格行和单元格不会阻止点击事件 */
.el-table__row {
  pointer-events: auto !important;
  position: relative;
  z-index: 10;
}

.el-table__body {
  pointer-events: auto !important;
  position: relative;
  z-index: 5;
}

/* 确保按钮区域的所有元素都能接收点击事件 */
.project-actions * {
  pointer-events: auto !important;
  cursor: pointer !important;
  position: relative;
  z-index: 9999;
}

/* 确保导出按钮可点击 */
.section-actions .el-button {
  position: relative;
  z-index: 9999;
  pointer-events: auto !important;
  cursor: pointer !important;
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

/* 确保选择器输入框中的文字垂直居中 */
.admin-page.dark-theme :deep(.el-select .el-input__inner) {
  display: flex !important;
  align-items: center !important;
  height: 32px !important;
  line-height: 32px !important;
  font-size: 14px !important;
  padding: 0 12px !important;
  box-sizing: border-box !important;
  vertical-align: middle !important;
}

.admin-page.dark-theme :deep(.el-select .el-input__inner::placeholder) {
  line-height: 32px !important;
  vertical-align: middle !important;
  font-size: 14px !important;
}

/* 确保下拉菜单容器样式正确 */
.admin-page.dark-theme :deep(.el-select-dropdown) {
  background: linear-gradient(135deg, var(--hw-dark) 0%, var(--hw-dark-bg) 100%) !important;
  border: 1px solid var(--hw-dark-border) !important;
  border-radius: var(--hw-radius) !important;
  box-shadow: var(--hw-dark-shadow-modal) !important;
  z-index: 9999 !important;
  padding: 0 !important;
  box-sizing: border-box !important;
}

/* 确保下拉菜单容器样式正确 */
.admin-page.dark-theme :deep(.el-popper) {
  z-index: 9999 !important;
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

/* 对话框样式 */
.admin-page .el-dialog {
  z-index: 9999 !important;
  background: #1e293b !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3) !important;
}

.admin-page .el-dialog__wrapper {
  z-index: 9999 !important;
  background: transparent !important;
  backdrop-filter: none !important;
  box-shadow: none !important;
  opacity: 1 !important;
}

/* 确保没有其他元素影响蒙版 */
.admin-page .el-dialog__wrapper::before {
  display: none !important;
}

.admin-page .el-dialog__wrapper::after {
  display: none !important;
}

.admin-page .el-dialog__body {
  color: #f8fafc !important;
  background: #1e293b !important;
}

.admin-page .el-dialog__header {
  background: #334155 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.admin-page .el-dialog__title {
  color: #f8fafc !important;
  font-weight: 600 !important;
}

.admin-page .el-dialog__footer {
  background: #334155 !important;
  border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.admin-page .el-dialog__close {
  color: #94a3b8 !important;
}

/* 确保表单元素在对话框中可见 */
.admin-page .el-dialog .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.admin-page .el-dialog .el-textarea__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.admin-page .el-dialog .el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.admin-page .el-dialog .el-select-dropdown {
  background: #1e293b !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  z-index: 10000 !important;
}

/* 确保其他下拉组件层级高于弹窗 */
.admin-page .el-date-picker {
  z-index: 10000 !important;
}

.admin-page .el-dropdown-menu {
  z-index: 10000 !important;
}

.admin-page .el-dialog .el-select-dropdown__item {
  color: #f8fafc !important;
}

.admin-page .el-dialog .el-select-dropdown__item:hover {
  background: rgba(59, 130, 246, 0.1) !important;
}

.admin-page .el-dialog .el-select-dropdown__item.selected {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
}

.admin-page .el-dialog .el-switch__core {
  border-color: rgba(255, 255, 255, 0.12) !important;
}

.admin-page .el-dialog .el-switch.is-active .el-switch__core {
  background-color: #3b82f6 !important;
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
