<template>
  <div class="app-container">
    <div class="filter-section">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
        <el-form-item label="关键字" prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            placeholder="班级名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="年级" prop="grade">
          <el-select
            v-model="queryParams.grade"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in gradeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
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
        :data="pagedList"
        border
        stripe
        highlight-current-row
        class="table-section__content"
        row-key="id"
      >
        <el-table-column label="班级名称" prop="className" min-width="140" />
        <el-table-column label="年级" prop="grade" width="120" align="center" />
        <el-table-column label="班主任" prop="teacherName" width="120" align="center">
          <template #default="scope">
            {{ scope.row.teacherName || "—" }}
          </template>
        </el-table-column>
        <el-table-column label="学生人数" prop="studentCount" width="100" align="center">
          <template #default="scope">
            {{ scope.row.studentCount ?? 0 }}
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150">
          <template #default="scope">
            {{ scope.row.remark || "—" }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center" />
        <el-table-column label="操作" fixed="right" width="280" align="center">
          <template #default="scope">
            <el-button type="primary" icon="edit" link size="small" @click="handleEditClick(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" icon="delete" link size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
            <el-button type="info" icon="user" link size="small" @click="handleViewStudents(scope.row)">
              查看学生
            </el-button>
            <el-button type="warning" icon="data-line" link size="small" @click="handleViewScores(scope.row)">
              查看成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="filteredList.length > 0"
        v-model:total="filteredList.length"
        v-model:page="currentPage"
        v-model:limit="pageSize"
        @pagination="handlePagination"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      append-to-body
      @close="closeDialog"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="formData.className" placeholder="请输入班级名称" />
        </el-form-item>

        <el-form-item label="年级" prop="grade">
          <el-select v-model="formData.grade" placeholder="请选择年级" style="width: 100%">
            <el-option
              v-for="item in gradeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="班主任" prop="teacherId">
          <el-select v-model="formData.teacherId" placeholder="请选择班主任（可选）" clearable style="width: 100%">
            <el-option
              v-for="t in teacherOptions"
              :key="t.id"
              :label="t.realName + ' (' + t.username + ')'"
              :value="t.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="备注（可选）"
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

    <!-- 查看学生弹窗 -->
    <el-dialog
      v-model="studentDialogVisible"
      :title="'班级学生列表 - ' + (currentClassName || '')"
      width="700px"
      append-to-body
    >
      <template #header>
        <span>班级学生列表 - {{ currentClassName }}</span>
        <el-button type="primary" size="small" style="margin-left: 16px" @click="handleAddStudentClick">
          + 添加学生
        </el-button>
      </template>

      <el-table v-loading="studentLoading" :data="studentList" border stripe>
        <el-table-column label="用户名" prop="username" min-width="120" />
        <el-table-column label="姓名" prop="realName" width="100" align="center" />
        <el-table-column label="学号" prop="studentNo" width="130" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button type="danger" link size="small" @click="handleRemoveStudent(scope.row)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!studentLoading && studentList.length === 0" description="暂无学生" />

      <!-- 添加学生子弹窗 -->
      <el-dialog
        v-model="addStudentDialogVisible"
        title="添加学生到班级"
        width="500px"
        append-to-body
      >
        <el-form :inline="true">
          <el-form-item label="搜索学生">
            <el-input
              v-model="studentSearchKeyword"
              placeholder="按姓名/学号搜索"
              clearable
              style="width: 220px"
              @keyup.enter="searchStudents"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchStudents">搜索</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="studentSearchLoading" :data="candidateStudents" border stripe max-height="300">
          <el-table-column label="用户名" prop="username" min-width="110" />
          <el-table-column label="姓名" prop="realName" width="100" align="center" />
          <el-table-column label="学号" prop="studentNo" width="120" align="center" />
          <el-table-column label="当前状态" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.classId === currentClassId" type="success" size="small">已在班</el-tag>
              <el-tag v-else-if="scope.row.classId" type="info" size="small">其他班</el-tag>
              <el-tag v-else type="warning" size="small">未分配</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope">
              <el-button
                type="primary"
                link
                size="small"
                :disabled="scope.row.classId === currentClassId"
                @click="confirmAddStudent(scope.row)"
              >
                {{ scope.row.classId === currentClassId ? '已在班' : '添加' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </el-dialog>

    <!-- 查看成绩弹窗 -->
    <el-dialog
      v-model="scoreDialogVisible"
      title="班级成绩"
      width="800px"
      append-to-body
    >
      <el-form :inline="true" style="margin-bottom: 16px">
        <el-form-item label="选择考试">
          <el-select
            v-model="selectedExamId"
            placeholder="全部考试"
            clearable
            style="width: 260px"
            @change="fetchScores"
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

      <el-table v-loading="scoreLoading" :data="scoreList" border stripe>
        <el-table-column label="学生姓名" prop="realName" width="100" align="center" />
        <el-table-column label="学号" prop="studentNo" width="120" align="center" />
        <el-table-column label="考试名称" prop="examTitle" min-width="150" />
        <el-table-column label="得分" prop="score" width="80" align="center" />
        <el-table-column label="总分" prop="totalScore" width="80" align="center" />
        <el-table-column label="状态" prop="statusName" width="100" align="center">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.status === 2
                  ? 'success'
                  : scope.row.status === 3
                    ? 'warning'
                    : 'info'
              "
            >
              {{ scope.row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" prop="submitTime" width="170" align="center">
          <template #default="scope">
            {{ scope.row.submitTime || "—" }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!scoreLoading && scoreList.length === 0" description="暂无成绩数据" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from "vue";
import { useDebounceFn } from "@vueuse/core";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import ClassAPI, { type ClassRoomVO, type ClassCreateForm, type ClassUpdateForm, type ClassScoreVO } from "@/api/business/class";
import ExamAPI, { type ExamVO } from "@/api/business/exam";
import UserAPI from "@/api/system/user";
import type { UserItem } from "@/api/system/user/types";

defineOptions({
  name: "ClassManage",
  inheritAttrs: false,
});

const queryFormRef = ref<FormInstance>();
const formRef = ref<FormInstance>();

// 教师列表（供班主任下拉选择）
const teacherOptions = ref<UserItem[]>([]);

// 年级选项（按入学年份生成，如 "2026级"）
const currentYear = new Date().getFullYear();
const gradeOptions = Array.from({ length: 7 }, (_, i) => {
  const year = currentYear - i;
  return `${year}级`;
});

// 查询参数
const queryParams = reactive({
  keyword: "",
  grade: "",
});

// 列表数据
const allList = ref<ClassRoomVO[]>([]);
const loading = ref(false);

// 前端分页
const currentPage = ref(1);
const pageSize = ref(10);

// 过滤后的列表
const filteredList = computed(() => {
  let list = allList.value;
  if (queryParams.keyword) {
    const kw = queryParams.keyword.toLowerCase();
    list = list.filter((item) => item.className?.toLowerCase().includes(kw));
  }
  if (queryParams.grade) {
    list = list.filter((item) => item.grade === queryParams.grade);
  }
  return list;
});

// 当前页数据
const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredList.value.slice(start, end);
});

function handlePagination() {
  // 分页组件切换时自动通过 computed 更新
}

// 弹窗状态
const dialogVisible = ref(false);
const dialogTitle = ref("新增班级");

// 表单初始数据
const initialFormData: ClassCreateForm & { id?: number } = {
  className: "",
  grade: "",
  teacherId: undefined,
  remark: "",
};

const formData = reactive<ClassCreateForm & { id?: number }>({ ...initialFormData });

const rules: FormRules = {
  className: [{ required: true, message: "请输入班级名称", trigger: "blur" }],
  grade: [{ required: true, message: "请选择年级", trigger: "change" }],
};

// 查看学生弹窗
const studentDialogVisible = ref(false);
const studentLoading = ref(false);
const studentList = ref<any[]>([]);
const currentClassId = ref<number>(0);
const currentClassName = ref("");

// 添加学生子弹窗
const addStudentDialogVisible = ref(false);
const studentSearchKeyword = ref("");
const studentSearchLoading = ref(false);
const candidateStudents = ref<any[]>([]);

// 查看成绩弹窗
const scoreDialogVisible = ref(false);
const scoreLoading = ref(false);
const scoreList = ref<ClassScoreVO[]>([]);
const selectedExamId = ref<number | undefined>(undefined);
const examOptions = ref<ExamVO[]>([]);

/** 加载教师列表（供班主任下拉） */
async function fetchTeachers(): Promise<void> {
  try {
    const res = await UserAPI.getPage({ page: 1, size: 200, role: 1 });
    teacherOptions.value = res.records ?? [];
  } catch {
    teacherOptions.value = [];
  }
}

/** 加载班级列表 */
async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const res = await ClassAPI.list();
    allList.value = res ?? [];
  } finally {
    loading.value = false;
  }
}

function handleQuery(): void {
  currentPage.value = 1;
  // 前端过滤，通过 computed 自动更新
}

function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  currentPage.value = 1;
}

/** 删除班级 */
async function deleteClass(id: number): Promise<void> {
  await ClassAPI.delete(id);
  ElMessage.success("删除成功");
  fetchList();
}

function handleDelete(row: ClassRoomVO): void {
  ElMessageBox.confirm(`确认删除班级【${row.className}】吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => deleteClass(row.id),
    () => {}
  );
}

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
  dialogTitle.value = "新增班级";
  Object.assign(formData, { ...initialFormData });
  openDialog();
}

function handleEditClick(row: ClassRoomVO): void {
  dialogTitle.value = "修改班级";
  Object.assign(formData, {
    id: row.id,
    className: row.className,
    grade: row.grade,
    teacherId: row.teacherId,
    remark: row.remark,
  });
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
    if (formData.id) {
      const updateData: ClassUpdateForm = {
        className: formData.className,
        grade: formData.grade,
        teacherId: formData.teacherId,
        remark: formData.remark,
      };
      await ClassAPI.update(formData.id, updateData);
      ElMessage.success("修改班级成功");
    } else {
      await ClassAPI.create(formData);
      ElMessage.success("新增班级成功");
    }
    closeDialog();
    fetchList();
  } finally {
    loading.value = false;
  }
}, 300);

/** 查看学生 */
async function handleViewStudents(row: ClassRoomVO): Promise<void> {
  currentClassId.value = row.id;
  currentClassName.value = row.className || "";
  studentDialogVisible.value = true;
  studentLoading.value = true;
  studentList.value = [];
  try {
    const res = await ClassAPI.getStudents(row.id);
    studentList.value = res ?? [];
  } finally {
    studentLoading.value = false;
  }
}

/** 打开添加学生弹窗 */
function handleAddStudentClick(): void {
  addStudentDialogVisible.value = true;
  studentSearchKeyword.value = "";
  candidateStudents.value = [];
}

/** 搜索待添加的学生 */
async function searchStudents(): Promise<void> {
  studentSearchLoading.value = true;
  try {
    const res = await UserAPI.getPage({
      page: 1,
      size: 50,
      role: 0,
      keyword: studentSearchKeyword.value || undefined,
    });
    candidateStudents.value = res.records ?? [];
  } catch {
    candidateStudents.value = [];
  } finally {
    studentSearchLoading.value = false;
  }
}

/** 确认添加学生 */
async function confirmAddStudent(row: any): Promise<void> {
  if (row.classId === currentClassId.value) return;
  try {
    await ClassAPI.addStudent(currentClassId.value, row.id);
    ElMessage.success(`已将 ${row.realName || row.username} 添加到班级`);
    // 刷新学生列表
    const res = await ClassAPI.getStudents(currentClassId.value);
    studentList.value = res ?? [];
    // 关闭子弹窗
    addStudentDialogVisible.value = false;
  } catch (e: any) {
    ElMessage.error(e?.message || "添加失败");
  }
}

/** 移除学生 */
async function handleRemoveStudent(row: any): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确认将 ${row.realName || row.username} 移出班级吗？`,
      "确认",
      { type: "warning" }
    );
  } catch {
    return;
  }
  try {
    await ClassAPI.removeStudent(row.id);
    ElMessage.success("已移出班级");
    studentList.value = studentList.value.filter((s: any) => s.id !== row.id);
  } catch (e: any) {
    ElMessage.error(e?.message || "移除失败");
  }
}

/** 查看成绩 */
async function handleViewScores(row: ClassRoomVO): Promise<void> {
  currentClassId.value = row.id;
  selectedExamId.value = undefined;
  scoreList.value = [];
  scoreDialogVisible.value = true;

  // 加载考试列表供下拉选择
  try {
    const res = await ExamAPI.getPage({ page: 1, size: 200 });
    examOptions.value = res.records ?? [];
  } catch {
    examOptions.value = [];
  }

  // 加载全部成绩
  fetchScores();
}

async function fetchScores(): Promise<void> {
  scoreLoading.value = true;
  try {
    const res = await ClassAPI.getScores(currentClassId.value, selectedExamId.value);
    scoreList.value = res ?? [];
  } finally {
    scoreLoading.value = false;
  }
}

onMounted(() => {
  fetchList();
  fetchTeachers();
});
</script>

