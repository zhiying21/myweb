<script setup>
import VideoBackground from '@/components/VideoBackground.vue'
</script>

<template>
  <div class="page">
    <VideoBackground />
    <h1>留言板</h1>
    <p>建设中…</p>
  </div>
</template>

<style scoped>
.page {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: #fff;
  background: transparent;
  transition: color 0.5s ease;
}
h1 { margin-bottom: 1rem; letter-spacing: 4px; }
p { color: rgba(255,255,255,0.5); }
:global([data-theme="day"]) .page { color: #222; }
:global([data-theme="day"]) .page p { color: rgba(0,0,0,0.45); }
</style>
