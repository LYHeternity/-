<template>
  <div class="favorites-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
      <p class="page-subtitle">管理您收藏的项目、服务、文章和社区帖子</p>
    </div>

    <!-- 标签页 -->
    <div class="tabs-container">
      <el-tabs v-model="activeTab" type="card" class="favorites-tabs">
        <el-tab-pane label="收藏的项目" name="projects">
          <div v-loading="loading.projects" class="favorites-list">
            <div v-if="favoriteProjects.length === 0" class="empty-state">
              <i class="el-icon-collection-tag"></i>
              <h3>暂无收藏的项目</h3>
              <p>浏览项目并收藏感兴趣的内容</p>
              <el-button type="primary" @click="$router.push('/projects')">浏览项目</el-button>
            </div>
            <div v-else class="projects-grid">
              <div v-for="project in favoriteProjects" :key="project.id" class="project-card">
                <div class="project-image">
                  <img :src="getImageUrl(project.projectImage)" :alt="project.title" />
                </div>
                <div class="project-info">
                  <div class="project-status" :class="project.status">
                    {{ getStatusText(project.status) }}
                  </div>
                  <h3 class="project-title">{{ project.title }}</h3>
                  <p class="project-subtitle">{{ project.subtitle }}</p>
                  <div class="project-meta">
                    <span class="meta-item">{{ project.industry }}</span>
                    <span class="meta-item">{{ project.stage }}</span>
                    <span class="meta-item">{{ project.fundingNeeded }}万</span>
                  </div>
                  <div class="project-stats">
                    <span class="stat-item"><i class="el-icon-view"></i> {{ project.viewCount || 0 }}</span>
                    <span class="stat-item"><i class="el-icon-thumb"></i> {{ project.likeCount || 0 }}</span>
                  </div>
                  <div class="project-actions">
                    <el-button type="primary" size="small" @click="goToProject(project.id)">
                      查看详情
                    </el-button>
                    <el-button type="danger" size="small" @click="removeFavorite('project', project.id, project)">
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收藏的服务" name="services">
          <div v-loading="loading.services" class="favorites-list">
            <div v-if="favoriteServices.length === 0" class="empty-state">
              <i class="el-icon-s-service"></i>
              <h3>暂无收藏的服务</h3>
              <p>浏览服务并收藏感兴趣的内容</p>
              <el-button type="primary" @click="$router.push('/services')">浏览服务</el-button>
            </div>
            <div v-else class="services-grid">
              <div v-for="service in favoriteServices" :key="service.id" class="service-card">
                <div class="service-image">
                  <img :src="getImageUrl(service.resourceImage)" :alt="service.title" />
                </div>
                <div class="service-info">
                  <div class="service-category">{{ service.category }}</div>
                  <h3 class="service-title">{{ service.title }}</h3>
                  <p class="service-subtitle">{{ service.subtitle }}</p>
                  <div class="service-price">¥{{ service.price }}</div>
                  <div class="service-meta">
                    <span class="meta-item">{{ service.serviceCycle }}</span>
                    <span class="meta-item">{{ service.providerName }}</span>
                  </div>
                  <div class="service-stats">
                    <span class="stat-item"><i class="el-icon-view"></i> {{ service.viewCount || 0 }}</span>
                    <span class="stat-item"><i class="el-icon-s-comment"></i> {{ service.reviewCount || 0 }}</span>
                  </div>
                  <div class="service-actions">
                    <el-button type="primary" size="small" @click="goToService(service.id)">
                      查看详情
                    </el-button>
                    <el-button type="danger" size="small" @click="removeFavorite('resource', service.id, service)">
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收藏的文章" name="articles">
          <div v-loading="loading.articles" class="favorites-list">
            <div v-if="favoriteArticles.length === 0" class="empty-state">
              <i class="el-icon-document"></i>
              <h3>暂无收藏的文章</h3>
              <p>浏览文章并收藏感兴趣的内容</p>
              <el-button type="primary" @click="$router.push('/articles')">浏览文章</el-button>
            </div>
            <div v-else class="articles-grid">
              <div v-for="article in favoriteArticles" :key="article.id" class="article-card">
                <div class="article-info">
                  <h3 class="article-title">{{ article.title }}</h3>
                  <p class="article-content">{{ truncateContent(article.content) }}</p>
                  <div class="article-meta">
                    <span class="meta-item">{{ article.source }}</span>
                    <span class="meta-item">{{ formatDate(article.publishDate) }}</span>
                  </div>
                  <div class="article-stats">
                    <span class="stat-item"><i class="el-icon-view"></i> {{ article.viewCount || 0 }}</span>
                    <span class="stat-item"><i class="el-icon-thumb"></i> {{ article.likeCount || 0 }}</span>
                  </div>
                  <div class="article-actions">
                    <el-button type="primary" size="small" @click="goToArticle(article.id)">
                      查看详情
                    </el-button>
                    <el-button type="danger" size="small" @click="removeFavorite('article', article.id, article)">
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收藏的帖子" name="posts">
          <div v-loading="loading.posts" class="favorites-list">
            <div v-if="favoritePosts.length === 0" class="empty-state">
              <i class="el-icon-chat-line-round"></i>
              <h3>暂无收藏的帖子</h3>
              <p>浏览社区并收藏感兴趣的内容</p>
              <el-button type="primary" @click="$router.push('/community')">浏览社区</el-button>
            </div>
            <div v-else class="posts-grid">
              <div v-for="post in favoritePosts" :key="post.id" class="post-card">
                <div class="post-info">
                  <div class="post-type" :class="post.type">{{ post.type === 'article' ? '文章' : '问题' }}</div>
                  <h3 class="post-title">{{ post.title }}</h3>
                  <p class="post-content">{{ truncateContent(post.content) }}</p>
                  <div v-if="post.tags" class="post-tags">
                    <span v-for="tag in post.tags.split(',')" :key="tag" class="tag-item">{{ tag.trim() }}</span>
                  </div>
                  <div class="post-stats">
                    <span class="stat-item"><i class="el-icon-view"></i> {{ post.viewCount || 0 }}</span>
                    <span class="stat-item"><i class="el-icon-chat-dot-round"></i> {{ post.commentCount || 0 }}</span>
                    <span class="stat-item"><i class="el-icon-thumb"></i> {{ post.likeCount || 0 }}</span>
                  </div>
                  <div class="post-actions">
                    <el-button type="primary" size="small" @click="goToPost(post.id)">
                      查看详情
                    </el-button>
                    <el-button type="danger" size="small" @click="removeFavorite('post', post.id, post)">
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { getFavoriteProjects, getFavoriteResources } from '@/api/user'
import { toggleFavorite as toggleProjectFavorite } from '@/api/project'
import { toggleFavorite as toggleServiceFavorite } from '@/api/resource'
import { toggleFavorite as toggleArticleFavorite } from '@/api/article'
import { favoriteAPI } from '@/api/community'
import request from '@/utils/request'
import { getImageUrl as getImageUrlUtil } from '@/utils/image'

export default {
  name: 'Favorites',
  data() {
    return {
      activeTab: 'projects',
      loading: {
        projects: false,
        services: false,
        articles: false,
        posts: false
      },
      favoriteProjects: [],
      favoriteServices: [],
      favoriteArticles: [],
      favoritePosts: []
    }
  },
  created() {
    this.loadFavorites()
  },
  methods: {
    async loadFavorites() {
      this.loading.projects = true
      this.loading.services = true
      this.loading.articles = true
      this.loading.posts = true
      try {
        // 并行加载所有收藏数据
        const [projects, services, articles, posts] = await Promise.all([
          getFavoriteProjects({ page: 1, size: 10 }),
          getFavoriteResources({ page: 1, size: 10 }),
          this.getFavoriteArticles(),
          this.getFavoritePosts()
        ])
        this.favoriteProjects = projects
        this.favoriteServices = services
        this.favoriteArticles = articles
        this.favoritePosts = posts
      } catch (error) {
        this.$message.error('加载收藏列表失败')
        console.error('加载收藏失败:', error)
      } finally {
        this.loading.projects = false
        this.loading.services = false
        this.loading.articles = false
        this.loading.posts = false
      }
    },
    async getFavoriteArticles() {
      try {
        // 假设后端提供了获取收藏文章的API
        // 如果没有，这里可以返回空数组
        // 实际项目中需要根据后端API调整
        const response = await request.get('/user/favorites/articles', { params: { page: 1, size: 10 } })
        return response || []
      } catch (error) {
        console.error('获取收藏文章失败:', error)
        return []
      }
    },
    async getFavoritePosts() {
      try {
        const response = await request.get('/user/favorites/posts', { params: { page: 1, size: 10 } })
        return response || []
      } catch (error) {
        console.error('获取收藏帖子失败:', error)
        return []
      }
    },
    async removeFavorite(type, id, item) {
      try {
        if (type === 'project') {
          await toggleProjectFavorite(id)
          this.favoriteProjects = this.favoriteProjects.filter(p => p.id !== id)
        } else if (type === 'resource') {
          await toggleServiceFavorite(id)
          this.favoriteServices = this.favoriteServices.filter(s => s.id !== id)
        } else if (type === 'article') {
          await toggleArticleFavorite(id)
          this.favoriteArticles = this.favoriteArticles.filter(a => a.id !== id)
        } else if (type === 'post') {
          await favoriteAPI.toggleFavorite(id)
          this.favoritePosts = this.favoritePosts.filter(p => p.id !== id)
        }
        this.$message.success('已取消收藏')
      } catch (error) {
        this.$message.error('取消收藏失败')
        console.error('取消收藏失败:', error)
      }
    },
    goToProject(id) {
      this.$router.push(`/project/${id}`)
    },
    goToService(id) {
      this.$router.push(`/service/${id}`)
    },
    goToArticle(id) {
      this.$router.push(`/article/detail/${id}`)
    },
    goToPost(id) {
      this.$router.push(`/community/post/${id}`)
    },
    getStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'pending': '审核中',
        'published': '已发布',
        'rejected': '已拒绝',
        'closed': '已关闭'
      }
      return statusMap[status] || '未知状态'
    },
    getImageUrl(imageName) {
      return getImageUrlUtil(imageName)
    },
    truncateContent(content) {
      if (!content) return ''
      // 移除HTML标签
      const plainText = content.replace(/<[^>]+>/g, '')
      // 截断文本
      return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.favorites-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background: #f8fafc;
  color: #1e293b;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
  margin: 0;
}

/* 标签页 */
.tabs-container {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.favorites-tabs {
  width: 100%;
}

.favorites-tabs .el-tabs__header {
  margin-bottom: 24px;
}

.favorites-tabs .el-tabs__item {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  padding: 12px 24px;
}

.favorites-tabs .el-tabs__item.is-active {
  color: #3b82f6;
}

.favorites-tabs .el-tabs__active-bar {
  background: #3b82f6;
  height: 3px;
}

/* 收藏列表 */
.favorites-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 24px;
  color: #cbd5e1;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 24px;
}

/* 项目网格 */
.projects-grid,
.services-grid,
.articles-grid,
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

/* 项目卡片 */
.project-card,
.service-card,
.article-card,
.post-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.3s ease;
}

.article-card:hover,
.post-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.article-info,
.post-info {
  padding: 20px;
}

.article-title,
.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
  line-height: 1.3;
}

.article-content,
.post-content {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-meta,
.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-item {
  font-size: 12px;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 12px;
}

.post-type {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 12px;
}

.post-type.article {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.post-type.question {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.article-stats,
.post-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #64748b;
}

.article-actions,
.post-actions {
  display: flex;
  gap: 8px;
}

.article-actions .el-button,
.post-actions .el-button {
  flex: 1;
  font-size: 12px;
  padding: 8px 16px;
  border-radius: 8px;
}

.project-card:hover,
.service-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.project-image,
.service-image {
  height: 200px;
  overflow: hidden;
}

.project-image img,
.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.project-card:hover .project-image img,
.service-card:hover .service-image img {
  transform: scale(1.05);
}

.project-info,
.service-info {
  padding: 20px;
}

.project-status,
.service-category {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 12px;
}

.project-status.published {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.project-status.pending {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.project-status.rejected {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.service-category {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.project-title,
.service-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1.3;
}

.project-subtitle,
.service-subtitle {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-meta,
.service-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.meta-item {
  font-size: 12px;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 12px;
}

.service-price {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 12px;
}

.project-stats,
.service-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #64748b;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item i {
  font-size: 14px;
}

.project-actions,
.service-actions {
  display: flex;
  gap: 8px;
}

.project-actions .el-button,
.service-actions .el-button {
  flex: 1;
  font-size: 12px;
  padding: 8px 16px;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorites-page {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .tabs-container {
    padding: 16px;
  }
  
  .projects-grid,
  .services-grid {
    grid-template-columns: 1fr;
  }
  
  .project-image,
  .service-image {
    height: 180px;
  }
  
  .project-info,
  .service-info {
    padding: 16px;
  }
  
  .project-actions,
  .service-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .project-title,
  .service-title {
    font-size: 16px;
  }
  
  .project-subtitle,
  .service-subtitle {
    font-size: 13px;
  }
  
  .project-stats,
  .service-stats {
    font-size: 12px;
    gap: 12px;
  }
}
</style>