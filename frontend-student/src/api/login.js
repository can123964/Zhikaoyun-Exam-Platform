import { postWithLoadTip } from '@/utils/request'

export default {
  // 我们的后端也是 POST /api/user/login，返回 LoginVO { token, userId, username, realName, role }
  login: query => postWithLoadTip(`/api/user/login`, query),
  // JWT 无状态，退出只需清除前端 token
  logout: () => {
    localStorage.removeItem('exam_token')
    return Promise.resolve({ code: 200 })
  }
}
