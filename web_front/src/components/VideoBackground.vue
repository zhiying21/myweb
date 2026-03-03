<script setup>
defineProps({
  src: { type: String, default: '/video/2_duro.mp4' },
})
</script>

<template>
  <video
    class="video-bg-fixed"
    autoplay
    muted
    loop
    playsinline
    preload="auto"
  >
    <source :src="src" type="video/mp4" />
  </video>
  <div class="video-overlay-fixed" />
</template>

<style scoped>
.video-bg-fixed {
  position: fixed;
  top: 50%;
  left: 50%;
  min-width: 100%;
  min-height: 100%;
  width: auto;
  height: auto;
  transform: translate(-50%, -50%);
  object-fit: cover;
  z-index: -2;
}

.video-overlay-fixed {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: -1;
  pointer-events: none;
}
</style>
