import { onMounted, onUnmounted } from 'vue'

const COOLDOWN_MS = 600

export function useScrollSection() {
  let cooldown = false
  let raf = null

  function getSections() {
    return [...document.querySelectorAll('.scroll-section')]
  }

  function getCurrentIndex() {
    const sections = getSections()
    if (!sections.length) return 0
    const vh = window.innerHeight
    let best = 0
    let bestDist = Infinity
    sections.forEach((el, i) => {
      const top = el.getBoundingClientRect().top
      const dist = Math.abs(top)
      if (dist < bestDist) {
        bestDist = dist
        best = i
      }
    })
    return best
  }

  function scrollToSection(index) {
    const sections = getSections()
    if (index < 0 || index >= sections.length) return
    sections[index].scrollIntoView({ behavior: 'smooth', block: 'start' })
    cooldown = true
    setTimeout(() => { cooldown = false }, COOLDOWN_MS)
  }

  function onWheel(e) {
    if (cooldown) {
      e.preventDefault()
      return
    }
    const sections = getSections()
    if (!sections.length) return
    const idx = getCurrentIndex()
    if (e.deltaY > 0) {
      if (idx < sections.length - 1) {
        e.preventDefault()
        scrollToSection(idx + 1)
      }
    } else {
      if (idx > 0) {
        e.preventDefault()
        scrollToSection(idx - 1)
      }
    }
  }

  onMounted(() => {
    window.addEventListener('wheel', onWheel, { passive: false })
  })

  onUnmounted(() => {
    window.removeEventListener('wheel', onWheel)
  })
}
