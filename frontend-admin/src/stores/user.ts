import request from "@/utils/request";
import { store } from "@/stores";

import AuthAPI, { type LoginRequest } from "@/api/auth";
import { getToken, setToken, removeToken, setCachedUser, getCachedUser } from "@/utils/auth";
import { useTagsViewStore } from "@/stores";

/** 用户信息（GET /api/user/info 返回的结构） */
export interface UserInfo {
  id: number;
  username: string;
  realName: string;
  role: number; // 0=学生 1=教师 2=管理员
  roleName: string;
  phone?: string;
  email?: string;
  avatar?: string;
  status?: number;
  studentNo?: string;
  classId?: number;
  lastLoginTime?: string;
  loginCount?: number;
  createTime?: string;
}

export const useUserStore = defineStore("user", () => {
  const userInfo = ref<UserInfo>({} as UserInfo);

  /** 是否已完成初始化（从缓存恢复 + 尝试请求最新数据），防止守卫重复进入 */
  const _initialized = ref(false);

  /** 是否已登录 */
  const isLoggedIn = () => !!getToken();

  /** 当前角色 */
  const roleName = computed(() => {
    const map: Record<number, string> = { 0: "学生", 1: "教师", 2: "管理员" };
    return map[userInfo.value.role] || "未知";
  });

  /**
   * 登录
   */
  async function login(loginRequest: LoginRequest): Promise<void> {
    const { token, userId, username, realName, role, avatar } = await AuthAPI.login(loginRequest);

    // 学生角色不允许登录管理端
    if (role === 0) {
      throw new Error("学生账号请使用学生端登录");
    }

    setToken(token);
    setCachedUser({ id: userId, role, username, realName, avatar });
    userInfo.value = { id: userId, username, realName, role, roleName: "", avatar };
    _initialized.value = true;
  }

  /**
   * 获取用户信息（GET /api/user/info）
   */
  async function getUserInfo(): Promise<UserInfo> {
    const res = await request.get<UserInfo>("/api/user/info");
    Object.assign(userInfo.value, res);
    return userInfo.value;
  }

  /**
   * 退出登录
   */
  async function logout(): Promise<void> {
    try {
      await AuthAPI.logout();
    } catch {
      // 忽略后端退出接口失败
    } finally {
      removeToken();
      userInfo.value = {} as UserInfo;
      _initialized.value = false;
      useTagsViewStore().delAllViews();
    }
  }

  /**
   * 初始化：页面刷新后从缓存恢复用户信息并请求最新数据
   *
   * 缓存数据同步写入 store，保证路由守卫可以立即使用角色等信息，
   * API 请求在后台异步执行，不阻塞导航渲染，避免白屏。
   */
  function initFromCache(): void {
    if (!getToken()) {
      _initialized.value = true;
      return;
    }

    // 1. 同步恢复缓存数据（不强制 id:0，保留已有值）
    const cached = getCachedUser();
    if (cached) {
      userInfo.value = { ...userInfo.value, ...cached, roleName: "" };
    }

    // 2. 异步请求最新用户信息（不阻塞导航）
    getUserInfo()
      .catch(() => {
        // 获取失败则继续使用缓存数据，不做额外处理
        // 若返回 401，响应拦截器会自动 redirectToLogin
      })
      .finally(() => {
        _initialized.value = true;
      });
  }

  return {
    userInfo,
    _initialized,
    roleName,
    isLoggedIn,
    login,
    logout,
    getUserInfo,
    initFromCache,
  };
});

/**
 * 组件外部使用
 */
export function useUserStoreHook() {
  return useUserStore(store);
}
