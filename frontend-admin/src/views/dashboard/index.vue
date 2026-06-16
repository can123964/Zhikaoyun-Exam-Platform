<template>
  <div class="dashboard-page">
    <!-- 问候 -->
    <div class="greeting-header">
      <div class="greeting-avatar">
        <img v-if="userStore.userInfo.avatar" :src="userStore.userInfo.avatar" alt="" />
        <div v-else class="avatar-placeholder">
          {{ (userStore.userInfo.realName || userStore.userInfo.username || '?')[0] }}
        </div>
      </div>
      <div class="greeting-text">
        <h2>{{ greetings }}</h2>
        <p>欢迎使用智考云在线考试平台</p>
      </div>
    </div>

    <!-- 管理员统计 -->
    <template v-if="userStore.userInfo.role === 2">
      <div class="section-title">系统概览</div>
      <div class="stats-grid stats-grid-4">
        <div class="stat-card">
          <div class="stat-icon stat-icon--users">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalUsers ?? 0 }}</span>
            <span class="stat-label">总用户数</span>
          </div>
          <div class="stat-footer">学生 {{ dashboard.totalStudents ?? 0 }} · 教师 {{ dashboard.totalTeachers ?? 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--questions">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M7 3H3v4M13 3h4v4M7 17H3v-4M13 17h4v-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 7v6M7 10h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalQuestions ?? 0 }}</span>
            <span class="stat-label">题目总数</span>
          </div>
          <div class="stat-footer">错题本 {{ dashboard.totalWrongBooks ?? 0 }} 条</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--exams">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M3 4h14M3 10h14M3 16h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="15" cy="16" r="2.5" stroke="currentColor" stroke-width="1.5"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalExams ?? 0 }}</span>
            <span class="stat-label">考试总数</span>
          </div>
          <div class="stat-footer">进行中 {{ dashboard.ongoingExams ?? 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--records">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M5 3h10a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M7 7h6M7 10h6M7 13h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalRecords ?? 0 }}</span>
            <span class="stat-label">考试记录</span>
          </div>
          <div class="stat-footer">累计考试提交次数</div>
        </div>
      </div>
    </template>

    <!-- 教师统计 -->
    <template v-if="userStore.userInfo.role === 1">
      <div class="section-title">我的数据</div>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon stat-icon--questions">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M7 3H3v4M13 3h4v4M7 17H3v-4M13 17h4v-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 7v6M7 10h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalQuestions ?? 0 }}</span>
            <span class="stat-label">我的题目数</span>
          </div>
          <div class="stat-footer">题库中累计创建的题目</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--exams">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M3 4h14M3 10h14M3 16h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="15" cy="16" r="2.5" stroke="currentColor" stroke-width="1.5"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalExams ?? 0 }}</span>
            <span class="stat-label">我的考试数</span>
          </div>
          <div class="stat-footer">进行中 {{ dashboard.ongoingExams ?? 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon--users">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M13 17v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M5 5a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-body">
            <span class="stat-number">{{ dashboard.totalStudents ?? 0 }}</span>
            <span class="stat-label">我的学生数</span>
          </div>
          <div class="stat-footer">负责班级中的学生总数</div>
        </div>
      </div>
    </template>

    <!-- 图表区域 -->
    <div class="charts-grid" :class="{ 'charts-grid--single': userStore.userInfo.role === 1 }">
      <div class="chart-card chart-card--trend">
        <div class="chart-card__header">
          <h3 class="chart-card__title">近 7 天考试趋势</h3>
          <span class="chart-card__subtitle">每日考试提交数量</span>
        </div>
        <div ref="trendChartRef" class="chart-card__body chart-canvas-wrap"></div>
      </div>
      <div v-if="userStore.userInfo.role === 2" class="chart-card chart-card--pie">
        <div class="chart-card__header">
          <h3 class="chart-card__title">用户角色分布</h3>
          <span class="chart-card__subtitle">系统各角色用户占比</span>
        </div>
        <div ref="pieChartRef" class="chart-card__body chart-canvas-wrap"></div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="activity-card">
      <div class="chart-card__header">
        <h3 class="chart-card__title">最近活动</h3>
        <span class="chart-card__subtitle">实时系统动态</span>
      </div>
      <div class="activity-list" v-if="activities.length > 0">
        <div
          class="activity-item"
          v-for="(item, index) in activities"
          :key="index"
        >
          <div class="activity-dot" :class="'activity-dot--' + item.type"></div>
          <div class="activity-content">
            <span class="activity-title">{{ item.title }}</span>
            <span class="activity-subtitle" v-if="item.subtitle">{{ item.subtitle }}</span>
          </div>
          <span class="activity-time">{{ item.time }}</span>
        </div>
      </div>
      <div v-else class="activity-empty">
        <p>暂无活动记录</p>
      </div>
      <div v-if="activityTotal > 0" class="activity-pagination">
        <el-pagination
          v-model:current-page="activityPage"
          v-model:page-size="activityPageSize"
          :page-sizes="[10, 20, 30]"
          :total="activityTotal"
          layout="total, sizes, prev, pager, next"
          background
          @current-change="handleActivityPageChange"
          @size-change="handleActivitySizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onBeforeUnmount, nextTick } from "vue";
import * as echarts from "echarts/core";
import { BarChart, PieChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";
import { graphic } from "echarts/core";
import type { EChartsOption } from "echarts";
import { useUserStore } from "@/stores/user";
import DashboardAPI, {
  type DashboardVO,
  type ActivityItem,
} from "@/api/business/dashboard";

// 注册 ECharts 组件
echarts.use([
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  CanvasRenderer,
]);

defineOptions({
  name: "Dashboard",
  inheritAttrs: false,
});

const userStore = useUserStore();

const pageLoading = ref(true);
const dashboard = ref<DashboardVO>({});
const activities = ref<ActivityItem[]>([]);
const activityPage = ref(1);
const activityTotal = ref(0);
const activityPageSize = ref(10);

// 图表 DOM refs
const trendChartRef = ref<HTMLDivElement>();
const pieChartRef = ref<HTMLDivElement>();

// ECharts 实例
let trendChart: echarts.ECharts | null = null;
let pieChart: echarts.ECharts | null = null;

const greetings = computed(() => {
  const hours = new Date().getHours();
  const nickname =
    userStore.userInfo.realName || userStore.userInfo.username || "用户";
  const role = userStore.userInfo.role;
  const roleText = role === 0 ? "同学" : role === 1 ? "老师" : "管理员";
  if (hours >= 6 && hours < 12) return `上午好，${nickname} ${roleText}`;
  if (hours >= 12 && hours < 18) return `下午好，${nickname} ${roleText}`;
  if (hours >= 18 && hours < 24) return `晚上好，${nickname} ${roleText}`;
  return `夜深了，${nickname} ${roleText}，注意休息`;
});

/** 初始化趋势柱状图 */
function initTrendChart(data: { date: string; count: number }[]) {
  if (!trendChartRef.value) return;
  trendChart = echarts.init(trendChartRef.value, undefined, {
    devicePixelRatio: window.devicePixelRatio || 2,
  });

  const accent = getComputedStyle(document.documentElement)
    .getPropertyValue("--brand-accent")
    .trim() || "#007AFF";
  const muted = getComputedStyle(document.documentElement)
    .getPropertyValue("--brand-muted")
    .trim() || "#6e6e73";

  const option: EChartsOption = {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255,255,255,0.95)",
      borderColor: "#eee",
      borderWidth: 1,
      textStyle: { color: "#333", fontSize: 13 },
      formatter: (params: any) => {
        const p = params[0];
        return `<b>${p.name}</b><br/>提交次数: ${p.value}`;
      },
    },
    grid: {
      top: 20,
      right: 20,
      bottom: 30,
      left: 45,
    },
    xAxis: {
      type: "category",
      data: data.map((d) => d.date),
      axisLine: { lineStyle: { color: "#ddd" } },
      axisLabel: { color: muted, fontSize: 12 },
      axisTick: { show: false },
    },
    yAxis: {
      type: "value",
      minInterval: 1,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: muted, fontSize: 12 },
      splitLine: { lineStyle: { color: "#f0f0f0", type: "dashed" } },
    },
    series: [
      {
        type: "bar",
        data: data.map((d) => d.count),
        barWidth: "40%",
        itemStyle: {
          color: new graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: accent },
            { offset: 1, color: accent + "40" },
          ]),
          borderRadius: [6, 6, 0, 0],
        },
      },
    ],
  };

  trendChart.setOption(option);

  // 初始化后确保尺寸正确（防止 backdrop-filter 导致 Canvas 模糊）
  setTimeout(() => {
    trendChart?.resize();
  }, 0);
}

/** 初始化饼图 */
function initPieChart() {
  if (!pieChartRef.value) return;
  pieChart = echarts.init(pieChartRef.value, undefined, {
    devicePixelRatio: window.devicePixelRatio || 2,
  });

  const students = dashboard.value.totalStudents ?? 0;
  const teachers = dashboard.value.totalTeachers ?? 0;
  const admins = Math.max(
    0,
    (dashboard.value.totalUsers ?? 0) - students - teachers
  );

  const accent = getComputedStyle(document.documentElement)
    .getPropertyValue("--brand-accent")
    .trim() || "#007AFF";

  const option: EChartsOption = {
    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(255,255,255,0.95)",
      borderColor: "#eee",
      borderWidth: 1,
      textStyle: { color: "#333", fontSize: 13 },
      formatter: "{b}: {c} ({d}%)",
    },
    legend: {
      bottom: 10,
      textStyle: { fontSize: 12 },
    },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        center: ["50%", "45%"],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 8,
          borderColor: "rgba(255,255,255,0.5)",
          borderWidth: 2,
        },
        label: {
          show: true,
          formatter: "{b}\n{d}%",
          fontSize: 12,
        },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: "bold" },
        },
        data: [
          { value: students, name: "学生", itemStyle: { color: accent } },
          { value: teachers, name: "教师", itemStyle: { color: "#5AC8FA" } },
          { value: admins, name: "管理员", itemStyle: { color: "#FF9500" } },
        ],
      },
    ],
  };

  pieChart.setOption(option);

  // 初始化后确保尺寸正确（防止 backdrop-filter 导致 Canvas 模糊）
  setTimeout(() => {
    pieChart?.resize();
  }, 0);
}

/** 窗口大小变化时重绘图表 */
function handleResize() {
  trendChart?.resize();
  pieChart?.resize();
}

async function fetchOverview(): Promise<void> {
  try {
    const res = await DashboardAPI.getOverview();
    dashboard.value = res;
    await nextTick();
    // 初始化图表
    if (res.examTrend && res.examTrend.length > 0) {
      initTrendChart(res.examTrend);
    } else {
      // 没有数据时显示空状态
      initTrendChart([
        { date: "暂无", count: 0 },
      ]);
    }
    initPieChart();
  } catch {
    // ignore
  } finally {
    pageLoading.value = false;
  }
}

async function fetchActivity(): Promise<void> {
  try {
    const res = await DashboardAPI.getActivity(activityPage.value, activityPageSize.value);
    activities.value = res.list || [];
    activityTotal.value = res.total || 0;
  } catch {
    activities.value = [];
  }
}

function handleActivityPageChange(page: number) {
  activityPage.value = page;
  fetchActivity();
}

function handleActivitySizeChange(size: number) {
  activityPageSize.value = size;
  activityPage.value = 1;
  fetchActivity();
}

onMounted(() => {
  fetchOverview();
  fetchActivity();
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  trendChart?.dispose();
  pieChart?.dispose();
});
</script>

<style scoped>
/* ===== Page wrapper ===== */
.dashboard-page {
  max-width: 1100px;
  margin: 0 auto;
  min-height: 100%;
  padding-bottom: 32px;
}

/* ===== Greeting header ===== */
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

/* ===== Section title ===== */
.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--brand-dark, #1d1d1f);
  margin-bottom: 20px;
  letter-spacing: -0.2px;
}

/* ===== Stats grid ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}

.stats-grid-4 {
  grid-template-columns: repeat(4, 1fr);
}

/* ===== Stat card ===== */
.stat-card {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 24px;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.stat-card:hover {
  transform: scale(1.02);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.14),
    0 4px 12px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

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

.stat-icon--users {
  color: #007AFF;
}
.stat-icon--questions {
  color: #34C759;
}
.stat-icon--exams {
  color: #007AFF;
}
.stat-icon--records {
  color: #FF9500;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 12px;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: var(--brand-dark, #1d1d1f);
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--brand-muted, #6e6e73);
}

.stat-footer {
  font-size: 12px;
  color: var(--brand-muted, #6e6e73);
  margin-top: 4px;
}

/* ===== Charts grid ===== */
.charts-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
  margin-bottom: 28px;
}

.charts-grid--single {
  grid-template-columns: 1fr;
}

.chart-card {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 24px;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.chart-card:hover {
  transform: scale(1.02);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.14),
    0 4px 12px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.chart-card__header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-card__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--brand-dark, #1d1d1f);
  margin: 0;
}

.chart-card__subtitle {
  font-size: 12px;
  color: var(--brand-muted, #6e6e73);
}

.chart-card__body {
  width: 100%;
  height: 260px;
}

/* 图表 Canvas 容器 - 防止 backdrop-filter 导致模糊 */
.chart-canvas-wrap {
  isolation: isolate;
  position: relative;
}

/* ===== Activity card ===== */
.activity-card {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  padding: 24px;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.activity-dot--exam_submit {
  background: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.2);
}

.activity-dot--user_register {
  background: #34C759;
  box-shadow: 0 0 0 3px rgba(52, 199, 89, 0.2);
}

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

.activity-subtitle {
  font-size: 12px;
  color: var(--brand-muted, #6e6e73);
}

.activity-time {
  font-size: 12px;
  color: var(--brand-muted, #6e6e73);
  white-space: nowrap;
  flex-shrink: 0;
}

.activity-empty {
  padding: 40px 0;
  text-align: center;
}

.activity-empty p {
  color: var(--brand-muted, #6e6e73);
  font-size: 14px;
  margin: 0;
}

.activity-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  margin-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

/* ===== Responsive ===== */
@media (max-width: 900px) {
  .stats-grid-4 {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid,
  .stats-grid-4 {
    grid-template-columns: 1fr;
  }

  .greeting-card {
    padding: 24px;
  }
}
</style>
