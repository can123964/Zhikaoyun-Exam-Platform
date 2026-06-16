import userApi from '@/api/user'

// 用户状态管理（适配智考云后端 JWT 认证）
const state = {
  userName: localStorage.getItem('exam_username') || '',
  realName: localStorage.getItem('exam_realname') || '',
  role: localStorage.getItem('exam_role') || '',
  userInfo: null,
  messageCount: 0
}

const actions = {
  initUserInfo ({ commit }) {
    userApi.getCurrentUser().then(re => {
      // 我们的后端返回 { code: 200, data: UserVO }
      if (re.data) {
        commit('setUserInfo', re.data)
      }
    })
  },
  getUserMessageInfo () {
    // 消息功能暂不实现 — 保留占位
    return Promise.resolve({ code: 200, data: { unreadCount: 0 } })
  }
}

const mutations = {
  setUserName (state, userName) {
    state.userName = userName
    localStorage.setItem('exam_username', userName)
  },
  setRealName (state, realName) {
    state.realName = realName
    localStorage.setItem('exam_realname', realName)
  },
  setRole (state, role) {
    state.role = role
    localStorage.setItem('exam_role', role)
  },
  setUserInfo (state, userInfo) {
    state.userInfo = userInfo
    state.userName = userInfo.username || ''
    state.realName = userInfo.realName || ''
    state.role = userInfo.role !== undefined ? String(userInfo.role) : ''
    localStorage.setItem('exam_username', state.userName)
    localStorage.setItem('exam_realname', state.realName)
    localStorage.setItem('exam_role', state.role)
  },
  setMessageCount (state, count) {
    state.messageCount = count
  },
  messageCountSubtract (state, num) {
    state.messageCount = state.messageCount - num
  },
  clearLogin (state) {
    state.userName = ''
    state.realName = ''
    state.role = ''
    state.userInfo = null
    localStorage.removeItem('exam_token')
    localStorage.removeItem('exam_username')
    localStorage.removeItem('exam_realname')
    localStorage.removeItem('exam_role')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
