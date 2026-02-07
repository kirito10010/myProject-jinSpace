import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login' // 默认重定向到登录页
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { requiresAuth: false } // 不需要登录
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
    meta: { requiresAuth: false } // 不需要登录
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }, // 需要登录
    redirect: '/home/resource/url', // 默认重定向到网址链接
    children: [
      {
        path: 'resource/url',
        name: 'resource-url',
        component: () => import('../views/ResourceUrl.vue')
      },
      {
        path: 'resource/exe',
        name: 'resource-exe',
        component: () => import('../views/ResourceExe.vue')
      },
      {
        path: 'resource/zip',
        name: 'resource-zip',
        component: () => import('../views/ResourceZip.vue')
      },
      {
        path: 'tools/weather',
        name: 'tools-weather',
        component: () => import('../views/ToolsWeather.vue')
      },
      {
        path: 'tools/music',
        name: 'tools-music',
        component: () => import('../views/ToolsMusic.vue')
      },
      {
        path: 'data/finance',
        name: 'data-finance',
        component: () => import('../views/DataFinance.vue')
      },
      {
        path: 'data/statistics',
        name: 'data-statistics',
        component: () => import('../views/DataStatistics.vue')
      },
      {
        path: 'data/performance',
        name: 'data-performance',
        component: () => import('../views/DataPerformance.vue')
      },
      {
        path: 'admin/user-management',
        name: 'admin-user-management',
        component: () => import('../views/AdminUserManagement.vue'),
        meta: { requiresSuperAdmin: true }
      },
      {
        path: 'admin/feedback',
        name: 'admin-feedback',
        component: () => import('../views/AdminFeedback.vue'),
        meta: { requiresSuperAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 检查路由是否需要登录
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  // 检查路由是否需要超级管理员权限
  const requiresSuperAdmin = to.matched.some(record => record.meta.requiresSuperAdmin)
  
  // 获取用户信息
  const userInfo = localStorage.getItem('userInfo')
  let user = null
  try {
    user = userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    user = null
  }
  
  if (requiresAuth) {
    // 需要登录的路由
    if (user) {
      // 已登录，检查是否需要超级管理员权限
      if (requiresSuperAdmin) {
        // 需要超级管理员权限
        if (user.role === 1) {
          // 是超级管理员，允许访问
          next()
        } else {
          // 不是超级管理员，重定向到主页
          next('/home')
        }
      } else {
        // 不需要超级管理员权限，允许访问
        next()
      }
    } else {
      // 未登录，重定向到登录页
      next('/login')
    }
  } else {
    // 不需要登录的路由
    if (user && (to.path === '/login' || to.path === '/register')) {
      // 已登录，访问登录或注册页时重定向到主页
      next('/home')
    } else {
      // 允许访问
      next()
    }
  }
})

export default router