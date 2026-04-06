<template>
  <div class="admin-page">
    <div class="page-card">
      <div class="page-header">
        <h3 class="page-title">系统设置</h3>
        <span class="page-subtitle">管理平台基础信息、业务参数和安全配置</span>
        <div class="page-actions">
          <el-button type="primary" icon="el-icon-refresh" @click="loadConfigList">
            刷新配置
          </el-button>
        </div>
      </div>

      <!-- 快捷访问入口 -->
      <div class="quick-access">
        <h4 class="quick-access-title">常用设置</h4>
        <div class="quick-access-grid">
          <div class="quick-access-item" @click="activeTab = 'basic'">
            <div class="quick-access-icon">
              <i class="el-icon-home"></i>
            </div>
            <div class="quick-access-label">基础信息</div>
          </div>
          <div class="quick-access-item" @click="activeTab = 'business'">
            <div class="quick-access-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="quick-access-label">业务参数</div>
          </div>
          <div class="quick-access-item" @click="activeTab = 'security'">
            <div class="quick-access-icon">
              <i class="el-icon-lock"></i>
            </div>
            <div class="quick-access-label">安全配置</div>
          </div>
          <div class="quick-access-item" @click="activeTab = 'history'">
            <div class="quick-access-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="quick-access-label">变更历史</div>
          </div>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="config-tabs">
        <el-tab-pane label="基础信息" name="basic">
          <div class="section-header">
            <h4 class="section-title">网站基础信息</h4>
            <p class="section-desc">网站名称、LOGO、版权等展示信息。</p>
          </div>
          <el-form ref="basicForm" :model="basic" :rules="basicRules" label-width="120px" style="max-width:560px" class="config-form">
            <el-form-item label="网站名称" prop="siteName">
              <el-input v-model="basic.siteName" placeholder="如：创业者综合服务平台" />
            </el-form-item>
            <el-form-item label="LOGO 路径" prop="logo">
              <el-input v-model="basic.logo" placeholder="/static/logo.png" />
            </el-form-item>
            <el-form-item label="版权信息" prop="copyright">
              <el-input v-model="basic.copyright" placeholder="Copyright © 2025" />
            </el-form-item>
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="basic.contact" placeholder="选填" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicConfig">保存</el-button>
              <el-button @click="resetBasicConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="业务参数" name="business">
          <div class="section-header">
            <h4 class="section-title">业务流程参数</h4>
            <p class="section-desc">审核时效、有效期等业务相关配置。</p>
          </div>
          <el-form ref="businessForm" :model="business" :rules="businessRules" label-width="140px" style="max-width:560px" class="config-form">
            <el-form-item label="项目审核时效(天)" prop="projectAuditDays">
              <el-input-number v-model="business.projectAuditDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="服务审核时效(天)" prop="resourceAuditDays">
              <el-input-number v-model="business.resourceAuditDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="投资意向有效期(天)" prop="investmentValidDays">
              <el-input-number v-model="business.investmentValidDays" :min="7" :max="365" />
            </el-form-item>
            <el-form-item label="服务预约有效期(天)" prop="bookingValidDays">
              <el-input-number v-model="business.bookingValidDays" :min="7" :max="90" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBusinessConfig">保存</el-button>
              <el-button @click="resetBusinessConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="安全配置" name="security">
          <div class="section-header">
            <h4 class="section-title">安全参数设置</h4>
            <p class="section-desc">密码强度、令牌过期时间等安全配置。</p>
          </div>
          <el-form ref="securityForm" :model="security" :rules="securityRules" label-width="160px" style="max-width:560px" class="config-form">
            <el-form-item label="密码最少位数" prop="passwordStrength">
              <el-input-number v-model="security.passwordStrength" :min="6" :max="20" />
            </el-form-item>
            <el-form-item label="JWT 过期时间(小时)" prop="jwtExpireHours">
              <el-input-number v-model="security.jwtExpireHours" :min="1" :max="72" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSecurityConfig">保存</el-button>
              <el-button @click="resetSecurityConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="配置历史" name="history">
          <div class="section-header">
            <h4 class="section-title">配置变更历史</h4>
            <p class="section-desc">查看系统配置的变更记录和版本回溯。</p>
          </div>
          <el-table v-loading="historyLoading" :data="configHistory" border stripe class="data-table">
            <el-table-column prop="configKey" label="配置项" width="220" />
            <el-table-column prop="oldValue" label="旧值" min-width="120" />
            <el-table-column prop="newValue" label="新值" min-width="120" />
            <el-table-column prop="updatedBy" label="操作人" width="120" />
            <el-table-column prop="updatedAt" label="操作时间" width="180" />
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button size="small" type="primary" @click="revertConfig(scope.row)">
                  恢复
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!historyLoading && !configHistory.length" class="data-empty">
            <div class="empty-icon"><i class="el-icon-time"></i></div>
            <p class="empty-text">暂无配置变更历史</p>
          </div>
        </el-tab-pane>

        <el-tab-pane label="全部配置项" name="all">
          <div class="section-header">
            <h4 class="section-title">所有配置项</h4>
            <p class="section-desc">查看和管理所有系统配置项。</p>
          </div>
          <el-table v-loading="loading" :data="list" border stripe class="data-table">
            <el-table-column prop="configKey" label="配置项" width="220" />
            <el-table-column prop="configValue" label="值" min-width="120" />
            <el-table-column prop="description" label="说明" width="200" />
          </el-table>
          <div v-if="!loading && !list.length" class="data-empty">
            <div class="empty-icon"><i class="el-icon-setting"></i></div>
            <p class="empty-text">暂无配置数据</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { adminConfig } from '@/api/admin'
export default {
  name: 'AdminConfig',
  data() {
    return {
      activeTab: 'basic',
      loading: false,
      historyLoading: false,
      list: [],
      configHistory: [],
      basic: { siteName: '创业者综合服务平台', logo: '/static/logo.png', copyright: 'Copyright © 2025', contact: '' },
      business: { projectAuditDays: 7, resourceAuditDays: 7, investmentValidDays: 30, bookingValidDays: 15 },
      security: { passwordStrength: 8, jwtExpireHours: 2 },
      // 表单验证规则
      basicRules: {
        siteName: [
          { required: true, message: '请输入网站名称', trigger: 'blur' },
          { min: 2, max: 50, message: '网站名称长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        logo: [
          { required: true, message: '请输入 LOGO 路径', trigger: 'blur' }
        ],
        copyright: [
          { required: true, message: '请输入版权信息', trigger: 'blur' }
        ]
      },
      businessRules: {
        projectAuditDays: [
          { required: true, message: '请输入项目审核时效', trigger: 'blur' },
          { type: 'number', min: 1, max: 30, message: '项目审核时效在 1 到 30 天', trigger: 'blur' }
        ],
        resourceAuditDays: [
          { required: true, message: '请输入服务审核时效', trigger: 'blur' },
          { type: 'number', min: 1, max: 30, message: '服务审核时效在 1 到 30 天', trigger: 'blur' }
        ],
        investmentValidDays: [
          { required: true, message: '请输入投资意向有效期', trigger: 'blur' },
          { type: 'number', min: 7, max: 365, message: '投资意向有效期在 7 到 365 天', trigger: 'blur' }
        ],
        bookingValidDays: [
          { required: true, message: '请输入服务预约有效期', trigger: 'blur' },
          { type: 'number', min: 7, max: 90, message: '服务预约有效期在 7 到 90 天', trigger: 'blur' }
        ]
      },
      securityRules: {
        passwordStrength: [
          { required: true, message: '请输入密码最少位数', trigger: 'blur' },
          { type: 'number', min: 6, max: 20, message: '密码最少位数在 6 到 20 位', trigger: 'blur' }
        ],
        jwtExpireHours: [
          { required: true, message: '请输入 JWT 过期时间', trigger: 'blur' },
          { type: 'number', min: 1, max: 72, message: 'JWT 过期时间在 1 到 72 小时', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadConfigList()
    this.loadConfigHistory()
  },
  methods: {
    // 保存基础配置
    async saveBasicConfig() {
      this.$refs.basicForm.validate(async (valid) => {
        if (valid) {
          try {
            await adminConfig.set('siteName', this.basic.siteName)
            await adminConfig.set('logo', this.basic.logo)
            await adminConfig.set('copyright', this.basic.copyright)
            await adminConfig.set('contact', this.basic.contact)
            this.$message.success('基础配置已保存')
            this.loadConfigList()
            this.loadConfigHistory()
          } catch (error) {
            console.error('Save basic config error:', error)
            this.$message.error('保存失败，请稍后重试')
          }
        }
      })
    },
    // 重置基础配置
    resetBasicConfig() {
      this.basic = { siteName: '创业者综合服务平台', logo: '/static/logo.png', copyright: 'Copyright © 2025', contact: '' }
      this.$refs.basicForm.resetFields()
      this.$message.info('基础配置已重置')
    },
    // 保存业务配置
    async saveBusinessConfig() {
      this.$refs.businessForm.validate(async (valid) => {
        if (valid) {
          try {
            await adminConfig.set('projectAuditDays', this.business.projectAuditDays)
            await adminConfig.set('resourceAuditDays', this.business.resourceAuditDays)
            await adminConfig.set('investmentValidDays', this.business.investmentValidDays)
            await adminConfig.set('bookingValidDays', this.business.bookingValidDays)
            this.$message.success('业务配置已保存')
            this.loadConfigList()
            this.loadConfigHistory()
          } catch (error) {
            console.error('Save business config error:', error)
            this.$message.error('保存失败，请稍后重试')
          }
        }
      })
    },
    // 重置业务配置
    resetBusinessConfig() {
      this.business = { projectAuditDays: 7, resourceAuditDays: 7, investmentValidDays: 30, bookingValidDays: 15 }
      this.$refs.businessForm.resetFields()
      this.$message.info('业务配置已重置')
    },
    // 保存安全配置
    async saveSecurityConfig() {
      this.$refs.securityForm.validate(async (valid) => {
        if (valid) {
          try {
            await adminConfig.set('passwordStrength', this.security.passwordStrength)
            await adminConfig.set('jwtExpireHours', this.security.jwtExpireHours)
            this.$message.success('安全配置已保存')
            this.loadConfigList()
            this.loadConfigHistory()
          } catch (error) {
            console.error('Save security config error:', error)
            this.$message.error('保存失败，请稍后重试')
          }
        }
      })
    },
    // 重置安全配置
    resetSecurityConfig() {
      this.security = { passwordStrength: 8, jwtExpireHours: 2 }
      this.$refs.securityForm.resetFields()
      this.$message.info('安全配置已重置')
    },
    // 加载配置列表
    loadConfigList() {
      this.loading = true
      adminConfig.list().then(res => {
        this.list = res || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 加载配置历史
    loadConfigHistory() {
      this.historyLoading = true
      // 模拟配置历史数据，实际项目中应从API获取
      setTimeout(() => {
        this.configHistory = [
          {
            configKey: 'siteName',
            oldValue: '创业者服务平台',
            newValue: '创业者综合服务平台',
            updatedBy: 'admin',
            updatedAt: '2024-01-01 10:00:00'
          },
          {
            configKey: 'projectAuditDays',
            oldValue: '5',
            newValue: '7',
            updatedBy: 'admin',
            updatedAt: '2024-01-02 14:30:00'
          },
          {
            configKey: 'passwordStrength',
            oldValue: '6',
            newValue: '8',
            updatedBy: 'admin',
            updatedAt: '2024-01-03 09:15:00'
          }
        ]
        this.historyLoading = false
      }, 500)
    },
    // 恢复配置
    revertConfig(row) {
      this.$confirm(`确定要恢复 ${row.configKey} 到值 ${row.oldValue} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await adminConfig.set(row.configKey, row.oldValue)
          this.$message.success('配置恢复成功')
          this.loadConfigList()
          this.loadConfigHistory()
        } catch (error) {
          console.error('Revert config error:', error)
          this.$message.error('恢复失败，请稍后重试')
        }
      }).catch(() => {
        // 取消恢复
      })
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
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
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

.page-actions {
  display: flex;
  gap: 12px;
}

/* 快捷访问入口 */
.quick-access {
  background: #334155;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.quick-access-title {
  font-size: 16px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 16px;
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.quick-access-item {
  background: #1e293b;
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  min-height: 120px;
  justify-content: center;
}

.quick-access-item:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  border-color: #3b82f6;
}

.quick-access-icon {
  font-size: 32px;
  color: #3b82f6;
  transition: all 0.3s ease;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 50%;
}

.quick-access-item:hover .quick-access-icon {
  color: #ffffff;
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.2);
}

.quick-access-label {
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
  transition: all 0.3s ease;
  line-height: 1.2;
}

.quick-access-item:hover .quick-access-label {
  color: #ffffff;
  font-weight: 600;
}

/* 区块标题 */
.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 8px;
}

.section-desc {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 24px;
  font-weight: 500;
}

/* 标签页 */
.config-tabs {
  margin-top: 20px;
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

/* 配置描述 */
.section-desc {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 24px;
  font-weight: 500;
}

/* 配置表单 */
.config-form {
  background: #334155;
  border-radius: 12px;
  padding: 28px;
  border: 1px solid rgba(59, 130, 246, 0.2);
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 24px;
}

.el-form-item__label {
  color: #94a3b8;
  font-weight: 600;
  font-size: 14px;
}

.el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s ease;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
}

.el-input__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
  background: rgba(255, 255, 255, 0.1);
}

/* 输入数字样式 */
.el-input-number .el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  height: 40px;
  line-height: 40px;
}

.el-input-number__decrease,
.el-input-number__increase {
  background: #475569;
  border-color: rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  transition: all 0.3s ease;
}

.el-input-number__decrease:hover,
.el-input-number__increase:hover {
  background: #3b82f6;
  border-color: #3b82f6;
  transform: translateY(-1px);
}

/* 表单验证错误提示 */
.el-form-item__error {
  color: #ef4444;
  font-size: 12px;
  margin-top: 4px;
}

/* 表单按钮区域 */
.el-form-item:last-child {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* 数据表格 */
.data-table {
  background: transparent !important;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
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
  padding: 60px 24px;
  color: #94a3b8;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-top: 20px;
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
  
  .config-form {
    padding: 20px;
  }
  
  .el-tabs__item {
    font-size: 14px;
    padding: 10px 20px;
    margin-right: 16px;
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
  
  .config-form {
    padding: 16px;
  }
  
  .el-form-item__label {
    font-size: 13px;
  }
  
  .el-tabs__item {
    font-size: 13px;
    padding: 8px 16px;
    margin-right: 12px;
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
