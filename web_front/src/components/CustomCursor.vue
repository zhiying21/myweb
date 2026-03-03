<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const dotX = ref(-100)
const dotY = ref(-100)
const ringX = ref(-100)
const ringY = ref(-100)
const isHover = ref(false)

let mouseX = -100
let mouseY = -100
let rafId = null

const LERP = 0.15

function onMove(e) {
  mouseX = e.clientX
  mouseY = e.clientY
  dotX.value = mouseX
  dotY.value = mouseY
}

function animate() {
  ringX.value += (mouseX - ringX.value) * LERP
  ringY.value += (mouseY - ringY.value) * LERP
  rafId = requestAnimationFrame(animate)
}

function onEnter(e) {
  if (e.target.closest('a, button, [data-cursor-hover]')) isHover.value = true
}
function onLeave(e) {
  if (e.target.closest('a, button, [data-cursor-hover]')) isHover.value = false
}

onMounted(() => {
  window.addEventListener('mousemove', onMove)
  document.addEventListener('mouseover', onEnter)
  document.addEventListener('mouseout', onLeave)
  rafId = requestAnimationFrame(animate)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMove)
  document.removeEventListener('mouseover', onEnter)
  document.removeEventListener('mouseout', onLeave)
  cancelAnimationFrame(rafId)
})
</script>

<template>
  <!-- 中心小圆点：精确跟随 -->
  <div
    class="cursor-dot"
    :style="{ left: dotX + 'px', top: dotY + 'px' }"
  />
  <!-- 外圈大圆环：缓动跟随 -->
  <div
    class="cursor-ring"
    :class="{ hover: isHover }"
    :style="{ left: ringX + 'px', top: ringY + 'px' }"
  />
</template>

<style scoped>
.cursor-dot {
  position: fixed;
  z-index: 100000;
  pointer-events: none;
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.cursor-ring {
  position: fixed;
  z-index: 99999;
  pointer-events: none;
  width: 36px;
  height: 36px;
  border: 1.5px solid rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  background: transparent;
  transform: translate(-50%, -50%);
  transition:
    width 0.3s cubic-bezier(0.25, 0.8, 0.25, 1),
    height 0.3s cubic-bezier(0.25, 0.8, 0.25, 1),
    border-color 0.3s ease,
    background 0.3s ease;
}

.cursor-ring.hover {
  width: 54px;
  height: 54px;
  border-color: rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.08);
}
</style>
