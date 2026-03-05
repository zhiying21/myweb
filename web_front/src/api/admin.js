import request from '@/utils/request'

export const adminApi = {
  getTickets: () => request.get('/admin/tickets'),
  replyTicket: (ticketId, content) => request.post(`/admin/tickets/${ticketId}/reply`, { content }),
  addResource: (data) => request.post('/admin/resource', data),
  deleteResource: (id) => request.delete(`/admin/resource/${id}`),
  getResources: () => request.get('/admin/resources'),
  getResume: () => request.get('/admin/resume'),
  saveResume: (content) => request.put('/admin/resume', { content }),
  upload: (formData) => request.post('/admin/upload', formData, { timeout: 30000 }),
}

export default adminApi
