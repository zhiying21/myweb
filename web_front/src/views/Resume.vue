<script setup>
import { ref, computed } from 'vue'
import MarkdownIt from 'markdown-it'
import request from '@/utils/request'

const PASSWORD = 'AC_pH_under7'
const md = new MarkdownIt({ html: true, linkify: true })

const passwordInput = ref('')
const unlocked = ref(false)
const content = ref('')
const loading = ref(false)
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
  } catch {
    content.value = ''
  }
}

const htmlContent = computed(() => (content.value ? md.render(content.value) : ''))
</script>

<template>
  <div class="resume-page">
    <div class="resume-orbit-layer" aria-hidden="true">
      <div class="orbit orbit-1">
        <span class="orbit-dot" />
      </div>
      <div class="orbit orbit-2">
        <span class="orbit-dot" />
      </div>
      <div class="orbit orbit-3">
        <span class="orbit-dot" />
      </div>
    </div>
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
        </div>
        <div class="resume-body md-body" v-html="htmlContent" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.resume-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: radial-gradient(circle at top, #e8fdf3 0, #c9f6dd 35%, #bfe4ff 100%);
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
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.gate-card h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.hint {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

.password-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.2s;
}

.password-input:focus {
  border-color: #0ea5e9;
}

.error-msg {
  margin-top: 12px;
  font-size: 13px;
  color: #dc2626;
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #0284c7;
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
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.resume-header {
  padding: 32px 40px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.resume-header h1 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 2px;
}

.resume-body {
  padding: 32px 40px 48px;
}

:deep(.md-body h1),
:deep(.md-body h2),
:deep(.md-body h3) {
  margin-top: 1.5em;
  margin-bottom: 0.6em;
  font-weight: 600;
  color: #1a1a1a;
  background: rgba(255, 255, 255, 0.85);
  padding: 10px 14px;
  border-radius: 8px;
  border-left: 4px solid #0ea5e9;
}

:deep(.md-body h2) {
  font-size: 1.1em;
  padding-bottom: 8px;
  border-bottom: none;
}

:deep(.md-body p),
:deep(.md-body li) {
  color: #333;
  line-height: 1.75;
}

:deep(.md-body table) {
  width: 100%;
  border-collapse: collapse;
}

:deep(.md-body th),
:deep(.md-body td) {
  border: 1px solid #e5e5e5;
  padding: 10px 14px;
  text-align: left;
}

:deep(.md-body a) {
  color: #0ea5e9;
}

:deep(.md-body hr) {
  border: none;
  border-top: 1px solid #eee;
  margin: 1.5em 0;
}
.resume-orbit-layer {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  opacity: 0.8;
}

.orbit {
  position: absolute;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  border: 1px dashed rgba(59, 130, 246, 0.15);
  animation: orbit-float 18s linear infinite;
}

.orbit-1 {
  top: 10%;
  left: 5%;
}

.orbit-2 {
  top: 55%;
  right: -40px;
  animation-duration: 22s;
}

.orbit-3 {
  bottom: -60px;
  left: 40%;
  width: 260px;
  height: 260px;
  animation-duration: 26s;
}

.orbit-dot {
  position: absolute;
  top: 15%;
  left: 60%;
  width: 16px;
  height: 16px;
  border-radius: 999px;
  background: radial-gradient(circle at 30% 30%, #93c5fd, #1d4ed8);
  box-shadow:
    0 0 12px rgba(59, 130, 246, 0.9),
    0 0 28px rgba(59, 130, 246, 0.7);
  transform-origin: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.orbit-dot::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 1px;
  transform-origin: left center;
  transform: translateY(-50%);
  background: linear-gradient(to right, rgba(148, 163, 253, 0.1), rgba(96, 165, 250, 0.8));
  opacity: 0;
  transition: width 0.3s ease, opacity 0.3s ease;
}

.resume-inner:hover .orbit-dot,
.orbit-dot:hover {
  transform: scale(1.35);
  box-shadow:
    0 0 18px rgba(59, 130, 246, 1),
    0 0 40px rgba(59, 130, 246, 0.9);
}

.resume-inner:hover .orbit-dot::after,
.orbit-dot:hover::after {
  width: 140px;
  opacity: 1;
}

@keyframes orbit-float {
  0% {
    transform: translate3d(0, 0, 0) rotate(0deg);
  }
  50% {
    transform: translate3d(10px, -12px, 0) rotate(180deg);
  }
  100% {
    transform: translate3d(0, 0, 0) rotate(360deg);
  }
}

[data-theme="night"] .resume-page {
  background: #020617;
}

[data-theme="night"] .resume-inner,
[data-theme="night"] .gate-card {
  background: rgba(15, 23, 42, 0.97);
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.7);
}

[data-theme="night"] .resume-header h1,
[data-theme="night"] .gate-card h2,
[data-theme="night"] .hint {
  color: #e5e7eb;
}

[data-theme="night"] .password-input {
  background: rgba(15, 23, 42, 0.9);
  border-color: rgba(148, 163, 184, 0.8);
  color: #e5e7eb;
}

[data-theme="night"] .submit-btn {
  background: #0ea5e9;
}

[data-theme="night"] ::deep(.md-body h1),
[data-theme="night"] ::deep(.md-body h2),
[data-theme="night"] ::deep(.md-body h3) {
  color: #e5e7eb;
  background: rgba(15, 23, 42, 0.9);
  border-left-color: #38bdf8;
}

[data-theme="night"] ::deep(.md-body p),
[data-theme="night"] ::deep(.md-body li) {
  color: #e5e7eb;
}

[data-theme="night"] ::deep(.md-body th),
[data-theme="night"] ::deep(.md-body td) {
  border-color: rgba(148, 163, 184, 0.5);
}

[data-theme="night"] ::deep(.md-body a) {
  color: #38bdf8;
}

[data-theme="night"] ::deep(.md-body hr) {
  border-top-color: rgba(148, 163, 184, 0.6);
}
</style>
