import { SidebarColor, ThemeMode } from "@/enums";
import type { LayoutMode } from "@/enums";
import { applyTheme, generateThemeColors, toggleDarkMode, toggleSidebarColor } from "@/utils/theme";
import { STORAGE_KEYS } from "@/constants";
import { defaults } from "@/settings";

export const useSettingsStore = defineStore("setting", () => {
  // 界面显示
  const settingsVisible = ref(false);
  const showTagsView = useStorage(STORAGE_KEYS.SHOW_TAGS_VIEW, defaults.showTagsView);
  const showAppLogo = useStorage(STORAGE_KEYS.SHOW_APP_LOGO, defaults.showAppLogo);
  const showWatermark = useStorage(STORAGE_KEYS.SHOW_WATERMARK, defaults.showWatermark);
  const pageSwitchingAnimation = useStorage(
    STORAGE_KEYS.PAGE_SWITCHING_ANIMATION,
    defaults.pageSwitchingAnimation
  );

  // 布局
  const layout = useStorage<LayoutMode>(STORAGE_KEYS.LAYOUT, defaults.layout as LayoutMode);
  const sidebarColorScheme = useStorage(
    STORAGE_KEYS.SIDEBAR_COLOR_SCHEME,
    defaults.sidebarColorScheme
  );

  // 主题
  const theme = useStorage<ThemeMode>(STORAGE_KEYS.THEME, defaults.theme);
  const themeColor = useStorage(STORAGE_KEYS.THEME_COLOR, defaults.themeColor);

  // 迁移旧主题色：如果 localStorage 中缓存了旧版 Neumorphism 风格的紫色，
  // 自动替换为 Spatial UI 的 Apple 蓝 #007AFF
  if (themeColor.value && themeColor.value.toLowerCase() === "#6c63ff") {
    themeColor.value = defaults.themeColor;
  }

  // 特殊模式
  const grayMode = useStorage(STORAGE_KEYS.GRAY_MODE, false);
  const colorWeak = useStorage(STORAGE_KEYS.COLOR_WEAK, false);

  // 主题变化监听（含容错：localStorage 中的无效数据不会导致页面崩溃）
  watch(
    [theme, themeColor],
    ([t, c]: [ThemeMode, string]) => {
      try {
        toggleDarkMode(t === ThemeMode.DARK);
        applyTheme(generateThemeColors(c || defaults.themeColor, t || defaults.theme));
      } catch {
        // 忽略主题初始化错误，防止 localStorage 脏数据导致白屏
      }
    },
    { immediate: true }
  );

  watch(sidebarColorScheme, (v) => {
    try {
      toggleSidebarColor(v === SidebarColor.CLASSIC_BLUE);
    } catch {
      // 忽略
    }
  }, {
    immediate: true,
  });

  // 灰色模式监听
  watch(
    grayMode,
    (v) => {
      document.documentElement.style.filter = v ? "grayscale(100%)" : "";
    },
    { immediate: true }
  );

  // 色弱模式监听
  watch(
    colorWeak,
    (v) => {
      document.documentElement.classList.toggle("color-weak", v);
    },
    { immediate: true }
  );

  function resetSettings() {
    showTagsView.value = defaults.showTagsView;
    showAppLogo.value = defaults.showAppLogo;
    showWatermark.value = defaults.showWatermark;
    pageSwitchingAnimation.value = defaults.pageSwitchingAnimation;
    grayMode.value = false;
    colorWeak.value = false;
    sidebarColorScheme.value = defaults.sidebarColorScheme;
    layout.value = defaults.layout as LayoutMode;
    themeColor.value = defaults.themeColor;
    theme.value = defaults.theme;
  }

  return {
    settingsVisible,
    showTagsView,
    showAppLogo,
    showWatermark,
    pageSwitchingAnimation,
    grayMode,
    colorWeak,
    sidebarColorScheme,
    layout,
    themeColor,
    theme,
    resetSettings,
  };
});
