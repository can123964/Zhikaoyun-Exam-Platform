// initial state
const state = {
  user: {
    roleEnum: [{ key: 0, value: '学生' }, { key: 1, value: '教师' }, { key: 2, value: '管理员' }]
  },
  exam: {
    examPaperAnswer: {
      statusEnum: [{ key: 0, value: '进行中' }, { key: 1, value: '已提交' }, { key: 2, value: '超时自动交卷' }],
      statusTag: [{ key: 0, value: 'warning' }, { key: 1, value: 'success' }, { key: 2, value: 'danger' }]
    },
    question: {
      typeEnum: [{ key: 0, value: '单选' }, { key: 1, value: '多选' }, { key: 2, value: '判断' }],
      difficultyEnum: [{ key: 1, value: '简单' }, { key: 2, value: '中等' }, { key: 3, value: '困难' }],
      answer: {
        doRightTag: [{ key: true, value: 'success' }, { key: false, value: 'danger' }, { key: null, value: 'warning' }],
        doRightEnum: [{ key: true, value: '正确' }, { key: false, value: '错误' }, { key: null, value: '待批改' }],
        doCompletedTag: [{ key: false, value: 'info' }, { key: true, value: 'success' }]
      }
    },
    wrongBook: {
      masteredEnum: [{ key: 0, value: '未掌握' }, { key: 1, value: '已掌握' }],
      masteredTag: [{ key: 0, value: 'danger' }, { key: 1, value: 'success' }]
    }
  }
}

// getters
const getters = {
  enumFormat: (state) => (array, key) => {
    return format(array, key)
  }
}

// actions
const actions = {}

// mutations
const mutations = {}

const format = function (array, key) {
  for (let item of array) {
    if (item.key === key) {
      return item.value
    }
  }
  return null
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
