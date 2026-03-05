<script setup>
import { ref, computed, onMounted } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { user } = useAuth()
const tickets = ref([])
const resources = ref([])
const loveConfig = ref({ startTime: '', name1: '', name2: '', avatar1: '', avatar2: '' })
const tab = ref('tickets')
const saving = ref(false)
const newResource = ref({ name: '', icon: '', link: '' })
const avatarFile1 = ref(null)
const avatarFile2 = ref(null)
const resourceIconFile = ref(null)
const replyingTicketId = ref(null)
const replyContent = ref('')
const resumeContent = ref('')
const resumeSaving = ref(false)

const avatarPreview1 = computed(() => {
  const s = loveConfig.value.avatar1
  return s && (s.startsWith('http') || s.startsWith('/')) ? s : ''
})
const avatarPreview2 = computed(() => {
  const s = loveConfig.value.avatar2
  return s && (s.startsWith('http') || s.startsWith('/')) ? s : ''
})

async function fetchTickets() {
  try {
    const res = await request.get('/admin/tickets')
    tickets.value = (res && res.data) ? res.data : []
  } catch (e) {
    tickets.value = []
    if (e.response?.status === 403) alert('需要管理员权限')
  }
}

async function replyTicket(ticketId) {
  if (!replyContent.value.trim()) { alert('回复内容不能为空'); return }
  try {
    await request.post(`/admin/tickets/${ticketId}/reply`, { content: replyContent.value.trim() })
    replyContent.value = ''
    replyingTicketId.value = null
    await fetchTickets()
    alert('回复成功')
  } catch (e) {
    alert(e.message || '回复失败')
  }
}

async function fetchResources() {
  try {
    const res = await request.get('/admin/resources')
    resources.value = (res && res.data) ? res.data : []
  } catch {
    try {
      const res = await request.get('/resource/list')
      resources.value = (res && res.data) ? res.data : []
    } catch {
      resources.value = []
    }
  }
}

async function addResource() {
  if (!newResource.value.name?.trim() || !newResource.value.link?.trim()) {
    alert('名称和链接不能为空')
    return
  }
  try {
    await request.post('/admin/resource', newResource.value)
    newResource.value = { name: '', icon: '', link: '' }
    await fetchResources()
  } catch (e) {
    alert(e.message || '添加失败')
  }
}

async function deleteResource(id) {
  if (!confirm('确定删除这个资源？')) return
  try {
    await request.delete(`/admin/resource/${id}`)
    resources.value = resources.value.filter(r => r.id !== id)
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

function triggerResourceIcon() {
  resourceIconFile.value?.click()
}

async function uploadResourceIcon() {
  const input = resourceIconFile.value
  if (!input?.files?.length) return
  const file = input.files[0]
  const isImage = (file.type || '').startsWith('image/') || /\.(jpe?g|png|gif|webp|bmp|svg)$/i.test(file.name || '')
  if (!isImage) { alert('请选择图片文件'); return }
  const formData = new FormData()
  formData.append('file', file)
  input.value = ''
  try {
    const res = await request.post('/admin/upload', formData, { timeout: 30000 })
    const url = res?.data ?? res
    if (!url) throw new Error('上传失败')
    newResource.value.icon = url
  } catch (e) {
    alert(e.message || '上传失败')
  }
}

async function fetchLoveConfig() {
  try {
    const res = await request.get('/love/config')
    if (res && res.data) {
      loveConfig.value = {
        startTime: res.data.startTime ? res.data.startTime.slice(0, 16) : '',
        name1: res.data.name1 || '',
        name2: res.data.name2 || '',
        avatar1: res.data.avatar1 || '',
        avatar2: res.data.avatar2 || '',
      }
    }
  } catch {}
}

async function saveLoveConfig() {
  saving.value = true
  try {
    await request.post('/love/config', {
      startTime: loveConfig.value.startTime || new Date().toISOString().slice(0, 19),
      name1: loveConfig.value.name1 || 'TA',
      name2: loveConfig.value.name2 || 'TA',
      avatar1: loveConfig.value.avatar1 || null,
      avatar2: loveConfig.value.avatar2 || null,
    })
    alert('保存成功')
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function uploadAvatar(which) {
  const input = which === 1 ? avatarFile1.value : avatarFile2.value
  if (!input?.files?.length) return
  const file = input.files[0]
  const isImage = (file.type || '').startsWith('image/')
  if (!isImage) { alert('请选择图片文件'); return }
  const formData = new FormData()
  formData.append('file', file)
  input.value = ''
  try {
    const res = await request.post('/admin/upload', formData, { timeout: 30000 })
    const url = res?.data ?? res
    if (!url) throw new Error('上传失败')
    if (which === 1) loveConfig.value.avatar1 = url
    else loveConfig.value.avatar2 = url
  } catch (e) {
    alert(e.message || '上传失败')
  }
}

async function fetchResume() {
  try {
    const res = await request.get('/admin/resume')
    resumeContent.value = (res && res.data) ? res.data : ''
  } catch {}
}

async function saveResume() {
  if (!resumeContent.value.trim()) { alert('内容不能为空'); return }
  resumeSaving.value = true
  try {
    await request.put('/admin/resume', { content: resumeContent.value })
    alert('简历保存成功')
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    resumeSaving.value = false
  }
}

function statusLabel(s) {
  return s === 'REPLIED' ? '已回复' : s === 'CLOSED' ? '已关闭' : '待处理'
}

function formatDate(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  fetchTickets()
  fetchResources()
  fetchLoveConfig()
  fetchResume()
})
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="admin-container">
      <h1>管理后台</h1>
      <div class="tabs glass-card">
        <button type="button" :class="{ active: tab === 'tickets' }" @click="tab = 'tickets'; fetchTickets()">工单管理</button>
        <button type="button" :class="{ active: tab === 'resources' }" @click="tab = 'resources'; fetchResources()">资源管理</button>
        <button type="button" :class="{ active: tab === 'resume' }" @click="tab = 'resume'; fetchResume()">简历管理</button>
        <button type="button" :class="{ active: tab === 'love' }" @click="tab = 'love'">恋爱日记配置</button>
      </div>

      <!-- 工单管理 -->
      <div v-if="tab === 'tickets'" class="section">
        <div class="section-header">
          <span class="count-badge">共 {{ tickets.length }} 条</span>
          <button type="button" class="refresh-btn" @click="fetchTickets">刷新</button>
        </div>
        <div v-for="t in tickets" :key="t.id" class="ticket-card glass-card">
          <div class="ticket-header">
            <span class="email">{{ t.email }}</span>
            <span class="status" :class="t.status === 'REPLIED' ? 'replied' : t.status === 'CLOSED' ? 'closed' : 'pending'">
              {{ statusLabel(t.status) }}
            </span>
            <span class="time">{{ formatDate(t.createTime) }}</span>
          </div>
          <h3>{{ t.subject }}</h3>
          <p class="ticket-content">{{ t.content }}</p>

          <!-- 已有回复 -->
          <div v-if="t.replies && t.replies.length" class="existing-replies">
            <div v-for="r in t.replies" :key="r.id" class="reply-bubble">
              <span class="reply-admin">{{ r.adminNickname || '管理员' }}</span>
              <p>{{ r.content }}</p>
              <span class="time">{{ formatDate(r.createTime) }}</span>
            </div>
          </div>

          <!-- 回复框 -->
          <div v-if="replyingTicketId === t.id" class="reply-form">
            <textarea v-model="replyContent" placeholder="输入回复内容…" rows="3" />
            <div class="reply-actions">
              <button type="button" class="btn-primary" @click="replyTicket(t.id)">发送回复</button>
              <button type="button" class="btn-ghost" @click="replyingTicketId = null">取消</button>
            </div>
          </div>
          <button v-else type="button" class="reply-btn" @click="replyingTicketId = t.id; replyContent = ''">
            {{ t.replies?.length ? '继续回复' : '回复工单' }}
          </button>
        </div>
        <p v-if="!tickets.length" class="empty">暂无工单</p>
      </div>

      <!-- 资源管理 -->
      <div v-else-if="tab === 'resources'" class="section">
        <div class="add-resource glass-card">
          <h3>添加新资源</h3>
          <div class="form-row">
            <input v-model="newResource.name" placeholder="软件名称 *" />
          </div>
          <div class="form-row icon-row">
            <input v-model="newResource.icon" placeholder="图标 URL 或选择本地图片" />
            <input ref="resourceIconFile" type="file" accept="image/*" class="hidden-input" @change="uploadResourceIcon" />
            <button type="button" class="btn-ghost" @click.prevent="triggerResourceIcon()">选择图片</button>
          </div>
          <div class="form-row">
            <input v-model="newResource.link" placeholder="下载链接 *" />
          </div>
          <button type="button" class="btn-primary" @click="addResource">添加资源</button>
        </div>

        <div class="resource-list">
          <div v-for="r in resources" :key="r.id" class="resource-item glass-card">
            <img v-if="r.icon" :src="r.icon" alt="" class="resource-icon" @error="$event.target.style.display='none'" />
            <div v-else class="resource-icon-ph">{{ r.name?.charAt(0) || '?' }}</div>
            <div class="resource-info">
              <span class="resource-name">{{ r.name }}</span>
              <a :href="r.link" target="_blank" rel="noopener noreferrer" class="resource-link">{{ r.link }}</a>
            </div>
            <button type="button" class="delete-sm" @click="deleteResource(r.id)">删除</button>
          </div>
          <p v-if="!resources.length" class="empty">暂无资源</p>
        </div>
      </div>

      <!-- 简历管理 -->
      <div v-else-if="tab === 'resume'" class="section">
        <div class="resume-editor glass-card">
          <h3>编辑简历（Markdown）</h3>
          <p class="hint-text">支持 Markdown 格式，保存后前端简历页面将实时更新</p>
          <textarea v-model="resumeContent" class="resume-textarea" placeholder="在此输入 Markdown 格式的简历内容…" rows="24" />
          <div class="resume-actions">
            <button type="button" class="btn-primary" :disabled="resumeSaving" @click="saveResume">
              {{ resumeSaving ? '保存中…' : '保存简历' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 恋爱日记配置 -->
      <div v-else class="section">
        <div class="love-config glass-card">
          <h3>恋爱日记配置</h3>
          <div class="form-row">
            <label>开始时间</label>
            <input v-model="loveConfig.startTime" type="datetime-local" />
          </div>
          <div class="form-row">
            <label>姓名1（左边）</label>
            <input v-model="loveConfig.name1" type="text" placeholder="TA" />
          </div>
          <div class="form-row">
            <label>姓名2（右边）</label>
            <input v-model="loveConfig.name2" type="text" placeholder="TA" />
          </div>
          <div class="form-row">
            <label>头像1</label>
            <div class="avatar-upload">
              <img v-if="avatarPreview1" :src="avatarPreview1" class="avatar-preview" alt="头像1" />
              <input v-model="loveConfig.avatar1" type="text" placeholder="图片链接或上传本地图片" />
              <div class="avatar-actions">
                <input id="avatar-file-1" ref="avatarFile1" type="file" accept="image/*" class="hidden-input" @change="uploadAvatar(1)" />
                <label for="avatar-file-1" class="btn-ghost">选择本地图片</label>
              </div>
            </div>
          </div>
          <div class="form-row">
            <label>头像2</label>
            <div class="avatar-upload">
              <img v-if="avatarPreview2" :src="avatarPreview2" class="avatar-preview" alt="头像2" />
              <input v-model="loveConfig.avatar2" type="text" placeholder="图片链接或上传本地图片" />
              <div class="avatar-actions">
                <input id="avatar-file-2" ref="avatarFile2" type="file" accept="image/*" class="hidden-input" @change="uploadAvatar(2)" />
                <label for="avatar-file-2" class="btn-ghost">选择本地图片</label>
              </div>
            </div>
          </div>
          <button type="button" class="btn-primary" :disabled="saving" @click="saveLoveConfig">
            {{ saving ? '保存中…' : '保存配置' }}
          </button>
        </div>
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

.admin-container {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 24px;
  letter-spacing: 4px;
  font-size: 1.8rem;
  color: #fff;
}

.glass-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.tabs {
  display: flex;
  gap: 0;
  margin-bottom: 24px;
  padding: 4px;
  overflow-x: auto;
}

.tabs button {
  flex: 1;
  padding: 10px 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  background: none;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.tabs button.active {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.section { display: flex; flex-direction: column; gap: 16px; }

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.count-badge {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.refresh-btn {
  padding: 7px 16px;
  font-size: 13px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
}

/* 工单 */
.ticket-card { padding: 20px; }

.ticket-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.email { color: rgba(255, 255, 255, 0.9); font-size: 14px; }
.status {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 10px;
}
.pending { background: rgba(251, 191, 36, 0.15); color: #fbbf24; }
.replied { background: rgba(52, 211, 153, 0.15); color: #34d399; }
.closed { background: rgba(156, 163, 175, 0.15); color: #9ca3af; }
.time { font-size: 12px; color: rgba(255, 255, 255, 0.4); margin-left: auto; }

.ticket-card h3 { font-size: 15px; margin-bottom: 6px; color: #7dd3fc; }
.ticket-content { font-size: 14px; line-height: 1.6; color: rgba(255, 255, 255, 0.75); margin-bottom: 12px; }

.existing-replies {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 10px;
}

.reply-bubble {
  background: rgba(52, 211, 153, 0.07);
  border-left: 3px solid rgba(52, 211, 153, 0.4);
  padding: 10px 14px;
  border-radius: 0 8px 8px 0;
}
.reply-admin { font-size: 13px; font-weight: 600; color: #34d399; }
.reply-bubble p { font-size: 14px; color: rgba(255, 255, 255, 0.8); margin: 4px 0; }

.reply-form { margin-top: 12px; }
.reply-form textarea {
  width: 100%;
  padding: 10px 14px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  resize: vertical;
  box-sizing: border-box;
}
.reply-actions { display: flex; gap: 8px; margin-top: 8px; }

.btn-primary {
  padding: 9px 22px;
  font-size: 14px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary:hover:not(:disabled) { background: #0284c7; }

.btn-ghost {
  padding: 9px 16px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-ghost:hover { background: rgba(255, 255, 255, 0.14); }

.reply-btn {
  font-size: 13px;
  color: #38bdf8;
  background: none;
  border: 1px solid rgba(56, 189, 248, 0.3);
  border-radius: 6px;
  padding: 5px 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.reply-btn:hover { background: rgba(56, 189, 248, 0.1); }

/* 资源 */
.add-resource { padding: 24px; }
.add-resource h3 { margin-bottom: 16px; color: #7dd3fc; font-size: 15px; }

.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}
.form-row input {
  flex: 1;
  min-width: 0;
  padding: 10px 14px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
}
.form-row input::placeholder { color: rgba(255, 255, 255, 0.4); }
.icon-row { align-items: center; }

.resource-list { display: flex; flex-direction: column; gap: 10px; }
.resource-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
}
.resource-icon { width: 40px; height: 40px; object-fit: contain; flex-shrink: 0; }
.resource-icon-ph {
  width: 40px; height: 40px;
  background: rgba(125, 211, 252, 0.2);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px; font-weight: 600; color: #7dd3fc;
  flex-shrink: 0;
}
.resource-info { flex: 1; min-width: 0; }
.resource-name { display: block; font-weight: 600; color: rgba(255, 255, 255, 0.9); font-size: 15px; }
.resource-link { font-size: 12px; color: rgba(255, 255, 255, 0.55); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }

.delete-sm {
  padding: 5px 12px;
  font-size: 12px;
  color: #f87171;
  background: none;
  border: 1px solid rgba(248, 113, 113, 0.3);
  border-radius: 6px;
  cursor: pointer;
  flex-shrink: 0;
}
.delete-sm:hover { background: rgba(248, 113, 113, 0.15); }

/* 简历编辑器 */
.resume-editor { padding: 24px; }
.resume-editor h3 { margin-bottom: 8px; color: #7dd3fc; font-size: 15px; }
.hint-text { font-size: 13px; color: rgba(255, 255, 255, 0.45); margin-bottom: 16px; }
.resume-textarea {
  width: 100%;
  min-height: 480px;
  padding: 16px;
  font-size: 14px;
  color: #fff;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  resize: vertical;
  font-family: 'Courier New', monospace;
  line-height: 1.6;
  box-sizing: border-box;
}
.resume-actions { margin-top: 16px; display: flex; justify-content: flex-end; }

/* 恋爱配置 */
.love-config { padding: 24px; }
.love-config h3 { margin-bottom: 20px; color: #7dd3fc; font-size: 15px; }
.love-config .form-row { flex-direction: column; align-items: flex-start; }
.love-config .form-row label { font-size: 14px; color: rgba(255, 255, 255, 0.8); margin-bottom: 6px; }
.love-config .form-row input { width: 100%; box-sizing: border-box; }

.avatar-upload { display: flex; flex-direction: column; gap: 8px; width: 100%; }
.avatar-preview { width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid rgba(255, 255, 255, 0.2); }
.avatar-actions { display: flex; align-items: center; gap: 8px; }

.hidden-input { position: absolute; width: 0.1px; height: 0.1px; opacity: 0.01; overflow: hidden; z-index: -1; }

.empty { text-align: center; color: rgba(255, 255, 255, 0.4); font-size: 14px; padding: 30px 0; }
</style>
