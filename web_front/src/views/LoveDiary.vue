<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

const PASSWORD = 'AC_pH_under7'
const passwordInput = ref('')
const unlocked = ref(false)
const config = ref({ startTime: null, name1: 'TA', name2: 'TA', avatar1: null, avatar2: null })
const loading = ref(true)
const pwdError = ref('')

async function verifyPassword() {
  if (passwordInput.value !== PASSWORD) {
    pwdError.value = '密码错误'
    return
  }
  unlocked.value = true
  pwdError.value = ''
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
    <div class="love-bg">
      <div class="hearts-bg" />
    </div>
    <div v-if="!unlocked" class="password-gate">
      <div class="gate-card">
        <h2>恋爱日记</h2>
        <p class="hint">请输入密码查看</p>
        <input v-model="passwordInput" type="password" placeholder="密码" @keyup.enter="verifyPassword" />
        <p v-if="pwdError" class="error">{{ pwdError }}</p>
        <button type="button" class="submit-btn" @click="verifyPassword">确认</button>
      </div>
    </div>
    <div v-else-if="loading" class="loading">加载中…</div>
    <div v-else class="love-content">
      <div class="avatars-row">
        <div class="avatar-wrap">
          <img :src="config.avatar1 || '/vite.svg'" :alt="config.name1" class="avatar" @error="$event.target.src='/vite.svg'" />
          <span class="name">{{ config.name1 }}</span>
        </div>
        <div class="heartbeat-line">
          <svg viewBox="0 0 120 40" class="heart-svg">
            <path
              class="heart-path"
              d="M10 20 Q30 0 60 20 Q90 40 110 20"
              fill="none"
              stroke="url(#heartGrad)"
              stroke-width="3"
              stroke-linecap="round"
            />
            <defs>
              <linearGradient id="heartGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#ff6b9d" />
                <stop offset="100%" style="stop-color:#c44569" />
              </linearGradient>
            </defs>
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
  </div>
</template>

<style scoped>
.love-diary {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  overflow: hidden;
}

.love-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a0a2e 0%, #2d1b4e 50%, #1a0a2e 100%);
  z-index: 0;
}

.hearts-bg {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 20% 30%, rgba(255, 107, 157, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(196, 69, 105, 0.15) 0%, transparent 50%);
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
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 107, 157, 0.5);
  min-width: 320px;
}

.gate-card h2 {
  font-size: 1.4rem;
  color: #ffb8d0;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.gate-card .hint {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 20px;
}

.gate-card input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  margin-bottom: 12px;
}

.gate-card .error {
  font-size: 13px;
  color: #f87171;
  margin-bottom: 12px;
}

.gate-card .submit-btn {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  color: #fff;
  background: linear-gradient(135deg, #ff6b9d, #c44569);
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.loading {
  position: relative;
  z-index: 2;
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.love-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 48px;
}

.avatars-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
  justify-content: center;
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
  color: #ffb8d0;
  letter-spacing: 2px;
}

.heartbeat-line {
  width: 80px;
  height: 40px;
  flex-shrink: 0;
}

.heart-svg {
  width: 100%;
  height: 100%;
}

.heart-path {
  stroke-dasharray: 200;
  stroke-dashoffset: 200;
  animation: draw 2s ease-in-out infinite;
}

@keyframes draw {
  0% { stroke-dashoffset: 200; }
  50% { stroke-dashoffset: 0; }
  100% { stroke-dashoffset: 200; }
}

.love-time-block {
  text-align: center;
  padding: 32px 48px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  border: 1px solid rgba(255, 107, 157, 0.5);
  backdrop-filter: blur(10px);
}

.label {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12px;
}

.time-display {
  font-size: 1.8rem;
  font-weight: 600;
  color: #ffb8d0;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
}
</style>
