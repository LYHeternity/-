import request from '@/utils/request'

export function getArticles(params) {
  return request({ url: '/articles', method: 'get', params })
}

export function searchArticles(keyword, params) {
  return request({ url: `/articles/search?keyword=${keyword}`, method: 'get', params })
}

export function getHotArticles(limit) {
  return request({ url: `/articles/hot?limit=${limit}`, method: 'get' })
}

export function getArticleById(id) {
  return request({ url: `/articles/${id}`, method: 'get' })
}

export function toggleFavorite(id) {
  return request({ url: `/articles/${id}/favorite`, method: 'post' })
}

export function isFavorited(id) {
  return request({ url: `/articles/${id}/favorite`, method: 'get' })
}

export function scrapeArticles() {
  return request({ url: '/articles/scrape', method: 'post' })
}
