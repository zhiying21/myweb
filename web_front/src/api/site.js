import request from '@/utils/request'

export const siteApi = {
  getStats: () => request.get('/site/stats'),
  visit: () => request.post('/site/visit'),
  verifyResume: (password) => request.post('/site/resume/verify', { password }),
  verifyLoveDiary: (password) => request.post('/site/love-diary/verify', { password }),
}

export default siteApi
