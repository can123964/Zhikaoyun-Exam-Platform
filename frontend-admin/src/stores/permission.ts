/**
 * 权限 Store — 精简版
 *
 * 旧版从后端动态获取菜单路由，现在改为静态路由，此 store 仅提供兼容接口。
 */
import { store } from "@/stores";
import { asyncRoutes } from "@/router";
import type { RouteRecordRaw } from "vue-router";

export const usePermissionStore = defineStore("permission", () => {
  /** 是否已生成路由 */
  const isRouteGenerated = ref(true);

  /** 菜单路由列表 */
  const routes = computed(() => {
    const rootRoute = asyncRoutes.find((r) => r.path === "/");
    if (!rootRoute?.children) return [];
    return rootRoute.children.filter((r) => !r.meta?.hidden);
  });

  /** 混合布局侧边菜单 */
  const mixLayoutSideMenus = ref<RouteRecordRaw[]>([]);

  /** 设置混合布局侧边菜单（根据顶级菜单路径过滤子路由） */
  function setMixLayoutSideMenus(topMenuPath: string) {
    const matched = routes.value.find(
      (r) => r.path === topMenuPath || `/${r.path}` === topMenuPath
    );
    mixLayoutSideMenus.value = (matched?.children as RouteRecordRaw[]) ?? [];
  }

  return {
    isRouteGenerated,
    routes,
    mixLayoutSideMenus,
    setMixLayoutSideMenus,
  };
});

/** 组件外部使用 */
export function usePermissionStoreHook() {
  return usePermissionStore(store);
}
