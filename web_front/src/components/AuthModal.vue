<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '@/stores/auth'

const emit = defineEmits(['close', 'success'])

const mode = ref('login')
const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const { login, register } = useAuth()

const title = computed(() => (mode.value === 'login' ? '登入' : '注册'))
const switchText = computed(() => (mode.value === 'login' ? '没有账号？去注册' : '已有账号？去登入'))

function switchMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  error.value = ''
}

async function submit() {
  error.value = ''
  if (!email.value.trim()) {
    error.value = '请输入邮箱'
    return
  }
  if (!password.value) {
    error.value = '请输入密码'
    return
  }
  loading.value = true
  try {
    if (mode.value === 'login') {
      await login(email.value.trim(), password.value)
    } else {
      await register(email.value.trim(), password.value)
    }
    emit('success')
    emit('close')
  } catch (e) {
    error.value = e.message || '操作失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-overlay" @click.self="emit('close')">
    <div class="auth-modal">
      <button type="button" class="close-btn" @click="emit('close')">×</button>
      <h2>{{ title }}</h2>
      <input v-model="email" type="email" placeholder="邮箱" class="input" />
      <input v-model="password" type="password" placeholder="密码" class="input" />
      <p v-if="error" class="error">{{ error }}</p>
      <button type="button" class="submit-btn" :disabled="loading" @click="submit">
        {{ loading ? '处理中…' : title }}
      </button>
      <button type="button" class="switch-btn" @click="switchMode">{{ switchText }}</button>
    </div>
  </div>
</template>

<style scoped>
.auth-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
}

.auth-modal {
  position: relative;
  width: 90%;
  max-width: 360px;
  padding: 32px;
  background: rgba(30, 30, 40, 0.95);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  font-size: 24px;
  color: rgba(255, 255, 255, 0.6);
  background: none;
  border: none;
  cursor: pointer;
}

.auth-modal h2 {
  font-size: 1.4rem;
  color: #fff;
  margin-bottom: 24px;
  letter-spacing: 2px;
}

.input {
  width: 100%;
  padding: 12px 16px;
  margin-bottom: 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  outline: none;
}

.input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.error {
  font-size: 13px;
  color: #f87171;
  margin-bottom: 12px;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.7;
}

.switch-btn {
  width: 100%;
  margin-top: 12px;
  padding: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  background: none;
  border: none;
  cursor: pointer;
}

.switch-btn:hover {
  color: #38bdf8;
}
</style>
