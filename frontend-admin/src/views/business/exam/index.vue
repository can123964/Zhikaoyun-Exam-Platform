<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option label="未发布" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="关键字" prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            placeholder="考试名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">搜索</el-button>
          <el-button icon="refresh" @click="handleResetQuery">重置</el-button>
          <el-button type="success" icon="plus" @click="handleCreateClick">新增</el-button>
        </el-form-item>
      </el-form>
    </div>

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
        <el-table-column label="考试名称" prop="title" min-width="160" />
        <el-table-column label="时长(分钟)" prop="duration" width="110" align="center" />
        <el-table-column label="开始时间" prop="startTime" width="170" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" prop="endTime" width="170" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusTagMap[scope.row.status]">{{ statusLabelMap[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="完成进度" width="120" align="center">
          <template #default="scope">
            <span v-if="scope.row.status === 1 && scope.row.studentCount">
              {{ scope.row.submittedCount ?? 0 }} / {{ scope.row.studentCount }}
            </span>
            <span v-else-if="scope.row.status === 2 && scope.row.studentCount">
              {{ scope.row.submittedCount ?? 0 }} / {{ scope.row.studentCount }}
            </span>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="总分" prop="totalScore" width="80" align="center">
          <template #default="scope">
            {{ scope.row.totalScore ?? "—" }}
          </template>
        </el-table-column>
        <el-table-column label="及格分" prop="passScore" width="80" align="center">
          <template #default="scope">
            {{ scope.row.passScore ?? "—" }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="300" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              icon="edit"
              link
              size="small"
              :disabled="scope.row.status !== 0 || !canOperate(scope.row)"
              @click="handleEditClick(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              icon="delete"
              link
              size="small"
              :disabled="scope.row.status !== 0 || !canOperate(scope.row)"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
            <el-button
              type="success"
              icon="VideoPlay"
              link
              size="small"
              :disabled="scope.row.status !== 0 || !canOperate(scope.row)"
              @click="handlePublish(scope.row)"
            >
              发布
            </el-button>
            <el-button
              type="warning"
              icon="VideoPause"
              link
              size="small"
              :disabled="scope.row.status !== 1 || !canOperate(scope.row)"
              @click="handleEnd(scope.row)"
            >
              结束
            </el-button>
            <el-button
              type="primary"
              icon="DocumentAdd"
              link
              size="small"
              :disabled="scope.row.status !== 0 || !canOperate(scope.row)"
              @click="handlePaperClick(scope.row)"
            >
              组卷
            </el-button>
            <el-button
              type="info"
              icon="View"
              link
              size="small"
              @click="handleViewQuestions(scope.row)"
            >
              题目
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      append-to-body
      @close="closeDialog"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="考试名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入考试名称" />
        </el-form-item>

        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="formData.examType" style="width: 100%">
            <el-option label="正式考试" :value="0" />
            <el-option label="模拟考试" :value="1" />
            <el-option label="练习" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入考试描述（可选）"
          />
        </el-form-item>

        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="formData.duration" :min="1" :max="600" controls-position="right" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="formData.totalScore" :min="0" controls-position="right" />
        </el-form-item>

        <el-form-item label="及格分" prop="passScore">
          <el-input-number v-model="formData.passScore" :min="0" controls-position="right" />
        </el-form-item>

        <el-form-item label="显示答案" prop="showAnswer">
          <el-switch v-model="formData.showAnswer" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <el-form-item label="允许重考" prop="allowRetry">
          <el-switch v-model="formData.allowRetry" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <el-form-item label="题目顺序" prop="questionOrder">
          <el-switch
            v-model="formData.questionOrder"
            :active-value="1"
            :inactive-value="0"
            active-text="随机"
            inactive-text="固定"
          />
        </el-form-item>

        <el-form-item label="选项顺序" prop="optionOrder">
          <el-switch
            v-model="formData.optionOrder"
            :active-value="1"
            :inactive-value="0"
            active-text="随机"
            inactive-text="固定"
          />
        </el-form-item>

        <el-form-item label="切屏次数限制" prop="maxTabSwitches">
          <el-input-number v-model="formData.maxTabSwitches" :min="0" controls-position="right" placeholder="0表示不限制" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注（可选）"
          />
        </el-form-item>

        <el-form-item label="关联班级" prop="classIds">
          <el-checkbox-group v-model="formData.classIds">
            <el-checkbox
              v-for="cls in classList"
              :key="cls.id"
              :value="cls.id"
              :label="cls.id"
            >
              {{ cls.className }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 组卷弹窗 -->
    <el-dialog
      v-model="paperDialogVisible"
      title="组卷"
      width="800px"
      append-to-body
      @close="closePaperDialog"
    >
      <el-tabs v-model="paperTab">
        <!-- 手动组卷 -->
        <el-tab-pane label="手动组卷" name="manual">
          <div class="filter-section" style="margin-bottom: 12px">
            <el-form :inline="true" :model="questionQueryParams">
              <el-form-item label="题型">
                <el-select v-model="questionQueryParams.type" placeholder="全部" clearable style="width: 110px">
                  <el-option label="单选" :value="0" />
                  <el-option label="多选" :value="1" />
                  <el-option label="判断" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item label="关键字">
                <el-input v-model="questionQueryParams.keyword" placeholder="题目内容" clearable @keyup.enter="fetchQuestionList" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="search" @click="fetchQuestionList">搜索</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table
            ref="questionTableRef"
            v-loading="questionLoading"
            :data="questionList"
            :row-key="(row: any) => row.id"
            border
            stripe
            max-height="350"
            @selection-change="handleQuestionSelect"
          >
            <el-table-column type="selection" :reserve-selection="true" width="50" align="center" />
            <el-table-column label="题号" prop="id" width="70" align="center" />
            <el-table-column label="题型" prop="type" width="80" align="center">
              <template #default="scope">
                <el-tag :type="typeTagMap[scope.row.type]">{{ typeLabelMap[scope.row.type] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="题目内容" prop="content" min-width="200" />
            <el-table-column label="难度" prop="difficulty" width="80" align="center">
              <template #default="scope">
                <el-tag :type="difficultyTagMap[scope.row.difficulty]">{{ difficultyLabelMap[scope.row.difficulty] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="分值" width="100" align="center">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row._score"
                  :min="1"
                  :max="100"
                  size="small"
                  controls-position="right"
                />
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-if="questionTotal > 0"
            v-model:total="questionTotal"
            v-model:page="questionQueryParams.page"
            v-model:limit="questionQueryParams.size"
            @pagination="fetchQuestionList"
          />
        </el-tab-pane>

        <!-- 随机组卷（支持多条规则，可对每种题型分别设置数量/分值/难度） -->
        <el-tab-pane label="随机组卷" name="random">
          <div style="margin-top: 12px">
            <!-- 已有规则列表 -->
            <el-table :data="randomRules" border stripe size="small" style="margin-bottom: 12px">
              <el-table-column label="题型" width="90" align="center">
                <template #default="scope">
                  <el-tag size="small">{{ ['单选','多选','判断'][scope.row.type] }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="难度" width="80" align="center">
                <template #default="scope">
                  {{ scope.row.difficulty ? ['','简单','中等','困难'][scope.row.difficulty] : '不限' }}
                </template>
              </el-table-column>
              <el-table-column label="分类" min-width="100">
                <template #default="scope">
                  {{ scope.row.category || '不限' }}
                </template>
              </el-table-column>
              <el-table-column label="数量" width="70" align="center" prop="count" />
              <el-table-column label="分值/题" width="80" align="center" prop="score" />
              <el-table-column label="操作" width="60" align="center">
                <template #default="scope">
                  <el-button type="danger" icon="delete" link size="small" @click="removeRule(scope.$index)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 添加规则 -->
            <el-card shadow="never" style="margin-bottom: 12px">
              <template #header><span>添加抽题规则</span></template>
              <el-row :gutter="12">
                <el-col :span="6">
                  <el-form-item label="题型" label-width="50px">
                    <el-select v-model="newRule.type" style="width: 100%">
                      <el-option label="单选" :value="0" />
                      <el-option label="多选" :value="1" />
                      <el-option label="判断" :value="2" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="难度" label-width="50px">
                    <el-select v-model="newRule.difficulty" placeholder="不限" clearable style="width: 100%">
                      <el-option label="简单" :value="1" />
                      <el-option label="中等" :value="2" />
                      <el-option label="困难" :value="3" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label="分类" label-width="50px">
                    <el-select v-model="newRule.category" placeholder="不限" clearable filterable style="width: 100%">
                      <el-option v-for="cat in categoryOptions" :key="cat" :label="cat" :value="cat" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item label="数量" label-width="50px">
                    <el-input-number v-model="newRule.count" :min="1" :max="200" controls-position="right" style="width: 100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item label="分值" label-width="50px">
                    <el-input-number v-model="newRule.score" :min="1" :max="100" controls-position="right" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-button type="primary" icon="Plus" @click="addRule">添加规则</el-button>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="paperLoading" @click="handlePaperSubmit">确认</el-button>
          <el-button @click="closePaperDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看题目弹窗 -->
    <el-dialog
      v-model="viewQuestionsVisible"
      title="考试题目"
      width="750px"
      append-to-body
      @close="closeViewQuestions"
    >
      <el-table
        v-loading="viewQuestionsLoading"
        :data="examQuestionList"
        border
        stripe
        max-height="450"
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="题型" prop="type" width="80" align="center">
          <template #default="scope">
            <el-tag :type="typeTagMap[scope.row.type]">{{ typeLabelMap[scope.row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="题目内容" prop="content" min-width="250" />
        <el-table-column label="分值" prop="score" width="80" align="center" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button
              type="danger"
              icon="delete"
              link
              size="small"
              :disabled="currentExam?.status !== 0"
              @click="handleRemoveQuestion(scope.row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useDebounceFn } from "@vueuse/core";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import ExamAPI, { type ExamVO, type ExamCreateForm, type ExamUpdateForm, type ExamQueryParams, type ExamQuestionVO, type RandomPaperForm, type RandomRule } from "@/api/business/exam";
import QuestionAPI, { type QuestionVO, type QuestionQueryParams } from "@/api/business/question";
import ClassAPI, { type ClassRoomVO } from "@/api/business/class";
import { useUserStore } from "@/stores/user";

defineOptions({
  name: "ExamManage",
  inheritAttrs: false,
});

// 当前用户（用于操作权限判断）
const userStore = useUserStore();

/** 判断当前用户是否有权操作该考试（管理员可操作全部，教师只能操作自己的） */
function canOperate(exam: ExamVO): boolean {
  // 管理员可以操作全部
  if (userStore.userInfo.role === 2) return true;
  // 教师只能操作自己创建的
  return userStore.userInfo.id === exam.creatorId;
}

// 映射表
const statusLabelMap: Record<number, string> = { 0: "未发布", 1: "进行中", 2: "已结束" };
const statusTagMap: Record<number, "primary" | "success" | "warning" | "info" | "danger"> = { 0: "info", 1: "success", 2: "danger" };
const typeLabelMap: Record<number, string> = { 0: "单选", 1: "多选", 2: "判断" };
const typeTagMap: Record<number, "primary" | "success" | "warning" | "info" | "danger"> = { 0: "primary", 1: "warning", 2: "success" };
const difficultyLabelMap: Record<number, string> = { 1: "简单", 2: "中等", 3: "困难" };
const difficultyTagMap: Record<number, "primary" | "success" | "warning" | "info" | "danger"> = { 1: "success", 2: "warning", 3: "danger" };

// 时间格式化
function formatTime(time?: string): string {
  if (!time) return "—";
  return time.replace("T", " ").substring(0, 19);
}

// ========== 列表相关 ==========
const queryFormRef = ref<FormInstance>();
const queryParams = reactive<ExamQueryParams>({
  page: 1,
  size: 10,
  status: undefined,
  keyword: undefined,
});

const tableData = ref<ExamVO[]>([]);
const total = ref(0);
const loading = ref(false);

async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const res = await ExamAPI.getPage(queryParams);
    tableData.value = res.records ?? [];
    total.value = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleQuery(): void {
  queryParams.page = 1;
  fetchList();
}

function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  queryParams.page = 1;
  queryParams.size = 10;
  fetchList();
}

function handlePagination(): void {
  fetchList();
}

// ========== 新增/编辑弹窗 ==========
const formRef = ref<FormInstance>();
const dialogVisible = ref(false);
const dialogTitle = ref("新增考试");
const submitLoading = ref(false);

const classList = ref<ClassRoomVO[]>([]);

interface FormData extends ExamCreateForm {
  id?: number;
}

const initialFormData: FormData = {
  title: "",
  description: "",
  examType: 0,
  duration: 60,
  startTime: "",
  endTime: "",
  totalScore: 100,
  passScore: 60,
  showAnswer: 0,
  allowRetry: 0,
  questionOrder: 0,
  optionOrder: 0,
  maxTabSwitches: 0,
  remark: "",
  classIds: [],
};

const formData = reactive<FormData>({ ...initialFormData });

const rules: FormRules = {
  title: [{ required: true, message: "请输入考试名称", trigger: "blur" }],
  duration: [{ required: true, message: "请输入考试时长", trigger: "blur" }],
  startTime: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  endTime: [{ required: true, message: "请选择结束时间", trigger: "change" }],
  // classIds 不设必填 — 空数组表示不限制班级，所有学生可见
  examType: [{ required: true, message: "请选择考试类型", trigger: "change" }],
};

function openDialog(): void {
  dialogVisible.value = true;
}

function closeDialog(): void {
  dialogVisible.value = false;
  formRef.value?.resetFields();
  formRef.value?.clearValidate();
  Object.assign(formData, { ...initialFormData });
}

function handleCreateClick(): void {
  dialogTitle.value = "新增考试";
  Object.assign(formData, { ...initialFormData });
  openDialog();
}

function handleEditClick(row: ExamVO): void {
  dialogTitle.value = "修改考试";
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    description: row.description ?? "",
    duration: row.duration,
    startTime: row.startTime ? row.startTime.replace("T", " ").substring(0, 19) : "",
    endTime: row.endTime ? row.endTime.replace("T", " ").substring(0, 19) : "",
    totalScore: row.totalScore ?? 100,
    passScore: row.passScore ?? 60,
    showAnswer: row.showAnswer ?? 0,
    allowRetry: row.allowRetry ?? 0,
    questionOrder: row.questionOrder ?? 0,
    optionOrder: row.optionOrder ?? 0,
    maxTabSwitches: row.maxTabSwitches ?? 0,
    remark: row.remark ?? "",
    classIds: row.classIds ?? [],
  });
  openDialog();
}

const handleSubmit = useDebounceFn(async () => {
  const valid = await formRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  submitLoading.value = true;
  try {
    if (formData.id) {
      const updateData: ExamUpdateForm = {
        title: formData.title,
        description: formData.description || undefined,
        examType: formData.examType,
        duration: formData.duration,
        startTime: formData.startTime,
        endTime: formData.endTime,
        totalScore: formData.totalScore,
        passScore: formData.passScore,
        showAnswer: formData.showAnswer,
        allowRetry: formData.allowRetry,
        questionOrder: formData.questionOrder,
        optionOrder: formData.optionOrder,
        maxTabSwitches: formData.maxTabSwitches,
        remark: formData.remark || undefined,
        classIds: formData.classIds,
      };
      await ExamAPI.update(formData.id, updateData);
      ElMessage.success("修改考试成功");
    } else {
      const createData: ExamCreateForm = {
        title: formData.title,
        description: formData.description || undefined,
        examType: formData.examType,
        duration: formData.duration,
        startTime: formData.startTime,
        endTime: formData.endTime,
        totalScore: formData.totalScore,
        passScore: formData.passScore,
        showAnswer: formData.showAnswer,
        allowRetry: formData.allowRetry,
        questionOrder: formData.questionOrder,
        optionOrder: formData.optionOrder,
        maxTabSwitches: formData.maxTabSwitches,
        remark: formData.remark || undefined,
        classIds: formData.classIds,
      };
      await ExamAPI.create(createData);
      ElMessage.success("新增考试成功");
    }
    closeDialog();
    fetchList();
  } finally {
    submitLoading.value = false;
  }
}, 300);

// ========== 删除/发布/结束 ==========
async function deleteExam(id: number): Promise<void> {
  await ExamAPI.delete(id);
  ElMessage.success("删除成功");
  fetchList();
}

function handleDelete(row: ExamVO): void {
  ElMessageBox.confirm(`确认删除考试「${row.title}」吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => deleteExam(row.id),
    () => {}
  );
}

function handlePublish(row: ExamVO): void {
  ElMessageBox.confirm(`确认发布考试「${row.title}」吗？发布后不可编辑。`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  }).then(
    async () => {
      await ExamAPI.publish(row.id);
      ElMessage.success("发布成功");
      fetchList();
    },
    () => {}
  );
}

function handleEnd(row: ExamVO): void {
  ElMessageBox.confirm(`确认结束考试「${row.title}」吗？结束后学生将无法继续答题。`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    async () => {
      await ExamAPI.end(row.id);
      ElMessage.success("考试已结束");
      fetchList();
    },
    () => {}
  );
}

// ========== 组卷弹窗 ==========
const paperDialogVisible = ref(false);
const paperTab = ref("manual");
const paperLoading = ref(false);
const currentExam = ref<ExamVO | null>(null);

// 手动组卷 - 题库列表
const questionQueryParams = reactive<QuestionQueryParams>({
  page: 1,
  size: 10,
  type: undefined,
  keyword: undefined,
});
const questionTableRef = ref();
const questionList = ref<(QuestionVO & { _score: number })[]>([]);
const questionTotal = ref(0);
const questionLoading = ref(false);
const categoryOptions = ref<string[]>([]);

async function fetchQuestionList(): Promise<void> {
  questionLoading.value = true;
  try {
    const res = await QuestionAPI.getPage(questionQueryParams);
    questionList.value = (res.records ?? []).map((q) => ({ ...q, _score: 5 }));
    questionTotal.value = res.total ?? 0;
  } finally {
    questionLoading.value = false;
  }
}

async function fetchSelections(): Promise<void> {
  try {
    const res = await QuestionAPI.getSelections();
    categoryOptions.value = res.categories ?? [];
  } catch {
    categoryOptions.value = [];
  }
}

function handleQuestionSelect(_rows: (QuestionVO & { _score: number })[]): void {
  // 使用 reserve-selection + row-key 自动维护跨页选择，无需手动存储
}

// 随机组卷表单 - 规则列表
const randomRules = ref<RandomRule[]>([]);
const newRule = reactive<RandomRule>({
  type: 0,
  count: 5,
  score: 5,
});

function addRule(): void {
  if (newRule.count <= 0 || newRule.score <= 0) {
    ElMessage.warning("数量和分值必须大于0");
    return;
  }
  randomRules.value.push({ ...newRule });
  // 重置默认值
  newRule.type = 0;
  newRule.difficulty = undefined;
  newRule.category = undefined;
  newRule.count = 5;
  newRule.score = 5;
}

function removeRule(index: number): void {
  randomRules.value.splice(index, 1);
}

function handlePaperClick(row: ExamVO): void {
  currentExam.value = row;
  paperTab.value = "manual";
  questionTableRef.value?.clearSelection();
  randomRules.value = [];
  newRule.type = 0;
  newRule.count = 5;
  newRule.score = 5;
  questionQueryParams.page = 1;
  questionQueryParams.type = undefined;
  questionQueryParams.keyword = undefined;
  paperDialogVisible.value = true;
  fetchQuestionList();
  fetchSelections();
}

function closePaperDialog(): void {
  paperDialogVisible.value = false;
  currentExam.value = null;
  questionTableRef.value?.clearSelection();
}

async function handlePaperSubmit(): Promise<void> {
  if (!currentExam.value) return;

  if (paperTab.value === "manual") {
    const selectedRows = questionTableRef.value?.getSelectionRows() ?? [];
    if (selectedRows.length === 0) {
      ElMessage.warning("请至少选择一道题目");
      return;
    }
    paperLoading.value = true;
    try {
      await Promise.all(selectedRows.map(q => ExamAPI.addQuestion(currentExam.value.id, q.id, q._score)));
      ElMessage.success(`成功添加 ${selectedRows.length} 道题目`);
      closePaperDialog();
      fetchList();
    } finally {
      paperLoading.value = false;
    }
  } else {
    // 随机组卷
    if (randomRules.value.length === 0) {
      ElMessage.warning("请至少添加一条抽题规则");
      return;
    }
    paperLoading.value = true;
    try {
      const result = await ExamAPI.randomPaper(currentExam.value.id, {
        rules: randomRules.value,
      });
      const count = result?.length ?? 0;
      ElMessage.success(`随机组卷成功，已添加 ${count} 道题目`);
      closePaperDialog();
      fetchList();
    } finally {
      paperLoading.value = false;
    }
  }
}

// ========== 查看题目弹窗 ==========
const viewQuestionsVisible = ref(false);
const viewQuestionsLoading = ref(false);
const examQuestionList = ref<ExamQuestionVO[]>([]);

function handleViewQuestions(row: ExamVO): void {
  currentExam.value = row;
  viewQuestionsVisible.value = true;
  fetchExamQuestions(row.id);
}

async function fetchExamQuestions(examId: number): Promise<void> {
  viewQuestionsLoading.value = true;
  try {
    const res = await ExamAPI.getQuestions(examId);
    examQuestionList.value = res ?? [];
  } finally {
    viewQuestionsLoading.value = false;
  }
}

function closeViewQuestions(): void {
  viewQuestionsVisible.value = false;
  examQuestionList.value = [];
  currentExam.value = null;
}

function handleRemoveQuestion(row: ExamQuestionVO): void {
  if (!currentExam.value) return;
  ElMessageBox.confirm("确认移除该题目吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    async () => {
      await ExamAPI.removeQuestion(currentExam.value!.id, row.questionId);
      ElMessage.success("移除成功");
      fetchExamQuestions(currentExam.value!.id);
    },
    () => {}
  );
}

// ========== 加载班级列表 ==========
async function fetchClassList(): Promise<void> {
  try {
    const res = userStore.userInfo.role === 2
      ? await ClassAPI.list()
      : await ClassAPI.getMyClasses();
    classList.value = res ?? [];
  } catch {
    classList.value = [];
  }
}

// ========== 初始化 ==========
onMounted(() => {
  fetchList();
  fetchClassList();
});
</script>

