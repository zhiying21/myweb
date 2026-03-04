<script setup>
import { ref, watch, computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const props = defineProps({
  path: { type: String, default: '' },
  content: { type: String, default: '' },
})

const content = ref('')
const loading = ref(false)
const error = ref(null)

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (_) {}
    }
    return ''
  },
})

/** 移除 <font> 标签，保留文本内容，避免渲染失败或低对比度 */
function stripFontTags(text) {
  if (!text) return ''
  return text.replace(/<font[^>]*>/gi, '').replace(/<\/font>/gi, '')
}

const htmlContent = computed(() => {
  const raw = content.value
  if (!raw) return ''
  return md.render(stripFontTags(raw))
})

async function fetchContent() {
  if (props.content) {
    content.value = props.content
    return
  }
  if (!props.path) {
    content.value = ''
    return
  }
  loading.value = true
  error.value = null
  try {
    const res = await fetch(props.path)
    if (!res.ok) throw new Error('加载失败')
    content.value = await res.text()
  } catch (e) {
    error.value = e.message
    content.value = ''
  } finally {
    loading.value = false
  }
}

watch([() => props.path, () => props.content], fetchContent, { immediate: true })
</script>

<template>
  <div class="md-viewer">
    <div v-if="loading" class="md-loading">加载中…</div>
    <div v-else-if="error" class="md-error">{{ error }}</div>
    <div
      v-else
      class="md-body"
      v-html="htmlContent"
    />
  </div>
</template>

<style scoped>
.md-viewer {
  min-height: 120px;
}

.md-loading,
.md-error {
  padding: 24px;
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.md-error {
  color: #f87171;
}

.md-body {
  font-size: 14px;
  line-height: 1.75;
  color: #e2e8f0;
}

/* 夜间模式：高对比度浅色文字 */
.md-body :deep(p),
.md-body :deep(li),
.md-body :deep(td),
.md-body :deep(th),
.md-body :deep(span) {
  color: inherit;
}

/* 日间模式：高对比度深色文字 */
[data-theme="day"] .md-body {
  color: #1e293b;
}

[data-theme="day"] .md-body :deep(blockquote) {
  color: #334155;
}

/* 覆盖可能残留的 font 标签，统一使用高对比度颜色 */
.md-body :deep(font) {
  color: inherit !important;
  background-color: transparent !important;
}

.md-body :deep(h1),
.md-body :deep(h2),
.md-body :deep(h3),
.md-body :deep(h4) {
  margin-top: 1.2em;
  margin-bottom: 0.6em;
  font-weight: 600;
  color: #7dd3fc;
}

.md-body :deep(h1) { font-size: 1.4em; }
.md-body :deep(h2) { font-size: 1.2em; }
.md-body :deep(h3) { font-size: 1.1em; }

.md-body :deep(p) {
  margin-bottom: 0.8em;
}

.md-body :deep(ul),
.md-body :deep(ol) {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.md-body :deep(li) {
  margin-bottom: 0.3em;
}

.md-body :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid rgba(167, 243, 208, 0.5);
  background: rgba(167, 243, 208, 0.08);
  color: rgba(255, 255, 255, 0.85);
}

.md-body :deep(code) {
  padding: 0.2em 0.4em;
  font-size: 0.9em;
  background: rgba(0, 0, 0, 0.35);
  border-radius: 4px;
  color: #e2e8f0;
}

.md-body :deep(pre) {
  margin: 1em 0;
  padding: 1em;
  overflow-x: auto;
  border-radius: 8px;
  background: #1e293b;
}

.md-body :deep(pre code) {
  padding: 0;
  background: none;
  color: #e2e8f0;
}

.md-body :deep(a) {
  color: #38bdf8;
  text-decoration: none;
}

.md-body :deep(a:hover) {
  text-decoration: underline;
}

.md-body :deep(hr) {
  border: none;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  margin: 1.5em 0;
}

.md-body :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 1em 0;
}

.md-body :deep(th),
.md-body :deep(td) {
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 0.5em 0.75em;
  text-align: left;
}

[data-theme="day"] .md-body :deep(th),
[data-theme="day"] .md-body :deep(td) {
  border-color: rgba(0, 0, 0, 0.15);
}

[data-theme="day"] .md-body :deep(h1),
[data-theme="day"] .md-body :deep(h2),
[data-theme="day"] .md-body :deep(h3),
[data-theme="day"] .md-body :deep(h4) {
  color: #0ea5e9;
}

[data-theme="day"] .md-body :deep(code) {
  color: #0c4a6e;
  background: rgba(14, 165, 233, 0.12);
}

[data-theme="day"] .md-body :deep(pre) {
  background: #f1f5f9;
}

[data-theme="day"] .md-body :deep(pre code) {
  color: #0f172a;
}

[data-theme="day"] .md-body :deep(a) {
  color: #0ea5e9;
}
</style>
