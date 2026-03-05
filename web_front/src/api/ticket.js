import request from '@/utils/request'

export const ticketApi = {
  create: (data) => request.post('/ticket/create', data),
  my: () => request.get('/ticket/my'),
}

export default ticketApi
