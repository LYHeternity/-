/**
 * Axios 封装：baseURL、请求头携带 JWT、响应拦截统一提示
 */
import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  err => Promise.reject(err)
)

request.interceptors.response.use(
  res => {
    if (res.config.responseType === 'blob') {
      return res.data
    }
    const body = res.data
    if (typeof body !== 'object' || body === null) {
      return body
    }
    const { code, message, data } = body
    if (code === 200) {
      return data !== undefined ? data : body
    }
    Message.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  err => {
    if (err.response) {
      if (err.response.status === 401) {
        Message.error('请先登录')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      } else if (err.response.status === 403) {
        Message.error('权限不足')
      } else {
        const skipMsg = err.config && err.config.skipGlobalError
        if (!skipMsg && err.config.responseType !== 'blob') {
          const msg = err.response.data?.message || err.message || '网络错误'
          Message.error(msg)
        }
      }
    } else {
      if (!(err.config && err.config.skipGlobalError))
        Message.error(err.message || '网络错误')
    }
    return Promise.reject(err)
  }
)

export default request
