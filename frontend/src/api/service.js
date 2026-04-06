import request from '@/utils/request'

export function getDetail(id) {
  return request({ url: `/service/${id}`, method: 'get' })
}

export function toggleFavorite(id) {
  return request({ url: `/service/${id}/favorite`, method: 'post' })
}

export function isFavorited(id) {
  return request({ url: `/service/${id}/favorite`, method: 'get' })
}

export function list(params) {
  return request({ url: '/service', method: 'get', params })
}

export function create(data) {
  return request({ url: '/service', method: 'post', data })
}

export function update(id, data) {
  return request({ url: `/service/${id}`, method: 'put', data })
}

export function remove(id) {
  return request({ url: `/service/${id}`, method: 'delete' })
}
