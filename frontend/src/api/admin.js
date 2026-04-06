import request from '@/utils/request'

export const adminUser = {
  list: (params) => request({ url: '/admin/user/list', method: 'get', params }),
  create: (data) => request({ url: '/admin/user', method: 'post', data }),
  setStatus: (id, status) => request({ url: `/admin/user/${id}/status`, method: 'put', data: { status } }),
  resetPassword: (id, password) => request({ url: `/admin/user/${id}/reset-password`, method: 'put', data: { password } }),
  update: (id, data) => request({ url: `/admin/user/${id}`, method: 'put', data }),
  batchSetStatus: (ids, status) => request({ url: '/admin/user/batch-status', method: 'put', data: { ids, status } }),
  batchResetPassword: (ids, password) => request({ url: '/admin/user/batch-reset-password', method: 'put', data: { ids, password } }),
  batchDelete: (ids) => request({ url: '/admin/user/batch-delete', method: 'delete', data: { ids } }),
  getUserRoles: (id) => request({ url: `/admin/user/${id}/roles`, method: 'get' }),
  assignRoles: (id, roleCodes) => request({ url: `/admin/user/${id}/roles`, method: 'post', data: roleCodes }),
  removeRole: (id, roleCode) => request({ url: `/admin/user/${id}/roles/${roleCode}`, method: 'delete' }),
  getUsersByRole: (roleCode) => request({ url: `/admin/user/by-role/${roleCode}`, method: 'get' })
}

export const adminAudit = {
  projectList: (params) => request({ url: '/admin/audit/project/list', method: 'get', params }),
  projectAudit: (id, data) => request({ url: `/admin/audit/project/${id}`, method: 'put', data }),
  resourceList: (params) => request({ url: '/admin/audit/resource/list', method: 'get', params }),
  resourceAudit: (id, data) => request({ url: `/admin/audit/resource/${id}`, method: 'put', data })
}

export const adminConfig = {
  list: () => request({ url: '/admin/config/list', method: 'get' }),
  set: (key, value) => request({ url: `/admin/config/key/${key}`, method: 'put', data: { value } })
}

export const adminStats = {
  overview: () => request({ url: '/admin/stats/overview', method: 'get' }),
  userTrend: (params) => request({ url: '/admin/stats/user-trend', method: 'get', params }),
  projectTrend: () => request({ url: '/admin/stats/project-trend', method: 'get' })
}

export const adminLog = {
  list: (params) => request({ url: '/admin/log/list', method: 'get', params })
}

export function exportUsers() {
  return request({
    url: '/admin/export/users',
    method: 'get',
    responseType: 'blob'
  })
}

export function exportLogs() {
  return request({
    url: '/admin/export/logs',
    method: 'get',
    responseType: 'blob'
  })
}

export const adminProject = {
  list: (params) => request({ url: '/admin/project/list', method: 'get', params }),
  getDetail: (id) => request({ url: `/admin/project/${id}`, method: 'get' }),
  update: (id, data) => request({ url: `/admin/project/${id}`, method: 'put', data }),
  resubmitAudit: (id) => request({ url: `/admin/project/${id}/resubmit-audit`, method: 'put' }),
  delete: (id) => request({ url: `/admin/project/${id}`, method: 'delete' })
}

export const adminService = {
  list: (params) => request({ url: '/admin/service/list', method: 'get', params }),
  getDetail: (id) => request({ url: `/admin/service/${id}`, method: 'get' }),
  update: (id, data) => request({ url: `/admin/service/${id}`, method: 'put', data }),
  resubmitAudit: (id) => request({ url: `/admin/service/${id}/resubmit-audit`, method: 'put' }),
  delete: (id) => request({ url: `/admin/service/${id}`, method: 'delete' })
}
