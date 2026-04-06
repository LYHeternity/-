import request from '@/utils/request'

export function create(data) {
  return request({ url: '/order', method: 'post', data })
}

export function listMy(params) {
  return request({ url: '/order/my', method: 'get', params })
}

export function listProvider(params) {
  return request({ url: '/order/provider', method: 'get', params })
}

export function getDetail(id) {
  return request({ url: `/order/${id}`, method: 'get' })
}

export function audit(id, data) {
  return request({ url: `/order/${id}/audit`, method: 'post', data })
}

export function setStatus(id, status) {
  return request({ url: `/order/${id}/status`, method: 'put', data: { status } })
}

export function review(id, data) {
  return request({ url: `/order/${id}/review`, method: 'post', data })
}
