<template>
  <el-container class="app-layout">
    <!-- 管理员：左侧边栏 -->
    <el-aside v-if="isAdmin && $route.path.startsWith('/admin')" :class="['admin-sidebar', { 'collapsed': sidebarCollapsed }]" :width="sidebarCollapsed ? '80px' : '260px'">
      <div class="sidebar-header">
        <div class="sidebar-logo" :class="{ 'collapsed': sidebarCollapsed }">
          <i class="el-icon-s-platform logo-icon"></i>
          <span class="logo-text">管理后台</span>
        </div>
        <div class="sidebar-version" v-if="!sidebarCollapsed">v1.0.0</div>
      </div>
      <el-menu
        :default-active="$route.path"
        class="admin-menu"
        background-color="transparent"
        text-color="#94a3b8"
        active-text-color="#fff"
        router
        :collapse-transition="false"
      >
        <el-menu-item index="/admin" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-s-home menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">工作台</span>
            <div class="menu-badge" v-if="showDashboardBadge && !sidebarCollapsed">
              <span class="badge-dot"></span>
            </div>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-users" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-user menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">用户管理</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-roles" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-s-custom menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">角色权限</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-audit" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-document-checked menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">内容审核</span>
            <div class="menu-badge" v-if="pendingAudits > 0 && !sidebarCollapsed">
              <span class="badge-count">{{ pendingAudits }}</span>
            </div>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-stats" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-data-analysis menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">数据统计</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-config" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-setting menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">系统设置</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-log" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-document menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">操作日志</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-projects" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-document menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">项目管理</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-services" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-goods menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">服务管理</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-community" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-chat-line-round menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">社区管理</span>
          </div>
        </el-menu-item>
        <el-menu-item index="/admin-backup" class="menu-item" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="menu-item-content">
            <i class="el-icon-folder-opened menu-icon"></i>
            <span class="menu-text" v-if="!sidebarCollapsed">数据备份</span>
          </div>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer" :class="{ 'collapsed': sidebarCollapsed }">
        <div class="sidebar-toggle" @click="toggleSidebar">
          <i :class="sidebarCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        </div>
        <div class="user-info" :class="{ 'collapsed': sidebarCollapsed }">
          <div class="user-avatar">
            {{ (user.realName || user.username || 'A').charAt(0) }}
          </div>
          <div class="user-details" v-if="!sidebarCollapsed">
            <div class="user-name">{{ user.realName || user.username }}</div>
            <div class="user-role">管理员</div>
          </div>
        </div>
        <div class="logout-btn" @click="handleCommand('logout')" :class="{ 'collapsed': sidebarCollapsed }">
          <i class="el-icon-switch-button"></i>
          <span v-if="!sidebarCollapsed">退出登录</span>
        </div>
      </div>
    </el-aside>

    <el-container direction="vertical" class="main-wrap">
      <!-- 管理员功能区：包含进入前台按钮和用户信息 -->
      <div v-if="isAdmin && $route.path.startsWith('/admin')" class="admin-top-bar">
        <div class="admin-top-content">
          <!-- 管理员专用：进入前台按钮 -->
          <el-button type="primary" class="admin-frontend-btn" @click="goToFrontend">
            <i class="el-icon-s-home"></i>
            <span>以管理员身份进入前台</span>
          </el-button>
          <div class="user-profile">
            <el-dropdown trigger="click" @command="handleCommand" class="user-dropdown">
              <div class="user-profile-trigger">
                <div class="user-avatar-wrapper">
                  <div class="user-avatar" v-if="!user.avatar">
                    {{ (user.realName || user.username || 'U').charAt(0) }}
                  </div>
                  <img v-else :src="user.avatar" class="user-avatar-image" alt="用户头像" />
                  <div class="avatar-status online"></div>
                </div>
                <div class="user-details">
                  <div class="user-name">{{ user.realName || user.username }}</div>
                  <div class="user-role">系统管理员</div>
                </div>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="header-dropdown">
                <el-dropdown-item command="workspace">
                  <i class="el-icon-s-home"></i> 工作台
                </el-dropdown-item>
                <el-dropdown-item command="myProjects">
                  <i class="el-icon-s-order"></i> 我的项目
                </el-dropdown-item>
                <el-dropdown-item command="myServices">
                  <i class="el-icon-goods"></i> 我的服务
                </el-dropdown-item>
                <el-dropdown-item command="myOrders">
                  <i class="el-icon-s-finance"></i> 我的订单
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>

      <!-- 前台顶部导航栏 -->
      <header v-else class="home-header">
        <div class="header-container">
          <div class="header-content">
            <div class="logo">
              <router-link to="/home" class="logo-text" exact-active-class="nav-brand-active">
                创业者平台
              </router-link>
            </div>
            <nav class="nav-menu">
              <router-link to="/home" class="nav-item" exact active-class="nav-item-active">
                <i class="el-icon-s-home"></i> 首页
              </router-link>
              <router-link to="/projects" class="nav-item" active-class="nav-item-active">
                <i class="el-icon-document"></i> 项目大厅
              </router-link>
              <router-link to="/services" class="nav-item" active-class="nav-item-active">
                <i class="el-icon-goods"></i> 服务市场
              </router-link>
              <router-link to="/articles" class="nav-item" active-class="nav-item-active">
                <i class="el-icon-document"></i> 创业资讯
              </router-link>
              <router-link to="/community" class="nav-item" active-class="nav-item-active">
                <i class="el-icon-chat-line-round"></i> 社区
              </router-link>
              <el-dropdown class="nav-item dropdown-item" trigger="click" @command="handlePublish">
                <span class="nav-item dropdown-trigger">
                  <i class="el-icon-edit-outline"></i> 我要发布
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown" class="publish-dropdown">
                  <el-dropdown-item command="project">
                    <i class="el-icon-document"></i> 发布项目
                  </el-dropdown-item>
                  <el-dropdown-item command="service">
                    <i class="el-icon-goods"></i> 发布服务
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <div class="nav-item" @click="goToMessages" :class="{ 'nav-item-active': $route.path === '/messages' }">
                  <i class="el-icon-message"></i> 消息中心
                  <span class="message-badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
                </div>
            </nav>
            <div class="header-actions">
              <div class="user-profile">
                <el-dropdown trigger="click" @command="handleCommand" class="user-dropdown">
                  <div class="user-profile-trigger">
                    <div class="user-avatar-wrapper">
                      <div class="user-avatar" v-if="!user.avatar">
                        {{ (user.realName || user.username || 'U').charAt(0) }}
                      </div>
                      <img v-else :src="user.avatar" class="user-avatar-image" alt="用户头像" />
                      <div class="avatar-status online"></div>
                    </div>
                    <div class="user-details">
                      <div class="user-name">{{ user.realName || user.username }}</div>
                      <div class="user-role" v-if="isAdmin">管理员</div>
                      <div class="user-role" v-else-if="isEntrepreneur">创业者</div>
                      <div class="user-role" v-else-if="isInvestor">投资者</div>
                      <div class="user-role" v-else-if="isProvider">服务提供商</div>
                    </div>
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </div>
                  <el-dropdown-menu slot="dropdown" class="header-dropdown">
                <el-dropdown-item command="workspace">
                  <i class="el-icon-s-home"></i> 工作台
                </el-dropdown-item>
                <el-dropdown-item command="myProjects">
                  <i class="el-icon-s-order"></i> 我的项目
                </el-dropdown-item>
                <el-dropdown-item command="myServices">
                  <i class="el-icon-goods"></i> 我的服务
                </el-dropdown-item>
                <el-dropdown-item command="myOrders">
                  <i class="el-icon-s-finance"></i> 我的订单
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <i class="el-icon-collection-tag"></i> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>
      </header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'
import { adminAudit } from '@/api/admin'
export default {
  name: 'Layout',
  data() {
    return {
      showDashboardBadge: true,
      pendingAudits: 0,
      sidebarCollapsed: localStorage.getItem('sidebarCollapsed') === 'true'
    }
  },
  computed: {
    ...mapState('user', ['username', 'realName', 'avatar', 'role', 'token']),
    ...mapState('message', ['unreadCount']),
    user() {
      return { username: this.username, realName: this.realName, avatar: this.avatar }
    },
    ...mapGetters('user', ['isAdmin', 'isEntrepreneur', 'isInvestor', 'isProvider']),
    isLoggedIn() {
      return !!this.token || !!localStorage.getItem('token')
    },
    roleEntry() {
      if (this.role === 'admin') return '/admin'
      if (this.role === 'entrepreneur') return '/entrepreneur'
      if (this.role === 'investor') return '/investor'
      if (this.role === 'provider') return '/provider'
      return '/home'
    }
  },
  methods: {
    ...mapActions('message', ['fetchUnreadCount']),
    ...mapActions('user', ['logout']),
    async fetchPendingAudits() {
      if (!this.isAdmin) return
      try {
        // 获取待审核项目数量
        const projectRes = await adminAudit.projectList({ size: 1 })
        const projectCount = projectRes.total || 0
        
        // 获取待审核服务数量
        const resourceRes = await adminAudit.resourceList({ size: 1 })
        const resourceCount = resourceRes.total || 0
        
        // 计算总待审核数量
        this.pendingAudits = projectCount + resourceCount
      } catch (error) {
        console.error('获取待审核数量失败:', error)
      }
    },
    handleCommand(cmd) {
      // 检查是否登录
      if (!this.isLoggedIn) {
        this.$message.error('请先登录')
        this.$router.push('/login')
        return
      }
      
      if (cmd === 'workspace') {
        // 根据用户角色跳转到相应的工作台
        let targetRoute = '/home'
        if (this.isEntrepreneur) {
          targetRoute = '/entrepreneur'
        } else if (this.isInvestor) {
          targetRoute = '/investor'
        } else if (this.isProvider) {
          targetRoute = '/provider'
        }
        if (this.$route.path !== targetRoute) {
          this.$router.push(targetRoute)
        }
      } else if (cmd === 'myProjects') {
        if (this.$route.path !== '/my-projects') {
          this.$router.push('/my-projects')
        }
      } else if (cmd === 'myServices') {
        if (this.$route.path !== '/my-services') {
          this.$router.push('/my-services')
        }
      } else if (cmd === 'myOrders') {
        if (this.$route.path !== '/my-orders') {
          this.$router.push('/my-orders')
        }
      } else if (cmd === 'profile') {
        if (this.$route.path !== '/profile') {
          this.$router.push('/profile')
        }
      } else if (cmd === 'favorites') {
        if (this.$route.path !== '/favorites') {
          this.$router.push('/favorites')
        }
      } else if (cmd === 'logout') {
        this.logout()
        this.$router.push('/login')
      }
    },
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
      localStorage.setItem('sidebarCollapsed', this.sidebarCollapsed)
    },
    // 管理员进入前台方法
    goToFrontend() {
      // 使用当前管理员账号身份信息切换到前台
      this.$router.push('/home')
    },
    // 处理发布按钮点击
    handlePublish(cmd) {
      if (!this.isLoggedIn) {
        this.$message.error('请先登录')
        this.$router.push('/login')
        return
      }
      if (cmd === 'project') {
        this.$router.push('/project-publish')
      } else if (cmd === 'service') {
        this.$router.push('/service-publish')
      }
    },
    // 处理消息中心点击
    goToMessages() {
      if (!this.isLoggedIn) {
        this.$message.error('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/messages')
    }
  },
  mounted() {
    this.fetchUnreadCount()
    if (this.isAdmin) {
      this.fetchPendingAudits()
    }
    
    // 监听来自Audit组件的事件，当审核操作完成时更新待审核数量
    this.$root.$on('refreshPendingAudits', () => {
      if (this.isAdmin) {
        this.fetchPendingAudits()
      }
    })
  },
  beforeDestroy() {
    // 清理事件监听器
    this.$root.$off('refreshPendingAudits')
  },
  watch: {
    // 监听路由变化，当切换到审核相关页面时刷新待审核数量
    $route: {
      handler() {
        if (this.isAdmin) {
          this.fetchPendingAudits()
        }
      },
      immediate: false
    }
  }
}
</script>

<style scoped>
.app-layout { min-height: 100vh; }
.admin-sidebar {
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  box-shadow: 4px 0 24px rgba(0,0,0,.12);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.admin-sidebar.collapsed {
  width: 80px !important;
}

.sidebar-header {
  position: relative;
  padding: 20px 0;
  border-bottom: none;
  text-align: center;
}

.sidebar-toggle {
  width: 100%;
  height: 40px;
  border-radius: 10px;
  background: rgba(255,255,255,.08);
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255,255,255,.12);
  margin-bottom: 16px;
}

.sidebar-toggle:hover {
  background: rgba(59,130,246,.2);
  color: #3b82f6;
  border-color: rgba(59,130,246,.3);
  transform: scale(1.02);
}

.admin-sidebar.collapsed .sidebar-toggle {
  margin-bottom: 12px;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.sidebar-logo.collapsed {
  gap: 0;
  justify-content: center;
}

.sidebar-logo.collapsed .logo-text {
  display: none;
}

.admin-sidebar.collapsed .sidebar-footer {
  padding: 16px 8px;
}

.admin-sidebar.collapsed .user-info {
  justify-content: center;
  padding: 8px;
}

.admin-sidebar.collapsed .user-info .user-details {
  display: none;
}

.admin-sidebar.collapsed .logout-btn {
  justify-content: center;
  padding: 12px;
}

.admin-sidebar.collapsed .logout-btn span {
  display: none;
}

.admin-sidebar.collapsed .menu-item {
  margin: 6px 8px;
  justify-content: center;
}

.admin-sidebar.collapsed .menu-item-content {
  justify-content: center;
  padding: 0 12px;
}

.admin-sidebar.collapsed .menu-item:hover {
  transform: none;
}

.admin-sidebar.collapsed .menu-item.is-active {
  border-left: none;
  background: rgba(59,130,246,.25) !important;
}

.admin-sidebar.collapsed .menu-badge {
  display: none;
}
.logo-icon { 
  font-size: 28px; 
  color: #3b82f6;
  animation: logoPulse 2s ease-in-out infinite;
}
.logo-text {
  font-size: 19px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}
.sidebar-version {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}
.admin-menu {
  border-right: none;
  padding: 20px 0;
  flex: 1;
}
.admin-menu .menu-item {
  margin: 6px 16px;
  border-radius: 12px;
  height: 56px;
  line-height: 56px;
  transition: all .3s ease;
  overflow: hidden;
}
.admin-menu .menu-item:hover {
  background: rgba(255,255,255,.08) !important;
  color: #e2e8f0;
  transform: translateX(4px);
}
.admin-menu .menu-item.is-active {
  background: linear-gradient(90deg, rgba(59,130,246,.25) 0%, rgba(59,130,246,.1) 100%) !important;
  color: #fff;
  border-left: 3px solid #3b82f6;
}
.menu-item-content {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 100%;
}
.menu-icon {
  font-size: 20px;
  margin-right: 14px;
  width: 24px;
  text-align: center;
}
.menu-text {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
}
.menu-badge {
  display: flex;
  align-items: center;
  justify-content: center;
}
.badge-dot {
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: badgePulse 2s ease-in-out infinite;
}
.badge-count {
  background: #3b82f6;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}
.sidebar-footer {
  padding: 20px 16px;
  border-top: none;
  background: rgba(15, 23, 42, 0.8);
}
.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255,255,255,.04);
  transition: all .3s ease;
}
.user-info:hover {
  background: rgba(255,255,255,.08);
  transform: translateY(-2px);
}
.user-info .user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}
.user-details {
  flex: 1;
  min-width: 0;
}
.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.user-role {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}
.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.15));
  color: #fca5a5;
  cursor: pointer;
  transition: all .3s ease;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(239, 68, 68, 0.2);
  box-shadow: none;
  outline: none;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}
.logout-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0));
  pointer-events: none;
}
.logout-btn:hover {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.25), rgba(220, 38, 38, 0.25));
  color: #ef4444;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
}
.logout-btn i {
  font-size: 16px;
  position: relative;
  z-index: 1;
}
.logout-btn span {
  position: relative;
  z-index: 1;
}

/* 确保在折叠状态下也没有白边 */
.sidebar-footer.collapsed .logout-btn {
  border-color: rgba(239, 68, 68, 0.2);
  box-shadow: none;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.15));
}

/* 动画效果 */
@keyframes logoPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.05); opacity: 0.9; }
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-sidebar {
    width: 220px !important;
  }
  
  .logo-text {
    font-size: 17px;
  }
  
  .menu-text {
    font-size: 14px;
  }
  
  .user-name {
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    position: fixed;
    left: -260px;
    top: 0;
    bottom: 0;
    z-index: 1001;
    transition: left .3s ease;
  }
  
  .admin-sidebar.open {
    left: 0;
  }
  
  .main-content {
    padding: 16px;
  }
}

.main-wrap { flex: 1; min-width: 0; }

/* 前台顶部导航栏 */
.home-header {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #FFFFFF;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: none;
}

.home-header:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.header-container {
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  max-width: 100%;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  transition: all 0.3s ease;
}

.logo {
  flex-shrink: 0;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #333333;
  letter-spacing: 0.5px;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(59, 130, 246, 0.05);
  border: 1px solid rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-text::before {
  content: '⚡';
  font-size: 18px;
  animation: pulse 2s infinite;
}

.logo-text:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.2);
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  font-size: 14px;
  font-weight: 500;
  color: #666666;
  text-decoration: none;
  padding: 12px 20px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dropdown-item {
  cursor: pointer;
}

.dropdown-trigger {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-trigger:hover {
  color: #3b82f6;
}

.publish-dropdown.el-dropdown-menu {
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: #ffffff;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 180px;
}

.publish-dropdown .el-dropdown-menu__item {
  border-radius: 8px;
  padding: 14px 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #333333;
  font-size: 14px;
  font-weight: 500;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  overflow: hidden;
}

.publish-dropdown .el-dropdown-menu__item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.publish-dropdown .el-dropdown-menu__item:hover {
  background: rgba(59, 130, 246, 0.05);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.1);
  transform: translateX(4px);
  padding-left: 20px;
}

.publish-dropdown .el-dropdown-menu__item:hover::before {
  transform: scaleY(1);
}

.publish-dropdown .el-dropdown-menu__item i {
  font-size: 16px;
  transition: all 0.3s ease;
  width: 20px;
  text-align: center;
  color: #666666;
}

.publish-dropdown .el-dropdown-menu__item:hover i {
  transform: scale(1.1);
  color: #3b82f6;
}

.nav-item:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.nav-item-active {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
  border: 1px solid rgba(59, 130, 246, 0.1);
}

.nav-item i {
  font-size: 16px;
  transition: all 0.3s ease;
  color: #666666;
}

.nav-item:hover i {
  transform: scale(1.1);
  color: #3b82f6;
}

.message-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 20px;
  height: 20px;
  padding: 0 8px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
  animation: badgePulse 2s infinite;
}

.nav-item {
  position: relative;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

/* 前台用户信息样式 */
.home-header .user-profile-trigger {
  background: rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.home-header .user-profile-trigger:hover {
  background: rgba(59, 130, 246, 0.05);
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.home-header .user-name {
  color: #333333;
}

.home-header .user-role {
  color: #999999;
}

.home-header .user-profile-trigger:hover .user-name {
  color: #3b82f6;
}

.home-header .user-profile-trigger:hover .user-role {
  color: #60a5fa;
}

.home-header .user-profile-trigger .el-icon--right {
  color: #999999;
}

.home-header .user-profile-trigger:hover .el-icon--right {
  color: #3b82f6;
}

/* 管理员顶部功能栏 */
.admin-top-bar {
  background: #0f172a;
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-top-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 100%;
}

/* 管理员进入前台按钮样式 */
.admin-frontend-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 10px 20px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  color: white !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3) !important;
  cursor: pointer !important;
  position: relative !important;
  overflow: hidden !important;
}

.admin-frontend-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4) !important;
}

.admin-frontend-btn:active {
  transform: translateY(0) !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3) !important;
}

.admin-frontend-btn::before {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: -100% !important;
  width: 100% !important;
  height: 100% !important;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent) !important;
  transition: left 0.6s ease !important;
}

.admin-frontend-btn:hover::before {
  left: 100% !important;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-frontend-btn {
    padding: 8px 16px !important;
    font-size: 13px !important;
  }
  
  .admin-frontend-btn i {
    font-size: 14px !important;
  }
}

@media (max-width: 768px) {
  .admin-frontend-btn {
    padding: 6px 12px !important;
    font-size: 12px !important;
  }
  
  .admin-frontend-btn i {
    font-size: 12px !important;
  }
  
  .admin-frontend-btn span {
    display: none;
  }
  
  .admin-frontend-btn {
    min-width: 40px;
    justify-content: center;
  }
}

.user-profile {
  position: relative;
}

.user-profile-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.user-profile-trigger:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
}

.user-avatar-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.user-avatar-image {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  object-fit: cover;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.user-profile-trigger:hover .user-avatar-image {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
  border-color: rgba(255, 255, 255, 0.2);
}

.user-profile-trigger:hover .user-avatar {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
  border-color: rgba(255, 255, 255, 0.2);
}

.avatar-status {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid rgba(15, 23, 42, 0.8);
  transition: all 0.3s ease;
}

.avatar-status.online {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
  color: #f1f5f9;
  transition: all 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.user-role {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.user-profile-trigger:hover .user-name {
  color: #ffffff;
}

.user-profile-trigger:hover .user-role {
  color: #93c5fd;
}

.user-profile-trigger .el-icon--right {
  font-size: 12px;
  color: #94a3b8;
  margin-left: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-profile-trigger:hover .el-icon--right {
  transform: rotate(180deg);
  color: #93c5fd;
}

.user-dropdown {
  display: flex;
  align-items: center;
  width: 100%;
}

.main-content {
  padding: 0;
  min-height: calc(100vh - 64px);
}

/* 后台页面的主内容区域 */
.admin-top-bar + .main-content {
  min-height: calc(100vh - 100px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-top-bar {
    padding: 8px 16px;
  }
  
  .admin-top-content {
    gap: 12px;
  }
  
  .user-profile-trigger {
    gap: 8px;
    padding: 8px 12px;
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .user-avatar-image {
    width: 32px;
    height: 32px;
  }
  
  .user-details {
    display: none;
  }
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(239, 68, 68, 0.6);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    padding: 0 16px;
  }
  
  .nav-menu {
    gap: 8px;
  }
  
  .nav-item {
    padding: 8px 12px;
    font-size: 13px;
  }
  
  .logo-text {
    font-size: 18px;
    padding: 8px 16px;
  }
}

@media (max-width: 768px) {
  .header-content {
    height: 56px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .logo-text {
    font-size: 16px;
    padding: 6px 12px;
  }
  
  .user-profile-trigger {
    gap: 8px;
    padding: 8px 12px;
    border-radius: 12px;
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
    font-size: 12px;
    border-radius: 8px;
  }
  
  .user-details {
    display: none;
  }
  
  .user-profile-trigger .el-icon--right {
    margin-left: 2px;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 0 12px;
  }
  
  .logo-text {
    font-size: 14px;
    padding: 4px 8px;
  }
  
  .logo-text::before {
    font-size: 14px;
  }
  
  .user-profile-trigger {
    padding: 6px 8px;
  }
  
  .user-avatar {
    width: 28px;
    height: 28px;
    font-size: 11px;
  }
  
  .avatar-status {
    width: 10px;
    height: 10px;
    bottom: -1px;
    right: -1px;
  }
}


</style>

<style>
/* 下拉菜单：平滑展开 + 阴影过渡 */
.header-dropdown.el-dropdown-menu {
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: #ffffff;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 180px;
}

.header-dropdown .el-dropdown-menu__item {
  border-radius: 8px;
  padding: 14px 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #333333;
  font-size: 14px;
  font-weight: 500;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  overflow: hidden;
}

.header-dropdown .el-dropdown-menu__item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.header-dropdown .el-dropdown-menu__item:hover {
  background: rgba(59, 130, 246, 0.05);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.1);
  transform: translateX(4px);
  padding-left: 20px;
}

.header-dropdown .el-dropdown-menu__item:hover::before {
  transform: scaleY(1);
}

.header-dropdown .el-dropdown-menu__item i {
  font-size: 16px;
  transition: all 0.3s ease;
  width: 20px;
  text-align: center;
  color: #666666;
}

.header-dropdown .el-dropdown-menu__item:hover i {
  transform: scale(1.1);
  color: #3b82f6;
}

.header-dropdown .el-dropdown-menu__item.divided {
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  margin-top: 8px;
  padding-top: 14px;
}

/* 确保下拉菜单项在不同状态下的样式一致 */
.header-dropdown .el-dropdown-menu__item:focus {
  background: rgba(59, 130, 246, 0.05);
  color: #3b82f6;
  border-color: rgba(59, 130, 246, 0.1);
}

/* 下拉菜单动画效果 */
.el-dropdown-menu-enter-active,
.el-dropdown-menu-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-dropdown-menu-enter,
.el-dropdown-menu-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}
</style>
