<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
        <el-form-item label="题型" prop="type">
          <el-select
            v-model="queryParams.type"
            placeholder="全部"
            clearable
            style="width: 120px"
            @change="handleQuery"
          >
            <el-option label="单选" :value="0" />
            <el-option label="多选" :value="1" />
            <el-option label="判断" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="难度" prop="difficulty">
          <el-select
            v-model="queryParams.difficulty"
            placeholder="全部"
            clearable
            style="width: 120px"
            @change="handleQuery"
          >
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select
            v-model="queryParams.category"
            placeholder="全部"
            clearable
            style="width: 140px"
            @change="handleQuery"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="关键字" prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            placeholder="题目内容"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">搜索</el-button>
          <el-button icon="refresh" @click="handleResetQuery">重置</el-button>
          <el-button type="success" icon="plus" @click="handleCreateClick">新增</el-button>
          <el-button type="warning" icon="upload" @click="handleImportClick">导入</el-button>
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
        <el-table-column label="题号" prop="id" width="70" align="center" />
        <el-table-column label="题型" prop="type" width="90" align="center">
          <template #default="scope">
            <el-tag :type="typeTagMap[scope.row.type]">{{ typeLabelMap[scope.row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="题目内容" prop="content" min-width="200" />
        <el-table-column label="难度" prop="difficulty" width="90" align="center">
          <template #default="scope">
            <el-tag :type="difficultyTagMap[scope.row.difficulty]">{{ difficultyLabelMap[scope.row.difficulty] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类" prop="category" width="120" align="center">
          <template #default="scope">
            {{ scope.row.category || "—" }}
          </template>
        </el-table-column>
        <el-table-column label="答案" prop="answer" width="80" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="170" align="center">
          <template #default="scope">
            {{ scope.row.createTime || "—" }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" icon="edit" link size="small" @click="handleEditClick(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" icon="delete" link size="small" @click="handleDelete(scope.row)">
              删除
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
      width="600px"
      append-to-body
      @close="closeDialog"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="题型" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange">
            <el-radio :value="0">单选</el-radio>
            <el-radio :value="1">多选</el-radio>
            <el-radio :value="2">判断</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="3"
            placeholder="请输入题目内容"
          />
        </el-form-item>

        <el-form-item label="选项A" prop="optionA">
          <el-input
            v-model="formData.optionA"
            :placeholder="formData.type === 2 ? '对' : '请输入选项A'"
            :disabled="formData.type === 2"
          />
        </el-form-item>

        <el-form-item label="选项B" prop="optionB">
          <el-input
            v-model="formData.optionB"
            :placeholder="formData.type === 2 ? '错' : '请输入选项B'"
            :disabled="formData.type === 2"
          />
        </el-form-item>

        <el-form-item v-if="formData.type !== 2" label="选项C" prop="optionC">
          <el-input v-model="formData.optionC" placeholder="请输入选项C（可选）" />
        </el-form-item>

        <el-form-item v-if="formData.type !== 2" label="选项D" prop="optionD">
          <el-input v-model="formData.optionD" placeholder="请输入选项D（可选）" />
        </el-form-item>

        <el-form-item label="正确答案" prop="answer">
          <!-- 单选题 -->
          <el-radio-group v-if="formData.type === 0" v-model="formData.answer">
            <el-radio value="A">A</el-radio>
            <el-radio value="B">B</el-radio>
            <el-radio v-if="formData.optionC" value="C">C</el-radio>
            <el-radio v-if="formData.optionD" value="D">D</el-radio>
          </el-radio-group>
          <!-- 多选题 -->
          <el-checkbox-group v-else-if="formData.type === 1" v-model="multiAnswer">
            <el-checkbox value="A">A</el-checkbox>
            <el-checkbox value="B">B</el-checkbox>
            <el-checkbox v-if="formData.optionC" value="C">C</el-checkbox>
            <el-checkbox v-if="formData.optionD" value="D">D</el-checkbox>
          </el-checkbox-group>
          <!-- 判断题 -->
          <el-radio-group v-else v-model="formData.answer">
            <el-radio value="A">对</el-radio>
            <el-radio value="B">错</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="formData.difficulty">
            <el-radio :value="1">简单</el-radio>
            <el-radio :value="2">中等</el-radio>
            <el-radio :value="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-input v-model="formData.category" placeholder="请输入分类（可选）" />
        </el-form-item>

        <el-form-item label="解析" prop="explanation">
          <el-input
            v-model="formData.explanation"
            type="textarea"
            :rows="3"
            placeholder="请输入解析（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入题目"
      width="450px"
      append-to-body
      @close="closeImportDialog"
    >
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
        :on-exceed="() => ElMessage.warning('只能上传一个文件')"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx / .xls 格式文件</div>
        </template>
      </el-upload>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="importLoading" @click="handleImportSubmit">确认导入</el-button>
          <el-button @click="closeImportDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from "vue";
import { useDebounceFn } from "@vueuse/core";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from "element-plus";
import { UploadFilled } from "@element-plus/icons-vue";
import QuestionAPI, { type QuestionVO, type QuestionForm, type QuestionQueryParams, type SelectionVO, type ImportResultVO } from "@/api/business/question";

defineOptions({
  name: "QuestionManage",
  inheritAttrs: false,
});

const queryFormRef = ref<FormInstance>();
const formRef = ref<FormInstance>();
const uploadRef = ref<any>();

// 映射表
const typeLabelMap: Record<number, string> = { 0: "单选", 1: "多选", 2: "判断" };
const typeTagMap: Record<number, "primary" | "success" | "warning" | "info" | "danger"> = { 0: "primary", 1: "warning", 2: "success" };
const difficultyLabelMap: Record<number, string> = { 1: "简单", 2: "中等", 3: "困难" };
const difficultyTagMap: Record<number, "primary" | "success" | "warning" | "info" | "danger"> = { 1: "success", 2: "warning", 3: "danger" };

// 分类选项
const categoryOptions = ref<string[]>([]);

// 查询参数
const queryParams = reactive<QuestionQueryParams>({
  page: 1,
  size: 10,
  type: undefined,
  difficulty: undefined,
  category: undefined,
  keyword: undefined,
});

// 列表数据
const tableData = ref<QuestionVO[]>([]);
const total = ref(0);
const loading = ref(false);

// 弹窗状态
const dialogVisible = ref(false);
const dialogTitle = ref("新增题目");

// 多选题答案用数组
const multiAnswer = ref<string[]>([]);

// 表单初始数据
const initialFormData: QuestionForm & { id?: number } = {
  type: 0,
  content: "",
  optionA: "",
  optionB: "",
  optionC: "",
  optionD: "",
  answer: "",
  explanation: "",
  difficulty: 1,
  category: "",
};

const formData = reactive<QuestionForm & { id?: number }>({ ...initialFormData });

const rules: FormRules = {
  type: [{ required: true, message: "请选择题型", trigger: "change" }],
  content: [{ required: true, message: "请输入题目内容", trigger: "blur" }],
  optionA: [{ required: true, message: "请输入选项A", trigger: "blur" }],
  optionB: [{ required: true, message: "请输入选项B", trigger: "blur" }],
  answer: [{ required: true, message: "请选择正确答案", trigger: "change" }],
  difficulty: [{ required: true, message: "请选择难度", trigger: "change" }],
};

// 导入弹窗
const importDialogVisible = ref(false);
const importLoading = ref(false);
const importFile = ref<File | null>(null);

/** 加载分类选项 */
async function fetchSelections(): Promise<void> {
  try {
    const res = await QuestionAPI.getSelections();
    categoryOptions.value = res.categories ?? [];
  } catch {
    categoryOptions.value = [];
  }
}

/** 加载列表 */
async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const res = await QuestionAPI.getPage(queryParams);
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

/** 题型切换处理 */
function handleTypeChange(val: string | number | boolean | undefined): void {
  if (val === 2) {
    // 判断题：自动填充选项和答案
    formData.optionA = "对";
    formData.optionB = "错";
    formData.optionC = "";
    formData.optionD = "";
    formData.answer = "";
    multiAnswer.value = [];
  } else {
    // 单选/多选：清空选项让用户填写
    formData.optionA = "";
    formData.optionB = "";
    formData.optionC = "";
    formData.optionD = "";
    formData.answer = "";
    multiAnswer.value = [];
  }
}

/** 多选答案同步到 formData.answer */
watch(multiAnswer, (val) => {
  if (formData.type === 1) {
    formData.answer = [...val].sort().join("");
  }
});

/** 编辑时从 answer 字符串解析多选答案 */
function parseMultiAnswer(answer: string): string[] {
  return answer ? answer.split("").sort() : [];
}

function openDialog(): void {
  dialogVisible.value = true;
}

function closeDialog(): void {
  dialogVisible.value = false;
  formRef.value?.resetFields();
  formRef.value?.clearValidate();
  Object.assign(formData, { ...initialFormData });
  multiAnswer.value = [];
}

function handleCreateClick(): void {
  dialogTitle.value = "新增题目";
  Object.assign(formData, { ...initialFormData });
  multiAnswer.value = [];
  openDialog();
}

function handleEditClick(row: QuestionVO): void {
  dialogTitle.value = "修改题目";
  Object.assign(formData, {
    id: row.id,
    type: row.type,
    content: row.content,
    optionA: row.optionA,
    optionB: row.optionB,
    optionC: row.optionC ?? "",
    optionD: row.optionD ?? "",
    answer: row.answer,
    explanation: row.explanation ?? "",
    difficulty: row.difficulty,
    category: row.category ?? "",
  });
  if (row.type === 1) {
    multiAnswer.value = parseMultiAnswer(row.answer);
  } else {
    multiAnswer.value = [];
  }
  openDialog();
}

/** 提交表单 */
const handleSubmit = useDebounceFn(async () => {
  const valid = await formRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  loading.value = true;
  try {
    const submitData: QuestionForm = {
      type: formData.type,
      content: formData.content,
      optionA: formData.optionA,
      optionB: formData.optionB,
      optionC: formData.type !== 2 ? formData.optionC : undefined,
      optionD: formData.type !== 2 ? formData.optionD : undefined,
      answer: formData.answer,
      explanation: formData.explanation || undefined,
      difficulty: formData.difficulty,
      category: formData.category || undefined,
    };

    if (formData.id) {
      await QuestionAPI.update(formData.id, submitData);
      ElMessage.success("修改题目成功");
    } else {
      await QuestionAPI.create(submitData);
      ElMessage.success("新增题目成功");
    }
    closeDialog();
    fetchList();
    fetchSelections();
  } finally {
    loading.value = false;
  }
}, 300);

/** 删除题目 */
async function deleteQuestion(id: number): Promise<void> {
  await QuestionAPI.delete(id);
  ElMessage.success("删除成功");
  fetchList();
}

function handleDelete(row: QuestionVO): void {
  ElMessageBox.confirm(`确认删除该题目吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => deleteQuestion(row.id),
    () => {}
  );
}

/** 导入 */
function handleImportClick(): void {
  importFile.value = null;
  importDialogVisible.value = true;
}

function closeImportDialog(): void {
  importDialogVisible.value = false;
  importFile.value = null;
  uploadRef.value?.clearFiles();
}

function handleFileChange(file: UploadFile): void {
  importFile.value = file.raw ?? null;
}

async function handleImportSubmit(): Promise<void> {
  if (!importFile.value) {
    ElMessage.warning("请先选择文件");
    return;
  }
  importLoading.value = true;
  try {
    const res = await QuestionAPI.importExcel(importFile.value);
    ElMessage.success(`导入完成：成功 ${res.successCount} 条，失败 ${res.failCount} 条`);
    closeImportDialog();
    fetchList();
    fetchSelections();
  } finally {
    importLoading.value = false;
  }
}

onMounted(() => {
  fetchList();
  fetchSelections();
});
</script>

