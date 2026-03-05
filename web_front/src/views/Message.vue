<script setup>
import { ref, onMounted } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import AuthModal from '@/components/AuthModal.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { isLoggedIn, user } = useAuth()
const showAuth = ref(false)
const messages = ref([])
const newContent = ref('')
const loading = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')

async function fetchMessages() {
  try {
    const res = await request.get('/message/list')
    messages.value = (res && res.data) ? res.data : (Array.isArray(res) ? res : [])
  } catch {
    messages.value = []
  }
}

async function addMessage() {
  if (!newContent.value.trim()) return
  if (!isLoggedIn.value) { showAuth.value = true; return }
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

async function deleteMessage(id) {
  if (!confirm('确定删除这条留言？')) return
  try {
    await request.delete(`/message/${id}`)
    messages.value = messages.value.filter(m => m.id !== id)
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

async function toggleMessageLike(msg) {
  if (!isLoggedIn.value) { showAuth.value = true; return }
  try {
    const res = await request.post(`/message/${msg.id}/like`)
    const d = res?.data ?? res
    msg.likeCount = d.likeCount
    msg.liked = d.liked
  } catch (e) {
    if (e.status === 401) showAuth.value = true
  }
}

async function addComment(messageId, parentId, replyToId, content) {
  if (!content?.trim()) return
  if (!isLoggedIn.value) { showAuth.value = true; return }
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

async function deleteComment(commentId) {
  if (!confirm('删除评论？')) return
  try {
    await request.delete(`/message/comment/${commentId}`)
    await fetchMessages()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

async function toggleCommentLike(c) {
  if (!isLoggedIn.value) { showAuth.value = true; return }
  try {
    await request.post(`/message/comment/${c.id}/like`)
    await fetchMessages()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
  }
}

function startReply(c) {
  replyingTo.value = c
  replyContent.value = ''
}

function canDeleteMsg(msg) {
  return isLoggedIn.value && (user.value?.role === 'ADMIN' || msg.userId === user.value?.userId)
}
function canDeleteComment(c) {
  return isLoggedIn.value && (user.value?.role === 'ADMIN' || c.userId === user.value?.userId)
}

function formatDate(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(fetchMessages)
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="message-container">
      <h1>留言板</h1>
      <p class="sub">留下你的足迹</p>

      <!-- 发留言区 -->
      <div class="add-form glass-card">
        <div v-if="isLoggedIn" class="input-row">
          <img v-if="user?.avatar" :src="user.avatar" alt="" class="user-avatar" @error="$event.target.style.display='none'" />
          <div v-else class="avatar-ph">{{ (user?.nickname || '?').charAt(0) }}</div>
          <textarea v-model="newContent" placeholder="分享你的想法…" rows="3" />
        </div>
        <p v-else class="login-hint">
          请先 <button type="button" class="link-btn" @click="showAuth = true">登入</button> 后留言
        </p>
        <div v-if="isLoggedIn" class="form-actions">
          <button type="button" class="submit-btn" :disabled="loading || !newContent.trim()" @click="addMessage">
            {{ loading ? '发送中…' : '发布留言' }}
          </button>
        </div>
      </div>

      <!-- 留言列表 -->
      <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message-card glass-card">
          <div class="msg-header">
            <img v-if="msg.userAvatar" :src="msg.userAvatar" alt="" class="user-avatar" @error="$event.target.style.display='none'" />
            <div v-else class="avatar-ph">{{ (msg.userNickname || '?').charAt(0) }}</div>
            <div class="msg-info">
              <span class="username">{{ msg.userNickname || '匿名' }}</span>
              <span class="time">{{ formatDate(msg.createTime) }}</span>
            </div>
            <div class="msg-actions">
              <button type="button" class="like-btn" :class="{ liked: msg.liked }" @click="toggleMessageLike(msg)">
                {{ msg.liked ? '❤' : '🤍' }} {{ msg.likeCount || 0 }}
              </button>
              <button v-if="canDeleteMsg(msg)" type="button" class="delete-sm" @click="deleteMessage(msg.id)">删除</button>
            </div>
          </div>
          <p class="msg-content">{{ msg.content }}</p>

          <!-- 评论 -->
          <div v-if="msg.comments && msg.comments.length" class="comments">
            <div v-for="c in msg.comments" :key="c.id" class="comment">
              <div class="comment-header">
                <img v-if="c.userAvatar" :src="c.userAvatar" alt="" class="mini-avatar" @error="$event.target.style.display='none'" />
                <div v-else class="avatar-ph small">{{ (c.userNickname || '?').charAt(0) }}</div>
                <span class="username small">{{ c.userNickname }}</span>
                <span class="time">{{ formatDate(c.createTime) }}</span>
                <div class="comment-actions">
                  <button type="button" class="like-btn-sm" @click="toggleCommentLike(c)">❤ {{ c.likeCount }}</button>
                  <button type="button" class="reply-btn" @click="startReply(c)">回复</button>
                  <button v-if="canDeleteComment(c)" type="button" class="delete-sm" @click="deleteComment(c.id)">删除</button>
                </div>
              </div>
              <p class="comment-content">{{ c.content }}</p>

              <!-- 回复框 -->
              <div v-if="replyingTo?.id === c.id" class="reply-form">
                <input v-model="replyContent" placeholder="回复内容…" @keyup.enter="addComment(msg.id, c.id, c.id, replyContent)" />
                <button type="button" @click="addComment(msg.id, c.id, c.id, replyContent)">发送</button>
                <button type="button" class="cancel-btn" @click="replyingTo = null">取消</button>
              </div>

              <!-- 嵌套回复 -->
              <div v-if="c.replies && c.replies.length" class="replies">
                <div v-for="r in c.replies" :key="r.id" class="reply-item">
                  <img v-if="r.userAvatar" :src="r.userAvatar" alt="" class="mini-avatar" @error="$event.target.style.display='none'" />
                  <div v-else class="avatar-ph small">{{ (r.userNickname || '?').charAt(0) }}</div>
                  <div class="reply-body">
                    <span class="username small">{{ r.userNickname }}</span>
                    <span class="reply-content">{{ r.content }}</span>
                    <button type="button" class="like-btn-sm" @click="toggleCommentLike(r)">❤ {{ r.likeCount }}</button>
                    <button v-if="canDeleteComment(r)" type="button" class="delete-sm" @click="deleteComment(r.id)">删除</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速回复入口 -->
          <button type="button" class="quick-reply-btn" @click="startReply({ id: -msg.id, _messageId: msg.id })">
            💬 添加评论
          </button>
          <div v-if="replyingTo?.id === -msg.id" class="reply-form mt">
            <input v-model="replyContent" placeholder="写下评论…" @keyup.enter="addComment(msg.id, null, null, replyContent)" />
            <button type="button" @click="addComment(msg.id, null, null, replyContent)">发送</button>
            <button type="button" class="cancel-btn" @click="replyingTo = null">取消</button>
          </div>
        </div>
        <p v-if="!messages.length" class="empty">暂时还没有留言，来第一个吧！</p>
      </div>
    </div>
    <AuthModal v-if="showAuth" @close="showAuth = false" @success="fetchMessages" />
  </div>
</template>

<style scoped>
.page {
  position: relative;
  min-height: 100vh;
  padding: 100px 24px 80px;
  color: #fff;
}

.message-container {
  max-width: 680px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 6px;
  letter-spacing: 4px;
  font-size: 1.8rem;
  color: #fff;
}

.sub {
  text-align: center;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  margin-bottom: 28px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.add-form {
  padding: 20px;
  margin-bottom: 28px;
}

.input-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.avatar-ph {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(125, 211, 252, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #7dd3fc;
  flex-shrink: 0;
}

.avatar-ph.small { width: 28px; height: 28px; font-size: 12px; }

.input-row textarea {
  flex: 1;
  padding: 12px 16px;
  font-size: 15px;
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  resize: vertical;
  min-height: 80px;
}

.login-hint {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.link-btn {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  text-decoration: underline;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.submit-btn {
  padding: 9px 24px;
  font-size: 14px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.submit-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* 留言列表 */
.messages { display: flex; flex-direction: column; gap: 16px; }

.message-card { padding: 20px; }

.msg-header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}

.msg-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 500;
  color: #7dd3fc;
  font-size: 15px;
}

.username.small { font-size: 13px; }

.time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.msg-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  font-size: 13px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.2s;
}
.like-btn:hover { background: rgba(255, 255, 255, 0.12); }
.like-btn.liked {
  background: rgba(248, 113, 113, 0.15);
  border-color: rgba(248, 113, 113, 0.4);
  color: #f87171;
}

.delete-sm {
  padding: 4px 10px;
  font-size: 11px;
  color: #f87171;
  background: none;
  border: 1px solid rgba(248, 113, 113, 0.3);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}
.delete-sm:hover { background: rgba(248, 113, 113, 0.15); }

.msg-content {
  font-size: 15px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 14px;
  padding-left: 50px;
}

/* 评论 */
.comments {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 12px;
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment { padding-left: 50px; }

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.mini-avatar { width: 28px; height: 28px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }

.comment-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
}

.like-btn-sm {
  font-size: 12px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.55);
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 10px;
  transition: color 0.2s;
}
.like-btn-sm:hover { color: #f87171; }

.reply-btn {
  font-size: 12px;
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  padding: 2px 6px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 4px;
}

/* 回复 */
.replies { padding-left: 36px; display: flex; flex-direction: column; gap: 6px; margin-top: 6px; }
.reply-item { display: flex; gap: 8px; align-items: flex-start; }
.reply-body {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  font-size: 13px;
}
.reply-content { color: rgba(255, 255, 255, 0.7); }

/* 回复表单 */
.reply-form {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding-left: 50px;
}
.reply-form.mt { padding-left: 0; margin-top: 12px; }
.reply-form input {
  flex: 1;
  padding: 8px 12px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
}
.reply-form button {
  padding: 8px 14px;
  font-size: 13px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.cancel-btn {
  background: rgba(255, 255, 255, 0.1) !important;
}

.quick-reply-btn {
  font-size: 12px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.45);
  cursor: pointer;
  padding: 4px 0;
  transition: color 0.2s;
}
.quick-reply-btn:hover { color: #38bdf8; }

.empty {
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 14px;
  padding: 40px 0;
}

/* 日间模式 */
[data-theme="day"] .message-container h1 { color: #fff; }
[data-theme="day"] .username { color: #7dd3fc; }
</style>
