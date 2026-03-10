<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { Chart, registerables } from 'chart.js'
import request from '@/utils/request'

const router = useRouter()

const techTags = [
  { label: 'HTML/CSS', color: '#e34f26' },
  { label: 'Js/ts', color: '#f7df1e' },
  { label: 'python', color: '#3776ab' },
  { label: 'java', color: '#ed8b00' },
  { label: 'next.js', color: '#0ea5e9' },
  { label: 'React', color: '#61dafb' },
  { label: 'Vue', color: '#38bdf8' },
  { label: 'Qt', color: '#41cd52' },
  { label: 'Shell', color: '#89e051' },
]

Chart.register(...registerables)
gsap.registerPlugin(ScrollTrigger)

const introText = '🌸 欢迎来到枝莺的个人网站，可以在此处驻足小憩 🌿 或分享或查看大家的知识、经历、博客、资源 🍀 祝你天天开心 🌺'
const displayed = ref('')
let typeTimer = null
let charIndex = 0
let isDeleting = false

const visitCount = ref(0)
const runningHours = ref(0)
const chartRef = ref(null)
let activityChart = null

const notesCount = ref(0)
const blogCount = ref(0)
const allDocuments = ref([])
const githubActivity = ref([])
const githubLabels = ref([])

const PAGE_SIZE = 4
const currentPage = ref(1)
const totalPages = computed(() => Math.ceil(allDocuments.value.length / PAGE_SIZE))
const paginatedDocs = computed(() => {
  const start = (currentPage.value - 1) * PAGE_SIZE
  return allDocuments.value.slice(start, start + PAGE_SIZE)
})

const selectedDoc = ref(null)

function selectDoc(doc) {
  selectedDoc.value = doc
}

function goToDoc(doc) {
  const route = doc.type === 'notes' ? '/notes' : '/blog'
  router.push({ path: route, query: { id: doc.id } })
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

watch(currentPage, () => {
  const docs = paginatedDocs.value
  if (docs.length && (!selectedDoc.value || !docs.find((d) => d.id === selectedDoc.value.id))) {
    selectDoc(docs[0])
  }
})

function typeTick() {
  if (!isDeleting) {
    displayed.value = introText.slice(0, charIndex + 1)
    charIndex++
    if (charIndex >= introText.length) {
      isDeleting = true
      typeTimer = setTimeout(typeTick, 2500)
      return
    }
    typeTimer = setTimeout(typeTick, 120)
  } else {
    displayed.value = introText.slice(0, charIndex)
    charIndex--
    if (charIndex < 0) {
      isDeleting = false
      charIndex = 0
      typeTimer = setTimeout(typeTick, 800)
      return
    }
    typeTimer = setTimeout(typeTick, 60)
  }
}

function initScrollAnimations() {
  gsap.utils.toArray('.profile-block').forEach((el) => {
    gsap.fromTo(el, { opacity: 0, y: 32 }, {
      opacity: 1,
      y: 0,
      duration: 0.4,
      ease: 'power2.out',
      scrollTrigger: {
        trigger: el,
        start: 'top 88%',
        toggleActions: 'play none none reverse',
      },
    })
  })
  gsap.fromTo('.left-panels .panel', { opacity: 0, x: -30 }, {
    opacity: 1,
    x: 0,
    duration: 0.45,
    stagger: 0.1,
    ease: 'power2.out',
    scrollTrigger: {
      trigger: '.merged-section',
      start: 'top 85%',
      toggleActions: 'play none none reverse',
    },
  })
  gsap.fromTo('.doc-panel', { opacity: 0, x: 40 }, {
    opacity: 1,
    x: 0,
    duration: 0.5,
    ease: 'power2.out',
    scrollTrigger: {
      trigger: '.merged-section',
      start: 'top 85%',
      toggleActions: 'play none none reverse',
    },
  })
}

function initChart() {
  if (!chartRef.value) return
  if (activityChart) activityChart.destroy()
  const ctx = chartRef.value.getContext('2d')
  activityChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: githubLabels.value,
      datasets: [{
        label: 'GitHub 活跃度',
        data: githubActivity.value,
        borderColor: '#22c55e',
        backgroundColor: 'rgba(34, 197, 94, 0.2)',
        borderWidth: 2,
        fill: true,
        tension: 0.35,
        pointRadius: 2,
        pointHoverRadius: 4,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: {
          ticks: { color: 'rgba(120, 120, 120, 0.8)' },
          grid: { display: false },
        },
        y: {
          beginAtZero: true,
          ticks: { precision: 0, color: 'rgba(120, 120, 120, 0.8)' },
          grid: { color: 'rgba(120, 120, 120, 0.15)' },
        },
      },
    },
  })
}

async function fetchSiteStats() {
  try {
    await request.post('/site/visit')
    const res = await request.get('/site/stats')
    const d = res?.data ?? res
    runningHours.value = d?.runningHours ?? 0
    visitCount.value = d?.visitCount ?? 0
    blogCount.value = d?.blogCount ?? 0
    notesCount.value = d?.noteCount ?? 0
  } catch {
    runningHours.value = 0
    visitCount.value = 0
    blogCount.value = 0
    notesCount.value = 0
  }
}

async function fetchDocs() {
  try {
    const [notesRes, blogRes] = await Promise.all([
      request.get('/document/list', { params: { type: 'notes' } }),
      request.get('/document/list', { params: { type: 'blog' } }),
    ])
    const notes = (notesRes && notesRes.data) ? notesRes.data : []
    const blogs = (blogRes && blogRes.data) ? blogRes.data : []
    allDocuments.value = [
      ...notes.map((d) => ({ ...d, type: 'notes', path: d.id, image: d.coverImage })),
      ...blogs.map((d) => ({ ...d, type: 'blog', path: d.id, image: d.coverImage })),
    ]
  } catch {
    allDocuments.value = []
    notesCount.value = 0
    blogCount.value = 0
  }
}

function formatActivityDate(date) {
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${month}/${day}`
}

function buildFallbackActivity() {
  const labels = []
  const values = []
  const today = new Date()
  for (let i = 13; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(today.getDate() - i)
    labels.push(formatActivityDate(d))
    values.push((i % 5) + 1)
  }
  githubLabels.value = labels
  githubActivity.value = values
}

async function fetchGithubActivity() {
  try {
    const resp = await fetch('https://api.github.com/users/zhiying21/events/public')
    if (!resp.ok) throw new Error('GitHub API error')
    const events = await resp.json()
    const countMap = {}
    for (const ev of events) {
      const key = (ev.created_at || '').slice(0, 10)
      if (!key) continue
      countMap[key] = (countMap[key] || 0) + 1
    }
    const labels = []
    const values = []
    const today = new Date()
    for (let i = 13; i >= 0; i--) {
      const d = new Date(today)
      d.setDate(today.getDate() - i)
      const key = d.toISOString().slice(0, 10)
      labels.push(formatActivityDate(d))
      values.push(countMap[key] || 0)
    }
    githubLabels.value = labels
    githubActivity.value = values
  } catch {
    buildFallbackActivity()
  }
}

onMounted(async () => {
  await fetchSiteStats()
  await fetchDocs()
  await fetchGithubActivity()
  typeTimer = setTimeout(typeTick, 400)
  if (paginatedDocs.value.length) selectDoc(paginatedDocs.value[0])
  nextTick(() => {
    initScrollAnimations()
    initChart()
  })
})

onUnmounted(() => {
  clearTimeout(typeTimer)
  if (activityChart) activityChart.destroy()
})
</script>

<template>
  <section class="profile-section">
    <div class="intro-fullscreen profile-block scroll-section">
      <div class="intro-content intro-float">
        <p class="intro-kaomoji">(｡・ω・｡)ノ</p>
        <p class="intro-text">
          <span class="typed-text">{{ displayed }}</span>
          <span class="cursor-blink">|</span>
        </p>
        <p class="intro-kaomoji-end">٩(◕‿◕｡)۶</p>
      </div>
    </div>

    <div class="merged-section scroll-section">
      <div class="merged-layout">
        <aside class="left-panels">
          <div class="panel info-block">
            <div class="avatar-wrap">
              <img src="/cc.jpg" alt="头像" class="avatar" @error="$event.target.src='/vite.svg'" />
            </div>
            <div class="info-text">
              <p class="info-id">ID: zhiying21</p>
              <p class="info-en">Full Stack Developer</p>
              <div class="social-links">
                <a href="https://github.com" target="_blank" rel="noopener noreferrer" class="social-link" data-cursor-hover title="GitHub">
                  <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/></svg>
                </a>
                <a href="https://gitee.com" target="_blank" rel="noopener noreferrer" class="social-link" data-cursor-hover title="Gitee">
                  <svg viewBox="0 0 24 24" fill="currentColor"><path d="M11.984 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.016 0zm6.048 14.448c-.2.2-.5.2-.7 0l-1.4-1.4-.1-.1c-.2-.2-.2-.5 0-.7l.1-.1 1.4-1.4c.2-.2.5-.2.7 0l.1.1 1.4 1.4c.2.2.2.5 0 .7l-.1.1-1.4 1.4c-.2.2-.5.2-.7 0l-.1-.1zM12 2.4c-.6 0-1.2.4-1.4 1l-1.4 4.2c-.2.6-.2 1.2 0 1.8l1.4 4.2c.2.6.8 1 1.4 1 .6 0 1.2-.4 1.4-1l1.4-4.2c.2-.6.2-1.2 0-1.8L13.4 3.4c-.2-.6-.8-1-1.4-1z"/></svg>
                </a>
              </div>
            </div>
          </div>

          <div class="panel stats-block">
            <h3 class="block-title">网站数据统计</h3>
            <div class="stats-grid">
              <div class="stat-item">
                <span class="stat-value">{{ runningHours }}</span>
                <span class="stat-label">运行(小时)</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ notesCount }}</span>
                <span class="stat-label">笔记数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ blogCount }}</span>
                <span class="stat-label">博客数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ visitCount }}</span>
                <span class="stat-label">访问量</span>
              </div>
            </div>
            <div class="chart-wrap">
              <canvas ref="chartRef" width="200" height="140"></canvas>
            </div>
            <div class="tech-tags">
              <span v-for="t in techTags" :key="t.label" class="tech-tag" :style="{ backgroundColor: t.color + '33', color: t.color }">&lt;{{ t.label }}&gt;</span>
            </div>
          </div>
        </aside>

        <div class="doc-panel">
          <div class="doc-cards-list">
            <div
              v-for="doc in paginatedDocs"
              :key="doc.path"
              class="doc-card"
              :class="{ active: selectedDoc?.id === doc.id }"
              @click="goToDoc(doc)"
            >
              <div class="doc-card-media">
                <div class="doc-card-img">
                  <img :src="doc.publisherAvatar || doc.image || '/vite.svg'" :alt="doc.title" @error="$event.target.src='/vite.svg'" />
                </div>
                <p class="doc-card-author">{{ doc.publisherNickname || '匿名' }}</p>
              </div>
              <div class="doc-card-body">
                <h4 class="doc-card-title">{{ doc.title }}</h4>
                <p class="doc-card-desc">{{ doc.description }}</p>
                <button type="button" class="doc-card-btn" @click.stop="goToDoc(doc)">阅读全文 →</button>
              </div>
            </div>
          </div>
          <div v-if="totalPages > 1" class="doc-pagination">
            <button type="button" class="page-btn" :disabled="currentPage <= 1" @click="prevPage">上一页</button>
            <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
            <button type="button" class="page-btn" :disabled="currentPage >= totalPages" @click="nextPage">下一页</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profile-section {
  min-height: 100vh;
  padding: 60px 20px 80px;
}

/* 开灯模式字体颜色 - 绿色系 */
[data-theme="day"] .intro-text,
[data-theme="day"] .intro-kaomoji,
[data-theme="day"] .intro-kaomoji-end,
[data-theme="day"] .info-id,
[data-theme="day"] .info-en,
[data-theme="day"] .block-title,
[data-theme="day"] .stat-value,
[data-theme="day"] .stat-label,
[data-theme="day"] .doc-card-title,
[data-theme="day"] .doc-card-desc,
[data-theme="day"] .doc-card-btn,
[data-theme="day"] .page-btn,
[data-theme="day"] .page-info,
[data-theme="day"] .social-link {
  color: #059669 !important;
}

/* 关灯模式字体颜色 - 蓝色系 */
[data-theme="night"] .intro-text,
[data-theme="night"] .intro-kaomoji,
[data-theme="night"] .intro-kaomoji-end,
[data-theme="night"] .info-id,
[data-theme="night"] .info-en,
[data-theme="night"] .block-title,
[data-theme="night"] .stat-value,
[data-theme="night"] .stat-label,
[data-theme="night"] .doc-card-title,
[data-theme="night"] .doc-card-desc,
[data-theme="night"] .doc-card-btn,
[data-theme="night"] .page-btn,
[data-theme="night"] .page-info,
[data-theme="night"] .social-link {
  color: #7dd3fc !important;
}

.intro-fullscreen {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px 80px;
  background: transparent !important;
}

.intro-content {
  text-align: center;
  max-width: 540px;
}

.intro-float {
  animation: float 5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.intro-kaomoji { font-size: clamp(1.2rem, 3vw, 1.6rem); margin-bottom: 16px; opacity: 0.9; }
.intro-kaomoji-end { font-size: clamp(1.2rem, 3vw, 1.6rem); margin-top: 20px; opacity: 0.9; }
.intro-text { font-size: clamp(1rem, 2.5vw, 1.2rem); line-height: 2; letter-spacing: 1px; min-height: 2.8em; }

.cursor-blink { animation: blink 1s step-end infinite; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

.scroll-section {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.merged-section {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 48px 28px;
}

.merged-layout {
  display: grid;
  grid-template-columns: minmax(300px, 360px) minmax(420px, 1fr);
  gap: 36px;
  align-items: stretch;
}

.left-panels {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel {
  padding: 28px 24px;
  background: linear-gradient(160deg, rgba(167, 243, 208, 0.35) 0%, rgba(255, 255, 255, 0.25) 50%, rgba(209, 250, 229, 0.3) 100%);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 8px 32px rgba(134, 239, 172, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

[data-theme="night"] .panel {
  background: linear-gradient(160deg, rgba(6, 95, 70, 0.4) 0%, rgba(20, 83, 45, 0.35) 50%, rgba(5, 46, 22, 0.4) 100%);
  border-color: rgba(167, 243, 208, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), inset 0 1px 0 rgba(167, 243, 208, 0.08);
}

.info-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-wrap {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  padding: 3px;
  background: linear-gradient(145deg, rgba(167, 243, 208, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%);
  border: 2px solid rgba(255, 255, 255, 0.7);
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.info-id { font-size: 15px; font-weight: 500; }
.info-en { font-size: 13px; margin-top: 6px; opacity: 0.9; }

.social-links { display: flex; gap: 16px; margin-top: 12px; }
.social-link {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.35s ease;
}
.social-link:hover { transform: scale(1.1); }
.social-link svg { width: 22px; height: 22px; }

.block-title {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  margin-bottom: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 18px;
}

.stat-item {
  padding: 16px 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.6);
  transition: transform 0.3s ease;
}
.stat-item:hover { transform: translateY(-2px); }
[data-theme="night"] .stat-item {
  background: rgba(0, 0, 0, 0.25);
  border-color: rgba(167, 243, 208, 0.15);
}

.stat-value { display: block; font-size: 1.4rem; font-weight: 600; }
.stat-label { font-size: 13px; margin-top: 4px; letter-spacing: 1px; }

.chart-wrap {
  height: 140px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 12px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.5);
}
[data-theme="night"] .chart-wrap {
  background: rgba(0, 0, 0, 0.2);
  border-color: rgba(167, 243, 208, 0.1);
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  justify-content: center;
}
.tech-tag {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 6px;
  font-weight: 500;
}

.doc-panel {
  padding: 32px;
  background: linear-gradient(160deg, rgba(167, 243, 208, 0.2) 0%, rgba(255, 255, 255, 0.15) 50%, rgba(209, 250, 229, 0.18) 100%);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 32px rgba(134, 239, 172, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

[data-theme="night"] .doc-panel {
  background: linear-gradient(160deg, rgba(6, 95, 70, 0.35) 0%, rgba(20, 83, 45, 0.3) 50%, rgba(5, 46, 22, 0.35) 100%);
  border-color: rgba(167, 243, 208, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}

.doc-cards-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.doc-card {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}
.doc-card:hover {
  background: rgba(255, 255, 255, 0.6);
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(5, 150, 105, 0.15);
}
.doc-card.active {
  background: rgba(167, 243, 208, 0.35);
  border-color: rgba(5, 150, 105, 0.5);
}
[data-theme="night"] .doc-card {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(167, 243, 208, 0.15);
}
[data-theme="night"] .doc-card:hover { background: rgba(0, 0, 0, 0.45); }
[data-theme="night"] .doc-card.active {
  background: rgba(6, 95, 70, 0.5);
  border-color: rgba(110, 231, 183, 0.3);
}

.doc-card-img {
  flex-shrink: 0;
  flex: 1;
  min-width: 0;
  max-width: 180px;
  aspect-ratio: 16/10;
  border-radius: 12px;
  overflow: hidden;
}
.doc-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doc-card-media {
  flex-shrink: 0;
  flex: 1;
  min-width: 0;
  max-width: 180px;
}

.doc-card-author {
  margin-top: 8px;
  font-size: 12px;
  text-align: center;
  opacity: 0.85;
}

.doc-card-body { flex: 2; min-width: 0; }
.doc-card-title { font-size: 17px; font-weight: 600; margin-bottom: 8px; }
.doc-card-desc {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.doc-card-btn {
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid rgba(5, 150, 105, 0.5);
  background: rgba(167, 243, 208, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}
.doc-card-btn:hover {
  background: rgba(167, 243, 208, 0.5);
}
[data-theme="night"] .doc-card-btn {
  border-color: rgba(110, 231, 183, 0.4);
  background: rgba(6, 95, 70, 0.4);
}

.doc-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}
.page-btn {
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 8px;
  border: 1px solid rgba(5, 150, 105, 0.4);
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}
.page-btn:hover:not(:disabled) {
  background: rgba(167, 243, 208, 0.4);
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
[data-theme="night"] .page-btn {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(110, 231, 183, 0.3);
}
.page-info { font-size: 13px; }

@media (max-width: 900px) {
  .merged-layout {
    grid-template-columns: 1fr;
  }
  .left-panels { order: 2; }
  .doc-panel { order: 1; }
}

@media (max-width: 600px) {
  .merged-section { padding: 24px 16px; }
  .stats-grid { grid-template-columns: 1fr; }
  .doc-panel { padding: 20px; }
  .doc-card-img { max-width: 120px; }
}
</style>