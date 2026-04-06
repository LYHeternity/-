<template>
  <div class="home-page">

    <!-- 轮播图区域 -->
    <section class="hero-carousel-section">
      <div class="hero-carousel">
        <div class="carousel-container">
          <div class="carousel-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div v-for="(slide, index) in carouselSlides" :key="index" class="carousel-slide">
              <div class="slide-image" :style="{ backgroundImage: `url(${slide.image})` }"></div>
              <div class="slide-content">
                <div class="slide-text">
                  <h2 class="slide-title">{{ slide.title }}</h2>
                  <p class="slide-subtitle">{{ slide.subtitle }}</p>
                  <router-link :to="slide.link" class="slide-button">{{ slide.button }}</router-link>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 导航按钮 -->
          <button class="carousel-nav carousel-nav--prev" @click="prevSlide" aria-label="Previous slide">
            <i class="el-icon-arrow-left"></i>
          </button>
          <button class="carousel-nav carousel-nav--next" @click="nextSlide" aria-label="Next slide">
            <i class="el-icon-arrow-right"></i>
          </button>
          
          <!-- 指示器 -->
          <div class="carousel-indicators">
            <button 
              v-for="(slide, index) in carouselSlides" 
              :key="index" 
              class="carousel-indicator" 
              :class="{ active: index === currentSlide }"
              @click="goToSlide(index)"
              :aria-label="`Go to slide ${index + 1}`"
            ></button>
          </div>
        </div>
      </div>
    </section>

    <!-- 核心价值展示 -->
    <section class="section value-section">
      <div class="section-container">
        <h2 class="section-title">核心价值</h2>
        <div class="value-grid">
          <div class="value-card" v-for="(item, index) in valueItems" :key="index">
            <div class="value-icon">
              <i :class="item.icon"></i>
            </div>
            <h3 class="value-title">{{ item.title }}</h3>
            <p class="value-desc">{{ item.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台数据概览 -->
    <section class="section overview-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t1">平台数据概览</h2>
        <el-row :gutter="24" class="overview-row">
          <el-col :xs="12" :sm="12" :md="6">
            <div class="overview-card hw-clickable reveal-item" @click="$router.push('/projects')" ref="c1">
              <div class="overview-icon project"><i class="el-icon-document"></i></div>
              <div class="overview-num">{{ animatedOverview.projectCount }}</div>
              <div class="overview-label">在营项目</div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6">
            <div class="overview-card hw-clickable reveal-item" @click="$router.push('/services')" ref="c2">
              <div class="overview-icon service"><i class="el-icon-goods"></i></div>
              <div class="overview-num">{{ animatedOverview.resourceCount }}</div>
              <div class="overview-label">服务资源</div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6">
            <div class="overview-card reveal-item" ref="c3">
              <div class="overview-icon user"><i class="el-icon-user-solid"></i></div>
              <div class="overview-num">{{ animatedOverview.userCount }}</div>
              <div class="overview-label">注册用户</div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6">
            <div class="overview-card reveal-item" ref="c4">
              <div class="overview-icon order"><i class="el-icon-s-order"></i></div>
              <div class="overview-num">{{ animatedOverview.orderCount }}</div>
              <div class="overview-label">服务订单</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 数据趋势图 -->
    <section class="section chart-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t2">数据成果展示</h2>
        <div class="chart-wrap reveal-item" ref="chartWrap">
          <div ref="trendChart" class="trend-chart"></div>
        </div>
      </div>
    </section>

    <!-- 核心功能入口 -->
    <section class="section feature-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t3">核心功能</h2>
        <el-row :gutter="24" class="feature-row">
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="$router.push('/projects')" ref="f1">
              <div class="feature-icon"><i class="el-icon-document"></i></div>
              <h3>项目大厅</h3>
              <p>浏览、发布与对接创业项目，获取融资与资源</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="$router.push('/services')" ref="f2">
              <div class="feature-icon"><i class="el-icon-goods"></i></div>
              <h3>服务市场</h3>
              <p>技术、法律、市场等专业服务预约与对接</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="$router.push('/my-investments')" ref="f3">
              <div class="feature-icon"><i class="el-icon-coin"></i></div>
              <h3>投资对接</h3>
              <p>提交意向、在线沟通与投资记录管理</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="$router.push('/messages')" ref="f4">
              <div class="feature-icon"><i class="el-icon-message"></i></div>
              <h3>消息中心</h3>
              <p>系统通知、业务消息与在线沟通</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="navigateToProjectPublish" ref="f5">
              <div class="feature-icon"><i class="el-icon-edit-outline"></i></div>
              <h3>发布项目</h3>
              <p>填写项目信息，提交审核后展示</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="navigateToServicePublish" ref="f6">
              <div class="feature-icon"><i class="el-icon-goods"></i></div>
              <h3>发布服务</h3>
              <p>填写服务信息，提交审核后展示</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card hw-clickable reveal-item" @click="$router.push('/my-orders')" ref="f7">
              <div class="feature-icon"><i class="el-icon-s-order"></i></div>
              <h3>我的订单</h3>
              <p>查看服务订单状态与履约记录</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 品牌简介 -->
    <section class="section intro-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t4">关于平台</h2>
        <div class="intro-card reveal-item" ref="introCard">
          <p>创业者综合服务平台面向<strong>创业者、投资人、服务商</strong>三类角色，提供项目展示、融资对接、服务预约、消息通知等一站式能力。平台采用前后端分离架构，支持多角色权限与内容审核，无第三方支付与短信依赖，可在本地完成全流程闭环。</p>
          <p>支持项目与服务的发布与审核、投资意向提交与沟通、服务订单与评价、管理员数据统计与系统配置等核心能力。</p>
        </div>
      </div>
    </section>

    <!-- 客户/案例展示 -->
    <section class="section cases-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t5">案例与成果</h2>
        <div class="waterfall reveal-item" ref="waterfall">
          <div
            v-for="(item, index) in caseList"
            :key="item.id"
            class="waterfall-item"
            :style="{ animationDelay: (index % 4) * 0.08 + 's' }"
            @click="handleCaseClick(item)"
          >
            <div class="case-img-wrap">
              <img :src="item.image" :alt="item.title" loading="lazy" />
              <div class="case-overlay"></div>
            </div>
            <div class="case-body">
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
              <span class="case-tag">{{ item.tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="section faq-section">
      <div class="section-container">
        <h2 class="section-title reveal-item" ref="t6">常见问题</h2>
        <div class="faq-list reveal-item" ref="faqList">
          <div class="faq-item">
            <h4>如何发布项目？</h4>
            <p>注册并登录创业者账号后，在「发布项目」中填写项目名称、行业、阶段、融资需求等信息，提交后等待审核通过即可展示。</p>
          </div>
          <div class="faq-item">
            <h4>服务商如何接单？</h4>
            <p>服务商在「服务市场」发布服务后，用户可在线预约；订单将出现在「服务订单」中，可进行接单与履约管理。</p>
          </div>
          <div class="faq-item">
            <h4>投资人如何提交意向？</h4>
            <p>在项目详情页点击「提交投资意向」，填写意向说明与联系方式，创业者可在「我的项目」中查看并回复意向。</p>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'
import request from '@/utils/request'
import * as echarts from 'echarts'

/* 图片素材来源：本地 images 目录 */
const CASE_IMAGES = [
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png',
  '/images/2.png'
]
const CASE_TITLES = [
  '智能硬件孵化项目', '企业级SaaS解决方案', '新能源出行平台', '医疗健康数字化', '教育科技产品', '跨境电商服务', '本地生活O2O', '金融科技风控', 'AI内容生产工具', '供应链协同平台',
  '智慧园区建设', '环保科技应用', '农业物联网', '文创IP孵化', '企业培训SaaS', '智能客服系统', '数据安全方案', '区块链存证', '短视频营销', '跨境支付对接', '工业互联网', '消费品牌孵化', '社区团购运营'
]
const CASE_DESCS = [
  '从0到1的孵化与融资对接案例', '多轮融资与规模化落地经验', '政企合作与试点推广', '技术驱动与产品迭代', '市场拓展与用户增长', '团队组建与组织升级', '合规与资质办理', '品牌与PR建设', '供应链与渠道整合', '国际化与出海',
  '产业园区数字化升级', '节能减排与碳管理', '智慧农业与溯源', 'IP授权与衍生开发', '企业学习与人才发展', '智能客服与工单系统', '数据合规与隐私保护', '电子存证与司法链', '短视频与直播带货', '跨境结算与汇兑', '设备联网与预测维护', '新消费品牌从0到1', '社区团购与私域运营'
]
const TAGS = ['融资案例', '服务案例', '合作案例', '产品案例', '孵化案例']

function buildCaseList() {
  return CASE_TITLES.slice(0, 22).map((title, i) => ({
    id: i + 1,
    title,
    desc: CASE_DESCS[i % CASE_DESCS.length],
    image: CASE_IMAGES[i % CASE_IMAGES.length],
    tag: TAGS[i % TAGS.length]
  }))
}



function buildValueItems() {
  return [
    {
      icon: 'el-icon-lightning',
      title: '高效对接',
      description: '智能匹配算法，精准连接创业者与投资人'
    },
    {
      icon: 'el-icon-medal',
      title: '专业服务',
      description: '一站式创业服务，涵盖技术、法律、市场等领域'
    },
    {
      icon: 'el-icon-data-line',
      title: '数据驱动',
      description: '基于大数据分析，提供精准的商业洞察'
    },
    {
      icon: 'el-icon-lock',
      title: '安全可靠',
      description: '严格的信息安全保护，确保数据隐私安全'
    }
  ]
}

export default {
  name: 'Home',
  components: {
  },
  data() {
    return {
      overview: { userCount: 0, projectCount: 0, resourceCount: 0, orderCount: 0 },
      animatedOverview: { userCount: 0, projectCount: 0, resourceCount: 0, orderCount: 0 },
      caseList: buildCaseList(),
      valueItems: buildValueItems(),
      trendChart: null,
      // 轮播图数据
      currentSlide: 0,
      carouselSlides: [
        {
          image: '/images/1.png',
          title: '创业者综合服务平台',
          subtitle: '一站式对接项目、投资与服务资源，助力创业全周期发展',
          button: '立即体验',
          link: '/register'
        },
        {
          image: '/images/2.png',
          title: '精准项目融资对接',
          subtitle: '高效连接创业者与投资人，实现精准匹配',
          button: '查看项目',
          link: '/projects'
        },
        {
          image: '/images/3.png',
          title: '专业服务市场',
          subtitle: '汇聚技术、法律、市场等全方位创业服务',
          button: '服务市场',
          link: '/services'
        }
      ],
      carouselInterval: null
    }
  },
  computed: {
    ...mapState('message', ['unreadCount'])
  },
  methods: {
    ...mapActions('message', ['fetchUnreadCount']),
    async loadOverview() {
      // 使用缓存减少重复请求
      if (localStorage.getItem('overviewData')) {
        try {
          const cached = JSON.parse(localStorage.getItem('overviewData'))
          this.overview = cached
          this.$nextTick(() => this.startNumberAnimation())
          return
        } catch (e) {
          localStorage.removeItem('overviewData')
        }
      }
      
      try {
        const res = await request({ url: '/stats/overview', method: 'get', skipGlobalError: true })
        if (res && typeof res === 'object') {
          this.overview.userCount = res.userCount || 0
          this.overview.projectCount = res.projectCount || 0
          this.overview.resourceCount = res.resourceCount || 0
          this.overview.orderCount = res.orderCount || 0
          // 缓存数据，有效期5分钟
          localStorage.setItem('overviewData', JSON.stringify(this.overview))
          setTimeout(() => localStorage.removeItem('overviewData'), 5 * 60 * 1000)
        }
      } catch (_) {
        this.overview = { userCount: 128, projectCount: 56, resourceCount: 34, orderCount: 89 }
      }
      this.$nextTick(() => this.startNumberAnimation())
    },
    triggerReveal(els) {
      if (!els) return
      els.forEach((el, i) => {
        if (!el) return
        setTimeout(() => el.classList.add('reveal-in'), 120 + i * 80)
      })
    },
    setupRevealObserver() {
      const refs = ['t1', 'c1', 'c2', 'c3', 'c4', 't2', 'chartWrap', 't3', 'f1', 'f2', 'f3', 'f4', 'f5', 'f6', 'f7', 't4', 'introCard', 't5', 'waterfall', 't6', 'faqList']
      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              entry.target.classList.add('reveal-in')
            }
          })
        },
        { threshold: 0.15, rootMargin: '0px 0px -40px 0px' }
      )
      refs.forEach((r) => {
        const el = this.$refs[r]
        if (el) (Array.isArray(el) ? el : [el]).forEach((e) => e && observer.observe(e))
      })
    },
    initTrendChart() {
      const el = this.$refs.trendChart
      if (!el) return
      this.trendChart = echarts.init(el)
      const dates = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      const values = [22, 38, 45, 52, 48, 65, 72]
      this.trendChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '12%', containLabel: true },
        xAxis: { type: 'category', data: dates, boundaryGap: false, axisLine: { lineStyle: { color: '#e2e8f0' } } },
        yAxis: { type: 'value', name: '数量', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f1f5f9' } } },
        series: [
          {
            name: '平台活跃',
            type: 'line',
            smooth: true,
            data: values,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#007dff' },
            itemStyle: { color: '#007dff' },
            areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(0,125,255,0.25)' }, { offset: 1, color: 'rgba(0,125,255,0.02)' }] } }
          }
        ],
        animation: true,
        animationDuration: 1200,
        animationEasing: 'cubicOut'
      })
    },
    handleCaseClick(item) {
      this.$router.push('/projects')
    },
    navigateToProjectPublish() {
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/project-publish')
    },
    navigateToServicePublish() {
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/service-publish')
    },
    startNumberAnimation() {
      const duration = 1200
      const keys = ['userCount', 'projectCount', 'resourceCount', 'orderCount']
      const start = Date.now()
      const tick = () => {
        const elapsed = Date.now() - start
        const t = Math.min(elapsed / duration, 1)
        const easeOut = 1 - Math.pow(1 - t, 2)
        keys.forEach((k) => {
          this.animatedOverview[k] = Math.round(this.overview[k] * easeOut)
        })
        if (t < 1) requestAnimationFrame(tick)
        else keys.forEach((k) => { this.animatedOverview[k] = this.overview[k] })
      }
      requestAnimationFrame(tick)
    },
    // 轮播图控制方法
    nextSlide() {
      this.currentSlide = (this.currentSlide + 1) % this.carouselSlides.length
      this.resetCarouselInterval()
    },
    prevSlide() {
      this.currentSlide = (this.currentSlide - 1 + this.carouselSlides.length) % this.carouselSlides.length
      this.resetCarouselInterval()
    },
    goToSlide(index) {
      this.currentSlide = index
      this.resetCarouselInterval()
    },
    startCarousel() {
      // 只在浏览器环境中启动轮播，避免在SSR中执行
      if (typeof window !== 'undefined') {
        this.carouselInterval = setInterval(() => {
          this.nextSlide()
        }, 5000)
      }
    },
    stopCarousel() {
      if (this.carouselInterval) {
        clearInterval(this.carouselInterval)
        this.carouselInterval = null
      }
    },
    resetCarouselInterval() {
      this.stopCarousel()
      this.startCarousel()
    }

  },
  created() {
    this.loadOverview()
    this.fetchUnreadCount()
  },
  mounted() {
    this.$nextTick(() => {
      this.triggerReveal([this.$refs.heroTitle, this.$refs.heroDesc, this.$refs.heroActions])
      this.setupRevealObserver()
      this.initTrendChart()
      this.startCarousel()
    })
  },
  beforeDestroy() {
    if (this.trendChart) this.trendChart.dispose()
    this.stopCarousel()
  }
}
</script>

<style scoped>
.home-page { 
  max-width: 100%; 
  margin: 0; 
  padding: 0; 
  background: #ffffff;
}

/* 轮播图样式 */
.hero-carousel-section {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: #f8fafc;
}

.hero-carousel {
  position: relative;
  width: 100%;
  height: 600px;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.carousel-slides {
  display: flex;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.carousel-slide {
  position: relative;
  flex: 0 0 100%;
  height: 100%;
  overflow: hidden;
}

.slide-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: transform 7s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.carousel-slide:hover .slide-image {
  transform: scale(1.05);
}

.slide-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.3));
}

.slide-text {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 60px;
  color: white;
}

.slide-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  opacity: 0;
  transform: translateY(20px);
  animation: slideInUp 0.8s ease forwards;
}

.slide-subtitle {
  font-size: 20px;
  margin-bottom: 32px;
  line-height: 1.5;
  max-width: 600px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  opacity: 0;
  transform: translateY(20px);
  animation: slideInUp 0.8s ease 0.2s forwards;
}

.slide-button {
  display: inline-block;
  padding: 14px 36px;
  background: #007dff;
  color: white;
  text-decoration: none;
  border-radius: 50px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  animation: slideInUp 0.8s ease 0.4s forwards;
  box-shadow: 0 4px 12px rgba(0, 125, 255, 0.3);
}

.slide-button:hover {
  background: #0066cc;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 125, 255, 0.4);
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 导航按钮 */
.carousel-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 3;
}

.carousel-nav:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-50%) scale(1.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.carousel-nav--prev {
  left: 30px;
}

.carousel-nav--next {
  right: 30px;
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 3;
}

.carousel-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-indicator.active {
  width: 36px;
  border-radius: 6px;
  background: #007dff;
}

.carousel-indicator:hover {
  background: rgba(255, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hero-carousel {
    height: 500px;
  }
  
  .slide-text {
    padding: 0 40px;
  }
  
  .slide-title {
    font-size: 40px;
  }
  
  .slide-subtitle {
    font-size: 18px;
  }
  
  .carousel-nav {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .carousel-nav--prev {
    left: 20px;
  }
  
  .carousel-nav--next {
    right: 20px;
  }
}

@media (max-width: 768px) {
  .hero-carousel {
    height: 400px;
  }
  
  .slide-text {
    padding: 0 30px;
  }
  
  .slide-title {
    font-size: 32px;
    margin-bottom: 16px;
  }
  
  .slide-subtitle {
    font-size: 16px;
    margin-bottom: 24px;
  }
  
  .slide-button {
    padding: 12px 28px;
    font-size: 14px;
  }
  
  .carousel-nav {
    width: 44px;
    height: 44px;
    font-size: 18px;
  }
  
  .carousel-indicators {
    bottom: 20px;
    gap: 8px;
  }
  
  .carousel-indicator {
    width: 10px;
    height: 10px;
  }
  
  .carousel-indicator.active {
    width: 28px;
  }
}

@media (max-width: 480px) {
  .hero-carousel {
    height: 350px;
  }
  
  .slide-text {
    padding: 0 20px;
  }
  
  .slide-title {
    font-size: 28px;
    margin-bottom: 12px;
  }
  
  .slide-subtitle {
    font-size: 14px;
    margin-bottom: 20px;
  }
  
  .slide-button {
    padding: 10px 24px;
    font-size: 13px;
  }
  
  .carousel-nav {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .carousel-nav--prev {
    left: 10px;
  }
  
  .carousel-nav--next {
    right: 10px;
  }
  
  .carousel-indicators {
    bottom: 15px;
    gap: 6px;
  }
  
  .carousel-indicator {
    width: 8px;
    height: 8px;
  }
  
  .carousel-indicator.active {
    width: 24px;
  }
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 核心价值区域 */
.value-section {
  background: #ffffff;
  padding: 80px 0;
}

.value-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
  margin-top: 40px;
}

.value-card {
  text-align: center;
  padding: 32px 24px;
  border-radius: var(--hw-radius-lg);
  background: var(--hw-bg-card);
  box-shadow: var(--hw-shadow);
  border: 1px solid var(--hw-border);
  transition: all var(--hw-duration);
}

.value-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--hw-shadow-hover);
}

.value-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--hw-primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: var(--hw-primary);
  font-size: 28px;
}

.value-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--hw-text);
  margin-bottom: 12px;
}

.value-desc {
  font-size: 14px;
  color: var(--hw-text-secondary);
  line-height: 1.6;
}

.section {
  padding: 80px 0;
  margin-bottom: 0;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--hw-text);
  text-align: center;
  margin-bottom: 48px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: var(--hw-primary);
  border-radius: 2px;
}
.overview-section {
  background: #f8fafc;
}

.overview-row { 
  margin-bottom: 0; 
}

.overview-card {
  background: var(--hw-bg-card);
  border-radius: var(--hw-radius-lg);
  padding: 32px 24px;
  text-align: center;
  box-shadow: var(--hw-shadow);
  border: 1px solid var(--hw-border);
  cursor: pointer;
  transition: all var(--hw-duration);
  margin-bottom: 20px;
  height: 100%;
}

.overview-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--hw-shadow-hover);
  border-color: var(--hw-primary-light);
}

.overview-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.overview-icon.project { 
  background: linear-gradient(135deg, #007dff, #0066cc); 
}

.overview-icon.service { 
  background: linear-gradient(135deg, #007dff, #0052a3); 
}

.overview-icon.user { 
  background: linear-gradient(135deg, #1e293b, #0f172a); 
}

.overview-icon.order { 
  background: linear-gradient(135deg, #007dff, #004c99); 
}

.overview-num { 
  font-size: 32px; 
  font-weight: 700; 
  color: var(--hw-text);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.overview-label { 
  font-size: 14px; 
  color: var(--hw-text-secondary);
  font-weight: 500;
}

.chart-section {
  background: #ffffff;
}

.chart-section .chart-wrap {
  background: var(--hw-bg-card);
  border-radius: var(--hw-radius-lg);
  padding: 32px;
  border: 1px solid var(--hw-border);
  box-shadow: var(--hw-shadow);
  max-width: 1000px;
  margin: 0 auto;
}

.trend-chart { 
  height: 360px; 
}

.feature-section {
  background: #f8fafc;
}

.feature-row { 
  margin-bottom: 0; 
}

.feature-card {
  background: var(--hw-bg-card);
  border-radius: var(--hw-radius-lg);
  padding: 32px;
  border: 1px solid var(--hw-border);
  box-shadow: var(--hw-shadow);
  cursor: pointer;
  transition: all var(--hw-duration);
  margin-bottom: 24px;
  height: 100%;
  text-align: center;
}

.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--hw-shadow-hover);
  border-color: var(--hw-primary-light);
}

.feature-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--hw-primary-light);
  color: var(--hw-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin: 0 auto 20px;
}

.feature-card h3 { 
  font-size: 18px; 
  font-weight: 600; 
  color: var(--hw-text);
  margin-bottom: 12px;
}

.feature-card p { 
  font-size: 14px; 
  color: var(--hw-text-secondary);
  line-height: 1.6;
}

.intro-section {
  background: #ffffff;
}

.intro-section .intro-card {
  background: var(--hw-bg-card);
  border-radius: var(--hw-radius-lg);
  padding: 40px;
  border: 1px solid var(--hw-border);
  box-shadow: var(--hw-shadow);
  max-width: 800px;
  margin: 0 auto;
}

.intro-card p { 
  color: var(--hw-text-secondary);
  line-height: 1.8;
  margin-bottom: 16px;
  font-size: 16px;
}

.intro-card p:last-child { 
  margin-bottom: 0; 
}

.intro-card strong { 
  color: var(--hw-text);
  font-weight: 600;
}

.cases-section {
  background: #f8fafc;
}

/* 瀑布流 */
.waterfall {
  column-count: 4;
  column-gap: 24px;
}

@media (max-width: 1200px) { 
  .waterfall { 
    column-count: 3; 
  } 
}

@media (max-width: 768px) { 
  .waterfall { 
    column-count: 2; 
  } 
}

@media (max-width: 480px) { 
  .waterfall { 
    column-count: 1; 
  } 
}

.waterfall-item {
  break-inside: avoid;
  margin-bottom: 24px;
  border-radius: var(--hw-radius-lg);
  overflow: hidden;
  background: var(--hw-bg-card);
  box-shadow: var(--hw-shadow);
  border: 1px solid var(--hw-border);
  cursor: pointer;
  transition: all var(--hw-duration);
  animation: waterfall-in 0.6s ease backwards;
}

.waterfall-item:hover {
  transform: translateY(-6px);
  box-shadow: var(--hw-shadow-hover);
}

@keyframes waterfall-in {
  from { 
    opacity: 0; 
    transform: translateY(20px); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0); 
  }
}

.case-img-wrap {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.case-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.waterfall-item:hover .case-img-wrap img { 
  transform: scale(1.08); 
}

.case-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.5), transparent 60%);
  pointer-events: none;
}

.case-body { 
  padding: 20px; 
}

.case-body h4 { 
  font-size: 16px; 
  font-weight: 600; 
  color: var(--hw-text);
  margin-bottom: 8px;
}

.case-body p { 
  font-size: 14px; 
  color: var(--hw-text-secondary);
  line-height: 1.5;
  margin-bottom: 12px;
}

.case-tag {
  font-size: 12px;
  color: var(--hw-primary);
  font-weight: 600;
  background: var(--hw-primary-light);
  padding: 4px 12px;
  border-radius: 20px;
  display: inline-block;
}

.faq-section {
  background: #ffffff;
}

.faq-list { 
  display: grid; 
  gap: 24px; 
  max-width: 800px;
  margin: 0 auto;
}

.faq-item {
  background: var(--hw-bg-card);
  border-radius: var(--hw-radius-lg);
  padding: 28px 32px;
  border: 1px solid var(--hw-border);
  box-shadow: var(--hw-shadow);
  transition: all var(--hw-duration);
}

.faq-item:hover {
  border-color: var(--hw-primary-light);
  box-shadow: var(--hw-shadow-hover);
}

.faq-item h4 { 
  font-size: 18px; 
  font-weight: 600; 
  color: var(--hw-text);
  margin-bottom: 12px;
}

.faq-item p { 
  font-size: 15px; 
  color: var(--hw-text-secondary);
  line-height: 1.7;
}


</style>
