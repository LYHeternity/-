<template>
  <div class="community-home">
    <!-- 顶部导航 -->
    <div class="community-header">
      <div class="container">
        <div class="header-content">
          <div class="logo-section">
            <h1 class="community-logo">社区交流</h1>
          </div>
          <div class="search-section">
            <el-input
              v-model="filters.keyword"
              placeholder="搜索帖子标题或内容"
              class="search-input"
              @keyup.enter="loadPosts"
              clearable
            >
              <el-button slot="append" icon="el-icon-search" @click="loadPosts"></el-button>
            </el-input>
            <!-- 搜索建议 -->
            <div class="search-suggestions" v-if="showSearchSuggestions && (searchHistory.length > 0 || hotSearchKeywords.length > 0)">
              <div v-if="searchHistory.length > 0" class="suggestion-section">
                <div class="suggestion-title">
                  <span>搜索历史</span>
                  <el-button size="mini" type="text" @click="clearSearchHistory">清空</el-button>
                </div>
                <ul class="suggestion-list">
                  <li v-for="(item, index) in searchHistory" :key="index" @click="selectSearchHistory(item)">
                    <i class="el-icon-time"></i>
                    <span>{{ item }}</span>
                  </li>
                </ul>
              </div>
              <div v-if="hotSearchKeywords.length > 0" class="suggestion-section">
                <div class="suggestion-title">热门搜索</div>
                <ul class="suggestion-list">
                  <li v-for="(item, index) in hotSearchKeywords" :key="index" @click="selectHotSearch(item)">
                    <i class="el-icon-star-on"></i>
                    <span>{{ item }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <div class="action-section">
            <el-button type="primary" size="large" @click="navigateToCreate" class="create-btn">
              <i class="el-icon-edit"></i> 发布帖子
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主体内容区 -->
    <div class="community-body">
      <div class="container">
        <div class="body-content">
          <!-- 左侧分类导航 -->
          <div class="left-sidebar">
            <div class="sidebar-section">
              <h3 class="sidebar-title">分类导航</h3>
              <ul class="category-list">
                <li 
                  v-for="category in categories" 
                  :key="category.value"
                  :class="{ active: filters.category === category.value }"
                  @click="selectCategory(category.value)"
                >
                  <i :class="category.icon"></i>
                  <span>{{ category.label }}</span>
                </li>
              </ul>
            </div>
            
            <div class="sidebar-section">
              <h3 class="sidebar-title">热门话题</h3>
              <ul class="hot-topics">
                <li v-for="(topic, index) in hotTopics" :key="index" @click="searchTopic(topic)">
                  <span class="topic-rank">{{ index + 1 }}</span>
                  <span class="topic-name">{{ topic }}</span>
                </li>
              </ul>
            </div>
            
            <div class="sidebar-section">
              <h3 class="sidebar-title">我的关注</h3>
              <ul class="followed-topics">
                <li v-for="(topic, index) in followedTopics" :key="index">
                  <i class="el-icon-star-on"></i>
                  <span>{{ topic }}</span>
                </li>
              </ul>
            </div>
          </div>
          
          <!-- 中间帖子列表 -->
          <div class="main-content">
            <!-- 排序和筛选 -->
            <div class="filter-bar">
              <div class="sort-options">
                <span class="sort-label">排序：</span>
                <el-radio-group v-model="sortBy" @change="loadPosts" class="sort-group">
                  <el-radio-button label="hot">热度</el-radio-button>
                  <el-radio-button label="time">时间</el-radio-button>
                </el-radio-group>
              </div>
              
              <div class="type-filter">
                <el-select v-model="filters.type" placeholder="选择类型" @change="loadPosts" class="type-select">
                  <el-option label="全部" value=""></el-option>
                  <el-option label="文章" value="article"></el-option>
                  <el-option label="问题" value="question"></el-option>
                </el-select>
              </div>
            </div>
            
            <!-- 帖子列表 -->
            <div class="post-list" v-loading="loading">
              <!-- 帖子骨架屏 -->
              <div v-if="loading" class="post-skeleton" v-for="i in 5" :key="'skeleton-' + i">
                <div class="skeleton-header">
                  <div class="skeleton-avatar"></div>
                  <div class="skeleton-author-info">
                    <div class="skeleton-author-name"></div>
                    <div class="skeleton-post-time"></div>
                  </div>
                  <div class="skeleton-type"></div>
                </div>
                <div class="skeleton-title"></div>
                <div class="skeleton-content"></div>
                <div class="skeleton-tags" v-if="Math.random() > 0.5">
                  <div class="skeleton-tag" v-for="j in 3" :key="'tag-' + j"></div>
                </div>
                <div class="skeleton-footer">
                  <div class="skeleton-stats">
                    <div class="skeleton-stat" v-for="k in 3" :key="'stat-' + k"></div>
                  </div>
                  <div class="skeleton-actions">
                    <div class="skeleton-action" v-for="l in 3" :key="'action-' + l"></div>
                  </div>
                </div>
              </div>
              
              <!-- 帖子列表 -->
              <el-card v-for="(post, index) in posts" :key="post.id" :class="['post-card', index < 3 ? 'top-post' : '']" :data-rank="index < 3 ? index + 1 : ''" v-show="!loading">
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
                      <el-button size="mini" :type="likedPosts.has(post.id) ? 'primary' : 'default'" @click.stop="toggleLike(post.id, $event)" class="action-btn">
                        <i class="el-icon-thumb"></i>
                      </el-button>
                      <el-button size="mini" :type="favoritedPosts.has(post.id) ? 'warning' : 'default'" @click.stop="toggleFavorite(post.id, $event)" class="action-btn">
                        <i class="el-icon-star-on"></i>
                      </el-button>
                      <el-button size="mini" @click.stop="markNotInterested(post.id, $event)" class="action-btn">
                        <i class="el-icon-close"></i>
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-card>
              
              <!-- 空状态 -->
              <div v-if="!loading && posts.length === 0" class="empty-state">
                <el-empty description="暂无帖子" />
              </div>
            </div>
          </div>
          
          <!-- 右侧侧边栏 -->
          <div class="right-sidebar">
            <div class="sidebar-section">
              <h3 class="sidebar-title">热门话题</h3>
              <ul class="hot-topics-list">
                <li v-for="(topic, index) in hotTopics" :key="index" class="hot-topic-item" @click="searchTopic(topic)">
                  <span class="topic-rank">{{ index + 1 }}</span>
                  <span class="topic-name">{{ topic }}</span>
                  <span class="topic-count">{{ Math.floor(Math.random() * 1000) + 100 }} 讨论</span>
                </li>
              </ul>
            </div>
            
            <div class="sidebar-section">
              <h3 class="sidebar-title">推荐用户</h3>
              <ul class="recommended-users">
                <li v-for="(user, index) in recommendedUsers" :key="index" class="user-item">
                  <el-avatar :size="36" :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="user-avatar"></el-avatar>
                  <div class="user-info">
                    <span class="user-name">{{ user.name }}</span>
                    <span class="user-desc">{{ user.desc }}</span>
                  </div>
                  <el-button size="small" :type="user.followed ? 'default' : 'primary'" :plain="!user.followed" class="follow-btn" @click="toggleFollow(user)">
                    {{ user.followed ? '已关注' : '关注' }}
                  </el-button>
                </li>
              </ul>
            </div>
            
            <div class="sidebar-section">
              <h3 class="sidebar-title">社区公告</h3>
              <div class="announcement">
                <p class="announcement-content">欢迎来到创业社区，这里是创业者交流的平台，分享经验，互助成长！</p>
                <a href="#" class="announcement-link">查看更多</a>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页 - 放在容器正下方 -->
        <div v-if="!loading && posts.length > 0" class="pagination-container">
          <div class="pagination">
            <div class="pagination-top">
              <span class="total-posts">共 {{ posts.length }} 条帖子</span>
              <div class="page-size-selector">
                <span>每页显示：</span>
                <el-select v-model="pagination.pageSize" @change="loadPosts" size="small" class="page-size-select">
                  <el-option label="10条" value="10"></el-option>
                  <el-option label="20条" value="20"></el-option>
                  <el-option label="30条" value="30"></el-option>
                  <el-option label="50条" value="50"></el-option>
                </el-select>
              </div>
            </div>
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="posts.length"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="post-pagination"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { postAPI, likeAPI, favoriteAPI } from '@/api/community'

export default {
  name: 'CommunityHome',
  data() {
    return {
      posts: [],
      filters: {
        category: '',
        type: '',
        keyword: ''
      },
      sortBy: 'hot', // hot 或 time
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      // 搜索相关
      showSearchSuggestions: false,
      searchHistory: [],
      hotSearchKeywords: [
        '融资', '创业', '技术', '招聘', '市场营销', '产品运营'
      ],
      // 分类导航数据
      categories: [
        { label: '全部', value: '', icon: 'el-icon-s-home' },
        { label: '创业经验', value: '创业经验', icon: 'el-icon-s-flag' },
        { label: '融资技巧', value: '融资技巧', icon: 'el-icon-s-finance' },
        { label: '技术分享', value: '技术分享', icon: 'el-icon-s-marketing' },
        { label: '行业动态', value: '行业动态', icon: 'el-icon-s-promotion' },
        { label: '求职招聘', value: '求职招聘', icon: 'el-icon-s-grid' },
        { label: '活动公告', value: '活动公告', icon: 'el-icon-s-ticket' },
        { label: '产品运营', value: '产品运营', icon: 'el-icon-s-operation' },
        { label: '市场营销', value: '市场营销', icon: 'el-icon-s-marketing' },
        { label: '团队管理', value: '团队管理', icon: 'el-icon-s-management' }
      ],
      // 热门话题数据
      hotTopics: [
        '如何获得第一轮融资',
        '初创公司如何吸引人才',
        '2026年创业趋势',
        '技术创业的注意事项',
        '如何撰写商业计划书',
        '创业团队管理经验',
        '市场推广策略分享',
        '产品迭代经验谈'
      ],
      // 关注的话题
      followedTopics: [
        '创业经验',
        '融资技巧',
        '技术分享'
      ],
      // 推荐用户数据
      recommendedUsers: [
        { name: '张创业', desc: '连续创业者，3次成功退出', avatar: '', followed: false },
        { name: '李投资', desc: '天使投资人，专注早期项目', avatar: '', followed: false },
        { name: '王技术', desc: '技术专家，多次技术创业', avatar: '', followed: false },
        { name: '赵市场', desc: '营销专家，品牌策划师', avatar: '', followed: false }
      ],
      // 点赞状态
      likedPosts: new Set(),
      // 收藏状态
      favoritedPosts: new Set()
    }
  },
  mounted() {
    this.loadSearchHistory()
    this.loadPosts()
  },
  methods: {
    loadPosts() {
      this.loading = true
      this.pagination.currentPage = 1
      
      // 保存搜索历史
      if (this.filters.keyword.trim()) {
        this.saveSearchHistory(this.filters.keyword.trim())
      }
      
      postAPI.getPostList({
        page: this.pagination.currentPage,
        size: this.pagination.pageSize,
        category: this.filters.category,
        type: this.filters.type,
        keyword: this.filters.keyword,
        sort: this.sortBy
      }).then(res => {
        console.log('社区API返回数据:', res)
        if (res && res.records) {
          // 计算热度并排序
          this.posts = (res.records || []).sort((a, b) => {
            // 计算热度分数：浏览量*0.1 + 评论数*2 + 点赞数*3
            const scoreA = (a.viewCount || 0) * 0.1 + (a.commentCount || 0) * 2 + (a.likeCount || 0) * 3
            const scoreB = (b.viewCount || 0) * 0.1 + (b.commentCount || 0) * 2 + (b.likeCount || 0) * 3
            return scoreB - scoreA // 降序排列
          })
          this.pagination.total = res.total || 0
          console.log('设置分页总数:', this.pagination.total)
        } else {
          this.posts = []
          this.pagination.total = 0
        }
      }).catch(err => {
        console.error('加载帖子失败:', err)
        this.posts = []
        this.pagination.total = 0
      }).finally(() => {
        this.loading = false
        this.showSearchSuggestions = false
      })
    },
    // 搜索相关方法
    handleSearchFocus() {
      this.showSearchSuggestions = true
    },
    handleSearchBlur() {
      // 延迟隐藏，以便点击建议项
      setTimeout(() => {
        this.showSearchSuggestions = false
      }, 200)
    },
    saveSearchHistory(keyword) {
      // 从本地存储获取历史记录
      let history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
      // 移除重复项
      history = history.filter(item => item !== keyword)
      // 添加到开头
      history.unshift(keyword)
      // 限制历史记录数量
      history = history.slice(0, 10)
      // 保存到本地存储
      localStorage.setItem('searchHistory', JSON.stringify(history))
      this.searchHistory = history
    },
    loadSearchHistory() {
      this.searchHistory = JSON.parse(localStorage.getItem('searchHistory') || '[]')
    },
    clearSearchHistory() {
      localStorage.removeItem('searchHistory')
      this.searchHistory = []
    },
    selectSearchHistory(keyword) {
      this.filters.keyword = keyword
      this.loadPosts()
    },
    selectHotSearch(keyword) {
      this.filters.keyword = keyword
      this.loadPosts()
    },
    // 分页相关方法
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.currentPage = 1
      this.loadPosts()
    },
    handleCurrentChange(current) {
      this.pagination.currentPage = current
      this.loadPosts()
    },
    selectCategory(category) {
      this.filters.category = category
      this.filters.keyword = '' // 清除关键词，确保显示所有帖子
      this.loadPosts()
    },
    searchTopic(topic) {
      this.filters.keyword = topic
      this.loadPosts()
    },
    navigateToCreate() {
      // 检查是否登录
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/community/create')
    },
    navigateToDetail(id) {
      this.$router.push(`/community/post/${id}`)
    },
    toggleLike(postId, event) {
      event.stopPropagation()
      
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      likeAPI.toggleLike(postId, 'post').then(res => {
        if (res.code === 200) {
          const post = this.posts.find(p => p.id === postId)
          if (post) {
            if (this.likedPosts.has(postId)) {
              this.likedPosts.delete(postId)
              post.likeCount--
            } else {
              this.likedPosts.add(postId)
              post.likeCount++
            }
            this.$message.success(this.likedPosts.has(postId) ? '点赞成功' : '取消点赞')
          }
        }
      }).catch(err => {
        console.error('点赞失败:', err)
        this.$message.error('操作失败，请稍后重试')
      })
    },
    toggleFavorite(postId, event) {
      event.stopPropagation()
      
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      favoriteAPI.toggleFavorite(postId).then(res => {
        const post = this.posts.find(p => p.id === postId)
        if (post) {
          if (res) {
            this.favoritedPosts.add(postId)
            this.$message.success('收藏成功')
          } else {
            this.favoritedPosts.delete(postId)
            this.$message.success('取消收藏')
          }
        }
      }).catch(err => {
        console.error('收藏失败:', err)
        this.$message.error('操作失败，请稍后重试')
      })
    },
    markNotInterested(postId, event) {
      event.stopPropagation()
      
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      // 模拟标记不感兴趣
      this.posts = this.posts.filter(post => post.id !== postId)
      this.$message.success('已标记为不感兴趣')
    },
    toggleFollow(user) {
      if (!this.$store.state.user.token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      // 模拟关注/取消关注操作
      user.followed = !user.followed
      
      // 发送消息通知给被关注的用户
      if (user.followed) {
        this.$message.success(`已关注 ${user.name}`)
        // 这里可以调用API发送消息通知
        // messageAPI.sendFollowNotification(user.id, this.$store.state.user.userId)
        console.log(`发送关注通知给 ${user.name}`)
      } else {
        this.$message.success(`已取消关注 ${user.name}`)
      }
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
      // 移除HTML标签，获取纯文本
      const plainText = content.replace(/<[^>]+>/g, '')
      // 截断文本并添加省略号
      return plainText.length > 150 ? plainText.substring(0, 150) + '...' : plainText
    }
  }
}
</script>

<style scoped>
.community-home {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 顶部导航 */
.community-header {
  background-color: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  padding: 8px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.community-header:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.logo-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.community-logo {
  font-size: 24px;
  font-weight: 700;
  color: #1890ff;
  margin: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.community-logo:hover {
  color: #40a9ff;
}

.search-section {
  flex: 1;
  max-width: 600px;
  position: relative;
}

.search-input {
  width: 100%;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.search-input:focus {
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 搜索建议 */
.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  margin-top: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.suggestion-section {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-section:last-child {
  border-bottom: none;
}

.suggestion-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 4px;
}

.suggestion-list li:hover {
  background-color: #f0f9ff;
  color: #1890ff;
}

.suggestion-list li i {
  font-size: 14px;
  color: #999;
}

.action-section {
  flex-shrink: 0;
}

.create-btn {
  border-radius: 20px;
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.create-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}



/* 主体内容区 */
.community-body {
  padding: 32px 0 40px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 200px);
}

.body-content {
  display: grid;
  grid-template-columns: 200px minmax(0, 1fr) 280px;
  gap: 24px;
  align-items: start;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 左侧分类导航 */
.left-sidebar {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 4px;
  width: 100%;
}

.category-list li:hover {
  background-color: #f0f9ff;
  color: #1890ff;
}

.category-list li.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 600;
}

.category-list li i {
  font-size: 16px;
}

.hot-topics {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hot-topics li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  width: 100%;
  pointer-events: auto;
  user-select: none;
}

.hot-topics li:last-child {
  border-bottom: none;
}

.hot-topics li:hover {
  color: #1890ff;
  background-color: #f0f9ff;
  padding-left: 8px;
  padding-right: 8px;
}

.topic-rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #666;
}

.hot-topics li:nth-child(1) .topic-rank,
.hot-topics li:nth-child(2) .topic-rank,
.hot-topics li:nth-child(3) .topic-rank {
  background-color: #ff4d4f;
  color: #ffffff;
}

.followed-topics {
  list-style: none;
  padding: 0;
  margin: 0;
}

.followed-topics li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  width: 100%;
}

.followed-topics li:last-child {
  border-bottom: none;
}

.followed-topics li:hover {
  color: #1890ff;
}

.followed-topics li i {
  color: #faad14;
}

/* 中间帖子列表 */
.main-content {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  min-height: 600px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 帖子列表 - 三列网格布局 */
.post-list {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 28px;
  padding: 24px;
  align-items: start;
  width: 100%;
  box-sizing: border-box;
}

/* 分页容器 - 放在主内容区域底部 */
.pagination-container {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  width: 100%;
}

.pagination {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.pagination-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.post-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.sort-group {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
}

.type-filter {
  flex-shrink: 0;
}

.type-select {
  width: 120px;
  border-radius: 8px;
}

.post-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e8e8e8;
  height: auto;
  min-height: 320px;
  display: flex;
  flex-direction: column;
  animation: fadeIn 0.6s ease-out;
  width: 100%;
  box-sizing: border-box;
  word-wrap: break-word;
  background-color: #fff;
  position: relative;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.post-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #e8e8e8;
}

/* 卡片顶部装饰条 */
.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: #1890ff;
  border-radius: 12px 12px 0 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.post-card:hover::before {
  opacity: 1;
}

/* 前三名帖子卡片的特殊样式 */
.top-post {
  border: 1px solid #1890ff;
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.15);
  position: relative;
  width: 100%;
  box-sizing: border-box;
  word-wrap: break-word;
  overflow: hidden;
}

.top-post::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: #1890ff;
  border-radius: 12px 12px 0 0;
  opacity: 1;
}

.top-post .post-title {
  color: #1890ff;
  font-weight: 600;
}

.top-post .post-container {
  background-color: rgba(24, 144, 255, 0.02);
}

/* 排名标识 */
.top-post::after {
  content: attr(data-rank);
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  background-color: #1890ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

.top-post:nth-child(1)::after {
  background-color: #ff4d4f;
}

.top-post:nth-child(2)::after {
  background-color: #fa8c16;
}

.top-post:nth-child(3)::after {
  background-color: #faad14;
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

/* 响应式设计 */
@media (max-width: 1400px) {
  .body-content {
    grid-template-columns: 180px minmax(0, 1fr) 260px;
    gap: 20px;
    padding: 0 16px;
  }

  .post-list {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 1200px) {
  .body-content {
    grid-template-columns: 160px minmax(0, 1fr) 240px;
    gap: 16px;
    padding: 0 16px;
  }

  .post-list {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .left-sidebar,
  .right-sidebar .sidebar-section {
    padding: 16px;
  }

  .main-content {
    padding: 20px;
  }

  .post-card {
    min-height: 420px;
  }

  .post-container {
    padding: 20px;
  }

  .post-title {
    font-size: 18px;
  }

  .post-content {
    font-size: 14px;
  }
}

@media (max-width: 1024px) {
  .body-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 0 16px;
  }

  .left-sidebar,
  .right-sidebar {
    display: none;
  }

  .post-list {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }

  .community-body {
    padding: 24px 0;
  }

  .main-content {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .community-header {
    top: 0;
  }

  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-section {
    max-width: none;
  }

  .post-list {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .community-body {
    padding: 100px 0 24px;
  }

  .main-content {
    padding: 20px;
  }

  .community-header {
    padding: 6px 0;
  }

  .community-logo {
    font-size: 20px;
  }

  .create-btn {
    padding: 6px 16px;
    font-size: 12px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .sort-options {
    justify-content: space-between;
  }

  .type-select {
    width: 100%;
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

  .pagination-top {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .post-card {
    min-height: 300px;
  }

  .post-container {
    padding: 20px;
  }

  .post-title {
    font-size: 18px;
  }

  .post-content {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .community-header {
    padding: 12px 0;
  }
  
  .community-logo {
    font-size: 20px;
  }
  
  .create-btn {
    padding: 6px 16px;
    font-size: 12px;
  }
  
  .main-content {
    padding: 16px;
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
  
  .community-body {
    padding: 110px 0 20px;
  }
  
  .header-content {
    gap: 10px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .sort-options {
    width: 100%;
  }
  
  .type-filter {
    width: 100%;
  }
  
  .type-select {
    width: 100%;
  }

  .post-card {
    min-height: 280px;
  }

  .post-list {
    gap: 16px;
    padding: 16px;
  }

  .post-stats {
    gap: 12px;
  }

  .post-actions {
    gap: 8px;
  }

  .stat-item {
    padding: 2px 6px;
    font-size: 12px;
  }

  .post-actions .el-button {
    padding: 2px 8px;
    font-size: 12px;
  }
}

/* 骨架屏样式 */
.post-skeleton {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
}

.skeleton-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  animation: skeleton-loading 1.5s ease-in-out infinite;
}

.skeleton-author-info {
  flex: 1;
}

.skeleton-author-name {
  width: 120px;
  height: 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 8px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.2s;
}

.skeleton-post-time {
  width: 80px;
  height: 14px;
  background-color: #f0f0f0;
  border-radius: 4px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.3s;
}

.skeleton-type {
  width: 48px;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.4s;
}

.skeleton-title {
  width: 80%;
  height: 24px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.5s;
}

.skeleton-content {
  width: 100%;
  height: 80px;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  position: relative;
  overflow: hidden;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.6s;
}

.skeleton-content::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: skeleton-shimmer 1.5s infinite;
}

.skeleton-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.skeleton-tag {
  width: 60px;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.7s;
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skeleton-stats {
  display: flex;
  gap: 24px;
}

.skeleton-stat {
  width: 48px;
  height: 14px;
  background-color: #f0f0f0;
  border-radius: 4px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.8s;
}

.skeleton-actions {
  display: flex;
  gap: 12px;
}

.skeleton-action {
  width: 32px;
  height: 32px;
  background-color: #f0f0f0;
  border-radius: 4px;
  animation: skeleton-loading 1.5s ease-in-out infinite 0.9s;
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

@keyframes skeleton-shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.post-container {
  cursor: pointer;
  padding: 20px;
  transition: all 0.3s ease;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-container:hover {
  background-color: #f8f9fa;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  flex-shrink: 0;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-type {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.post-type.article {
  background-color: #f0f9ff;
  color: #1890ff;
  border: 1px solid #e6f7ff;
}

.post-type.question {
  background-color: #fff7e6;
  color: #fa8c16;
  border: 1px solid #fff1e6;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1.4;
  transition: all 0.3s ease;
  flex-shrink: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
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
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
  min-height: 88px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.tag-item {
  border-radius: 12px;
  font-size: 12px;
  padding: 3px 10px;
  background-color: #f8f9fa;
  color: #666;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.tag-item:hover {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #e6f7ff;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
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
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-item:hover {
  color: #1890ff;
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  pointer-events: auto;
}

.post-actions .el-button {
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  pointer-events: auto;
  user-select: none;
  padding: 2px 8px;
}

.post-actions .el-button:hover {
  color: #1890ff;
  border-color: #1890ff;
  background-color: rgba(24, 144, 255, 0.05);
}

.post-actions .el-button--primary {
  background-color: #1890ff;
  border-color: #1890ff;
}

.post-actions .el-button--primary:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 500px;
  text-align: center;
  padding: 0;
  width: 100%;
  margin: 0 auto;
  flex: 1;
}

.load-more {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.load-more-btn {
  border-radius: 20px;
  padding: 8px 32px;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 分页样式 */
.pagination {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.pagination-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.total-posts {
  font-weight: 500;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-size-select {
  width: 100px;
}

.post-pagination {
  display: flex;
  justify-content: center;
}

.post-pagination .el-pagination__sizes {
  margin-right: 16px;
}

.post-pagination .el-pagination__total {
  margin-right: 16px;
}

.post-pagination .el-pagination__jump {
  margin-left: 16px;
}

/* 右侧侧边栏 */
.right-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-sidebar .sidebar-section {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hot-topics-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
  width: 100%;
  border-radius: 8px;
  user-select: none;
  pointer-events: auto;
}

.hot-topic-item:last-child {
  border-bottom: none;
}

.hot-topic-item:hover {
  color: #1890ff;
  background-color: #f0f9ff;
  transform: translateX(4px);
}

.hot-topic-item .topic-rank {
  flex-shrink: 0;
}

.hot-topic-item .topic-name {
  flex: 1;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topic-count {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}

.recommended-users {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  width: 100%;
}

.user-item:last-child {
  border-bottom: none;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.follow-btn {
  flex-shrink: 0;
  border-radius: 12px;
  font-size: 12px;
  padding: 2px 12px;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  background-color: #1890ff;
  color: #ffffff;
}

.announcement {
  background-color: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 8px;
  padding: 16px;
}

.announcement-content {
  font-size: 14px;
  line-height: 1.5;
  color: #1890ff;
  margin: 0 0 12px 0;
}

.announcement-link {
  font-size: 12px;
  color: #1890ff;
  text-decoration: none;
  transition: all 0.3s ease;
}

.announcement-link:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .body-content {
    grid-template-columns: 200px 1fr 240px;
    gap: 20px;
  }
  
  .left-sidebar,
  .right-sidebar .sidebar-section {
    padding: 16px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .search-section {
    max-width: 400px;
  }
}

@media (max-width: 992px) {
  .body-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .left-sidebar {
    position: static;
    order: 2;
  }
  
  .main-content {
    order: 1;
  }
  
  .right-sidebar {
    order: 3;
  }
  
  .category-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .category-list li {
    margin-bottom: 0;
    white-space: nowrap;
    flex: 1 1 calc(33.333% - 8px);
    min-width: 120px;
  }
  
  .hot-topics,
  .followed-topics {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .hot-topics li,
  .followed-topics li {
    border-bottom: none;
    white-space: nowrap;
    background-color: #f8f9fa;
    padding: 6px 12px;
    border-radius: 16px;
  }
  
  .hot-topics-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .hot-topic-item {
    border-bottom: none;
    white-space: nowrap;
    background-color: #f8f9fa;
    padding: 8px 12px;
    border-radius: 16px;
    flex: 1 1 calc(50% - 8px);
    min-width: 150px;
  }
  
  .recommended-users {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .user-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 16px;
    border-bottom: none;
    background-color: #f8f9fa;
    border-radius: 8px;
    width: calc(50% - 8px);
  }
  
  .user-info {
    align-items: center;
  }
  
  .search-section {
    max-width: none;
  }
  
  .header-content {
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .community-header {
    top: 0;
  }
  
  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .search-section {
    max-width: none;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .sort-options {
    justify-content: space-between;
  }
  
  .type-select {
    width: 100%;
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
  
  .user-item {
    width: 100%;
  }
  
  .community-body {
    padding: 120px 0 24px;
  }
  
  .category-list li {
    flex: 1 1 calc(50% - 8px);
  }
  
  .hot-topic-item {
    flex: 1 1 100%;
  }
  
  .search-suggestions {
    max-height: 250px;
  }
}

@media (max-width: 480px) {
  .community-header {
    padding: 12px 0;
  }
  
  .community-logo {
    font-size: 20px;
  }
  
  .create-btn {
    padding: 6px 16px;
    font-size: 12px;
  }
  
  .main-content {
    padding: 16px;
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
  
  .community-body {
    padding: 110px 0 20px;
  }
  
  .category-list li {
    flex: 1 1 100%;
  }
}

/* 容器样式 */
.container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 20px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
}
</style>