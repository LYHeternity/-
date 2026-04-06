<template>
  <div class="service-publish-container">
    <!-- 页面头部 -->
    <div class="publish-header">
      <div class="header-content">
        <h1 class="page-title">发布服务</h1>
        <p class="page-subtitle">创建并提交您的服务，为创业者提供专业支持</p>
      </div>
      <div class="header-actions">
        <el-button type="info" icon="el-icon-info" @click="showGuidelines">
          发布指南
        </el-button>
      </div>
    </div>

    <!-- 进度指示器 -->
    <div class="progress-indicator">
      <div 
        v-for="(step, index) in steps" 
        :key="index"
        class="progress-step"
        :class="{ 'active': currentStep === index, 'completed': currentStep > index }"
      >
        <div class="step-number">{{ index + 1 }}</div>
        <div class="step-content">
          <h3 class="step-title">{{ step.title }}</h3>
          <p class="step-description">{{ step.description }}</p>
        </div>
        <div class="step-line" v-if="index < steps.length - 1"></div>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="form-container">
      <el-form ref="form" :model="form" :rules="rules" label-width="140px" class="service-form">
        <!-- 基本信息 -->
        <template v-if="currentStep === 0">
          <div class="form-section">
            <h2 class="section-title">基本信息</h2>
            <p class="section-description">填写服务的基本信息，让创业者快速了解您的服务</p>
          </div>

          <el-form-item label="服务名称" prop="title" class="form-item">
            <el-input 
              v-model="form.title" 
              placeholder="请输入服务名称" 
              clearable 
              maxlength="100" 
              show-word-limit
              class="form-input"
            />
            <div class="form-hint">简洁明了的服务名称有助于创业者快速理解您的服务内容</div>
          </el-form-item>

          <el-form-item label="服务副标题" prop="subtitle" class="form-item">
            <el-input 
              v-model="form.subtitle" 
              placeholder="请输入服务副标题" 
              clearable 
              maxlength="200" 
              show-word-limit
              class="form-input"
            />
            <div class="form-hint">简短描述服务的核心价值和目标</div>
          </el-form-item>

          <el-form-item label="服务类型" prop="category" class="form-item">
            <el-select 
              v-model="form.category" 
              placeholder="请选择服务类型" 
              clearable
              class="form-select"
            >
              <el-option label="技术开发" value="技术开发" />
              <el-option label="法律咨询" value="法律咨询" />
              <el-option label="财务服务" value="财务服务" />
              <el-option label="市场营销" value="市场营销" />
              <el-option label="人力资源" value="人力资源" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>

          <el-form-item label="服务图片" class="form-item">
            <el-upload
              class="image-upload"
              :action="uploadUrl"
              :headers="{ 'Authorization': 'Bearer ' + token }"
              :on-success="handleImageUploadSuccess"
              :on-error="handleImageUploadError"
              :before-upload="beforeImageUpload"
              :auto-upload="false"
              ref="imageUpload"
            >
              <el-button size="small" type="primary">选择图片</el-button>
              <div slot="tip" class="form-hint">
                支持jpg、png格式，单个文件大小不超过5MB
              </div>
            </el-upload>
            <div v-if="form.resourceImage" class="image-preview">
              <el-image
                :src="form.resourceImage"
                fit="cover"
                class="preview-image"
              />
              <el-button size="small" type="danger" @click="removeImage">删除图片</el-button>
            </div>
          </el-form-item>

          <el-form-item label="服务标签" class="form-item">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              class="service-tag"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-model="inputTag"
              placeholder="输入标签并按Enter添加"
              @keyup.enter="addTag"
              class="tag-input"
            />
            <div class="form-hint">添加相关标签，提高服务曝光率</div>
          </el-form-item>

          <div class="form-section">
            <h2 class="section-title">服务商信息</h2>
            <p class="section-description">填写服务商的基本信息，增强服务的可信度</p>
          </div>

          <el-form-item label="服务商名称" prop="providerName" class="form-item">
            <el-input 
              v-model="form.providerName" 
              placeholder="请输入服务商名称" 
              clearable 
              maxlength="100" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="服务商等级" prop="providerLevel" class="form-item">
            <el-select 
              v-model="form.providerLevel" 
              placeholder="请选择服务商等级" 
              clearable
              class="form-select"
            >
              <el-option label="认证服务商" value="认证服务商" />
              <el-option label="银牌服务商" value="银牌服务商" />
              <el-option label="金牌服务商" value="金牌服务商" />
              <el-option label="钻石服务商" value="钻石服务商" />
            </el-select>
          </el-form-item>

          <el-form-item label="从业年限" prop="yearsExperience" class="form-item">
            <el-input-number 
              v-model="form.yearsExperience" 
              :min="0" 
              :max="50" 
              placeholder="请输入从业年限"
              class="form-input-number"
            />
          </el-form-item>

          <el-form-item label="服务商描述" prop="providerDescription" class="form-item">
            <el-input 
              v-model="form.providerDescription" 
              type="textarea" 
              :rows="4" 
              placeholder="请简要介绍服务商的背景和优势" 
              maxlength="500" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="服务商头像" class="form-item">
            <el-upload
              class="image-upload"
              :action="uploadUrl"
              :headers="{ 'Authorization': 'Bearer ' + token }"
              :on-success="handleProviderAvatarUploadSuccess"
              :on-error="handleImageUploadError"
              :before-upload="beforeImageUpload"
              :auto-upload="false"
              ref="providerAvatarUpload"
            >
              <el-button size="small" type="primary">选择头像</el-button>
              <div slot="tip" class="form-hint">
                支持jpg、png格式，单个文件大小不超过2MB
              </div>
            </el-upload>
            <div v-if="form.providerAvatar" class="image-preview">
              <el-image
                :src="form.providerAvatar"
                fit="cover"
                class="preview-image"
                style="width: 100px; height: 100px; border-radius: 50%"
              />
              <el-button size="small" type="danger" @click="removeProviderAvatar">删除头像</el-button>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-button type="primary" @click="nextStep" class="action-button primary">
              下一步
            </el-button>
          </div>
        </template>

        <!-- 服务配置 -->
        <template v-if="currentStep === 1">
          <div class="form-section">
            <h2 class="section-title">服务配置</h2>
            <p class="section-description">详细配置您的服务参数和价格信息</p>
          </div>

          <el-form-item label="服务价格(元)" prop="price" class="form-item">
            <el-input-number 
              v-model="form.price" 
              :min="0" 
              :max="999999" 
              placeholder="请输入服务价格"
              class="form-input-number"
            />
          </el-form-item>

          <el-form-item label="服务周期" prop="serviceCycle" class="form-item">
            <el-input 
              v-model="form.serviceCycle" 
              placeholder="如 30天、1个月" 
              clearable
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="服务版本" class="form-item">
            <el-input 
              v-model="form.version" 
              placeholder="如 v1.0.0" 
              clearable
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="部署环境" class="form-item">
            <el-select 
              v-model="form.deploymentEnv" 
              placeholder="请选择部署环境" 
              clearable
              class="form-select"
            >
              <el-option label="本地部署" value="local" />
              <el-option label="云端部署" value="cloud" />
              <el-option label="混合部署" value="hybrid" />
            </el-select>
          </el-form-item>

          <div class="form-actions">
            <el-button @click="prevStep" class="action-button">
              上一步
            </el-button>
            <el-button type="primary" @click="nextStep" class="action-button primary">
              下一步
            </el-button>
          </div>
        </template>

        <!-- 服务详情 -->
        <template v-if="currentStep === 2">
          <div class="form-section">
            <h2 class="section-title">服务详情</h2>
            <p class="section-description">详细描述您的服务内容、接口定义和依赖管理</p>
          </div>

          <el-form-item label="服务简介" prop="summary" class="form-item">
            <el-input 
              v-model="form.summary" 
              type="textarea" 
              :rows="3" 
              placeholder="请简要描述服务的核心价值和目标" 
              maxlength="200" 
              show-word-limit
              class="form-textarea"
            />
            <div class="form-hint">简洁明了的服务简介，让创业者快速了解服务价值</div>
          </el-form-item>

          <el-form-item label="服务详情" prop="description" class="form-item">
            <el-input 
              v-model="form.description" 
              type="textarea" 
              :rows="8" 
              placeholder="请详细描述服务的功能、优势和使用场景" 
              maxlength="2000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="接口定义" class="form-item">
            <el-input 
              v-model="form.apiDefinition" 
              type="textarea" 
              :rows="5" 
              placeholder="请描述服务提供的接口和使用方法" 
              maxlength="1000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="服务依赖" class="form-item">
            <el-input 
              v-model="form.dependencies" 
              type="textarea" 
              :rows="4" 
              placeholder="请说明服务的依赖项和环境要求" 
              maxlength="800" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <div class="form-section">
            <h2 class="section-title">服务详情配置</h2>
            <p class="section-description">详细描述服务内容、流程和交付标准</p>
          </div>

          <el-form-item label="服务内容" prop="serviceContent" class="form-item">
            <el-input 
              v-model="form.serviceContent" 
              type="textarea" 
              :rows="5" 
              placeholder="请详细描述服务的具体内容" 
              maxlength="1000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="服务流程" prop="serviceProcess" class="form-item">
            <el-input 
              v-model="form.serviceProcess" 
              type="textarea" 
              :rows="6" 
              placeholder="请描述服务的流程步骤，每个步骤占一行" 
              maxlength="1000" 
              show-word-limit
              class="form-textarea"
            />
            <div class="form-hint">例如：1. 需求沟通\n2. 方案确认\n3. 服务实施\n4. 交付验收\n5. 售后支持</div>
          </el-form-item>

          <el-form-item label="交付标准" prop="deliveryStandards" class="form-item">
            <el-input 
              v-model="form.deliveryStandards" 
              type="textarea" 
              :rows="4" 
              placeholder="请描述服务的交付标准" 
              maxlength="800" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="服务区域" prop="serviceArea" class="form-item">
            <el-input 
              v-model="form.serviceArea" 
              placeholder="请输入服务区域，如：全国、北京等" 
              clearable 
              maxlength="100" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="服务亮点" prop="serviceHighlights" class="form-item">
            <el-input 
              v-model="form.serviceHighlights" 
              type="textarea" 
              :rows="4" 
              placeholder="请描述服务的亮点和优势" 
              maxlength="800" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <div class="form-actions">
            <el-button @click="prevStep" class="action-button">
              上一步
            </el-button>
            <el-button type="primary" :loading="loading" @click="submit" class="action-button primary">
              提交审核
            </el-button>
          </div>
        </template>
      </el-form>
    </div>

    <!-- 发布指南弹窗 -->
    <el-dialog
      title="服务发布指南"
      :visible.sync="showGuidelinesDialog"
      width="800px"
      class="guidelines-dialog"
    >
      <div class="guidelines-content">
        <h3>发布服务的最佳实践</h3>
        <ul class="guidelines-list">
          <li><strong>服务名称</strong>：简洁明了，能准确反映服务核心内容</li>
          <li><strong>服务类型</strong>：选择最符合服务的分类</li>
          <li><strong>服务价格</strong>：合理定价，明确收费标准</li>
          <li><strong>服务周期</strong>：清晰说明服务提供的时间范围</li>
          <li><strong>服务详情</strong>：详细描述服务的功能、优势和使用场景</li>
          <li><strong>接口定义</strong>：明确服务提供的接口和使用方法</li>
          <li><strong>服务依赖</strong>：清晰说明服务的依赖项和环境要求</li>
        </ul>
        <p class="guidelines-note">
          提示：服务提交后将进入审核流程，审核通过后将在服务市场展示。
        </p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showGuidelinesDialog = false">我知道了</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { publish } from '@/api/resource'
import { upload } from '@/api/file'
export default {
  name: 'ServicePublish',
  data() {
    return {
      currentStep: 0,
      loading: false,
      showGuidelinesDialog: false,
      inputTag: '',
      uploadUrl: '/files/upload',
      token: '',
      steps: [
        { title: '基本信息', description: '服务名称、类型、标签等基本信息' },
        { title: '服务配置', description: '价格、周期、版本、部署环境等配置信息' },
        { title: '服务详情', description: '服务描述、接口定义、依赖管理等详细信息' }
      ],
      form: {
        title: '',
        subtitle: '',
        category: '',
        type: 'service',
        tags: [],
        resourceImage: '',
        price: 0,
        serviceCycle: '',
        version: '',
        deploymentEnv: '',
        summary: '',
        description: '',
        apiDefinition: '',
        dependencies: '',
        serviceContent: '',
        serviceProcess: '',
        deliveryStandards: '',
        serviceArea: '',
        serviceHighlights: '',
        providerName: '',
        providerLevel: '',
        providerAvatar: '',
        yearsExperience: 0,
        providerDescription: ''
      },
      rules: {
        title: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
        price: [{ required: true, message: '请输入服务价格', trigger: 'blur' }],
        summary: [{ required: true, message: '请输入服务简介', trigger: 'blur' }],
        description: [{ required: true, message: '请输入服务详情', trigger: 'blur' }],
        serviceContent: [{ required: true, message: '请输入服务内容', trigger: 'blur' }],
        serviceProcess: [{ required: true, message: '请输入服务流程', trigger: 'blur' }],
        deliveryStandards: [{ required: true, message: '请输入交付标准', trigger: 'blur' }],
        providerName: [{ required: true, message: '请输入服务商名称', trigger: 'blur' }],
        providerLevel: [{ required: true, message: '请选择服务商等级', trigger: 'change' }],
        yearsExperience: [{ required: true, message: '请输入从业年限', trigger: 'blur' }],
        providerDescription: [{ required: true, message: '请输入服务商描述', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.token = localStorage.getItem('token')
  },
  methods: {
    showGuidelines() {
      this.showGuidelinesDialog = true
    },
    addTag() {
      if (this.inputTag && !this.form.tags.includes(this.inputTag)) {
        this.form.tags.push(this.inputTag)
        this.inputTag = ''
      }
    },
    removeTag(tag) {
      this.form.tags = this.form.tags.filter(t => t !== tag)
    },
    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--
      }
    },
    nextStep() {
      if (this.currentStep === 0) {
        this.$refs.form.validateField(['title', 'category', 'providerName', 'providerLevel', 'yearsExperience', 'providerDescription'], (err) => {
          if (!err) this.currentStep = 1
        })
      } else if (this.currentStep === 1) {
        this.$refs.form.validateField(['price'], (err) => {
          if (!err) this.currentStep = 2
        })
      }
    },
    beforeImageUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isJPG) {
        this.$message.error('只支持jpg、png格式的图片')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB')
        return false
      }

      // 手动上传
      this.uploadImage(file)
      return false
    },
    async uploadImage(file) {
      try {
        const response = await upload(file)
        this.form.resourceImage = response.data.url
        this.$message.success('图片上传成功')
      } catch (error) {
        this.$message.error('图片上传失败，请重试')
        console.error('Image upload error:', error)
      }
    },
    handleImageUploadSuccess(response) {
      this.form.resourceImage = response.data.url
      this.$message.success('图片上传成功')
    },
    handleImageUploadError() {
      this.$message.error('图片上传失败，请重试')
    },
    removeImage() {
      this.form.resourceImage = ''
      this.$message.success('图片已删除')
    },
    handleProviderAvatarUploadSuccess(response) {
      this.form.providerAvatar = response.data.url
      this.$message.success('头像上传成功')
    },
    removeProviderAvatar() {
      this.form.providerAvatar = ''
      this.$message.success('头像已删除')
    },
    async submit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          // 将标签数组转换为字符串
          const formData = { ...this.form }
          if (Array.isArray(formData.tags)) {
            formData.tags = formData.tags.join(',')
          }
          await publish(formData)
          this.$message.success('服务已提交，等待审核')
          this.$router.push('/my-services')
        } catch (e) {
          this.$message.error(e.response?.data?.message || e.message || '提交失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.service-publish-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background: #f8fafc;
  color: #1e293b;
}

/* 页面头部 */
.publish-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 2px solid #3b82f6;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 进度指示器 */
.progress-indicator {
  display: flex;
  justify-content: space-between;
  margin-bottom: 48px;
  position: relative;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
  z-index: 1;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.progress-step.active .step-number {
  background: #3b82f6;
  color: #fff;
  border-color: #60a5fa;
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.3);
}

.progress-step.completed .step-number {
  background: #10b981;
  color: #fff;
  border-color: #34d399;
}

.step-content {
  text-align: center;
  max-width: 200px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

.progress-step.active .step-title {
  color: #1e293b;
}

.progress-step.completed .step-title {
  color: #10b981;
}

.step-description {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

.step-line {
  position: absolute;
  top: 20px;
  left: 50%;
  width: 100%;
  height: 2px;
  background: #e2e8f0;
  z-index: -1;
}

.progress-step.completed .step-line {
  background: #10b981;
}

/* 表单容器 */
.form-container {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

/* 表单部分 */
.form-section {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.section-description {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 表单项目 */
.form-item {
  margin-bottom: 24px;
}

.form-input,
.form-select,
.form-textarea,
.form-input-number {
  width: 100%;
  border-radius: 8px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  transition: all 0.3s ease;
  padding: 10px 12px;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus,
.form-input-number:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
}

/* 标签样式 */
.service-tag {
  margin-right: 8px;
  margin-bottom: 8px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.2);
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
}

.tag-input {
  margin-top: 12px;
  width: 100%;
  max-width: 300px;
}

.image-upload {
  margin-bottom: 16px;
}

.image-preview {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-image {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.action-button {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-button.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: #fff;
}

.action-button.primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

.action-button {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.action-button:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-2px);
}

/* 发布指南弹窗 */
.guidelines-dialog {
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.15);
}

.guidelines-dialog .el-dialog__header {
  border-bottom: 1px solid #e2e8f0;
  padding: 24px;
}

.guidelines-dialog .el-dialog__title {
  color: #1e293b;
  font-size: 18px;
  font-weight: 600;
}

.guidelines-dialog .el-dialog__body {
  padding: 24px;
  color: #1e293b;
}

.guidelines-content h3 {
  color: #1e293b;
  margin-bottom: 16px;
}

.guidelines-list {
  list-style: disc;
  padding-left: 24px;
  margin-bottom: 24px;
}

.guidelines-list li {
  margin-bottom: 8px;
  color: #64748b;
}

.guidelines-list li strong {
  color: #1e293b;
}

.guidelines-note {
  color: #94a3b8;
  font-style: italic;
  border-top: 1px solid #e2e8f0;
  padding-top: 16px;
  margin-top: 16px;
}

.guidelines-dialog .el-dialog__footer {
  border-top: 1px solid #e2e8f0;
  padding: 20px 24px;
  background: #f8fafc;
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

/* 表单样式 */
.el-form-item__label {
  color: #334155;
  font-weight: 500;
  font-size: 14px;
}

.el-form-item__content {
  color: #1e293b;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .service-publish-container {
    padding: 16px;
  }
  
  .publish-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-actions {
    align-self: flex-end;
  }
  
  .form-container {
    padding: 24px;
  }
  
  .progress-indicator {
    flex-direction: column;
    gap: 24px;
  }
  
  .progress-step {
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    gap: 16px;
  }
  
  .step-line {
    display: none;
  }
  
  .step-content {
    text-align: left;
    max-width: none;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .form-container {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
  }
  
  .el-form {
    label-width: 100px !important;
  }
  
  .tag-input {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .el-form {
    label-width: 80px !important;
  }
  
  .step-content {
    display: none;
  }
  
  .progress-step {
    justify-content: center;
  }
}
</style>
