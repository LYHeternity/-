<template>
  <div class="admin-page">
    <div class="page-header-section">
      <div class="page-header-content">
        <h1 class="page-title">角色权限管理</h1>
        <p class="page-subtitle">管理平台角色及可访问的菜单与操作权限</p>
      </div>
      <div class="page-header-actions">
        <el-button type="primary" @click="openAddRoleDialog" class="primary-action-button">
          <i class="el-icon-plus"></i> 新增角色
        </el-button>
      </div>
    </div>

    <div class="role-management-section">
      <div class="section-header">
        <h2 class="section-title">角色列表</h2>
        <div class="section-actions">
          <el-button type="primary" icon="el-icon-download" @click="doExport" class="action-button">
            导出角色
          </el-button>
        </div>
      </div>

      <!-- 搜索筛选区域 -->
      <div class="search-filter-section">
        <el-form inline class="search-form">
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="角色名称/代码" clearable class="search-input" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadRoles" class="search-button">
              <i class="el-icon-search"></i> 查询
            </el-button>
            <el-button @click="resetFilters" class="reset-button">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 角色表格 -->
      <div class="role-table-container">
        <el-table
          v-loading="loading"
          :data="roleList"
          style="width: 100%;"
          class="role-table"
          stripe
          :cell-style="{ backgroundColor: '#1e293b', color: '#f8fafc', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
          :header-cell-style="{ backgroundColor: '#334155', color: '#94a3b8', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }"
        >
          <el-table-column prop="name" label="角色名称" width="160" />
          <el-table-column prop="code" label="角色代码" width="140" />
          <el-table-column prop="desc" label="说明" min-width="240" />
          <el-table-column label="操作" min-width="360" align="right">
            <template slot-scope="scope">
              <div class="role-actions">
                <el-button type="primary" size="small" @click="assignPermissions(scope.row.code)" class="action-button primary">
                  <i class="el-icon-lock"></i> 分配权限
                </el-button>
                <el-button size="small" @click="viewUsers(scope.row.code)" class="action-button">
                  <i class="el-icon-user"></i> 查看用户
                </el-button>
                <el-button size="small" @click="editRole(scope.row.code)" class="action-button">
                  <i class="el-icon-edit"></i> 编辑
                </el-button>
                <el-button type="danger" size="small" @click="deleteRole(scope.row.code)" class="action-button">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空数据状态 -->
        <div v-if="!loading && !roleList.length" class="empty-state">
          <div class="empty-icon-container">
            <i class="el-icon-s-custom"></i>
          </div>
          <h3 class="empty-title">暂无角色数据</h3>
          <p class="empty-description">当前筛选条件下没有找到角色信息</p>
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

    <!-- 新增角色弹窗 -->
    <el-dialog 
      title="新增角色" 
      :visible.sync="showAddRoleDialog" 
      width="500px"
      class="custom-dialog"
      :modal="false"
      :z-index="1000"
    >
      <el-form :model="newRoleForm" :rules="roleFormRules" ref="newRoleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="newRoleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="newRoleForm.code" placeholder="请输入角色代码" />
        </el-form-item>
        <el-form-item label="角色说明" prop="desc">
          <el-input v-model="newRoleForm.desc" type="textarea" placeholder="请输入角色说明" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddRoleDialog = false">取消</el-button>
          <el-button type="primary" @click="addRole">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑角色弹窗 -->
    <el-dialog 
      title="编辑角色" 
      :visible.sync="showEditRoleDialog" 
      width="500px"
      class="custom-dialog"
      :modal="false"
      :z-index="1001"
    >
      <el-form :model="editRoleForm" :rules="roleFormRules" ref="editRoleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="editRoleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="editRoleForm.code" placeholder="请输入角色代码" disabled />
        </el-form-item>
        <el-form-item label="角色说明" prop="desc">
          <el-input v-model="editRoleForm.desc" type="textarea" placeholder="请输入角色说明" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditRoleDialog = false">取消</el-button>
          <el-button type="primary" @click="updateRole">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog 
      title="分配权限" 
      :visible.sync="showPermissionDialog" 
      width="800px"
      class="custom-dialog"
      :modal="false"
      :z-index="1002"
    >
      <div class="permission-assignment">
        <h4 class="permission-title">为角色 <span class="role-name">{{ currentRoleCode }}</span> 分配权限</h4>
        <el-tree
          :data="permissionTree"
          show-checkbox
          node-key="code"
          :default-expanded-keys="expandedKeys"
          :default-checked-keys="checkedKeys"
          @check-change="handlePermissionChange"
          class="permission-tree"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPermissionDialog = false">取消</el-button>
          <el-button type="primary" @click="savePermissions">保存权限</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色用户列表弹窗 -->
    <el-dialog 
      title="角色用户列表" 
      :visible.sync="showUserListDialog" 
      width="800px"
      class="custom-dialog"
      :modal="false"
      :z-index="1003"
    >
      <div class="user-list-assignment">
        <h4 class="user-list-title">角色 <span class="role-name">{{ currentRoleCode }}</span> 关联的用户</h4>
        <el-table
          v-loading="userListLoading"
          :data="userList"
          style="width: 100%;"
          class="user-list-table"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="realName" label="真实姓名" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="200" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.status === 'active'">启用</el-tag>
              <el-tag type="danger" v-else>禁用</el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 空数据状态 -->
        <div v-if="!userListLoading && !userList.length" class="empty-user-state">
          <div class="empty-icon-container">
            <i class="el-icon-user"></i>
          </div>
          <h3 class="empty-title">暂无关联用户</h3>
          <p class="empty-description">当前角色下没有关联的用户</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUserListDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AdminRoleManage',
  data() {
    return {
      loading: false,
      roleList: [],
      query: {
        keyword: ''
      },
      page: {
        current: 1,
        size: 10,
        total: 0
      },
      showAddRoleDialog: false,
      showEditRoleDialog: false,
      showPermissionDialog: false,
      showUserListDialog: false,
      newRoleForm: {
        name: '',
        code: '',
        desc: ''
      },
      editRoleForm: {
        name: '',
        code: '',
        desc: ''
      },
      currentRoleCode: '',
      permissionTree: [],
      expandedKeys: [],
      checkedKeys: [],
      userList: [],
      userListLoading: false,
      roleFormRules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 20, message: '角色名称长度在 2-20 之间', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入角色代码', trigger: 'blur' },
          { pattern: /^[a-z_]+$/, message: '角色代码只能包含小写字母和下划线', trigger: 'blur' }
        ],
        desc: [
          { required: true, message: '请输入角色说明', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadRoles()
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
    async loadRoles() {
      this.loading = true
      try {
        // 模拟API请求
        // const res = await adminRole.list(this.page.current, this.page.size, this.query.keyword)
        // this.roleList = res.records || []
        // this.page.total = res.total || 0
        
        // 模拟数据
        this.roleList = [
          { name: '超级管理员', code: 'admin', desc: '拥有全部管理权限' },
          { name: '内容审核员', code: 'auditor', desc: '项目与服务审核' },
          { name: '数据统计员', code: 'analyst', desc: '数据统计与报表查看' },
          { name: '运营专员', code: 'operator', desc: '日常运营管理' },
          { name: '客服专员', code: 'customer_service', desc: '客户服务与支持' }
        ]
        this.page.total = this.roleList.length
      } catch (error) {
        console.error('Load roles error:', error)
        this.$message.error('加载角色列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(size) {
      this.page.size = size
      this.loadRoles()
    },
    handleCurrentChange(current) {
      this.page.current = current
      this.loadRoles()
    },
    resetFilters() {
      this.query = { keyword: '' }
      this.page.current = 1
      this.loadRoles()
    },
    async doExport() {
      try {
        this.$message.success('导出成功')
      } catch (error) {
        console.error('Export error:', error)
        this.$message.error('导出失败')
      }
    },
    openAddRoleDialog() {
      this.newRoleForm = {
        name: '',
        code: '',
        desc: ''
      }
      this.$refs.newRoleFormRef && this.$refs.newRoleFormRef.resetFields()
      this.showAddRoleDialog = true
    },
    async addRole() {
      try {
        await this.$refs.newRoleFormRef.validate()
        // 模拟API请求
        // await adminRole.create(this.newRoleForm)
        this.$message.success('新增角色成功')
        this.showAddRoleDialog = false
        this.loadRoles()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Add role error:', error)
          this.$message.error('新增角色失败')
        }
      }
    },
    editRole(roleCode) {
      const role = this.roleList.find(r => r.code === roleCode)
      if (role) {
        this.editRoleForm = {
          name: role.name,
          code: role.code,
          desc: role.desc
        }
        this.$refs.editRoleFormRef && this.$refs.editRoleFormRef.resetFields()
        this.showEditRoleDialog = true
      }
    },
    async updateRole() {
      try {
        await this.$refs.editRoleFormRef.validate()
        // 模拟API请求
        // await adminRole.update(this.editRoleForm.code, this.editRoleForm)
        this.$message.success('更新角色成功')
        this.showEditRoleDialog = false
        this.loadRoles()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Update role error:', error)
          this.$message.error('更新角色失败')
        }
      }
    },
    async deleteRole(roleCode) {
      try {
        await this.$confirm('确定删除该角色？此操作不可恢复', '警告', { type: 'danger' })
        // 模拟API请求
        // await adminRole.delete(roleCode)
        this.$message.success('删除角色成功')
        this.loadRoles()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Delete role error:', error)
          this.$message.error('删除角色失败')
        }
      }
    },
    assignPermissions(roleCode) {
      this.currentRoleCode = roleCode
      // 模拟权限树数据
      this.permissionTree = [
        {
          label: '系统管理',
          code: 'system',
          children: [
            { label: '用户管理', code: 'user_manage' },
            { label: '角色管理', code: 'role_manage' },
            { label: '系统设置', code: 'system_setting' }
          ]
        },
        {
          label: '内容管理',
          code: 'content',
          children: [
            { label: '项目管理', code: 'project_manage' },
            { label: '服务管理', code: 'service_manage' },
            { label: '资源管理', code: 'resource_manage' }
          ]
        },
        {
          label: '审核管理',
          code: 'audit',
          children: [
            { label: '项目审核', code: 'project_audit' },
            { label: '服务审核', code: 'service_audit' }
          ]
        },
        {
          label: '数据统计',
          code: 'stats',
          children: [
            { label: '用户统计', code: 'user_stats' },
            { label: '项目统计', code: 'project_stats' }
          ]
        }
      ]
      this.expandedKeys = ['system', 'content', 'audit', 'stats']
      this.checkedKeys = ['user_manage', 'role_manage', 'project_manage', 'project_audit']
      this.showPermissionDialog = true
    },
    handlePermissionChange(data, checked, indeterminate) {
      console.log('Permission change:', data, checked, indeterminate)
    },
    async savePermissions() {
      try {
        // 模拟API请求
        // await adminRole.assignPermissions(this.currentRoleCode, this.checkedKeys)
        this.$message.success('权限分配成功')
        this.showPermissionDialog = false
      } catch (error) {
        console.error('Save permissions error:', error)
        this.$message.error('权限分配失败')
      }
    },
    async viewUsers(roleCode) {
      this.currentRoleCode = roleCode
      this.userListLoading = true
      try {
        // 模拟API请求
        // const res = await adminUser.getUsersByRole(roleCode)
        // this.userList = res || []
        
        // 模拟数据
        this.userList = [
          { id: 1, username: 'admin', realName: '管理员', email: 'admin@example.com', status: 'active' },
          { id: 2, username: 'user1', realName: '用户1', email: 'user1@example.com', status: 'active' },
          { id: 3, username: 'user2', realName: '用户2', email: 'user2@example.com', status: 'active' }
        ]
      } catch (error) {
        console.error('Load users by role error:', error)
        this.$message.error('加载用户列表失败')
        this.userList = []
      } finally {
        this.userListLoading = false
        this.showUserListDialog = true
      }
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

/* 角色管理区域 */
.role-management-section {
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

/* 角色表格容器 */
.role-table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  border-radius: 12px;
  background: #1e293b;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-sizing: border-box;
}

/* 为role-table类添加更具体的深色主题样式 */
.role-table {
  background-color: #1e293b !important;
}

.role-table th {
  background-color: #334155 !important;
  color: #94a3b8 !important;
}

.role-table td {
  background-color: #1e293b !important;
  color: #f8fafc !important;
}

.role-table tr:hover td {
  background-color: #334155 !important;
}

/* 角色操作按钮组 */
.role-actions {
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

/* 弹窗样式 */
.custom-dialog {
  border-radius: 16px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5) !important;
  z-index: 1000 !important;
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

/* 权限分配样式 */
.permission-assignment {
  padding: 16px;
}

.permission-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 20px;
}

.role-name {
  color: #3b82f6;
  font-weight: 700;
}

.permission-tree {
  background: #334155;
  border-radius: 8px;
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.permission-tree .el-tree-node__content {
  color: #f8fafc;
  height: 40px;
  line-height: 40px;
}

.permission-tree .el-tree-node.is-current > .el-tree-node__content {
  background-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.permission-tree .el-tree-node.is-current > .el-tree-node__content:hover {
  background-color: rgba(59, 130, 246, 0.3);
}

/* 用户列表样式 */
.user-list-assignment {
  padding: 16px;
}

.user-list-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 20px;
}

.user-list-table {
  background-color: #334155 !important;
  border-radius: 8px;
  overflow: hidden;
}

.user-list-table th {
  background-color: #475569 !important;
  color: #94a3b8 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.user-list-table td {
  background-color: #334155 !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.user-list-table tr:hover td {
  background-color: #475569 !important;
}

.empty-user-state {
  text-align: center;
  padding: 60px 24px;
  color: #94a3b8;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.empty-user-state .empty-icon-container {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
  color: #64748b;
}

.empty-user-state .empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #94a3b8;
  margin: 0 0 8px 0;
}

.empty-user-state .empty-description {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-title {
    font-size: 28px;
  }
  
  .search-input {
    width: 200px;
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
  
  .role-management-section {
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