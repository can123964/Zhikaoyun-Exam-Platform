<template>
  <div style="line-height:1.8">
    <!-- 单选题 type=0 -->
    <div v-if="qType===0" v-loading="qLoading">
      <div class="q-title">{{ question.content }}</div>
      <div class="q-content" v-if="question.image">
        <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
      </div>
      <div class="q-content">
        <el-radio-group v-model="answer.content" @change="onSingleChange" >
          <el-radio label="A">A. {{ question.optionA }}</el-radio>
          <el-radio label="B">B. {{ question.optionB }}</el-radio>
          <el-radio label="C">C. {{ question.optionC }}</el-radio>
          <el-radio label="D">D. {{ question.optionD }}</el-radio>
        </el-radio-group>
      </div>
    </div>
    <!-- 多选题 type=1 -->
    <div v-else-if="qType===1" v-loading="qLoading">
      <div class="q-title">{{ question.content }}</div>
      <div class="q-content" v-if="question.image">
        <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
      </div>
      <div class="q-content">
        <el-checkbox-group v-model="answer.contentArray" @change="onMultiChange" >
          <el-checkbox label="A">A. {{ question.optionA }}</el-checkbox>
          <el-checkbox label="B">B. {{ question.optionB }}</el-checkbox>
          <el-checkbox label="C">C. {{ question.optionC }}</el-checkbox>
          <el-checkbox label="D">D. {{ question.optionD }}</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <!-- 判断题 type=2 -->
    <div v-else-if="qType===2" v-loading="qLoading">
      <div class="q-title">{{ question.content }}</div>
      <div class="q-content" v-if="question.image">
        <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
      </div>
      <div class="q-content">
        <el-radio-group v-model="answer.content" @change="onSingleChange" >
          <el-radio label="A">A. 对</el-radio>
          <el-radio label="B">B. 错</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div v-else>
    </div>
  </div>
</template>

<script>
import examPaperAnswerApi from '@/api/examPaperAnswer'

export default {
  name: 'QuestionEdit',
  props: {
    question: {
      type: Object,
      default: function () {
        return {}
      }
    },
    answer: {
      type: Object,
      default: function () {
        return { questionId: null, content: '', contentArray: [], completed: false }
      }
    },
    qLoading: {
      type: Boolean,
      default: false
    },
    qType: {
      type: Number,
      default: 0
    },
    recordId: {
      type: [Number, String],
      default: null
    }
  },
  created () {
    // 创建防抖版本的 saveAnswer (300ms 防抖，防止快速点击并发请求)
    this._pendingSave = null
    this._debouncedSave = this._debounce(function (questionId, answer) {
      this._pendingSave = null
      let rid = this.recordId || this.$route.query.recordId
      if (rid) {
        examPaperAnswerApi.saveAnswer(rid, { questionId: questionId, answer: answer }).catch(() => {
          this.$message.error('答案保存失败，请检查网络后重试')
        })
      }
    }, 300)
  },
  methods: {
    _debounce (fn, delay) {
      let timer = null
      let self = this
      return function (...args) {
        // 记录待执行的保存参数，供 flush 使用
        self._pendingSave = args
        if (timer) clearTimeout(timer)
        timer = setTimeout(() => {
          fn.apply(self, args)
          timer = null
        }, delay)
      }
    },
    /** 立即执行挂起的保存（交卷前调用，防止答案丢失） */
    flushPendingSave () {
      if (this._pendingSave) {
        let args = this._pendingSave
        this._pendingSave = null
        let rid = this.recordId || this.$route.query.recordId
        if (rid) {
          return examPaperAnswerApi.saveAnswer(rid, { questionId: args[0], answer: args[1] })
        }
      }
      return Promise.resolve()
    },
    onSingleChange (val) {
      this.answer.completed = true
      this.$emit('on-answer-change', this.answer.questionId, this.answer)
      this._debouncedSave(this.answer.questionId, val)
    },
    onMultiChange (val) {
      this.answer.completed = val.length > 0
      this.$emit('on-answer-change', this.answer.questionId, this.answer)
      let answerStr = val.slice().sort().join('')
      this._debouncedSave(this.answer.questionId, answerStr)
    },
    saveAnswer (questionId, answer) {
      // 保留原始 saveAnswer 以便外部直接调用（不使用防抖）
      let rid = this.recordId || this.$route.query.recordId
      if (rid) {
        examPaperAnswerApi.saveAnswer(rid, { questionId: questionId, answer: answer }).catch(() => {
          this.$message.error('答案保存失败，请检查网络后重试')
        })
      }
    }
  }
}
</script>
