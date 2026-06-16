<template>
  <div class="p-4">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="6">
        <div class="left-column">
          <!-- 用户信息卡片 -->
          <el-card class="user-card">
            <div class="user-info">
              <div class="avatar-wrapper">
                <el-avatar :src="userStore.userInfo.avatar" :size="100" />
              </div>
              <div class="user-name">
                <span class="nickname">{{ userProfile.realName || userProfile.username }}</span>
                <el-icon class="edit-icon" @click="handleOpenDialog(DialogType.ACCOUNT)">
                  <Edit />
                </el-icon>
              </div>
              <div class="user-role">{{ userProfile.roleName }}</div>

              <!-- 安全评分 -->
              <div class="security-score">
                <el-progress
                  type="dashboard"
                  :percentage="securityScore"
                  :color="scoreColor"
                  :stroke-width="8"
                  :width="80"
                />
                <span class="score-text">账号安全</span>
              </div>
            </div>
          </el-card>

          <!-- 快捷入口 -->
          <el-card class="quick-actions">
            <template #header>
              <span class="card-header-title">快捷操作</span>
            </template>
            <div class="action-list">
              <div class="action-item" @click="handleOpenDialog(DialogType.PASSWORD)">
                <div class="action-icon password">
                  <el-icon><Lock /></el-icon>
                </div>
                <span class="action-name">修改密码</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>

      <!-- 右侧信息卡片 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="18">
        <div class="right-column">
          <!-- 账号信息卡片 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <span class="card-header-title">
                  <el-icon><User /></el-icon>
                  账号信息
                </span>
                <el-button type="primary" link @click="handleOpenDialog(DialogType.ACCOUNT)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
              </div>
            </template>
            <div class="info-grid">
              <div class="info-item">
                <div class="info-label">
                  <el-icon><User /></el-icon>
                  用户名
                </div>
                <div class="info-value">{{ userProfile.username || "-" }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><User /></el-icon>
                  姓名
                </div>
                <div class="info-value">{{ userProfile.realName || "-" }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Key /></el-icon>
                  角色
                </div>
                <div class="info-value">{{ userProfile.roleName || "-" }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Iphone /></el-icon>
                  手机号码
                </div>
                <div class="info-value" :class="{ 'text-muted': !userProfile.phone }">
                  {{ userProfile.phone || "未填写" }}
                </div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Message /></el-icon>
                  邮箱
                </div>
                <div class="info-value" :class="{ 'text-muted': !userProfile.email }">
                  {{ userProfile.email || "未填写" }}
                </div>
              </div>
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Timer /></el-icon>
                  注册时间
                </div>
                <div class="info-value">{{ userProfile.createTime || "-" }}</div>
              </div>
            </div>
          </el-card>

          <!-- 安全设置卡片 -->
          <el-card class="security-card">
            <template #header>
              <div class="card-header">
                <span class="card-header-title">
                  <el-icon><Key /></el-icon>
                  安全设置
                </span>
              </div>
            </template>
            <div class="security-list">
              <div class="security-item">
                <div class="security-left">
                  <div class="security-icon password">
                    <el-icon><Lock /></el-icon>
                  </div>
                  <div class="security-content">
                    <div class="security-title">账户密码</div>
                    <div class="security-desc">定期修改密码有助于保护账户安全</div>
                  </div>
                </div>
                <el-button type="primary" link @click="handleOpenDialog(DialogType.PASSWORD)">
                  修改
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogState.visible" :title="dialogState.title" :width="500">
      <!-- 编辑资料 -->
      <el-form
        v-if="dialogState.type === DialogType.ACCOUNT"
        ref="profileFormRef"
        :model="profileForm"
        :label-width="100"
      >
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.realName" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" />
        </el-form-item>
      </el-form>

      <!-- 修改密码 -->
      <el-form
        v-if="dialogState.type === DialogType.PASSWORD"
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        :label-width="100"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import UserAPI from "@/api/system/user";
import type { UserItem } from "@/api/system/user";

import { computed, onMounted, reactive, ref } from "vue";
import type { FormInstance } from "element-plus";
import { useUserStoreHook } from "@/stores";
import { redirectToLogin } from "@/utils/auth";

import {
  Edit,
  Lock,
  Iphone,
  Message,
  User,
  Timer,
  Key,
} from "@element-plus/icons-vue";

const userStore = useUserStoreHook();

const userProfile = ref<Partial<UserItem>>({});

const enum DialogType {
  ACCOUNT = "account",
  PASSWORD = "password",
}

const dialogState = reactive({
  visible: false,
  title: "",
  type: "" as DialogType,
});

const profileFormRef = ref<FormInstance>();
const passwordFormRef = ref<FormInstance>();

const profileForm = reactive<{ realName: string; phone: string; email: string }>({
  realName: "",
  phone: "",
  email: "",
});

const passwordForm = reactive<{
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}>({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const securityScore = computed(() => {
  let score = 60;
  if (userProfile.value.phone) score += 20;
  if (userProfile.value.email) score += 20;
  return score;
});

const scoreColor = computed(() => {
  if (securityScore.value >= 80) return "#67c23a";
  if (securityScore.value >= 60) return "#e6a23c";
  return "#f56c6c";
});

const passwordRules = {
  oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPassword: [{ required: true, message: "请输入新密码", trigger: "blur" }],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (_rule: any, value: string, callback: (_error?: Error) => void) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
          return;
        }
        callback();
      },
      trigger: "blur",
    },
  ],
};

const handleOpenDialog = (type: DialogType) => {
  dialogState.type = type;
  dialogState.visible = true;
  switch (type) {
    case DialogType.ACCOUNT:
      dialogState.title = "编辑资料";
      profileForm.realName = userProfile.value.realName || "";
      profileForm.phone = userProfile.value.phone || "";
      profileForm.email = userProfile.value.email || "";
      break;
    case DialogType.PASSWORD:
      dialogState.title = "修改密码";
      passwordForm.oldPassword = "";
      passwordForm.newPassword = "";
      passwordForm.confirmPassword = "";
      break;
  }
};

const handleSubmit = async () => {
  try {
    if (dialogState.type === DialogType.ACCOUNT) {
      await UserAPI.updateProfile({
        realName: profileForm.realName || undefined,
        phone: profileForm.phone || undefined,
        email: profileForm.email || undefined,
      });
      ElMessage.success("资料修改成功");
      dialogState.visible = false;
      await loadUserProfile();
    } else if (dialogState.type === DialogType.PASSWORD) {
      const valid = await passwordFormRef.value?.validate();
      if (!valid) return;

      await UserAPI.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
      });
      dialogState.visible = false;
      await redirectToLogin("密码已修改，请重新登录");
    }
  } catch {
    // ignore
  }
};

const handleCancel = () => {
  dialogState.visible = false;
  if (dialogState.type === DialogType.ACCOUNT) {
    profileFormRef.value?.resetFields();
  } else if (dialogState.type === DialogType.PASSWORD) {
    passwordFormRef.value?.resetFields();
  }
};

const loadUserProfile = async () => {
  try {
    const data = await UserAPI.getInfo();
    userProfile.value = data;
  } catch {
    ElMessage.error("获取用户信息失败");
  }
};

onMounted(async () => {
  await loadUserProfile();
});
</script>

<style lang="scss" scoped>
.left-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-card {
  .user-info {
    padding: 10px 0;
    text-align: center;

    .avatar-wrapper {
      position: relative;
      display: inline-block;
      margin-bottom: 12px;
    }

    .user-name {
      margin-bottom: 6px;

      .nickname {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .edit-icon {
        margin-left: 8px;
        color: var(--el-text-color-secondary);
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .user-role {
      margin-bottom: 16px;
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }

    .security-score {
      display: flex;
      flex-direction: column;
      gap: 8px;
      align-items: center;
      padding-top: 10px;

      .score-text {
        font-size: 13px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

.quick-actions {
  .card-header-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .action-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .action-item {
    display: flex;
    gap: 12px;
    align-items: center;
    padding: 12px;
    cursor: pointer;
    border-radius: var(--brand-radius-sm);
    transition: all 0.2s ease;

    &:hover {
      background: var(--el-fill-color-light);
    }

    .action-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      font-size: 18px;
      border-radius: var(--brand-radius-sm);

      &.password {
        color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }
    }

    .action-name {
      font-size: 14px;
      color: var(--el-text-color-primary);
    }
  }
}

.info-card,
.security-card {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .card-header-title {
      display: flex;
      gap: 8px;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px;
  background: var(--el-fill-color-light);
  border-radius: var(--brand-radius-sm);

  .info-label {
    display: flex;
    gap: 6px;
    align-items: center;
    font-size: 13px;
    color: var(--el-text-color-secondary);

    .el-icon {
      font-size: 14px;
    }
  }

  .info-value {
    display: flex;
    gap: 8px;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
    color: var(--el-text-color-primary);

    &.text-muted {
      color: var(--el-text-color-placeholder);
    }
  }
}

.security-list {
  display: flex;
  flex-direction: column;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  transition: background 0.2s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--el-fill-color-light);
    border-radius: var(--brand-radius-sm);
  }

  .security-left {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .security-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    font-size: 20px;
    border-radius: var(--brand-radius-sm);

    &.password {
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
    }
  }

  .security-content {
    .security-title {
      margin-bottom: 4px;
      font-size: 15px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }

    .security-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
