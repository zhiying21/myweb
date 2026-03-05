import request from '@/utils/request'

export const messageApi = {
  list: () => request.get('/message/list'),
  add: (content) => request.post('/message/add', { content }),
  delete: (id) => request.delete(`/message/${id}`),
  like: (id) => request.post(`/message/${id}/like`),
  addComment: (data) => request.post('/message/comment', data),
  deleteComment: (commentId) => request.delete(`/message/comment/${commentId}`),
  likeComment: (commentId) => request.post(`/message/comment/${commentId}/like`),
}

export default messageApi
