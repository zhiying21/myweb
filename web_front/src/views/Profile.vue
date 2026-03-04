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
})

function triggerAvatarFile() {
  avatarFileInput.value?.click()
}

async function onAvatarFileChange(e) {
  const file = e.target.files?.[0]
  if (!file?.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  try {
    const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
    const token = localStorage.getItem('zhiying_token')
    const res = await fetch(`${baseURL}/auth/upload`, {
      method: 'POST',
      headers: token ? { Authorization: `Bearer ${token}` } : {},
      body: formData,
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      avatar.value = data.data
    } else {
      throw new Error(data.message || '上传失败')
    }
  } catch (err) {
    alert(err.message || '上传失败')
  }
  e.target.value = ''
}

async function save() {
  saving.value = true
  try {
    const res = await request.put('/auth/profile', {
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
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div v-if="isLoggedIn" class="profile-container">
      <h1>个人主页</h1>
      <p class="sub">修改昵称与头像，将作为文档发布者展示</p>
      <div class="profile-form">
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

.profile-container {
  max-width: 480px;
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
  margin-bottom: 32px;
  font-size: 14px;
}

.profile-form {
  padding: 32px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
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

.avatar-actions {
  display: flex;
  gap: 8px;
}

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
  background: rgba(14, 165, 233, 0.6);
  border-radius: 8px;
  cursor: pointer;
}

.btn-label:hover {
  background: rgba(14, 165, 233, 0.8);
}

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
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
