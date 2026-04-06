<template>
  <div class="admin-page">
    <div class="page-card">
      <div class="page-header">
        <h3 class="page-title">数据备份与恢复</h3>
        <span class="page-subtitle">对平台数据进行备份或从备份恢复，建议定期备份</span>
      </div>
      <p class="section-desc">定期备份数据可以有效防止数据丢失，建议每周至少备份一次。</p>
      <el-row :gutter="24">
        <el-col :span="12">
          <div class="backup-card">
            <div class="backup-icon download">
              <i class="el-icon-download"></i>
              <div class="backup-icon-glow"></div>
            </div>
            <h4 class="backup-title">备份数据</h4>
            <p class="backup-desc">将当前数据库导出为备份文件，可下载到本地保存。</p>
            <el-button type="primary" icon="el-icon-download" @click="backupData">立即备份</el-button>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="backup-card">
            <div class="backup-icon upload">
              <i class="el-icon-upload2"></i>
              <div class="backup-icon-glow"></div>
            </div>
            <h4 class="backup-title">恢复数据</h4>
            <p class="backup-desc">从已保存的备份文件中恢复数据，请谨慎操作。</p>
            <el-button type="warning" icon="el-icon-upload2" @click="restoreData">选择文件恢复</el-button>
          </div>
        </el-col>
      </el-row>
      <div class="page-card backup-records" style="margin-top:24px">
        <div class="section-header">
          <h3 class="section-title">备份记录</h3>
          <el-button type="primary" icon="el-icon-refresh" @click="refreshBackupList">刷新记录</el-button>
        </div>
        <el-table :data="backupList" border stripe class="data-table">
          <el-table-column prop="name" label="备份名称" width="200" />
          <el-table-column prop="time" label="备份时间" width="180" />
          <el-table-column prop="size" label="大小" width="100" />
          <el-table-column label="操作" width="160">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="downloadBackup(scope.row.name)">下载</el-button>
              <el-button type="text" size="small" @click="restoreFromBackup(scope.row.name)">恢复</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="data-empty" v-if="!backupList.length">
          <div class="empty-icon"><i class="el-icon-folder-opened"></i></div>
          <p class="empty-text">暂无备份记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminBackup',
  data() {
    return {
      backupList: []
    }
  },
  mounted() {
    // 页面加载时刷新备份记录
    this.refreshBackupList();
  },
  methods: {
    // 备份数据
    async backupData() {
      try {
        const response = await this.$axios.post('http://localhost:8080/api/backup/create');
        if (response.code === 200) {
          this.$message.success('备份成功');
          this.refreshBackupList();
        } else {
          this.$message.error(response.message || '备份失败');
        }
      } catch (error) {
        this.$message.error('备份失败: ' + (error.message || '未知错误'));
      }
    },
    // 恢复数据
    async restoreData() {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.sql';
      input.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        this.$confirm(`确定要从文件 ${file.name} 恢复数据吗？此操作将覆盖当前数据，请谨慎操作。`, '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          try {
            const formData = new FormData();
            formData.append('file', file);
            const response = await this.$axios.post('http://localhost:8080/api/backup/restore', formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            });
            if (response.code === 200) {
              this.$message.success('恢复成功');
            } else {
              this.$message.error(response.message || '恢复失败');
            }
          } catch (error) {
            this.$message.error('恢复失败: ' + (error.message || '未知错误'));
          }
        }).catch(() => {
          // 取消操作
        });
      };
      input.click();
    },
    // 刷新备份记录
    async refreshBackupList() {
      try {
        const response = await this.$axios.get('http://localhost:8080/api/backup/list');
        if (response.code === 200) {
          this.backupList = response.data || [];
        } else {
          this.$message.error(response.message || '获取备份列表失败');
        }
      } catch (error) {
        this.$message.error('获取备份列表失败: ' + (error.message || '未知错误'));
      }
    },
    // 下载备份
    async downloadBackup(name) {
      try {
        window.open(`http://localhost:8080/api/backup/download/${name}`, '_blank');
      } catch (error) {
        this.$message.error('下载失败: ' + (error.message || '未知错误'));
      }
    },
    // 从备份恢复
    async restoreFromBackup(name) {
      this.$confirm(`确定要从备份 ${name} 恢复数据吗？此操作将覆盖当前数据，请谨慎操作。`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await this.$axios.post(`http://localhost:8080/api/backup/restore/${name}`);
          if (response.code === 200) {
            this.$message.success('恢复成功');
          } else {
            this.$message.error(response.message || '恢复失败');
          }
        } catch (error) {
          this.$message.error('恢复失败: ' + (error.message || '未知错误'));
        }
      }).catch(() => {
        // 取消操作
      });
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

/* 备份记录卡片 */
.backup-records {
  margin-top: 24px;
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

/* 区块标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
}

/* 配置描述 */
.section-desc {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 24px;
  font-weight: 500;
  line-height: 1.5;
}

/* 备份卡片 */
.backup-card {
  background: #334155;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.backup-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  border-color: rgba(59, 130, 246, 0.3);
}

/* 备份图标 */
.backup-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  margin: 0 auto 20px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.backup-icon:hover {
  transform: scale(1.1);
}

.backup-icon.download {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
}

.backup-icon.upload {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
}

/* 图标发光效果 */
.backup-icon-glow {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 24px;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: glow-pulse 2s infinite;
  z-index: -1;
}

/* 备份标题 */
.backup-title {
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 12px;
}

/* 备份描述 */
.backup-desc {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 24px;
  line-height: 1.5;
  font-weight: 500;
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
  padding: 10px 24px;
  font-size: 14px;
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

.el-button--warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
}

.el-button--warning:hover {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
  transform: translateY(-1px);
}

/* 按钮文本样式 */
.el-button--text {
  color: #94a3b8;
  transition: all 0.2s ease;
}

.el-button--text:hover {
  color: #3b82f6;
}

/* 加载状态 */
.el-loading-mask {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
}

.el-loading-spinner .path {
  stroke: #3b82f6;
}

/* 动画效果 */
@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.8;
  }
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
  
  .backup-card {
    padding: 24px;
  }
  
  .backup-icon {
    width: 64px;
    height: 64px;
    font-size: 24px;
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
  
  .backup-card {
    padding: 20px;
  }
  
  .backup-icon {
    width: 56px;
    height: 56px;
    font-size: 20px;
  }
  
  .backup-title {
    font-size: 16px;
  }
  
  .backup-desc {
    font-size: 13px;
  }
  
  .el-button {
    padding: 8px 20px;
    font-size: 13px;
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
