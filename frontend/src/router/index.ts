import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/user';
import MainLayout from '@/layouts/MainLayout.vue';
import Login from '@/views/Login.vue';
import Dashboard from '@/views/Dashboard.vue';
import MerchantList from '@/views/merchant/List.vue';
import MerchantDetail from '@/views/merchant/Detail.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: Dashboard
        },
        {
          path: 'merchants',
          name: 'MerchantList',
          component: MerchantList
        },
        {
          path: 'merchants/:id',
          name: 'MerchantDetail',
          component: MerchantDetail
        }
      ]
    }
  ]
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if (requiresAuth && !userStore.token) {
    next('/login');
  } else if (to.path === '/login' && userStore.token) {
    next('/');
  } else {
    next();
  }
});

export default router;