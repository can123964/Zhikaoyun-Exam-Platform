<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form :inline="true">
        <el-form-item label="选择考试">
          <el-select
            v-model="selectedExamId"
            placeholder="请选择考试"
            clearable
            style="width: 320px"
            @change="handleExamChange"
          >
            <el-option
              v-for="exam in examOptions"
              :key="exam.id"
              :label="exam.title"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <template v-if="!selectedExamId">
      <el-card shadow="hover">
        <el-empty description="请先选择一场考试查看考试记录" />
      </el-card>
    </template>

    <template v-else>
      <el-card shadow="hover" class="table-section">
        <el-table
          v-loading="loading"
          :data="tableData"
          border
          stripe
          highlight-current-row
          class="table-section__content"
          row-key="id"
        >
          <el-table-column label="学生姓名" prop="realName" width="100" align="center" />
          <el-table-column label="状态" prop="statusName" width="100" align="center">
            <template #default="scope">
              <el-tag :type="statusTagType(scope.row.status)">
                {{ scope.row.statusName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="得分" prop="score" width="80" align="center">
            <template #default="scope">
              {{ scope.row.score ?? "—" }}
            </template>
          </el-table-column>
          <el-table-column label="开始时间" prop="startTime" width="180" align="center">
            <template #default="scope">
              {{ scope.row.startTime || "—" }}
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="submitTime" width="180" align="center">
            <template #default="scope">
              {{ scope.row.submitTime || "—" }}
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="120" align="center">
            <template #default="scope">
              <el-button type="primary" icon="view" link size="small" @click="handleViewDetail(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-if="total > 0"
          v-model:total="total"
          v-model:page="queryParams.page"
          v-model:limit="queryParams.size"
          @pagination="handlePagination"
        />
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import ExamAPI, { type ExamVO } from "@/api/business/exam";
import RecordAPI, { type ExamRecordVO } from "@/api/business/record";

defineOptions({
  name: "ExamRecords",
  inheritAttrs: false,
});

const router = useRouter();

// 考试下拉
const examOptions = ref<ExamVO[]>([]);
const selectedExamId = ref<number | undefined>(undefined);

const queryParams = reactive({
  page: 1,
  size: 10,
});

const tableData = ref<ExamRecordVO[]>([]);
const total = ref(0);
const loading = ref(false);

function statusTagType(status: number): "primary" | "success" | "warning" | "info" | "danger" {
  if (status === 0) return "info";
  if (status === 1) return "success";
  if (status === 2) return "warning";
  return "info";
}

/** 加载考试列表 */
async function fetchExams(): Promise<void> {
  try {
    const res = await ExamAPI.getPage({ page: 1, size: 200 });
    examOptions.value = res.records ?? [];
  } catch {
    examOptions.value = [];
  }
}

/** 考试选择变化 */
function handleExamChange(examId: number | undefined): void {
  if (!examId) {
    tableData.value = [];
    total.value = 0;
    return;
  }
  queryParams.page = 1;
  fetchList(examId);
}

/** 加载记录 */
async function fetchList(examId?: number): Promise<void> {
  const eid = examId ?? selectedExamId.value;
  if (!eid) return;
  loading.value = true;
  try {
    const res = await RecordAPI.getExamRecords(eid, { page: queryParams.page, size: queryParams.size });
    tableData.value = res.records ?? [];
    total.value = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handlePagination(): void {
  fetchList();
}

function handleViewDetail(row: ExamRecordVO): void {
  router.push(`/record/${row.id}`);
}

onMounted(() => {
  fetchExams();
});
</script>

