<template>
  <div class="dashboard-page">
    <div class="greeting-header">
      <div class="greeting-avatar">
        <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="" />
        <div v-else class="avatar-placeholder">{{ (userName || '?')[0] }}</div>
      </div>
      <div class="greeting-text">
        <h2>{{ greeting }}，{{ userName }}</h2>
        <p>欢迎使用智考云在线考试平台</p>
      </div>
    </div>

    <div class="section-title">学习概览</div>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon stat-icon--exams">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><rect x="9" y="3" width="6" height="4" rx="1" stroke="currentColor" stroke-width="1.5"/><path d="M9 14l2 2 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
        <div class="stat-body">
          <span class="stat-number">{{ totalExams }}</span>
          <span class="stat-label">已考场次</span>
        </div>
        <div class="stat-footer">累计参加考试</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon stat-icon--questions">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><circle cx="12" cy="17" r="0.5" fill="currentColor" stroke="currentColor"/></svg>
        </div>
        <div class="stat-body">
          <span class="stat-number">{{ totalWrong }}</span>
          <span class="stat-label">待订正错题</span>
        </div>
        <div class="stat-footer">错题本中未掌握</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon stat-icon--records">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M12 8v4l3 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/></svg>
        </div>
        <div class="stat-body">
          <span class="stat-number">{{ examList.length }}</span>
          <span class="stat-label">待参加考试</span>
        </div>
        <div class="stat-footer">即将开始的考试</div>
      </div>
      <div class="stat-card stat-card--action" @click="$router.push('/paper/index')">
        <div class="stat-icon stat-icon--action">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
        </div>
        <div class="stat-body">
          <span class="stat-number stat-number--small">全部考试</span>
          <span class="stat-label">查看并参加考试</span>
        </div>
        <div class="stat-footer">前往考试列表</div>
      </div>
    </div>

    <div class="section-title">最近动态</div>
    <div class="activity-card">
      <div v-loading="recordLoading" class="activity-list">
        <div v-if="recentRecords.length === 0 && !recordLoading" class="activity-empty"><p>暂无考试记录</p></div>
        <div v-for="item in recentRecords" :key="item.id" class="activity-item" @click="viewRecord(item.id)">
          <div class="activity-dot" :class="item.status === 1 ? 'activity-dot--success' : item.status === 2 ? 'activity-dot--danger' : 'activity-dot--warning'"></div>
          <div class="activity-content">
            <span class="activity-title">{{ item.examTitle }}</span>
            <span class="activity-subtitle">
              <span v-if="item.status === 0">答题中</span>
              <span v-else-if="item.status === 1">已提交 · {{ item.totalScore }}分</span>
              <span v-else-if="item.status === 2">超时交卷 · {{ item.totalScore }}分</span>
            </span>
          </div>
          <span class="activity-time">{{ formatTime(item.startTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import examPaperAnswerApi from '@/api/examPaperAnswer'
import questionAnswerApi from '@/api/questionAnswer'
import userApi from '@/api/user'
export default {
  data () {
    return {
      userName: '',
      userInfo: {},
      examLoading: false,
      recordLoading: false,
      examList: [],
      recentRecords: [],
      totalExams: 0,
      totalWrong: 0
    }
  },
  computed: {
    greeting () {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    }
  },
  created () {
    this.loadUserInfo()
    this.loadRecentRecords()
    this.loadAvailableExams()
    this.loadWrongCount()
  },
  methods: {
    loadUserInfo () {
      userApi.getCurrentUser().then(re => {
        this.userInfo = re.data || {}
        this.userName = re.data.realName || re.data.username || ''
      })
    },
    loadRecentRecords () {
      this.recordLoading = true
      examPaperAnswerApi.pageList({ page: 1, size: 5 }).then(re => {
        this.recentRecords = (re.data && re.data.records) || []
        this.totalExams = (re.data && re.data.total) || 0
        this.recordLoading = false
      }).catch(() => { this.recordLoading = false })
    },
    loadAvailableExams () {
      this.examLoading = true
      examPaperAnswerApi.getAvailable().then(re => {
        this.examList = re.data || []
        this.examLoading = false
      }).catch(() => { this.examLoading = false })
    },
    loadWrongCount () {
      questionAnswerApi.pageList({ page: 1, size: 1 }).then(re => {
        this.totalWrong = (re.data && re.data.total) || 0
      }).catch(() => {})
    },
    viewRecord (id) {
      this.$router.push({ path: '/read', query: { recordId: id } })
    },
    formatTime (t) {
      if (!t) return ''
      return t.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>

<style scoped>
.dashboard-page {
  max-width: 1100px;
  margin: 0 auto;
  min-height: 100%;
  padding: 24px 0 32px;
}
.greeting-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}
.greeting-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.2);
}
.greeting-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  background: linear-gradient(135deg, #007AFF, #5AC8FA);
  border-radius: 50%;
}
.greeting-text h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--brand-dark, #1d1d1f);
  margin: 0;
  letter-spacing: -0.3px;
}
.greeting-text p {
  font-size: 14px;
  color: var(--brand-muted, #6e6e73);
  margin: 4px 0 0;
}
.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--brand-dark, #1d1d1f);
  margin-bottom: 20px;
  letter-spacing: -0.2px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}
.stat-card {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}
.stat-card:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.14), 0 4px 12px rgba(0, 0, 0, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.9);
}
.stat-card--action { cursor: pointer; }
.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.stat-icon--exams { color: #007AFF; }
.stat-icon--questions { color: #FF9500; }
.stat-icon--records { color: #34C759; }
.stat-icon--action { color: #5856D6; }
.stat-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}
.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: var(--brand-dark, #1d1d1f);
  line-height: 1.1;
  letter-spacing: -0.5px;
}
.stat-number--small { font-size: 18px; }
.stat-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--brand-muted, #6e6e73);
}
.stat-footer {
  font-size: 12px;
  color: var(--brand-light, #a1a1a6);
  margin-top: 4px;
}
.activity-card {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.8);
}
.activity-list { display: flex; flex-direction: column; }
.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: background 0.2s;
}
.activity-item:hover { background: rgba(0, 0, 0, 0.02); }
.activity-item:last-child { border-bottom: none; }
.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.activity-dot--success { background: #34C759; box-shadow: 0 0 0 3px rgba(52, 199, 89, 0.2); }
.activity-dot--warning { background: #FF9500; box-shadow: 0 0 0 3px rgba(255, 149, 0, 0.2); }
.activity-dot--danger { background: #FF3B30; box-shadow: 0 0 0 3px rgba(255, 59, 48, 0.2); }
.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--brand-dark, #1d1d1f);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.activity-subtitle { font-size: 12px; color: var(--brand-muted, #6e6e73); }
.activity-time { font-size: 12px; color: var(--brand-muted, #6e6e73); white-space: nowrap; flex-shrink: 0; }
.activity-empty { padding: 40px 0; text-align: center; }
.activity-empty p { color: var(--brand-muted, #6e6e73); font-size: 14px; margin: 0; }
@media (max-width: 900px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .stats-grid { grid-template-columns: 1fr; } .greeting-text h2 { font-size: 20px; } }
</style>
