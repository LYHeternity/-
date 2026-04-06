import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { guest: true } },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'admin', name: 'Admin', component: () => import('@/views/admin/Dashboard.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'entrepreneur', name: 'Entrepreneur', component: () => import('@/views/entrepreneur/Dashboard.vue'), meta: { requiresAuth: true, role: 'entrepreneur' } },
      { path: 'investor', name: 'Investor', component: () => import('@/views/investor/Dashboard.vue'), meta: { requiresAuth: true, role: 'investor' } },
      { path: 'provider', name: 'Provider', component: () => import('@/views/provider/Dashboard.vue'), meta: { requiresAuth: true, role: 'provider' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
      { path: 'projects', name: 'ProjectList', component: () => import('@/views/project/List.vue') },
      { path: 'project/:id', name: 'ProjectDetail', component: () => import('@/views/project/Detail.vue') },
      { path: 'project-publish', name: 'ProjectPublish', component: () => import('@/views/project/Publish.vue'), meta: { requiresAuth: true, role: ['entrepreneur', 'founder'] } },
      { path: 'my-projects', name: 'MyProjects', component: () => import('@/views/project/MyList.vue'), meta: { requiresAuth: true } },
      { path: 'my-services', name: 'MyServices', component: () => import('@/views/resource/MyList.vue'), meta: { requiresAuth: true } },
      { path: 'services', name: 'ServiceList', component: () => import('@/views/resource/List.vue') },
      { path: 'service/:id', name: 'ServiceDetail', component: () => import('@/views/resource/Detail.vue') },
      { path: 'service-publish', name: 'ServicePublish', component: () => import('@/views/resource/Publish.vue'), meta: { requiresAuth: true, role: ['provider', 'entrepreneur', 'founder'] } },
      { path: 'my-investments', name: 'MyInvestments', component: () => import('@/views/investment/MyList.vue'), meta: { requiresAuth: true, role: 'investor' } },
      { path: 'my-orders', name: 'MyOrders', component: () => import('@/views/order/MyList.vue'), meta: { requiresAuth: true } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/order/Detail.vue'), meta: { requiresAuth: true } },
      { path: 'provider-orders', name: 'ProviderOrders', component: () => import('@/views/order/ProviderList.vue'), meta: { requiresAuth: true, role: ['provider', 'entrepreneur', 'founder'] } },
      { path: 'messages', name: 'Messages', component: () => import('@/views/Messages.vue'), meta: { requiresAuth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/Favorites.vue'), meta: { requiresAuth: true } },
      { path: 'admin-users', name: 'AdminUsers', component: () => import('@/views/admin/UserList.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-audit', name: 'AdminAudit', component: () => import('@/views/admin/Audit.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-stats', name: 'AdminStats', component: () => import('@/views/admin/Stats.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-config', name: 'AdminConfig', component: () => import('@/views/admin/Config.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-log', name: 'AdminLog', component: () => import('@/views/admin/LogList.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-roles', name: 'AdminRoles', component: () => import('@/views/admin/RoleManage.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-backup', name: 'AdminBackup', component: () => import('@/views/admin/Backup.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-projects', name: 'AdminProject', component: () => import('@/views/admin/ProjectManage.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-services', name: 'AdminService', component: () => import('@/views/admin/ServiceManage.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'admin-community', name: 'AdminCommunity', component: () => import('@/views/admin/CommunityManage.vue'), meta: { requiresAuth: true, role: 'admin' } },
      { path: 'articles', name: 'ArticleList', component: () => import('@/views/article/List.vue') },
      { path: 'article/detail/:id', name: 'ArticleDetail', component: () => import('@/views/article/Detail.vue') },
      { path: 'community', name: 'CommunityHome', component: () => import('@/views/community/CommunityHome.vue') },
      { path: 'community/create', name: 'PostCreate', component: () => import('@/views/community/PostCreate.vue'), meta: { requiresAuth: true } },
      { path: 'community/edit/:id', name: 'PostEdit', component: () => import('@/views/community/PostCreate.vue'), meta: { requiresAuth: true } },
      { path: 'community/post/:id', name: 'PostDetail', component: () => import('@/views/community/PostDetail.vue') },
      { path: 'community/favorites', name: 'CommunityFavorites', component: () => import('@/views/community/FavoritePosts.vue'), meta: { requiresAuth: true } }
    ]
  }
]

const router = new VueRouter({ mode: 'history', base: process.env.BASE_URL, routes })

// 捕获NavigationDuplicated和Redirected错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name === 'NavigationDuplicated' || err.message && err.message.includes('Redirected when going')) {
      // 忽略重复导航和重定向错误
      return err
    }
    // 抛出其他错误
    return Promise.reject(err)
  })
}

// 同样处理replace方法的错误
const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (err.name === 'NavigationDuplicated' || err.message && err.message.includes('Redirected when going')) {
      // 忽略重复导航和重定向错误
      return err
    }
    // 抛出其他错误
    return Promise.reject(err)
  })
}

router.beforeEach((to, from, next) => {
  let token = store.state.user.token
  let userRole = store.state.user.role
  if (!token) {
    // 检查localStorage中是否有token
    token = localStorage.getItem('token')
    if (token) {
      try {
        const saved = localStorage.getItem('user')
        if (saved) {
          const data = JSON.parse(saved)
          // 验证token是否有效（简单验证：检查是否过期）
          const tokenPayload = JSON.parse(atob(token.split('.')[1]))
          const currentTime = Math.floor(Date.now() / 1000)
          if (tokenPayload.exp && tokenPayload.exp < currentTime) {
            // Token已过期，清除本地存储
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            token = null
          } else {
            // Token有效，设置用户信息
            store.commit('user/SET_TOKEN', token)
            store.commit('user/SET_USER', {
              userId: data.userId,
              username: data.username,
              role: data.role,
              avatar: data.avatar,
              realName: data.realName,
              permissions: data.permissions || []
            })
            userRole = data.role
          }
        }
      } catch (_) {
        // 清除无效的token和user信息
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        token = null
      }
    }
  } else {
    userRole = store.state.user.role
  }
  // 处理guest页面
  if (to.meta.guest) {
    if (token) {
      // 登录用户访问登录/注册页时，根据角色跳转
      if (userRole === 'admin') {
        return next({ path: '/admin', replace: true })
      } else {
        return next({ path: '/', replace: true })
      }
    } else {
      // 未登录用户可以访问登录/注册页
      return next()
    }
  }
  // 处理需要认证的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      // 使用setTimeout延迟重定向，避免Vue Router的错误检测
      setTimeout(() => {
        next({ path: '/login', replace: true })
      }, 0)
      return
    }
  }
  // 处理需要特定角色的页面
  if (to.meta.role && token) {
    if (userRole === 'admin') {
      // 管理员可以访问所有页面
      return next()
    }
    const roles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]
    if (!roles.includes(userRole)) {
      return next({ path: '/', replace: true })
    }
  }
  // 其他情况，允许导航
  next()
})

export default router
