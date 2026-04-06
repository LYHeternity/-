<template>
  <div class="article-list">
    <div class="container">
      <!-- 搜索栏 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索创业资讯..."
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      
      <!-- 头条新闻 -->
      <div v-if="!loading && featuredArticle" class="featured-article">
        <div class="featured-image">
          <img :src="getArticleImage(featuredArticle.category, featuredArticle.id)" :alt="featuredArticle.title">
          <div class="featured-overlay" @click="navigateToDetail(featuredArticle.id)" style="cursor: pointer;">
            <span class="featured-category">{{ featuredArticle.category }}</span>
            <h2 class="featured-title">{{ featuredArticle.title }}</h2>
            <div class="featured-meta">
              <span class="featured-source">{{ featuredArticle.source }}</span>
              <span class="featured-date">{{ featuredArticle.publicationDate }}</span>
            </div>
            <p class="featured-summary">{{ featuredArticle.contentSummary }}</p>
            <el-button type="primary" @click.stop="navigateToDetail(featuredArticle.id)" class="featured-button">阅读全文</el-button>
          </div>
        </div>
      </div>
      
      <!-- 分类标签 -->
      <div class="category-section">
        <div class="category-header">
          <h3 class="section-title">资讯分类</h3>
        </div>
        <div class="category-tabs">
          <el-tabs v-model="selectedCategory" @tab-click="handleCategoryChange" type="card">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="创业资讯" name="创业资讯"></el-tab-pane>
            <el-tab-pane label="投资融资" name="投资融资"></el-tab-pane>
            <el-tab-pane label="科技创新" name="科技创新"></el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <!-- 最新新闻 -->
      <div class="latest-news">
        <div class="section-header">
          <h3 class="section-title">最新资讯</h3>
          <el-select v-model="sortBy" @change="loadArticles" class="sort-select">
            <el-option label="最新发布" value="latest"></el-option>
            <el-option label="相关度" value="relevance"></el-option>
          </el-select>
        </div>
        <div class="article-grid">
          <el-card
            v-for="(article, index) in displayArticles"
            :key="article.id"
            class="article-card"
            v-if="!loading"
          >
            <div @click="navigateToDetail(article.id)" style="cursor: pointer;">
            <div class="article-image">
              <img :src="getArticleImage(article.category, article.id)" :alt="article.title">
              <div class="article-badge" v-if="article.relevanceScore > 0.8">
                热门
              </div>
            </div>
            <div class="article-content">
              <span class="category-tag">{{ article.category }}</span>
              <h4 class="article-title">{{ article.title }}</h4>
              <div class="article-meta">
                <span class="source">{{ article.source }}</span>
                <span class="author" v-if="article.author && article.author !== '未知作者'">{{ article.author }}</span>
                <span class="date">{{ article.publicationDate }}</span>
                <span class="relevance" v-if="article.relevanceScore > 0">
                  相关度: {{ Math.round(article.relevanceScore * 100) }}%
                </span>
              </div>
              <!-- 标签显示 -->
              <div class="article-tags" v-if="article.tags && article.tags.length > 0">
                <el-tag size="small" v-for="(tag, tagIndex) in article.tags.split(', ').filter(tag => tag.trim())" :key="`${article.id}-tag-${tagIndex}`" class="tag-item">{{ tag }}</el-tag>
              </div>
              <p class="article-summary">{{ article.contentSummary }}</p>
              <div class="article-actions">
                <el-button type="text" @click.stop="navigateToDetail(article.id)" icon="el-icon-view">
                  阅读全文
                </el-button>
                <el-button type="text" @click.stop="handleFavorite(article.id)" :icon="article.isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'">
                  {{ article.isFavorited ? '已收藏' : '收藏' }}
                </el-button>
                <el-button type="text" @click.stop="handleShare(article.id)" icon="el-icon-share">
                  分享
                </el-button>
              </div>
            </div>
            </div>
          </el-card>
          
          <!-- 骨架屏 -->
          <div v-else class="skeleton-grid">
            <el-card class="article-card skeleton-card" v-for="i in 6" :key="i">
              <div class="skeleton-image">
                <el-skeleton :rows="1" animated style="height: 180px;" />
              </div>
              <div class="skeleton-content">
                <el-skeleton :rows="1" animated style="width: 80px;" />
                <el-skeleton :rows="2" animated />
                <el-skeleton :rows="1" animated style="width: 200px;" />
                <el-skeleton :rows="2" animated />
                <el-skeleton :rows="1" animated style="width: 150px;" />
              </div>
            </el-card>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { getArticles, searchArticles, toggleFavorite, isFavorited } from '@/api/article'

export default {
  name: 'ArticleList',
  data() {
    return {
      articles: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      selectedCategory: '',
      searchKeyword: '',
      isSearching: false,
      loading: false,
      sortBy: 'latest'
    }
  },
  computed: {
    featuredArticle() {
      // 返回第一篇文章作为特色文章
      return this.articles.length > 0 ? this.articles[0] : null
    },
    displayArticles() {
      // 显示除了特色文章之外的文章
      return this.articles.length > 0 ? this.articles.slice(1) : []
    }
  },
  mounted() {
    this.isSearching = false
    this.loadArticles()
    
    // 监听新文章通知
    window.addEventListener('newArticles', this.handleNewArticles)
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('newArticles', this.handleNewArticles)
  },
  methods: {
    loadArticles() {
      this.loading = true
      const params = {
        category: this.selectedCategory || undefined,
        page: this.currentPage,
        size: this.pageSize,
        sortBy: this.sortBy
      }
      
      getArticles(params).then(response => {
        // 处理后端返回的响应
        if (response && response.records) {
          // 响应是PageResult对象（被响应拦截器提取过）
          this.articles = response.records
          this.total = response.total || response.records.length
        } else if (Array.isArray(response)) {
          // 响应是直接的数组（兼容旧版本）
          this.articles = response
          this.total = response.length
        } else if (response && response.data && response.data.records) {
          // 响应是包含data字段的Result对象
          this.articles = response.data.records
          this.total = response.data.total || response.data.records.length
        } else {
          this.articles = []
          this.total = 0
        }
        
        // 检查每篇文章的收藏状态
        this.checkFavorites()
      }).catch(error => {
        console.error('Failed to load articles:', error)
        this.$message.error('加载文章失败，请稍后重试')
      }).finally(() => {
        this.loading = false
      })
    },
    checkFavorites() {
      // 检查每篇文章的收藏状态
      this.articles.forEach(article => {
        isFavorited(article.id).then(response => {
          article.isFavorited = response
        }).catch(() => {
          article.isFavorited = false
        })
      })
    },
    handleSearch() {
      if (this.searchKeyword) {
        this.loading = true
        this.isSearching = true
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          sortBy: this.sortBy
        }
        searchArticles(this.searchKeyword, params).then(response => {
          if (response && response.records) {
            // 响应是PageResult对象（被响应拦截器提取过）
            this.articles = response.records
            this.total = response.total
          } else if (Array.isArray(response)) {
            // 响应是直接的数组（兼容旧版本）
            this.articles = response
            this.total = response.length
          } else if (response && response.data && response.data.records) {
            // 响应是包含data字段的Result对象
            this.articles = response.data.records
            this.total = response.data.total
          } else {
            this.articles = []
            this.total = 0
          }
          
          // 检查每篇文章的收藏状态
          this.checkFavorites()
        }).catch(error => {
          console.error('Failed to search articles:', error)
          this.$message.error('搜索失败，请稍后重试')
        }).finally(() => {
          this.loading = false
        })
      } else {
        this.isSearching = false
        this.loadArticles()
      }
    },
    handleCategoryChange() {
      this.currentPage = 1
      this.isSearching = false
      this.loadArticles()
    },
    selectCategory(category) {
      this.selectedCategory = category
      this.currentPage = 1
      this.isSearching = false
      this.loadArticles()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadArticles()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadArticles()
    },
    navigateToDetail(id) {
      // 避免重复导航到当前页面
      const currentId = this.$route.params.id
      if (id !== currentId) {
        // 使用replace而不是push，避免重复添加历史记录
        this.$router.replace(`/article/detail/${id}`)
      }
    },
    handleNewArticles(event) {
      const articleCount = event.detail
      this.$message({
        message: `有 ${articleCount} 篇新的创业资讯`,
        type: 'success',
        duration: 3000
      })
      // 刷新文章列表
      this.loadArticles()
    },
    handleFavorite(id) {
      // 检查用户是否已登录
      if (!this.$store.getters['user/isLoggedIn']) {
        this.$message({
          message: '请先登录后再收藏',
          type: 'warning',
          duration: 2000
        })
        this.$router.push('/login')
        return
      }
      
      toggleFavorite(id).then(response => {
        // 更新文章的收藏状态
        const article = this.articles.find(a => a.id === id)
        if (article) {
          article.isFavorited = response
        }
        this.$message({
          message: response ? '收藏成功' : '取消收藏成功',
          type: 'success',
          duration: 2000
        })
      }).catch(error => {
        console.error('Failed to toggle favorite:', error)
        this.$message.error('操作失败，请稍后重试')
      })
    },
    handleShare(id) {
      // 实现分享功能
      if (navigator.share) {
        navigator.share({
          title: '创业资讯',
          text: '查看这篇创业资讯',
          url: window.location.origin + '/article/detail/' + id
        }).catch(error => {
          console.error('Error sharing:', error)
        })
      } else {
        // 复制链接到剪贴板
        const url = window.location.origin + '/article/detail/' + id
        navigator.clipboard.writeText(url).then(() => {
          this.$message({
            message: '链接已复制到剪贴板',
            type: 'success',
            duration: 2000
          })
        }).catch(() => {
          this.$message.error('复制链接失败，请手动复制')
        })
      }
    },
    getArticleImage(category, id) {
      // 使用 C:\Users\Administrator\Desktop\lunwen\images 目录下的图片
      // 根据文章ID生成图片索引，确保不同文章显示不同图片
      const imageIndex = (id % 3) + 1 // 1, 2, 3
      return `http://localhost:8081/images/${imageIndex}.png`
    },
    // 预加载文章图片
    preloadImages() {
      const imageUrls = [
        'http://localhost:8081/images/1.png',
        'http://localhost:8081/images/2.png',
        'http://localhost:8081/images/3.png'
      ]
      imageUrls.forEach(url => {
        const img = new Image()
        img.src = url
      })
    }
  },
  created() {
    // 页面创建时预加载图片
    this.preloadImages()
  }
}
</script>

<style scoped>
.article-list {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 容器样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 搜索栏样式 */
.search-section {
  margin: 30px 0;
  display: flex;
  justify-content: center;
}

.search-input {
  width: 600px;
  max-width: 100%;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-input .el-input__inner {
  height: 48px;
  font-size: 16px;
  border-radius: 25px 0 0 25px;
  border-right: none;
}

.search-input .el-button {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 0 25px 25px 0;
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  border: none;
  color: white;
  padding: 0 30px;
  transition: all 0.3s ease;
}

.search-input .el-button:hover {
  background: linear-gradient(135deg, #40a9ff 0%, #69c0ff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.search-input .el-input__prefix {
  color: #999;
  font-size: 18px;
}

/* 特色文章样式 */
.featured-article {
  margin: 30px 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.featured-image {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.featured-article:hover .featured-image img {
  transform: scale(1.05);
}

.featured-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.4), transparent);
  color: white;
  padding: 40px;
  z-index: 1;
}

.featured-category {
  background-color: #1890ff;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
  margin-bottom: 15px;
}

.featured-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 15px 0;
  line-height: 1.3;
}

.featured-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 15px;
}

.featured-summary {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
  max-width: 80%;
  opacity: 0.9;
}

.featured-button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  position: relative;
  display: inline-block;
}

.featured-button:hover {
  background-color: #40a9ff;
}

/* 分类标签样式 */
.category-section {
  margin: 30px 0;
}

.category-header {
  margin-bottom: 15px;
}

.category-tabs {
  background-color: white;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 最新新闻样式 */
.latest-news {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
}

.section-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #e0e0e0;
  margin-left: 20px;
}

.sort-select {
  width: 150px;
}

/* 文章网格样式 */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

/* 文章卡片样式 */
.article-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background-color: white;
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

/* 文章图片样式 */
.article-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #f0f0f0;
  position: relative;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.05);
}

.article-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #ff4d4f;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  z-index: 1;
}

/* 文章内容样式 */
.article-content {
  padding: 20px;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 10px 0 15px 0;
  line-height: 1.4;
}

.category-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
}

.article-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.relevance {
  color: #52c41a;
  font-weight: 500;
}

.article-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 文章操作按钮 */
.article-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}

.article-actions .el-button {
  font-size: 12px;
  padding: 0;
  min-width: 60px;
}

/* 标签样式 */
.article-tags {
  margin: 10px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #f5f5f5;
  color: #666;
}

/* 作者样式 */
.author {
  color: #666;
  font-weight: 500;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  margin-bottom: 60px;
}

/* 骨架屏样式 */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.skeleton-card {
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  background-color: white;
}

.skeleton-image {
  width: 100%;
  height: 200px;
  margin-bottom: 15px;
}

.skeleton-content {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-input {
    width: 100%;
  }
  
  .featured-image {
    height: 300px;
  }
  
  .featured-overlay {
    padding: 20px;
  }
  
  .featured-title {
    font-size: 24px;
  }
  
  .featured-summary {
    max-width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .section-title::after {
    display: none;
  }
  
  .article-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .skeleton-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .article-actions {
    justify-content: space-between;
  }
}
</style>
