import request from '@/utils/request'

// 帖子相关API
export const postAPI = {
  // 获取帖子列表
  getPostList: (params) => request.get('/community/posts', { params }),
  // 获取帖子详情
  getPostDetail: (id) => request.get(`/community/posts/${id}`),
  // 发布帖子
  createPost: (data) => request.post('/community/posts', data),
  // 更新帖子
  updatePost: (id, data) => request.put(`/community/posts/${id}`, data),
  // 删除帖子
  deletePost: (id) => request.delete(`/community/posts/${id}`),
  // 设置最佳答案
  setBestAnswer: (id, commentId) => request.put(`/community/posts/${id}/best-answer`, { commentId })
}

// 评论相关API
export const commentAPI = {
  // 获取帖子评论列表
  getCommentsByPostId: (postId, params) => request.get(`/community/comments/post/${postId}`, { params }),
  // 获取评论的回复列表
  getRepliesByCommentId: (commentId) => request.get(`/community/comments/${commentId}/replies`),
  // 发布评论
  createComment: (data) => request.post('/community/comments', data),
  // 更新评论
  updateComment: (id, data) => request.put(`/community/comments/${id}`, data),
  // 删除评论
  deleteComment: (id) => request.delete(`/community/comments/${id}`)
}

// 点赞相关API
export const likeAPI = {
  // 检查是否已点赞
  checkLike: (targetId, targetType) => request.get('/community/likes/check', { params: { targetId, targetType } }),
  // 切换点赞状态
  toggleLike: (targetId, targetType) => request.post('/community/likes/toggle', {}, { params: { targetId, targetType } })
}

// 关注相关API
export const followAPI = {
  // 检查是否已关注
  checkFollow: (followUserId) => request.get('/community/follows/check', { params: { followUserId } }),
  // 切换关注状态
  toggleFollow: (followUserId) => request.post('/community/follows/toggle', { followUserId }),
  // 获取用户关注数
  getFollowCount: (userId) => request.get(`/community/follows/count/following/${userId}`),
  // 获取用户粉丝数
  getFollowerCount: (userId) => request.get(`/community/follows/count/followers/${userId}`)
}

// 收藏相关API
export const favoriteAPI = {
  // 检查是否已收藏
  checkFavorite: (postId) => request.get(`/community/posts/${postId}/favorite`),
  // 切换收藏状态
  toggleFavorite: (postId) => request.post(`/community/posts/${postId}/favorite`),
  // 获取用户收藏的帖子列表
  getFavoritePosts: (params) => request.get('/user/favorites/posts', { params })
}