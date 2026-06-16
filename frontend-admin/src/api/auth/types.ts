/**
 * 认证相关类型定义（对齐智考云后端）
 */

/** 登录请求 */
export interface LoginRequest {
  username: string;
  password: string;
}

/** 登录响应 */
export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  realName: string;
  role: number; // 0=学生 1=教师 2=管理员
  avatar?: string;
}
