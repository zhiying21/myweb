<script setup>
import { ref, onMounted, onUnmounted, provide, watch } from 'vue'
import PersonalProfile from '@/components/PersonalProfile.vue'
import ScrollHint from '@/components/ScrollHint.vue'
import { useScrollSection } from '@/composables/useScrollSection'
import request from '@/utils/request'

const pureMode = ref(false)
provide('pureMode', pureMode)
useScrollSection()

const fullText = '🌸 如有三两欢喜 🍃 也是天上人间 🌺'
const displayed = ref('')
let timer = null
let charIndex = 0
let isDeleting = false

const TYPE_SPEED = 150
const DELETE_SPEED = 80
const PAUSE_AFTER_TYPE = 2500
const PAUSE_AFTER_DELETE = 800

// 网站统计数据
const stats = ref({ runningHours: 0, visitCount: 0, blogCount: 0, noteCount: 0 })

function tick() {
  if (!isDeleting) {
    displayed.value = fullText.slice(0, charIndex + 1)
    charIndex++
    if (charIndex >= fullText.length) {
      isDeleting = true
      timer = setTimeout(tick, PAUSE_AFTER_TYPE)
      return
    }
    timer = setTimeout(tick, TYPE_SPEED)
  } else {
    displayed.value = fullText.slice(0, charIndex)
    charIndex--
    if (charIndex < 0) {
      isDeleting = false
      charIndex = 0
      timer = setTimeout(tick, PAUSE_AFTER_DELETE)
      return
    }
    timer = setTimeout(tick, DELETE_SPEED)
  }
}

async function fetchStats() {
  try {
    // 记录访问
    await request.post('/site/visit')
    const res = await request.get('/site/stats')
    const d = res?.data ?? res
    if (d) {
      stats.value = {
        runningHours: d.runningHours ?? 0,
        visitCount: d.visitCount ?? 0,
        blogCount: d.blogCount ?? 0,
        noteCount: d.noteCount ?? 0,
      }
    }
  } catch {
    // 静默失败
  }
}

watch(pureMode, (v) => {
  document.body.classList.toggle('pure-mode', v)
})

onMounted(() => {
  timer = setTimeout(tick, 600)
  if (pureMode.value) document.body.classList.add('pure-mode')
  fetchStats()
})

onUnmounted(() => {
  clearTimeout(timer)
  document.body.classList.remove('pure-mode')
})
</script>

<template>
  <div class="home-page" :class="{ 'pure-mode': pureMode }">
    <!-- 固定视频背景（全页滚动时保持） -->
    <video
      class="video-bg-fixed"
      autoplay
      muted
      loop
      playsinline
      preload="auto"
    >
      <source src="/video/2_duro.mp4" type="video/mp4" />
    </video>
    <div class="video-overlay-fixed" />

    <!-- 左下角纯净模式 -->
    <button type="button" class="pure-mode-btn" :title="pureMode ? '退出纯净模式' : '纯净模式'" @click="pureMode = !pureMode">
      {{ pureMode ? '退出纯净' : '纯净模式' }}
    </button>

    <!-- 首屏 Hero -->
    <section class="hero-section scroll-section">
      <div class="hero">
        <h1 class="hero-title">枝 莺</h1>
        <p class="hero-sub">
          <span class="typed-text">{{ displayed }}</span>
          <span class="cursor-blink">|</span>
        </p>

        <!-- 网站统计卡片 -->
        <div class="stats-bar">
          <div class="stat-item">
            <span class="stat-value">{{ stats.runningHours }}</span>
            <span class="stat-label">运行小时</span>
          </div>
          <div class="stat-divider" />
          <div class="stat-item">
            <span class="stat-value">{{ stats.visitCount }}</span>
            <span class="stat-label">访问次数</span>
          </div>
          <div class="stat-divider" />
          <div class="stat-item">
            <span class="stat-value">{{ stats.blogCount }}</span>
            <span class="stat-label">博客文章</span>
          </div>
          <div class="stat-divider" />
          <div class="stat-item">
            <span class="stat-value">{{ stats.noteCount }}</span>
            <span class="stat-label">学习笔记</span>
          </div>
        </div>
      </div>
    </section>

    <ScrollHint />

    <!-- 个人主页 -->
    <PersonalProfile />
  </div>
</template>

<style scoped>
.home-page {
  overflow-x: hidden;
}

/* 固定视频背景：全页滚动时保持可见 */
.video-bg-fixed {
  position: fixed;
  top: 50%;
  left: 50%;
  min-width: 100%;
  min-height: 100%;
  width: auto;
  height: auto;
  transform: translate(-50%, -50%);
  object-fit: cover;
  z-index: -2;
}

.video-overlay-fixed {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: -1;
  pointer-events: none;
}

.hero-section {
  position: relative;
  width: 100%;
  height: 100vh;
}

.hero {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  gap: 0;
}

.hero-title {
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: 600;
  letter-spacing: 10px;
  color: #fff;
  text-shadow: 0 3px 20px rgba(0, 0, 0, 0.45);
}

.hero-sub {
  margin-top: 20px;
  font-size: clamp(0.9rem, 2vw, 1.15rem);
  font-weight: 300;
  letter-spacing: 3px;
  color: rgba(255, 255, 255, 0.75);
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.35);
  min-height: 1.8em;
}

.typed-text { display: inline; }

.cursor-blink {
  display: inline-block;
  margin-left: 2px;
  font-weight: 200;
  color: rgba(255, 255, 255, 0.8);
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 统计栏 */
.stats-bar {
  display: flex;
  align-items: center;
  gap: 0;
  margin-top: 40px;
  padding: 16px 32px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 0 24px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 0 12px rgba(125, 211, 252, 0.5);
  letter-spacing: 1px;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
  letter-spacing: 2px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.15);
}

/* 纯净模式：只显示背景（在 Home 内） */
.pure-mode .hero-section,
.pure-mode .scroll-hint,
.pure-mode .profile-section {
  opacity: 0;
  pointer-events: none;
  visibility: hidden;
}

.pure-mode-btn {
  position: fixed;
  bottom: 24px;
  left: 24px;
  z-index: 500;
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(0, 0, 0, 0.3);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}
.pure-mode-btn:hover {
  background: rgba(0, 0, 0, 0.5);
  border-color: rgba(255, 255, 255, 0.6);
}

[data-theme="day"] .stat-value {
  color: #fff;
  text-shadow: 0 0 12px rgba(14, 165, 233, 0.6);
}
</style>
