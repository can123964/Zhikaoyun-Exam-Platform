<template>
  <div v-loading="qLoading" style="line-height:1.8">
    <div v-if="qType===0 || qType===1 || qType===2">
      <!-- 单选题 type=0 -->
      <div v-if="qType===0">
        <div class="q-title">{{ question.content }}</div>
        <div class="q-content" v-if="question.image">
          <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
        </div>
        <div class="q-content">
          <el-radio-group v-model="answer.content" disabled>
            <el-radio label="A">A. {{ question.optionA }}</el-radio>
            <el-radio label="B">B. {{ question.optionB }}</el-radio>
            <el-radio label="C">C. {{ question.optionC }}</el-radio>
            <el-radio label="D">D. {{ question.optionD }}</el-radio>
          </el-radio-group>
        </div>
      </div>
      <!-- 多选题 type=1 -->
      <div v-else-if="qType===1">
        <div class="q-title">{{ question.content }}</div>
        <div class="q-content" v-if="question.image">
          <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
        </div>
        <div class="q-content">
          <el-checkbox-group v-model="answer.contentArray" disabled>
            <el-checkbox label="A">A. {{ question.optionA }}</el-checkbox>
            <el-checkbox label="B">B. {{ question.optionB }}</el-checkbox>
            <el-checkbox label="C">C. {{ question.optionC }}</el-checkbox>
            <el-checkbox label="D">D. {{ question.optionD }}</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <!-- 判断题 type=2 -->
      <div v-else-if="qType===2">
        <div class="q-title">{{ question.content }}</div>
        <div class="q-content" v-if="question.image">
          <img :src="question.image" style="max-width: 100%; max-height: 200px;"/>
        </div>
        <div class="q-content">
          <el-radio-group v-model="answer.content" disabled>
            <el-radio label="A">A. 对</el-radio>
            <el-radio label="B">B. 错</el-radio>
          </el-radio-group>
        </div>
      </div>
      <div class="question-answer-show-item" style="margin-top: 15px">
        <span class="question-show-item">结果：</span>
        <el-tag :type="answer.isCorrect ? 'success' : 'danger'">
          {{ answer.isCorrect ? '正确' : '错误' }}
        </el-tag>
      </div>
      <div class="question-answer-show-item">
        <span class="question-show-item">得分：</span>
        <span>{{ answer.score }}</span>
      </div>
      <div class="question-answer-show-item">
        <span class="question-show-item">我的答案：</span>
        <span>{{ answer.studentAnswer }}</span>
      </div>
      <div class="question-answer-show-item">
        <span class="question-show-item">正确答案：</span>
        <span>{{ answer.correctAnswer }}</span>
      </div>
      <div class="question-answer-show-item" v-if="answer.explanation" style="line-height: 1.8">
        <span class="question-show-item">解析：</span>
        <span>{{ answer.explanation }}</span>
      </div>
    </div>
    <div v-else>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuestionAnswerShow',
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
        return { content: '', contentArray: [], isCorrect: null, score: 0, studentAnswer: '', correctAnswer: '', explanation: '' }
      }
    },
    qLoading: {
      type: Boolean,
      default: false
    },
    qType: {
      type: Number,
      default: 0
    }
  }
}
</script>
