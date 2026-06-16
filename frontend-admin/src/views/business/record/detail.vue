<template>
  <div class="app-container">
    <div v-loading="loading">
      <!-- 顶部信息 -->
      <el-card shadow="hover" style="margin-bottom: 20px">
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div>
            <h2 style="margin: 0 0 12px 0">{{ record.examTitle }}</h2>
            <el-descriptions :column="4" border size="small">
              <el-descriptions-item label="得分">
                <span style="font-weight: bold; font-size: 16px">{{ record.score ?? "—" }}</span>
                <span v-if="record.totalScore"> / {{ record.totalScore }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(record.status)">{{ record.statusName }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">{{ record.startTime || "—" }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ record.submitTime || "—" }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <el-button icon="back" @click="goBack">返回</el-button>
        </div>
      </el-card>

      <!-- 题目列表 -->
      <el-card shadow="hover">
        <div v-for="(item, index) in answerDetails" :key="item.questionId" class="question-item">
          <div class="question-header">
            <el-tag :type="questionTypeTag(item.type)" size="small" style="margin-right: 8px">
              {{ questionTypeLabel(item.type) }}
            </el-tag>
            <span class="question-index">第 {{ index + 1 }} 题</span>
            <span style="margin-left: 12px; font-size: 13px; color: var(--el-text-color-secondary)">
              （{{ item.score ?? 0 }} 分）
            </span>
          </div>
          <div class="question-content">{{ item.content }}</div>

          <!-- 选项 -->
          <div v-if="item.type !== 2" class="question-options">
            <div v-if="item.optionA" class="option-line">A. {{ item.optionA }}</div>
            <div v-if="item.optionB" class="option-line">B. {{ item.optionB }}</div>
            <div v-if="item.optionC" class="option-line">C. {{ item.optionC }}</div>
            <div v-if="item.optionD" class="option-line">D. {{ item.optionD }}</div>
          </div>
          <div v-else class="question-options">
            <div class="option-line">A. 对</div>
            <div class="option-line">B. 错</div>
          </div>

          <!-- 答案对比 -->
          <div class="answer-compare">
            <span class="answer-label">我的答案：</span>
            <span :class="item.isCorrect === 1 ? 'answer-correct' : 'answer-wrong'">
              {{ formatAnswer(item.studentAnswer, item.type) }}
            </span>
            <span class="answer-label" style="margin-left: 24px">正确答案：</span>
            <span class="answer-correct">{{ formatAnswer(item.correctAnswer, item.type) }}</span>
            <el-tag
              :type="item.isCorrect === 1 ? 'success' : 'danger'"
              size="small"
              style="margin-left: 16px"
            >
              {{ item.isCorrect === 1 ? "正确" : "错误" }}
            </el-tag>
          </div>

          <!-- 解析 -->
          <div v-if="item.explanation" class="question-explanation">
            <span style="color: var(--el-color-primary)">解析：</span>{{ item.explanation }}
          </div>
        </div>

        <el-empty v-if="!loading && answerDetails.length === 0" description="暂无答题详情" />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import RecordAPI, { type ExamRecordVO, type AnswerDetailVO } from "@/api/business/record";

defineOptions({
  name: "RecordDetail",
  inheritAttrs: false,
});

const route = useRoute();
const router = useRouter();
const recordId = Number(route.params.recordId);

const record = ref<ExamRecordVO>({} as ExamRecordVO);
const answerDetails = ref<AnswerDetailVO[]>([]);
const loading = ref(false);

function statusTagType(status: number): "primary" | "success" | "warning" | "info" | "danger" {
  if (status === 0) return "info";
  if (status === 1) return "success";
  if (status === 2) return "warning";
  return "info";
}

function questionTypeLabel(type?: number): string {
  if (type === 0) return "单选";
  if (type === 1) return "多选";
  if (type === 2) return "判断";
  return "未知";
}

function questionTypeTag(type?: number): "primary" | "success" | "warning" | "info" | "danger" {
  if (type === 0) return "primary";
  if (type === 1) return "warning";
  if (type === 2) return "success";
  return "info";
}

function formatAnswer(answer?: string, type?: number): string {
  if (!answer) return "未作答";
  if (type === 2) {
    if (answer === "A") return "A (对)";
    if (answer === "B") return "B (错)";
  }
  return answer;
}

function goBack(): void {
  router.back();
}

async function fetchDetail(): Promise<void> {
  loading.value = true;
  try {
    const res = await RecordAPI.getRecordDetail(recordId);
    record.value = res;
    answerDetails.value = res.answerDetails ?? [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchDetail();
});
</script>

<style scoped lang="scss">
.question-item {
  padding: 16px 0;

  &:last-child {
    border-bottom: none;
  }
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.question-index {
  font-weight: 600;
  font-size: 15px;
}

.question-content {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.question-options {
  padding-left: 12px;
  margin-bottom: 10px;
}

.option-line {
  font-size: 14px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}

.answer-compare {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 8px;
}

.answer-label {
  color: var(--el-text-color-secondary);
}

.answer-correct {
  color: var(--el-color-success);
  font-weight: 600;
}

.answer-wrong {
  color: var(--el-color-danger);
  font-weight: 600;
}

.question-explanation {
  font-size: 13px;
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-lighter);
  padding: 8px 12px;
  border-radius: var(--brand-radius-sm);
  margin-top: 4px;
}
</style>
