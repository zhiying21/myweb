<script setup>
import { ref, onMounted } from 'vue'
import VideoBackground from '@/components/VideoBackground.vue'
import request from '@/utils/request'

const resources = ref([])

async function fetchResources() {
  try {
    const res = await request.get('/resource/list')
    resources.value = (res && res.data) ? res.data : []
  } catch {
    resources.value = []
  }
}

function download(item) {
  if (item.link) window.open(item.link, '_blank')
}

onMounted(fetchResources)
</script>

<template>
  <div class="page">
    <VideoBackground />
    <div class="resources-container">
      <div class="page-header glass-card">
        <h1>资源分享</h1>
        <p class="sub">精选工具与资源，点击下载</p>
      </div>
      <div class="resource-grid">
        <div
          v-for="item in resources"
          :key="item.id"
          class="resource-item glass-card"
          @click="download(item)"
        >
          <div class="icon-wrap">
            <img v-if="item.icon" :src="item.icon" :alt="item.name" class="icon" />
            <span v-else class="icon-placeholder">{{ item.name?.charAt(0) || '?' }}</span>
          </div>
          <span class="name">{{ item.name }}</span>
          <span class="download-hint">点击下载 ↓</span>
        </div>
      </div>
      <div v-if="!resources.length" class="empty glass-card">
        <p>🔍 暂无资源，管理员尚未上传</p>
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

.resources-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  padding: 32px 40px 28px;
  margin-bottom: 36px;
  text-align: center;
}

h1 {
  font-size: 2rem;
  margin-bottom: 8px;
  letter-spacing: 4px;
  color: #fff;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
}

.sub {
  color: rgba(186, 230, 253, 0.85);
  font-size: 14px;
  letter-spacing: 1px;
  margin: 0;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 20px;
}

.resource-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 28px 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.resource-item:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(56, 189, 248, 0.4);
  transform: translateY(-5px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.35);
}

.resource-item:hover .download-hint {
  opacity: 1;
}

.icon-wrap {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

.icon-placeholder {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 700;
  color: #38bdf8;
  background: rgba(56, 189, 248, 0.15);
  border: 1px solid rgba(56, 189, 248, 0.3);
  border-radius: 14px;
}

.name {
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  color: #fff;
  line-height: 1.4;
}

.download-hint {
  font-size: 12px;
  color: rgba(186, 230, 253, 0.7);
  opacity: 0;
  transition: opacity 0.2s;
}

.empty {
  padding: 60px 40px;
  text-align: center;
  margin-top: 12px;
}

.empty p {
  color: rgba(186, 230, 253, 0.7);
  font-size: 15px;
}

/* 日间模式 */
[data-theme="day"] h1 {
  color: #fff;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
}

[data-theme="day"] .sub,
[data-theme="day"] .empty p,
[data-theme="day"] .download-hint {
  color: rgba(255, 255, 255, 0.75);
}

[data-theme="day"] .name {
  color: #fff;
}

[data-theme="day"] .resource-item:hover {
  background: rgba(255, 255, 255, 0.2);
}
</style>
