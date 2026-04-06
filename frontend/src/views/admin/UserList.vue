<template>
  <div class="admin-page dark-theme">
    <div class="page-header-section">
      <div class="page-header-content">
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">管理平台用户账号、角色权限及相关设置</p>
      </div>
      <div class="page-header-actions">
        <el-button type="primary" @click="openAddUserDialog" class="primary-action-button">
          <i class="el-icon-plus"></i> 新增用户
        </el-button>
      </div>
    </div>

    <div class="user-management-dashboard">
      <!-- 用户概览卡片 -->
      <div class="user-stats-grid">
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">总用户数</h3>
            <span class="stat-icon total"><i class="el-icon-user"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-change positive">+12% <span>较上月</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">活跃用户</h3>
            <span class="stat-icon active"><i class="el-icon-video-camera"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ activeUsers }}</div>
            <div class="stat-change positive">+8% <span>较上月</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">待审核</h3>
            <span class="stat-icon pending"><i class="el-icon-time"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ pendingUsers }}</div>
            <div class="stat-change warning">+3 <span>今日</span></div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <h3 class="stat-title">禁用用户</h3>
            <span class="stat-icon disabled"><i class="el-icon-close"></i></span>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ disabledUsers }}</div>
            <div class="stat-change negative">-2 <span>本月</span></div>
          </div>
        </div>
      </div>

      <!-- 用户列表管理 -->
      <div class="user-list-section">
        <div class="section-header">
          <h2 class="section-title">用户列表</h2>
          <div class="section-actions">
            <el-button type="primary" icon="el-icon-download" @click="doExport" class="action-button">
              导出 Excel
            </el-button>
            <el-dropdown trigger="click" class="batch-actions">
              <el-button icon="el-icon-s-operation" class="action-button">
                批量操作 <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click="batchEnable">批量启用</el-dropdown-item>
                <el-dropdown-item @click="batchDisable">批量禁用</el-dropdown-item>
                <el-dropdown-item @click="batchResetPassword">批量重置密码</el-dropdown-item>
                <el-dropdown-item divided @click="batchDelete">批量删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>

        <!-- 搜索筛选区域 -->
        <div class="search-filter-section">
          <el-form inline class="search-form">
            <el-form-item label="关键词">
              <el-input v-model="query.keyword" placeholder="用户名/手机/邮箱" clearable class="search-input" />
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="query.role" clearable placeholder="全部" class="filter-select">
                <el-option label="创业者" value="entrepreneur" />
                <el-option label="投资人" value="investor" />
                <el-option label="服务商" value="provider" />
                <el-option label="管理员" value="admin" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="query.status" clearable placeholder="全部" class="filter-select">
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="注册时间">
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
              <el-button type="primary" @click="load" class="search-button">
                <i class="el-icon-search"></i> 查询
              </el-button>
              <el-button @click="resetFilters" class="reset-button">
                <i class="el-icon-refresh"></i> 重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 批量选择栏 -->
        <div v-if="selectedRows.length > 0" class="batch-selection-bar">
          <div class="selection-info">
            <i class="el-icon-check-box"></i>
            <span>已选择 {{ selectedRows.length }} 个用户</span>
          </div>
          <div class="selection-actions">
            <el-button size="small" @click="batchEnable">批量启用</el-button>
            <el-button size="small" @click="batchDisable">批量禁用</el-button>
            <el-button size="small" @click="batchResetPassword">批量重置密码</el-button>
            <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
            <el-button size="small" @click="clearSelection">取消选择</el-button>
          </div>
        </div>

        <!-- 用户表格 -->
        <div class="user-table-container">
          <el-table
            v-loading="loading"
            :data="list"
            style="width: 100%;"
            class="user-table"
            @selection-change="handleSelectionChange"
            stripe
            :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
            :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="用户信息" min-width="200">
              <template slot-scope="scope">
                <div class="user-info-cell">
                  <div class="user-avatar">
                    <img v-if="scope.row.avatar" :src="scope.row.avatar" class="user-avatar-image" alt="用户头像" />
                    <span v-else>{{ (scope.row.realName || scope.row.username || 'A').charAt(0) }}</span>
                  </div>
                  <div class="user-details">
                    <h4 class="user-name">{{ scope.row.realName || scope.row.username }}</h4>
                    <p class="user-username">{{ scope.row.username }}</p>
                    <p class="user-contact">{{ scope.row.phone }} | {{ scope.row.email }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="120">
              <template slot-scope="scope">
                <el-tag :class="getRoleTagClass(scope.row.role)" effect="light">
                  {{ getRoleLabel(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :class="getStatusTagClass(scope.row.status)" effect="light">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime || scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="lastLoginAt" label="最后登录" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.lastLoginAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="320" align="right">
              <template slot-scope="scope">
                <div class="user-actions">
                  <el-button type="primary" size="small" @click="openEditUserDialog(scope.row)" class="action-button primary">
                    <i class="el-icon-edit"></i> 编辑
                  </el-button>
                  <el-button 
                    :type="scope.row.status === 1 ? 'warning' : 'success'" 
                    size="small" 
                    @click="setStatus(scope.row.id, scope.row.status === 1 ? 0 : 1)"
                    class="action-button"
                  >
                    <i :class="scope.row.status === 1 ? 'el-icon-close' : 'el-icon-check'"></i> {{ scope.row.status === 1 ? '禁用' : '启用' }}
                  </el-button>
                  <el-button type="info" size="small" @click="openAssignRolesDialog(scope.row)" class="action-button">
                    <i class="el-icon-s-custom"></i> 分配角色
                  </el-button>
                  <el-button type="danger" size="small" @click="resetPwd(scope.row.id)" class="action-button">
                    <i class="el-icon-key"></i> 重置密码
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 空数据状态 -->
          <div v-if="!loading && !list.length" class="empty-state">
            <div class="empty-icon-container">
              <i class="el-icon-user"></i>
            </div>
            <h3 class="empty-title">暂无用户数据</h3>
            <p class="empty-description">当前筛选条件下没有找到用户信息</p>
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
    </div>

    <!-- 新增用户弹窗 -->
    <el-dialog 
      title="新增用户" 
      :visible.sync="showAddUserDialog" 
      width="600px"
      class="custom-dialog"
      :modal="false"
      :z-index="1000"
    >
      <el-form :model="newUserForm" :rules="userFormRules" ref="newUserFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="newUserForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="newUserForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="newUserForm.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="newUserForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="newUserForm.role" placeholder="请选择角色">
            <el-option label="创业者" value="entrepreneur" />
            <el-option label="投资人" value="investor" />
            <el-option label="服务商" value="provider" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="初始密码" prop="password">
          <el-input v-model="newUserForm.password" type="password" placeholder="请输入初始密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddUserDialog = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户弹窗 -->
    <el-dialog 
      title="编辑用户" 
      :visible.sync="showEditUserDialog" 
      width="600px"
      class="custom-dialog"
      :modal="false"
      :z-index="1001"
    >
      <el-form :model="editUserForm" :rules="userFormRules" ref="editUserFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editUserForm.username" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editUserForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="editUserForm.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="editUserForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editUserForm.role" placeholder="请选择角色">
            <el-option label="创业者" value="entrepreneur" />
            <el-option label="投资人" value="investor" />
            <el-option label="服务商" value="provider" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditUserDialog = false">取消</el-button>
          <el-button type="primary" @click="updateUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 分配角色弹窗 -->
    <el-dialog 
      title="分配角色" 
      :visible.sync="showAssignRolesDialog" 
      width="500px"
      class="custom-dialog"
      :modal="false"
      :z-index="1002"
    >
      <el-form :model="assignRolesForm" label-width="100px">
        <el-form-item label="用户">
          <el-input v-model="assignRolesForm.userName" placeholder="用户名" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="assignRolesForm.roleCodes" multiple placeholder="请选择角色" style="width: 100%">
            <el-option label="创业者" value="entrepreneur" />
            <el-option label="投资人" value="investor" />
            <el-option label="服务商" value="provider" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAssignRolesDialog = false">取消</el-button>
          <el-button type="primary" @click="assignRoles">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminUser, exportUsers } from '@/api/admin'
export default {
  name: 'AdminUserList',
  data() {
    return {
      loading: false,
      list: [],
      query: {
        role: '',
        keyword: '',
        status: null
      },
      page: {
        current: 1,
        size: 10,
        total: 0
      },
      dateRange: [],
      selectedRows: [],
      showAddUserDialog: false,
      showEditUserDialog: false,
      showAssignRolesDialog: false,
      newUserForm: {
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: '',
        password: ''
      },
      editUserForm: {
        id: '',
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: ''
      },
      assignRolesForm: {
        userId: '',
        userName: '',
        roleCodes: []
      },
      userFormRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3-20 之间', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的电子邮箱', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        password: [
          { required: true, message: '请输入初始密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
        ]
      },
      totalUsers: 128,
      activeUsers: 96,
      pendingUsers: 8,
      disabledUsers: 12
    }
  },
  created() {
    this.load()
    // 监听Vuex中用户信息的变化，当当前用户更新头像时，刷新列表
    this.$store.watch(
      state => state.user,
      (newUser) => {
        // 当用户信息更新且用户仍然登录时，重新加载用户列表
        const token = this.$store.state.user.token || localStorage.getItem('token')
        if (token) {
          this.load()
        }
      },
      { deep: true }
    )
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
    async load() {
      this.loading = true
      try {
        const params = {
          current: this.page.current,
          size: this.page.size,
          role: this.query.role,
          keyword: this.query.keyword,
          status: this.query.status
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await adminUser.list(params)
        this.list = res.records || []
        this.page.total = res.total || 0
      } catch (error) {
        console.error('Load users error:', error)
        this.$message.error('加载用户列表失败')
        // 模拟数据
        this.list = [
          {
            id: 1,
            username: 'admin',
            realName: '管理员',
            phone: '13800138000',
            email: 'admin@example.com',
            role: 'admin',
            status: 1,
            createdAt: '2024-01-01 00:00:00',
            lastLoginAt: '2024-01-15 14:30:00'
          },
          {
            id: 2,
            username: 'zhangsan',
            realName: '张三',
            phone: '13900139000',
            email: 'zhangsan@example.com',
            role: 'entrepreneur',
            status: 1,
            createdAt: '2024-01-02 10:00:00',
            lastLoginAt: '2024-01-14 09:15:00'
          },
          {
            id: 3,
            username: 'lisi',
            realName: '李四',
            phone: '13700137000',
            email: 'lisi@example.com',
            role: 'investor',
            status: 0,
            createdAt: '2024-01-03 14:00:00',
            lastLoginAt: '2024-01-10 16:45:00'
          },
          {
            id: 4,
            username: 'wangwu',
            realName: '王五',
            phone: '13600136000',
            email: 'wangwu@example.com',
            role: 'provider',
            status: 1,
            createdAt: '2024-01-04 09:00:00',
            lastLoginAt: '2024-01-15 10:30:00'
          }
        ]
        this.page.total = 4
      } finally {
        this.loading = false
      }
    },
    async setStatus(id, status) {
      try {
        await adminUser.setStatus(id, status)
        this.$message.success('状态更新成功')
        this.load()
      } catch (e) {
        console.error('Set status error:', e)
        this.$message.error(e.message || '操作失败')
      }
    },
    async resetPwd(id) {
      try {
        await this.$confirm('确定将该用户密码重置为 Admin@1234？', '提示', { type: 'warning' })
        await adminUser.resetPassword(id, 'Admin@1234')
        this.$message.success('密码重置成功')
      } catch (e) {
        if (e !== 'cancel') {
          console.error('Reset password error:', e)
          this.$message.error(e.message || '操作失败')
        }
      }
    },
    async doExport() {
      try {
        const blob = await exportUsers()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = '用户列表.xlsx'
        a.click()
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (e) {
        console.error('Export error:', e)
        this.$message.error('导出失败')
      }
    },
    handleSizeChange(size) {
      this.page.size = size
      this.load()
    },
    handleCurrentChange(current) {
      this.page.current = current
      this.load()
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    clearSelection() {
      this.$refs.userTable.clearSelection()
    },
    resetFilters() {
      this.query = { role: '', keyword: '', status: null }
      this.dateRange = []
      this.page.current = 1
      this.load()
    },
    openAddUserDialog() {
      this.newUserForm = {
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: '',
        password: ''
      }
      this.$refs.newUserFormRef && this.$refs.newUserFormRef.resetFields()
      this.showAddUserDialog = true
    },
    openEditUserDialog(user) {
      this.editUserForm = {
        id: user.id,
        username: user.username,
        realName: user.realName,
        phone: user.phone,
        email: user.email,
        role: user.role
      }
      this.$refs.editUserFormRef && this.$refs.editUserFormRef.resetFields()
      this.showEditUserDialog = true
    },
    async addUser() {
      try {
        console.log('开始新增用户，表单数据:', this.newUserForm)
        await this.$refs.newUserFormRef.validate()
        console.log('表单验证通过，准备调用API')
        const result = await adminUser.create(this.newUserForm)
        console.log('API调用成功，结果:', result)
        this.$message.success('新增用户成功')
        this.showAddUserDialog = false
        this.load()
      } catch (error) {
        console.error('Add user error:', error)
        console.error('Error details:', error.message, error.response)
        if (error !== 'cancel') {
          this.$message.error('新增用户失败')
        }
      }
    },
    async updateUser() {
      try {
        await this.$refs.editUserFormRef.validate()
        await adminUser.update(this.editUserForm.id, this.editUserForm)
        this.$message.success('更新用户成功')
        this.showEditUserDialog = false
        this.load()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Update user error:', error)
          this.$message.error('更新用户失败')
        }
      }
    },
    async batchEnable() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要启用的用户')
        return
      }
      try {
        const ids = this.selectedRows.map(row => row.id)
        await adminUser.batchSetStatus(ids, 1)
        this.$message.success('批量启用成功')
        this.clearSelection()
        this.load()
      } catch (error) {
        console.error('Batch enable error:', error)
        this.$message.error('批量启用失败')
      }
    },
    async batchDisable() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要禁用的用户')
        return
      }
      try {
        const ids = this.selectedRows.map(row => row.id)
        await adminUser.batchSetStatus(ids, 0)
        this.$message.success('批量禁用成功')
        this.clearSelection()
        this.load()
      } catch (error) {
        console.error('Batch disable error:', error)
        this.$message.error('批量禁用失败')
      }
    },
    async batchResetPassword() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要重置密码的用户')
        return
      }
      try {
        await this.$confirm('确定将选中用户密码重置为 Admin@1234？', '提示', { type: 'warning' })
        const ids = this.selectedRows.map(row => row.id)
        await adminUser.batchResetPassword(ids, 'Admin@1234')
        this.$message.success('批量重置密码成功')
        this.clearSelection()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Batch reset password error:', error)
          this.$message.error('批量重置密码失败')
        }
      }
    },
    async batchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要删除的用户')
        return
      }
      try {
        await this.$confirm('确定删除选中的用户？此操作不可恢复', '警告', { type: 'danger' })
        const ids = this.selectedRows.map(row => row.id)
        await adminUser.batchDelete(ids)
        this.$message.success('批量删除成功')
        this.clearSelection()
        this.load()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Batch delete error:', error)
          this.$message.error('批量删除失败')
        }
      }
    },
    openAssignRolesDialog(user) {
      this.assignRolesForm = {
        userId: user.id,
        userName: user.realName || user.username,
        roleCodes: []
      }
      // 加载用户现有的角色
      this.loadUserRoles(user.id)
      this.showAssignRolesDialog = true
    },
    async loadUserRoles(userId) {
      try {
        const res = await adminUser.getUserRoles(userId)
        if (res.code === 200) {
          this.assignRolesForm.roleCodes = res.data || []
        }
      } catch (error) {
        console.error('Load user roles error:', error)
      }
    },
    async assignRoles() {
      try {
        await adminUser.assignRoles(this.assignRolesForm.userId, this.assignRolesForm.roleCodes)
        this.$message.success('角色分配成功')
        this.showAssignRolesDialog = false
        this.load()
      } catch (error) {
        console.error('Assign roles error:', error)
        this.$message.error('角色分配失败')
      }
    },
    getRoleLabel(role) {
      const roleMap = {
        admin: '管理员',
        entrepreneur: '创业者',
        investor: '投资人',
        provider: '服务商'
      }
      return roleMap[role] || role
    },
    getRoleTagClass(role) {
      const classMap = {
        admin: 'tag-admin',
        entrepreneur: 'tag-entrepreneur',
        investor: 'tag-investor',
        provider: 'tag-provider'
      }
      return classMap[role] || 'tag-default'
    },
    getStatusTagClass(status) {
      return status === 1 ? 'tag-status-active' : 'tag-status-inactive'
    },
    formatDate(date) {
      if (!date) return '-'
      if (typeof date === 'string') {
        if (date.length >= 19) {
          return date.substring(0, 19).replace('T', ' ')
        }
        return date
      }
      return date
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

/* 全局表格样式覆盖 */
.el-table,
.el-table__header-wrapper,
.el-table__body-wrapper,
.el-table__footer-wrapper {
  background-color: #1e293b !important;
}

.el-table th,
.el-table__header-wrapper th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.el-table td,
.el-table__body-wrapper td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.el-table tr:hover td,
.el-table__body-wrapper tr:hover td {
  background-color: #334155 !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #1e293b !important;
}

/* 确保表格容器也使用深色背景 */
.user-table-container {
  background-color: #1e293b !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
}

/* 全局覆盖Element UI默认样式 */
.dark-theme .admin-page .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.dark-theme .admin-page .el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.dark-theme .admin-page .el-date-editor .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.dark-theme .admin-page .el-table {
  background-color: #1e293b !important;
}

.dark-theme .admin-page .el-table th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
}

.dark-theme .admin-page .el-table td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
}

.dark-theme .admin-page .el-table tr:hover td {
  background-color: #334155 !important;
}

.dark-theme .admin-page .el-pagination {
  color: #94a3b8 !important;
}

.dark-theme .admin-page .el-pager li {
  background: #334155 !important;
  color: #94a3b8 !important;
}

.dark-theme .admin-page .el-pager li.active {
  background: #3b82f6 !important;
  color: #fff !important;
}

.dark-theme .admin-page .el-pagination__prev,
.dark-theme .admin-page .el-pagination__next {
  background: #334155 !important;
  color: #94a3b8 !important;
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

/* 用户管理仪表盘 */
.user-management-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 用户概览卡片网格 */
.user-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 8px;
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

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.stat-icon.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
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

/* 用户列表区域 */
.user-list-section {
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

/* 覆盖Element UI输入框默认样式 */
.dark-theme .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  font-size: 14px !important;
  padding: 10px 12px !important;
}

.dark-theme .el-input__inner:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
}

/* 覆盖Element UI选择器默认样式 */
.dark-theme .el-select .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

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

/* 直接针对下拉框容器的样式 */
.el-popper {
  z-index: 9999 !important;
}

/* 确保下拉框的 z-index 高于弹窗 */
.el-select-dropdown {
  z-index: 9999 !important;
}

.dark-theme .el-select-dropdown__item {
  color: #f8fafc !important;
  padding: 12px 16px !important;
  transition: all 0.2s ease !important;
  font-size: 13px !important;
}

.dark-theme .el-select-dropdown__item:hover {
  background: rgba(59, 130, 246, 0.1) !important;
}

.dark-theme .el-select-dropdown__item.selected {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
}

/* 日期选择器样式 */
.dark-theme .el-date-editor .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

.dark-theme .el-date-editor .el-input__inner:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
}

.dark-theme .el-date-picker {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 8px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
  z-index: 9999 !important;
}

.dark-theme .el-date-picker__header {
  background: #334155 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.dark-theme .el-date-picker__header-label {
  color: #f8fafc !important;
}

.dark-theme .el-date-picker__month-btn:hover,
.dark-theme .el-date-picker__year-btn:hover {
  background: rgba(59, 130, 246, 0.1) !important;
  color: #3b82f6 !important;
}

.dark-theme .el-date-picker__day {
  color: #f8fafc !important;
}

.dark-theme .el-date-picker__day:hover {
  background: rgba(59, 130, 246, 0.1) !important;
}

.dark-theme .el-date-picker__day--selected {
  background: #3b82f6 !important;
  color: white !important;
}

.dark-theme .el-date-picker__day--disabled {
  color: #64748b !important;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
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

.date-picker {
  width: 280px;
  border-radius: 8px;
}

/* 确保表单标签和输入框对齐 */
.el-form-item {
  display: flex;
  align-items: center;
  margin-bottom: 0;
}

.el-form-item__label {
  margin-right: 8px;
  white-space: nowrap;
  color: #94a3b8;
  font-weight: 500;
  font-size: 13px;
}

/* 确保日期选择器中的元素对齐 */
.el-date-editor {
  display: flex;
  align-items: center;
}

.el-date-editor .el-range-input {
  text-align: center;
}

.el-date-editor .el-range-separator {
  margin: 0 8px;
  color: #94a3b8;
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

/* 批量选择栏 */
.batch-selection-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 10px;
  margin-bottom: 20px;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #3b82f6;
}

.selection-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.selection-actions .el-button {
  padding: 6px 12px;
  font-size: 12px;
}

/* 用户表格容器 */
.user-table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  border-radius: 12px;
  background: #1e293b;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-sizing: border-box;
}

/* 为user-table类添加更具体的深色主题样式 */
.user-table {
  background-color: #1e293b !important;
}

.user-table th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
}

.user-table td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
}

.admin-page .user-table tr:hover td,
.admin-page .user-table tr:hover .el-table__cell {
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

.user-table--striped .user-table__row--striped td {
  background-color: #1e293b !important;
}

/* 覆盖Element UI表格默认样式 */
.dark-theme .el-table {
  background-color: #1e293b !important;
  border-radius: 12px;
  overflow: hidden;
}

.dark-theme .el-table th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
  font-weight: 600 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
  text-align: left;
}

.dark-theme .el-table td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
  transition: all 0.2s ease;
}

.dark-theme .el-table tr:hover td {
  background-color: #334155 !important;
}

/* 覆盖Element UI表格其他样式 */
.dark-theme .el-table--striped .el-table__row--striped td {
  background-color: #1e293b !important;
}

.dark-theme .el-table--enable-row-transition .el-table__row {
  transition: background-color 0.2s ease;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-username {
  font-size: 12px;
  color: #94a3b8;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-contact {
  font-size: 12px;
  color: #64748b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 用户操作按钮组 */
.user-actions {
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

/* 调整选择框列的宽度和对齐方式 */
.el-table-column--selection {
  width: 50px !important;
}

.el-table-column--selection .cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 80px;
}

/* 确保日期选择器背景为深色 */
#app .el-date-editor,
#app .el-date-editor .el-input,
#app .el-date-editor .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  border-radius: 8px !important;
  color: #f8fafc !important;
}

/* 确保日期选择器中的所有元素都使用深色背景 */
#app .el-date-editor .el-range-separator {
  color: #94a3b8 !important;
}

#app .el-date-editor .el-range-input {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
}

#app .el-date-editor .el-range-input:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
}

/* 确保搜索表单中的所有输入框都使用深色背景 */
#app .search-form .el-input__inner,
#app .search-form .el-select .el-input__inner,
#app .search-form .el-date-editor .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
  border-radius: 8px !important;
}

/* 确保分页控件背景为深色 */
#app .el-pagination {
  background: rgba(30, 41, 59, 0.8) !important;
  border-radius: 8px !important;
  padding: 12px 16px !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
}

#app .el-pagination__sizes {
  color: #94a3b8 !important;
}

#app .el-pagination__sizes .el-input .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
  border-radius: 8px !important;
}

/* 确保分页控件中的所有元素都使用深色背景 */
#app .el-pagination button,
#app .el-pagination span:not(.el-pagination__total) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #94a3b8 !important;
  border-radius: 8px !important;
}

#app .el-pagination button:hover,
#app .el-pagination span:not(.el-pagination__total):hover {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
}

#app .el-pagination .btn-next,
#app .el-pagination .btn-prev {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #94a3b8 !important;
  border-radius: 8px !important;
}

#app .el-pagination .btn-next:hover,
#app .el-pagination .btn-prev:hover {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
}

#app .el-pagination .el-pager li {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #94a3b8 !important;
  border-radius: 8px !important;
}

#app .el-pagination .el-pager li:hover {
  background: rgba(59, 130, 246, 0.2) !important;
  color: #3b82f6 !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
}

#app .el-pagination .el-pager li.active {
  background: #3b82f6 !important;
  color: #fff !important;
  border-color: #3b82f6 !important;
}

/* 角色标签样式 */
.tag-admin {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.3);
}

.tag-entrepreneur {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.3);
}

.tag-investor {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  border-color: rgba(245, 158, 11, 0.3);
}

.tag-provider {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.3);
}

.tag-default {
  background: rgba(100, 116, 139, 0.1);
  color: #64748b;
  border-color: rgba(100, 116, 139, 0.3);
}

/* 状态标签样式 */
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

/* 覆盖Element UI分页控件默认样式 */
.dark-theme .el-pagination {
  color: #94a3b8 !important;
}

.dark-theme .el-pagination__total {
  color: #94a3b8 !important;
}

.dark-theme .el-pager li {
  background: #334155 !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #94a3b8 !important;
  border-radius: 8px !important;
  margin: 0 4px !important;
  transition: all 0.2s ease !important;
}

.dark-theme .el-pager li:hover {
  background: #475569 !important;
  color: #f8fafc !important;
  transform: translateY(-1px) !important;
}

.dark-theme .el-pager li.active {
  background: #3b82f6 !important;
  color: #fff !important;
  border-color: #3b82f6 !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4) !important;
}

.dark-theme .el-pagination__prev,
.dark-theme .el-pagination__next {
  background: #334155 !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #94a3b8 !important;
  border-radius: 8px !important;
  transition: all 0.2s ease !important;
}

.dark-theme .el-pagination__prev:hover,
.dark-theme .el-pagination__next:hover {
  background: #475569 !important;
  color: #f8fafc !important;
  transform: translateY(-1px) !important;
}

.dark-theme .el-pagination__sizes .el-input .el-input__inner {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #f8fafc !important;
  border-radius: 8px !important;
}

/* 弹窗样式 */
.custom-dialog {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
  z-index: 1000;
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

/* 表单样式 */
.dark-theme .el-form-item__label {
  color: #94a3b8;
  font-weight: 500;
  font-size: 13px;
}

/* 按钮样式 */
.dark-theme .el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
}

/* 标签样式 */
.dark-theme .el-tag {
  border-radius: 8px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
}

/* 加载状态 */
.dark-theme .el-loading-mask {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
}

.dark-theme .el-loading-spinner .path {
  stroke: #3b82f6;
}

/* 消息提示 */
.dark-theme .el-message {
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

/* 确认对话框 */
.dark-theme .el-dialog__wrapper {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
}

.dark-theme .el-confirm {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
}

.dark-theme .el-confirm__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.dark-theme .el-confirm__title {
  color: #f8fafc;
}

.dark-theme .el-confirm__content {
  color: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
  
  .search-input {
    width: 200px;
  }
  
  .filter-select {
    width: 140px;
  }
  
  .date-picker {
    width: 240px;
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
  
  .page-title {
    font-size: 24px;
  }
  
  .user-list-section {
    padding: 20px;
  }
  
  .search-filter-section {
    padding: 16px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-input,
  .filter-select,
  .date-picker {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .section-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .pagination-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .pagination-controls {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 12px;
  }
  
  .user-list-section {
    padding: 16px;
  }
  
  .user-stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .batch-selection-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .selection-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .user-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  
  .user-actions .el-button {
    width: 100%;
    justify-content: center;
  }
  
  .custom-dialog {
    width: 90% !important;
    margin: 20px auto;
  }
  
  .custom-dialog .el-dialog__header,
  .custom-dialog .el-dialog__body,
  .custom-dialog .el-dialog__footer {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .admin-page {
    padding: 8px;
  }
  
  .user-list-section {
    padding: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .user-info-cell {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .user-avatar {
    align-self: flex-start;
  }
}
</style>

<style>
/* 全局样式，不使用scoped，确保直接应用到表格元素 */
#app.dark-theme .el-table,
#app.dark-theme .el-table__header-wrapper,
#app.dark-theme .el-table__body-wrapper,
#app.dark-theme .el-table__footer-wrapper {
  background-color: #1e293b !important;
  background-image: none !important;
}

#app.dark-theme .el-table th,
#app.dark-theme .el-table__header-wrapper th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  background-image: none !important;
}

#app.dark-theme .el-table td,
#app.dark-theme .el-table__body-wrapper td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  background-image: none !important;
}

#app.dark-theme .el-table tr:hover td,
#app.dark-theme .el-table__body-wrapper tr:hover td {
  background-color: #334155 !important;
  background-image: none !important;
}

#app.dark-theme .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #1e293b !important;
  background-image: none !important;
}

#app.dark-theme .el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: #334155 !important;
  background-image: none !important;
}

/* 确保用户表格容器也使用深色背景 */
#app.dark-theme .user-table-container {
  background-color: #1e293b !important;
  background-image: none !important;
}

/* 确保user-table类也使用深色背景 */
#app.dark-theme .user-table {
  background-color: #1e293b !important;
  background-image: none !important;
}
</style>
