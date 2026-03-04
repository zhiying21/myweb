<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import MarkdownDocumentViewer from '@/components/MarkdownDocumentViewer.vue'
import VideoBackground from '@/components/VideoBackground.vue'
import AuthModal from '@/components/AuthModal.vue'
import { useAuth } from '@/stores/auth'
import request from '@/utils/request'

const { isLoggedIn } = useAuth()
const showAuth = ref(false)
let pendingUpload = false

const route = useRoute()
const blogList = ref([])
const currentDoc = ref(null)
const showList = computed(() => !route.query.id)
const showUpload = ref(false)
const uploadTitle = ref('')
const uploadDesc = ref('')
const uploadContent = ref('')
const uploading = ref(false)

async function fetchList() {
  try {
    const res = await request.get('/document/list', { params: { type: 'blog' } })
    blogList.value = (res && res.data) ? res.data : []
  } catch {
    blogList.value = []
  }
}

async function fetchDetail(id) {
  if (!id) return
  try {
    const res = await request.get(`/document/detail/${id}`)
    currentDoc.value = res && res.data ? res.data : null
  } catch {
    currentDoc.value = null
  }
}

watch(() => route.query.id, (id) => {
  if (id) fetchDetail(Number(id))
  else currentDoc.value = null
}, { immediate: true })

function onAuthSuccess() {
  showAuth.value = false
  fetchList()
  if (pendingUpload) {
    pendingUpload = false
    showUpload.value = true
  }
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
      type: 'blog',
      title: uploadTitle.value.trim(),
      description: uploadDesc.value.trim(),
      content: uploadContent.value.trim(),
    })
    showUpload.value = false
    uploadTitle.value = ''
    uploadDesc.value = ''
    uploadContent.value = ''
    await fetchList()
  } catch (e) {
    if (e.status === 401) showAuth.value = true
    else alert(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

function openUpload() {
  if (isLoggedIn.value) {
    showUpload.value = true
  } else {
    showAuth.value = true
    pendingUpload = true
  }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleFileUpload(e) {
  const file = e.target.files?.[0]
  if (!file || !file.name.endsWith('.md')) {
    alert('请选择 .md 文件')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    uploadContent.value = ev.target?.result || ''
    if (!uploadTitle.value) uploadTitle.value = file.name.replace(/\.md$/i, '')
  }
  reader.readAsText(file, 'UTF-8')
}
</script>

<template>
  <div class="blog-page">
    <VideoBackground />
    <div v-if="showList" class="blog-list-view">
      <h1>博客</h1>
      <p class="sub">共 {{ blogList.length }} 篇</p>
      <button type="button" class="upload-btn" @click="openUpload">上传 .md 文档</button>
      <div v-if="showUpload" class="upload-modal">
        <h3>上传博客</h3>
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
        <RouterLink
          v-for="doc in blogList"
          :key="doc.id"
          :to="{ path: '/blog', query: { id: doc.id } }"
          class="doc-item"
        >
          <div class="doc-thumb">
            <img v-if="doc.coverImage" :src="doc.coverImage" :alt="doc.title" @error="$event.target.style.display='none'" />
            <span v-else class="thumb-placeholder">📝</span>
          </div>
          <h3>{{ doc.title }}</h3>
          <p class="doc-meta">{{ doc.publisherNickname || '匿名' }}</p>
          <p>{{ doc.description }}</p>
        </RouterLink>
      </div>
    </div>
    <div v-else-if="currentDoc" class="blog-detail-view">
      <button type="button" class="back-to-top" aria-label="回到顶部" @click="scrollToTop">↑</button>
      <RouterLink to="/blog" class="back-link">← 返回博客列表</RouterLink>
      <div class="doc-header">
        <div class="doc-author">
          <img v-if="currentDoc.publisherAvatar || currentDoc.coverImage" :src="currentDoc.publisherAvatar || currentDoc.coverImage" alt="" class="author-avatar" @error="$event.target.style.display='none'" />
          <span class="author-name">{{ currentDoc.publisherNickname || '匿名' }}</span>
        </div>
        <h1>{{ currentDoc.title }}</h1>
        <p class="doc-desc">{{ currentDoc.description }}</p>
      </div>
      <div class="doc-body">
        <div class="md-viewer-inline">
          <MarkdownDocumentViewer :content="currentDoc.content" />
        </div>
      </div>
    </div>
    <AuthModal v-if="showAuth" @close="showAuth = false; pendingUpload = false" @success="onAuthSuccess" />
  </div>
</template>

<style scoped>
.blog-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 60px;
  color: #fff;
  background: transparent;
}

.blog-list-view h1 {
  text-align: center;
  margin-bottom: 8px;
  letter-spacing: 6px;
  font-size: 1.8rem;
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
  background: rgba(14, 165, 233, 0.5);
  border: 1px solid rgba(14, 165, 233, 0.8);
  border-radius: 8px;
  cursor: pointer;
}

.upload-modal {
  max-width: 480px;
  margin: 0 auto 32px;
  padding: 24px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 12px;
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
}

.upload-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.upload-actions button {
  padding: 8px 20px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.upload-actions button:last-child {
  background: #0ea5e9;
}

.doc-grid {
  max-width: 900px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

.doc-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  text-decoration: none;
  color: inherit;
}

.doc-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.doc-thumb {
  width: 100%;
  aspect-ratio: 16/10;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.doc-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-placeholder {
  font-size: 2rem;
}

.doc-item h3 {
  font-size: 1rem;
  margin-bottom: 4px;
  color: #7dd3fc;
}

.doc-meta {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 8px;
}

.doc-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.doc-item p {
  font-size: 13px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.7);
}

.blog-detail-view {
  max-width: 720px;
  margin: 0 auto;
  position: relative;
}

.back-to-top {
  position: fixed;
  right: 24px;
  top: 80px;
  z-index: 100;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s, transform 0.2s;
}

.back-to-top:hover {
  background: rgba(14, 165, 233, 0.6);
  transform: scale(1.05);
}

.back-link {
  display: inline-block;
  margin-bottom: 24px;
  color: #38bdf8;
  font-size: 14px;
  text-decoration: none;
}

.doc-header {
  margin-bottom: 32px;
}

.doc-header h1 {
  font-size: 1.6rem;
  margin-bottom: 12px;
  color: #7dd3fc;
}

.doc-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
  line-height: 1.6;
}

.doc-body {
  padding: 24px 0;
}

.md-viewer-inline {
  min-height: 200px;
}
</style>
