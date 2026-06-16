<template>
  <div class="layout-bg">
    <div class="layout-grid"></div>
  </div>
  <BaseLayout>
    <!-- 顶部菜单-->
    <div class="layout__header">
      <div class="layout__header-left">
        <div v-if="showLogo" class="layout__header-logo">
          <LayoutLogo :collapse="isLogoCollapsed" />
        </div>
        <div class="layout__header-menu">
          <LayoutSidebar :data="topMenuItems" menu-mode="horizontal" base-path="" />
        </div>
      </div>
      <div class="layout__header-right">
        <LayoutToolbar />
      </div>
    </div>

    <!-- 主内容区 -->
    <div :class="{ hasTagsView: showTagsView }" class="layout__main">
      <LayoutTagsView v-if="showTagsView" />
      <LayoutMain />
    </div>
  </BaseLayout>
</template>

<script setup lang="ts">
import { useWindowSize } from "@vueuse/core";
import { useLayout } from "./useLayout";
import { usePermissionStore } from "@/stores";
import BaseLayout from "./BaseLayout.vue";
import LayoutLogo from "./components/LayoutLogo.vue";
import LayoutSidebar from "./components/LayoutSidebar.vue";
import LayoutToolbar from "./components/LayoutToolbar.vue";
import LayoutTagsView from "./components/LayoutTagsView.vue";
import LayoutMain from "./components/LayoutMain.vue";

const { showTagsView, showLogo } = useLayout();
const { width } = useWindowSize();

const permissionStore = usePermissionStore();

const topMenuItems = computed(() => {
  return permissionStore.routes.filter((item) => !item.meta?.hidden);
});

const isLogoCollapsed = computed(() => width.value < 768);
</script>

<style lang="scss" scoped>
.layout-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  background: #edf0f5;
  background-image: linear-gradient(135deg, #e8edf5 0%, #edf0f7 25%, #f0eef5 50%, #edf2f5 75%, #eef0f5 100%);
  pointer-events: none;

  &::before {
    content: "";
    position: absolute;
    top: -80px;
    right: -60px;
    width: 500px;
    height: 500px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(0, 122, 255, 0.07) 0%, transparent 70%);
    pointer-events: none;
  }

  &::after {
    content: "";
    position: absolute;
    bottom: -100px;
    left: 200px;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(175, 82, 222, 0.05) 0%, transparent 70%);
    pointer-events: none;
  }
}

html.dark .layout-bg {
  background: #1c1c1e;
  background-image: none;

  &::before,
  &::after {
    display: none;
  }
}

.layout {
  &__header {
    position: sticky;
    top: 0;
    z-index: 999;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    height: $navbar-height;
    backdrop-filter: blur(40px) saturate(200%);
    -webkit-backdrop-filter: blur(40px) saturate(200%);
    background: rgba(255, 255, 255, 0.55);
    border-bottom: 1px solid rgba(255, 255, 255, 0.6);
    box-shadow:
      0 1px 8px rgba(0, 0, 0, 0.06),
      inset 0 -1px 0 rgba(255, 255, 255, 0.4);

    &-left {
      display: flex;
      flex: 1;
      align-items: center;
      min-width: 0;
      height: 100%;
    }

    &-logo {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      height: 100%;

      :deep(.logo) {
        height: $navbar-height;
      }
    }

    &-menu {
      display: flex;
      flex: 1;
      align-items: center;
      min-width: 0;
      height: 100%;
      overflow: hidden;

      :deep(.el-menu) {
        height: 100%;
        background-color: transparent;
        border: none;
      }

      :deep(.el-menu--horizontal) {
        flex: 1;
        min-width: 0;
        height: $navbar-height;
        overflow: hidden;
        line-height: $navbar-height;
        background-color: transparent;
        border: none;

        .el-menu-item {
          height: $navbar-height;
          line-height: $navbar-height;
        }

        .el-sub-menu {
          .el-sub-menu__title {
            height: $navbar-height;
            line-height: $navbar-height;
          }

          &.has-active-child {
            .el-sub-menu__title {
              color: var(--el-color-primary) !important;
              border-bottom: none !important;
              box-shadow: inset 0 -2px 0 0 var(--el-color-primary);

              .menu-icon {
                color: var(--el-color-primary) !important;
              }
            }
          }
        }

        .el-menu--popup {
          min-width: 160px;
        }
      }
    }

    &-right {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      height: 100%;
      padding-left: 12px;
    }
  }

  &__main {
    height: calc(100vh - $navbar-height);
    overflow-y: auto;
  }
}

.hasTagsView {
  :deep(.app-main) {
    height: calc(100vh - $navbar-height - $tags-view-height) !important;
  }
}

html.dark .layout__header {
  background: rgba(44, 44, 46, 0.85);
  border-bottom-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.2);
}
</style>
