/**
 * 应用常量定义
 */

/** 应用存储前缀 */
export const APP_PREFIX = "vea";

/** 存储键名常量 */
export const STORAGE_KEYS = {
  // 认证（实际使用 auth.ts 中的 exam_token / exam_user，此处仅作类型参考）
  TOKEN: "exam_token",
  USER: "exam_user",

  // UI 设置
  SHOW_TAGS_VIEW: `${APP_PREFIX}:ui:show_tags_view`,
  SHOW_APP_LOGO: `${APP_PREFIX}:ui:show_app_logo`,
  SHOW_WATERMARK: `${APP_PREFIX}:ui:show_watermark`,
  PAGE_SWITCHING_ANIMATION: `${APP_PREFIX}:ui:page_switching_animation`,
  LAYOUT: `${APP_PREFIX}:ui:layout`,
  SIDEBAR_COLOR_SCHEME: `${APP_PREFIX}:ui:sidebar_color_scheme`,
  THEME: `${APP_PREFIX}:ui:theme`,
  THEME_COLOR: `${APP_PREFIX}:ui:theme_color`,
  GRAY_MODE: `${APP_PREFIX}:ui:gray_mode`,
  COLOR_WEAK: `${APP_PREFIX}:ui:color_weak`,

  // 应用状态
  DEVICE: `${APP_PREFIX}:app:device`,
  SIZE: `${APP_PREFIX}:app:size`,
  LANGUAGE: `${APP_PREFIX}:app:language`,
  SIDEBAR_STATUS: `${APP_PREFIX}:app:sidebar_status`,
  ACTIVE_TOP_MENU_PATH: `${APP_PREFIX}:app:active_top_menu_path`,
} as const;

export type StorageKey = (typeof STORAGE_KEYS)[keyof typeof STORAGE_KEYS];
