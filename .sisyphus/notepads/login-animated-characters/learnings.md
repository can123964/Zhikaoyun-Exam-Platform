# Login Animated Characters - Learnings

## Files Created
- `frontend-student/src/views/login/AnimatedCharacters.vue` - 4 CSS角色组件 (Vue 2 Options API)
- `frontend-student/src/views/login/index.vue` - 重构为VisionOS两栏布局

## Architecture
- **AnimatedCharacters.vue**: 动画状态使用非响应式实例属性（直接DOM操作），保证60fps
  - `created()` 中初始化状态变量
  - `mounted()` 中缓存DOM引用、附加事件监听、启动眨眼定时器
  - `beforeDestroy()` 中清理所有定时器和事件监听
  - 通过 `$refs` 暴露方法给父组件调用：`setTyping`, `setPasswordFocus`, `setShowPassword`, `setHasPassword`, `triggerError`
  - 通过 `$emit('password-visible', bool)` 上报密码可见状态

- **index.vue**: 
  - 左面板：品牌logo + 动画角色（`var(--brand-bg)` 浅色渐变背景）
  - 右面板：登录表单（`backdrop-filter: blur()` 玻璃效果）
  - 密码框使用 `:type="pwdFieldType"` + custom SVG toggle（不使用 `show-password`）
  - 焦点事件通过 `@focus.native` / `@blur.native` 监听

## Key Decisions
- 角色动画完全复刻参考实现的6种交互状态：空闲、打字、密码回避、显示密码偷看、登录失败摇头、随机眨眼
- Element UI 的 `el-input` 在 Vue 2 中使用 `suffix` slot 放置自定义密码切换图标
- 使用 `requestAnimationFrame` 节流 mousemove 处理器
- 响应式：≤768px 隐藏左面板（角色场景）

## Admin Version (Vue 3 Composition API)
- `frontend-admin/src/views/login/AnimatedCharacters.vue` - Vue 3 `<script setup lang="ts">` + TypeScript
- `frontend-admin/src/views/login/index.vue` - VisionOS两栏布局

### Differences from Student (Vue 2) Version
- Vue 3 Composition API: `ref`, `watch`, `onMounted`, `onUnmounted` 替代 Vue 2 的 `data`/`methods`/`created`/`beforeDestroy`
- 使用 `defineProps` + `defineEmits` 替代 Vue 2 的 `props`/`$emit`
- 使用 `document.getElementById` 直接操作 DOM 替代 Vue 2 的 `this.$refs` 暴露方法
- 通过 watch props (`usernameFocus`, `passwordFocus`, `passwordVisible`, `loginError`) 控制动画状态
- Element Plus (Vue 3) 的 `el-input` 使用 `@focus` / `@blur` 而不是 `@focus.native`
- Element Plus 的 suffix slot 用法相同
- 密码框使用 `:type="pwdFieldType"` computed 绑定 + 自定义 SVG toggle

### Build Notes
- `pnpm run build` = `vue-tsc --noEmit && vite build`
- vue-tsc 类型检查发现 business/ 目录下存在预存 TS 错误（与本次修改无关）
- Vite build 成功 (4.63s)，login chunk 正确生成

## Reference
- 参考实现: https://github.com/guohaolian/animatedlogin/blob/main/index.html
- 角色参数（颜色/尺寸/眼睛位置）完全遵循参考实现
