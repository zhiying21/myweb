<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import VideoBackground from '@/components/VideoBackground.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const router = useRouter()
const { user, fetchUser, isLoggedIn } = useAuth()
const nickname = ref('')
const avatar = ref('')
const saving = ref(false)
const avatarFileInput = ref(null)
const tickets = ref([])
const activeTab = ref('profile')

const avatarPreview = computed(() => {
  const s = avatar.value
  return s && (s.startsWith('http') || s.startsWith('/')) ? s : ''
})

onMounted(() => {
  if (!isLoggedIn.value) {
    router.replace({ path: '/', query: { auth: '1' } })
    return
  }
  fetchUser().then((u) => {
    if (u) {
      nickname.value = u.nickname || ''
      avatar.value = u.avatar || ''
    }
  })
  fetchTickets()
})

async function fetchTickets() {
  try {
    const res = await request.get('/ticket/my')
    tickets.value = (res && res.data) ? res.data : []
  } catch {
    tickets.value = []
  }
}

function triggerAvatarFile() {
  avatarFileInput.value?.click()
}

async function onAvatarFileChange(e) {
  const file = e.target.files?.[0]
  if (!file?.type.startsWith('image/')) { alert('请选择图片文件'); return }
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await request.post('/auth/upload', formData)
    const url = res?.data ?? res
    if (url && typeof url === 'string') avatar.value = url
    else throw new Error('上传失败')
  } catch (err) {
    alert(err.message || '上传失败')
  }
  e.target.value = ''
}

async function save() {
  saving.value = true
  try {
    await request.put('/auth/profile', {
      nickname: nickname.value.trim(),
      avatar: avatar.value.trim() || null,
    })
    await fetchUser()
    alert('保存成功')
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function statusLabel(s) {
  return s === 'REPLIED' ? '已回复' : s === 'CLOSED' ? '已关闭' : '待处理'
}
function statusClass(s) {
  return s === 'REPLIED' ? 'status-replied' : s === 'CLOSED' ? 'status-closed' : 'status-pending'
}
function formatDate(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div v-if="isLoggedIn" class="profile-container">
      <div class="user-hero">
        <img v-if="avatarPreview" :src="avatarPreview" class="hero-avatar" alt="头像" />
        <div v-else class="hero-avatar-ph">{{ (user?.nickname || user?.email || '?').charAt(0) }}</div>
        <div class="hero-info">
          <h1>{{ user?.nickname || user?.email || '用户' }}</h1>
          <p class="hero-email">{{ user?.email }}</p>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="tabs glass-card">
        <button type="button" :class="{ active: activeTab === 'profile' }" @click="activeTab = 'profile'">个人资料</button>
        <button type="button" :class="{ active: activeTab === 'tickets' }" @click="activeTab = 'tickets'; fetchTickets()">
          我的工单 <span v-if="tickets.length" class="badge">{{ tickets.length }}</span>
        </button>
      </div>

      <!-- 个人资料 -->
      <div v-if="activeTab === 'profile'" class="profile-form glass-card">
        <div class="form-row">
          <label>昵称</label>
          <input v-model="nickname" type="text" placeholder="设置昵称" />
        </div>
        <div class="form-row">
          <label>头像</label>
          <div class="avatar-upload">
            <img v-if="avatarPreview" :src="avatarPreview" class="avatar-preview" alt="头像" />
            <input v-model="avatar" type="text" placeholder="头像链接或上传本地图片" />
            <div class="avatar-actions">
              <input ref="avatarFileInput" type="file" accept="image/*" class="hidden-input" @change="onAvatarFileChange" />
              <label class="btn-label" @click="triggerAvatarFile">选择本地图片</label>
            </div>
          </div>
        </div>
        <button type="button" class="save-btn" :disabled="saving" @click="save">
          {{ saving ? '保存中…' : '保存资料' }}
        </button>
      </div>

      <!-- 我的工单 -->
      <div v-else-if="activeTab === 'tickets'" class="tickets-section">
        <div v-for="t in tickets" :key="t.id" class="ticket-card glass-card">
          <div class="ticket-header">
            <span class="ticket-subject">{{ t.subject }}</span>
            <span class="ticket-status" :class="statusClass(t.status)">{{ statusLabel(t.status) }}</span>
          </div>
          <p class="ticket-content">{{ t.content }}</p>
          <span class="ticket-time">{{ formatDate(t.createTime) }}</span>

          <!-- 管理员回复 -->
          <div v-if="t.replies && t.replies.length" class="replies">
            <div class="replies-label">管理员回复：</div>
            <div v-for="r in t.replies" :key="r.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-admin">管理员</span>
                <span class="ticket-time">{{ formatDate(r.createTime) }}</span>
              </div>
              <p class="reply-content">{{ r.content }}</p>
            </div>
          </div>
        </div>
        <p v-if="!tickets.length" class="empty">暂无工单，前往"联系我"提交工单</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 60px;
  color: #fff;
}

.profile-container {
  max-width: 560px;
  margin: 0 auto;
}

.glass-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.user-hero {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.hero-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(125, 211, 252, 0.4);
}

.hero-avatar-ph {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(125, 211, 252, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  color: #7dd3fc;
  border: 2px solid rgba(125, 211, 252, 0.4);
}

.hero-info h1 {
  font-size: 1.4rem;
  margin: 0;
  color: #fff;
  letter-spacing: 2px;
}

.hero-email {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 4px;
}

.tabs {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  padding: 4px;
  overflow: hidden;
}

.tabs button {
  flex: 1;
  padding: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  background: none;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.tabs button.active {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #0ea5e9;
  color: #fff;
  font-size: 10px;
  margin-left: 4px;
}

.profile-form {
  padding: 24px;
}

.form-row {
  margin-bottom: 20px;
}

.form-row label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.form-row input[type="text"] {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  box-sizing: border-box;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.avatar-actions { display: flex; gap: 8px; }

.hidden-input {
  position: absolute;
  width: 0.1px;
  height: 0.1px;
  opacity: 0.01;
  overflow: hidden;
  z-index: -1;
}

.btn-label {
  display: inline-block;
  padding: 8px 16px;
  font-size: 13px;
  color: #fff;
  background: rgba(14, 165, 233, 0.5);
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(14, 165, 233, 0.6);
}
.btn-label:hover { background: rgba(14, 165, 233, 0.7); }

.save-btn {
  width: 100%;
  margin-top: 24px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.save-btn:disabled { opacity: 0.7; cursor: not-allowed; }
.save-btn:hover:not(:disabled) { background: #0284c7; }

/* 工单 */
.tickets-section { display: flex; flex-direction: column; gap: 16px; }

.ticket-card { padding: 20px; }

.ticket-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.ticket-subject {
  font-weight: 600;
  color: #7dd3fc;
  font-size: 15px;
}

.ticket-status {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 10px;
  flex-shrink: 0;
}
.status-pending { background: rgba(251, 191, 36, 0.15); color: #fbbf24; border: 1px solid rgba(251, 191, 36, 0.3); }
.status-replied { background: rgba(52, 211, 153, 0.15); color: #34d399; border: 1px solid rgba(52, 211, 153, 0.3); }
.status-closed { background: rgba(156, 163, 175, 0.15); color: #9ca3af; border: 1px solid rgba(156, 163, 175, 0.3); }

.ticket-content {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 8px;
}

.ticket-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.replies {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.replies-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 8px;
}

.reply-item {
  background: rgba(52, 211, 153, 0.06);
  border-left: 3px solid rgba(52, 211, 153, 0.4);
  padding: 10px 14px;
  border-radius: 0 8px 8px 0;
  margin-top: 8px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.reply-admin {
  font-size: 13px;
  font-weight: 500;
  color: #34d399;
}

.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
}

.empty {
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 14px;
  padding: 40px 0;
}
</style>
