import { get, put } from '@/utils/request'

export default {
  // 获取当前用户信息：GET /api/user/info → UserVO
  getCurrentUser: () => get('/api/user/info'),
  // 更新个人资料：PUT /api/user/profile  body: { realName, phone, email }
  update: data => put('/api/user/profile', data)
}
