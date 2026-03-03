<template>
  <!-- 这是一个空组件，只提供样式覆盖 -->
</template>

<style>
/* 覆盖默认样式：从左下角移到右下角 */
#waifu {
  position: fixed !important;
  bottom: 0 !important;
  right: 0 !important;
  left: auto !important;
  z-index: 999 !important;
}

#waifu-tool {
  position: absolute;
  right: 10px;
  top: 0;
  left: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  pointer-events: auto;
  cursor: none;
}

#waifu-tool span {
  color: rgba(255, 255, 255, 0.5);
  cursor: none;
  transition: color 0.2s;
}

#waifu-tool span:hover {
  color: #fff;
}
</style>