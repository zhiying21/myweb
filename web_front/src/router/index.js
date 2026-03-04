import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/views/Resume.vue'),
    meta: { title: '简历' },
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('@/views/Blog.vue'),
    meta: { title: '博客' },
  },
  {
    path: '/notes',
    name: 'Notes',
    component: () => import('@/views/Notes.vue'),
    meta: { title: '笔记' },
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/views/Message.vue'),
    meta: { title: '留言板' },
  },
  {
    path: '/resources',
    name: 'Resources',
    component: () => import('@/views/Resources.vue'),
    meta: { title: '资源分享' },
  },
  {
    path: '/love-diary',
    name: 'LoveDiary',
    component: () => import('@/views/LoveDiary.vue'),
    meta: { title: '恋爱日记' },
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('@/views/Contact.vue'),
    meta: { title: '联系我' },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人主页', requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/Admin.vue'),
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 枝莺`
  }
  const auth = useAuth()
  const token = localStorage.getItem('zhiying_token')
  const isLoggedIn = !!token
  if (to.meta?.requiresAuth && !isLoggedIn) {
    return { path: '/', query: { auth: '1' } }
  }
  if (to.meta?.requiresAdmin) {
    if (!isLoggedIn) return { path: '/', query: { auth: '1' } }
    if (!auth.user?.value?.role) await auth.fetchUser()
    if (auth.user?.value?.role !== 'ADMIN') {
      return { path: '/' }
    }
  }
})

export default router
