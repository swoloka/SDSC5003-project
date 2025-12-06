import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import PetProfilesView from '../views/PetProfilesView.vue'
import ServiceProfilesView from '../views/ServiceProfilesView.vue'
import MyPostsView from '../views/MyPostsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/pet-profiles',
      name: 'pet-profiles',
      component: PetProfilesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/service-profiles',
      name: 'service-profiles',
      component: ServiceProfilesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/my-posts',
      name: 'my-posts',
      component: MyPostsView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth

  // 初始化认证状态（从localStorage恢复）
  if (!authStore.isLoggedIn) {
    authStore.initAuth()
  }

  if (requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router

