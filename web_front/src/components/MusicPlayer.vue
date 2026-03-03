<script setup>
import { ref } from 'vue'

const isOpen = ref(false)

const PLAYLIST_ID = '3778678'
const embedSrc = `https://music.163.com/outchain/player?type=0&id=${PLAYLIST_ID}&auto=1&height=430`

function toggle() {
  isOpen.value = !isOpen.value
}
</script>

<template>
  <div class="music-wrapper">
    <button
      class="music-btn"
      data-cursor-hover
      :class="{ active: isOpen }"
      @click="toggle"
      title="音乐"
    >
      <svg class="music-icon" :class="{ playing: isOpen }" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M9 18V5l12-2v13" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
        <circle cx="6" cy="18" r="3" stroke="currentColor" stroke-width="1.8"/>
        <circle cx="18" cy="16" r="3" stroke="currentColor" stroke-width="1.8"/>
      </svg>
    </button>

    <!-- v-show 而非 v-if，收起时 iframe 不销毁，音乐继续播放 -->
    <div class="music-panel" v-show="isOpen">
      <iframe
        :src="embedSrc"
        width="100%"
        height="430"
        frameborder="0"
        allow="autoplay; encrypted-media"
        allowfullscreen
      />
    </div>
  </div>
</template>

<style scoped>
.music-wrapper {
  position: relative;
}

.music-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.25);
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: none;
  padding: 0;
}

.music-btn:hover,
.music-btn.active {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.35);
  color: #fff;
}

.music-icon {
  width: 18px;
  height: 18px;
}

.music-icon.playing {
  animation: note-bounce 1.5s ease-in-out infinite;
}

@keyframes note-bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.12) rotate(5deg); }
}

.music-panel {
  position: absolute;
  top: 44px;
  right: 0;
  width: 280px;
  background: rgba(15, 15, 20, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.music-panel iframe {
  display: block;
  border-radius: 8px;
}
</style>
