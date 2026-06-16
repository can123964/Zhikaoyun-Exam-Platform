/**
 * 应用配置
 */

import { LayoutMode, ComponentSize, SidebarColor, ThemeMode, LanguageEnum } from "@/enums";

const env = import.meta.env;
const { pkg } = __APP_INFO__;
const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;

// 应用配置
export const appConfig = {
  name: "智考云",
  version: pkg.version as string,
  title: "智考云在线考试平台",
} as const;

// 用户偏好默认值
export const defaults = {
  theme: prefersDark ? ThemeMode.DARK : ThemeMode.LIGHT,
  themeColor: "#007AFF",
  sidebarColorScheme: SidebarColor.CLASSIC_BLUE,
  layout: LayoutMode.LEFT,
  size: ComponentSize.DEFAULT,
  language: LanguageEnum.ZH_CN,
  showTagsView: true,
  showAppLogo: true,
  showWatermark: false,
  pageSwitchingAnimation: "fade-slide",
  showSettings: true,
  watermarkContent: pkg.name,
} as const;

// 主题色预设
export const themeColorPresets = [
  "#007AFF",
  "#1890FF",
  "#409EFF",
  "#FA8C16",
  "#722ED1",
  "#13C2C2",
  "#52C41A",
  "#F5222D",
  "#2F54EB",
  "#EB2F96",
] as const;
