/**
 * 布局 Composable
 *
 * 整合布局状态、设备检测、菜单数据
 */
import { useRoute } from "vue-router";
import { useWindowSize } from "@vueuse/core";
import { useAppStore, useSettingsStore, useUserStore } from "@/stores";
import { asyncRoutes } from "@/router";
import { DeviceEnum } from "@/enums/settings";
import { defaults } from "@/settings";

const DESKTOP_BREAKPOINT = 992;
const ROLE_MAP: Record<number, string> = { 0: "STUDENT", 1: "TEACHER", 2: "ADMIN" };

export function useLayout() {
  const route = useRoute();
  const appStore = useAppStore();
  const settingsStore = useSettingsStore();
  const userStore = useUserStore();
  const { width } = useWindowSize();

  // 设备检测

  const isDesktop = computed(() => width.value >= DESKTOP_BREAKPOINT);
  const isMobile = computed(() => appStore.device === DeviceEnum.MOBILE);

  // 监听窗口变化，自动调整设备类型和侧边栏
  watchEffect(() => {
    const device = isDesktop.value ? DeviceEnum.DESKTOP : DeviceEnum.MOBILE;
    appStore.toggleDevice(device);

    if (isDesktop.value) {
      appStore.openSideBar();
    } else {
      appStore.closeSideBar();
    }
  });

  // 布局状态

  const currentLayout = computed(() => settingsStore.layout);
  const isSidebarOpen = computed(() => appStore.sidebar.opened);
  const showTagsView = computed(() => settingsStore.showTagsView);
  const showSettings = computed(() => defaults.showSettings);
  const showLogo = computed(() => settingsStore.showAppLogo);

  const layoutClass = computed(() => ({
    hideSidebar: !appStore.sidebar.opened,
    openSidebar: appStore.sidebar.opened,
    mobile: appStore.device === DeviceEnum.MOBILE,
    [`layout-${settingsStore.layout}`]: true,
  }));

  // 菜单数据（直接从 router 静态路由取）

  /** 路由列表（左侧/顶部菜单） */
  const routes = computed(() => {
    // 取 "/" 路由 of children，且过滤掉 hidden 与无角色权限的路由
    const rootRoute = asyncRoutes.find((r) => r.path === "/");
    if (!rootRoute?.children) return [];
    
    const userRoleStr = ROLE_MAP[userStore.userInfo.role];

    return rootRoute.children.filter((r) => {
      // 1. 过滤 hidden 的路由
      if (r.meta?.hidden) return false;

      // 2. 根据角色过滤路由显示
      const requiredRoles = r.meta?.roles as string[] | undefined;
      if (requiredRoles && requiredRoles.length > 0) {
        if (!userRoleStr || !requiredRoles.includes(userRoleStr)) {
          return false;
        }
      }
      return true;
    });
  });

  /** 混合布局侧边菜单（暂简化处理，返回全部路由） */
  const sideMenuRoutes = computed(() => routes.value);

  /** 顶部菜单激活路径 */
  const activeTopMenuPath = computed(() => appStore.activeTopMenuPath);

  /** 当前激活菜单 */
  const activeMenu = computed(() => {
    const { meta, path } = route;
    return meta?.activeMenu || path;
  });

  // ============================================
  // 操作方法
  // ============================================

  function toggleSidebar() {
    appStore.toggleSidebar();
  }

  function closeSidebar() {
    appStore.closeSideBar();
  }

  return {
    // 设备
    isDesktop,
    isMobile,
    // 布局
    currentLayout,
    layoutClass,
    isSidebarOpen,
    showTagsView,
    showSettings,
    showLogo,
    // 菜单
    routes,
    sideMenuRoutes,
    activeMenu,
    activeTopMenuPath,
    // 方法
    toggleSidebar,
    closeSidebar,
  };
}
