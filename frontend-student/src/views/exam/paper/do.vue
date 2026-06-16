<template>
  <div>
    <el-row class="do-exam-title">
      <el-col :span="24">
        <span :key="index" v-for="(item, index) in questions">
          <el-tag :type="questionCompleted(item.studentAnswer) ? 'success' : 'info'" class="do-exam-title-tag" @click="goAnchor('#question-'+index)">{{ index + 1 }}</el-tag>
        </span>
        <span class="do-exam-time">
          <label>剩余时间：</label>
          <label>{{ formatSeconds(remainTime) }}</label>
        </span>
      </el-col>
    </el-row>
    <el-row class="do-exam-title-hidden">
      <el-col :span="24">
        <span :key="index" v-for="(item, index) in questions">
          <el-tag class="do-exam-title-tag">{{ index + 1 }}</el-tag>
        </span>
        <span class="do-exam-time">
          <label>剩余时间：</label>
        </span>
      </el-col>
    </el-row>
    <el-container class="app-item-contain">
      <el-header class="align-center">
        <h1>{{ examTitle }}</h1>
        <div>
          <span class="question-title-padding">考试时长：{{ duration }}分钟</span>
        </div>
      </el-header>
      <el-main>
        <el-form v-loading="formLoading" label-width="100px">
          <el-card class="exampaper-item-box">
            <el-form-item :key="index" :label="(index + 1) + '.'"
                          v-for="(item, index) in questions"
                          class="exam-question-item" label-width="50px" :id="'question-'+ index">
              <QuestionEdit :ref="'q_' + item.questionId" :recordId="recordId" :qType="item.type" :question="item"
                            :answer="answerMap[item.questionId]"
                            @on-answer-change="onAnswerChange"/>
            </el-form-item>
          </el-card>
          <el-row class="do-align-center">
            <el-button type="primary" @click="submitForm" :loading="submitLoading">提交</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-row>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { formatSeconds } from '@/utils'
import QuestionEdit from '../components/QuestionEdit'
import examPaperAnswerApi from '@/api/examPaperAnswer'

export default {
  components: { QuestionEdit },
  data () {
    return {
      recordId: null,
      examTitle: '',
      duration: 0,
      remainTime: 0,
      questions: [],
      answerMap: {},
      formLoading: false,
      submitLoading: false,
      timer: null,
      tabSwitching: false,
      submitted: false
    }
  },
  created () {
    let recordId = this.$route.query.recordId
    let examId = this.$route.query.examId
    if (recordId) {
      this.recordId = recordId
      this.loadExamPaper(examId)
    } else if (examId) {
      this.loadExamPaper(examId)
    } else {
      this.$message.error('缺少考试参数')
      return
    }
    // 切屏检测
    document.addEventListener('visibilitychange', this.handleVisibilityChange)
    window.addEventListener('beforeunload', this.handleBeforeUnload)
  },
  beforeDestroy () {
    window.clearInterval(this.timer)
    document.removeEventListener('visibilitychange', this.handleVisibilityChange)
    window.removeEventListener('beforeunload', this.handleBeforeUnload)
  },
  methods: {
    formatSeconds (theTime) {
      return formatSeconds(theTime)
    },
    loadExamPaper (examId) {
      let _this = this
      if (!examId) {
        _this.$message.error('缺少考试ID，无法继续考试')
        _this.formLoading = false
        _this.$router.push('/paper/index')
        return
      }
      _this.formLoading = true
      examPaperAnswerApi.enterExam(examId).then(re => {
        let data = re.data
        _this.recordId = _this.recordId || data.recordId
        _this.examTitle = data.examTitle
        _this.duration = data.duration
        _this.remainTime = data.remainingSeconds
        _this.questions = data.questions || []

        // 防切屏提示
        var maxSwitches = data.maxTabSwitches
        if (maxSwitches > 0) {
          _this.formLoading = false
          _this.$alert(
            '本场考试已开启防切屏检测，最多允许切屏 ' + maxSwitches + ' 次。\n' +
            '切屏次数超过限制后，系统将自动交卷。\n\n请专注答题，不要切换出考试页面！',
            '考试须知',
            {
              confirmButtonText: '开始答题',
              type: 'warning',
              closeOnClickModal: false,
              closeOnPressEscape: false
            }
          ).then(function () {
            _this.initAnswerMap()
            _this.timeReduce()
          })
        } else {
          _this.initAnswerMap()
          _this.timeReduce()
          _this.formLoading = false
        }
      }).catch(() => {
        _this.formLoading = false
      })
    },
    initAnswerMap () {
      let map = {}
      for (let q of this.questions) {
        map[q.questionId] = {
          questionId: q.questionId,
          content: q.studentAnswer || '',
          contentArray: q.studentAnswer ? q.studentAnswer.split('') : [],
          completed: !!q.studentAnswer
        }
      }
      this.answerMap = map
    },
    onAnswerChange (questionId, answer) {
      this.$set(this.answerMap, questionId, answer)
    },
    questionCompleted (studentAnswer) {
      return !!studentAnswer
    },
    goAnchor (selector) {
      this.$el.querySelector(selector).scrollIntoView({ behavior: 'instant', block: 'center', inline: 'nearest' })
    },
    timeReduce () {
      let _this = this
      this.timer = setInterval(function () {
        if (_this.remainTime <= 0 && !_this.submitted) {
          _this.submitForm()
        } else {
          --_this.remainTime
        }
      }, 1000)
    },
    // 切屏检测
    reportTabSwitch () {
      if (!this.recordId || this.tabSwitching || this.submitted) return
      this.tabSwitching = true
      examPaperAnswerApi.tabSwitch(this.recordId).then((re) => {
        this.tabSwitching = false
        var data = re.data || {}
        var current = data.currentCount || 1
        var maxCount = data.maxCount || 3
        if (data.submitted) {
          window.clearInterval(this.timer)
          this.$alert('切屏次数过多，系统已自动交卷', '考试结束', {
            confirmButtonText: '查看记录',
            type: 'error',
            closeOnClickModal: false,
            closeOnPressEscape: false
          }).then(function () {
            this.$router.push('/record/index')
          }.bind(this))
        } else {
          var remain = maxCount - current
          this.$notify({
            title: '切屏警告',
            message: '检测到您切出了考试界面（第 ' + current + '/' + maxCount + ' 次）！\n剩余 ' + remain + ' 次机会，超过后将自动交卷。',
            type: 'warning',
            duration: 4000,
            position: 'top-center'
          })
        }
      }).catch(function (e) {
        this.tabSwitching = false
        var msg = (e && e.message) || '考试已结束'
        this.$alert(msg, '提示', {
          confirmButtonText: '查看考试记录',
          type: 'info',
          closeOnClickModal: false,
          closeOnPressEscape: false
        }).then(function () {
          this.$router.push('/record/index')
        }.bind(this))
      }.bind(this))
    },
    handleVisibilityChange () {
      if (document.hidden) {
        this.reportTabSwitch()
      }
    },
    handleBeforeUnload () {
      if (!this.recordId) return
      // 使用 fetch + keepalive 替代同步 XHR（同步 XHR 已被现代浏览器废弃）
      var token = localStorage.getItem('exam_token') || ''
      fetch('/api/record/' + this.recordId + '/tab-switch', {
        method: 'POST',
        headers: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        keepalive: true
      }).catch(function () {})
    },
    submitForm () {
      let _this = this
      if (_this.submitLoading) return
      if (_this.submitted) return
      _this.submitted = true
      window.clearInterval(_this.timer)
      _this.submitLoading = true

      // 交卷前 flush 所有挂起的防抖保存，防止最后一道题答案丢失
      var flushPromises = []
      for (var q of _this.questions) {
        var refs = _this.$refs['q_' + q.questionId]
        if (refs && refs[0] && typeof refs[0].flushPendingSave === 'function') {
          flushPromises.push(refs[0].flushPendingSave())
        }
      }
      Promise.all(flushPromises).catch(() => {}).then(function () {
        examPaperAnswerApi.submitExam(_this.recordId).then(re => {
          _this.$alert('试卷得分：' + re.data + '分', '考试结果', {
            confirmButtonText: '返回考试记录',
            callback: action => {
              _this.$router.push('/record/index')
            }
          })
          _this.submitLoading = false
        }).catch(() => {
          _this.submitLoading = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .align-center {
    text-align: center
  }

  .exam-question-item {
    padding: 10px;

    .el-form-item__label {
      font-size: 15px !important;
    }

    .el-card {
      border-radius: var(--glass-radius) !important;
    }
  }

  .question-title-padding {
    padding-left: 25px;
    padding-right: 25px;
  }
</style>
