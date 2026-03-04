<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import request from '@/utils/request'

const tickets = ref([])
const resources = ref([])
const loveConfig = ref({ startTime: '', name1: '', name2: '', avatar1: '', avatar2: '' })
const tab = ref('tickets')
const saving = ref(false)
const newResource = ref({ name: '', icon: '', link: '' })
const avatarFile1 = ref(null)
const avatarFile2 = ref(null)
const resourceIconFile = ref(null)
// 仅当像 URL 时才显示预览，避免输入过程中图片加载失败
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

async function fetchResources() {
  try {
    const res = await request.get('/resource/list')
    resources.value = (res && res.data) ? res.data : []
  } catch {
    resources.value = []
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

function triggerResourceIcon() {
  resourceIconFile.value?.click()
}

async function uploadResourceIcon() {
  const input = resourceIconFile.value
  if (!input?.files?.length) return
  const file = input.files[0]
  const isImage = (file.type || '').startsWith('image/') || /\.(jpe?g|png|gif|webp|bmp|svg)$/i.test(file.name || '')
  if (!isImage) {
    alert('请选择图片文件')
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  input.value = ''
  try {
    const res = await request.post('/admin/upload', formData, { timeout: 30000 })
    const url = res?.data
    if (!url) throw new Error('上传失败')
    newResource.value.icon = url
  } catch (e) {
    if (e.status === 403 || e.response?.status === 403) {
      alert('无权限上传，请确认已使用管理员账号登录后重试')
    } else {
      alert(e.message || '上传失败')
    }
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
    const res = await request.post('/love/config', {
      startTime: loveConfig.value.startTime || new Date().toISOString().slice(0, 19).replace('Z', ''),
      name1: loveConfig.value.name1 || 'TA',
      name2: loveConfig.value.name2 || 'TA',
      avatar1: loveConfig.value.avatar1 || null,
      avatar2: loveConfig.value.avatar2 || null,
    })
    if (res && res.code !== 200 && res.code !== undefined) {
      throw new Error(res.message || '保存失败')
    }
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
  const type = file.type || ''
  const isImage = type.startsWith('image/') || /\.(jpe?g|png|gif|webp|bmp|svg)$/i.test(file.name || '')
  if (!isImage) {
    alert('请选择图片文件（如 JPG、PNG、GIF、WebP）')
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  input.value = ''
  try {
    const res = await request.post('/admin/upload', formData, { timeout: 30000 })
    const url = res?.data
    if (!url) throw new Error('上传失败')
    if (which === 1) loveConfig.value.avatar1 = url
    else loveConfig.value.avatar2 = url
  } catch (e) {
    if (e.status === 403 || e.response?.status === 403) {
      alert('无权限上传，请确认已使用管理员账号登录后重试')
    } else {
      alert(e.message || '上传失败')
    }
  }
}

watch(tab, (t) => {
  if (t === 'tickets') fetchTickets()
})

onMounted(() => {
  fetchTickets()
  fetchResources()
  fetchLoveConfig()
})
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="admin-container">
      <h1>管理后台</h1>
      <div class="tabs">
        <button type="button" :class="{ active: tab === 'tickets' }" @click="tab = 'tickets'">工单</button>
        <button type="button" :class="{ active: tab === 'resources' }" @click="tab = 'resources'">资源</button>
        <button type="button" :class="{ active: tab === 'love' }" @click="tab = 'love'">恋爱日记配置</button>
      </div>
      <div v-if="tab === 'tickets'" class="tickets-list">
        <div class="tickets-toolbar">
          <span class="ticket-count">共 {{ tickets.length }} 条</span>
          <button type="button" class="refresh-btn" @click="fetchTickets">刷新</button>
        </div>
        <div v-for="t in tickets" :key="t.id" class="ticket-card">
          <div class="ticket-header">
            <span class="email">{{ t.email }}</span>
            <span class="status">{{ t.status }}</span>
            <span class="time">{{ t.createTime }}</span>
          </div>
          <h3>{{ t.subject }}</h3>
          <p>{{ t.content }}</p>
        </div>
        <p v-if="!tickets.length" class="empty">暂无工单</p>
      </div>
      <div v-else-if="tab === 'resources'" class="resources-admin">
        <div class="add-resource">
          <div class="add-resource-row">
            <input v-model="newResource.name" placeholder="软件名称" />
          </div>
          <div class="add-resource-row resource-icon-row">
            <input v-model="newResource.icon" placeholder="图标 URL 或选择本地图片" />
            <input ref="resourceIconFile" type="file" accept="image/jpeg,image/png,image/gif,image/webp,.jpg,.jpeg,.png,.gif,.webp" class="hidden-input" @change="uploadResourceIcon" />
            <button type="button" class="btn-label" @click.prevent="triggerResourceIcon()">选择本地图片</button>
          </div>
          <div class="add-resource-row">
            <input v-model="newResource.link" placeholder="下载链接" />
          </div>
          <div class="add-resource-row">
            <button type="button" class="add-btn" @click="addResource">添加</button>
          </div>
        </div>
        <div class="resource-list">
          <div v-for="r in resources" :key="r.id" class="resource-item">
            <span class="name">{{ r.name }}</span>
            <a :href="r.link" target="_blank" rel="noopener noreferrer" class="link">{{ r.link }}</a>
          </div>
        </div>
      </div>
      <div v-else class="love-config">
        <div class="form-row">
          <label>开始时间</label>
          <input v-model="loveConfig.startTime" type="datetime-local" />
        </div>
        <div class="form-row">
          <label>姓名1</label>
          <input v-model="loveConfig.name1" type="text" placeholder="TA" />
        </div>
        <div class="form-row">
          <label>姓名2</label>
          <input v-model="loveConfig.name2" type="text" placeholder="TA" />
        </div>
        <div class="form-row">
          <label>头像1</label>
          <div class="avatar-upload">
            <img v-if="avatarPreview1" :src="avatarPreview1" class="avatar-preview" alt="头像1" />
            <input v-model="loveConfig.avatar1" type="text" placeholder="图片链接或上传本地图片" />
            <div class="avatar-actions">
              <input id="avatar-file-1" ref="avatarFile1" type="file" accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/svg+xml,.jpg,.jpeg,.png,.gif,.webp,.bmp,.svg" class="hidden-input" @change="uploadAvatar(1)" />
              <label for="avatar-file-1" class="btn-label">选择本地图片</label>
            </div>
          </div>
        </div>
        <div class="form-row">
          <label>头像2</label>
          <div class="avatar-upload">
            <img v-if="avatarPreview2" class="avatar-preview" alt="头像2" :src="avatarPreview2" />
            <input v-model="loveConfig.avatar2" type="text" placeholder="图片链接或上传本地图片" />
            <div class="avatar-actions">
              <input id="avatar-file-2" ref="avatarFile2" type="file" accept="image/jpeg,image/png,image/gif,image/webp,image/bmp,image/svg+xml,.jpg,.jpeg,.png,.gif,.webp,.bmp,.svg" class="hidden-input" @change="uploadAvatar(2)" />
              <label for="avatar-file-2" class="btn-label">选择本地图片</label>
            </div>
          </div>
        </div>
        <button type="button" class="save-btn" :disabled="saving" @click="saveLoveConfig">
          {{ saving ? '保存中…' : '保存' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  position: relative;
  min-height: 100vh;
  padding: 100px 24px 60px;
  color: #fff;
}

.admin-container {
  max-width: 720px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 32px;
  letter-spacing: 4px;
}

.tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tabs button {
  padding: 10px 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
}

.tabs button.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.tickets-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.ticket-count {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
}

.refresh-btn {
  padding: 8px 16px;
  font-size: 13px;
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.18);
}

.ticket-card {
  padding: 20px;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.ticket-header {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 13px;
}

.email { color: rgba(255, 255, 255, 0.95); }
.status { color: #fbbf24; }
.time { color: rgba(255, 255, 255, 0.55); }

.ticket-card h3 {
  font-size: 1rem;
  margin-bottom: 8px;
}

.ticket-card p {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
}

.hidden-input {
  position: absolute;
  width: 0.1px;
  height: 0.1px;
  opacity: 0.01;
  overflow: hidden;
  z-index: -1;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-preview {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.avatar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar-actions .btn-label {
  display: inline-block;
  padding: 6px 14px;
  font-size: 13px;
  color: #fff;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 6px;
  cursor: pointer;
}

.avatar-actions .btn-label:hover {
  background: rgba(255, 255, 255, 0.25);
}

.love-config .form-row {
  margin-bottom: 16px;
}

.love-config label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.love-config input {
  width: 100%;
  padding: 10px 14px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
}

.save-btn {
  margin-top: 24px;
  padding: 12px 32px;
  font-size: 15px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.empty {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
}

.add-resource {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 24px;
  max-width: 560px;
}

.add-resource-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.add-resource-row input {
  flex: 1;
  min-width: 0;
  padding: 10px 14px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 8px;
}

.add-resource-row input::placeholder {
  color: rgba(255, 255, 255, 0.45);
}

.add-resource-row.resource-icon-row input {
  flex: 1;
  min-width: 0;
}

.add-resource-row .btn-label {
  padding: 10px 16px;
  font-size: 14px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
}

.add-resource-row .btn-label:hover {
  background: rgba(255, 255, 255, 0.18);
}

.add-resource-row .add-btn {
  padding: 10px 24px;
  font-size: 14px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.resources-admin .resource-list {
  margin-top: 8px;
}

.resource-list .resource-item {
  padding: 14px 18px;
  margin-bottom: 10px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
}

.resource-list .resource-item .name {
  font-weight: 600;
  margin-right: 12px;
  color: rgba(255, 255, 255, 0.95);
  font-size: 15px;
}

.resource-list .resource-item .link {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}
</style>
