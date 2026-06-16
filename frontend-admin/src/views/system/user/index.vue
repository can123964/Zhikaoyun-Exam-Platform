<template>
  <div class="app-container">
    <div class="filter-section">
      <el-tabs v-model="activeRoleTab" type="card" @tab-change="handleRoleTabChange">
        <el-tab-pane v-if="isAdmin" label="全部" :name="-1" />
        <el-tab-pane label="学生" :name="0" />
        <el-tab-pane v-if="isAdmin" label="教师" :name="1" />
        <el-tab-pane v-if="isAdmin" label="管理员" :name="2" />
      </el-tabs>

      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto" style="margin-top: 12px">
        <el-form-item label="关键字" prop="keyword">
          <el-input
            v-model="queryParams.keyword"
            placeholder="用户名/真实姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">搜索</el-button>
          <el-button icon="refresh" @click="handleResetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-section">
      <div class="table-section__toolbar">
        <div class="table-section__toolbar--actions">
          <el-button
            v-hasPerm="2"
            type="success"
            icon="plus"
            @click="handleCreateClick"
          >
            新增
          </el-button>
          <el-button
            v-hasPerm="2"
            type="danger"
            icon="delete"
            :disabled="!selectedUserId"
            @click="handleDelete(selectedUserId || undefined)"
          >
            删除
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        highlight-current-row
        class="table-section__content"
        row-key="id"
        @current-change="handleCurrentChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="用户名" prop="username" min-width="120" />
        <el-table-column label="真实姓名" width="120" align="center" prop="realName" />
        <el-table-column label="角色" width="100" align="center" prop="roleName" />
        <el-table-column label="学号" width="120" align="center" prop="studentNo" />
        <el-table-column label="班级" width="150" align="center">
          <template #default="scope">
            {{ classNameMap[scope.row.classId] || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" align="center" prop="phone" width="130" />
        <el-table-column label="邮箱" align="center" prop="email" width="160" />
        <el-table-column label="状态" align="center" prop="status" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="scope">
            <el-button
              v-hasPerm="2"
              type="primary"
              icon="RefreshLeft"
              link
              size="small"
              @click="handleResetPassword(scope.row)"
            >
              重置密码
            </el-button>
            <el-button
              v-hasPerm="2"
              type="primary"
              icon="edit"
              link
              size="small"
              @click="handleEditClick(scope.row.id)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPerm="2"
              type="danger"
              icon="delete"
              link
              size="small"
              @click="handleDelete(scope.row.id)"
            >
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
        @pagination="fetchList"
      />
    </el-card>

    <!-- 新增/编辑抽屉 -->
    <el-drawer
      v-model="dialogVisible"
      :title="dialogTitle"
      append-to-body
      size="500px"
      @close="closeDialog"
    >
      <el-form ref="userFormRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            :disabled="!!formData.id"
            placeholder="登录用户名"
          />
        </el-form-item>

        <el-form-item v-if="!formData.id" label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="初始密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="真实姓名" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%" :disabled="!!formData.id">
            <el-option label="学生" :value="0" />
            <el-option v-if="isAdmin" label="教师" :value="1" />
            <el-option v-if="isAdmin" label="管理员" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="formData.role === 0" label="学号" prop="studentNo">
          <el-input v-model="formData.studentNo" placeholder="学号" />
        </el-form-item>
        <el-form-item v-if="formData.role === 0" label="班级" prop="classId">
          <el-select v-model="formData.classId" placeholder="请选择班级" clearable style="width: 100%">
            <el-option
              v-for="c in classOptions"
              :key="c.id"
              :label="c.grade + ' - ' + c.className"
              :value="c.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="邮箱" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            inline-prompt
            active-text="正常"
            inactive-text="禁用"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="loading" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useDebounceFn } from "@vueuse/core";
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from "element-plus";
import UserAPI, { type UserForm, type UserQueryParams, type UserItem } from "@/api/system/user";
import ClassAPI from "@/api/business/class";
import { useUserStore } from "@/stores";

defineOptions({
  name: "User",
  inheritAttrs: false,
});

const userStore = useUserStore();

const queryFormRef = ref<FormInstance>();
const userFormRef = ref<FormInstance>();

// 当前用户是否为管理员（教师只能看学生）
const isAdmin = computed(() => userStore.userInfo.role === 2);
const roleLabel = computed(() => {
  const map: Record<number, string> = { 0: "学生", 1: "教师", 2: "管理员" };
  const r = queryParams.role;
  return r != null ? map[r] ?? "用户" : "用户";
});

// 角色标签页：教师默认学生，管理员默认全部
const activeRoleTab = ref<number>(isAdmin.value ? -1 : 0);

const queryParams = reactive<UserQueryParams>({
  page: 1,
  size: 10,
  role: isAdmin.value ? undefined : 0,
});

/** 角色标签切换 */
function handleRoleTabChange(tabName: string | number): void {
  const role = Number(tabName);
  queryParams.role = role === -1 ? undefined : role;
  queryParams.page = 1;
  handleQuery();
}

// 列表数据
const userList = ref<UserItem[]>([]);
const total = ref(0);
const loading = ref(false);

// 班级列表（学生选班用 + 表格班级名称映射）
const classOptions = ref<{ id: number; className: string; grade: string }[]>([]);
const classNameMap = computed(() => {
  const map: Record<number, string> = {};
  for (const c of classOptions.value) {
    map[c.id] = c.grade + ' - ' + c.className;
  }
  return map;
});

async function fetchClassOptions(): Promise<void> {
  try {
    const res = await ClassAPI.list();
    classOptions.value = res ?? [];
  } catch {
    classOptions.value = [];
  }
}

// 当前选中行ID
const selectedUserId = ref<number | null>(null);

// 弹窗状态
const dialogVisible = ref(false);
const dialogTitle = ref("新增用户");

// 表单初始数据
const initialFormData: UserForm = {
  status: 1,
};

// 表单数据
const formData = reactive<UserForm>({ ...initialFormData });

const rules: FormRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  realName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
};

/** 加载用户列表 */
async function fetchList(): Promise<void> {
  loading.value = true;
  try {
    const res = await UserAPI.getPage(queryParams);
    userList.value = res.records ?? [];
    total.value = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleCurrentChange(val: UserItem | null) {
  selectedUserId.value = val?.id ?? null;
}

function handleQuery(): void {
  queryParams.page = 1;
  fetchList();
}

function handleResetQuery(): void {
  queryFormRef.value?.resetFields();
  queryParams.page = 1;
  fetchList();
}

/** 重置用户密码 */
async function handleResetPassword(row: any): Promise<void> {
  ElMessageBox.prompt(`请输入用户【${row.username}】的新密码`, "重置密码", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /.{6,}/,
    inputErrorMessage: "密码至少需要6位字符",
  }).then(
    async ({ value }) => {
      if (row.id) {
        await UserAPI.resetPassword(row.id, value);
        ElMessage.success("密码重置成功");
      }
    },
    () => {}
  );
}

/** 删除用户 */
async function deleteUser(id: number): Promise<void> {
  // 防止删除当前登录用户
  if (id === userStore.userInfo?.id) {
    ElMessage.error("不能删除当前登录用户");
    return;
  }
  try {
    await UserAPI.deleteById(id);
    ElMessage.success("删除成功");
    handleQuery();
  } catch {
    ElMessage.error("删除失败，请重试");
  }
}

function handleDelete(id?: number): void {
  if (!id) {
    ElMessage.warning("请选择要删除的用户");
    return;
  }
  ElMessageBox.confirm("确认删除该用户吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(
    () => deleteUser(id),
    () => {}
  );
}

function openDialog(): void {
  dialogVisible.value = true;
}

function closeDialog(): void {
  dialogVisible.value = false;
  userFormRef.value?.resetFields();
  userFormRef.value?.clearValidate();
  Object.assign(formData, { ...initialFormData });
}

async function handleCreateClick(): Promise<void> {
  dialogTitle.value = `新增${roleLabel.value}`;
  Object.assign(formData, { ...initialFormData, role: queryParams.role ?? 0 });
  openDialog();
}

async function handleEditClick(id: number): Promise<void> {
  dialogTitle.value = "编辑用户";
  const row = userList.value.find((u) => u.id === id);
  if (row) {
    Object.assign(formData, {
      id: row.id,
      username: row.username,
      realName: row.realName,
      role: row.role,
      studentNo: row.studentNo,
      phone: row.phone,
      email: row.email,
      status: row.status,
    });
    openDialog();
  }
}

/** 提交表单 */
const handleSubmit = useDebounceFn(async () => {
  const valid = await userFormRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  loading.value = true;
  try {
    if (formData.id) {
      await UserAPI.update(formData.id, formData);
      ElMessage.success("修改用户成功");
    } else {
      await UserAPI.create(formData);
      ElMessage.success("新增用户成功");
    }
    closeDialog();
    handleQuery();
  } finally {
    loading.value = false;
  }
}, 300);

onMounted(() => {
  handleQuery();
  fetchClassOptions();
});
</script>

<style scoped lang="scss">
.table-section__toolbar {
  margin-bottom: 12px;
}
</style>
