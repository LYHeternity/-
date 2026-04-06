<template>
  <div class="favorite-posts-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">我的收藏</h1>
        <p class="page-subtitle">管理您收藏的社区帖子</p>
      </div>

      <div class="content-section">
        <div v-loading="loading" class="posts-list">
          <div v-if="posts.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无收藏的帖子">
              <el-button type="primary" @click="goToCommunity">浏览社区</el-button>
            </el-empty>
          </div>
          <div v-else class="posts-grid">
            <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover">
              <div class="post-container" @click="navigateToDetail(post.id)">
                <div class="post-header">
                  <div class="author-info">
                    <el-avatar :size="40" :src="post.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="author-avatar"></el-avatar>
                    <div class="author-details">
                      <span class="author-name">{{ post.authorName || '匿名用户' }}</span>
                      <span class="post-time">{{ formatDate(post.createdAt) }}</span>
                    </div>
                  </div>
                  <span class="post-type" :class="post.type">{{ post.type === 'article' ? '文章' : '问题' }}</span>
                </div>
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="post-content" v-html="truncateContent(post.content)"></div>
                <div class="post-tags" v-if="post.tags">
                  <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small" effect="plain" class="tag-item">{{ tag.trim() }}</el-tag>
                </div>
                <div class="post-footer">
                  <div class="post-stats">
                    <span class="stat-item">
                      <i class="el-icon-view"></i> {{ post.viewCount }}
                    </span>
                    <span class="stat-item">
                      <i class="el-icon-chat-dot-round"></i> {{ post.commentCount }}
                    </span>
                    <span class="stat-item">
                      <i class="el-icon-thumb"></i> {{ post.likeCount }}
                    </span>
                  </div>
                  <div class="post-actions">
                    <el-button size="small" type="primary" @click.stop="navigateToDetail(post.id)" class="action-btn">
                      查看详情
                    </el-button>
                    <el-button size="small" @click.stop="removeFavorite(post.id)" class="action-btn">
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <div v-if="posts.length > 0" class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            class="post-pagination"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { favoriteAPI } from '@/api/community'

export default {
  name: 'FavoritePosts',
  data() {
    return {
      posts: [],
      loading: false,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadFavoritePosts()
  },
  methods: {
    loadFavoritePosts() {
      this.loading = true
      favoriteAPI.getFavoritePosts({
        page: this.pagination.currentPage,
        size: this.pagination.pageSize
      }).then(res => {
        if (res) {
          this.posts = res || []
          this.pagination.total = res.length || 0
        } else {
          this.posts = []
          this.pagination.total = 0
        }
      }).catch(err => {
        console.error('加载收藏帖子失败:', err)
        this.$message.error('加载收藏帖子失败')
        this.posts = []
        this.pagination.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    removeFavorite(postId) {
      this.$confirm('确定要取消收藏这个帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        favoriteAPI.toggleFavorite(postId).then(res => {
          if (res.code === 200) {
            this.$message.success('已取消收藏')
            this.loadFavoritePosts()
          }
        }).catch(err => {
          console.error('取消收藏失败:', err)
          this.$message.error('取消收藏失败')
        })
      }).catch(() => {
        // 取消删除
      })
    },
    navigateToDetail(id) {
      this.$router.push(`/community/post/${id}`)
    },
    goToCommunity() {
      this.$router.push('/community')
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.currentPage = 1
      this.loadFavoritePosts()
    },
    handleCurrentChange(current) {
      this.pagination.currentPage = current
      this.loadFavoritePosts()
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
    },
    truncateContent(content) {
      const plainText = content.replace(/<[^>]+>/g, '')
      return plainText.length > 150 ? plainText.substring(0, 150) + '...' : plainText
    }
  }
}
</script>

<style scoped>
.favorite-posts-page {
  padding: 30px 0;
  min-height: calc(100vh - 120px);
  background-color: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 8px;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  font-weight: 500;
  margin: 0;
}

.content-section {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
  min-height: 400px;
}

.posts-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.post-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #e8e8e8;
  height: 100%;
  display: flex;
  flex-direction: column;
  animation: fadeIn 0.5s ease-out;
}

.post-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  border-color: #1890ff;
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

.post-container {
  cursor: pointer;
  padding: 20px;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.post-container:hover {
  background-color: rgba(24, 144, 255, 0.05);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  flex-shrink: 0;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-type {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.post-type.article {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.post-type.question {
  background-color: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.post-title:hover {
  color: #1890ff;
}

.post-content {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag-item {
  border-radius: 12px;
  font-size: 12px;
  padding: 2px 8px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #999;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: #1890ff;
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  border-radius: 6px;
  transition: all 0.3s ease;
  pointer-events: auto;
  cursor: pointer;
  user-select: none;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

.post-pagination {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .favorite-posts-page {
    padding: 20px 0;
  }
  
  .container {
    padding: 0 15px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .content-section {
    padding: 16px;
  }
  
  .posts-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .post-stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .post-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .action-btn {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .post-container {
    padding: 16px;
  }
  
  .post-title {
    font-size: 16px;
  }
  
  .post-content {
    font-size: 13px;
  }
}
</style>
