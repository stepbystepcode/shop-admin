import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { 
        requiresAuth: false,
        title: '登录'
      }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { 
        requiresAuth: true,
        title: '商家管理系统'
      },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: {
            title: '数据概览'
          }
        },
        {
          path: 'merchants',
          name: 'MerchantDashboard',
          component: () => import('@/views/merchant/MerchantDashboard.vue'),
          meta: {
            title: '商家概览'
          }
        },
        {
          path: 'merchants/list',
          name: 'MerchantList',
          component: () => import('@/views/merchant/MerchantList.vue'),
          meta: {
            title: '商家列表'
          }
        },
        {
          path: 'merchants/:id',
          name: 'MerchantDetail',
          component: () => import('@/views/merchant/Detail.vue'),
          meta: {
            title: '商家详情'
          }
        }
      ]
    }
  ]
})

// 更新页面标题
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // 设置页面标题
  const title = to.meta.title
  if (title) {
    document.title = `${title} - 商家管理系统`
  }

  // 权限检查
  if (requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn()) {
    next('/')
  } else {
    next()
  }
})

export default router