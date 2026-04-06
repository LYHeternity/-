import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
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
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的是blob类型，直接返回
    if (response.config.responseType === 'blob') {
      return res
    }
    
    // 如果不是对象或为null，直接返回
    if (typeof res !== 'object' || res === null) {
      return res
    }
    
    // 解构响应数据
    const { code, message, data } = res
    
    // 如果code为200，返回data数据
    if (code === 200) {
      return data !== undefined ? data : res
    }
    
    // 如果code不为200，显示错误信息
    Message.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  error => {
    console.error('Error:', error)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          Message.error('请先登录')
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          store.commit('user/SET_TOKEN', null)
          store.commit('user/SET_USER', null)
          window.location.href = '/login'
          break
        case 403:
          // 无权限
          Message.error('无权限操作')
          break
        case 404:
          // 资源不存在
          Message.error('请求的资源不存在')
          break
        case 500:
          // 服务器错误
          Message.error('服务器错误，请稍后重试')
          break
        default:
          Message.error('请求失败，请稍后重试')
      }
    } else {
      Message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
