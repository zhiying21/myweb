<script setup>
import { ref, watch, onMounted } from 'vue'

const STORAGE_KEY = 'zhiying-theme'
const isDark = ref(localStorage.getItem(STORAGE_KEY) !== 'day')

function toggle() {
  isDark.value = !isDark.value
}

watch(isDark, (dark) => {
  document.documentElement.setAttribute('data-theme', dark ? 'night' : 'day')
  localStorage.setItem(STORAGE_KEY, dark ? 'night' : 'day')
}, { immediate: true })

onMounted(() => {
  document.documentElement.setAttribute('data-theme', isDark.value ? 'night' : 'day')
})
</script>

<template>
  <button
    class="theme-toggle"
    data-cursor-hover
    :class="{ day: !isDark }"
    @click="toggle"
    :title="isDark ? '切换到白天' : '切换到夜晚'"
  >
    <div class="toggle-track">
      <div class="toggle-thumb">
        <!-- 太阳 -->
        <svg v-if="!isDark" class="icon sun" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="5" fill="#FFB300" stroke="#FF8F00" stroke-width="1"/>
          <g stroke="#FFB300" stroke-width="2" stroke-linecap="round">
            <line x1="12" y1="1" x2="12" y2="4"/>
            <line x1="12" y1="20" x2="12" y2="23"/>
            <line x1="1" y1="12" x2="4" y2="12"/>
            <line x1="20" y1="12" x2="23" y2="12"/>
            <line x1="4.2" y1="4.2" x2="6.3" y2="6.3"/>
            <line x1="17.7" y1="17.7" x2="19.8" y2="19.8"/>
            <line x1="4.2" y1="19.8" x2="6.3" y2="17.7"/>
            <line x1="17.7" y1="6.3" x2="19.8" y2="4.2"/>
          </g>
        </svg>
        <!-- 月亮 -->
        <svg v-else class="icon moon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z"
            fill="#B0BEC5"
            stroke="#78909C"
            stroke-width="1"
            stroke-linejoin="round"
          />
          <circle cx="9.5" cy="8" r="0.8" fill="#78909C"/>
          <circle cx="13" cy="14" r="0.6" fill="#78909C"/>
          <circle cx="8" cy="14" r="1" fill="#78909C"/>
        </svg>
      </div>
    </div>
  </button>
</template>

<style scoped>
.theme-toggle {
  position: relative;
  background: none;
  border: none;
  padding: 0;
  outline: none;
  cursor: none;
}

.toggle-track {
  width: 52px;
  height: 28px;
  border-radius: 14px;
  background: rgba(30, 30, 50, 0.6);
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  position: relative;
  transition: background 0.4s ease, border-color 0.4s ease;
}

.day .toggle-track {
  background: rgba(255, 200, 50, 0.25);
  border-color: rgba(255, 180, 0, 0.35);
}

.toggle-thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(20, 20, 40, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1), background 0.35s ease;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.day .toggle-thumb {
  transform: translateX(24px);
  background: rgba(255, 220, 80, 0.85);
}

.icon {
  width: 16px;
  height: 16px;
}

.sun {
  animation: sun-spin 10s linear infinite;
}

@keyframes sun-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
