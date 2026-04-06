<template>
  <div class="post-detail">
    <div class="container">
      <!-- 帖子骨架屏 -->
      <div v-if="loading" class="post-skeleton">
        <div class="skeleton-header">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-author-info">
            <div class="skeleton-author-name"></div>
            <div class="skeleton-meta">
              <div class="skeleton-meta-item" v-for="i in 5" :key="i"></div>
            </div>
          </div>
        </div>
        <div class="skeleton-title"></div>
        <div class="skeleton-tags">
          <div class="skeleton-tag" v-for="i in 3" :key="i"></div>
        </div>
        <div class="skeleton-content">
          <div class="skeleton-content-line" v-for="i in 8" :key="i"></div>
        </div>
        <div class="skeleton-actions">
          <div class="skeleton-action" v-for="i in 4" :key="i"></div>
        </div>
      </div>
      
      <!-- 帖子内容 -->
      <el-card v-if="post" class="post-card" v-show="!loading">
        <div class="post-header">
          <h2 class="post-title">{{ post.title }}</h2>
          <div class="post-meta">
            <div class="author-info">
              <el-avatar :size="48" :src="post.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="author-avatar"></el-avatar>
              <div class="author-details">
                <span class="author-name">{{ post.authorName || '匿名用户' }}</span>
                <div class="post-stats">
                  <span class="stat-item"><i class="el-icon-time"></i> {{ formatDate(post.createdAt) }}</span>
                  <span class="stat-item"><i class="el-icon-folder"></i> {{ post.category }}</span>
                  <span class="stat-item"><i class="el-icon-view"></i> {{ post.viewCount }} 浏览</span>
                  <span class="stat-item"><i class="el-icon-thumb"></i> {{ post.likeCount }} 点赞</span>
                  <span class="stat-item"><i class="el-icon-chat-dot-round"></i> {{ post.commentCount }} 评论</span>
                </div>
              </div>
            </div>
            <div v-if="post.tags" class="post-tags">
              <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small" effect="plain" class="tag-item">{{ tag.trim() }}</el-tag>
            </div>
          </div>
        </div>
        <div class="post-content" v-html="post.content"></div>
        <div class="post-actions">
          <el-button 
            :type="isLiked ? 'primary' : 'default'"
            @click="toggleLike"
            icon="el-icon-thumb"
            class="action-btn"
          >
            {{ post.likeCount }} 点赞
          </el-button>
          <el-button 
            :type="isFavorited ? 'warning' : 'default'"
            @click="toggleFavorite"
            icon="el-icon-star-on"
            class="action-btn"
          >
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button 
            type="default"
            @click="sharePost"
            icon="el-icon-share"
            class="action-btn"
          >
            分享
          </el-button>
          <el-button 
            type="default"
            @click="showCommentForm = true"
            icon="el-icon-chat-line-round"
            class="action-btn"
          >
            {{ post.commentCount }} 评论
          </el-button>
          <el-button 
            v-if="post.userId === currentUserId" 
            type="warning"
            @click="navigateToEdit"
            icon="el-icon-edit"
            class="action-btn"
          >
            编辑
          </el-button>
          <el-button 
            v-if="post.userId === currentUserId" 
            type="danger"
            @click="deletePost"
            icon="el-icon-delete"
            class="action-btn"
          >
            删除
          </el-button>
        </div>
      </el-card>
      
      <!-- 评论区 -->
      <div class="comment-section">
        <div class="comment-header-section">
          <h3 class="comment-title">评论 ({{ post && post.commentCount ? post.commentCount : 0 }})</h3>
          <el-button type="primary" @click="showCommentForm = true" class="comment-btn">
            <i class="el-icon-edit"></i> 发表评论
          </el-button>
        </div>
        
        <!-- 评论表单 -->
        <el-card v-if="showCommentForm" class="comment-form" shadow="hover">
          <div class="form-header">
            <el-avatar :size="40" :src="userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="form-avatar"></el-avatar>
            <span class="form-username">{{ username || '匿名用户' }}</span>
          </div>
          <el-input
            type="textarea"
            v-model="commentContent"
            placeholder="写下你的评论..."
            :rows="4"
            resize="none"
            class="comment-textarea"
          ></el-input>
          <div class="form-actions">
            <el-button @click="showCommentForm = false" class="cancel-btn">取消</el-button>
            <el-button type="primary" @click="submitComment" :loading="submitting" class="submit-btn">
              发布评论
            </el-button>
          </div>
        </el-card>
        
        <!-- 评论列表 -->
        <div class="comment-list">
          <el-card v-for="comment in comments" :key="comment.id" class="comment-item" shadow="hover">
            <div class="comment-header">
              <el-avatar :size="40" :src="comment.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="comment-avatar"></el-avatar>
              <div class="comment-header-info">
                <div class="comment-author-row">
                  <span class="comment-author">{{ comment.username }}</span>
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                  <el-tag v-if="comment.isBestAnswer" type="success" size="small" class="best-answer-tag">最佳答案</el-tag>
                </div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button 
                size="small" 
                :type="comment.isLiked ? 'primary' : 'default'"
                @click="toggleCommentLike(comment.id)"
                class="comment-action-btn"
              >
                <i class="el-icon-thumb"></i> {{ comment.likeCount }}
              </el-button>
              <el-button size="small" @click="replyToComment(comment.id)" class="comment-action-btn">
                <i class="el-icon-chat-dot-round"></i> 回复
              </el-button>
              <el-button 
                v-if="post.type === 'question' && post.userId === currentUserId && !post.bestAnswerId"
                size="small" 
                type="success"
                @click="setBestAnswer(comment.id)"
                class="comment-action-btn"
              >
                <i class="el-icon-check"></i> 设为最佳答案
              </el-button>
            </div>
            
            <!-- 回复列表 -->
            <div class="reply-list" v-if="comment.replies && comment.replies.length > 0">
              <el-card v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <el-avatar :size="32" :src="reply.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="reply-avatar"></el-avatar>
                  <div class="reply-header-info">
                    <div class="reply-author-row">
                      <span class="reply-author">{{ reply.username }}</span>
                      <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                    </div>
                  </div>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="reply-actions">
                  <el-button size="mini" @click="replyToReply(comment.id, reply.id)" class="reply-action-btn">
                    <i class="el-icon-chat-dot-round"></i> 回复
                  </el-button>
                </div>
                
                <!-- 嵌套回复 -->
                <div class="nested-reply-list" v-if="reply.replies && reply.replies.length > 0">
                  <el-card v-for="nestedReply in reply.replies" :key="nestedReply.id" class="nested-reply-item">
                    <div class="reply-header">
                      <el-avatar :size="28" :src="nestedReply.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="reply-avatar"></el-avatar>
                      <div class="reply-header-info">
                        <div class="reply-author-row">
                          <span class="reply-author">{{ nestedReply.username }}</span>
                          <span class="reply-time">{{ formatDate(nestedReply.createdAt) }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="reply-content">{{ nestedReply.content }}</div>
                    <div class="reply-actions">
                      <el-button size="mini" @click="replyToReply(comment.id, reply.id, nestedReply.id)" class="reply-action-btn">
                        <i class="el-icon-chat-dot-round"></i> 回复
                      </el-button>
                    </div>
                  </el-card>
                </div>
              </el-card>
            </div>
            
            <!-- 回复表单 -->
            <div v-if="replyingTo === comment.id" class="reply-form">
              <div class="form-header">
                <el-avatar :size="32" :src="userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="form-avatar"></el-avatar>
                <span class="form-username">{{ username || '匿名用户' }}</span>
              </div>
              <el-input
                type="textarea"
                v-model="replyContent"
                placeholder="写下你的回复..."
                :rows="3"
                resize="none"
                class="reply-textarea"
              ></el-input>
              <div class="form-actions">
                <el-button size="small" @click="replyingTo = null" class="cancel-btn">取消</el-button>
                <el-button size="small" type="primary" @click="submitReply(comment.id)" :loading="submitting" class="submit-btn">
                  发布回复
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 空状态 -->
        <div v-if="!loading && comments.length === 0" class="empty-comments">
          <el-empty description="暂无评论，快来发表第一条评论吧！" />
        </div>
        
        <!-- 评论分页 -->
        <div class="pagination" v-if="pagination.total > 0">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-sizes="[10, 20, 50]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            class="comment-pagination"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { postAPI, commentAPI, likeAPI, favoriteAPI } from '@/api/community'
import { messageAPI } from '@/api/message'

export default {
  name: 'PostDetail',
  data() {
    return {
      post: null,
      comments: [],
      isLiked: false,
      isFavorited: false,
      showCommentForm: false,
      commentContent: '',
      replyingTo: null,
      replyingToReply: null,
      replyingToNestedReply: null,
      replyContent: '',
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      currentUserId: null,
      username: '',
      userAvatar: '',
      submitting: false,
      loading: false
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadPostDetail()
    this.loadComments()
  },
  methods: {
    loadUserInfo() {
      const user = this.$store.state.user
      if (user.token) {
        this.currentUserId = user.id || user.userId
        this.username = user.username
        this.userAvatar = user.avatar
      }
    },
    loadPostDetail() {
      const id = this.$route.params.id
      this.loading = true
      postAPI.getPostDetail(id).then(res => {
        if (res) {
          this.post = res
          // 检查是否已点赞
          this.checkLike()
          // 检查是否已收藏
          this.checkFavorite()
        }
      }).catch(err => {
        console.error('加载帖子详情失败:', err)
        this.$message.error('加载帖子详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    loadComments() {
      const postId = this.$route.params.id
      this.loading = true
      commentAPI.getCommentsByPostId(postId, {
        page: this.pagination.currentPage,
        size: this.pagination.pageSize
      }).then(res => {
        if (res && res.records) {
          this.comments = res.records
          this.pagination.total = res.total || 0
          // 加载每个评论的回复
          this.comments.forEach(comment => {
            this.loadReplies(comment)
          })
        } else {
          this.comments = []
          this.pagination.total = 0
        }
      }).catch(err => {
        console.error('加载评论失败:', err)
        this.$message.error('加载评论失败')
      }).finally(() => {
        this.loading = false
      })
    },
    loadReplies(comment) {
      commentAPI.getRepliesByCommentId(comment.id).then(res => {
        if (res) {
          comment.replies = res
          // 递归加载嵌套回复
          comment.replies.forEach(reply => {
            this.loadNestedReplies(reply)
          })
        } else {
          comment.replies = []
        }
      })
    },
    loadNestedReplies(reply) {
      commentAPI.getRepliesByCommentId(reply.id).then(res => {
        if (res) {
          reply.replies = res
        } else {
          reply.replies = []
        }
      })
    },
    checkLike() {
      if (this.post && this.currentUserId) {
        likeAPI.checkLike(this.post.id, 'post').then(res => {
          if (res) {
            this.isLiked = res
          }
        })
      }
    },
    toggleLike() {
      if (!this.currentUserId) {
        this.$message.error('请先登录')
        return
      }
      likeAPI.toggleLike(this.post.id, 'post').then(res => {
        this.isLiked = !this.isLiked
        this.post.likeCount += this.isLiked ? 1 : -1
        this.$message.success(this.isLiked ? '点赞成功' : '取消点赞')
      })
    },
    checkFavorite() {
      if (this.post && this.currentUserId) {
        favoriteAPI.checkFavorite(this.post.id).then(res => {
          if (res) {
            this.isFavorited = res
          }
        })
      }
    },
    toggleFavorite() {
      if (!this.currentUserId) {
        this.$message.error('请先登录')
        return
      }
      favoriteAPI.toggleFavorite(this.post.id).then(res => {
        this.isFavorited = res
        this.$message.success(this.isFavorited ? '收藏成功' : '取消收藏')
      })
    },
    sharePost() {
      // 实现分享功能
      const shareUrl = window.location.href
      if (navigator.share) {
        // 使用Web Share API
        navigator.share({
          title: this.post.title,
          text: this.post.content.substring(0, 100),
          url: shareUrl
        }).catch(err => {
          console.error('分享失败:', err)
        })
      } else {
        // 复制链接到剪贴板
        navigator.clipboard.writeText(shareUrl).then(() => {
          this.$message.success('链接已复制到剪贴板')
        }).catch(err => {
          console.error('复制失败:', err)
          this.$message.error('分享失败，请手动复制链接')
        })
      }
    },
    toggleCommentLike(commentId) {
      if (!this.currentUserId) {
        this.$message.error('请先登录')
        return
      }
      likeAPI.toggleLike(commentId, 'comment').then(res => {
        const comment = this.comments.find(c => c.id === commentId)
        if (comment) {
          comment.isLiked = !comment.isLiked
          comment.likeCount += comment.isLiked ? 1 : -1
          this.$message.success(comment.isLiked ? '点赞成功' : '取消点赞')
        }
      })
    },
    submitComment() {
      if (!this.currentUserId) {
        this.$message.error('请先登录')
        return
      }
      if (!this.commentContent.trim()) {
        this.$message.error('评论内容不能为空')
        return
      }
      this.submitting = true
      commentAPI.createComment({
        postId: this.post.id,
        content: this.commentContent,
        parentId: 0
      }).then(res => {
        this.$message.success('评论成功')
        this.showCommentForm = false
        this.commentContent = ''
        this.loadComments()
        this.post.commentCount++
        
        // 发送评论通知给帖子作者
        if (this.post.userId !== this.currentUserId) {
          this.sendCommentNotification(this.post.userId, this.post.id, this.post.title)
        }
      }).catch(err => {
        console.error('发布评论失败:', err)
        this.$message.error('发布评论失败，请稍后重试')
      }).finally(() => {
        this.submitting = false
      })
    },
    replyToComment(commentId) {
      this.replyingTo = commentId
      this.replyingToReply = null
      this.replyingToNestedReply = null
    },
    replyToReply(commentId, replyId, nestedReplyId = null) {
      this.replyingTo = commentId
      this.replyingToReply = replyId
      this.replyingToNestedReply = nestedReplyId
    },
    submitReply(commentId) {
      if (!this.currentUserId) {
        this.$message.error('请先登录')
        return
      }
      if (!this.replyContent.trim()) {
        this.$message.error('回复内容不能为空')
        return
      }
      this.submitting = true
      commentAPI.createComment({
        postId: this.post.id,
        content: this.replyContent,
        parentId: commentId
      }).then(res => {
        this.$message.success('回复成功')
        this.replyingTo = null
        this.replyingToReply = null
        this.replyContent = ''
        this.loadComments()
        this.post.commentCount++
        
        // 发送回复通知给被回复的用户
        const comment = this.comments.find(c => c.id === commentId)
        if (comment && comment.userId !== this.currentUserId) {
          this.sendCommentNotification(comment.userId, this.post.id, this.post.title)
        }
      }).catch(err => {
        console.error('发布回复失败:', err)
        this.$message.error('发布回复失败，请稍后重试')
      }).finally(() => {
        this.submitting = false
      })
    },
    sendCommentNotification(targetUserId, postId, postTitle) {
      messageAPI.sendNotification({
        targetUserId,
        type: 'comment',
        content: `有人评论了你的帖子《${postTitle}》`,
        relatedId: postId,
        relatedType: 'post'
      }).then(res => {
        if (res.code !== 200) {
          console.error('发送通知失败:', res)
        }
      }).catch(err => {
        console.error('发送通知失败:', err)
      })
    },
    setBestAnswer(commentId) {
      postAPI.setBestAnswer(this.post.id, commentId).then(res => {
        this.$message.success('设置成功')
        this.post.bestAnswerId = commentId
        // 更新评论列表中的最佳答案标记
        this.comments.forEach(comment => {
          comment.isBestAnswer = comment.id === commentId
        })
      })
    },
    navigateToEdit() {
      this.$router.push(`/community/edit/${this.post.id}`)
    },
    deletePost() {
      this.$confirm('确定要删除这篇帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        postAPI.deletePost(this.post.id).then(res => {
          this.$message.success('删除成功')
          this.$router.push('/community')
        })
      }).catch(() => {
        // 取消删除
      })
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.loadComments()
    },
    handleCurrentChange(current) {
      this.pagination.currentPage = current
      this.loadComments()
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor(diff / (1000 * 60))
      
      if (days > 0) {
        return `${days}天前`
      } else if (hours > 0) {
        return `${hours}小时前`
      } else if (minutes > 0) {
        return `${minutes}分钟前`
      } else {
        return '刚刚'
      }
    }
  }
}
</script>

<style scoped>
.post-detail {
  padding: 30px 0;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.post-card {
  margin-bottom: 40px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: fadeIn 0.5s ease-out;
}

.post-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.post-header {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.post-title {
  font-size: 28px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 20px;
  line-height: 1.3;
}

.post-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.author-info {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.author-avatar {
  flex-shrink: 0;
}

.author-details {
  flex: 1;
}

.author-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.post-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tag-item {
  border-radius: 16px;
  font-size: 12px;
}

.post-content {
  padding: 24px;
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.post-actions {
  padding: 0 24px 24px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comment-section {
  margin-top: 50px;
}

.comment-header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
  flex-wrap: wrap;
  gap: 12px;
}

.comment-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.comment-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.comment-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.comment-form {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.form-avatar {
  flex-shrink: 0;
}

.form-username {
  font-weight: 600;
  color: #333;
}

.comment-textarea {
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
}

.reply-textarea {
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  gap: 12px;
}

.cancel-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.submit-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
}

.comment-item {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-header-info {
  flex: 1;
}

.comment-author-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.comment-author {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.best-answer-tag {
  margin-left: auto;
  border-radius: 12px;
}

.comment-content {
  margin-bottom: 16px;
  line-height: 1.6;
  color: #333;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.comment-action-btn {
  border-radius: 6px;
  transition: all 0.2s ease;
}

.comment-action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.reply-list {
  margin-top: 16px;
  padding-left: 52px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 嵌套回复 */
.nested-reply-list {
  margin-top: 12px;
  padding-left: 40px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.nested-reply-item {
  border-radius: 6px;
  padding: 12px;
  background-color: #f8f9fa;
}

.reply-item {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.reply-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.reply-header {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-header-info {
  flex: 1;
}

.reply-author-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.reply-author {
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.reply-time {
  font-size: 11px;
  color: #999;
}

.reply-content {
  font-size: 13px;
  line-height: 1.5;
  color: #666;
  margin-bottom: 8px;
}

.reply-actions {
  display: flex;
  gap: 4px;
}

.reply-action-btn {
  border-radius: 4px;
  transition: all 0.2s ease;
  font-size: 11px;
  padding: 2px 8px;
}

.reply-action-btn:hover {
  transform: translateY(-1px);
}

.reply-form {
  margin-top: 16px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.empty-comments {
  text-align: center;
  padding: 60px 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 32px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.comment-pagination {
  display: flex;
  justify-content: center;
}

/* 骨架屏样式 */
.post-skeleton {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
}

.skeleton-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f0f0f0;
  animation: skeleton-loading 1.5s ease-in-out infinite;
}

.skeleton-author-info {
  flex: 1;
}

.skeleton-author-name {
  width: 150px;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.2s;
}

.skeleton-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.skeleton-meta-item {
  width: 100px;
  height: 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.3s;
}

.skeleton-title {
  width: 90%;
  height: 32px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 20px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.4s;
}

.skeleton-tags {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.skeleton-tag {
  width: 80px;
  height: 24px;
  background-color: #f0f0f0;
  border-radius: 12px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.5s;
}

.skeleton-content {
  margin-bottom: 24px;
}

.skeleton-content-line {
  width: 100%;
  height: 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.6s;
}

.skeleton-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.skeleton-action {
  width: 120px;
  height: 36px;
  background-color: #f0f0f0;
  border-radius: 4px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.7s;
}

@keyframes skeleton-loading {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

/* 响应式设计 */
@media (max-width: 992px) {
  .post-detail {
    padding: 25px 0;
  }
  
  .container {
    max-width: 800px;
  }
  
  .post-title {
    font-size: 26px;
  }
  
  .post-actions {
    flex-wrap: wrap;
  }
  
  .action-btn {
    flex: 1 1 calc(50% - 6px);
    min-width: 120px;
  }
}

@media (max-width: 768px) {
  .post-detail {
    padding: 20px 0;
  }
  
  .container {
    padding: 0 15px;
    max-width: 100%;
  }
  
  .post-title {
    font-size: 24px;
  }
  
  .post-header {
    padding: 20px;
  }
  
  .post-content {
    padding: 20px;
  }
  
  .post-actions {
    padding: 0 20px 20px;
    flex-direction: column;
    gap: 8px;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .comment-header-section {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .comment-btn {
    width: 100%;
  }
  
  .post-stats {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .reply-list {
    padding-left: 40px;
  }
  
  .comment-author-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .best-answer-tag {
    margin-left: 0;
  }
  
  .nested-reply-list {
    padding-left: 30px;
  }
}

@media (max-width: 480px) {
  .post-detail {
    padding: 15px 0;
  }
  
  .post-title {
    font-size: 20px;
  }
  
  .post-header {
    padding: 16px;
  }
  
  .post-content {
    padding: 16px;
    font-size: 14px;
  }
  
  .post-actions {
    padding: 0 16px 16px;
  }
  
  .comment-section {
    margin-top: 30px;
  }
  
  .reply-list {
    padding-left: 30px;
  }
  
  .nested-reply-list {
    padding-left: 20px;
  }
  
  .form-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .cancel-btn,
  .submit-btn {
    width: 100%;
  }
}
</style>