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
  if (!email.value.trim()) {
    alert('请输入邮箱')
    return
  }
  if (!subject.value.trim()) {
    alert('请输入主题')
    return
  }
  if (!content.value.trim()) {
    alert('请输入内容')
    return
  }
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
      <p class="sub">发送工单，我会尽快回复</p>
      <div v-if="success" class="success-msg">工单已提交，感谢你的留言！</div>
      <form v-else class="ticket-form" @submit.prevent="submit">
        <input v-model="email" type="email" placeholder="你的邮箱 *" required />
        <input v-model="subject" type="text" placeholder="主题 *" required />
        <textarea v-model="content" placeholder="详细描述你的问题或建议… *" rows="6" required />
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
  letter-spacing: 4px;
}

.sub {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 40px;
  font-size: 14px;
}

.ticket-form input,
.ticket-form textarea {
  width: 100%;
  padding: 14px 18px;
  margin-bottom: 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
}

.ticket-form input::placeholder,
.ticket-form textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.7;
}

.success-msg {
  padding: 24px;
  text-align: center;
  color: #34d399;
  background: rgba(52, 211, 153, 0.1);
  border-radius: 12px;
}
</style>
