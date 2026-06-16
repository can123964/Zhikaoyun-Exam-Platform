import { get, post } from '@/utils/request'

export default {
  // 考试记录列表：GET /api/record/my?page=1&size=10
  pageList: query => get('/api/record/my', query),
  // 保存单题答案：POST /api/record/{recordId}/answer  body: { questionId, answer }
  saveAnswer: (recordId, data) => post('/api/record/' + recordId + '/answer', data),
  // 交卷：POST /api/record/{recordId}/submit  → 返回分数
  submitExam: recordId => post('/api/record/' + recordId + '/submit'),
  // 查看试卷：GET /api/record/detail/{recordId}
  read: recordId => get('/api/record/detail/' + recordId),
  // 进入考试：POST /api/record/{examId}/enter → 返回试卷数据
  enterExam: examId => post('/api/record/' + examId + '/enter'),
  // 可参加考试列表：GET /api/record/available
  getAvailable: () => get('/api/record/available'),
  // 切屏上报：POST /api/record/{recordId}/tab-switch
  tabSwitch: recordId => post('/api/record/' + recordId + '/tab-switch')
}
