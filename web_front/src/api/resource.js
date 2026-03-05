import request from '@/utils/request'

export const resourceApi = {
  list: () => request.get('/resource/list'),
}

export default resourceApi
