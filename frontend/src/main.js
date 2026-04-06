import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/style.css'
import webSocketService from './utils/websocket'
import axios from 'axios'

// 配置axios
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10000

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 重置store中的用户状态
      store.commit('user/LOGOUT')
      // 跳转到登录页面
      router.push('/login')
      // 显示过期提示
      Vue.prototype.$message.error('您的账号已过期请重新登录')
    }
    return Promise.reject(error)
  }
)

// 将axios注入到Vue实例中
Vue.prototype.$axios = axios

// 配置Element UI默认样式
Vue.use(ElementUI, {
  size: 'medium',
  zIndex: 9999
})

Vue.config.productionTip = false

// 全局错误处理，忽略重定向错误
Vue.config.errorHandler = function(err, vm, info) {
  // 忽略重定向错误
  if (err.message && err.message.includes('Redirected when going from')) {
    return
  }
  // 其他错误正常处理
  console.error('Vue error:', err, info)
}

/* 滚动渐入指令：元素进入视口 1/3 时添加 .reveal-in */
Vue.directive('reveal', {
  inserted(el) {
    el.classList.add('reveal-item')
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) entry.target.classList.add('reveal-in')
        })
      },
      { threshold: 0.2, rootMargin: '0px 0px -30px 0px' }
    )
    observer.observe(el)
  }
})

new Vue({
  router,
  store,
  render: h => h(App),
  mounted() {
    // 连接WebSocket
    webSocketService.connect()
    
    // 组件销毁时断开连接
    this.$once('hook:beforeDestroy', () => {
      webSocketService.disconnect()
    })
  }
}).$mount('#app')
