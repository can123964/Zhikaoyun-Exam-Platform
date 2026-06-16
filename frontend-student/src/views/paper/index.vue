<template>
  <div style="margin-top: 10px" class="app-contain">
    <h3 class="page-title">我的考试</h3>
    <div v-loading="listLoading">
      <el-empty v-if="examList.length === 0 && !listLoading" description="暂无可参加的考试"></el-empty>
      <el-row :gutter="20">
        <el-col :span="8" v-for="item in examList" :key="item.examId" style="margin-bottom: 20px;">
          <el-card shadow="hover" class="exam-card">
            <div slot="header" class="exam-card-header" style="display: flex; justify-content: space-between; align-items: center;">
              <span>{{ item.title }}</span>
              <div>
                <el-tag :type="getExamStateTagType(item.examState)" size="mini" style="margin-right: 5px;">
                  {{ getExamStateText(item.examState) }}
                </el-tag>
                <el-tag :type="getUserStatusTagType(item.userStatus)" size="mini">
                  {{ getUserStatusText(item.userStatus) }}
                </el-tag>
              </div>
            </div>
            <div class="exam-card-content">
              <p v-if="item.description">{{ item.description }}</p>
              <p><i class="el-icon-time"></i> 考试时长：{{ item.duration }}分钟</p>
              <p><i class="el-icon-document"></i> 总分：{{ item.totalScore }}分 | 及格分：{{ item.passScore }}分</p>
              <p><i class="el-icon-date"></i> 开始时间：{{ item.startTime }}</p>
              <p><i class="el-icon-date"></i> 结束时间：{{ item.endTime }}</p>
              <p v-if="item.allowRetry"><el-tag type="info" size="mini">允许重考</el-tag></p>
            </div>
            <div class="exam-card-footer">
              <el-button :type="isEnterDisabled(item) ? 'info' : 'primary'" size="small" :disabled="isEnterDisabled(item)" @click="enterExam(item.examId)">
                {{ getEnterButtonText(item) }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import examPaperAnswerApi from '@/api/examPaperAnswer'

export default {
  data () {
    return {
      listLoading: false,
      examList: []
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      let _this = this
      _this.listLoading = true
      examPaperAnswerApi.getAvailable().then(re => {
        _this.examList = re.data || []
        _this.listLoading = false
      }).catch(() => {
        _this.listLoading = false
        _this.$message.error('获取考试列表失败')
      })
    },
    enterExam (examId) {
      let _this = this
      _this.$confirm('确认进入考试？进入后计时开始', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 直接导航到 /do，由 /do 统一处理入场 (避免重复调用 enterExam)
        _this.$router.push({ path: '/do', query: { examId: examId } })
      }).catch(() => { /* 用户取消确认框 */ })
    },
    isEnterDisabled (item) {
      if (item.examState === 0 || item.examState === 1 || item.examState === 3) {
        return true
      }
      if (item.examState === 2) {
        if (item.userStatus === 1 && item.allowRetry !== 1) {
          return true
        }
      }
      return false
    },
    getEnterButtonText (item) {
      if (item.examState === 0) return '暂未发布'
      if (item.examState === 1) return '尚未开始'
      if (item.examState === 3) return '考试已结束'
      if (item.examState === 2) {
        if (item.userStatus === 0) return '继续考试'
        if (item.userStatus === 1) {
          return item.allowRetry === 1 ? '再次考试' : '已交卷'
        }
        return '进入考试'
      }
      return '进入考试'
    },
    getExamStateText (state) {
      switch (state) {
        case 0: return '未发布'
        case 1: return '未开始'
        case 2: return '进行中'
        case 3: return '已结束'
        default: return '未知'
      }
    },
    getExamStateTagType (state) {
      switch (state) {
        case 0: return 'info'
        case 1: return 'warning'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
      }
    },
    getUserStatusText (status) {
      switch (status) {
        case -1: return '未参加'
        case 0: return '答题中'
        case 1: return '已提交'
        default: return '未参加'
      }
    },
    getUserStatusTagType (status) {
      switch (status) {
        case -1: return 'info'
        case 0: return 'primary'
        case 1: return 'success'
        default: return 'info'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  .page-title {
    font-size: 22px;
    font-weight: 600;
    color: var(--brand-dark);
    border-left: solid 6px var(--brand-accent);
    padding-left: 12px;
    margin-bottom: 20px;
  }

  .exam-card-header {
    font-size: 16px;
    font-weight: bold;
  }

  .exam-card-content {
    font-size: 13px;
    color: var(--brand-light);
    line-height: 2;

    p {
      margin: 0;
    }
  }

  .exam-card-footer {
    margin-top: 10px;
    text-align: right;
  }
</style>
