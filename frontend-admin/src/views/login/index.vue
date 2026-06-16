<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左面板：品牌 + 角色动画 -->
      <div class="left-panel">
        <div class="brand-header">
          <div class="app-logo">
            <svg width="44" height="44" viewBox="0 0 44 44" fill="none">
              <rect width="44" height="44" rx="10" fill="var(--brand-accent)"/>
              <path d="M13 30V20L22 13L31 20V30L22 36L13 30Z" fill="white" fill-opacity="0.95"/>
              <path d="M18 26V21L22 18L26 21V26L22 29L18 26Z" fill="white"/>
            </svg>
          </div>
          <div class="brand-info">
            <h1 class="app-name">智考云</h1>
            <p class="app-subtitle">在线考试平台管理端</p>
          </div>
        </div>

        <div class="characters-section">
          <div class="characters-wrapper">
            <AnimatedCharacters
              :username-focus="usernameFocus"
              :password-focus="passwordFocus"
              :password-visible="showPassword"
              :login-error="loginError"
              @password-visible="onPasswordVisible"
              @trigger-error="onTriggerError"
            />
          </div>
        </div>
      </div>

      <!-- 右面板：登录表单 -->
      <div class="right-panel">
        <div class="form-container">
          <div class="form-header">
            <h2 class="form-title">欢迎回来</h2>
            <p class="form-subtitle">请登录您的管理账号</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="form"
            :rules="rules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="UserIcon"
                @focus="usernameFocus = true"
                @blur="usernameFocus = false"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                :type="pwdFieldType"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="LockIcon"
                @focus="passwordFocus = true"
                @blur="passwordFocus = false"
                @keyup.enter="handleLogin"
              >
                <template #suffix>
                  <span class="pwd-toggle" @click="togglePassword">
                    <svg
                      v-if="showPassword"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    >
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                    <svg
                      v-else
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    >
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  </span>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                :loading="loading"
                type="primary"
                size="large"
                class="login-btn"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="card-footer">
            <span class="copyright">智考云在线考试平台 &copy; 2026</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import type { FormInstance } from "element-plus"
import { ElMessage } from "element-plus"
import { User as UserIcon, Lock as LockIcon } from "@element-plus/icons-vue"
import { useUserStore } from "@/stores/user"
import AnimatedCharacters from "./AnimatedCharacters.vue"

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ---- 表单 ----
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: "",
  password: "",
})

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码不能少于6位", trigger: "blur" },
  ],
}

// ---- 密码可见切换 ----
const pwdFieldType = ref<"password" | "text">("password")
const showPassword = computed(() => pwdFieldType.value === "text")

function togglePassword() {
  pwdFieldType.value = pwdFieldType.value === "password" ? "text" : "password"
}

// ---- 角色动画状态 ----
const usernameFocus = ref(false)
const passwordFocus = ref(false)
const loginError = ref(false)

function onPasswordVisible(value: boolean) {
  // 角色偷看密码时的回调
}

function onTriggerError() {
  // 角色错误动画开始时的回调
}

// ---- 登录 ----
async function handleLogin() {
  // 重置错误动画状态（让组件可以重新触发）
  loginError.value = false

  try {
    const valid = await loginFormRef.value?.validate().catch(() => false)
    if (!valid) {
      // 校验失败 → 触发角色错误动画
      loginError.value = true
      return
    }

    loading.value = true
    await userStore.login(form)
    const redirect = (route.query.redirect as string) || "/"
    router.push(decodeURIComponent(redirect))
  } catch (e: any) {
    // 登录请求失败 → 触发角色错误动画
    loginError.value = true
    ElMessage.error(e?.message || "登录失败")
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
/* ===== 页面背景 ===== */
.login-page {
  min-height: 100vh;
  background: #edf0f5;
  background-image: linear-gradient(135deg, #e8edf5 0%, #edf0f7 25%, #f0eef5 50%, #edf2f5 75%, #eef0f5 100%);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

html.dark .login-page {
  background: linear-gradient(135deg, #1c1c1e 0%, #2c2c2e 50%, #1c1c1e 100%);
  background-image: none;
}

/* ===== 左右两栏容器 ===== */
.login-container {
  display: flex;
  min-height: 100vh;
}

/* ===== 左面板 (品牌 + 角色) ===== */
.left-panel {
  flex: 1.3;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  padding: 48px 48px 0;
  overflow: hidden;
  background: var(--brand-bg, linear-gradient(135deg, #e8edf5 0%, #edf0f7 25%, #f0eef5 50%, #edf2f5 75%, #eef0f5 100%));

  /* 装饰光晕 */
  &::before {
    content: "";
    position: absolute;
    top: 15%;
    right: 10%;
    width: 300px;
    height: 300px;
    background: rgba(120, 100, 200, 0.12);
    border-radius: 50%;
    filter: blur(100px);
    pointer-events: none;
  }

  &::after {
    content: "";
    position: absolute;
    bottom: 10%;
    left: 5%;
    width: 200px;
    height: 200px;
    background: rgba(0, 122, 255, 0.08);
    border-radius: 50%;
    filter: blur(80px);
    pointer-events: none;
  }
}

.brand-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
  align-self: flex-start;
}

.app-logo {
  display: flex;
  flex-shrink: 0;
}

.app-logo svg {
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(0, 122, 255, 0.2);
}

.brand-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.app-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--brand-dark, #1d1d1f);
  margin: 0;
  line-height: 1.3;
  letter-spacing: -0.3px;
}

.app-subtitle {
  font-size: 13px;
  color: var(--brand-muted, #6e6e73);
  margin: 0;
  font-weight: 400;
}

.characters-section {
  position: relative;
  z-index: 1;
  flex: 1;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.characters-wrapper {
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

/* ===== 右面板 (登录表单) ===== */
.right-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

html.dark .right-panel {
  border-left: 1px solid rgba(255, 255, 255, 0.06);
}

.form-container {
  width: 100%;
  max-width: 400px;
  animation: formFadeIn 0.6s ease both 0.15s;
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: var(--glass-bg, rgba(255, 255, 255, 0.35));
  border-radius: var(--brand-radius-lg, 24px);
  box-shadow: var(--brand-shadow, 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06));
  border: var(--glass-border, 1px solid rgba(255, 255, 255, 0.6));
  padding: 40px;
}

@keyframes formFadeIn {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 表单头部 ===== */
.form-header {
  margin-bottom: 36px;
  text-align: center;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--brand-dark, #1d1d1f);
  letter-spacing: -0.5px;
  margin: 0 0 8px;
}

.form-subtitle {
  font-size: 15px;
  color: var(--brand-muted, #6e6e73);
  margin: 0;
  font-weight: 400;
}

/* ===== 登录表单 ===== */
.login-form {
  text-align: left;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__error) {
  font-size: 12px;
  padding-top: 4px;
}

/* 输入框 wrapper — glass inset */
.login-form :deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px) saturate(150%);
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  box-shadow: none !important;
  padding: 0 16px;
  transition: all 0.25s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.25);
  box-shadow: none !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.15) !important;
}

/* 输入框 inner */
.login-form :deep(.el-input__inner) {
  background: transparent;
  border: none;
  height: 44px;
  font-size: 15px;
  color: var(--brand-dark, #1d1d1f);
}

.login-form :deep(.el-input__inner::placeholder) {
  color: var(--brand-muted, #6e6e73);
  font-weight: 400;
  opacity: 0.6;
}

/* 前缀图标 */
.login-form :deep(.el-input__prefix .el-icon) {
  color: var(--brand-muted, #6e6e73);
  font-size: 17px;
}

/* 自定义密码切换按钮 */
.pwd-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--brand-muted, #6e6e73);
  padding: 4px;
  border-radius: 6px;
  transition: color 0.2s, background 0.2s;

  &:hover {
    color: var(--brand-accent, #007AFF);
    background: rgba(0, 122, 255, 0.08);
  }

  &:active {
    transform: scale(0.92);
  }
}

/* ===== 登录按钮 ===== */
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  background: #007AFF;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  letter-spacing: 2px;
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.35);
  transition: all 0.25s ease;
}

.login-btn:hover,
.login-btn:focus {
  background: #0066d6;
  transform: scale(1.02);
  box-shadow: 0 8px 28px rgba(0, 122, 255, 0.45);
}

.login-btn:active {
  transform: scale(0.98);
  box-shadow: 0 3px 10px rgba(0, 122, 255, 0.25);
}

/* ===== 底部版权 ===== */
.card-footer {
  text-align: center;
  margin-top: 32px;
}

.copyright {
  font-size: 12px;
  color: var(--brand-muted, #6e6e73);
}

/* ===== 响应式: ≤768px 隐藏左面板 ===== */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .left-panel {
    display: none;
  }

  .right-panel {
    padding: 36px 24px;
  }

  .form-title {
    font-size: 24px;
  }
}
</style>
