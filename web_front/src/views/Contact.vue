<script setup>
import { ref, watch } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { user } = useAuth()
const email = ref('')

watch(user, (u) => {
  if (u?.email && !email.value) email.value = u.email
}, { immediate: true })

const subject = ref('')
const content = ref('')
const loading = ref(false)
const success = ref(false)

async function submit() {
  if (!email.value.trim()) { alert('请输入邮箱'); return }
  if (!subject.value.trim()) { alert('请输入主题'); return }
  if (!content.value.trim()) { alert('请输入内容'); return }
  loading.value = true
  success.value = false
  try {
    await request.post('/ticket/create', {
      email: email.value.trim(),
      subject: subject.value.trim(),
      content: content.value.trim(),
    })
    success.value = true
    subject.value = ''
    content.value = ''
  } catch (e) {
    alert(e.message || '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="contact-container">
      <h1>联系我</h1>
      <p class="sub">提交工单，我会尽快回复</p>

      <div v-if="success" class="success-msg glass-card">
        <div class="success-icon">✅</div>
        <p>工单已提交！你可以在 <strong>个人主页 → 我的工单</strong> 查看管理员回复</p>
        <button type="button" class="reset-btn" @click="success = false">再次提交</button>
      </div>

      <form v-else class="ticket-form glass-card" @submit.prevent="submit">
        <div class="form-group">
          <label>邮箱 <span class="required">*</span></label>
          <input v-model="email" type="email" placeholder="你的邮箱地址" required />
        </div>
        <div class="form-group">
          <label>主题 <span class="required">*</span></label>
          <input v-model="subject" type="text" placeholder="简述你的问题" required />
        </div>
        <div class="form-group">
          <label>详细描述 <span class="required">*</span></label>
          <textarea v-model="content" placeholder="详细描述你的问题或建议…" rows="6" required />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '提交中…' : '提交工单' }}
        </button>
      </form>
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

.contact-container {
  max-width: 560px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 8px;
  letter-spacing: 6px;
  font-size: 1.8rem;
  color: #fff;
}

.sub {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 32px;
  font-size: 14px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.ticket-form {
  padding: 32px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.required { color: #f87171; }

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: rgba(14, 165, 233, 0.6);
  outline: none;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.form-group textarea { resize: vertical; }

.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 2px;
}
.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(14, 165, 233, 0.35);
}
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.success-msg {
  padding: 40px 32px;
  text-align: center;
}

.success-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
}

.success-msg p {
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.7;
  margin-bottom: 20px;
}

.success-msg strong { color: #7dd3fc; }

.reset-btn {
  padding: 10px 24px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.reset-btn:hover { background: rgba(255, 255, 255, 0.18); }
</style>
