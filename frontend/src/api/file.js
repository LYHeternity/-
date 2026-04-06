import request from '@/utils/request'

export function upload(file) {
  const form = new FormData()
  form.append('file', file)
  return request({
    url: '/files/upload',
    method: 'post',
    data: form,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
