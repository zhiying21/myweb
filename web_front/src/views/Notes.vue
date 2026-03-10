<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import MarkdownDocumentViewer from '@/components/MarkdownDocumentViewer.vue'
import VideoBackground from '@/components/VideoBackground.vue'
import AuthModal from '@/components/AuthModal.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { isLoggedIn, user } = useAuth()
const showAuth = ref(false)
let pendingUpload = false

const route = useRoute()
const router = useRouter()
const notesList = ref([])
const currentDoc = ref(null)
const comments = ref([])
const showList = computed(() => !route.query.id)
const showUpload = ref(false)
const uploadTitle = ref('')
const uploadDesc = ref('')
const uploadContent = ref('')
const uploading = ref(false)
const newComment = ref('')
const submittingComment = ref(false)

async function fetchList() {
  try {
    const res = await request.get('/document/list', { params: { type: 'notes' } })
    notesList.value = (res && res.data) ? res.data : (Array.isArray(res) ? res : [])
  } catch {
    notesList.value = []
  }
}

async function fetchDetail(id) {
  if (!id) return
  try {
    const res = await request.get(`/document/detail/${id}`)
    currentDoc.value = (res && res.data) ? res.data : null
    await fetchComments(id)
  } catch {
    currentDoc.value = null
  }
}

async function fetchComments(id) {
  try {
    const res = await request.get(`/document/${id}/comments`)
    comments.value = (res && res.data) ? res.data : []
  } catch {
    comments.value = []
  }
}

watch(() => route.query.id, (id) => {
  if (id) fetchDetail(Number(id))
  else { currentDoc.value = null; comments.value = [] }
}, { immediate: true })

function onAuthSuccess() {
  showAuth.value = false
  fetchList()
  if (pendingUpload) { pendingUpload = false; showUpload.value = true }
}

onMounted(fetchList)

async function submitUpload() {
  if (!uploadTitle.value.trim() || !uploadContent.value.trim()) {
    alert('标题和内容不能为空')
    return
  }
  uploading.value = true
  try {
    await request.post('/document/upload', {
      type: 'notes',
      title: uploadTitle.value.trim(),
      description: uploadDesc.value.trim(),
      content: uploadContent.value.trim(),
    })
    showUpload.value = false
    uploadTitle.value = ''; uploadDesc.value = ''; uploadContent.value = ''
    await fetchList()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
    else alert(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

function openUpload() {
  if (isLoggedIn.value) showUpload.value = true
  else { showAuth.value = true; pendingUpload = true }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function openDoc(id) {
  router.push({ path: '/notes', query: { id } })
}

function handleFileUpload(e) {
  const file = e.target.files?.[0]
  if (!file || !file.name.endsWith('.md')) { alert('请选择 .md 文件'); return }
  const reader = new FileReader()
  reader.onload = (ev) => {
    uploadContent.value = ev.target?.result || ''
    if (!uploadTitle.value) uploadTitle.value = file.name.replace(/\.md$/i, '')
  }
  reader.readAsText(file, 'UTF-8')
}

async function deleteDoc(id) {
  if (!confirm('确定要删除这篇笔记吗？')) return
  try {
    await request.delete(`/document/${id}`)
    notesList.value = notesList.value.filter(d => d.id !== id)
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

async function toggleLike(docId) {
  if (!isLoggedIn.value) { showAuth.value = true; return }
  try {
    const res = await request.post(`/document/${docId}/like`)
    const d = res?.data ?? res
    if (currentDoc.value && currentDoc.value.id === docId) {
      currentDoc.value.likeCount = d.likeCount
      currentDoc.value.liked = d.liked
    }
    const idx = notesList.value.findIndex(n => n.id === docId)
    if (idx !== -1) notesList.value[idx].likeCount = d.likeCount
  } catch (e) {
    if (e.status === 401) showAuth.value = true
  }
}

async function submitComment() {
  if (!newComment.value.trim()) return
  if (!isLoggedIn.value) { showAuth.value = true; return }
  submittingComment.value = true
  try {
    await request.post(`/document/${currentDoc.value.id}/comments`, { content: newComment.value.trim() })
    newComment.value = ''
    await fetchComments(currentDoc.value.id)
  } catch (e) {
    if (e.status === 401) showAuth.value = true
    else alert(e.message || '评论失败')
  } finally {
    submittingComment.value = false
  }
}

async function deleteComment(commentId) {
  if (!confirm('删除评论？')) return
  try {
    await request.delete(`/document/comments/${commentId}`)
    await fetchComments(currentDoc.value.id)
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

function canDelete(doc) {
  return isLoggedIn.value && (user.value?.role === 'ADMIN' || doc.userId === user.value?.userId)
}
function canDeleteComment(c) {
  return isLoggedIn.value && (user.value?.role === 'ADMIN' || c.userId === user.value?.userId)
}

function formatDate(t) {
  if (!t) return ''
  return new Date(t).toLocaleDateString('zh-CN')
}
</script>

<template>
  <div class="notes-page">
    <VideoBackground />
    <div v-if="showList" class="notes-list-view">
      <h1>笔记</h1>
      <p class="sub">共 {{ notesList.length }} 篇</p>
      <button type="button" class="upload-btn" @click="openUpload">上传 .md 文档</button>
      <div v-if="showUpload" class="upload-modal glass-card">
        <h3>上传笔记</h3>
        <input v-model="uploadTitle" placeholder="标题" />
        <input v-model="uploadDesc" placeholder="描述（可选）" />
        <input type="file" accept=".md" @change="handleFileUpload" />
        <textarea v-model="uploadContent" placeholder="或直接粘贴 Markdown 内容" rows="10" />
        <div class="upload-actions">
          <button type="button" @click="showUpload = false">取消</button>
          <button type="button" :disabled="uploading" @click="submitUpload">提交</button>
        </div>
      </div>
      <div class="doc-grid">
        <div v-for="doc in notesList" :key="doc.id" class="doc-item glass-card" @click="openDoc(doc.id)">
          <div class="doc-thumb">
            <img v-if="doc.publisherAvatar || doc.coverImage" :src="doc.publisherAvatar || doc.coverImage" :alt="doc.title" @error="$event.target.style.display='none'" />
            <span v-else class="thumb-placeholder">📓</span>
          </div>
          <h3>{{ doc.title }}</h3>
          <div class="doc-author-row">
            <span class="doc-meta">{{ doc.publisherNickname || '匿名' }}</span>
            <span class="doc-date">{{ formatDate(doc.createTime) }}</span>
          </div>
          <p class="doc-desc">{{ doc.description }}</p>
          <div class="doc-stats">
            <span class="stat-icon">👁 {{ doc.viewCount || 0 }}</span>
            <span class="stat-icon">❤ {{ doc.likeCount || 0 }}</span>
            <button v-if="canDelete(doc)" type="button" class="delete-btn" @click.stop="deleteDoc(doc.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentDoc" class="notes-detail-view">
      <button type="button" class="back-to-top" aria-label="回到顶部" @click="scrollToTop">↑</button>
      <RouterLink to="/notes" class="back-link">← 返回笔记列表</RouterLink>
      <div class="doc-header glass-card">
        <div class="doc-author">
          <img v-if="currentDoc.publisherAvatar" :src="currentDoc.publisherAvatar" alt="" class="author-avatar" @error="$event.target.style.display='none'" />
          <span class="author-name">{{ currentDoc.publisherNickname || '匿名' }}</span>
          <span class="doc-date">{{ formatDate(currentDoc.createTime) }}</span>
        </div>
        <h1>{{ currentDoc.title }}</h1>
        <p v-if="currentDoc.description" class="doc-desc">{{ currentDoc.description }}</p>
        <div class="doc-stats-row">
          <span class="stat-icon">👁 {{ currentDoc.viewCount || 0 }} 浏览</span>
          <button type="button" class="like-btn-big" :class="{ liked: currentDoc.liked }" @click="toggleLike(currentDoc.id)">
            {{ currentDoc.liked ? '❤' : '🤍' }} {{ currentDoc.likeCount || 0 }} 点赞
          </button>
          <button v-if="canDelete(currentDoc)" type="button" class="delete-btn" @click="deleteDoc(currentDoc.id)">删除</button>
        </div>
      </div>
      <div class="doc-body glass-card">
        <MarkdownDocumentViewer :content="currentDoc.content" />
      </div>

      <!-- 评论区 -->
      <div class="comment-section glass-card">
        <h3>评论 <span class="comment-count">{{ comments.length }}</span></h3>
        <div v-if="isLoggedIn" class="comment-form">
          <div class="comment-input-row">
            <img v-if="user?.avatar" :src="user.avatar" alt="" class="mini-avatar" @error="$event.target.style.display='none'" />
            <textarea v-model="newComment" placeholder="写下你的想法…" rows="2" />
          </div>
          <button type="button" class="submit-btn" :disabled="submittingComment" @click="submitComment">
            {{ submittingComment ? '发布中…' : '发布评论' }}
          </button>
        </div>
        <p v-else class="login-hint">
          <button type="button" class="link-btn" @click="showAuth = true">登入</button> 后参与评论
        </p>
        <div class="comments-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <img v-if="c.userAvatar" :src="c.userAvatar" alt="" class="mini-avatar" @error="$event.target.style.display='none'" />
            <div v-else class="avatar-placeholder">{{ (c.userNickname || '?').charAt(0) }}</div>
            <div class="comment-body">
              <div class="comment-meta">
                <span class="comment-author">{{ c.userNickname || '匿名' }}</span>
                <span class="comment-time">{{ formatDate(c.createTime) }}</span>
                <button v-if="canDeleteComment(c)" type="button" class="delete-sm" @click="deleteComment(c.id)">删除</button>
              </div>
              <p class="comment-content">{{ c.content }}</p>
            </div>
          </div>
          <p v-if="!comments.length" class="empty-comment">暂无评论，来留下第一条吧</p>
        </div>
      </div>
    </div>
    <AuthModal v-if="showAuth" @close="showAuth = false; pendingUpload = false" @success="onAuthSuccess" />
  </div>
</template>

<style scoped>
.notes-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 60px;
  color: #fff;
  background: transparent;
}

.glass-card {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.notes-list-view h1 {
  text-align: center;
  margin-bottom: 8px;
  letter-spacing: 6px;
  font-size: 1.8rem;
  color: #fff;
}

.sub {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 24px;
  font-size: 14px;
}

.upload-btn {
  display: block;
  margin: 0 auto 32px;
  padding: 10px 24px;
  font-size: 14px;
  color: #fff;
  background: rgba(14, 165, 233, 0.4);
  border: 1px solid rgba(14, 165, 233, 0.6);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.upload-btn:hover { background: rgba(14, 165, 233, 0.6); }

.upload-modal {
  max-width: 480px;
  margin: 0 auto 32px;
  padding: 24px;
}
.upload-modal h3 { margin-bottom: 16px; }
.upload-modal input,
.upload-modal textarea {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  box-sizing: border-box;
}
.upload-modal textarea { resize: vertical; }

.upload-actions { display: flex; gap: 12px; margin-top: 8px; }
.upload-actions button {
  padding: 8px 20px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.upload-actions button:last-child { background: #0ea5e9; }

.doc-grid {
  max-width: 960px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.doc-item {
  padding: 20px;
  transition: all 0.3s ease;
}
.doc-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.doc-thumb {
  position: relative;
  width: 100%;
  aspect-ratio: 1/1;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 14px;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}
.doc-thumb img { width: 100%; height: 100%; object-fit: cover; }
.thumb-placeholder { font-size: 2rem; }

.doc-item h3 { font-size: 1rem; margin-bottom: 6px; color: #7dd3fc; }

.doc-author-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.mini-avatar {
  width: 24px; height: 24px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.doc-meta { font-size: 12px; color: rgba(255, 255, 255, 0.6); }
.doc-date { font-size: 11px; color: rgba(255, 255, 255, 0.4); margin-left: auto; }

.doc-desc {
  font-size: 13px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.65);
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.doc-stats { display: flex; align-items: center; gap: 12px; font-size: 12px; color: rgba(255, 255, 255, 0.5); }
.stat-icon { letter-spacing: 1px; }

.delete-btn {
  margin-left: auto;
  padding: 3px 10px;
  font-size: 11px;
  color: #f87171;
  background: rgba(248, 113, 113, 0.1);
  border: 1px solid rgba(248, 113, 113, 0.3);
  border-radius: 4px;
  cursor: pointer;
}
.delete-btn:hover { background: rgba(248, 113, 113, 0.25); }

.notes-detail-view { max-width: 760px; margin: 0 auto; }

.back-to-top {
  position: fixed;
  right: 24px; top: 80px;
  z-index: 100;
  width: 44px; height: 44px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.back-to-top:hover { background: rgba(14, 165, 233, 0.6); }

.back-link {
  display: inline-block;
  margin-bottom: 20px;
  color: #38bdf8;
  font-size: 14px;
  text-decoration: none;
}

.doc-header { padding: 24px; margin-bottom: 20px; }

.doc-author { display: flex; align-items: center; gap: 8px; margin-bottom: 14px; }
.author-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.author-name { font-size: 14px; color: rgba(255, 255, 255, 0.8); }

.doc-header h1 { font-size: 1.6rem; margin-bottom: 10px; color: #7dd3fc; }

.doc-stats-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 14px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.like-btn-big {
  padding: 6px 16px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.2s;
}
.like-btn-big.liked {
  background: rgba(248, 113, 113, 0.2);
  border-color: rgba(248, 113, 113, 0.5);
  color: #f87171;
}

.doc-body { padding: 28px; margin-bottom: 20px; }

.comment-section { padding: 24px; margin-bottom: 40px; }
.comment-section h3 { font-size: 1.1rem; margin-bottom: 16px; color: #7dd3fc; }
.comment-count { font-size: 13px; color: rgba(255, 255, 255, 0.5); margin-left: 6px; }

.comment-form { margin-bottom: 20px; }
.comment-input-row { display: flex; gap: 10px; align-items: flex-start; margin-bottom: 10px; }
.comment-input-row textarea {
  flex: 1;
  padding: 10px 14px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  resize: none;
}

.submit-btn {
  padding: 8px 20px;
  font-size: 14px;
  color: #fff;
  background: #0ea5e9;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.submit-btn:disabled { opacity: 0.6; }

.login-hint { font-size: 14px; color: rgba(255, 255, 255, 0.6); margin-bottom: 16px; }
.link-btn { background: none; border: none; color: #38bdf8; cursor: pointer; text-decoration: underline; }

.comments-list { display: flex; flex-direction: column; gap: 14px; }

.comment-item { display: flex; gap: 10px; align-items: flex-start; }
.avatar-placeholder {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: rgba(125, 211, 252, 0.2);
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 600;
  color: #7dd3fc;
  flex-shrink: 0;
}

.comment-body { flex: 1; }
.comment-meta { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; font-size: 13px; }
.comment-author { color: #7dd3fc; font-weight: 500; }
.comment-time { color: rgba(255, 255, 255, 0.4); font-size: 12px; }
.delete-sm {
  margin-left: auto;
  padding: 2px 8px;
  font-size: 11px;
  color: #f87171;
  background: none;
  border: 1px solid rgba(248, 113, 113, 0.3);
  border-radius: 4px;
  cursor: pointer;
}
.comment-content { font-size: 14px; line-height: 1.6; color: rgba(255, 255, 255, 0.8); }
.empty-comment { text-align: center; color: rgba(255, 255, 255, 0.4); font-size: 13px; padding: 20px 0; }
</style>
