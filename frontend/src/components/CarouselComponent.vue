<template>
  <div class="carousel-component">
    <div class="carousel-container">
      <el-carousel 
        :interval="5000" 
        height="500px"
        indicator-position="outside"
        arrow="always"
        class="traditional-carousel"
        @change="onCarouselChange"
        ref="carousel"
      >
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index" class="carousel-slide">
          <div class="slide-content">
            <!-- 背景图片 -->
            <div class="slide-bg" :style="{ backgroundImage: 'url(' + item.image + ')' }"></div>
            <!-- 渐变遮罩 -->
            <div class="slide-overlay"></div>
            <!-- 内容区域 -->
            <div class="slide-inner">
              <div class="content-wrapper">
                <div class="content-left">
                  <h2 class="slide-title">{{ item.title }}</h2>
                  <p class="slide-subtitle">{{ item.description }}</p>
                  <div class="slide-actions">
                    <router-link :to="item.link" class="primary-btn">{{ item.buttonText }}</router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script>
/* 图片素材来源：本地 images 目录 */
const CAROUSEL_IMAGES = [
  '/images/1.png',
  '/images/2.png',
  '/images/3.png',
  '/images/1.png'
]

function buildCarouselItems() {
  return [
    {
      image: CAROUSEL_IMAGES[0],
      title: '创业者综合服务平台',
      description: '一站式对接项目、投资与服务资源，助力创业全周期发展',
      buttonText: '立即体验',
      link: '/register'
    },
    {
      image: CAROUSEL_IMAGES[1],
      title: '精准项目融资对接',
      description: '高效连接创业者与投资人，实现精准匹配',
      buttonText: '查看项目',
      link: '/projects'
    },
    {
      image: CAROUSEL_IMAGES[2],
      title: '专业服务市场',
      description: '汇聚技术、法律、市场等全方位创业服务',
      buttonText: '服务市场',
      link: '/services'
    },
    {
      image: CAROUSEL_IMAGES[3],
      title: '智能管理平台',
      description: '全流程数字化管理，提升创业效率',
      buttonText: '了解详情',
      link: '/home'
    }
  ]
}

export default {
  name: 'CarouselComponent',
  data() {
    return {
      carouselItems: buildCarouselItems(),
      currentSlide: 0
    }
  },
  methods: {
    onCarouselChange(index) {
      this.currentSlide = index
    },
    prevSlide() {
      this.$refs.carousel.prev()
    },
    nextSlide() {
      this.$refs.carousel.next()
    }
  }
}
</script>

<style scoped>
.carousel-component {
  width: 100%;
  overflow: hidden;
}

/* 隐藏滚动条 */
:global(body) {
  overflow-y: auto;
  overflow-x: hidden;
}

/* 传统轮播图样式 */
.carousel-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.traditional-carousel {
  width: 100%;
}

.carousel-slide {
  position: relative;
  overflow: hidden;
}

.slide-content {
  position: relative;
  width: 100%;
  height: 100%;
}

.slide-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: transform 0.5s ease;
  z-index: 1;
}

.carousel-slide:hover .slide-bg {
  transform: scale(1.05);
}

.slide-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.4) 100%);
  z-index: 2;
}

.slide-inner {
  position: relative;
  z-index: 3;
  height: 100%;
  display: flex;
  align-items: center;
}

.content-wrapper {
  width: 100%;
  padding: 0 60px;
}

.content-left {
  max-width: 600px;
}

.slide-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin-bottom: 16px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.slide-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 32px;
  line-height: 1.5;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.slide-actions {
  display: flex;
  gap: 16px;
}

.primary-btn {
  display: inline-block;
  padding: 12px 32px;
  background: #007dff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.primary-btn:hover {
  background: #0066cc;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 125, 255, 0.4);
}

/* Element UI 轮播图自定义样式 */
.traditional-carousel ::v-deep .el-carousel__container {
  height: 100%;
}

.traditional-carousel ::v-deep .el-carousel__item {
  height: 100%;
}

.traditional-carousel ::v-deep .el-carousel__indicator {
  margin: 0 8px;
}

.traditional-carousel ::v-deep .el-carousel__indicator.is-active button {
  background-color: #007dff;
}

.traditional-carousel ::v-deep .el-carousel__arrow {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(5px);
}

.traditional-carousel ::v-deep .el-carousel__arrow:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-container {
    padding: 0 15px;
  }
  
  .content-wrapper {
    padding: 0 30px;
  }
  
  .slide-title {
    font-size: 28px;
  }
  
  .slide-subtitle {
    font-size: 16px;
    margin-bottom: 24px;
  }
  
  .primary-btn {
    padding: 10px 24px;
    font-size: 14px;
  }
}
</style>