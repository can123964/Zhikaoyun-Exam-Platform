import { ElNotification } from "element-plus";
import router from "@/router";

const TOKEN_KEY = "exam_token";
const USER_KEY = "exam_user";

/** 获取 Token */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

/** 存储 Token */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

/** 删除 Token */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

/** 缓存用户基本信息（登录成功后调用，供路由守卫快速读取角色） */
export function setCachedUser(user: {
  id?: number;
  role: number;
  username: string;
  realName: string;
  avatar?: string;
}): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

/** 读取缓存的用户信息 */
export function getCachedUser(): {
  id?: number;
  role: number;
  username: string;
  realName: string;
  avatar?: string;
} | null {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

/**
 * 简单角色检查（按钮级权限用）
 *
 * @example v-if="hasRole(2)"  → 仅管理员可见
 */
export function hasRole(...roles: number[]): boolean {
  const user = getCachedUser();
  if (!user) return false;
  return roles.includes(user.role);
}

/**
 * 重定向到登录页
 */
export function redirectToLogin(message: string = "请重新登录"): void {
  removeToken();
  ElNotification({
    title: "提示",
    message,
    type: "warning",
    duration: 3000,
  });

  try {
    const currentPath = router.currentRoute.value.fullPath;
    router.push(`/login?redirect=${encodeURIComponent(currentPath)}`);
  } catch {
    window.location.href = "/login";
  }
}
