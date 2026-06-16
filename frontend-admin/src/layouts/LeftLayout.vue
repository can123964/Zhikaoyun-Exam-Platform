<template>
  <div class="layout-bg">
    <div class="layout-grid"></div>
  </div>
  <BaseLayout>
    <!-- 左侧菜单 -->
    <div class="layout__sidebar" :class="{ 'layout__sidebar--collapsed': !isSidebarOpen }">
      <div :class="{ 'has-logo': showLogo }" class="layout-sidebar">
        <LayoutLogo v-if="showLogo" :collapse="!isSidebarOpen" />
        <el-scrollbar>
          <LayoutSidebar :data="routes" base-path="" />
        </el-scrollbar>
      </div>
    </div>

    <!-- 主内容区 -->
    <div
      class="layout__main"
      :class="{
        hasTagsView: showTagsView,
        'layout__main--collapsed': !isSidebarOpen,
      }"
    >
      <LayoutNavbar />
      <LayoutTagsView v-if="showTagsView" />
      <div class="layout__main-content">
        <LayoutMain />
      </div>
    </div>
  </BaseLayout>
</template>

<script setup lang="ts">
import { useLayout } from "./useLayout";
import BaseLayout from "./BaseLayout.vue";
import LayoutLogo from "./components/LayoutLogo.vue";
import LayoutNavbar from "./components/LayoutNavbar.vue";
import LayoutTagsView from "./components/LayoutTagsView.vue";
import LayoutMain from "./components/LayoutMain.vue";
import LayoutSidebar from "./components/LayoutSidebar.vue";

const { showTagsView, showLogo, isSidebarOpen, routes } = useLayout();
</script>

<style lang="scss" scoped>
.layout-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  background: #edf0f5;
  background-image: linear-gradient(135deg, #e8edf5 0%, #edf0f7 25%, #f0eef5 50%, #edf2f5 75%, #eef0f5 100%);
  pointer-events: none;

  // 装饰性渐变球 — 让玻璃面板后面有颜色变化，增强毛玻璃透视感
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

html.dark .layout-sidebar {
  background: rgba(44, 44, 46, 0.85) !important;
  border-right-color: rgba(255, 255, 255, 0.08) !important;
}

.layout {
  &__sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: 998;
    width: $sidebar-width;
    background: transparent;
    transition: width 0.28s;

    &--collapsed {
      width: $sidebar-width-collapsed;
    }

    .layout-sidebar {
      position: relative;
      height: 100%;
      backdrop-filter: blur(20px) saturate(150%);
      -webkit-backdrop-filter: blur(20px) saturate(150%);
      background: rgba(255, 255, 255, 0.72);
      border-right: 1px solid rgba(255, 255, 255, 0.6);
      box-shadow:
        2px 0 16px rgba(0, 0, 0, 0.04),
        inset -1px 0 0 rgba(255, 255, 255, 0.5);
      transition: width 0.28s;

      &.has-logo {
        .el-scrollbar {
          height: calc(100vh - $navbar-height);
        }
      }
    }
  }

  &__main {
    position: relative;
    z-index: 1;
    height: 100%;
    margin-left: $sidebar-width;
    transition: margin-left 0.28s;

    &--collapsed {
      margin-left: $sidebar-width-collapsed;
    }

    &-content {
      padding: 24px;
      height: calc(100vh - $navbar-height - $tags-view-height - 48px);
      overflow-y: auto;
    }
  }
}

.hasTagsView {
  .layout__main-content {
    min-height: calc(100vh - $navbar-height - $tags-view-height - 48px);
  }
}

.mobile {
  .layout__sidebar {
    width: $sidebar-width !important;
  }

  &.hideSidebar .layout__sidebar {
    transform: translateX(-$sidebar-width);
  }

  &.openSidebar .layout__sidebar {
    transform: translateX(0);
  }

  .layout__main {
    margin-left: 0 !important;
  }
}
</style>
