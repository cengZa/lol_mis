import pinia from '@/stores'
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/auth',
    component: null,
    meta: { guestOnly: true },
    children: [
      {
        path: 'login',
        component: () => import('@/views/auth/Login.vue'),
      },
      {
        path: 'register',
        component: () => import('@/views/auth/Register.vue'),
      }
    ]
  },


  {
    path: '/',
    name: 'MainLayout',
    component: () => import('@/views/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'change-password',
        component: () => import('@/views/user/ChangePassword.vue')
      },
      {
        path: "heroes",
        name: "HeroList",
        component: () => import('@/views/hero/HeroList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'heroDetail/:heroId',
        name: 'HeroDetail',
        component: () => import('@/views/hero/HeroDetail.vue')
      }
    ]
  }
]


const router = createRouter({
  history: createWebHistory(),
  routes
})

// 门卫系统（路由守卫）
router.beforeEach(async (to) => {

  // ?这里为什么需要传一个pinia实例？
  // *因为是在非组件环境，但是为什么呢？
  const auth = useAuthStore(pinia)

  console.log('前往路由:', to.path)
  console.log('认证状态:', auth.token)

  // 需要登录且无token时重定向
  if (to.meta.requiresAuth && !auth.token) {
    return '/auth/login'
  }

  // 已登录时禁止访问游客页
  if (to.meta.guestOnly && auth.token) {
    return '/'
  }

  // 已登录但未获取用户信息时自动获取
  if (auth.token && !auth.userInfo) {
    try {
      await auth.fetchUserInfo();
    } catch (error) {
      console.error('获取用户信息失败', error);
      auth.logout();
      return '/auth/login'
    }
  }
})

export default router