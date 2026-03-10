<script setup>
import { ref, computed } from 'vue'
import MarkdownIt from 'markdown-it'
import request from '@/utils/request'
import { useAuth } from '@/stores/auth'

const md = new MarkdownIt({ html: true, linkify: true })
const { user } = useAuth()

const passwordInput = ref('')
const unlocked = ref(false)
const content = ref('')
const editingContent = ref('')
const loading = ref(false)
const saving = ref(false)
const editMode = ref(false)
const error = ref('')

async function verify() {
  if (!passwordInput.value.trim()) {
    error.value = '请输入密码'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const res = await request.post('/site/resume/verify', { password: passwordInput.value })
    if (res && res.data) {
      unlocked.value = true
      await fetchContent()
    } else {
      error.value = '密码错误'
    }
  } catch (e) {
    error.value = e.message || '验证失败'
  } finally {
    loading.value = false
  }
}

async function fetchContent() {
  try {
    const res = await request.get('/resume/content')
    content.value = (res && res.data) ? res.data : (res || '')
    editingContent.value = content.value
  } catch {
    content.value = ''
    editingContent.value = ''
  }
}

const htmlContent = computed(() => (content.value ? md.render(content.value) : ''))
const isAdmin = computed(() => user.value?.role === 'ADMIN')

function startEdit() {
  editingContent.value = content.value
  editMode.value = true
}

function cancelEdit() {
  editMode.value = false
  editingContent.value = content.value
}

async function saveResume() {
  if (!editingContent.value.trim()) {
    alert('内容不能为空')
    return
  }
  saving.value = true
  try {
    await request.put('/resume/content', { content: editingContent.value })
    content.value = editingContent.value
    editMode.value = false
    alert('简历保存成功')
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="resume-page">
    <div v-if="!unlocked" class="password-gate">
      <div class="gate-card">
        <h2>简历</h2>
        <p class="hint">请输入密码查看</p>
        <input
          v-model="passwordInput"
          type="password"
          class="password-input"
          placeholder="密码"
          @keyup.enter="verify"
        />
        <p v-if="error" class="error-msg">{{ error }}</p>
        <button type="button" class="submit-btn" :disabled="loading" @click="verify">
          {{ loading ? '验证中…' : '确认' }}
        </button>
      </div>
    </div>
    <div v-else class="resume-content">
      <div class="resume-inner">
        <div class="resume-header">
          <h1>卢欢 · 求职简历</h1>
          <div v-if="isAdmin" class="resume-admin-actions">
            <button v-if="!editMode" type="button" class="submit-btn admin-btn" @click="startEdit">在线编辑</button>
            <template v-else>
              <button type="button" class="submit-btn admin-btn" :disabled="saving" @click="saveResume">
                {{ saving ? '保存中…' : '保存' }}
              </button>
              <button type="button" class="cancel-btn" @click="cancelEdit">取消</button>
            </template>
          </div>
        </div>
        <div v-if="!editMode" class="resume-body md-body" v-html="htmlContent" />
        <div v-else class="resume-body">
          <textarea v-model="editingContent" class="resume-editor" rows="24" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.resume-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
  transition: background 0.3s ease;
}

.password-gate {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.gate-card {
  width: 100%;
  max-width: 360px;
  padding: 40px 32px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.gate-card h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.hint {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 24px;
}

.password-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  outline: none;
  background: #f9fafb;
  color: #1f2937;
  transition: all 0.2s;
}

.password-input:focus {
  border-color: #10b981;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.error-msg {
  margin-top: 12px;
  font-size: 13px;
  color: #ef4444;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.resume-content {
  padding: 48px 24px 80px;
}

.resume-inner {
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.resume-header {
  padding: 32px 40px 24px;
  border-bottom: 2px solid #f0fdf4;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(5, 150, 105, 0.05) 100%);
}

.resume-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 2px;
}

.resume-admin-actions {
  margin-top: 14px;
  display: flex;
  gap: 10px;
}

.admin-btn {
  width: auto;
  margin-top: 0;
  padding: 10px 18px;
  font-size: 14px;
}

.cancel-btn {
  padding: 10px 18px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  border-radius: 8px;
  cursor: pointer;
}

.resume-body {
  padding: 32px 40px 48px;
  color: #374151;
}

.resume-editor {
  width: 100%;
  min-height: 520px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 14px;
  font-size: 14px;
  line-height: 1.7;
  resize: vertical;
  box-sizing: border-box;
  color: #111827;
  background: #ffffff;
  font-family: 'Courier New', monospace;
}

:deep(.md-body h1),
:deep(.md-body h2),
:deep(.md-body h3) {
  margin-top: 1.5em;
  margin-bottom: 0.6em;
  font-weight: 700;
  color: #1f2937;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.08) 0%, rgba(5, 150, 105, 0.08) 100%);
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #10b981;
}

:deep(.md-body h2) {
  font-size: 1.2em;
  padding-bottom: 8px;
  border-bottom: none;
}

:deep(.md-body h3) {
  font-size: 1.05em;
}

:deep(.md-body p),
:deep(.md-body li) {
  color: #374151;
  line-height: 1.8;
  font-size: 15px;
}

:deep(.md-body table) {
  width: 100%;
  border-collapse: collapse;
}

:deep(.md-body th),
:deep(.md-body td) {
  border: 1px solid #e5e7eb;
  padding: 12px 16px;
  text-align: left;
  color: #374151;
}

:deep(.md-body th) {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(5, 150, 105, 0.1) 100%);
  font-weight: 600;
}

:deep(.md-body a) {
  color: #059669;
  font-weight: 500;
  text-decoration: none;
  border-bottom: 2px solid rgba(5, 150, 105, 0.3);
  transition: all 0.2s;
}

:deep(.md-body a:hover) {
  border-bottom-color: #059669;
}

:deep(.md-body hr) {
  border: none;
  border-top: 2px solid #f0fdf4;
  margin: 1.5em 0;
}

/* 深色主题 - 使用 :deep() 正确写法 */
[data-theme="night"] .resume-page {
  background: linear-gradient(135deg, #1f2937 0%, #1a202c 50%, #0f1419 100%);
}

[data-theme="night"] .resume-inner,
[data-theme="night"] .gate-card {
  background: rgba(31, 41, 55, 0.98);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

[data-theme="night"] .resume-header {
  border-bottom-color: rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.08) 0%, rgba(5, 150, 105, 0.08) 100%);
}

[data-theme="night"] .resume-header h1,
[data-theme="night"] .gate-card h2 {
  color: #ffffff;
}

[data-theme="night"] .cancel-btn {
  color: #e5e7eb;
  border-color: rgba(209, 213, 219, 0.35);
  background: rgba(55, 65, 81, 0.9);
}

[data-theme="night"] .resume-editor {
  background: rgba(31, 41, 55, 0.96);
  color: #f3f4f6;
  border-color: rgba(209, 213, 219, 0.3);
}

[data-theme="night"] .hint {
  color: #d1d5db;
}

[data-theme="night"] .password-input {
  background: rgba(55, 65, 81, 0.9);
  border-color: rgba(209, 213, 219, 0.4);
  color: #ffffff;
}

[data-theme="night"] .password-input:focus {
  border-color: #10b981;
  background: rgba(55, 65, 81, 1);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
}

[data-theme="night"] .error-msg {
  color: #fca5a5;
}

[data-theme="night"] .submit-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}

[data-theme="night"] .submit-btn:hover:not(:disabled) {
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.5);
}

[data-theme="night"] :deep(.resume-body .md-body) {
  color: #f3f4f6 !important;
}

[data-theme="night"] :deep(.resume-body .md-body h1),
[data-theme="night"] :deep(.resume-body .md-body h2),
[data-theme="night"] :deep(.resume-body .md-body h3) {
  color: #ffffff !important;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.15) 100%);
  border-left-color: #10b981;
}

[data-theme="night"] :deep(.resume-body .md-body p),
[data-theme="night"] :deep(.resume-body .md-body li) {
  color: #f3f4f6 !important;
}

[data-theme="night"] :deep(.resume-body .md-body th),
[data-theme="night"] :deep(.resume-body .md-body td) {
  border-color: rgba(209, 213, 219, 0.3);
  color: #e5e7eb !important;
}

[data-theme="night"] :deep(.resume-body .md-body th) {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.15) 100%);
}

[data-theme="night"] :deep(.resume-body .md-body a) {
  color: #4ade80 !important;
  border-bottom-color: rgba(74, 222, 128, 0.3);
}

[data-theme="night"] :deep(.resume-body .md-body a:hover) {
  border-bottom-color: #4ade80;
}

[data-theme="night"] :deep(.resume-body .md-body hr) {
  border-top-color: rgba(209, 213, 219, 0.2);
}

[data-theme="night"] :deep(.resume-body .md-body strong),
[data-theme="night"] :deep(.resume-body .md-body em),
[data-theme="night"] :deep(.resume-body .md-body code) {
  color: #f3f4f6;
}

[data-theme="night"] :deep(.resume-body .md-body ul),
[data-theme="night"] :deep(.resume-body .md-body ol) {
  color: #f3f4f6;
}
</style>

<!-- 深色主题全局样式，用于处理 v-html 生成的动态内容 -->
<style>
[data-theme="night"] .md-body {
  color: #f3f4f6 !important;
}

[data-theme="night"] .md-body * {
  color: #f3f4f6 !important;
}

[data-theme="night"] .md-body h1,
[data-theme="night"] .md-body h2,
[data-theme="night"] .md-body h3,
[data-theme="night"] .md-body h4,
[data-theme="night"] .md-body h5,
[data-theme="night"] .md-body h6 {
  color: #ffffff !important;
}

[data-theme="night"] .md-body p,
[data-theme="night"] .md-body li,
[data-theme="night"] .md-body td,
[data-theme="night"] .md-body th,
[data-theme="night"] .md-body strong,
[data-theme="night"] .md-body em,
[data-theme="night"] .md-body span,
[data-theme="night"] .md-body div {
  color: #f3f4f6 !important;
}

[data-theme="night"] .md-body a {
  color: #4ade80 !important;
}

[data-theme="night"] .md-body code,
[data-theme="night"] .md-body pre {
  color: #f3f4f6 !important;
  background: rgba(50, 50, 50, 0.5) !important;
}
</style>