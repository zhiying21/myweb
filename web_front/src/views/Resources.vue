<script setup>
</script>

<template>
  <div class="page">
    <h1>资源分享</h1>
    <p>建设中…</p>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: #fff;
  background: #111;
  transition: background 0.5s ease, color 0.5s ease;
}
h1 { margin-bottom: 1rem; letter-spacing: 4px; }
p { color: rgba(255,255,255,0.5); }
:global([data-theme="day"]) .page { background: #f0f0f0; color: #222; }
:global([data-theme="day"]) .page p { color: rgba(0,0,0,0.45); }
</style>
