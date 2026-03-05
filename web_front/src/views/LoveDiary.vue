<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

const passwordInput = ref('')
const unlocked = ref(false)
const verifying = ref(false)
const config = ref({ startTime: null, name1: 'TA', name2: 'TA', avatar1: null, avatar2: null })
const loading = ref(true)
const pwdError = ref('')

// 新增功能状态
const activeTab = ref('overview') // overview, diary, photos, reminders
const diaries = ref([])
const newDiary = ref({ title: '', content: '', date: new Date().toISOString().split('T')[0] })
const editingDiary = ref(null)
const photos = ref([])
const reminders = ref([])
const newReminder = ref({ title: '', date: '', description: '' })
const emotionAnalysis = ref('')
const showDiaryModal = ref(false)
const showReminderModal = ref(false)
const showPhotoModal = ref(false)
const editingReminder = ref(null)
const newPhoto = ref({ description: '', takenDate: new Date().toISOString().split('T')[0] })
const selectedPhotoFile = ref(null)
const photoUploading = ref(false)

async function verifyPassword() {
  if (!passwordInput.value.trim()) {
    pwdError.value = '请输入密码'
    return
  }
  verifying.value = true
  pwdError.value = ''
  try {
    const res = await request.post('/site/love-diary/verify', { password: passwordInput.value })
    const ok = res?.data ?? res
    if (ok) {
      unlocked.value = true
      await loadData()
    } else {
      pwdError.value = '密码错误'
    }
  } catch {
    pwdError.value = '验证失败，请重试'
  } finally {
    verifying.value = false
  }
}

async function loadData() {
  try {
    // 加载日记
    const diaryRes = await request.get('/love/diaries')
    diaries.value = diaryRes?.data || []

    // 加载照片
    const photoRes = await request.get('/love/photos')
    photos.value = photoRes?.data || []

    // 加载提醒
    const reminderRes = await request.get('/love/reminders')
    reminders.value = reminderRes?.data || []
  } catch (e) {
    alert(e.message || '加载数据失败')
  }
}

async function saveDiary() {
  try {
    const diaryData = { ...newDiary.value }

    // 确保日期格式正确
    if (diaryData.date && typeof diaryData.date === 'string') {
      // 如果是 YYYY-MM-DD 格式，转换为完整的日期时间
      if (diaryData.date.length === 10) {
        diaryData.date = diaryData.date + 'T00:00:00'
      }
    }

    // 先进行情感分析
    try {
      const emotionRes = await request.post('/love/analyze-emotion', { content: diaryData.content })
      diaryData.emotionScore = emotionRes.data.score || 5
      diaryData.emotionAnalysis = emotionRes.data.analysis
    } catch (e) {
      console.warn('情感分析失败，使用默认分数')
      diaryData.emotionScore = 5
    }

    if (editingDiary.value) {
      await request.put(`/love/diaries/${editingDiary.value.id}`, diaryData)
      const index = diaries.value.findIndex(d => d.id === editingDiary.value.id)
      if (index !== -1) diaries.value[index] = { ...editingDiary.value, ...diaryData }
    } else {
      const res = await request.post('/love/diaries', diaryData)
      diaries.value.unshift(res.data)
    }
    closeDiaryModal()
  } catch (e) {
    alert(e.message || '保存日记失败')
  }
}

async function deleteDiary(id) {
  if (!confirm('确定要删除这条日记吗？')) return
  try {
    await request.delete(`/love/diaries/${id}`)
    diaries.value = diaries.value.filter(d => d.id !== id)
  } catch (e) {
    alert(e.message || '删除日记失败')
  }
}

async function uploadPhoto(event) {
  const file = event.target.files[0]
  if (!file) return

  selectedPhotoFile.value = file
  showPhotoModal.value = true
}

async function savePhoto() {
  if (!selectedPhotoFile.value) {
    alert('请先选择照片')
    return
  }

  const formData = new FormData()
  formData.append('photo', selectedPhotoFile.value)
  formData.append('description', newPhoto.value.description || '')
  formData.append('takenDate', newPhoto.value.takenDate ? new Date(newPhoto.value.takenDate).toISOString() : new Date().toISOString())

  photoUploading.value = true
  try {
    const res = await request.post('/love/photos', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    photos.value.unshift(res.data)
    closePhotoModal()
  } catch (e) {
    alert(e.message || '上传照片失败')
  } finally {
    photoUploading.value = false
  }
}

function closePhotoModal() {
  showPhotoModal.value = false
  selectedPhotoFile.value = null
  newPhoto.value = { description: '', takenDate: new Date().toISOString().split('T')[0] }
}

async function deletePhoto(id) {
  if (!confirm('确定要删除这张照片吗？')) return
  try {
    await request.delete(`/love/photos/${id}`)
    photos.value = photos.value.filter(p => p.id !== id)
  } catch (e) {
    alert(e.message || '删除照片失败')
  }
}

async function setFeaturedPhoto(id) {
  try {
    await request.put(`/love/photos/${id}/featured`)
    // 重新加载照片列表
    const photoRes = await request.get('/love/photos')
    photos.value = photoRes?.data || []
  } catch (e) {
    alert(e.message || '设置精选照片失败')
  }
}

async function editPhotoMetadata(photo) {
  const newDescription = prompt('请输入新的照片描述:', photo.description)
  if (newDescription !== null && newDescription !== photo.description) {
    try {
      await request.put(`/love/photos/${photo.id}/metadata`, { description: newDescription })
      photo.description = newDescription
    } catch (e) {
      alert(e.message || '编辑照片信息失败')
    }
  }
}

async function deleteReminder(id) {
  if (!confirm('确定要删除这个纪念日吗？')) return
  try {
    await request.delete(`/love/reminders/${id}`)
    reminders.value = reminders.value.filter(r => r.id !== id)
  } catch (e) {
    alert(e.message || '删除纪念日失败')
  }
}

async function editReminder(reminder) {
  editingReminder.value = reminder
  newReminder.value = { ...reminder }
  showReminderModal.value = true
}

async function updateReminder() {
  try {
    const reminderData = { ...newReminder.value }
    
    // 确保日期格式正确
    if (reminderData.date && typeof reminderData.date === 'string') {
      // 如果是 YYYY-MM-DD 格式，转换为完整的日期时间
      if (reminderData.date.length === 10) {
        reminderData.date = reminderData.date + 'T00:00:00'
      }
    }
    
    await request.put(`/love/reminders/${editingReminder.value.id}`, reminderData)
    const index = reminders.value.findIndex(r => r.id === editingReminder.value.id)
    if (index !== -1) reminders.value[index] = { ...editingReminder.value, ...reminderData }
    closeReminderModal()
  } catch (e) {
    alert(e.message || '更新纪念日失败')
  }
}

async function saveReminder() {
  try {
    const reminderData = { ...newReminder.value }
    
    // 确保日期格式正确
    if (reminderData.date && typeof reminderData.date === 'string') {
      // 如果是 YYYY-MM-DD 格式，转换为完整的日期时间
      if (reminderData.date.length === 10) {
        reminderData.date = reminderData.date + 'T00:00:00'
      }
    }
    
    const res = await request.post('/love/reminders', reminderData)
    reminders.value.push(res.data)
    closeReminderModal()
  } catch (e) {
    alert(e.message || '保存纪念日失败')
  }
}

async function analyzeEmotion(content) {
  try {
    const res = await request.post('/love/analyze-emotion', { content })
    emotionAnalysis.value = res.data.analysis
  } catch (e) {
    emotionAnalysis.value = '情感分析暂时不可用'
  }
}

function openDiaryModal(diary = null) {
  if (diary) {
    editingDiary.value = diary
    newDiary.value = { ...diary }
  } else {
    editingDiary.value = null
    newDiary.value = { title: '', content: '', date: new Date().toISOString().split('T')[0] }
  }
  showDiaryModal.value = true
}

function closeDiaryModal() {
  showDiaryModal.value = false
  editingDiary.value = null
  newDiary.value = { title: '', content: '', date: new Date().toISOString().split('T')[0] }
}

function openReminderModal(reminder = null) {
  if (reminder) {
    editingReminder.value = reminder
    newReminder.value = { ...reminder }
  } else {
    editingReminder.value = null
    newReminder.value = { title: '', date: '', description: '' }
  }
  showReminderModal.value = true
}

function closeReminderModal() {
  showReminderModal.value = false
  editingReminder.value = null
  newReminder.value = { title: '', date: '', description: '' }
}

const loveTime = computed(() => {
  const start = config.value.startTime
  if (!start) return { days: 0, hours: 0, minutes: 0, seconds: 0 }
  const s = new Date(start)
  const now = new Date()
  let diff = Math.floor((now - s) / 1000)
  const seconds = diff % 60
  diff = Math.floor(diff / 60)
  const minutes = diff % 60
  diff = Math.floor(diff / 60)
  const hours = diff % 24
  const days = Math.floor(diff / 24)
  return { days, hours, minutes, seconds }
})

const timeStr = computed(() => {
  const t = loveTime.value
  return `${t.days} 天 ${String(t.hours).padStart(2, '0')} : ${String(t.minutes).padStart(2, '0')} : ${String(t.seconds).padStart(2, '0')}`
})

const upcomingReminders = computed(() => {
  const now = new Date()
  return reminders.value
    .filter(r => new Date(r.date) > now)
    .sort((a, b) => new Date(a.date) - new Date(b.date))
    .slice(0, 3)
})

onMounted(async () => {
  try {
    const res = await request.get('/love/config')
    if (res && res.data) {
      config.value = res.data
    }
  } catch (e) {
    config.value.startTime = new Date().toISOString()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="love-diary">
    <!-- 浪漫背景 -->
    <div class="love-bg">
      <div class="hearts-bg" />
      <div class="flowers-bg" />
      <div class="floating-hearts" />
    </div>

    <!-- 密码验证 -->
    <div v-if="!unlocked" class="password-gate">
      <div class="gate-card">
        <h2>💕 恋爱日记 💕</h2>
        <p class="hint">请输入密码进入我们的甜蜜空间</p>
        <input
          v-model="passwordInput"
          type="password"
          placeholder="密码"
          class="password-input"
          @keyup.enter="verifyPassword"
        />
        <p v-if="pwdError" class="error">{{ pwdError }}</p>
        <button type="button" class="submit-btn" :disabled="verifying" @click="verifyPassword">
          <span>{{ verifying ? '验证中…' : '进入甜蜜世界 ✨' }}</span>
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading">
      <div class="loading-heart">💖</div>
      <p>正在加载我们的美好回忆...</p>
    </div>

    <!-- 主要内容 -->
    <div v-else class="love-content">
      <!-- 导航标签 -->
      <div class="nav-tabs">
        <button
          v-for="tab in [
            { key: 'overview', label: '🏠 首页', icon: '🏠' },
            { key: 'diary', label: '📖 日记', icon: '📖' },
            { key: 'photos', label: '📸 相册', icon: '📸' },
            { key: 'reminders', label: '🎉 纪念日', icon: '🎉' }
          ]"
          :key="tab.key"
          :class="['tab-btn', { active: activeTab === tab.key }]"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 首页概览 -->
      <div v-if="activeTab === 'overview'" class="overview-section">
        <!-- 恋爱时间 -->
        <div class="love-time-card">
          <div class="avatars-row">
            <div class="avatar-wrap">
              <img :src="config.avatar1 || '/vite.svg'" :alt="config.name1" class="avatar" @error="$event.target.src='/vite.svg'" />
              <span class="name">{{ config.name1 }}</span>
            </div>
            <div class="heartbeat-line">
              <svg viewBox="0 0 200 50" class="heart-svg" xmlns="http://www.w3.org/2000/svg">
                <defs>
                  <linearGradient id="heartGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stop-color="#ff6b9d" stop-opacity="0.6" />
                    <stop offset="50%" stop-color="#ff3d7f" />
                    <stop offset="100%" stop-color="#c44569" stop-opacity="0.6" />
                  </linearGradient>
                  <filter id="heartGlow">
                    <feGaussianBlur stdDeviation="1.5" result="coloredBlur" />
                    <feMerge>
                      <feMergeNode in="coloredBlur" />
                      <feMergeNode in="SourceGraphic" />
                    </feMerge>
                  </filter>
                </defs>
                <!-- 基础心电图波形 -->
                <path
                  class="ecg-base"
                  d="M0 25 L20 25 L28 25 L32 10 L36 35 L40 18 L44 30 L48 25 L80 25 L88 25 L92 10 L96 35 L100 18 L104 30 L108 25 L140 25 L148 25 L152 10 L156 35 L160 18 L164 30 L168 25 L200 25"
                  fill="none"
                  stroke="url(#heartGrad)"
                  stroke-width="2.5"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  filter="url(#heartGlow)"
                />
                <!-- 动画扫描线 -->
                <rect class="scanner" x="-20" y="0" width="20" height="50" fill="url(#scanGrad)" opacity="0.6">
                  <animateTransform attributeName="transform" type="translate" from="-20 0" to="220 0" dur="2.5s" repeatCount="indefinite" />
                </rect>
                <defs>
                  <linearGradient id="scanGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stop-color="#ff6b9d" stop-opacity="0" />
                    <stop offset="50%" stop-color="#ff3d7f" stop-opacity="0.5" />
                    <stop offset="100%" stop-color="#ff6b9d" stop-opacity="0" />
                  </linearGradient>
                </defs>
                <!-- 中心爱心 -->
                <text x="100" y="30" text-anchor="middle" font-size="14" fill="#ff6b9d" opacity="0.9" class="heart-pulse">💗</text>
              </svg>
            </div>
            <div class="avatar-wrap">
              <img :src="config.avatar2 || '/vite.svg'" :alt="config.name2" class="avatar" @error="$event.target.src='/vite.svg'" />
              <span class="name">{{ config.name2 }}</span>
            </div>
          </div>
          <div class="love-time-block">
            <p class="label">我们在一起已经</p>
            <p class="time-display">{{ timeStr }}</p>
          </div>
        </div>

        <!-- 最新动态 -->
        <div class="recent-activity">
          <h3>📝 最新日记</h3>
          <div v-if="diaries.length" class="recent-diary">
            <h4>{{ diaries[0].title }}</h4>
            <p>{{ diaries[0].content.substring(0, 100) }}...</p>
            <small>{{ new Date(diaries[0].date).toLocaleDateString() }}</small>
          </div>
          <p v-else class="no-data">还没有写日记哦，快去记录我们的美好时光吧！</p>
        </div>

        <!-- 即将到来的纪念日 -->
        <div class="upcoming-reminders">
          <h3>🎂 即将到来的纪念日</h3>
          <div v-if="upcomingReminders.length" class="reminder-list">
            <div v-for="reminder in upcomingReminders" :key="reminder.id" class="reminder-item">
              <div class="reminder-date">{{ new Date(reminder.date).toLocaleDateString() }}</div>
              <div class="reminder-title">{{ reminder.title }}</div>
            </div>
          </div>
          <p v-else class="no-data">还没有设置纪念日，添加一些重要的日子吧！</p>
        </div>
      </div>

      <!-- 日记页面 -->
      <div v-if="activeTab === 'diary'" class="diary-section">
        <div class="section-header">
          <h2>💕 我们的恋爱日记</h2>
          <button class="add-btn" @click="openDiaryModal()">✨ 写新日记</button>
        </div>

        <div class="diary-list">
          <div v-for="diary in diaries" :key="diary.id" class="diary-card">
            <div class="diary-header">
              <h3>{{ diary.title }}</h3>
              <small>{{ new Date(diary.date).toLocaleDateString() }}</small>
            </div>
            <p class="diary-content">{{ diary.content }}</p>
            <div v-if="diary.emotionScore" class="emotion-info">
              <span class="emotion-label">情感分析:</span>
              <span class="emotion-score">{{ diary.emotionScore }}/10</span>
            </div>
            <div class="diary-actions">
              <button @click="openDiaryModal(diary)">编辑</button>
              <button @click="deleteDiary(diary.id)" class="delete-btn">删除</button>
            </div>
          </div>
        </div>

        <!-- 日记编辑模态框 -->
        <div v-if="showDiaryModal" class="modal-overlay" @click="closeDiaryModal()">
          <div class="modal-content" @click.stop>
            <h3>{{ editingDiary ? '编辑日记' : '写新日记' }}</h3>
            <form @submit.prevent="saveDiary">
              <input v-model="newDiary.title" placeholder="日记标题" required />
              <input v-model="newDiary.date" type="date" required />
              <textarea v-model="newDiary.content" placeholder="写下我们的美好回忆..." rows="8" required></textarea>
              <div class="modal-actions">
                <button type="button" @click="closeDiaryModal()">取消</button>
                <button type="submit">{{ editingDiary ? '更新' : '保存' }}</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 相册页面 -->
      <div v-if="activeTab === 'photos'" class="photos-section">
        <div class="section-header">
          <h2>📸 我们的甜蜜相册</h2>
          <button class="upload-btn" @click="showPhotoModal = true">
            📷 上传照片
          </button>
        </div>

        <div class="photo-grid">
          <div v-for="photo in photos" :key="photo.id" class="photo-card">
            <img :src="photo.url" :alt="photo.description" />
            <div class="photo-overlay">
              <p>{{ photo.description }}</p>
              <div class="photo-actions">
                <button @click="editPhotoMetadata(photo)" class="photo-action-btn">✏️</button>
                <button @click="setFeaturedPhoto(photo.id)" class="photo-action-btn">⭐</button>
                <button @click="deletePhoto(photo.id)" class="photo-action-btn delete">🗑️</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 照片上传模态框 -->
        <div v-if="showPhotoModal" class="modal-overlay" @click="closePhotoModal()">
          <div class="modal-content" @click.stop>
            <h3>📷 上传照片</h3>
            <form @submit.prevent="savePhoto">
              <div class="photo-file-input-wrapper">
                <label for="photo-file" class="photo-file-label">
                  <span v-if="!selectedPhotoFile" class="file-placeholder">📁 点击选择照片或拖拽照片到此</span>
                  <span v-else class="file-name">✓ {{ selectedPhotoFile.name }}</span>
                </label>
                <input id="photo-file" type="file" accept="image/*" @change="(e) => selectedPhotoFile = e.target.files[0]" style="display: none;" required />
              </div>
              <input v-model="newPhoto.description" type="text" placeholder="照片描述（可选）" />
              <input v-model="newPhoto.takenDate" type="date" />
              <div class="modal-actions">
                <button type="button" @click="closePhotoModal()">取消</button>
                <button type="submit" :disabled="photoUploading || !selectedPhotoFile">
                  {{ photoUploading ? '上传中...' : '上传' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 纪念日页面 -->
      <div v-if="activeTab === 'reminders'" class="reminders-section">
        <div class="section-header">
          <h2>🎉 重要纪念日</h2>
          <button class="add-btn" @click="openReminderModal()">➕ 添加纪念日</button>
        </div>

        <div class="reminder-list">
          <div v-for="reminder in reminders" :key="reminder.id" class="reminder-card">
            <div class="reminder-date">{{ new Date(reminder.date).toLocaleDateString() }}</div>
            <h3>{{ reminder.title }}</h3>
            <p>{{ reminder.description }}</p>
            <div class="reminder-actions">
              <button @click="editReminder(reminder)" class="reminder-action-btn">编辑</button>
              <button @click="deleteReminder(reminder.id)" class="reminder-action-btn delete">删除</button>
            </div>
          </div>
        </div>

        <!-- 纪念日添加/编辑模态框 -->
        <div v-if="showReminderModal" class="modal-overlay" @click="closeReminderModal()">
          <div class="modal-content" @click.stop>
            <h3>{{ editingReminder ? '编辑纪念日' : '添加纪念日' }}</h3>
            <form @submit.prevent="editingReminder ? updateReminder() : saveReminder()">
              <input v-model="newReminder.title" placeholder="纪念日名称" required />
              <input v-model="newReminder.date" type="date" required />
              <textarea v-model="newReminder.description" placeholder="描述一下这个特别的日子..." rows="3"></textarea>
              <div class="modal-actions">
                <button type="button" @click="closeReminderModal()">取消</button>
                <button type="submit">{{ editingReminder ? '更新' : '保存' }}</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 情感分析提示 -->
      <div v-if="emotionAnalysis" class="emotion-tip">
        <h4>💭 情感小贴士</h4>
        <p>{{ emotionAnalysis }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dancing+Script:wght@400;700&family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

.love-diary {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  overflow: hidden;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.love-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 25%, #fecfef 50%, #a18cd1 75%, #fbc2eb 100%);
  z-index: 0;
}

.hearts-bg {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 30%, rgba(255, 107, 157, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(196, 69, 105, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(255, 182, 193, 0.1) 0%, transparent 50%);
}

.flowers-bg {
  position: absolute;
  inset: 0;
  background-image:
    url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.floating-hearts {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.floating-hearts::before,
.floating-hearts::after {
  content: '💕';
  position: absolute;
  font-size: 24px;
  animation: float 6s ease-in-out infinite;
  opacity: 0.6;
}

.floating-hearts::before {
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.floating-hearts::after {
  top: 60%;
  right: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

.password-gate {
  position: relative;
  z-index: 2;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.gate-card {
  padding: 40px 32px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  border: 2px solid rgba(255, 107, 157, 0.3);
  min-width: 320px;
  box-shadow: 0 8px 32px rgba(255, 107, 157, 0.2);
  backdrop-filter: blur(10px);
}

.gate-card h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #ff6b9d;
  margin-bottom: 8px;
  letter-spacing: 2px;
  text-align: center;
  font-family: 'Dancing Script', cursive;
}

.gate-card .hint {
  font-size: 14px;
  color: rgba(255, 107, 157, 0.8);
  margin-bottom: 20px;
  text-align: center;
}

.password-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  color: #333;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 107, 157, 0.3);
  border-radius: 12px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.password-input:focus {
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.2);
}

.gate-card .error {
  font-size: 13px;
  color: #e11d48;
  margin-bottom: 12px;
  text-align: center;
}

.gate-card .submit-btn {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  color: #fff;
  background: linear-gradient(135deg, #ff6b9d, #c44569);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
}

.gate-card .submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.5);
}

.loading {
  position: relative;
  z-index: 2;
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  text-align: center;
}

.loading-heart {
  font-size: 48px;
  animation: heartbeat 1.5s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.love-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 1200px;
  padding: 20px;
}

/* 导航标签 */
.nav-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 32px;
  justify-content: center;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.tab-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.tab-btn.active {
  background: linear-gradient(135deg, #ff6b9d, #c44569);
  color: #fff;
  border-color: rgba(255, 107, 157, 0.5);
}

/* 首页概览 */
.overview-section {
  display: grid;
  gap: 32px;
  grid-template-columns: 1fr;
}

.love-time-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(255, 107, 157, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.avatars-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 32px;
}

.avatar-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 107, 157, 0.5);
  box-shadow: 0 0 30px rgba(255, 107, 157, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

.avatar-wrap:last-child .avatar {
  animation-delay: 0.5s;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 30px rgba(255, 107, 157, 0.3); }
  50% { transform: scale(1.05); box-shadow: 0 0 40px rgba(255, 107, 157, 0.5); }
}

.name {
  font-size: 1.1rem;
  font-weight: 500;
  color: #ff6b9d;
  letter-spacing: 2px;
  font-family: 'Dancing Script', cursive;
}

.heartbeat-line {
  width: 160px;
  height: 50px;
  flex-shrink: 0;
  overflow: visible;
}

.heart-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.ecg-base {
  stroke-dasharray: 600;
  stroke-dashoffset: 600;
  animation: ecgDraw 2s ease-in-out infinite;
}

@keyframes ecgDraw {
  0% { stroke-dashoffset: 600; }
  70% { stroke-dashoffset: 0; }
  100% { stroke-dashoffset: 0; }
}

.heart-pulse {
  animation: heartPulse 1.2s ease-in-out infinite;
  transform-origin: 100px 25px;
}

@keyframes heartPulse {
  0%, 100% { transform: scale(1); opacity: 0.9; }
  30% { transform: scale(1.3); opacity: 1; }
  60% { transform: scale(0.95); }
}

.love-time-block {
  padding: 24px;
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.1), rgba(196, 69, 105, 0.1));
  border-radius: 16px;
  border: 1px solid rgba(255, 107, 157, 0.2);
}

.label {
  font-size: 1rem;
  color: rgba(255, 107, 157, 0.8);
  margin-bottom: 12px;
  font-family: 'Dancing Script', cursive;
}

.time-display {
  font-size: 1.8rem;
  font-weight: 600;
  color: #ff6b9d;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
  font-family: 'Dancing Script', cursive;
}

.recent-activity,
.upcoming-reminders {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.recent-activity h3,
.upcoming-reminders h3 {
  color: #ff6b9d;
  margin-bottom: 16px;
  font-family: 'Dancing Script', cursive;
  font-size: 1.3rem;
}

.recent-diary h4 {
  color: #c44569;
  margin-bottom: 8px;
  font-family: 'Dancing Script', cursive;
}

.recent-diary p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.recent-diary small {
  color: #999;
  font-size: 0.9rem;
}

.no-data {
  color: rgba(255, 107, 157, 0.6);
  font-style: italic;
  text-align: center;
  padding: 20px;
}

.reminder-list {
  display: grid;
  gap: 12px;
}

.reminder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.1), rgba(255, 192, 203, 0.1));
  border-radius: 8px;
  border: 1px solid rgba(255, 182, 193, 0.2);
}

.reminder-date {
  color: #ff6b9d;
  font-weight: 500;
  font-size: 0.9rem;
}

.reminder-title {
  color: #c44569;
  font-weight: 500;
}

/* 日记页面 */
.diary-section,
.photos-section,
.reminders-section {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(255, 107, 157, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-header h2 {
  color: #ff6b9d;
  font-family: 'Dancing Script', cursive;
  font-size: 1.8rem;
  margin: 0;
}

.add-btn,
.upload-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #ff6b9d, #c44569);
  color: #fff;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
}

.add-btn:hover,
.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.diary-list {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.diary-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 240, 245, 0.9));
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 107, 157, 0.2);
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.1);
}

.diary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.2);
}

.diary-header {
  margin-bottom: 16px;
}

.diary-header h3 {
  color: #c44569;
  margin-bottom: 8px;
  font-family: 'Dancing Script', cursive;
  font-size: 1.3rem;
}

.diary-header small {
  color: #999;
  font-size: 0.9rem;
}

.diary-content {
  color: #555;
  line-height: 1.6;
  margin-bottom: 16px;
}

.emotion-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.1), rgba(255, 192, 203, 0.1));
  border-radius: 8px;
  border: 1px solid rgba(255, 182, 193, 0.2);
}

.emotion-label {
  color: #ff6b9d;
  font-weight: 500;
  font-size: 0.9rem;
}

.emotion-score {
  color: #c44569;
  font-weight: 600;
  font-size: 0.9rem;
}

.diary-actions {
  display: flex;
  gap: 8px;
}

.diary-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.diary-actions button:first-child {
  background: rgba(255, 107, 157, 0.1);
  color: #ff6b9d;
}

.diary-actions button:first-child:hover {
  background: rgba(255, 107, 157, 0.2);
}

.diary-actions .delete-btn {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.diary-actions .delete-btn:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* 相册页面 */
.photo-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}

.photo-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 1;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.1);
}

.photo-card:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.2);
}

.photo-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 16px;
  color: #fff;
  font-size: 0.9rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.photo-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.photo-action-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  color: #fff;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.2s ease;
}

.photo-action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.photo-action-btn.delete {
  background: rgba(239, 68, 68, 0.3);
}

.photo-action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.5);
}

/* 纪念日页面 */
.reminder-list {
  display: grid;
  gap: 16px;
}

.reminder-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 240, 245, 0.9));
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255, 107, 157, 0.2);
  transition: all 0.3s ease;
}

.reminder-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.15);
}

.reminder-card .reminder-date {
  color: #ff6b9d;
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.reminder-card h3 {
  color: #c44569;
  margin-bottom: 8px;
  font-family: 'Dancing Script', cursive;
  font-size: 1.2rem;
}

.reminder-card p {
  color: #666;
  line-height: 1.5;
}

.reminder-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.reminder-action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  background: rgba(255, 107, 157, 0.1);
  color: #ff6b9d;
}

.reminder-action-btn:hover {
  background: rgba(255, 107, 157, 0.2);
}

.reminder-action-btn.delete {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.reminder-action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 32px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(255, 107, 157, 0.3);
  border: 2px solid rgba(255, 107, 157, 0.2);
}

.modal-content h3 {
  color: #ff6b9d;
  margin-bottom: 24px;
  text-align: center;
  font-family: 'Dancing Script', cursive;
  font-size: 1.5rem;
}

.modal-content input,
.modal-content textarea {
  width: 100%;
  padding: 12px 16px;
  margin-bottom: 16px;
  border: 2px solid rgba(255, 107, 157, 0.2);
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.modal-content input:focus,
.modal-content textarea:focus {
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
  outline: none;
}

.modal-content input:disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
}

.photo-file-input-wrapper {
  margin-bottom: 16px;
}

.photo-file-label {
  display: block;
  padding: 24px 16px;
  border: 2px dashed rgba(255, 107, 157, 0.3);
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.05), rgba(255, 192, 203, 0.05));
}

.photo-file-label:hover {
  border-color: #ff6b9d;
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.1), rgba(255, 192, 203, 0.1));
}

.file-placeholder {
  color: rgba(255, 107, 157, 0.6);
  font-size: 14px;
}

.file-name {
  color: #10b981;
  font-size: 14px;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.modal-actions button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modal-actions button:first-child {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.modal-actions button:first-child:hover {
  background: rgba(156, 163, 175, 0.2);
}

.modal-actions button:last-child {
  background: linear-gradient(135deg, #ff6b9d, #c44569);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
}

.modal-actions button:last-child:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

/* 情感分析提示 */
.emotion-tip {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.9), rgba(196, 69, 105, 0.9));
  color: #fff;
  padding: 16px 20px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.3);
  max-width: 300px;
  z-index: 100;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.emotion-tip h4 {
  margin-bottom: 8px;
  font-family: 'Dancing Script', cursive;
  font-size: 1.1rem;
}

.emotion-tip p {
  font-size: 0.9rem;
  line-height: 1.4;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .love-diary {
    padding: 16px;
  }

  .nav-tabs {
    flex-direction: column;
    align-items: stretch;
  }

  .tab-btn {
    padding: 12px 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
  }

  .diary-list {
    grid-template-columns: 1fr;
  }

  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .avatars-row {
    gap: 16px;
  }

  .avatar {
    width: 80px;
    height: 80px;
  }

  .modal-content {
    margin: 16px;
    padding: 24px;
  }
}
</style>
