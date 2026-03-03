<script setup>
import { computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import MarkdownDocumentViewer from '@/components/MarkdownDocumentViewer.vue'
import VideoBackground from '@/components/VideoBackground.vue'
import { notesList } from '@/data/documents'

const route = useRoute()
const docPath = computed(() => route.query.path || '')
const currentDoc = computed(() => notesList.find((d) => d.path === docPath.value))
const showList = computed(() => !docPath.value)
</script>

<template>
  <div class="notes-page">
    <VideoBackground />
    <div v-if="showList" class="notes-list-view">
      <h1>笔记</h1>
      <p class="sub">共 {{ notesList.length }} 篇</p>
      <div class="doc-grid">
        <RouterLink
          v-for="(doc, i) in notesList"
          :key="doc.path"
          :to="{ path: '/notes', query: { path: doc.path } }"
          class="doc-item"
        >
          <div class="doc-thumb">
            <img :src="doc.image" :alt="doc.title" @error="$event.target.src='/vite.svg'" />
          </div>
          <h3>{{ doc.title }}</h3>
          <p>{{ doc.description }}</p>
        </RouterLink>
      </div>
    </div>
    <div v-else-if="currentDoc" class="notes-detail-view">
      <RouterLink to="/notes" class="back-link">← 返回笔记列表</RouterLink>
      <div class="doc-header">
        <img :src="currentDoc.image" :alt="currentDoc.title" class="doc-cover" @error="$event.target.src='/vite.svg'" />
        <h1>{{ currentDoc.title }}</h1>
        <p class="doc-desc">{{ currentDoc.description }}</p>
      </div>
      <div class="doc-body">
        <MarkdownDocumentViewer :path="docPath" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.notes-page {
  position: relative;
  min-height: 100vh;
  padding: 80px 24px 60px;
  color: #fff;
  background: transparent;
  transition: color 0.5s ease;
}

:global([data-theme="day"]) .notes-page {
  color: #222;
}

.notes-list-view h1 {
  text-align: center;
  margin-bottom: 8px;
  letter-spacing: 6px;
  font-size: 1.8rem;
}

.sub {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 40px;
  font-size: 14px;
}

:global([data-theme="day"]) .sub {
  color: rgba(0, 0, 0, 0.5);
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
}

.doc-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doc-item h3 {
  font-size: 1rem;
  margin-bottom: 8px;
  color: #7dd3fc;
}

:global([data-theme="day"]) .doc-item h3 {
  color: #0284c7;
}

.doc-item p {
  font-size: 13px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.7);
}

:global([data-theme="day"]) .doc-item p {
  color: rgba(0, 0, 0, 0.6);
}

.notes-detail-view {
  max-width: 720px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  margin-bottom: 24px;
  color: #38bdf8;
  font-size: 14px;
  text-decoration: none;
}

.back-link:hover {
  text-decoration: underline;
}

.doc-header {
  margin-bottom: 32px;
}

.doc-cover {
  width: 100%;
  max-height: 280px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 20px;
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
</style>
