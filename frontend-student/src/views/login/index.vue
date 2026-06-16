<template>
  <div class="login-page">
    <!-- Left Panel: Brand + Animated Characters -->
    <div class="left-panel">
      <div class="brand-header">
        <svg width="32" height="32" viewBox="0 0 44 44" fill="none">
          <rect width="44" height="44" rx="10" fill="rgba(0,0,0,0.06)"/>
          <path d="M13 30V20L22 13L31 20V30L22 36L13 30Z" fill="#1d1d1f" fill-opacity="0.8"/>
          <path d="M18 26V21L22 18L26 21V26L22 29L18 26Z" fill="#1d1d1f"/>
        </svg>
        <span class="brand-name">智考云</span>
      </div>
      <div class="characters-area">
        <AnimatedCharacters
          ref="characters"
          @password-visible="onPasswordVisible"
        />
      </div>
    </div>

    <!-- Right Panel: Login Form -->
    <div class="right-panel">
      <div class="form-container">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-desc">登录你的账号开始考试</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" class="login-form">
          <div class="field">
            <label>用户名</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              @focus="onUsernameFocus"
              @blur="onUsernameBlur"
              @keyup.enter.native="handleLogin"
            />
          </div>
          <div class="field">
            <label>密码</label>
            <el-input
              v-model="loginForm.password"
              :type="pwdFieldType"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              class="pwd-input"
              @focus="onPwdFocus"
              @blur="onPwdBlur"
              @input="onPwdInput"
              @keyup.enter.native="handleLogin"
            >
              <i
                slot="suffix"
                class="toggle-pwd-btn"
                @click="togglePwdVisible"
              >
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
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form>

        <div class="form-footer">
          还没有账号？<router-link to="/register" class="register-link">立即注册</router-link>
        </div>
        <div class="form-bottom">
          <span class="copyright">智考云在线考试平台 &copy; 2026</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import loginApi from '@/api/login'
import AnimatedCharacters from './AnimatedCharacters.vue'

export default {
  name: 'Login',
  components: { AnimatedCharacters },
  data () {
    return {
      loginForm: { username: '', password: '' },
      loading: false,
      pwdFieldType: 'password'
    }
  },
  methods: {
    handleLogin () {
      if (!this.loginForm.username) { this.$message.error('请输入用户名'); return }
      if (!this.loginForm.password) { this.$message.error('请输入密码'); return }
      this.loading = true
      loginApi.login(this.loginForm).then(res => {
        if (res && res.code === 200) {
          const data = res.data
          localStorage.setItem('exam_token', data.token)
          localStorage.setItem('exam_user', JSON.stringify({ role: data.role, username: data.username, realName: data.realName }))
          this.$store.commit('user/setUserInfo', data)
          this.$router.push('/index')
        } else {
          this.$message.error(res.msg || '登录失败')
          this.triggerCharacterError()
        }
        this.loading = false
      }).catch((e) => {
        this.loading = false
        this.triggerCharacterError()
      })
    },

    /* ---- Character interaction methods ---- */
    onUsernameFocus () {
      if (this.$refs.characters) this.$refs.characters.setTyping(true)
    },
    onUsernameBlur () {
      if (this.$refs.characters) this.$refs.characters.setTyping(false)
    },
    onPwdFocus () {
      if (this.$refs.characters) this.$refs.characters.setPasswordFocus(true)
    },
    onPwdBlur () {
      if (this.$refs.characters) this.$refs.characters.setPasswordFocus(false)
    },
    onPwdInput (val) {
      if (this.$refs.characters) this.$refs.characters.setHasPassword(val.length > 0)
    },
    togglePwdVisible () {
      const next = this.pwdFieldType === 'password' ? 'text' : 'password'
      this.pwdFieldType = next
      if (this.$refs.characters) {
        this.$refs.characters.setShowPassword(next === 'text')
      }
    },
    onPasswordVisible (val) {
      // Track password visibility state emitted from characters
      // Child component reports its internal state
    },
    triggerCharacterError () {
      if (this.$refs.characters) this.$refs.characters.triggerError()
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  font-family: 'Noto Sans SC', 'Inter', -apple-system, sans-serif;
  background: var(--brand-bg);
}

/* ===== Left Panel: VisionOS Glass Style ===== */
.left-panel {
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
.left-panel::before {
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

.left-panel::after {
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
  margin-bottom: 48px;
  align-self: flex-start;
}

.brand-header .brand-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand-dark);
  letter-spacing: 2px;
}

.characters-area {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  flex: 1;
}

/* ===== Right Panel: Login Form ===== */
.right-panel {
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

.form-header { margin-bottom: 36px; }
.form-title { font-size: 26px; font-weight: 700; color: var(--brand-dark); margin: 0 0 8px; }
.form-desc { font-size: 14px; color: var(--brand-light); margin: 0; }

.login-form { text-align: left; }
.field { margin-bottom: 22px; }
.field label { display: block; margin-bottom: 8px; font-size: 13px; font-weight: 600; color: var(--brand-muted); }

/* Element UI input overrides */
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

/* Custom password toggle button */
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

/* Hide browser-provided password reveal/clear buttons */
.field >>> input[type="password"]::-ms-reveal,
.field >>> input[type="password"]::-ms-clear {
  display: none;
}

/* Login button */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  border: none !important;
  border-radius: 14px !important;
  background: var(--brand-accent) !important;
  letter-spacing: 3px;
  margin-top: 12px;
  transition: all 0.25s;
}

.login-btn:hover,
.login-btn:focus {
  background: var(--brand-accent-hover) !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.3);
}

.login-btn:active { transform: translateY(0); }

.form-footer { text-align: center; margin-top: 28px; font-size: 13px; color: var(--brand-light); }
.register-link { color: var(--brand-accent); font-weight: 600; text-decoration: none; }
.register-link:hover { color: var(--brand-accent-hover); }
.form-bottom { text-align: center; margin-top: 48px; }
.copyright { font-size: 12px; color: var(--brand-lighter); }

/* ===== Responsive: ≤768px hide left panel ===== */
@media (max-width: 768px) {
  .login-page { flex-direction: column; }
  .left-panel { display: none; }
  .right-panel {
    max-width: 100%;
    padding: 40px 24px;
    flex: 1;
    min-height: 100vh;
    border-left: none;
  }
}
</style>
