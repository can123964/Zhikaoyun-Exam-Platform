<template>
  <div :class="['navbar-actions', navbarActionsClass]">
    <!-- 桌面端工具项 -->
    <template v-if="isDesktop">
      <!-- 搜索 -->
      <div class="navbar-actions__item">
        <CommandPalette />
      </div>

      <!-- 全屏 -->
      <div class="navbar-actions__item">
        <Fullscreen />
      </div>

      <!-- 布局大小 -->
      <div class="navbar-actions__item">
        <SizeSelect />
      </div>

      <!-- 语言选择 -->
      <div class="navbar-actions__item">
        <LangSelect />
      </div>
    </template>

    <!-- 用户菜单 -->
    <div class="navbar-actions__item">
      <el-dropdown trigger="click">
        <div class="user-profile">
          <div class="user-profile__avatar-wrapper">
            <img
              :src="userStore.userInfo.avatar"
              class="user-profile__avatar"
              @error="avatarLoadError = true"
              @load="avatarLoadError = false"
            />
            <div v-if="avatarLoadError || !userStore.userInfo.avatar" class="user-profile__avatar-fallback">
              {{ avatarInitial }}
            </div>
          </div>
          <span class="user-profile__name">{{ userStore.userInfo.username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleProfileClick">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 系统设置 -->
    <div v-if="defaults.showSettings" class="navbar-actions__item" @click="handleSettingsClick">
      <div class="i-svg:setting" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { defaults } from "@/settings";
import { DeviceEnum, SidebarColor, ThemeMode, LayoutMode } from "@/enums/settings";
import { useAppStore, useSettingsStore, useUserStore } from "@/stores";

// 导入子组件
import CommandPalette from "@/components/CommandPalette/index.vue";
import Fullscreen from "@/components/Fullscreen/index.vue";
import SizeSelect from "@/components/SizeSelect/index.vue";
import LangSelect from "@/components/LangSelect/index.vue";
const appStore = useAppStore();
const settingStore = useSettingsStore();
const userStore = useUserStore();

const route = useRoute();
const router = useRouter();

// 头像加载状态
const avatarLoadError = ref(false);

// 头像首字母 fallback
const avatarInitial = computed(() => {
  const name = userStore.userInfo.username || "";
  return name.charAt(0).toUpperCase();
});

// 是否为桌面设备
const isDesktop = computed(() => appStore.device === DeviceEnum.DESKTOP);

/**
 * 打开个人中心页面
 */
function handleProfileClick() {
  router.push({ name: "Profile" });
}

// 根据主题和侧边栏配色方案选择样式
const navbarActionsClass = computed(() => {
  const { theme, sidebarColorScheme, layout } = settingStore;

  // 暗黑主题下，所有布局都使用白色文字
  if (theme === ThemeMode.DARK) {
    return "navbar-actions--white-text";
  }

  // 明亮主题
  if (theme === ThemeMode.LIGHT) {
    // 顶部布局和混合布局的顶部区域：
    // - 如果侧边栏是经典蓝色，使用白色文字
    // - 如果侧边栏是极简白色，使用深色文字
    if (layout === LayoutMode.TOP || layout === LayoutMode.MIX) {
      if (sidebarColorScheme === SidebarColor.CLASSIC_BLUE) {
        return "navbar-actions--white-text";
      } else {
        return "navbar-actions--dark-text";
      }
    }
  }

  return "navbar-actions--dark-text";
});

/**
 * 退出登录
 */
function logout() {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    lockScroll: false,
  }).then(() => {
    userStore.logout().finally(() => {
      // 若当前已在 404/401 等错误页，退出后不再跳回错误页
      const redirect = ["/404", "/401"].includes(route.path) ? "/" : route.fullPath;
      router.push(`/login?redirect=${encodeURIComponent(redirect)}`);
    });
  });
}

/**
 * 打开系统设置页面
 */
function handleSettingsClick() {
  settingStore.settingsVisible = true;
}
</script>

<style lang="scss" scoped>
.navbar-actions {
  display: flex;
  align-items: center;
  min-height: 44px;

  &__item {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 44px; /* 增加最小点击区域到44px，符合人机交互标准 */
    height: 44px;
    padding: 0 8px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;

    // 只对需要居中的子元素生效，不使用通配符避免影响选择器组件
    > [class^="i-svg:"] {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    // 确保 Element Plus 组件可以正常工作
    :deep(.el-dropdown),
    :deep(.el-tooltip) {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 44px;
    }

    :deep(.i-svg\:language) {
      flex-shrink: 0;
      width: 18px;
      height: 18px;
      font-size: 18px;
      line-height: 18px;
      background-size: 18px 18px;
    }

    // 图标样式
    :deep([class^="i-svg:"]) {
      font-size: 18px;
      line-height: 1;
      color: var(--el-text-color-regular);
      fill: currentColor;
      transition: color 0.3s;
    }

    // Element Plus 图标容器
    :deep(.el-icon) {
      font-size: 18px;
      color: var(--el-text-color-regular);
      transition: color 0.3s;
    }

    &:hover {
      background: var(--el-fill-color-light);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-primary);
        fill: currentColor;
      }

      :deep(.el-icon) {
        color: var(--el-color-primary);
      }
    }
  }

  .user-profile {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 44px;
    padding: 0 8px;

    &__avatar-wrapper {
      position: relative;
      width: 28px;
      height: 28px;
      overflow: hidden;
      border-radius: 50%;
      flex-shrink: 0;
    }

    &__avatar {
      display: block;
      width: 28px;
      height: 28px;
      object-fit: cover;
      object-position: center;
      border-radius: 50%;
    }

    &__avatar-fallback {
      position: absolute;
      top: 0;
      left: 0;
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: var(--brand-accent);
      color: #fff;
      font-size: 13px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    &__name {
      margin-left: 8px;
      color: var(--brand-slate);
      white-space: nowrap;
      font-size: 13px;
      font-weight: 500;
      transition: color 0.3s;
    }
  }
}

// 白色文字样式（用于深色背景：暗黑主题、顶部布局、混合布局）
.navbar-actions--white-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: rgba(255, 255, 255, 0.85) !important;
      fill: currentColor !important;
    }

    :deep(.el-icon) {
      color: rgba(255, 255, 255, 0.85) !important;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.1);

      :deep([class^="i-svg:"]) {
        color: #fff !important;
        fill: currentColor !important;
      }

      :deep(.el-icon) {
        color: #fff !important;
      }
    }
  }

  .user-profile__name {
    color: rgba(255, 255, 255, 0.85);
  }
}

// 深色文字样式（用于浅色背景：明亮主题下的左侧布局）
.navbar-actions--dark-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: #424245 !important;
      fill: currentColor !important;
    }

    :deep(.el-icon) {
      color: #424245 !important;
    }

    &:hover {
      background: rgba(0, 0, 0, 0.04);
      border-radius: 10px;

      :deep([class^="i-svg:"]) {
        color: #007AFF !important;
        fill: currentColor !important;
      }

      :deep(.el-icon) {
        color: #007AFF !important;
      }
    }
  }

  .user-profile__name {
    color: #424245 !important;
  }
}

// 确保下拉菜单中的图标不受影响
::v-deep(.el-dropdown-menu) {
  [class^="i-svg:"] {
    color: var(--el-text-color-regular) !important;

    &:hover {
      color: var(--el-color-primary) !important;
    }
  }
}
</style>
