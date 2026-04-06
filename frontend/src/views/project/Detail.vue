<template>
  <div class="project-detail-page" v-loading="loading">
    <template v-if="project">
      <!-- 项目头部信息 -->
      <div class="project-header" :style="{ backgroundImage: project.projectImage ? `url(${getImageUrl(project.projectImage)})` : '' }">
        <div class="project-header-container">
          <div class="project-header-content">
            <div class="project-header-left">
              <div class="project-status-badge" :class="project.status">
                {{ getStatusText(project.status) }}
              </div>
              <h1 class="project-title">{{ project.title }}</h1>
              <p class="project-subtitle">{{ project.subtitle || '创新项目，引领未来' }}</p>
              <div class="project-meta">
                <div class="meta-item">
                  <i class="el-icon-s-marketing meta-icon"></i>
                  <span class="meta-label">行业</span>
                  <span class="meta-value">{{ project.industry }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-s-order meta-icon"></i>
                  <span class="meta-label">阶段</span>
                  <span class="meta-value">{{ project.stage }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-coin meta-icon"></i>
                  <span class="meta-label">融资需求</span>
                  <span class="meta-value">{{ project.fundingNeeded }}万</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-s-finance meta-icon"></i>
                  <span class="meta-label">出让股权</span>
                  <span class="meta-value">{{ project.equityOffered }}%</span>
                </div>
              </div>
              <div class="project-stats">
                <div class="stat-item">
                  <i class="el-icon-view"></i>
                  <span>{{ project.viewCount }}</span>
                  <span class="stat-label">浏览</span>
                </div>
                <div class="stat-item">
                  <i class="el-icon-thumb"></i>
                  <span>{{ project.likeCount }}</span>
                  <span class="stat-label">点赞</span>
                </div>
                <div class="stat-item">
                  <i class="el-icon-time"></i>
                  <span>{{ formatDate(project.createdAt) }}</span>
                  <span class="stat-label">发布</span>
                </div>
              </div>
            </div>
            <div class="project-header-right">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="toggleFavorite"
                  :class="{ 'favorited': favorited }"
                >
                  <i :class="favorited ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                  {{ favorited ? '已收藏' : '收藏项目' }}
                </el-button>
                <el-button 
                  v-if="isInvestor" 
                  type="success" 
                  size="large" 
                  @click="showIntent = true"
                >
                  <i class="el-icon-s-flag"></i>
                  提交投资意向
                </el-button>
                <el-button 
                  type="warning" 
                  size="large" 
                  @click="showOrder = true"
                >
                  <i class="el-icon-s-order"></i>
                  发起订单
                </el-button>
                <el-button 
                  type="info" 
                  size="large" 
                  @click="showChat = true"
                >
                  <i class="el-icon-chat-dot-round"></i>
                  联系创始人
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目详情内容 -->
      <div class="project-content">
        <div class="content-container">
          <div class="content-left">
          <!-- 项目介绍 -->
          <el-card class="content-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-document"></i>
                <h3 class="card-title">项目介绍</h3>
              </div>
            </template>
            <div class="project-description" v-html="project.description" />
          </el-card>

          <!-- 市场分析 -->
          <el-card class="content-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-data-analysis"></i>
                <h3 class="card-title">市场分析</h3>
              </div>
            </template>
            <div class="market-analysis">
              <div class="analysis-item">
                <h4>市场规模</h4>
                <p>{{ project.marketSize || '该项目尚未提供市场规模分析' }}</p>
              </div>
              <div class="analysis-item">
                <h4>目标用户</h4>
                <p>{{ project.targetUsers || '该项目尚未提供目标用户分析' }}</p>
              </div>
              <div class="analysis-item">
                <h4>竞争优势</h4>
                <p>{{ project.competitiveAdvantage || '该项目尚未提供竞争优势分析' }}</p>
              </div>
            </div>
          </el-card>

          <!-- 项目进展 -->
          <el-card class="content-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-s-flag"></i>
                <h3 class="card-title">项目进展</h3>
              </div>
            </template>
            <div class="project-milestones">
              <div 
                v-if="(project.milestones || mockMilestones) && (project.milestones || mockMilestones).length"
                v-for="(milestone, index) in project.milestones || mockMilestones" 
                :key="milestone.id || index"
                class="milestone-item"
              >
                <div class="milestone-dot"></div>
                <div class="milestone-content">
                  <h4 class="milestone-title">{{ milestone.title }}</h4>
                  <p class="milestone-description">{{ milestone.description }}</p>
                  <span class="milestone-date">{{ milestone.date }}</span>
                </div>
              </div>
              <div v-else class="no-milestones">
                <p>暂无项目进展信息</p>
              </div>
            </div>
          </el-card>
        </div>

        <div class="content-right">
          <!-- 创始人信息 -->
          <el-card class="content-card founder-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-user"></i>
                <h3 class="card-title">创始人信息</h3>
              </div>
            </template>
            <div class="founder-info">
              <div class="founder-avatar">
                <img v-if="project.founderAvatar" :src="project.founderAvatar" class="founder-avatar-image" alt="创始人头像" />
                <span v-else>{{ (project.founderName || '创').charAt(0) }}</span>
              </div>
              <div class="founder-details">
                <h4 class="founder-name">{{ project.founderName || '创始人' }}</h4>
                <p class="founder-title">{{ project.founderTitle || 'CEO' }}</p>
                <p class="founder-bio">{{ project.founderBio || '拥有丰富的行业经验和创业经历' }}</p>
                <div class="founder-contact">
                  <div class="contact-item">
                    <i class="el-icon-phone"></i>
                    <span>{{ project.founderPhone || '*******1234' }}</span>
                  </div>
                  <div class="contact-item">
                    <i class="el-icon-message"></i>
                    <span>{{ project.founderEmail || 'founder@example.com' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 团队介绍 -->
          <el-card class="content-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-s-flag"></i>
                <h3 class="card-title">团队介绍</h3>
              </div>
            </template>
            <div class="team-members">
              <div 
                v-if="(project.teamMembers || teamMembers) && (project.teamMembers || teamMembers).length"
                v-for="(member, index) in project.teamMembers || teamMembers" 
                :key="member.id || index"
                class="team-member"
              >
                <div class="member-avatar">
                    <img v-if="member.avatar" :src="member.avatar" class="member-avatar-image" alt="团队成员头像" />
                    <span v-else>{{ (member.name || '团队').charAt(0) }}</span>
                  </div>
                <div class="member-info">
                  <h5 class="member-name">{{ member.name }}</h5>
                  <p class="member-role">{{ member.role }}</p>
                </div>
              </div>
              <div v-else class="no-team">
                <p>暂无团队成员信息</p>
              </div>
            </div>
          </el-card>

          <!-- 相关项目推荐 -->
          <el-card class="content-card">
            <template slot="header">
              <div class="card-header">
                <i class="el-icon-s-grid"></i>
                <h3 class="card-title">相关项目</h3>
              </div>
            </template>
            <div class="related-projects">
              <div 
                v-if="relatedProjects && relatedProjects.length"
                v-for="(item, index) in relatedProjects" 
                :key="item.id || index"
                class="related-item"
                @click="goToProject(item.id)"
              >
                <h5 class="related-title">{{ item.title }}</h5>
                <p class="related-info">{{ item.industry }} | {{ item.stage }} | {{ item.fundingNeeded }}万</p>
              </div>
              <div v-else class="no-related">
                <p>暂无相关项目推荐</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

      <!-- 提交投资意向对话框 -->
      <el-dialog 
        title="提交投资意向" 
        :visible.sync="showIntent" 
        width="600px"
        custom-class="intent-dialog"
      >
        <el-form ref="intentForm" :model="intentForm" :rules="intentRules" label-width="120px">
          <el-form-item label="投资金额(万)" prop="amount">
            <el-input-number 
              v-model="intentForm.amount" 
              :min="0" 
              :step="10" 
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="投资期限" prop="duration">
            <el-input 
              v-model="intentForm.duration" 
              placeholder="如 3年" 
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="投资条件/说明" prop="description">
            <el-input 
              v-model="intentForm.description" 
              type="textarea" 
              rows="4" 
              placeholder="请详细描述您的投资条件和要求"
            />
          </el-form-item>
          <el-form-item label="联系方式" prop="contactInfo">
            <el-input 
              v-model="intentForm.contactInfo" 
              placeholder="请留下您的联系方式"
            />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showIntent = false">取消</el-button>
          <el-button type="primary" @click="submitIntent">提交意向</el-button>
        </span>
      </el-dialog>

      <!-- 聊天对话窗口 -->
      <ChatDialog
        v-if="showChat"
        :visible.sync="showChat"
        title="与创始人聊天"
        :user-id="project.founderId || '1'"
        :user-name="project.founderName || '创始人'"
        :user-avatar="project.founderAvatar || ''"
      />

      <!-- 发起订单对话框 -->
      <el-dialog 
        title="发起订单" 
        :visible.sync="showOrder" 
        width="600px"
        custom-class="order-dialog"
      >
        <el-form ref="orderForm" :model="orderForm" :rules="orderRules" label-width="120px">
          <el-form-item label="订单标题" prop="title">
            <el-input 
              v-model="orderForm.title" 
              placeholder="请输入订单标题"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="数量" prop="quantity">
            <el-input-number 
              v-model="orderForm.quantity" 
              :min="1" 
              :step="1" 
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="价格" prop="amount">
            <el-input 
              v-model="orderForm.amount" 
              placeholder="请输入价格"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="交付时间" prop="deliveryTime">
            <el-date-picker 
              v-model="orderForm.deliveryTime" 
              type="datetime" 
              placeholder="选择交付时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="备注" prop="description">
            <el-input 
              v-model="orderForm.description" 
              type="textarea" 
              rows="4" 
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showOrder = false">取消</el-button>
          <el-button type="primary" @click="submitOrder">提交订单</el-button>
        </span>
      </el-dialog>

    </template>
  </div>
</template>

<script>
import { getDetail, toggleFavorite, isFavorited } from '@/api/project'
import { submit } from '@/api/investment'
import { create } from '@/api/order'
import { mapGetters } from 'vuex'
import ChatDialog from '@/components/ChatDialog.vue'
import './detail/style.css'
import { getImageUrl as getImageUrlUtil } from '@/utils/image'
export default {
  name: 'ProjectDetail',
  components: {
    ChatDialog
  },
  data() {
    return {
      loading: false,
      project: null,
      favorited: false,
      showIntent: false,
      showChat: false,
      showOrder: false,
      intentForm: { 
        amount: 0, 
        duration: '', 
        description: '',
        contactInfo: '' 
      },
      intentRules: {
        amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
        contactInfo: [{ required: true, message: '请留下联系方式', trigger: 'blur' }]
      },
      orderForm: {
        title: '',
        quantity: 1,
        amount: '',
        deliveryTime: '',
        description: ''
      },
      orderRules: {
        title: [{ required: true, message: '请输入订单标题', trigger: 'blur' }],
        quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
        amount: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        deliveryTime: [{ required: true, message: '请选择交付时间', trigger: 'change' }]
      },
      mockMilestones: [
        {
          title: '项目启动',
          description: '完成项目构思和初步规划，组建核心团队',
          date: '2024-01-15'
        },
        {
          title: '原型开发',
          description: '完成产品原型设计和初步开发',
          date: '2024-03-20'
        },
        {
          title: '市场测试',
          description: '进行小规模市场测试，收集用户反馈',
          date: '2024-05-10'
        },
        {
          title: '融资准备',
          description: '完成商业计划书和融资材料准备',
          date: '2024-07-01'
        }
      ],
      teamMembers: [],
      relatedProjects: []
    }
  },
  computed: {
    ...mapGetters('user', ['isInvestor'])
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const id = this.$route.params.id
        this.project = await getDetail(id)
        try {
          this.favorited = await isFavorited(id)
        } catch (_) {}
        // 获取相关项目推荐
        try {
          const response = await this.$http.get(`/project/${id}/related`)
          this.relatedProjects = response.data.data
        } catch (_) {}
        // 获取团队成员
        try {
          const response = await this.$http.get(`/project/${id}/team`)
          this.teamMembers = response.data.data
        } catch (_) {}
      } finally {
        this.loading = false
      }
    },
    async toggleFavorite() {
      await toggleFavorite(this.project.id)
      this.favorited = !this.favorited
      this.project.likeCount += this.favorited ? 1 : -1
      this.$message.success(this.favorited ? '已收藏' : '已取消')
    },
    submitIntent() {
      this.$refs.intentForm.validate(async valid => {
        if (!valid) return
        try {
          await submit({ 
            projectId: this.project.id, 
            amount: this.intentForm.amount, 
            duration: this.intentForm.duration, 
            description: this.intentForm.description,
            contactInfo: this.intentForm.contactInfo
          })
          this.$message.success('已提交投资意向')
          this.showIntent = false
        } catch (e) {
          this.$message.error(e.response?.data?.message || '提交失败')
        }
      })
    },
    submitOrder() {
      this.$refs.orderForm.validate(async valid => {
        if (!valid) return
        try {
          await create({
            resourceId: this.project.id,
            providerId: this.project.founderId || '1',
            title: this.orderForm.title,
            quantity: this.orderForm.quantity,
            amount: this.orderForm.amount,
            deliveryTime: this.orderForm.deliveryTime,
            description: this.orderForm.description
          })
          this.$message.success('订单已提交')
          this.showOrder = false
        } catch (e) {
          this.$message.error(e.response?.data?.message || '提交失败')
        }
      })
    },
    getStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'pending': '审核中',
        'published': '已发布',
        'rejected': '已拒绝',
        'closed': '已关闭'
      }
      return statusMap[status] || '未知状态'
    },
    formatDate(dateString) {
      if (!dateString) return '未知时间'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    goToProject(id) {
      this.$router.push(`/project/${id}`)
    },
    getImageUrl(imageName) {
      return getImageUrlUtil(imageName)
    }
  }
}
</script>
