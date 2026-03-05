import request from '@/utils/request'

export const authApi = {
  login: (email, password) => request.post('/auth/login', { email, password }),
  register: (email, password) => request.post('/auth/register', { email, password }),
  me: () => request.get('/auth/me'),
  updateProfile: (data) => request.put('/auth/profile', data),
  uploadAvatar: (formData) => request.post('/auth/upload', formData),
}

export default authApi
