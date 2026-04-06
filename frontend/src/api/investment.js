import request from '@/utils/request'

export function submit(data) {
  return request({ url: '/investment', method: 'post', data })
}

export function listByProject(projectId, params) {
  return request({ url: `/investment/project/${projectId}`, method: 'get', params })
}

export function listMy(params) {
  return request({ url: '/investment/my', method: 'get', params })
}

export function getDetail(id) {
  return request({ url: `/investment/${id}`, method: 'get' })
}

export function audit(id, data) {
  return request({ url: `/investment/${id}/audit`, method: 'post', data })
}
