import request from '@/utils/request'

export function getProfile() {
  return request({ url: '/user/profile', method: 'get' })
}

export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}

export function changePassword(data) {
  return request({ url: '/user/password', method: 'put', data })
}

export function getFavoriteProjects() {
  return request({ url: '/user/favorites/projects', method: 'get' })
}

export function getFavoriteResources() {
  return request({ url: '/user/favorites/resources', method: 'get' })
}
