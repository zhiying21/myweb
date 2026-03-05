import request from '@/utils/request'

export const loveApi = {
  getConfig: () => request.get('/love/config'),
  saveConfig: (data) => request.post('/love/config', data),
  getDiaries: () => request.get('/love/diaries'),
  createDiary: (data) => request.post('/love/diaries', data),
  updateDiary: (id, data) => request.put(`/love/diaries/${id}`, data),
  deleteDiary: (id) => request.delete(`/love/diaries/${id}`),
  analyzeEmotion: (content) => request.post('/love/analyze-emotion', { content }),
  getPhotos: () => request.get('/love/photos'),
  createPhoto: (data) => request.post('/love/photos/create', data),
  uploadPhoto: (formData) => request.post('/love/photos', formData),
  deletePhoto: (id) => request.delete(`/love/photos/${id}`),
  getReminders: () => request.get('/love/reminders'),
  createReminder: (data) => request.post('/love/reminders', data),
  updateReminder: (id, data) => request.put(`/love/reminders/${id}`, data),
  deleteReminder: (id) => request.delete(`/love/reminders/${id}`),
}

export default loveApi
