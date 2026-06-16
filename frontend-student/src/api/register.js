import { postWithLoadTip } from '@/utils/request'

export default {
  // 我们的后端：POST /api/user/register
  register: query => postWithLoadTip(`/api/user/register`, query)
}
