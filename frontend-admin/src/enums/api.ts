/**
 * API 响应码枚举（对齐智考云后端）
 */
export const enum ApiCodeEnum {
  /** 成功 */
  SUCCESS = 200,
  /** 未登录 / Token 过期 */
  UNAUTHORIZED = 401,
  /** 无权限 */
  FORBIDDEN = 403,
}
