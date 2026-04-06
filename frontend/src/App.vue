<template>
  <div id="app">
    <!-- 骨架屏：首屏加载时展示，内容就绪后渐隐 -->
    <div v-show="!appReady" class="app-skeleton">
      <div class="skeleton-header"></div>
      <div class="skeleton-body">
        <div class="skeleton-block skeleton-hero"></div>
        <div class="skeleton-block skeleton-line" style="width:60%; margin:24px 0"></div>
        <div class="skeleton-block skeleton-line" style="width:40%"></div>
        <div class="skeleton-cards">
          <div class="skeleton-block skeleton-card" v-for="i in 4" :key="i"></div>
        </div>
      </div>
    </div>
    <transition name="page-fade">
      <router-view v-show="appReady" />
    </transition>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return { appReady: false }
  },
  mounted() {
    this.$router.afterEach(() => {
      if (!this.appReady) this.appReady = true
    })
    setTimeout(() => { this.appReady = true }, 320)
  }
}
</script>

<style>
.app-skeleton {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: var(--hw-bg, #f8fafc);
  transition: opacity 0.35s ease;
}
.app-skeleton[style*='display: none'] { pointer-events: none; }
.skeleton-header {
  height: 64px;
  background: linear-gradient(90deg, #e2e8f0 0%, #f1f5f9 50%, #e2e8f0 100%);
  background-size: 200% 100%;
  animation: skeleton-shine 1.2s ease-in-out infinite;
}
.skeleton-body { max-width: 1200px; margin: 0 auto; padding: 24px; }
.skeleton-hero { height: 280px; border-radius: 16px; margin-bottom: 32px; }
.skeleton-cards { display: flex; gap: 20px; margin-top: 24px; }
.skeleton-card { flex: 1; height: 120px; border-radius: 12px; }
.page-fade-enter-active { transition: opacity 0.3s ease; }
.page-fade-enter { opacity: 0; }
</style>
