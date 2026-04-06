import { getDetail, toggleFavorite, isFavorited } from '@/api/project'
import { submit } from '@/api/investment'
import { create } from '@/api/order'
import { mapGetters } from 'vuex'
import ChatDialog from '@/components/ChatDialog.vue'

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
      if (!imageName) return ''
      // 检查是否是完整URL
      if (imageName.startsWith('http://') || imageName.startsWith('https://')) {
        return imageName
      }
      // 检查是否是已知的占位图片
      const placeholderImages = ['1.png', '2.png', '3.png']
      if (!placeholderImages.includes(imageName)) {
        // 如果不是已知的占位图片，返回默认占位图片
        return `/images/1.png`
      }
      // 否则返回本地图片路径
      return `/images/${imageName}`
    }
  }
}
