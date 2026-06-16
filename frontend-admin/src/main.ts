/**
 * 应用启动入口
 *
 * @description
 * Vue3 应用初始化，包括样式、插件、配置的加载
 */

// 全局错误兜底：捕获未处理异常并在页面展示，防止白屏
function showFatalError(err: unknown) {
  const msg = err instanceof Error ? err.message : String(err);
  const stack = err instanceof Error ? err.stack : "";
  const el = document.getElementById("app");
  if (el) {
    el.innerHTML = `<div style="padding:40px;font-family:monospace;color:#e53e3e;max-width:800px;margin:0 auto">
      <h2 style="margin-bottom:16px">应用加载出错</h2>
      <pre style="background:#f7f7f7;padding:16px;border-radius:8px;white-space:pre-wrap;word-break:break-all;font-size:13px">${msg}\n\n${stack}</pre>
      <p style="margin-top:16px;color:#666">请尝试刷新页面，或清除浏览器缓存后重试。</p>
    </div>`;
  }
}
window.addEventListener("unhandledrejection", (e) => showFatalError(e.reason));
window.addEventListener("error", (e) => showFatalError(e.error || e.message));

import { createApp } from "vue";
import App from "./App.vue";

// 样式导入
import "element-plus/dist/index.css";
import "element-plus/theme-chalk/dark/css-vars.css";
import "@/styles/index.scss";
import "uno.css";

// 核心配置
import { setupDirective } from "@/directives";
import { setupRouter } from "@/router";
import { setupStore } from "@/stores";

// 全局组件
import * as ElementPlusIcons from "@element-plus/icons-vue";

// 第三方插件（当前已无额外全局插件，如需使用 CodeMirror 等请在此添加）

// 路由守卫
import { setupPermissionGuard } from "@/router/guards/permission";

// 创建 Vue 应用实例
const app = createApp(App);

// 1️⃣ 核心配置
setupDirective(app);
setupRouter(app);
setupStore(app);

// 2️⃣ 全局组件（Element Plus 图标）
Object.entries(ElementPlusIcons).forEach(([name, comp]) => app.component(name, comp));

// 3️⃣ 路由守卫
setupPermissionGuard();

// 6️⃣ 挂载应用
try {
  app.mount("#app");
} catch (err) {
  showFatalError(err);
}
