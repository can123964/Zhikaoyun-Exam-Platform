import { get, put, del } from '@/utils/request'

export default {
  // 错题本列表：GET /api/wrong-book?page=1&size=10&type=0&mastered=0
  pageList: query => get('/api/wrong-book', query),
  // 标记已掌握：PUT /api/wrong-book/{id}/master
  markMastered: id => put('/api/wrong-book/' + id + '/master'),
  // 删除错题：DELETE /api/wrong-book/{id}
  deleteById: id => del('/api/wrong-book/' + id)
}
