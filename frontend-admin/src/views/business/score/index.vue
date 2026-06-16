<template>
  <div class="app-container">
    <!-- 顶部：考试选择 + 班级筛选 -->
    <el-card shadow="hover" class="filter-section">
      <el-form :inline="true">
        <el-form-item label="选择考试">
          <el-select
            v-model="selectedExamId"
            placeholder="请选择已结束的考试"
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
        <el-form-item label="班级筛选">
          <el-select
            v-model="selectedClassId"
            placeholder="全部班级"
            clearable
            style="width: 180px"
            @change="handleClassChange"
          >
            <el-option
              v-for="cls in classOptions"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 未选择考试时的空状态 -->
    <template v-if="!selectedExamId">
      <el-card shadow="hover">
        <el-empty description="请先选择一场已结束的考试查看成绩统计" />
      </el-card>
    </template>

    <!-- 选择考试后显示统计 -->
    <template v-else>
      <!-- 统计卡片区域 -->
      <el-row :gutter="16" class="stat-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card__label">参考人数</div>
            <div class="stat-card__value">{{ stat?.submitCount ?? '—' }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card__label">平均分</div>
            <div class="stat-card__value">{{ stat?.avgScore?.toFixed(1) ?? '—' }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card__label">最高分 / 最低分</div>
            <div class="stat-card__value">
              {{ stat?.maxScore?.toFixed(1) ?? '—' }} / {{ stat?.minScore?.toFixed(1) ?? '—' }}
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card__label">及格率</div>
            <div class="stat-card__value">
              {{ stat?.passRate != null ? stat.passRate.toFixed(1) + '%' : '—' }}
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 视图切换 -->
      <div class="view-switcher-wrapper">
        <el-radio-group v-model="viewMode" class="view-switcher">
          <el-radio-button value="overview">统计概览</el-radio-button>
          <el-radio-button value="distribution">分数段分布</el-radio-button>
          <el-radio-button value="pie">通过率</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 统计概览：排名表格 -->
      <el-card v-if="viewMode === 'overview'" shadow="hover" class="table-section">
        <el-table
          v-loading="rankLoading"
          :data="pagedRankList"
          border
          stripe
          highlight-current-row
          class="table-section__content"
          row-key="userId"
        >
          <el-table-column label="排名" type="index" width="70" align="center" :index="indexMethod" />
          <el-table-column label="学号" prop="studentNo" width="140" align="center">
            <template #default="scope">
              {{ scope.row.studentNo || '—' }}
            </template>
          </el-table-column>
          <el-table-column label="姓名" prop="realName" width="120" align="center">
            <template #default="scope">
              {{ scope.row.realName || '—' }}
            </template>
          </el-table-column>
          <el-table-column label="得分" prop="score" width="100" align="center">
            <template #default="scope">
              <span :class="{ 'score-pass': scope.row.score >= (stat?.passScore ?? 60), 'score-fail': scope.row.score < (stat?.passScore ?? 60) }">
                {{ scope.row.score?.toFixed(1) ?? '—' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="140" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'warning' : 'info'"
              >
                {{ statusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="submitTime" min-width="180" align="center">
            <template #default="scope">
              {{ scope.row.submitTime || '—' }}
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-if="rankTotal > 0"
          v-model:total="rankTotal"
          v-model:page="currentPage"
          v-model:limit="pageSize"
          @pagination="() => {}"
        />
      </el-card>

      <!-- 饼图：通过率 -->
      <el-card v-else-if="viewMode === 'pie'" shadow="hover" class="chart-section">
        <div ref="pieChartRef" class="chart-container"></div>
      </el-card>

      <!-- 分数段分布：柱状图 -->
      <el-card v-else shadow="hover" class="chart-section">
        <div ref="chartRef" class="chart-container"></div>
        <div v-if="stat?.scoreDistribution" class="distribution-summary">
          <div
            v-for="(count, range) in stat.scoreDistribution"
            :key="range"
            class="summary-item"
          >
            <div class="summary-range">{{ range }}</div>
            <div class="summary-count">{{ count }}<span class="summary-unit">人</span></div>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import * as echarts from "echarts";
import ExamAPI, { type ExamVO } from "@/api/business/exam";
import ScoreAPI, { type ScoreStatVO, type RankVO } from "@/api/business/score";
import ClassAPI from "@/api/business/class";
import { hasRole } from "@/utils/auth";

defineOptions({
  name: "ScoreManage",
  inheritAttrs: false,
});

// 考试下拉选项
const examOptions = ref<ExamVO[]>([]);
const selectedExamId = ref<number | undefined>(undefined);

// 班级筛选
const classOptions = ref<{ id: number; className: string }[]>([]);
const selectedClassId = ref<number | undefined>(undefined);

// 统计数据
const stat = ref<ScoreStatVO | null>(null);

// 视图切换
const viewMode = ref("overview");

// ECharts
const chartRef = ref<HTMLDivElement>();
const pieChartRef = ref<HTMLDivElement>();
let chartInstance: echarts.ECharts | null = null;
let pieChartInstance: echarts.ECharts | null = null;

function renderDistributionChart(): void {
  if (!chartRef.value || !stat.value?.scoreDistribution) return;

  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
  }

  const dist = stat.value.scoreDistribution;
  const sortedKeys = Object.keys(dist).sort();
  const data = sortedKeys.map((k) => ({ range: k, count: dist[k] }));

  chartInstance.setOption({
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      formatter: (params: any) => {
        const p = params[0];
        return `${p.name}<br/><strong>${p.value}</strong> 人`;
      },
    },
    grid: {
      left: 50,
      right: 30,
      top: 20,
      bottom: 30,
    },
    xAxis: {
      type: "category",
      data: data.map((d) => d.range),
      axisLabel: { color: "#6e6e73", fontWeight: 500, fontSize: 12 },
      axisLine: { lineStyle: { color: "#c7c7cc" } },
      axisTick: { show: false },
    },
    yAxis: {
      type: "value",
      minInterval: 1,
      axisLabel: { color: "#6e6e73", fontSize: 12 },
      splitLine: { lineStyle: { color: "rgba(255,255,255,0.3)" } },
    },
    series: [
      {
        type: "bar",
        data: data.map((d) => d.count),
        barWidth: 40,
        itemStyle: {
          color: "#007AFF",
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: { color: "#0066d6" },
        },
      },
    ],
  });
}

function renderPieChart(): void {
  if (!pieChartRef.value || !stat.value?.scoreDistribution) return;

  if (!pieChartInstance) {
    pieChartInstance = echarts.init(pieChartRef.value);
  }

  const dist = stat.value.scoreDistribution;
  const sortedKeys = Object.keys(dist).sort();
  const pieData = sortedKeys.map((k) => ({ name: k, value: dist[k] }));

  pieChartInstance.setOption({
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} 人 ({d}%)",
    },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        center: ["50%", "50%"],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: true,
          formatter: "{b}\n{c}人",
          color: "#1d1d1f",
          fontWeight: 500,
          fontSize: 12,
        },
        labelLine: {
          lineStyle: { color: "#c7c7cc" },
        },
        data: pieData,
        color: ["#007AFF", "#5AC8FA", "#34C759", "#FF9500", "#FF3B30"],
      },
    ],
  });
}

function handleResize(): void {
  chartInstance?.resize();
  pieChartInstance?.resize();
}

watch(viewMode, (mode) => {
  if (mode === "distribution") {
    // 释放饼图实例（DOM 已销毁），下次切回时会重建
    if (pieChartInstance) {
      pieChartInstance.dispose();
      pieChartInstance = null;
    }
    nextTick(renderDistributionChart);
  } else if (mode === "pie") {
    // 释放柱状图实例
    if (chartInstance) {
      chartInstance.dispose();
      chartInstance = null;
    }
    nextTick(renderPieChart);
  } else {
    // 切到概览表格时，两个图表 DOM 都会被销毁，释放实例
    if (chartInstance) {
      chartInstance.dispose();
      chartInstance = null;
    }
    if (pieChartInstance) {
      pieChartInstance.dispose();
      pieChartInstance = null;
    }
  }
});

onBeforeUnmount(() => {
  chartInstance?.dispose();
  chartInstance = null;
  pieChartInstance?.dispose();
  pieChartInstance = null;
  window.removeEventListener("resize", handleResize);
});

// 排名数据
const rankList = ref<RankVO[]>([]);
const rankLoading = ref(false);
const rankTotal = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 前端分页：当前页显示的排名数据
const pagedRankList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return rankList.value.slice(start, start + pageSize.value);
});

/** 加载已结束的考试列表 */
async function fetchExams() {
  try {
    const res = await ExamAPI.getPage({ page: 1, size: 200, status: 2 });
    examOptions.value = res.records ?? [];
  } catch {
    examOptions.value = [];
  }
}

/** 加载班级列表 */
async function fetchClasses() {
  try {
    if (hasRole(2)) {
      const res = await ClassAPI.list();
      classOptions.value = res ?? [];
    } else {
      const res = await ClassAPI.getMyClasses();
      classOptions.value = res ?? [];
    }
  } catch {
    classOptions.value = [];
  }
}

/** 考试选择变化 */
function handleExamChange(examId: number | undefined) {
  selectedClassId.value = undefined;
  if (!examId) {
    stat.value = null;
    rankList.value = [];
    rankTotal.value = 0;
    return;
  }
  currentPage.value = 1;
  fetchStat();
  fetchRank();
}

/** 班级筛选变化 */
function handleClassChange() {
  currentPage.value = 1;
  fetchStat();
  fetchRank();
}

/** 加载统计数据 */
async function fetchStat() {
  if (!selectedExamId.value) return;
  try {
    const res = await ScoreAPI.getExamStat(selectedExamId.value, selectedClassId.value);
    stat.value = res ?? null;
  } catch {
    stat.value = null;
  }
}

watch(stat, (val) => {
  if (val?.scoreDistribution) {
    if (viewMode.value === "distribution") {
      nextTick(renderDistributionChart);
    } else if (viewMode.value === "pie") {
      nextTick(renderPieChart);
    }
  }
});

/** 加载排名数据 */
async function fetchRank() {
  if (!selectedExamId.value) return;
  rankLoading.value = true;
  try {
    const res = await ScoreAPI.getExamRank(selectedExamId.value, selectedClassId.value);
    rankList.value = res ?? [];
    rankTotal.value = res.length ?? 0;
  } finally {
    rankLoading.value = false;
  }
}

/** 排名序号方法（考虑分页） */
function indexMethod(index: number) {
  return (currentPage.value - 1) * pageSize.value + index + 1;
}

/** 状态文本映射 */
function statusText(status?: number): string {
  if (status === 1) return "已提交";
  if (status === 2) return "超时交卷";
  if (status === 0) return "进行中";
  return "未知";
}

onMounted(() => {
  fetchExams();
  fetchClasses();
  window.addEventListener("resize", handleResize);
});
</script>

<style scoped lang="scss">
.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;

  &__label {
    font-size: 14px;
    color: var(--brand-light);
    margin-bottom: 8px;
  }

  &__value {
    font-size: 24px;
    font-weight: 600;
    color: var(--brand-dark);
  }
}

.score-pass {
  color: var(--el-color-success);
  font-weight: 600;
}

.score-fail {
  color: var(--el-color-danger);
  font-weight: 600;
}

/* 视图切换 */
.view-switcher-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.view-switcher {
  .el-radio-button__inner {
    border-radius: 8px !important;
    padding: 8px 24px;
    font-weight: 500;
    font-size: 13px;
    box-shadow: var(--brand-shadow-sm);
  }

  .el-radio-button:first-child .el-radio-button__inner {
    border-radius: 8px 0 0 8px !important;
  }

  .el-radio-button:last-child .el-radio-button__inner {
    border-radius: 0 8px 8px 0 !important;
  }

  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background: var(--brand-accent);
    border-color: var(--brand-accent) !important;
    color: #fff;
    box-shadow: var(--brand-shadow-inset);
  }
}

/* 图表容器 */
.chart-section {
  .chart-container {
    width: 100%;
    height: 380px;
  }
}

/* 分数段摘要 */
.distribution-summary {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 16px;
  padding-top: 16px;
  flex-wrap: wrap;
}

.summary-item {
  text-align: center;
  padding: 8px 20px;
  background: var(--brand-bg);
  border-radius: 8px;
  min-width: 80px;
}

.summary-range {
  font-size: 12px;
  color: var(--brand-light);
  margin-bottom: 4px;
}

.summary-count {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand-dark);
}

.summary-unit {
  font-size: 12px;
  font-weight: 400;
  color: var(--brand-muted);
  margin-left: 2px;
}
</style>
