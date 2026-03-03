<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const atBottom = ref(false)

function checkScroll() {
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  const sections = document.querySelectorAll('.scroll-section')
  const lastSection = sections.length ? sections[sections.length - 1] : null
  const onLastPage = lastSection ? lastSection.getBoundingClientRect().top <= clientHeight * 0.6 : false
  atBottom.value = scrollTop + clientHeight >= scrollHeight - 60 || onLastPage
}

onMounted(() => {
  window.addEventListener('scroll', checkScroll, { passive: true })
  checkScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', checkScroll)
})
</script>

<template>
  <Transition name="fade">
    <div v-show="!atBottom" class="scroll-hint">
      <span class="scroll-text">向下滑动</span>
      <span class="scroll-arrow">↓</span>
    </div>
  </Transition>
</template>

<style scoped>
.scroll-hint {
  position: fixed;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.78);
  pointer-events: none;
  animation: bounce 2s ease-in-out infinite;
}

.scroll-text { font-size: 12px; letter-spacing: 3px; }
.scroll-arrow { font-size: 18px; opacity: 0.85; }

@keyframes bounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(6px); }
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
