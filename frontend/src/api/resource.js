import request from '@/utils/request'

export function listMy(params) {
  return request({ url: '/resource/my', method: 'get', params })
}

export function listPublic(params) {
  return request({ url: '/resource/list', method: 'get', params })
}

export function getDetail(id) {
  return request({ url: `/resource/${id}`, method: 'get' })
}

export function publish(data) {
  return request({ url: '/resource', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/resource/${id}`, method: 'put', data })
}

export function setStatus(id, status) {
  return request({ url: `/resource/${id}/status`, method: 'put', data: { status } })
}

export function getReviews(id) {
  return request({ url: `/resource/${id}/reviews`, method: 'get' })
}

export function toggleFavorite(id) {
  return request({ url: `/resource/${id}/favorite`, method: 'post' })
}

export function isFavorited(id) {
  return request({ url: `/resource/${id}/favorite`, method: 'get' })
}
