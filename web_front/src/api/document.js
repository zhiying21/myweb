import request from '@/utils/request'

export const documentApi = {
  list: (type) => request.get('/document/list', { params: { type } }),
  detail: (id) => request.get(`/document/detail/${id}`),
  upload: (data) => request.post('/document/upload', data),
  delete: (id) => request.delete(`/document/${id}`),
  like: (id) => request.post(`/document/${id}/like`),
  hasLiked: (id) => request.get(`/document/${id}/liked`),
  getComments: (id) => request.get(`/document/${id}/comments`),
  addComment: (id, content) => request.post(`/document/${id}/comments`, { content }),
  deleteComment: (commentId) => request.delete(`/document/comments/${commentId}`),
}

export default documentApi
