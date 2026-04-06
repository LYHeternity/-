<template>
  <div class="project-publish-container">
    <!-- 页面头部 -->
    <div class="publish-header">
      <div class="header-content">
        <h1 class="page-title">发布项目</h1>
        <p class="page-subtitle">创建并提交您的创业项目，获取潜在投资者的关注</p>
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
      <el-form ref="form" :model="form" :rules="rules" label-width="140px" class="project-form">
        <!-- 基本信息 -->
        <template v-if="currentStep === 0">
          <div class="form-section">
            <h2 class="section-title">基本信息</h2>
            <p class="section-description">填写项目的基本信息，让投资者快速了解您的项目</p>
          </div>

          <el-form-item label="项目名称" prop="title" class="form-item">
            <el-input 
              v-model="form.title" 
              placeholder="请输入项目名称" 
              clearable 
              maxlength="100" 
              show-word-limit
              class="form-input"
            />
            <div class="form-hint">简洁明了的项目名称有助于投资者快速理解您的业务</div>
          </el-form-item>

          <el-form-item label="项目副标题" prop="subtitle" class="form-item">
            <el-input 
              v-model="form.subtitle" 
              placeholder="请输入项目副标题" 
              clearable 
              maxlength="200" 
              show-word-limit
              class="form-input"
            />
            <div class="form-hint">简短描述项目的核心价值和目标</div>
          </el-form-item>

          <el-form-item label="所属行业" prop="industry" class="form-item">
            <el-select 
              v-model="form.industry" 
              placeholder="请选择行业" 
              clearable
              class="form-select"
            >
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

          <el-form-item label="项目阶段" prop="stage" class="form-item">
            <el-select 
              v-model="form.stage" 
              placeholder="请选择项目阶段" 
              class="form-select"
            >
              <el-option label="想法阶段" value="idea" />
              <el-option label="种子期" value="seed" />
              <el-option label="天使轮" value="angel" />
              <el-option label="A轮" value="a-round" />
              <el-option label="B轮及以上" value="b-round-plus" />
            </el-select>
          </el-form-item>

          <el-form-item label="项目标签" class="form-item">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              class="project-tag"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-model="inputTag"
              placeholder="输入标签并按Enter添加"
              @keyup.enter="addTag"
              class="tag-input"
            />
            <div class="form-hint">添加相关标签，提高项目曝光率</div>
          </el-form-item>

          <el-form-item label="项目图片" class="form-item">
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
            <div v-if="form.projectImage" class="image-preview">
              <el-image
                :src="form.projectImage"
                fit="cover"
                class="preview-image"
              />
              <el-button size="small" type="danger" @click="removeImage">删除图片</el-button>
            </div>
          </el-form-item>

          <div class="form-section">
            <h2 class="section-title">创始人信息</h2>
            <p class="section-description">填写创始人的基本信息，增强项目的可信度</p>
          </div>

          <el-form-item label="创始人姓名" prop="founderName" class="form-item">
            <el-input 
              v-model="form.founderName" 
              placeholder="请输入创始人姓名" 
              clearable 
              maxlength="50" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="创始人职位" prop="founderTitle" class="form-item">
            <el-input 
              v-model="form.founderTitle" 
              placeholder="请输入创始人职位" 
              clearable 
              maxlength="50" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="创始人简介" prop="founderBio" class="form-item">
            <el-input 
              v-model="form.founderBio" 
              type="textarea" 
              :rows="3" 
              placeholder="请简要介绍创始人的背景和经验" 
              maxlength="300" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="创始人电话" prop="founderPhone" class="form-item">
            <el-input 
              v-model="form.founderPhone" 
              placeholder="请输入创始人电话" 
              clearable 
              maxlength="20" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="创始人邮箱" prop="founderEmail" class="form-item">
            <el-input 
              v-model="form.founderEmail" 
              placeholder="请输入创始人邮箱" 
              clearable 
              maxlength="100" 
              show-word-limit
              class="form-input"
            />
          </el-form-item>

          <el-form-item label="创始人头像" class="form-item">
            <el-upload
              class="image-upload"
              :action="uploadUrl"
              :headers="{ 'Authorization': 'Bearer ' + token }"
              :on-success="handleFounderAvatarUploadSuccess"
              :on-error="handleImageUploadError"
              :before-upload="beforeImageUpload"
              :auto-upload="false"
              ref="founderAvatarUpload"
            >
              <el-button size="small" type="primary">选择头像</el-button>
              <div slot="tip" class="form-hint">
                支持jpg、png格式，单个文件大小不超过2MB
              </div>
            </el-upload>
            <div v-if="form.founderAvatar" class="image-preview">
              <el-image
                :src="form.founderAvatar"
                fit="cover"
                class="preview-image"
                style="width: 100px; height: 100px; border-radius: 50%"
              />
              <el-button size="small" type="danger" @click="removeFounderAvatar">删除头像</el-button>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-button type="primary" @click="nextStep" class="action-button primary">
              下一步
            </el-button>
          </div>
        </template>

        <!-- 融资信息 -->
        <template v-if="currentStep === 1">
          <div class="form-section">
            <h2 class="section-title">融资信息</h2>
            <p class="section-description">详细说明您的融资需求和提供的回报</p>
          </div>

          <el-form-item label="融资需求(万元)" prop="fundingNeeded" class="form-item">
            <el-input-number 
              v-model="form.fundingNeeded" 
              :min="0" 
              :max="99999" 
              placeholder="请输入融资需求"
              class="form-input-number"
            />
          </el-form-item>

          <el-form-item label="出让股权(%)" prop="equityOffered" class="form-item">
            <el-input-number 
              v-model="form.equityOffered" 
              :min="0" 
              :max="100" 
              placeholder="请输入出让股权比例"
              class="form-input-number"
            />
          </el-form-item>

          <el-form-item label="融资用途" prop="fundingUsage" class="form-item">
            <el-input 
              v-model="form.fundingUsage" 
              type="textarea" 
              :rows="4" 
              placeholder="请详细说明融资资金的使用计划"
              maxlength="500" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="预期回报" class="form-item">
            <el-input 
              v-model="form.expectedReturn" 
              type="textarea" 
              :rows="3" 
              placeholder="请说明投资者的预期回报"
              maxlength="300" 
              show-word-limit
              class="form-textarea"
            />
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

        <!-- 项目详情 -->
        <template v-if="currentStep === 2">
          <div class="form-section">
            <h2 class="section-title">项目详情</h2>
            <p class="section-description">详细描述您的项目，包括产品、市场、团队等信息</p>
          </div>

          <el-form-item label="项目简介" prop="summary" class="form-item">
            <el-input 
              v-model="form.summary" 
              type="textarea" 
              :rows="3" 
              placeholder="请简要描述项目的核心价值和目标" 
              maxlength="200" 
              show-word-limit
              class="form-textarea"
            />
            <div class="form-hint">简洁明了的项目简介，让投资者快速了解项目价值</div>
          </el-form-item>

          <el-form-item label="项目描述" prop="description" class="form-item">
            <el-input 
              v-model="form.description" 
              type="textarea" 
              :rows="8" 
              placeholder="请详细描述项目的产品功能、技术优势、商业模式等" 
              maxlength="2000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="市场规模" prop="marketSize" class="form-item">
            <el-input 
              v-model="form.marketSize" 
              type="textarea" 
              :rows="3" 
              placeholder="请描述目标市场的规模和增长趋势" 
              maxlength="500" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="目标用户" prop="targetUsers" class="form-item">
            <el-input 
              v-model="form.targetUsers" 
              type="textarea" 
              :rows="3" 
              placeholder="请描述目标用户群体和需求" 
              maxlength="500" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="市场分析" class="form-item">
            <el-input 
              v-model="form.marketAnalysis" 
              type="textarea" 
              :rows="5" 
              placeholder="请分析目标市场规模、增长趋势、竞争格局等" 
              maxlength="1000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="竞争优势" class="form-item">
            <el-input 
              v-model="form.competitiveAdvantage" 
              type="textarea" 
              :rows="4" 
              placeholder="请说明项目的核心竞争优势" 
              maxlength="800" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="发展规划" class="form-item">
            <el-input 
              v-model="form.developmentPlan" 
              type="textarea" 
              :rows="5" 
              placeholder="请说明项目的短期和长期发展规划" 
              maxlength="1000" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="团队介绍" class="form-item">
            <el-input 
              v-model="form.teamIntroduction" 
              type="textarea" 
              :rows="4" 
              placeholder="请介绍核心团队成员及其背景" 
              maxlength="800" 
              show-word-limit
              class="form-textarea"
            />
          </el-form-item>

          <div class="form-section">
            <h2 class="section-title">项目进展</h2>
            <p class="section-description">添加项目的重要里程碑和进展情况</p>
          </div>

          <div v-for="(milestone, index) in form.milestones" :key="index" class="milestone-form">
            <div class="milestone-header">
              <h3>里程碑 {{ index + 1 }}</h3>
              <el-button type="danger" size="small" @click="removeMilestone(index)">删除</el-button>
            </div>
            <el-form-item :label="'标题'" :prop="`milestones.${index}.title`" class="form-item">
              <el-input 
                v-model="milestone.title" 
                placeholder="请输入里程碑标题" 
                clearable 
                maxlength="100" 
                show-word-limit
                class="form-input"
              />
            </el-form-item>
            <el-form-item :label="'描述'" :prop="`milestones.${index}.description`" class="form-item">
              <el-input 
                v-model="milestone.description" 
                type="textarea" 
                :rows="3" 
                placeholder="请输入里程碑描述" 
                maxlength="300" 
                show-word-limit
                class="form-textarea"
              />
            </el-form-item>
            <el-form-item :label="'日期'" :prop="`milestones.${index}.date`" class="form-item">
              <el-date-picker 
                v-model="milestone.date" 
                type="date" 
                placeholder="选择日期" 
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <el-button type="primary" size="small" @click="addMilestone" class="add-milestone-btn">
            <i class="el-icon-plus"></i> 添加里程碑
          </el-button>

          <div class="form-section">
            <h2 class="section-title">团队成员</h2>
            <p class="section-description">添加核心团队成员信息，展示团队实力</p>
          </div>

          <div v-for="(member, index) in form.teamMembers" :key="index" class="team-member-form">
            <div class="team-member-header">
              <h3>团队成员 {{ index + 1 }}</h3>
              <el-button type="danger" size="small" @click="removeTeamMember(index)">删除</el-button>
            </div>
            <el-form-item :label="'姓名'" :prop="`teamMembers.${index}.name`" class="form-item">
              <el-input 
                v-model="member.name" 
                placeholder="请输入成员姓名" 
                clearable 
                maxlength="50" 
                show-word-limit
                class="form-input"
              />
            </el-form-item>
            <el-form-item :label="'职位'" :prop="`teamMembers.${index}.role`" class="form-item">
              <el-input 
                v-model="member.role" 
                placeholder="请输入成员职位" 
                clearable 
                maxlength="50" 
                show-word-limit
                class="form-input"
              />
            </el-form-item>
            <el-form-item :label="'头像'" class="form-item">
              <el-upload
                class="image-upload"
                :action="uploadUrl"
                :headers="{ 'Authorization': 'Bearer ' + token }"
                :on-success="(response) => handleTeamMemberAvatarUploadSuccess(response, index)"
                :on-error="handleImageUploadError"
                :before-upload="beforeImageUpload"
                :auto-upload="false"
              >
                <el-button size="small" type="primary">选择头像</el-button>
              </el-upload>
              <div v-if="member.avatar" class="image-preview">
                <el-image
                  :src="member.avatar"
                  fit="cover"
                  class="preview-image"
                  style="width: 80px; height: 80px; border-radius: 50%"
                />
                <el-button size="small" type="danger" @click="removeTeamMemberAvatar(index)">删除头像</el-button>
              </div>
            </el-form-item>
          </div>

          <el-button type="primary" size="small" @click="addTeamMember" class="add-team-member-btn">
            <i class="el-icon-plus"></i> 添加团队成员
          </el-button>

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
      title="项目发布指南"
      :visible.sync="showGuidelinesDialog"
      width="800px"
      class="guidelines-dialog"
    >
      <div class="guidelines-content">
        <h3>发布项目的最佳实践</h3>
        <ul class="guidelines-list">
          <li><strong>项目名称</strong>：简洁明了，能准确反映项目核心业务</li>
          <li><strong>行业选择</strong>：选择最符合项目的行业分类</li>
          <li><strong>融资信息</strong>：明确融资需求和出让股权比例</li>
          <li><strong>项目描述</strong>：详细描述项目的产品功能、技术优势和商业模式</li>
          <li><strong>市场分析</strong>：提供数据支持的市场规模和增长趋势分析</li>
          <li><strong>竞争优势</strong>：清晰说明项目的核心竞争力</li>
          <li><strong>发展规划</strong>：提供合理的短期和长期发展目标</li>
          <li><strong>团队介绍</strong>：突出核心团队成员的专业背景和经验</li>
        </ul>
        <p class="guidelines-note">
          提示：项目提交后将进入审核流程，审核通过后将在项目大厅展示。
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
import { publish } from '@/api/project'
import { upload } from '@/api/file'
export default {
  name: 'ProjectPublish',
  data() {
    return {
      currentStep: 0,
      loading: false,
      showGuidelinesDialog: false,
      inputTag: '',
      uploadUrl: '/files/upload',
      token: '',
      steps: [
        { title: '基本信息', description: '项目名称、行业、阶段等基本信息' },
        { title: '融资信息', description: '融资需求、股权出让等融资相关信息' },
        { title: '项目详情', description: '项目描述、市场分析、团队介绍等详细信息' }
      ],
      form: {
        title: '',
        subtitle: '',
        industry: '',
        stage: 'idea',
        tags: [],
        projectImage: '',
        founderName: '',
        founderTitle: '',
        founderBio: '',
        founderPhone: '',
        founderEmail: '',
        founderAvatar: '',
        fundingNeeded: 0,
        equityOffered: 0,
        fundingUsage: '',
        expectedReturn: '',
        summary: '',
        description: '',
        marketSize: '',
        targetUsers: '',
        marketAnalysis: '',
        competitiveAdvantage: '',
        developmentPlan: '',
        teamIntroduction: '',
        teamMembers: [],
        milestones: []
      },
      rules: {
        title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        industry: [{ required: true, message: '请选择所属行业', trigger: 'change' }],
        stage: [{ required: true, message: '请选择项目阶段', trigger: 'change' }],
        summary: [{ required: true, message: '请输入项目简介', trigger: 'blur' }],
        description: [{ required: true, message: '请输入项目描述', trigger: 'blur' }],
        founderName: [{ required: true, message: '请输入创始人姓名', trigger: 'blur' }],
        founderTitle: [{ required: true, message: '请输入创始人职位', trigger: 'blur' }],
        founderBio: [{ required: true, message: '请输入创始人简介', trigger: 'blur' }],
        founderPhone: [{ required: true, message: '请输入创始人电话', trigger: 'blur' }],
        founderEmail: [{ required: true, message: '请输入创始人邮箱', trigger: 'blur' }, { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }]
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
        this.$refs.form.validateField(['title', 'industry', 'stage'], (err) => {
          if (!err) this.currentStep = 1
        })
      } else if (this.currentStep === 1) {
        this.currentStep = 2
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
        this.form.projectImage = response.data.url
        this.$message.success('图片上传成功')
      } catch (error) {
        this.$message.error('图片上传失败，请重试')
        console.error('Image upload error:', error)
      }
    },
    handleImageUploadSuccess(response) {
      this.form.projectImage = response.data.url
      this.$message.success('图片上传成功')
    },
    handleImageUploadError() {
      this.$message.error('图片上传失败，请重试')
    },
    handleFounderAvatarUploadSuccess(response) {
      this.form.founderAvatar = response.data.url
      this.$message.success('头像上传成功')
    },
    removeFounderAvatar() {
      this.form.founderAvatar = ''
      this.$message.success('头像已删除')
    },
    addTeamMember() {
      this.form.teamMembers.push({ name: '', role: '', avatar: '' })
    },
    removeTeamMember(index) {
      this.form.teamMembers.splice(index, 1)
    },
    handleTeamMemberAvatarUploadSuccess(response, index) {
      this.form.teamMembers[index].avatar = response.data.url
      this.$message.success('头像上传成功')
    },
    removeTeamMemberAvatar(index) {
      this.form.teamMembers[index].avatar = ''
      this.$message.success('头像已删除')
    },
    addMilestone() {
      this.form.milestones.push({ title: '', description: '', date: '' })
    },
    removeMilestone(index) {
      this.form.milestones.splice(index, 1)
    },
    removeImage() {
      this.form.projectImage = ''
      this.$message.success('图片已删除')
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
          // 处理团队成员数据
          if (Array.isArray(formData.teamMembers)) {
            formData.teamMembers = JSON.stringify(formData.teamMembers)
          }
          // 处理项目进展数据
          if (Array.isArray(formData.milestones)) {
            formData.milestones = JSON.stringify(formData.milestones)
          }
          await publish(formData)
          this.$message.success('项目已提交，等待审核')
          this.$router.push('/my-projects')
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
.project-publish-container {
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
.project-tag {
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

.team-member-form {
  margin-bottom: 32px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.team-member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.team-member-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.milestone-form {
  margin-bottom: 32px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.milestone-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.milestone-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.add-milestone-btn {
  margin: 16px 0 32px;
}

.add-team-member-btn {
  margin: 16px 0 32px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .project-publish-container {
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