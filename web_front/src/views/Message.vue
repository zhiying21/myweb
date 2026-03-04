<script setup>
import { ref, onMounted } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import AuthModal from '@/components/AuthModal.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { isLoggedIn } = useAuth()
const showAuth = ref(false)
const messages = ref([])
const newContent = ref('')
const loading = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')

async function fetchMessages() {
  try {
    const res = await request.get('/message/list')
    messages.value = (res && res.data) ? res.data : []
  } catch {
    messages.value = []
  }
}

async function addMessage() {
  if (!newContent.value.trim()) return
  loading.value = true
  try {
    await request.post('/message/add', { content: newContent.value.trim() })
    newContent.value = ''
    await fetchMessages()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
    else alert(e.message || '发送失败')
  } finally {
    loading.value = false
  }
}

async function addComment(messageId, parentId, replyToId, content) {
  if (!content?.trim()) return
  try {
    await request.post('/message/comment', { messageId, parentId, replyToId, content: content.trim() })
    replyingTo.value = null
    replyContent.value = ''
    await fetchMessages()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
    else alert(e.message || '发送失败')
  }
}

async function toggleLike(commentId) {
  try {
    await request.post(`/message/comment/${commentId}/like`)
    await fetchMessages()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
  }
}

function startReply(c) {
  replyingTo.value = c
  replyContent.value = ''
}

onMounted(fetchMessages)
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="message-container">
      <h1>留言板</h1>
      <p v-if="!isLoggedIn" class="login-hint">请先 <button type="button" class="link-btn" @click="showAuth = true">登入</button> 后留言</p>
      <div v-else class="add-form">
        <textarea v-model="newContent" placeholder="写下你的留言…" rows="3" />
        <button type="button" class="submit-btn" :disabled="loading" @click="addMessage">发送</button>
      </div>
      <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message-card">
          <div class="msg-header">
            <span class="user">{{ msg.userNickname }}</span>
            <span class="time">{{ msg.createTime }}</span>
          </div>
          <p class="msg-content">{{ msg.content }}</p>
          <div class="comments">
            <div v-for="c in msg.comments" :key="c.id" class="comment">
              <div class="comment-header">
                <span class="user">{{ c.userNickname }}</span>
                <span class="time">{{ c.createTime }}</span>
                <button type="button" class="like-btn" @click="toggleLike(c.id)">❤ {{ c.likeCount }}</button>
              </div>
              <p class="comment-content">{{ c.content }}</p>
              <button type="button" class="reply-btn" @click="startReply(c)">回复</button>
              <div v-if="replyingTo?.id === c.id" class="reply-form">
                <input v-model="replyContent" placeholder="回复内容" @keyup.enter="addComment(msg.id, c.id, c.id, replyContent)" />
                <button type="button" @click="addComment(msg.id, c.id, c.id, replyContent)">发送</button>
              </div>
              <div v-for="r in c.replies" :key="r.id" class="reply">
                <span class="user">{{ r.userNickname }}</span>
                <span class="content">{{ r.content }}</span>
                <button type="button" class="like-btn" @click="toggleLike(r.id)">❤ {{ r.likeCount }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <AuthModal v-if="showAuth" @close="showAuth = false" @success="fetchMessages" />
  </div>
</template>

<style scoped>
.page {
  position: relative;
  min-height: 100vh;
  padding: 100px 24px 60px;
  color: #fff;
}

.message-container {
  max-width: 640px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 24px;
  letter-spacing: 4px;
}

.login-hint {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 32px;
}

.link-btn {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  text-decoration: underline;
}

.add-form {
  margin-bottom: 32px;
}

.add-form textarea {
  width: 100%;
  padding: 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  resize: vertical;
}

.submit-btn {
  margin-top: 12px;
  padding: 10px 24px;
  font-size: 14px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.message-card {
  padding: 24px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.msg-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.user {
  font-weight: 500;
  color: #7dd3fc;
}

.time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.msg-content {
  line-height: 1.6;
  margin-bottom: 16px;
}

.comment {
  padding: 12px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.like-btn {
  background: none;
  border: none;
  color: #f87171;
  cursor: pointer;
  font-size: 13px;
}

.reply-btn {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  font-size: 12px;
}

.reply-form {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

.reply-form input {
  flex: 1;
  padding: 8px 12px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
}

.reply {
  margin-left: 24px;
  padding: 8px 0;
  font-size: 13px;
}

.reply .content {
  color: rgba(255, 255, 255, 0.8);
}
</style>
