import NProgress from "@/plugins/nprogress";
import router from "@/router";
import { useUserStore } from "@/stores";
import { getToken } from "@/utils/auth";

const ROLE_MAP: Record<number, string> = { 0: "STUDENT", 1: "TEACHER", 2: "ADMIN" };

export function setupPermissionGuard() {
  const whiteList = ["/login"];

  router.beforeEach((to, _from) => {
    NProgress.start();

    try {
      const token = getToken();
      const userStore = useUserStore();

      // 未登录
      if (!token) {
        if (whiteList.includes(to.path)) {
          return;
        }
        NProgress.done();
        return `/login?redirect=${encodeURIComponent(to.fullPath)}`;
      }

      // 已登录访问登录页 → 重定向首页
      if (to.path === "/login") {
        return { path: "/" };
      }

      // 首次进入：同步恢复缓存数据，异步请求最新用户信息（不阻塞导航，避免白屏）
      if (!userStore._initialized) {
        userStore.initFromCache();
      }

      // 角色权限校验（缓存数据已同步可用，此处不会因等待 API 而阻塞）
      const requiredRoles = to.meta.roles as string[] | undefined;
      if (requiredRoles && requiredRoles.length > 0) {
        try {
          const userRoleStr = ROLE_MAP[userStore.userInfo.role];
          if (!userRoleStr || !requiredRoles.includes(userRoleStr)) {
            NProgress.done();
            return "/401";
          }
        } catch {
          // 用户信息尚未从后台返回时角色检查容错，放行本次导航
        }
      }

      // 动态标题
      const title = (to.params.title as string) || (to.query.title as string);
      if (title) {
        to.meta.title = title;
      }
    } catch (error) {
      console.error("Route guard error:", error);
      NProgress.done();
      return "/login";
    }
  });

  router.afterEach(() => {
    NProgress.done();
  });
}
