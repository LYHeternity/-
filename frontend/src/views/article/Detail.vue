<template>
  <div class="article-detail">
    <div class="container">
      <el-button @click="goBack" type="text" class="back-button">
        <i class="el-icon-arrow-left"></i> 返回列表
      </el-button>
      
      <div v-if="article" class="article-content">
        <div class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="source">{{ article.source }}</span>
            <span class="author" v-if="article.author && article.author !== '未知作者'">{{ article.author }}</span>
            <span class="date">{{ article.publicationDate }}</span>
            <span class="category">{{ article.category }}</span>
            <span class="read-time" v-if="readTime">{{ readTime }} 分钟阅读</span>
          </div>
          <!-- 标签显示 -->
          <div class="article-tags" v-if="article.tags && article.tags.length > 0">
            <el-tag size="small" v-for="tag in article.tags.split(', ')" :key="tag" class="tag-item">{{ tag }}</el-tag>
          </div>
        </div>
        
        <div class="article-image">
          <img :src="getArticleImage(article.category, article.id)" :alt="article.title">
        </div>
        
        <div class="article-body">
          <div v-if="article.content" class="content-wrapper" v-html="article.content"></div>
          <div v-else class="no-content">暂无内容</div>
        </div>
        
        <div class="article-actions">
          <el-button type="primary" @click="toggleFavorite" :icon="isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'">
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button type="default" @click="handleShare" icon="el-icon-share">
            分享
          </el-button>
        </div>
        
        <div class="article-footer">
          <div class="footer-left">
            <span class="relevance">相关度: {{ Math.round(article.relevanceScore * 100) }}%</span>
          </div>
        </div>
      </div>
      
      <!-- 相关推荐 -->
      <div v-if="relatedArticles && relatedArticles.length > 0" class="related-articles">
        <h3 class="section-title">相关推荐</h3>
        <div class="related-grid">
          <el-card
            v-for="(item, index) in relatedArticles"
            :key="item.id"
            class="related-card"
          >
            <div @click="navigateToDetail(item.id)" style="cursor: pointer;">
              <div class="related-image">
                <img :src="getArticleImage(item.category, item.id)" :alt="item.title">
              </div>
              <div class="related-content">
                <h4 class="related-title">{{ item.title }}</h4>
                <div class="related-meta">
                  <span class="related-source">{{ item.source }}</span>
                  <span class="related-date">{{ item.publicationDate }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <div v-else class="loading">
        <div class="article-content">
          <div class="article-header">
            <el-skeleton :rows="1" animated style="width: 80%; height: 32px; margin-bottom: 20px;" />
            <div class="article-meta">
              <el-skeleton :rows="1" animated style="width: 100px; display: inline-block; margin-right: 15px;" />
              <el-skeleton :rows="1" animated style="width: 120px; display: inline-block; margin-right: 15px;" />
              <el-skeleton :rows="1" animated style="width: 80px; display: inline-block; margin-right: 15px;" />
              <el-skeleton :rows="1" animated style="width: 100px; display: inline-block;" />
            </div>
          </div>
          
          <div class="article-image">
            <el-skeleton :rows="1" animated style="height: 300px;" />
          </div>
          
          <div style="margin-top: 30px;">
            <el-skeleton :rows="8" animated />
          </div>
          
          <div class="article-actions">
            <el-skeleton :rows="1" animated style="width: 100px; height: 36px; display: inline-block; margin-right: 10px;" />
            <el-skeleton :rows="1" animated style="width: 80px; height: 36px; display: inline-block;" />
          </div>
          
          <div class="article-footer">
            <div class="footer-left">
              <el-skeleton :rows="1" animated style="width: 100px;" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getArticleById, toggleFavorite, isFavorited, getHotArticles } from '@/api/article'
import { marked } from 'marked'

export default {
  name: 'ArticleDetail',
  data() {
    return {
      article: null,
      isFavorited: false,
      loading: true,
      relatedArticles: [],
      readTime: 0
    }
  },
  mounted() {
    this.loadArticle()
  },
  watch: {
    '$route.params.id': function(newId, oldId) {
      if (newId !== oldId) {
        // 滚动页面到顶部
        window.scrollTo(0, 0)
        this.loadArticle()
      }
    }
  },
  methods: {
    loadArticle() {
      this.loading = true
      const id = this.$route.params.id
      getArticleById(id).then(response => {
        // 转换Markdown内容为HTML，配置marked库以避免错误识别标题
        if (response.content) {
          // 确保内容不是以#开头，避免被识别为一级标题
          if (response.content.startsWith('# ')) {
            response.content = response.content.substring(2)
          }
          // 配置marked库，确保生成的HTML能够正确应用首行缩进
          marked.setOptions({
            breaks: true,
            gfm: true,
            headerIds: false,
            mangle: false
          })
          response.content = marked(response.content)
        }
        this.article = response
        this.checkFavoriteStatus()
        this.calculateReadTime()
        this.loadRelatedArticles()
      }).catch(error => {
        console.error('Failed to load article:', error)
        this.$message.error('加载文章失败，请稍后重试')
      }).finally(() => {
        this.loading = false
      })
    },
    checkFavoriteStatus() {
      const id = this.$route.params.id
      isFavorited(id).then(response => {
        this.isFavorited = response
      }).catch(error => {
        console.error('Failed to check favorite status:', error)
      })
    },
    loadRelatedArticles() {
      // 获取热门文章作为相关推荐
      getHotArticles(6).then(response => {
        // 过滤掉当前文章
        this.relatedArticles = response.filter(article => article.id !== this.article.id)
      }).catch(error => {
        console.error('Failed to load related articles:', error)
      })
    },
    calculateReadTime() {
      // 计算阅读时间，假设平均阅读速度为每分钟300字
      if (this.article && this.article.content) {
        const text = this.article.content.replace(/<[^>]+>/g, '') // 移除HTML标签
        const wordCount = text.length
        this.readTime = Math.ceil(wordCount / 300)
      }
    },
    goBack() {
      this.$router.push('/articles')
    },
    navigateToDetail(id) {
      // 避免重复导航到当前页面
      const currentId = this.$route.params.id
      if (id !== currentId) {
        this.$router.push(`/article/detail/${id}`)
      }
    },
    toggleFavorite() {
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
      
      const id = this.$route.params.id
      toggleFavorite(id).then(response => {
        this.isFavorited = response
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
    handleShare() {
      // 实现分享功能
      if (navigator.share) {
        navigator.share({
          title: this.article.title,
          text: this.article.contentSummary,
          url: window.location.origin + '/article/detail/' + this.article.id
        }).catch(error => {
          console.error('Error sharing:', error)
        })
      } else {
        // 复制链接到剪贴板
        const url = window.location.origin + '/article/detail/' + this.article.id
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
    }
  }
}
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 40px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-button {
  margin-bottom: 30px;
  font-size: 16px;
  color: #666;
  transition: color 0.3s ease;
}

.back-button:hover {
  color: #1890ff;
}

.article-content {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

.article-header {
  margin-bottom: 30px;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.3;
  font-family: '方正小标宋简体', 'SimSun', '宋体', 'Noto Sans SC', sans-serif;
  letter-spacing: 1px;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #999;
}

.article-meta .category {
  background-color: #e6f7ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  align-self: center;
}

.article-meta .read-time {
  color: #666;
  font-size: 14px;
}

.article-image {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 30px;
  background-color: #f0f0f0;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-image:hover img {
  transform: scale(1.02);
}

.article-body {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}

.content-wrapper {
  /* 文章内容排版优化 */
  line-height: 1.8;
  color: #333;
  font-family: '方正小标宋简体', 'SimSun', '宋体', serif;
  font-size: 18px;
  letter-spacing: 0.8px;
  padding: 20px 0;
  background-color: #f9f9f9;
  padding: 30px;
  border-radius: 8px;
}

.content-wrapper p {
  margin-bottom: 1.8em;
  text-align: justify;
  line-height: 2.0;
  text-indent: 2em; /* 首行缩进2个字符 */
  letter-spacing: 0.8px;
  font-size: 18px;
  color: #333;
  font-family: '方正小标宋简体', 'SimSun', '宋体', serif;
}

/* 确保所有段落都有首行缩进，包括marked库生成的段落 */
.content-wrapper * {
  text-indent: 2em;
}

/* 标题不需要首行缩进 */
.content-wrapper h1,
.content-wrapper h2,
.content-wrapper h3,
.content-wrapper h4,
.content-wrapper h5,
.content-wrapper h6 {
  text-indent: 0;
}

/* 列表不需要首行缩进 */
.content-wrapper ul,
.content-wrapper ol {
  text-indent: 0;
}

.content-wrapper li {
  text-indent: 0;
}

/* 引用不需要首行缩进 */
.content-wrapper blockquote {
  text-indent: 0;
}

/* 代码块不需要首行缩进 */
.content-wrapper pre,
.content-wrapper code {
  text-indent: 0;
}

.content-wrapper p:first-child {
  margin-top: 0;
}

.content-wrapper p:last-child {
  margin-bottom: 0;
}

.content-wrapper h1,
.content-wrapper h2,
.content-wrapper h3,
.content-wrapper h4,
.content-wrapper h5,
.content-wrapper h6 {
  margin: 1.8em 0 1em;
  font-weight: 700;
  color: #222;
  line-height: 1.4;
}

.content-wrapper h1 {
  font-size: 2em;
  border-bottom: 2px solid #e6f7ff;
  padding-bottom: 0.5em;
  margin-top: 1em;
}

.content-wrapper h2 {
  font-size: 1.6em;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.3em;
}

.content-wrapper h3 {
  font-size: 1.3em;
}

.content-wrapper h4 {
  font-size: 1.1em;
}

.content-wrapper ul,
.content-wrapper ol {
  margin: 1.5em 0;
  padding-left: 2em;
  line-height: 1.8;
}

.content-wrapper li {
  margin-bottom: 0.8em;
}

.content-wrapper img {
  max-width: 100%;
  height: auto;
  margin: 2em 0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: block;
  margin-left: auto;
  margin-right: auto;
}

.content-wrapper a {
  color: #1890ff;
  text-decoration: none;
  transition: all 0.3s ease;
  border-bottom: 1px solid transparent;
}

.content-wrapper a:hover {
  color: #40a9ff;
  text-decoration: none;
  border-bottom: 1px solid #40a9ff;
}

.content-wrapper blockquote {
  border-left: 4px solid #1890ff;
  padding: 1.5em;
  background-color: #f6f8fa;
  margin: 2em 0;
  border-radius: 0 8px 8px 0;
  font-style: italic;
  color: #666;
  line-height: 1.7;
}

.content-wrapper pre {
  background-color: #f5f5f5;
  padding: 1.5em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 2em 0;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
}

.content-wrapper code {
  background-color: #f0f0f0;
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9em;
  color: #d9534f;
}

.content-wrapper table {
  width: 100%;
  border-collapse: collapse;
  margin: 2em 0;
  font-size: 14px;
}

.content-wrapper th,
.content-wrapper td {
  padding: 0.8em 1em;
  border: 1px solid #e0e0e0;
  text-align: left;
}

.content-wrapper th {
  background-color: #f9f9f9;
  font-weight: 600;
  color: #333;
}

.content-wrapper tr:nth-child(even) {
  background-color: #f9f9f9;
}

.no-content {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-style: italic;
}

.article-actions {
  display: flex;
  gap: 15px;
  margin: 30px 0;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #999;
}

.footer-left {
  flex: 1;
}

.relevance {
  color: #52c41a;
  font-weight: 500;
}

/* 标签样式 */
.article-tags {
  margin: 20px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 16px;
  background-color: #f0f2f5;
  color: #666;
  transition: all 0.3s ease;
}

.tag-item:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

/* 作者样式 */
.author {
  color: #666;
  font-weight: 500;
}

/* 相关推荐 */
.related-articles {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
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

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.related-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.related-image {
  width: 100%;
  height: 120px;
  overflow: hidden;
  background-color: #f0f0f0;
}

.related-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.related-card:hover .related-image img {
  transform: scale(1.05);
}

.related-content {
  padding: 15px;
}

.related-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.related-meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #999;
}

.loading {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-detail {
    padding: 20px 0;
  }
  
  .article-content {
    padding: 20px;
  }
  
  .article-title {
    font-size: 24px;
  }
  
  .article-image {
    height: 200px;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .article-actions {
    flex-direction: column;
  }
  
  .article-actions .el-button {
    width: 100%;
  }
  
  .article-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .related-articles {
    padding: 20px;
  }
  
  .related-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}
</style>
