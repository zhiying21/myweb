import { ref, computed } from 'vue'
import request from '@/utils/request'

const TOKEN_KEY = 'zhiying_token'
const token = ref(localStorage.getItem(TOKEN_KEY) || '')
const user = ref(null)

export function useAuth() {
  const isLoggedIn = computed(() => !!token.value)

  async function login(email, password) {
    const res = await request.post('/auth/login', { email, password })
    const d = res?.data ?? res
    const t = d?.token ?? res?.token
    if (t) {
      token.value = t
      user.value = { userId: d.userId, email: d.email, nickname: d.nickname, avatar: d.avatar, role: d.role }
      localStorage.setItem(TOKEN_KEY, t)
    }
    return d ?? res
  }

  async function register(email, password) {
    const res = await request.post('/auth/register', { email, password })
    const d = res?.data ?? res
    const t = d?.token ?? res?.token
    if (t) {
      token.value = t
      user.value = { userId: d.userId, email: d.email, nickname: d.nickname, avatar: d.avatar, role: d.role }
      localStorage.setItem(TOKEN_KEY, t)
    }
    return d ?? res
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  async function fetchUser() {
    if (!token.value) return null
    try {
      const res = await request.get('/auth/me')
      const d = res?.data ?? res
      user.value = d
      return d
    } catch {
      logout()
      return null
    }
  }

  function getToken() {
    return token.value
  }

  return { token, user, isLoggedIn, login, register, logout, fetchUser, getToken }
}
