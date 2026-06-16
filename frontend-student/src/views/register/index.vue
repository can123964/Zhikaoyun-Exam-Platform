<template>
  <div class="register-page">
    <!-- 左侧品牌展示区 -->
    <div class="brand-panel">
      <div class="brand-header">
        <svg width="32" height="32" viewBox="0 0 44 44" fill="none">
          <rect width="44" height="44" rx="10" fill="rgba(0,0,0,0.06)"/>
          <path d="M13 30V20L22 13L31 20V30L22 36L13 30Z" fill="#1d1d1f" fill-opacity="0.8"/>
          <path d="M18 26V21L22 18L26 21V26L22 29L18 26Z" fill="#1d1d1f"/>
        </svg>
        <span class="brand-name">智考云</span>
      </div>

      <div class="brand-content">
        <h1 class="brand-title">在线考试平台</h1>
        <p class="brand-subtitle">随时随地，高效学习</p>

        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M4 10L8 14L16 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <div class="feature-text">在线考试</div>
              <div class="feature-desc">支持多种题型，智能计时计分</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M4 10L8 14L16 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <div class="feature-text">成绩查看</div>
              <div class="feature-desc">考试结束即时出分，历史记录可查</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M4 10L8 14L16 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div>
              <div class="feature-text">错题回顾</div>
              <div class="feature-desc">自动收录错题，针对性复习提升</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="form-panel">
      <div class="form-container">
        <div class="form-header">
          <h2 class="form-title">学生注册</h2>
          <p class="form-desc">创建您的考试系统账号</p>
        </div>

        <el-form ref="registerForm" :model="registerForm" class="register-form">
          <div class="field">
            <label>用户名</label>
            <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user" />
          </div>
          <div class="field">
            <label>密码</label>
            <el-input
              v-model="registerForm.password"
              :type="pwdFieldType"
              placeholder="请输入密码（6-20位）"
              prefix-icon="el-icon-lock"
              class="pwd-input"
            >
              <i slot="suffix" class="toggle-pwd-btn" @click="togglePwdVisible">
                <svg v-if="pwdFieldType === 'password'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </i>
            </el-input>
          </div>
          <div class="field-row">
            <div class="field half">
              <label>真实姓名</label>
              <el-input v-model="registerForm.realName" placeholder="姓名" prefix-icon="el-icon-edit" />
            </div>
            <div class="field half">
              <label>学号</label>
              <el-input v-model="registerForm.studentNo" placeholder="学号" prefix-icon="el-icon-document" />
            </div>
          </div>
          <div class="field-row">
            <div class="field half">
              <label>邮箱</label>
              <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="el-icon-message" />
            </div>
            <div class="field half">
              <label>手机号</label>
              <el-input v-model="registerForm.phone" placeholder="手机号（可选）" prefix-icon="el-icon-phone" />
            </div>
          </div>
          <div class="field">
            <label>班级</label>
            <el-select v-model="registerForm.classId" placeholder="选择班级（可选）" clearable style="width: 100%">
              <el-option
                v-for="c in classOptions"
                :key="c.id"
                :label="c.grade + ' - ' + c.className"
                :value="c.id"
              />
            </el-select>
          </div>
          <el-button type="primary" size="large" class="register-btn" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
        </el-form>

        <div class="form-footer">
          已有账号？
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>

        <div class="form-bottom">
          <span class="copyright">智考云在线考试平台 &copy; 2026</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get } from '@/utils/request'

export default {
  name: 'Register',
  data () {
    return {
      registerForm: {
        username: '',
        password: '',
        realName: '',
        studentNo: '',
        email: '',
        phone: '',
        classId: null
      },
      classOptions: [],
      loading: false,
      pwdFieldType: 'password'
    }
  },
  methods: {
    togglePwdVisible () {
      this.pwdFieldType = this.pwdFieldType === 'password' ? 'text' : 'password'
    },
    handleRegister () {
      if (!this.registerForm.username) {
        this.$message.error('用户名不能为空')
        return
      }
      if (this.registerForm.username.length < 2 || this.registerForm.username.length > 50) {
        this.$message.error('用户名长度为2-50个字符')
        return
      }
      if (!this.registerForm.password) {
        this.$message.error('密码不能为空')
        return
      }
      if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
        this.$message.error('密码长度为6-20个字符')
        return
      }
      if (!this.registerForm.realName) {
        this.$message.error('真实姓名不能为空')
        return
      }
      if (!this.registerForm.studentNo) {
        this.$message.error('学号不能为空')
        return
      }
      if (!this.registerForm.email) {
        this.$message.error('邮箱不能为空')
        return
      }
      const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/
      if (!emailPattern.test(this.registerForm.email)) {
        this.$message.error('请输入正确的邮箱格式')
        return
      }

      this.loading = true
      const registerApi = import('@/api/register').then(m => m.default || m)
      registerApi.then(api => {
        api.register(this.registerForm).then(result => {
          if (result && result.code === 200) {
            this.$message.success('注册成功')
            this.$router.push({ path: '/login' })
          } else {
            this.$message.error(result.msg)
          }
          this.loading = false
        }).catch(() => {
          this.loading = false
          this.$message.error('注册失败，请检查网络连接')
        })
      })
    },
    async fetchClassOptions () {
      try {
        const res = await get('/api/class/list')
        this.classOptions = res.data || []
      } catch {
        this.classOptions = []
      }
    }
  },
  mounted () {
    this.fetchClassOptions()
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  font-family: 'Noto Sans SC', 'Inter', -apple-system, sans-serif;
  background: var(--brand-bg);
}

/* ===== 左侧品牌面板 ===== */
.brand-panel {
  flex: 1.3;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  background: var(--brand-bg);
  padding: 40px 48px;
}

/* Decorative ambient blurs */
.brand-panel::before {
  content: '';
  position: absolute;
  top: 15%;
  right: 10%;
  width: 320px;
  height: 320px;
  background: rgba(108, 63, 245, 0.08);
  border-radius: 50%;
  filter: blur(100px);
  pointer-events: none;
}

.brand-panel::after {
  content: '';
  position: absolute;
  bottom: 10%;
  left: 5%;
  width: 400px;
  height: 400px;
  background: rgba(0, 122, 255, 0.06);
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
}

.brand-header {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  align-self: flex-start;
  width: 100%;
  margin-bottom: 40px;
}

.brand-header .brand-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand-dark);
  letter-spacing: 2px;
}

.brand-content {
  position: relative;
  z-index: 2;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-width: 400px;
  width: 100%;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--brand-dark);
  margin: 0 0 12px;
  line-height: 1.3;
  text-align: center;
}

.brand-subtitle {
  font-size: 16px;
  color: var(--brand-light);
  margin: 0 0 52px;
  font-weight: 400;
  text-align: center;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.feature-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--brand-muted);
}

.feature-text {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-dark);
  margin-bottom: 2px;
}

.feature-desc {
  font-size: 13px;
  color: var(--brand-light);
}

/* ===== 右侧表单面板 ===== */
.form-panel {
  flex: 1;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-left: 1px solid rgba(255, 255, 255, 0.6);
  padding: 40px;
  min-height: 100vh;
}

.form-container {
  width: 100%;
  max-width: 400px;
  animation: slideUp 0.6s ease both;
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: var(--glass-bg, rgba(255, 255, 255, 0.35));
  border-radius: var(--brand-radius-lg, 24px);
  box-shadow: var(--brand-shadow, 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06));
  border: var(--glass-border, 1px solid rgba(255, 255, 255, 0.6));
  padding: 40px;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-header {
  margin-bottom: 36px;
}

.form-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--brand-dark);
  margin: 0 0 8px;
}

.form-desc {
  font-size: 14px;
  color: var(--brand-light);
  margin: 0;
}

.register-form {
  text-align: left;
}

.field {
  margin-bottom: 22px;
}

.field label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--brand-muted);
}

/* Element UI 输入框覆盖 — VisionOS 玻璃样式 */
.field >>> .el-input__inner {
  border-radius: 14px !important;
  border: 1px solid rgba(255, 255, 255, 0.35) !important;
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(150%);
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  height: 46px !important;
  line-height: 46px !important;
  font-size: 14px;
  color: var(--brand-dark);
  transition: all 0.2s;
  box-shadow: none !important;
  padding-right: 40px;
}

.field >>> .el-input__inner:focus {
  border-color: #007AFF !important;
  background: rgba(255, 255, 255, 0.25) !important;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1) !important;
}

.field >>> .el-input__prefix {
  left: 12px;
  color: var(--brand-light);
  display: flex;
  align-items: center;
}

.field >>> .el-input--prefix .el-input__inner {
  padding-left: 38px;
}

.field >>> .el-input__icon {
  line-height: 46px;
  font-size: 16px;
}

/* 自定义密码切换按钮 */
.toggle-pwd-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 10px;
  cursor: pointer;
  color: var(--brand-light);
  transition: color 0.2s;
}

.toggle-pwd-btn:hover {
  color: var(--brand-muted);
}

/* 隐藏浏览器默认密码显示/清除按钮 */
.field >>> input[type="password"]::-ms-reveal,
.field >>> input[type="password"]::-ms-clear {
  display: none;
}

.field-row {
  display: flex;
  gap: 12px;
}

.field.half {
  flex: 1;
}

/* 选择框玻璃样式 */
.field >>> .el-select .el-input__inner {
  border-radius: 14px !important;
  border: 1px solid rgba(255, 255, 255, 0.35) !important;
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(20px) saturate(150%);
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  height: 46px !important;
  line-height: 46px !important;
  font-size: 14px;
  color: var(--brand-dark);
  transition: all 0.2s;
  box-shadow: none !important;
}

.field >>> .el-select .el-input__inner:focus {
  border-color: #007AFF !important;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1) !important;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border: none !important;
  border-radius: 14px !important;
  letter-spacing: 3px;
  margin-top: 12px;
  background: var(--brand-accent) !important;
  transition: all 0.25s;
}

.register-btn:hover,
.register-btn:focus {
  background: var(--brand-accent-hover) !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.3);
}

.register-btn:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 28px;
  font-size: 13px;
  color: var(--brand-light);
}

.login-link {
  color: var(--brand-accent);
  font-weight: 600;
  text-decoration: none;
}

.login-link:hover {
  color: var(--brand-accent-hover);
}

.form-bottom {
  text-align: center;
  margin-top: 48px;
}

.copyright {
  font-size: 12px;
  color: var(--brand-lighter);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .register-page {
    flex-direction: column;
  }

  .brand-panel {
    display: none;
  }

  .form-panel {
    max-width: 100%;
    padding: 40px 24px;
    flex: 1;
    min-height: 100vh;
    border-left: none;
  }
}

@media (max-width: 480px) {
  .field-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
