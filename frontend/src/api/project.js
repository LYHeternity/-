import request from '@/utils/request'

export function listMy(params) {
  return request({ url: '/project/my', method: 'get', params })
}

export function listPublic(params) {
  return request({ url: '/project/list', method: 'get', params })
}

export function getDetail(id) {
  return request({ url: `/project/${id}`, method: 'get' })
}

export function publish(data) {
  return request({ url: '/project', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/project/${id}`, method: 'put', data })
}

export function setStatus(id, status) {
  return request({ url: `/project/${id}/status`, method: 'put', data: { status } })
}

export function addProgress(id, data) {
  return request({ url: `/project/${id}/progress`, method: 'post', data })
}

export function getProgress(id) {
  return request({ url: `/project/${id}/progress`, method: 'get' })
}

export function toggleFavorite(id) {
  return request({ url: `/project/${id}/favorite`, method: 'post' })
}

export function isFavorited(id) {
  return request({ url: `/project/${id}/favorite`, method: 'get' })
}
