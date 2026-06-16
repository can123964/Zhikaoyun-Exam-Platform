<template>
<div>
  <el-row class="do-exam-title">
    <el-col :span="24">
      <span :key="index" v-for="(item, index) in answerDetails">
        <el-tag :type="item.isCorrect ? 'success' : 'danger'" class="do-exam-title-tag" @click="goAnchor('#question-'+index)">{{ index + 1 }}</el-tag>
      </span>
    </el-col>
  </el-row>
  <el-row class="do-exam-title-hidden">
    <el-col :span="24">
      <span :key="index" v-for="(item, index) in answerDetails">
        <el-tag class="do-exam-title-tag">{{ index + 1 }}</el-tag>
      </span>
    </el-col>
  </el-row>
  <el-container class="app-item-contain">
    <el-header class="align-center">
      <h1>{{ recordInfo.examTitle }}</h1>
      <div>
        <span class="question-title-padding">试卷得分：{{ recordInfo.totalScore }}</span>
        <span class="question-title-padding">及格分：{{ recordInfo.passScore }}</span>
      </div>
    </el-header>
    <el-main>
      <el-form v-loading="formLoading" label-width="100px">
        <el-card class="exampaper-item-box">
          <el-form-item :key="index" :label="(index + 1) + '.'"
                        v-for="(item, index) in answerDetails"
                        class="exam-question-item" label-width="50px" :id="'question-'+ index">
            <QuestionAnswerShow :qType="item.type" :question="item" :answer="item"/>
          </el-form-item>
        </el-card>
      </el-form>
    </el-main>
  </el-container>
</div>
</template>

<script>
import QuestionAnswerShow from '../components/QuestionAnswerShow'
import examPaperAnswerApi from '@/api/examPaperAnswer'
export default {
  components: { QuestionAnswerShow },
  data () {
    return {
      formLoading: false,
      recordInfo: {
        examTitle: '',
        totalScore: 0,
        passScore: 0,
        duration: 0,
        status: null,
        startTime: '',
        submitTime: ''
      },
      answerDetails: []
    }
  },
  created () {
    let recordId = this.$route.query.recordId
    let _this = this
    if (recordId && parseInt(recordId) !== 0) {
      _this.formLoading = true
      examPaperAnswerApi.read(recordId).then(re => {
        let data = re.data
        _this.recordInfo = {
          examTitle: data.examTitle,
          totalScore: data.totalScore,
          passScore: data.passScore,
          duration: data.duration,
          status: data.status,
          startTime: data.startTime,
          submitTime: data.submitTime
        }
        let details = data.answerDetails || []
        if (details.length > 0) {
          _this.answerDetails = details.map(item => {
            return {
              ...item,
              content: item.studentAnswer || '',
              contentArray: item.studentAnswer ? item.studentAnswer.split('') : [],
              optionA: item.optionA,
              optionB: item.optionB,
              optionC: item.optionC,
              optionD: item.optionD
            }
          })
          _this.formLoading = false
        } else if (data.examId) {
          examPaperAnswerApi.enterExam(data.examId).then(paperRe => {
            let paper = paperRe.data
            _this.answerDetails = (paper.questions || []).map(q => {
              return {
                ...q,
                content: q.studentAnswer || '',
                contentArray: q.studentAnswer ? q.studentAnswer.split('') : [],
                correctAnswer: '',
                isCorrect: null,
                score: 0,
                explanation: ''
              }
            })
            _this.formLoading = false
          }).catch(() => { _this.formLoading = false })
        } else {
          _this.formLoading = false
        }
      }).catch(() => { _this.formLoading = false })
    }
  },
  methods: {
    goAnchor (selector) {
      this.$el.querySelector(selector).scrollIntoView({ behavior: 'instant', block: 'center', inline: 'nearest' })
    }
  }
}
</script>

<style lang="scss" scoped>
  .align-center {
    text-align: center
  }

  .exam-question-item{
    padding: 10px;
    .el-form-item__label{
      font-size: 15px !important;
    }
  }

  .question-title-padding{
    padding-left: 25px;
    padding-right: 25px;
  }
</style>
