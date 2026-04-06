<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="profile-header-section">
      <h2 class="profile-title">个人中心</h2>
      <p class="profile-subtitle">管理您的个人信息和账户设置</p>
    </div>

    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <!-- 头像和基本信息 -->
      <div class="profile-info-section">
        <div class="avatar-container">
          <div class="avatar-wrapper">
            <el-avatar :size="120" :src="form && form.avatar ? form.avatar : defaultAvatar" class="profile-avatar">
              <img :src="form && form.avatar ? form.avatar : defaultAvatar" alt="头像" />
            </el-avatar>
            <div class="avatar-edit-overlay">
              <el-button type="primary" size="small" class="avatar-upload-btn" @click="triggerFileInput">
                <i class="el-icon-camera"></i> 更换头像
              </el-button>
            </div>
          </div>
          <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display: none" />
          <div v-if="uploading" class="upload-progress">
            <el-progress :percentage="uploadProgress" :stroke-width="3" />
          </div>
        </div>

        <div class="basic-info">
          <h3 class="user-name">{{ form ? (form.realName || form.username) : '加载中...' }}</h3>
          <div class="user-role" v-if="form">
            <el-tag :type="getRoleType(form.role)" effect="dark" size="medium">
              {{ getRoleLabel(form.role) }}
            </el-tag>
          </div>
          <div class="user-stats" v-if="form">
            <div class="stat-item">
              <span class="stat-value">{{ userStats.projectCount || 0 }}</span>
              <span class="stat-label">项目</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.serviceCount || 0 }}</span>
              <span class="stat-label">服务</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.investmentCount || 0 }}</span>
              <span class="stat-label">投资意向</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.orderCount || 0 }}</span>
              <span class="stat-label">订单</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细信息表单 -->
      <div class="profile-form-section">
        <el-form v-if="form" ref="form" :model="form" label-width="120px" class="profile-form">
          <div class="form-row">
            <el-form-item label="用户名" class="form-item">
              <div class="form-value">{{ form.username }}</div>
            </el-form-item>
            <el-form-item label="注册时间" class="form-item">
              <div class="form-value">{{ formatDate(form.createdAt) }}</div>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="真实姓名" class="form-item">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" class="form-item">
              <el-input v-model="form.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="邮箱" class="form-item">
              <el-input v-model="form.email" placeholder="请输入电子邮箱" />
            </el-form-item>
            <el-form-item label="最后登录" class="form-item">
              <div class="form-value">{{ formatDate(form.lastLoginAt) }}</div>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="个人简介" class="form-item full-width">
              <el-input v-model="form.intro" type="textarea" rows="4" placeholder="请输入个人简介" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="个人标签" class="form-item full-width">
              <el-tag
                v-for="tag in form.tags || []"
                :key="tag"
                closable
                @close="removeTag(tag)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-model="newTag"
                placeholder="添加标签"
                @keyup.enter="addTag"
                class="tag-input"
              />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" @click="save" class="action-button primary">
              <i class="el-icon-check"></i> 保存更改
            </el-button>
            <el-button @click="showPwd = true" class="action-button secondary">
              <i class="el-icon-key"></i> 修改密码
            </el-button>
            <el-button @click="load" class="action-button secondary">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
        </el-form>
      </div>
    </div>



    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" :visible.sync="showPwd" width="450px" class="custom-dialog">
      <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="120px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showPwd = false">取消</el-button>
        <el-button type="primary" @click="changePwd">确定</el-button>
      </span>
    </el-dialog>


  </div>
</template>

<script>
import { getProfile, updateProfile, changePassword } from '@/api/user'
import { upload } from '@/api/file'
import { mapActions } from 'vuex'
export default {
  name: 'Profile',
  data() {
    return {
      form: null,
      showPwd: false,
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 8, message: '至少8位', trigger: 'blur' }],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.pwdForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      defaultAvatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20simple%20design&image_size=square',
      uploading: false,
      uploadProgress: 0,
      newTag: '',
      userStats: {
        projectCount: 0,
        serviceCount: 0,
        investmentCount: 0,
        orderCount: 0
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    ...mapActions('user', ['updateUser']),
    async load() {
      try {
        const data = await getProfile()
        // 确保tags字段格式正确，前端期望数组类型
        let tags = []
        if (data.tags) {
          if (typeof data.tags === 'string') {
            tags = data.tags.split(',').filter(tag => tag.trim())
          } else if (Array.isArray(data.tags)) {
            tags = data.tags
          }
        }
        this.form = { ...data, password: undefined, tags }
        // 模拟加载用户统计数据
        this.loadUserStats()
      } catch (error) {
        this.$message.error('加载个人信息失败')
        console.error('加载个人信息失败:', error)
      }
    },
    loadUserStats() {
      // 模拟加载用户统计数据
      setTimeout(() => {
        this.userStats = {
          projectCount: 5,
          serviceCount: 3,
          investmentCount: 8,
          orderCount: 12
        }
      }, 500)
    },
    async save() {
      try {
        // 确保tags字段格式正确，后端期望字符串类型
        const formData = { ...this.form }
        if (formData.tags && Array.isArray(formData.tags)) {
          formData.tags = formData.tags.join(',')
        }
        await updateProfile(formData)
        this.$message.success('保存成功')
        // 更新Vuex中的用户信息，确保用户管理页面同步更新
        this.updateUser({
          avatar: this.form.avatar,
          realName: this.form.realName,
          phone: this.form.phone,
          email: this.form.email
        })
      } catch (error) {
        this.$message.error('保存失败')
        console.error('保存失败:', error)
      }
    },
    changePwd() {
      this.$refs.pwdForm.validate(async valid => {
        if (!valid) return
        try {
          await changePassword(this.pwdForm)
          this.$message.success('密码已修改')
          this.showPwd = false
          this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
        } catch (e) {
          this.$message.error(e.response?.data?.message || '修改失败')
        }
      })
    },

    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (!file) return
      
      // 检查文件大小（限制为2MB）
      if (file.size > 2 * 1024 * 1024) {
        this.$message.error('文件大小不能超过2MB')
        return
      }
      
      // 检查文件类型
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
      if (!allowedTypes.includes(file.type)) {
        this.$message.error('仅支持JPG、PNG、GIF、WebP格式的图片')
        return
      }
      
      this.uploading = true
      this.uploadProgress = 0
      
      try {
        // 模拟上传进度
        const progressInterval = setInterval(() => {
          if (this.uploadProgress < 90) {
            this.uploadProgress += 10
          }
        }, 200)
        
        // 上传文件
        const url = await upload(file)
        
        // 清除进度定时器
        clearInterval(progressInterval)
        this.uploadProgress = 100
        
        // 更新头像URL
        this.form.avatar = url
        
        // 保存更新
        await this.save()
        
        this.$message.success('头像上传成功')
      } catch (error) {
        this.$message.error('头像上传失败')
        console.error('头像上传失败:', error)
      } finally {
        // 重置上传状态
        setTimeout(() => {
          this.uploading = false
          this.uploadProgress = 0
        }, 1000)
        
        // 清空文件输入
        event.target.value = ''
      }
    },
    getRoleType(role) {
      const roleTypes = {
        admin: 'warning',
        entrepreneur: 'primary',
        investor: 'success',
        provider: 'info'
      }
      return roleTypes[role] || 'info'
    },
    getRoleLabel(role) {
      const roleLabels = {
        admin: '管理员',
        entrepreneur: '创业者',
        investor: '投资人',
        provider: '服务商'
      }
      return roleLabels[role] || '用户'
    },
    formatDate(dateString) {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    addTag() {
      if (!this.newTag.trim()) return
      if (!this.form.tags) {
        this.form.tags = []
      }
      if (!this.form.tags.includes(this.newTag.trim())) {
        this.form.tags.push(this.newTag.trim())
      }
      this.newTag = ''
    },
    removeTag(tag) {
      if (this.form.tags) {
        this.form.tags = this.form.tags.filter(t => t !== tag)
      }
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.profile-container {
  padding: 20px;
  min-height: 100vh;
  background: #f8fafc;
}

/* 页面标题 */
.profile-header-section {
  text-align: center;
  margin-bottom: 32px;
  padding: 32px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.profile-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.profile-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* 个人信息卡片 */
.profile-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
}

/* 头像和基本信息 */
.profile-info-section {
  display: flex;
  align-items: center;
  gap: 40px;
  margin-bottom: 40px;
  padding-bottom: 32px;
  border-bottom: 2px solid #3b82f6;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: translateY(-4px);
}

.profile-avatar {
  border: 4px solid rgba(59, 130, 246, 0.2);
  transition: all 0.3s ease;
}

.profile-avatar:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 24px rgba(59, 130, 246, 0.3);
}

.avatar-edit-overlay {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.95);
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar-edit-overlay {
  opacity: 1;
  bottom: -12px;
}

.avatar-upload-btn {
  font-size: 12px;
  padding: 6px 12px;
  min-width: 100px;
}

.upload-progress {
  width: 100%;
  max-width: 220px;
  margin-top: 8px;
}

.upload-progress >>> .el-progress__bar {
  background-color: rgba(59, 130, 246, 0.1);
}

.upload-progress >>> .el-progress__bar__inner {
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
}

/* 基本信息 */
.basic-info {
  flex: 1;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.user-role {
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  min-width: 80px;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.stat-item:hover {
  background: rgba(59, 130, 246, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

/* 详细信息表单 */
.profile-form-section {
  margin-top: 24px;
}

.profile-form {
  background: #f8fafc;
  border-radius: 12px;
  padding: 32px;
  border: 1px solid #e2e8f0;
}

.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.form-item {
  flex: 1;
  min-width: 250px;
}

.form-item.full-width {
  flex: 100%;
  min-width: 100%;
}

.form-value {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 12px;
  color: #1e293b;
  font-weight: 500;
}

/* 标签样式 */
.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
}

.tag-input {
  width: 200px;
  margin-top: 8px;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  justify-content: flex-start;
}

.action-button {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-button.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: #ffffff;
}

.action-button.primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

.action-button.secondary {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.action-button.secondary:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-2px);
}



/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  color: #334155;
  font-weight: 500;
  font-size: 14px;
}

.el-form-item__content {
  color: #1e293b;
}

.el-input__inner {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  border-radius: 8px;
  padding: 10px 12px;
  transition: all 0.3s ease;
}

.el-input__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.el-textarea__inner {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  border-radius: 8px;
  padding: 10px 12px;
  resize: vertical;
  transition: all 0.3s ease;
}

.el-textarea__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 对话框样式 */
.custom-dialog {
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.15);
}

.custom-dialog .el-dialog__header {
  border-bottom: 1px solid #e2e8f0;
  padding: 24px;
}

.custom-dialog .el-dialog__title {
  color: #1e293b;
  font-size: 18px;
  font-weight: 600;
}

.custom-dialog .el-dialog__body {
  padding: 24px;
  color: #1e293b;
}

.custom-dialog .el-dialog__footer {
  border-top: 1px solid #e2e8f0;
  padding: 20px 24px;
  background: #f8fafc;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 消息提示 */
.el-message {
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.el-message--success {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.el-message--error {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

/* 标签样式 */
.el-tag {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  font-weight: 500;
}

.el-tag--success {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.el-tag--danger {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.el-tag--info {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.el-tag--warning {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.el-tag--primary {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  padding: 8px 16px;
}

.el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: #ffffff;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.el-button {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.el-button:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 12px;
  }
  
  .profile-header-section {
    padding: 24px 16px;
    margin-bottom: 24px;
  }
  
  .profile-title {
    font-size: 24px;
  }
  
  .profile-card {
    padding: 20px;
    margin-bottom: 16px;
  }
  
  .profile-info-section {
    flex-direction: column;
    text-align: center;
    gap: 24px;
    margin-bottom: 24px;
    padding-bottom: 24px;
  }
  
  .user-stats {
    justify-content: center;
    gap: 16px;
  }
  
  .stat-item {
    min-width: 70px;
    padding: 12px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 16px;
    margin-bottom: 16px;
  }
  
  .form-item {
    min-width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
    margin-top: 24px;
  }
  
  .action-button {
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
  
  .dialog-footer {
    flex-direction: column;
  }
  
  .dialog-footer .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .profile-header-section {
    padding: 20px 12px;
  }
  
  .profile-title {
    font-size: 20px;
  }
  
  .profile-card {
    padding: 16px;
  }
  
  .user-stats {
    gap: 12px;
  }
  
  .stat-item {
    min-width: 60px;
    padding: 10px;
  }
  
  .stat-value {
    font-size: 16px;
  }
  
  .profile-form {
    padding: 20px;
  }
}
</style>
