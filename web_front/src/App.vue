<script setup>
import { ref, watch, computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import CustomCursor from '@/components/CustomCursor.vue'
import ThemeToggle from '@/components/ThemeToggle.vue'
import MusicPlayer from '@/components/MusicPlayer.vue'
import Live2dWidget from '@/components/Live2dWidget.vue'
import AuthModal from '@/components/AuthModal.vue'
import { useAuth } from '@/stores/auth'

const route = useRoute()
const showAuth = ref(false)
const { isLoggedIn, logout, user, fetchUser } = useAuth()

const navItems = computed(() => {
  const items = [
    { to: '/', label: '首页' },
    { to: '/resume', label: '简历' },
    { to: '/blog', label: '博客' },
    { to: '/notes', label: '笔记' },
    { to: '/message', label: '留言板' },
    { to: '/resources', label: '资源分享' },
    { to: '/love-diary', label: '恋爱日记' },
    { to: '/contact', label: '联系我' },
  ]
  if (isLoggedIn.value) items.push({ to: '/profile', label: '个人主页' })
  if (user.value?.role === 'ADMIN') items.push({ to: '/admin', label: '管理' })
  return items
})

watch(() => route.query.auth, (v) => {
  if (v === '1') showAuth.value = true
}, { immediate: true })

function onAuthSuccess() {
  showAuth.value = false
  fetchUser()
}

fetchUser()
</script>

<template>
  <div id="app">
    <CustomCursor />

    <!-- 亮度遮罩层（日夜切换用） -->
    <div class="brightness-overlay" />

    <!-- 顶部导航 -->
    <nav class="glass-nav">
      <div class="nav-inner">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-link"
        >
          {{ item.label }}
        </RouterLink>
        <button v-if="!isLoggedIn" type="button" class="nav-link" @click="showAuth = true">登入</button>
        <button v-else type="button" class="nav-link" @click="logout">登出</button>
      </div>
    </nav>

    <!-- 右上角控件 -->
    <div class="top-right-controls">
      <ThemeToggle />
      <MusicPlayer />
    </div>

    <RouterView />

    <AuthModal v-if="showAuth" @close="showAuth = false" @success="onAuthSuccess" />

    <Live2dWidget />
  </div>
</template>

<style>
.glass-nav {
  position: fixed;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
}

.nav-inner {
  display: flex;
  gap: 0;
  background: transparent;
  border: none;
  border-radius: 40px;
  padding: 4px 6px;
}

.nav-link {
  position: relative;
  padding: 6px 20px;
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.65);
  border-radius: 30px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

button.nav-link {
  border: none;
  background: transparent;
  cursor: pointer;
}

.nav-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.nav-link.router-link-exact-active {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

/* 右上角控件组 */
.top-right-controls {
  position: fixed;
  top: 14px;
  right: 20px;
  z-index: 1000;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 日夜亮度遮罩 */
.brightness-overlay {
  position: fixed;
  inset: 0;
  z-index: 3;
  pointer-events: none;
  transition: background 0.6s ease;
  background: transparent;
}

/* 夜晚模式（默认） */
[data-theme="night"] .brightness-overlay {
  background: rgba(0, 0, 10, 0.15);
}

/* 白天模式 */
[data-theme="day"] .brightness-overlay {
  background: rgba(255, 255, 200, 0.06);
}

[data-theme="day"] .nav-link {
  color: rgba(255, 255, 255, 0.8);
}

[data-theme="day"] .nav-link.router-link-exact-active {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
}
</style>
