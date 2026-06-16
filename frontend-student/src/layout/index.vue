<template>
  <el-container>
    <el-header height="60" class="student-header">
      <div class="header-left">
        <router-link to="/" class="header-logo">
          <svg width="24" height="24" viewBox="0 0 28 28" fill="none">
            <rect width="28" height="28" rx="6" fill="#1e293b"/>
            <path d="M9 19V12L14 8L19 12V19L14 23L9 19Z" fill="white" fill-opacity="0.9"/>
            <path d="M12 16.5V13L14 11.5L16 13V16.5L14 18L12 16.5Z" fill="white"/>
          </svg>
          <span class="header-title">智考云</span>
        </router-link>
      </div>
      <el-menu class="header-menu" mode="horizontal" :default-active="defaultUrl" :router="true">
        <el-menu-item index="/index">首页</el-menu-item>
        <el-menu-item index="/paper/index">我的考试</el-menu-item>
        <el-menu-item index="/record/index">考试记录</el-menu-item>
        <el-menu-item index="/question/index">错题本</el-menu-item>
      </el-menu>
      <div class="header-right">
        <el-dropdown trigger="click" placement="bottom">
          <el-avatar class="header-avatar" size="small" :src="(userInfo.avatar === null || userInfo.avatar === undefined) ? require('@/assets/avatar.png') : userInfo.avatar"></el-avatar>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="$router.push({path:'/user/index'})">个人中心</el-dropdown-item>
            <el-dropdown-item @click.native="logout" divided>退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="student-main">
      <router-view/>
    </el-main>
    <div class="foot-copyright">
      <span>智考云在线考试平台</span>
    </div>
  </el-container>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import loginApi from '@/api/login'
import userApi from '@/api/user'
export default {
  name: 'Layout',
  data () {
    return {
      defaultUrl: '/index',
      userInfo: {
        avatar: null
      }
    }
  },
  created () {
    let _this = this
    this.defaultUrl = this.routeSelect(this.$route.path)
    userApi.getCurrentUser().then(re => {
      _this.userInfo = re.data
    })
  },
  watch: {
    $route (to, from) {
      this.defaultUrl = this.routeSelect(to.path)
    }
  },
  methods: {
    routeSelect (path) {
      let topPath = ['/', '/index', '/paper/index', '/record/index', '/question/index']
      if (topPath.includes(path)) {
        return path
      }
      return null
    },
    logout () {
      let _this = this
      loginApi.logout().then(function (result) {
        if (result && result.code === 200) {
          _this.clearLogin()
          _this.$router.push({ path: '/login' })
        }
      }).catch(function () {
        // 即使 API 失败也清除本地状态，确保用户能退出
        _this.clearLogin()
        _this.$router.push({ path: '/login' })
      })
    },
    ...mapMutations('user', ['clearLogin'])
  },
  computed: {
    ...mapState('user', {
      messageCount: state => state.messageCount
    })
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  min-height: 100vh;
}

.student-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px !important;
  background: rgba(255, 255, 255, 0.75) !important;
  backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  border-bottom: var(--glass-border) !important;
  box-shadow: var(--glass-shadow);
  height: 60px !important;
}

.header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.header-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--brand-dark);
  letter-spacing: 1px;
}

.header-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  border-bottom: none !important;
  background: transparent !important;
}

.header-menu .el-menu-item {
  font-size: 14px !important;
  color: var(--brand-muted) !important;
  height: 60px;
  line-height: 60px !important;
  border-bottom: none !important;
  margin: 0 4px;
  border-radius: 0;
}

.header-menu .el-menu-item:hover {
  color: var(--brand-dark) !important;
  background: transparent !important;
}

.header-menu .el-menu-item.is-active {
  color: var(--brand-dark) !important;
  font-weight: 600;
  border-bottom: 2px solid var(--brand-dark) !important;
}

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.el-main {
  margin-top: 60px;
}

.header-avatar {
  cursor: pointer;
  background: var(--brand-bg);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .student-header {
    padding: 0 12px !important;
  }

  .header-title {
    display: none;
  }

  .header-menu .el-menu-item {
    font-size: 12px !important;
    margin: 0 2px;
    padding: 0 8px;
  }
}
</style>

<style lang="scss">
/* Dropdown menu — VisionOS glassmorphism (non-scoped since el-dropdown-menu is teleported to body) */
.el-dropdown-menu {
  background: var(--glass-bg) !important;
  backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  border: var(--glass-border) !important;
  box-shadow: var(--glass-shadow) !important;
  border-radius: 12px !important;
  padding: 4px !important;
}

.el-dropdown-menu__item {
  color: var(--brand-muted) !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  margin: 2px 0 !important;
}

.el-dropdown-menu__item:hover {
  background: rgba(0, 0, 0, 0.05) !important;
  color: var(--brand-dark) !important;
}

.el-dropdown-menu__item--divided {
  border-top: 1px solid rgba(0, 0, 0, 0.06) !important;
}
</style>
