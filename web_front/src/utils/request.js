import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 响应拦截：统一处理业务 code 与错误
request.interceptors.response.use(
  (res) => {
    const { data } = res
    if (data && typeof data.code !== 'undefined' && data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (err) => {
    const message = err.response?.data?.message || err.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)

export default request
