import request from '@/utils/request'

export function list(params) {
  return request({ url: '/message/list', method: 'get', params })
}

export function unreadCount() {
  return request({ url: '/message/unread-count', method: 'get' })
}

export function markRead(id) {
  return request({ url: `/message/${id}/read`, method: 'put' })
}

export function chat(withUserId, limit) {
  return request({ url: '/message/chat', method: 'get', params: { withUserId, limit } })
}

export function send(data) {
  return request({ url: '/message/send', method: 'post', data })
}
