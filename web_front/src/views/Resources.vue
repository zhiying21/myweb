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
      <h1>资源分享</h1>
      <p class="sub">点击图标下载</p>
      <div class="resource-grid">
        <div
          v-for="item in resources"
          :key="item.id"
          class="resource-item"
          @click="download(item)"
        >
          <div class="icon-wrap">
            <img v-if="item.icon" :src="item.icon" :alt="item.name" class="icon" />
            <span v-else class="icon-placeholder">{{ item.name?.charAt(0) || '?' }}</span>
          </div>
          <span class="name">{{ item.name }}</span>
        </div>
      </div>
      <p v-if="!resources.length" class="empty">暂无资源</p>
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
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 8px;
  letter-spacing: 4px;
  color: #fff;
  text-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}

.sub {
  text-align: center;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 40px;
  font-size: 14px;
  text-shadow: 0 0 12px rgba(0, 0, 0, 0.25);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 24px;
}

.resource-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.resource-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
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
}

.icon-placeholder {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #38bdf8;
  background: rgba(56, 189, 248, 0.2);
  border-radius: 12px;
}

.name {
  font-size: 15px;
  font-weight: 600;
  text-align: center;
  color: #fff;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.empty {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 40px;
}

[data-theme="day"] .resources-container h1,
[data-theme="day"] .resources-container .name {
  color: #7dd3fc;
  text-shadow: none;
}

[data-theme="day"] .resources-container .sub,
[data-theme="day"] .resources-container .empty {
  color: #7dd3fc;
}
</style>
