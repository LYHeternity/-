<template>
  <div class="service-detail-page" v-loading="loading">
    <template v-if="resource">
      <!-- 服务头部信息 -->
      <div class="service-header" :style="{ backgroundImage: resource.resourceImage ? `url(${getImageUrl(resource.resourceImage)})` : '' }">
        <div class="header-container">
          <!-- 服务基本信息 -->
          <div class="service-basic-info">
            <div class="service-category-badge">
              {{ resource.category }}
            </div>
            <h1 class="service-title">{{ resource.title }}</h1>
            <p class="service-subtitle">{{ resource.subtitle || '专业服务，助力企业成长' }}</p>
          </div>
          
          <!-- 服务元数据和服务商信息 -->
          <div class="header-content">
            <!-- 服务元数据 -->
            <div class="service-meta">
              <div class="meta-item">
                <i class="el-icon-price-tag meta-icon"></i>
                <span class="meta-label">价格</span>
                <span class="meta-value price">¥{{ resource.price }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-star-on meta-icon"></i>
                <span class="meta-label">评分</span>
                <span class="meta-value rating">{{ resource.rating || '暂无评分' }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-s-comment meta-icon"></i>
                <span class="meta-label">评价</span>
                <span class="meta-value">{{ resource.reviewCount || 0 }}条</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-time meta-icon"></i>
                <span class="meta-label">发布时间</span>
                <span class="meta-value">{{ formatDate(resource.createdAt) }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-view meta-icon"></i>
                <span class="meta-label">浏览量</span>
                <span class="meta-value">{{ resource.viewCount || 0 }}</span>
              </div>
            </div>
            
            <!-- 服务商信息 -->
            <div class="provider-info">
              <div class="provider-avatar">
                <img v-if="resource.providerAvatar" :src="resource.providerAvatar" class="provider-avatar-image" alt="服务商头像" />
                <span v-else>{{ (resource.providerName || '服').charAt(0) }}</span>
              </div>
              <div class="provider-details">
                <h4 class="provider-name">{{ resource.providerName }}</h4>
                <p class="provider-level">{{ resource.providerLevel || '认证服务商' }}</p>
                <div class="provider-stats">
                  <span class="stat-item">{{ resource.serviceCount || 0 }}服务</span>
                  <span class="stat-item">{{ resource.completionRate || 100 }}%完成率</span>
                  <span class="stat-item">{{ resource.providerRating || 4.8 }}分</span>
                </div>
                <button class="provider-profile-btn" @click="viewProviderProfile">
                  <i class="el-icon-user"></i> 查看服务商资料
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 服务详情内容 -->
      <div class="service-content">
        <div class="content-container">
          <!-- 左侧主要内容 -->
          <div class="content-left">
            <!-- 服务介绍 -->
            <el-card class="content-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-document"></i>
                  <h3 class="card-title">服务介绍</h3>
                </div>
              </template>
              <div class="service-description" v-html="resource.description || '<p>专业服务，助力企业成长</p><p>我们提供高质量的服务，满足您的业务需求</p>'" />
            </el-card>

            <!-- 服务详情 -->
            <el-card class="content-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-s-grid"></i>
                  <h3 class="card-title">服务详情</h3>
                </div>
              </template>
              <div class="service-details">
                <!-- 服务内容 -->
                <div class="detail-section">
                  <h4 class="section-title">服务内容</h4>
                  <div class="section-content">
                    <p v-if="resource.serviceContent">{{ resource.serviceContent }}</p>
                    <ul v-else>
                      <li>• 提供专业的咨询服务</li>
                      <li>• 定制化解决方案</li>
                      <li>• 全程跟踪服务质量</li>
                      <li>• 提供详细的服务报告</li>
                    </ul>
                  </div>
                </div>
                
                <!-- 服务流程 -->
                <div class="detail-section">
                  <h4 class="section-title">服务流程</h4>
                  <div class="process-steps">
                    <div class="step-item" v-for="(step, index) in parseServiceProcess" :key="index">
                      <div class="step-number">{{ index + 1 }}</div>
                      <div class="step-content">
                        <h5>{{ step.title }}</h5>
                        <p>{{ step.description }}</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 交付标准 -->
                <div class="detail-section">
                  <h4 class="section-title">交付标准</h4>
                  <div class="delivery-standards">
                    <ul class="standards-list">
                      <li class="standard-item" v-for="(standard, index) in parseDeliveryStandards" :key="index">
                        <i class="el-icon-check-circle"></i>
                        <span>{{ standard }}</span>
                      </li>
                    </ul>
                  </div>
                </div>
                
                <!-- 服务保障 -->
                <div class="detail-section">
                  <h4 class="section-title">服务保障</h4>
                  <div class="guarantee-grid">
                    <div class="guarantee-item">
                      <div class="guarantee-icon">
                        <i class="el-icon-shield"></i>
                      </div>
                      <div class="guarantee-content">
                        <h5>质量保证</h5>
                        <p>服务质量不符合要求可申请退款</p>
                      </div>
                    </div>
                    <div class="guarantee-item">
                      <div class="guarantee-icon">
                        <i class="el-icon-clock"></i>
                      </div>
                      <div class="guarantee-content">
                        <h5>按时交付</h5>
                        <p>严格按照约定时间完成服务</p>
                      </div>
                    </div>
                    <div class="guarantee-item">
                      <div class="guarantee-icon">
                        <i class="el-icon-headset"></i>
                      </div>
                      <div class="guarantee-content">
                        <h5>售后支持</h5>
                        <p>提供7天售后技术支持</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 用户评价 -->
            <el-card class="content-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-s-comment"></i>
                  <h3 class="card-title">用户评价</h3>
                  <span class="review-count">({{ resource.reviewCount || 0 }}条)</span>
                  <el-button type="primary" size="small" @click="showReviewForm = true" class="review-btn">
                    <i class="el-icon-edit"></i> 写评价
                  </el-button>
                </div>
              </template>
              <div class="user-reviews">
                <div 
                  v-for="(review, index) in safeReviews" 
                  :key="index"
                  class="review-item"
                >
                  <div class="review-header">
                    <div class="reviewer-info">
                      <div class="reviewer-avatar">
                        <img v-if="review.avatar" :src="review.avatar" class="reviewer-avatar-image" alt="用户头像" />
                        <span v-else>{{ (review.username || '用户').charAt(0) }}</span>
                      </div>
                      <div class="reviewer-details">
                        <h5 class="reviewer-name">{{ review.username }}</h5>
                        <p class="review-time">{{ formatDate(review.createdAt) }}</p>
                      </div>
                    </div>
                    <div class="review-rating">
                      <i class="el-icon-star-on" v-for="i in (review.rating || 5)" :key="i"></i>
                      <i class="el-icon-star-off" v-for="i in (5 - (review.rating || 5))" :key="i + 5"></i>
                    </div>
                  </div>
                  <div class="review-content">
                    {{ review.content }}
                  </div>
                  <div class="review-tags" v-if="review.tags && review.tags.length">
                    <span class="review-tag" v-for="(tag, tagIndex) in review.tags" :key="tagIndex">{{ tag }}</span>
                  </div>
                </div>
                <div v-if="!safeReviews.length" class="no-reviews">
                  <i class="el-icon-info"></i>
                  <p>暂无用户评价</p>
                  <p class="no-reviews-tip">成为第一个评价此服务的用户吧！</p>
                  <el-button type="primary" size="medium" @click="showReviewForm = true" class="write-review-btn">
                    <i class="el-icon-edit"></i> 立即评价
                  </el-button>
                </div>
              </div>
            </el-card>

            <!-- 评价表单对话框 -->
            <el-dialog 
              title="写评价" 
              :visible.sync="showReviewForm" 
              width="600px"
              custom-class="review-dialog"
            >
              <el-form ref="reviewForm" :model="reviewForm" :rules="reviewRules" label-width="80px">
                <el-form-item label="评分" prop="rating">
                  <div class="rating-stars">
                    <i 
                      v-for="i in 5" 
                      :key="i"
                      :class="i <= reviewForm.rating ? 'el-icon-star-on' : 'el-icon-star-off'"
                      @click="reviewForm.rating = i"
                      class="star-icon"
                    ></i>
                  </div>
                </el-form-item>
                <el-form-item label="评价内容" prop="content">
                  <el-input 
                    v-model="reviewForm.content" 
                    type="textarea" 
                    rows="4" 
                    placeholder="请输入您的评价内容"
                  />
                </el-form-item>
                <el-form-item label="标签">
                  <div class="review-tags-select">
                    <el-tag 
                      v-for="tag in reviewTags" 
                      :key="tag"
                      :class="{ 'active': reviewForm.tags.includes(tag) }"
                      @click="toggleReviewTag(tag)"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>
                </el-form-item>
              </el-form>
              <span slot="footer" class="dialog-footer">
                <el-button @click="showReviewForm = false">取消</el-button>
                <el-button type="primary" @click="submitReview">提交评价</el-button>
              </span>
            </el-dialog>

            <!-- 常见问题 -->
            <el-card class="content-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-question"></i>
                  <h3 class="card-title">常见问题</h3>
                </div>
              </template>
              <div class="faq-section">
                <el-collapse>
                  <el-collapse-item title="服务周期是多久？" name="1">
                    <div class="faq-content">
                      服务周期根据具体需求而定，一般为3-15个工作日，具体时间会在服务合同中明确约定。
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="如何保证服务质量？" name="2">
                    <div class="faq-content">
                      我们有严格的服务质量控制流程，每个服务环节都有专业人员负责，确保服务质量符合标准。
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="可以提供发票吗？" name="3">
                    <div class="faq-content">
                      是的，我们可以提供正规增值税发票，您在预约服务时可以注明开票需求。
                    </div>
                  </el-collapse-item>
                  <el-collapse-item title="如何修改服务需求？" name="4">
                    <div class="faq-content">
                      在服务开始前，您可以随时联系服务商修改需求；服务开始后，重大需求变更可能会影响服务周期和价格。
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-card>
          </div>

          <!-- 右侧操作和信息区域 -->
          <div class="content-right">
            <!-- 服务操作 -->
            <el-card class="content-card action-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-s-operation"></i>
                  <h3 class="card-title">服务操作</h3>
                </div>
              </template>
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="toggleFavorite"
                  :class="{ 'favorited': favorited }"
                  class="action-btn primary"
                >
                  <i :class="favorited ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                  {{ favorited ? '已收藏' : '收藏服务' }}
                </el-button>
                <el-button 
                  v-if="isEntrepreneur" 
                  type="success" 
                  size="large" 
                  @click="showOrder = true"
                  class="action-btn success"
                >
                  <i class="el-icon-s-order"></i>
                  发起订单
                </el-button>
                <el-button 
                  type="info" 
                  size="large" 
                  @click="showChat = true"
                  class="action-btn info"
                >
                  <i class="el-icon-chat-dot-round"></i>
                  联系服务商
                </el-button>
              </div>
              
              <!-- 服务标签 -->
              <div class="service-tags">
                <span class="tag-item" v-for="(tag, index) in resource.tags || ['专业服务', '认证保障', '售后支持', '免费咨询']" :key="index">
                  {{ tag }}
                </span>
              </div>
              
              <!-- 服务亮点 -->
              <div class="service-highlights">
                <h4>服务亮点</h4>
                <ul>
                  <li v-for="(highlight, index) in parseServiceHighlights" :key="index">
                    <i class="el-icon-check"></i> {{ highlight }}
                  </li>
                </ul>
              </div>
            </el-card>

            <!-- 服务统计 -->
            <el-card class="content-card stats-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-data-line"></i>
                  <h3 class="card-title">服务数据</h3>
                </div>
              </template>
              <div class="service-stats">
                <div class="stat-row">
                  <span class="stat-label">服务次数</span>
                  <span class="stat-value">{{ resource.serviceCount || 0 }}</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">好评率</span>
                  <span class="stat-value">{{ resource.goodRate || 98 }}%</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">响应速度</span>
                  <span class="stat-value">{{ resource.responseTime || '1小时内' }}</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">服务区域</span>
                  <span class="stat-value">{{ resource.serviceArea || '全国' }}</span>
                </div>
              </div>
            </el-card>

            <!-- 相关服务推荐 -->
            <el-card class="content-card">
              <template slot="header">
                <div class="card-header">
                  <i class="el-icon-s-grid"></i>
                  <h3 class="card-title">相关服务</h3>
                </div>
              </template>
              <div class="related-services">
                <div 
                  v-for="(item, index) in relatedServices" 
                  :key="index"
                  class="related-item"
                  @click="goService(item.id)"
                  style="cursor: pointer;"
                >
                  <div class="related-header">
                    <h5 class="related-title">{{ item.title }}</h5>
                    <div class="related-price">¥{{ item.price }}</div>
                  </div>
                  <div class="related-info">
                    <span class="related-category">{{ item.category }}</span>
                    <div class="related-rating">
                      <i class="el-icon-star-on" v-for="i in Math.floor(item.rating || 4)" :key="i"></i>
                      <i class="el-icon-star-off" v-for="i in (5 - Math.floor(item.rating || 4))" :key="i + 5"></i>
                    </div>
                  </div>
                  <div class="related-provider">
                    <span class="provider-tag">服务商：{{ item.providerName }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>

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

      <!-- 聊天对话窗口 -->
      <ChatDialog
        v-if="showChat"
        :visible.sync="showChat"
        title="与服务商聊天"
        :user-id="resource.providerId || '1'"
        :user-name="resource.providerName || '服务商'"
        :user-avatar="resource.providerAvatar || ''"
      />


      <!-- 服务商资料对话框 -->
      <el-dialog 
        title="服务商资料" 
        :visible.sync="showProviderDialog" 
        width="700px"
        custom-class="provider-dialog"
      >
        <div class="provider-profile">
          <div class="provider-header">
            <div class="provider-avatar large">
              <img v-if="resource.providerAvatar" :src="resource.providerAvatar" class="provider-avatar-image" alt="服务商头像" />
              <span v-else>{{ (resource.providerName || '服').charAt(0) }}</span>
            </div>
            <div class="provider-info-detail">
              <h3>{{ resource.providerName }}</h3>
              <p class="provider-level">{{ resource.providerLevel || '认证服务商' }}</p>
              <div class="provider-stats-detail">
                <span class="stat-item">{{ resource.serviceCount || 0 }}服务</span>
                <span class="stat-item">{{ resource.completionRate || 100 }}%完成率</span>
                <span class="stat-item">{{ resource.providerRating || 4.8 }}分</span>
                <span class="stat-item">{{ resource.yearsExperience || 3 }}年经验</span>
              </div>
            </div>
          </div>
          <div class="provider-description">
            <h4>关于服务商</h4>
            <p>{{ resource.providerDescription || '专业的服务提供商，致力于为企业提供高质量的服务解决方案。' }}</p>
          </div>
          <div class="provider-services">
            <h4>热门服务</h4>
            <div class="service-list">
              <div class="service-item" v-for="(service, index) in providerServices" :key="index">
                <h5>{{ service.title }}</h5>
                <p class="service-price">¥{{ service.price }}</p>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="showProviderDialog = false">关闭</el-button>
        </span>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import { getDetail, toggleFavorite, isFavorited } from '@/api/resource'
import { create } from '@/api/order'
import { mapGetters } from 'vuex'
import ChatDialog from '@/components/ChatDialog.vue'
import { getImageUrl as getImageUrlUtil } from '@/utils/image'
export default {
  name: 'ServiceDetail',
  components: {
    ChatDialog
  },
  data() {
    return {
      loading: false,
      resource: null,
      showOrder: false,
      showChat: false,
      showProviderDialog: false,
      showReviewForm: false,
      favorited: false,
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
      reviewForm: {
        rating: 5,
        content: '',
        tags: []
      },
      reviewRules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
        content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
      },
      reviewTags: ['专业', '高效', '满意', '质量好', '价格合理', '服务周到', '响应及时', '性价比高'],
      serviceProcessSteps: [
        { title: '需求沟通', description: '了解您的具体需求，提供初步方案' },
        { title: '方案确认', description: '根据需求制定详细方案，确认服务内容' },
        { title: '服务实施', description: '专业团队按方案执行服务' },
        { title: '交付验收', description: '提交服务成果，进行验收' },
        { title: '售后支持', description: '提供7天售后技术支持' }
      ],
      deliveryStandards: [
        '符合国家相关标准和行业规范',
        '服务成果文档化，便于查阅和使用',
        '提供详细的操作指南和技术文档',
        '确保服务成果的可扩展性和可维护性'
      ],
      serviceHighlights: [
        '专业团队提供一对一服务',
        '定制化解决方案满足具体需求',
        '透明的服务流程和价格',
        '完善的售后服务体系',
        '快速响应，高效解决问题'
      ],
      providerServices: [
        { id: 1, title: '企业法律咨询服务', price: 2000 },
        { id: 2, title: '财务审计服务', price: 3000 },
        { id: 3, title: '市场调研服务', price: 1500 }
      ],
      mockReviews: [
        {
          username: '企业用户A',
          createdAt: new Date().toISOString(),
          rating: 5,
          content: '服务非常专业，团队响应速度快，解决了我们的实际问题，非常满意！',
          tags: ['专业', '高效', '满意']
        },
        {
          username: '创业者B',
          createdAt: new Date(Date.now() - 86400000).toISOString(),
          rating: 4,
          content: '服务质量不错，价格合理，就是交付时间稍微晚了一点，总体还是推荐的。',
          tags: ['质量好', '价格合理']
        }
      ],
      relatedServices: []
    }
  },
  computed: {
    ...mapGetters('user', ['isEntrepreneur']),
    parseServiceProcess() {
      if (!this.resource?.serviceProcess) return this.serviceProcessSteps
      try {
        const parsed = JSON.parse(this.resource.serviceProcess)
        return Array.isArray(parsed) && parsed.length ? parsed : this.serviceProcessSteps
      } catch (_) {
        return this.serviceProcessSteps
      }
    },
    parseDeliveryStandards() {
      if (!this.resource?.deliveryStandards) return this.deliveryStandards
      try {
        const parsed = JSON.parse(this.resource.deliveryStandards)
        return Array.isArray(parsed) && parsed.length ? parsed : this.deliveryStandards
      } catch (_) {
        return this.deliveryStandards
      }
    },
    parseServiceHighlights() {
      if (!this.resource?.serviceHighlights) return this.serviceHighlights
      try {
        const parsed = JSON.parse(this.resource.serviceHighlights)
        return Array.isArray(parsed) && parsed.length ? parsed : this.serviceHighlights
      } catch (_) {
        return this.serviceHighlights
      }
    },
    safeReviews() {
      if (Array.isArray(this.resource?.reviews) && this.resource.reviews.length) {
        return this.resource.reviews
      }
      return this.mockReviews
    }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const id = this.$route.params.id
        this.resource = await getDetail(id)
        // 模拟数据，实际项目中从API获取
        if (!this.resource) {
          this.resource = {
            id: id,
            title: '企业法律咨询服务',
            subtitle: '专业的企业法律顾问服务，为企业保驾护航',
            category: '法律服务',
            price: 2000,
            rating: 4.8,
            reviewCount: 2,
            viewCount: 234,
            createdAt: new Date().toISOString(),
            description: '<p>我们提供专业的企业法律咨询服务，包括但不限于：</p><ul><li>企业设立与变更法律咨询</li><li>合同起草与审查</li><li>劳动人事法律咨询</li><li>知识产权保护</li><li>企业纠纷处理</li></ul><p>我们的律师团队拥有丰富的实战经验，能够为企业提供全方位的法律保障。</p>',
            providerName: '专业法律事务所',
            providerLevel: '金牌服务商',
            providerRating: 4.9,
            serviceCount: 56,
            completionRate: 99,
            yearsExperience: 5,
            resourceImage: '1.png',
            providerAvatar: '2.png',
            reviews: [
              {
                username: '企业用户A',
                createdAt: new Date().toISOString(),
                rating: 5,
                content: '服务非常专业，团队响应速度快，解决了我们的实际问题，非常满意！',
                tags: ['专业', '高效', '满意']
              },
              {
                username: '创业者B',
                createdAt: new Date(Date.now() - 86400000).toISOString(),
                rating: 4,
                content: '服务质量不错，价格合理，就是交付时间稍微晚了一点，总体还是推荐的。',
                tags: ['质量好', '价格合理']
              }
            ]
          }
        }
        try {
          this.favorited = await isFavorited(id)
        } catch (_) {}
        
        // 加载本地存储的评价
        this.loadReviewsFromLocalStorage()
        
        // 获取相关服务推荐
        try {
          const response = await this.$http.get(`/resource/${id}/related`)
          this.relatedServices = response.data.data
        } catch (_) {
          // 模拟相关服务数据
          this.relatedServices = [
            {
              id: 2,
              title: '财务审计服务',
              price: 3000,
              category: '财务服务',
              rating: 4.7,
              providerName: '专业财务事务所'
            },
            {
              id: 3,
              title: '市场调研服务',
              price: 1500,
              category: '市场服务',
              rating: 4.5,
              providerName: '市场研究公司'
            }
          ]
        }
      } finally {
        this.loading = false
      }
    },
    async toggleFavorite() {
      try {
        await toggleFavorite(this.resource.id)
        this.favorited = !this.favorited
        this.$message.success(this.favorited ? '已收藏' : '已取消收藏')
      } catch (e) {
        this.$message.error('操作失败，请稍后重试')
      }
    },
    async submitOrder() {
      this.$refs.orderForm.validate(async valid => {
        if (!valid) return
        try {
          await create({
            resourceId: this.resource.id,
            providerId: this.resource.providerId || '1',
            title: this.orderForm.title,
            quantity: this.orderForm.quantity,
            amount: this.orderForm.amount,
            deliveryTime: this.orderForm.deliveryTime,
            description: this.orderForm.description
          })
          this.$message.success('订单已提交')
          this.showOrder = false
        } catch (e) {
          this.$message.error(e.response?.data?.message || '提交失败，请稍后重试')
        }
      })
    },
    viewProviderProfile() {
      this.showProviderDialog = true
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    goService(id) {
      console.log('跳转到服务详情页:', id)
      this.$router.push(`/resource/detail/${id}`)
    },
    getImageUrl(imageName) {
      return getImageUrlUtil(imageName)
    },
    toggleReviewTag(tag) {
      const index = this.reviewForm.tags.indexOf(tag)
      if (index > -1) {
        this.reviewForm.tags.splice(index, 1)
      } else {
        this.reviewForm.tags.push(tag)
      }
    },
    async submitReview() {
      this.$refs.reviewForm.validate(async valid => {
        if (!valid) return
        try {
          // 模拟提交评价
          const newReview = {
            id: Date.now().toString(),
            username: '当前用户',
            avatar: '',
            createdAt: new Date().toISOString(),
            rating: this.reviewForm.rating,
            content: this.reviewForm.content,
            tags: this.reviewForm.tags
          }
          
          // 添加到评价列表
          if (!this.resource.reviews) {
            this.resource.reviews = []
          }
          this.resource.reviews.unshift(newReview)
          this.resource.reviewCount = (this.resource.reviewCount || 0) + 1
          
          // 重新计算评分
          const totalRating = this.resource.reviews.reduce((sum, review) => sum + (review.rating || 5), 0)
          this.resource.rating = (totalRating / this.resource.reviews.length).toFixed(1)
          
          // 存储到localStorage
          this.saveReviewsToLocalStorage()
          
          this.$message.success('评价提交成功')
          this.showReviewForm = false
          
          // 重置表单
          this.reviewForm = {
            rating: 5,
            content: '',
            tags: []
          }
        } catch (e) {
          this.$message.error('提交失败，请稍后重试')
        }
      })
    },
    saveReviewsToLocalStorage() {
      try {
        const reviewsData = {
          reviews: this.resource.reviews,
          reviewCount: this.resource.reviewCount,
          rating: this.resource.rating
        }
        localStorage.setItem(`reviews_${this.resource.id}`, JSON.stringify(reviewsData))
      } catch (e) {
        console.error('保存评价数据失败:', e)
      }
    },
    loadReviewsFromLocalStorage() {
      try {
        const reviewsData = localStorage.getItem(`reviews_${this.resource.id}`)
        if (reviewsData) {
          const parsedData = JSON.parse(reviewsData)
          this.resource.reviews = parsedData.reviews
          this.resource.reviewCount = parsedData.reviewCount
          this.resource.rating = parsedData.rating
        }
      } catch (e) {
        console.error('加载评价数据失败:', e)
      }
    }
  }
}
</script>

<style scoped>
.service-detail-page {
  min-height: 100vh;
  background-color: #f8fafc;
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

/* 服务头部样式 */
.service-header {
  background: linear-gradient(135deg, #007dff, #0052a3);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: white;
  padding: 80px 0;
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.service-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.service-header > * {
  position: relative;
  z-index: 2;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.service-basic-info {
  margin-bottom: 40px;
  text-align: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
  margin-top: 30px;
}

.service-meta {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.header-right {
  flex-shrink: 0;
  min-width: 320px;
}

.service-category-badge {
  display: inline-block;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 24px;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.service-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.service-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 36px;
  line-height: 1.5;
}

.service-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px 16px;
  border-radius: 12px;
  backdrop-filter: blur(8px);
}

.meta-icon {
  font-size: 20px;
  opacity: 0.9;
}

.meta-label {
  opacity: 0.8;
  font-weight: 500;
}

.meta-value {
  font-weight: 700;
  font-size: 16px;
}

.meta-value.price {
  font-size: 28px;
  color: #ffd700;
  font-weight: 700;
}

.meta-value.rating {
  color: #ffd700;
  font-size: 18px;
}

.header-right {
  flex-shrink: 0;
}

.provider-info {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 28px;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 320px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.provider-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981, #059669);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

.provider-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.provider-details h4 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  color: white;
}

.provider-level {
  font-size: 15px;
  opacity: 0.8;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.8);
}

.provider-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  font-size: 14px;
  flex-wrap: wrap;
}

.stat-item {
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  font-weight: 600;
  backdrop-filter: blur(8px);
}

.provider-profile-btn {
  width: 100%;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

/* 服务内容区域 */
.service-content {
  padding-bottom: 80px;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.content-left {
  flex: 1;
  min-width: 0;
}

.content-right {
  width: 380px;
  flex-shrink: 0;
}

/* 卡片样式 */
.content-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  margin-bottom: 30px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 28px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
  position: relative;
}

.card-header::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(135deg, #007dff, #0052a3);
}

.card-header i {
  font-size: 20px;
  color: #007dff;
  flex-shrink: 0;
}

.card-header .card-title {
  font-size: 19px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
}

.review-btn {
  margin-left: auto;
  flex-shrink: 0;
}

.review-count {
  font-size: 15px;
  color: #64748b;
  margin-left: 12px;
  font-weight: 500;
  background: rgba(0, 125, 255, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
}

/* 服务描述 */
.service-description {
  padding: 30px 28px;
  line-height: 1.8;
  color: #475569;
  font-size: 16px;
}

.service-description p {
  margin-bottom: 20px;
  text-align: justify;
}

.service-description ul {
  margin-bottom: 20px;
  padding-left: 30px;
}

.service-description li {
  margin-bottom: 10px;
  position: relative;
}

.service-description ul li::before {
  content: '•';
  color: #007dff;
  font-weight: bold;
  position: absolute;
  left: -18px;
}

/* 服务详情 */
.service-details {
  padding: 30px 28px;
}

.detail-section {
  margin-bottom: 40px;
  padding: 24px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e2e8f0;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 24px;
  background: linear-gradient(135deg, #007dff, #0052a3);
  border-radius: 3px;
}

.section-content {
  color: #475569;
  line-height: 1.6;
  font-size: 15px;
}

.section-content p {
  margin-bottom: 16px;
}

.section-content ul {
  margin-bottom: 16px;
  padding-left: 24px;
}

.section-content li {
  margin-bottom: 8px;
}

.detail-content p {
  margin-bottom: 16px;
  color: #475569;
  line-height: 1.6;
  font-size: 15px;
}

/* 服务流程 */
.process-steps {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.step-item {
  display: flex;
  gap: 20px;
  position: relative;
  margin-bottom: 30px;
}

.step-item::before {
  content: '';
  position: absolute;
  left: 27px;
  top: 56px;
  bottom: -30px;
  width: 2px;
  background: linear-gradient(180deg, #007dff, #e2e8f0);
  z-index: 1;
}

.step-item:last-child::before {
  display: none;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-number {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007dff, #0052a3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  flex-shrink: 0;
  z-index: 2;
  box-shadow: 0 4px 16px rgba(0, 125, 255, 0.3);
}

.step-content {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  min-width: 0;
}

.step-content h5 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 10px;
}

.step-content p {
  color: #475569;
  line-height: 1.6;
  margin: 0;
  font-size: 15px;
}

/* 交付标准 */
.delivery-standards {
  padding: 0 8px;
}

.standards-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.standard-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  margin-bottom: 12px;
  transition: all 0.2s ease;
}

.standard-item:last-child {
  margin-bottom: 0;
}

.standard-item i {
  font-size: 20px;
  color: #10b981;
  margin-top: 2px;
  flex-shrink: 0;
}

.standard-item span {
  color: #475569;
  line-height: 1.6;
  font-size: 15px;
  flex: 1;
}

/* 服务保障 */
.guarantee-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.guarantee-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 24px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.guarantee-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 125, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.guarantee-icon i {
  font-size: 32px;
  color: #007dff;
  display: block;
}

.guarantee-content h5 {
  font-size: 17px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.guarantee-content p {
  color: #475569;
  line-height: 1.6;
  margin: 0;
  font-size: 15px;
}

/* 用户评价 */
.user-reviews {
  padding: 30px 28px;
}

.review-item {
  padding: 24px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  margin-bottom: 20px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.reviewer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007dff, #0052a3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 125, 255, 0.3);
}

.reviewer-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.reviewer-details {
  min-width: 0;
}

.reviewer-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.review-time {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  font-weight: 500;
}

.review-rating {
  display: flex;
  gap: 6px;
  background: rgba(251, 191, 36, 0.1);
  padding: 6px 12px;
  border-radius: 16px;
}

.review-rating i {
  font-size: 16px;
  color: #fbbf24;
}

.review-content {
  color: #475569;
  line-height: 1.6;
  margin-bottom: 16px;
  font-size: 15px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.review-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.review-tag {
  display: inline-block;
  padding: 6px 14px;
  background: #e0f2fe;
  color: #0284c7;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid #e0f2fe;
}

.no-reviews {
  text-align: center;
  padding: 80px 20px;
  color: #94a3b8;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px dashed #e2e8f0;
}

.no-reviews i {
  font-size: 64px;
  margin-bottom: 20px;
  display: block;
  color: #cbd5e1;
}

.no-reviews p {
  margin-bottom: 12px;
  font-size: 18px;
  font-weight: 500;
  color: #64748b;
}

.no-reviews-tip {
  font-size: 15px !important;
  opacity: 0.8;
  color: #94a3b8;
  margin-bottom: 24px;
}

.write-review-btn {
  margin-top: 16px;
}

/* 评价表单样式 */
.rating-stars {
  display: flex;
  gap: 12px;
}

.star-icon {
  font-size: 32px;
  color: #cbd5e1;
  cursor: pointer;
  transition: all 0.2s ease;
}

.star-icon.el-icon-star-on {
  color: #fbbf24;
}

.review-tags-select {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.review-tags-select .el-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.review-tags-select .el-tag.active {
  background: #007dff;
  color: white;
  border-color: #007dff;
}

/* 常见问题 */
.faq-section {
  padding: 30px 28px;
}

.faq-content {
  padding: 20px 0;
  color: #475569;
  line-height: 1.6;
  font-size: 15px;
}

.el-collapse-item__header {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.el-collapse-item__content {
  padding: 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 16px;
}

/* 操作卡片 */
.action-card {
  position: sticky;
  top: 24px;
}

.action-buttons {
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  align-items: stretch;
}

.action-btn {
  width: 100% !important;
  padding: 14px 20px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 2px solid transparent;
  box-sizing: border-box;
  height: 50px;
  margin-bottom: 0;
  text-align: center;
  min-width: 0;
  flex-shrink: 1;
  line-height: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.action-btn.favorited {
  background: #fbbf24 !important;
  border-color: #fbbf24 !important;
  box-shadow: 0 4px 16px rgba(251, 191, 36, 0.4);
}

.action-btn.favorited i {
  color: white !important;
}

.service-tags {
  padding: 0 28px 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  display: inline-block;
  padding: 8px 16px;
  background: #e0f2fe;
  color: #0284c7;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid #e0f2fe;
}

.service-highlights {
  padding: 0 28px 30px;
}

.service-highlights h4 {
  font-size: 17px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.service-highlights h4::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 2px;
}

.service-highlights ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.service-highlights li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  color: #475569;
  font-size: 15px;
  line-height: 1.6;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.service-highlights li i {
  font-size: 16px;
  color: #10b981;
  flex-shrink: 0;
  margin-top: 2px;
}

/* 服务统计卡片 */
.stats-card {
  margin-bottom: 30px;
}

.service-stats {
  padding: 30px 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.stat-label {
  font-size: 15px;
  color: #64748b;
  font-weight: 600;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #007dff;
}

/* 相关服务 */
.related-services {
  padding: 30px 28px;
}

.related-item {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  margin-bottom: 16px;
  cursor: pointer;
}

.related-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.related-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
  margin-right: 16px;
  line-height: 1.4;
}

.related-price {
  font-size: 18px;
  font-weight: 700;
  color: #007dff;
  flex-shrink: 0;
  background: rgba(0, 125, 255, 0.1);
  padding: 6px 12px;
  border-radius: 12px;
}

.related-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.related-category {
  font-size: 13px;
  color: #64748b;
  background: #e2e8f0;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
}

.related-rating {
  display: flex;
  gap: 4px;
  background: rgba(251, 191, 36, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
}

.related-rating i {
  font-size: 14px;
  color: #fbbf24;
}

.related-provider {
  font-size: 13px;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.provider-tag {
  background: transparent;
  padding: 0;
}

/* 对话框样式 */
.book-dialog,
.contact-dialog,
.provider-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.book-dialog .el-dialog__header,
.contact-dialog .el-dialog__header,
.provider-dialog .el-dialog__header {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 24px 28px;
}

.book-dialog .el-dialog__title,
.contact-dialog .el-dialog__title,
.provider-dialog .el-dialog__title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.book-dialog .el-dialog__body,
.contact-dialog .el-dialog__body,
.provider-dialog .el-dialog__body {
  padding: 30px 28px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 28px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.contact-item i {
  font-size: 20px;
  color: #007dff;
  flex-shrink: 0;
  width: 24px;
  text-align: center;
}

.contact-label {
  font-size: 15px;
  color: #64748b;
  font-weight: 600;
  min-width: 70px;
}

.contact-value {
  font-size: 15px;
  color: #1e293b;
  font-weight: 600;
  flex: 1;
}

.contact-note,
.contact-hours {
  padding: 20px;
  background: #fffbeb;
  border: 1px solid #fbbf24;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}

.contact-note i,
.contact-hours i {
  font-size: 18px;
  color: #f59e0b;
  flex-shrink: 0;
  margin-top: 2px;
}

.contact-note p,
.contact-hours p {
  color: #78350f;
  font-size: 15px;
  line-height: 1.5;
  margin: 0;
}

/* 服务商资料对话框 */
.provider-profile {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.provider-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 28px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.provider-avatar.large {
  width: 96px;
  height: 96px;
  font-size: 36px;
  overflow: hidden;
  background: linear-gradient(135deg, #10b981, #059669);
  box-shadow: 0 6px 24px rgba(16, 185, 129, 0.3);
}

.provider-avatar.large .provider-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.provider-info-detail h3 {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.provider-info-detail .provider-level {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 16px;
  font-weight: 500;
}

.provider-stats-detail {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.provider-stats-detail .stat-item {
  background: #e2e8f0;
  color: #64748b;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
}

.provider-description h4,
.provider-services h4 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.provider-description h4::before,
.provider-services h4::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background: linear-gradient(135deg, #007dff, #0052a3);
  border-radius: 2px;
}

.provider-description p {
  color: #475569;
  line-height: 1.6;
  margin: 0;
  font-size: 15px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-item {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.service-item h5 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.service-price {
  font-size: 18px;
  font-weight: 700;
  color: #007dff;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-container {
    gap: 30px;
  }
  
  .content-right {
    width: 320px;
  }
}

@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 30px;
  }
  
  .service-meta {
    width: 100%;
  }
  
  .header-right {
    width: 100%;
  }
  
  .provider-info {
    min-width: 100%;
  }
  
  .content-container {
    flex-direction: column;
  }
  
  .content-right {
    width: 100%;
    order: -1;
    margin-bottom: 30px;
  }
  
  .action-card {
    position: static;
  }
  
  .service-title {
    font-size: 36px;
  }
  
  .service-subtitle {
    font-size: 18px;
  }
  
  .guarantee-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .service-header {
    padding: 60px 0;
  }
  
  .service-basic-info {
    margin-bottom: 30px;
  }
  
  .service-title {
    font-size: 30px;
  }
  
  .service-subtitle {
    font-size: 16px;
  }
  
  .service-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .meta-item {
    font-size: 14px;
    padding: 8px 12px;
  }
  
  .content-container {
    padding: 0 16px;
  }
  
  .content-card {
    margin-bottom: 20px;
    border-radius: 12px;
  }
  
  .card-header {
    padding: 20px 24px;
  }
  
  .card-header .card-title {
    font-size: 17px;
  }
  
  .service-description,
  .service-details,
  .user-reviews,
  .faq-section,
  .action-buttons,
  .service-stats,
  .related-services {
    padding: 24px 20px;
  }
  
  .detail-section {
    padding: 20px;
    margin-bottom: 30px;
  }
  
  .section-title {
    font-size: 16px;
    margin-bottom: 16px;
  }
  
  .step-item {
    flex-direction: column;
    gap: 12px;
  }
  
  .step-number {
    width: 48px;
    height: 48px;
    font-size: 18px;
    align-self: flex-start;
  }
  
  .step-content {
    padding: 16px;
  }
  
  .standard-item {
    padding: 12px;
    margin-bottom: 10px;
  }
  
  .guarantee-item {
    padding: 20px;
  }
  
  .guarantee-icon {
    width: 56px;
    height: 56px;
  }
  
  .guarantee-icon i {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 16px;
  }
  
  .service-title {
    font-size: 28px;
  }
  
  .service-subtitle {
    font-size: 15px;
  }
  
  .content-container {
    padding: 0 16px;
  }
  
  .card-header {
    padding: 16px 20px;
  }
  
  .card-header .card-title {
    font-size: 16px;
  }
  
  .service-description,
  .service-details,
  .user-reviews,
  .faq-section,
  .action-buttons,
  .service-stats,
  .related-services {
    padding: 20px 16px;
  }
  
  .detail-section {
    padding: 16px;
  }
  
  .section-title {
    font-size: 15px;
  }
  
  .step-content h5 {
    font-size: 15px;
  }
  
  .guarantee-content h5 {
    font-size: 16px;
  }
  
  .action-btn {
    padding: 14px;
    font-size: 14px;
}

/* 对话框样式 */
.book-dialog,
  .contact-dialog,
  .provider-dialog {
    width: 95% !important;
  }
  
  .book-dialog .el-dialog__body,
  .contact-dialog .el-dialog__body,
  .provider-dialog .el-dialog__body {
    padding: 24px;
  }
}

@media (max-width: 480px) {
  .service-header {
    padding: 40px 0;
  }
  
  .service-title {
    font-size: 24px;
  }
  
  .service-subtitle {
    font-size: 14px;
    margin-bottom: 24px;
  }
  
  .header-container {
    padding: 0 16px;
  }
  
  .provider-info {
    padding: 20px;
    border-radius: 16px;
  }
  
  .content-left,
  .content-right {
    padding: 0;
  }
  
  .content-card {
    border-radius: 8px;
  }
  
  .service-description,
  .service-details,
  .user-reviews,
  .faq-section,
  .action-buttons,
  .service-stats,
  .related-services {
    padding: 16px;
  }
  
  .card-header {
    padding: 16px 20px;
  }
  
  .card-header .card-title {
    font-size: 16px;
  }
  
  .provider-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .provider-avatar.large {
    width: 80px;
    height: 80px;
    font-size: 32px;
  }
  
  .step-item {
    gap: 12px;
  }
  
  .step-number {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .step-content {
    padding: 12px;
  }
  
  .step-content h5 {
    font-size: 15px;
  }
  
  .step-content p {
    font-size: 14px;
  }
  
  .review-item {
    padding: 16px;
  }
  
  .reviewer-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .review-content {
    padding: 12px;
    font-size: 14px;
  }
  
  .action-btn {
    padding: 12px;
    font-size: 14px;
  }
  
  .related-item {
    padding: 16px;
  }
  
  .related-title {
    font-size: 15px;
  }
  
  .related-price {
    font-size: 16px;
  }
  
  .service-item {
    padding: 16px;
  }
  
  .service-item h5 {
    font-size: 15px;
  }
  
  .service-price {
    font-size: 16px;
  }
}
</style>
