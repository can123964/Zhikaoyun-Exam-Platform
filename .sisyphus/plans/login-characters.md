# 登录页交互动画角色

## TL;DR

> **Quick Summary**: 在管理端和学生端的登录页面添加 4 个 CSS 动画角色，角色眼睛跟随鼠标移动，输入密码时回避/闭眼，登录失败时摇头沮丧。两个前端统一采用 VisionOS Spatial UI 风格的左右两栏布局。
> 
> **Deliverables**:
> - 学生端登录页：VisionOS 风格左面板（品牌+角色）+ 右面板（表单）（Vue 2 组件）
> - 管理端登录页：VisionOS 风格左面板（品牌+角色）+ 右面板（表单）（Vue 3 组件）
> - 两个前端的角色外观、交互行为和布局风格完全一致
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 2 waves
> **Critical Path**: Task 1 (学生端) → Task 2 (管理端) → Task 3 (联合验证)

---

## Context

### Original Request
用户希望参考 https://github.com/guohaolian/animatedlogin 项目，在两个前端的登录页面同时实现动画角色效果。角色眼睛跟随鼠标移动，输入密码时闭眼/回避。

### Interview Summary
**Key Discussions**:
- 参考实现是单 HTML 文件，4 个 CSS 角色（紫色矩形、黑色矩形、橙色半圆、黄色圆角矩形）
- **两个前端统一采用 VisionOS Spatial UI 风格的左右两栏布局**
- 学生端（Vue 2）当前的左面板是旧样式（蓝色渐变+功能列表），需要完全重写为 VisionOS 风格
- 管理端（Vue 3）当前是居中卡片布局，需要重构为左右两栏
- 两个前端技术栈不同（Vue 3 vs Vue 2），需要各自独立实现
- 两个前端已有 VisionOS CSS 变量（glass tokens），左面板风格可以复用

**Research Findings**:
- 参考代码核心：`getBoundingClientRect()` + `Math.atan2()` 计算眼睛角度
- 6 种交互状态：空闲、打字、密码聚焦、显示密码、登录错误、随机眨眼
- 角色使用 CSS transitions 做身体倾斜，JS 直接操作 DOM style 保证 60fps

### Metis Review
**Identified Gaps** (addressed):
- 管理端布局重构范围：确认需要从居中卡片改为左右两栏
- 密码切换机制：改用 `:type` 绑定 + 自定义切换按钮，以便监听密码可见性
- 暗黑模式适配：管理端左面板需要深色渐变背景
- 响应式设计：≤768px 隐藏角色，恢复简洁布局
- 性能：使用 requestAnimationFrame 节流 mousemove
- 内存泄漏：组件销毁时清理所有定时器

---

## Work Objectives

### Core Objective
在两个前端登录页面添加 4 个交互动画角色，角色眼睛跟随鼠标、密码输入时回避、登录失败时摇头。

### Concrete Deliverables
- `frontend-student/src/views/login/AnimatedCharacters.vue` — 学生端角色组件（Vue 2）
- `frontend-student/src/views/login/index.vue` — 修改：VisionOS 风格左右两栏布局 + 集成角色
- `frontend-admin/src/views/login/AnimatedCharacters.vue` — 管理端角色组件（Vue 3）
- `frontend-admin/src/views/login/index.vue` — 修改：VisionOS 风格左右两栏布局 + 集成角色

### Definition of Done
- [ ] 两个前端登录页均显示 4 个动画角色
- [ ] 两个前端登录页均为 VisionOS 风格左右两栏布局
- [ ] 角色眼睛跟随鼠标移动
- [ ] 输入密码时角色回避/闭眼
- [ ] 登录失败时角色摇头沮丧
- [ ] 管理端暗黑模式下角色正常显示
- [ ] 移动端（≤768px）隐藏角色
- [ ] 两个前端构建成功无错误

### Must Have
- 4 个角色外观与参考实现完全一致（颜色、尺寸、形状）
- 6 种交互状态完整实现
- 角色动画流畅（60fps）
- 组件销毁时清理所有定时器
- 两个前端统一的 VisionOS Spatial UI 风格布局
- 左面板使用 VisionOS CSS 变量（glass tokens）

### Must NOT Have (Guardrails)
- 不修改登录逻辑（handleLogin、store 操作、路由跳转）
- 不引入新的 npm 依赖
- 不在不同前端间共享角色代码
- 不添加参考中不存在的交互状态（庆祝、挥手等）
- 不添加音效
- 不过度美化角色（严格复制参考外观）
- 不"顺便优化"现有 UI

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: YES（两个前端均有 ESLint）
- **Automated tests**: None（纯视觉/交互效果，不适合单元测试）
- **Framework**: N/A

### QA Policy
每个任务包含 agent-executed QA 场景。
Evidence 保存到 `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`。

- **Frontend/UI**: 使用 Playwright 截图 + DOM 检查
- **构建验证**: 使用 Bash 运行 build 命令

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — 独立任务):
├── Task 1: 学生端登录页动画角色 [visual-engineering]
└── Task 2: 管理端登录页布局重构 + 动画角色 [visual-engineering]

Wave 2 (After Wave 1 — 集成验证):
└── Task 3: 跨平台联合验证 [unspecified-high]

Wave FINAL (After ALL tasks):
├── F1: Plan compliance audit (oracle)
├── F2: Code quality review (unspecified-high)
├── F3: Real QA execution (unspecified-high)
└── F4: Scope fidelity check (deep)
→ Present results → Get explicit user okay
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|-----------|--------|
| 1 (学生端) | None | 3 |
| 2 (管理端) | None | 3 |
| 3 (联合验证) | 1, 2 | F1-F4 |

### Agent Dispatch Summary

- **Wave 1**: 2 tasks — T1 → `visual-engineering`, T2 → `visual-engineering`
- **Wave 2**: 1 task — T3 → `unspecified-high`
- **FINAL**: 4 tasks — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [ ] 1. 学生端登录页 VisionOS 风格重构 + 动画角色

  **What to do**:
  - 创建 `frontend-student/src/views/login/AnimatedCharacters.vue`（Vue 2 Options API 组件）
    - 4 个 CSS 角色：紫色矩形(170x370, #6c3ff5)、黑色矩形(115x290, #2d2d2d)、橙色半圆(230x190, #ff9b6b)、黄色圆角矩形(135x215, #e8d754)
    - 角色位置使用 absolute 定位，场景容器 480x360px
    - 眼睛：紫色和黑色角色有白色眼球+瞳孔，橙色和黄色角色只有裸瞳孔
    - 黄色角色有嘴巴（4px 高横线）
  - 实现 6 种交互状态（JS 直接操作 DOM style，不走 Vue 响应式）：
    - 空闲：`mousemove` → `calcPosition()` 计算身体倾斜(skewX ±6deg)，`calcPupilOffset()` 计算瞳孔偏移(atan2)
    - 打字（用户名聚焦）：角色互相对视，身体向表单方向倾斜
    - 密码聚焦（密码隐藏时）：所有角色转头回避，眼睛看向左上方
    - 显示密码：紫色角色周期性偷看（2-5秒间隔，持续800ms）
    - 登录失败：沮丧表情（眼睛向左下方）+ 橙色显示悲伤嘴巴 + shakeHead 动画(0.8s) + 2.5s后恢复
    - 随机眨眼：紫色和黑色角色每3-7秒眨眼150ms
  - 使用 `requestAnimationFrame` 节流 mousemove 处理器
  - `mounted` 中初始化动画，`beforeDestroy` 中清理所有定时器（blink、error、typing、peek）
  - 通过 `$emit('password-visible', bool)` 向父组件报告密码可见状态
  - 通过 `$emit('trigger-error')` 接收父组件的错误触发信号
  - 密码框使用 `:type="pwdFieldType"` + 自定义切换图标替代 `show-password`，以便完全控制密码可见性
  - 响应式：≤768px 隐藏角色场景
  - **重构 `frontend-student/src/views/login/index.vue` 布局为 VisionOS 风格左右两栏**：
    - 删除当前的蓝色渐变左面板（`.brand-panel`）和功能列表
    - 改为 VisionOS Spatial UI 风格的左右两栏布局（grid 或 flex）
    - **左面板**：居中显示品牌 logo + 名称 + 动画角色场景
      - 背景：使用 `var(--brand-bg)` 浅色渐变（与管理端一致）
      - 品牌 logo + 名称居中显示
      - 角色场景在品牌信息下方
      - 使用 `backdrop-filter: blur()` 玻璃效果
    - **右面板**：登录表单
      - 背景：白色/浅色，与管理端右面板风格一致
      - 保留现有表单验证和登录逻辑
    - 添加 `@focus.native` / `@blur.native` 事件监听用户名和密码输入框焦点
    - 密码框改为 `:type="pwdFieldType"` 绑定，添加自定义切换按钮
    - 在 `handleLogin` 的错误分支中通过 ref 触发角色错误动画
    - 监听 `password-visible` 事件更新密码可见状态
    - 响应式：≤768px 隐藏左面板，右面板全宽居中

  **Must NOT do**:
  - 不修改 `loginApi.login` 调用逻辑
  - 不修改 Vuex store
  - 不修改路由配置
  - 不引入新的 npm 依赖
  - 不添加参考中不存在的交互状态
  - 不保留旧的蓝色渐变左面板样式
  - 不添加功能列表（角色取代功能列表的位置）

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 纯前端视觉/交互动画任务，需要精确的 CSS 和 DOM 操作
  - **Skills**: [`frontend-design`]
    - `frontend-design`: 需要精确复刻参考实现的视觉效果和交互行为
  - **Skills Evaluated but Omitted**:
    - `ui-ux-pro-max`: 更偏向设计决策而非实现

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 2)
  - **Blocks**: Task 3
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `frontend-student/src/views/login/index.vue` — 当前登录页完整代码，需理解现有布局和逻辑
  - `frontend-student/src/styles/index.scss` — 全局样式变量（--brand-accent, --brand-dark 等 VisionOS CSS 变量定义）
  - `frontend-admin/src/styles/base/_variables.scss` — 管理端 VisionOS 变量定义（可参考玻璃材质参数）
  - `frontend-admin/src/views/login/index.vue` — 管理端当前登录页（可参考其 glass-morphism 效果）

  **API/Type References**:
  - `frontend-student/src/api/login.js` — 登录 API 调用方式

  **External References**:
  - https://github.com/guohaolian/animatedlogin/blob/main/index.html — 参考实现完整源码（HTML+CSS+JS），角色尺寸/颜色/动画参数全部来自此文件
  - 参考实现的核心函数：`calcPosition()`、`calcPupilOffset()`、`updateCharacters()`、`triggerLoginError()`

  **WHY Each Reference Matters**:
  - `index.vue`: 必须理解现有登录逻辑（handleLogin、表单验证、路由跳转）才能安全集成角色，同时理解需要删除的旧样式
  - `index.scss`: 学生端的 VisionOS CSS 变量定义，左面板背景色需要与这些变量协调
  - `_variables.scss`: 管理端的 VisionOS 变量定义（glass tokens），学生端需要保持一致的玻璃效果参数
  - `login.js`: 理解 API 调用方式，确保错误触发信号正确传递
  - 参考实现：所有 CSS 参数（角色尺寸、颜色、动画时长）和 JS 逻辑（状态机、数学计算）的唯一来源

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: VisionOS 布局验证
    Tool: Playwright
    Preconditions: 学生端开发服务器运行在 localhost:8001
    Steps:
      1. 导航到 http://localhost:8001/login
      2. 等待页面加载完成（networkidle）
      3. 检查页面是否为左右两栏布局
      4. 检查左面板是否存在且包含品牌信息和角色
      5. 检查左面板背景是否使用 VisionOS 浅色渐变
      6. 检查右面板是否包含登录表单
      7. 截图保存
    Expected Result: VisionOS 风格左右两栏布局，左面板有品牌+角色，右面板有表单
    Failure Indicators: 仍为旧的蓝色渐变布局、角色不存在
    Evidence: .sisyphus/evidence/task-1-visionos-layout.png

  Scenario: 角色渲染验证
    Tool: Playwright
    Preconditions: 学生端登录页已加载
    Steps:
      1. 检查 .characters-scene 或等效容器是否存在
      2. 检查 4 个角色元素（.char-purple, .char-black, .char-orange, .char-yellow）是否 visible
      3. 截图保存
    Expected Result: 4 个角色均可见，颜色和形状与参考一致
    Failure Indicators: 角色元素不存在、颜色错误、形状不正确
    Evidence: .sisyphus/evidence/task-1-characters-render.png

  Scenario: 鼠标跟随验证
    Tool: Playwright
    Preconditions: 登录页已加载
    Steps:
      1. 将鼠标移动到页面右侧（表单区域）
      2. 等待 500ms
      3. 检查角色瞳孔元素的 transform 属性是否包含 translate
      4. 将鼠标移动到页面左侧
      5. 等待 500ms
      6. 检查瞳孔 transform 是否改变方向
    Expected Result: 瞳孔跟随鼠标方向移动，身体有轻微 skewX 倾斜
    Failure Indicators: 瞳孔不动、身体不倾斜
    Evidence: .sisyphus/evidence/task-1-mouse-tracking.png

  Scenario: 密码聚焦回避验证
    Tool: Playwright
    Preconditions: 登录页已加载
    Steps:
      1. 点击密码输入框
      2. 等待 700ms（CSS transition 时间）
      3. 检查角色身体的 transform 是否包含 skewX 负值（向左倾斜）
      4. 检查瞳孔 transform 是否为向左上方偏移
      5. 点击用户名输入框（密码失焦）
      6. 等待 700ms
      7. 检查角色是否恢复中立姿势
    Expected Result: 密码聚焦时角色回避，失焦后恢复
    Failure Indicators: 角色不回避、恢复后位置不正确
    Evidence: .sisyphus/evidence/task-1-password-focus.png

  Scenario: 登录错误摇头验证
    Tool: Playwright
    Preconditions: 登录页已加载
    Steps:
      1. 不填写任何字段，直接点击"登录"按钮
      2. 等待 400ms
      3. 检查角色是否处于沮丧表情（瞳孔向左下方偏移）
      4. 检查橙色角色的悲伤嘴巴是否可见
      5. 等待 400ms
      6. 检查 shakeHead 动画是否正在执行
      7. 等待 3000ms
      8. 检查角色是否恢复空闲状态
    Expected Result: 校验失败触发摇头动画，2.5s 后恢复
    Failure Indicators: 不触发摇头、不恢复、橙色嘴巴不显示
    Evidence: .sisyphus/evidence/task-1-login-error.png

  Scenario: 构建验证
    Tool: Bash
    Preconditions: 无
    Steps:
      1. cd frontend-student && npm run build
      2. 检查退出码和输出
    Expected Result: 构建成功，无错误
    Failure Indicators: 构建失败、TypeScript/ESLint 错误
    Evidence: .sisyphus/evidence/task-1-build.txt
  ```

  **Commit**: YES
  - Message: `feat(student-login): add animated characters with eye tracking`
  - Files: `frontend-student/src/views/login/AnimatedCharacters.vue`, `frontend-student/src/views/login/index.vue`
  - Pre-commit: `cd frontend-student && npm run build`

---

- [ ] 2. 管理端登录页布局重构 + 动画角色

  **What to do**:
  - 创建 `frontend-admin/src/views/login/AnimatedCharacters.vue`（Vue 3 Composition API + `<script setup lang="ts">` 组件）
    - 与 Task 1 相同的 4 个 CSS 角色和 6 种交互状态
    - 使用 `onMounted` 初始化，`onUnmounted` 清理定时器
    - TypeScript 类型定义
    - 通过 `defineEmits` 声明 `password-visible` 和 `trigger-error` 事件
  - 重构 `frontend-admin/src/views/login/index.vue` 布局：
    - 从居中卡片改为左右两栏（grid 或 flex）
    - 左面板：品牌信息（logo + 名称）+ 动画角色场景
    - 右面板：登录表单（保留现有表单验证和登录逻辑）
    - 左面板背景：使用 VisionOS 浅色渐变 `var(--brand-bg)` + 玻璃效果
    - 暗黑模式：自动跟随 `html.dark` 的 CSS 变量切换（--brand-bg 变为深色）
    - 响应式：≤768px 隐藏左面板，右面板居中显示
  - 密码框改为 `:type="pwdFieldType"` 绑定 + 自定义切换按钮
  - 添加 `@focus` / `@blur` 事件监听输入框焦点
  - 在 `handleLogin` 的校验失败分支和 catch 分支中触发角色错误动画
  - 保留管理端的暗黑模式支持（`html.dark` class）

  **Must NOT do**:
  - 不修改 `userStore.login` 调用逻辑
  - 不修改路由配置、Pinia store
  - 不引入新的 npm 依赖
  - 不修改其他页面或组件
  - 不"顺便优化"现有 UI 风格

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 布局重构 + 视觉动画任务，需要精确的 CSS 布局和交互动画
  - **Skills**: [`frontend-design`]
    - `frontend-design`: 需要精确复刻参考实现并适配暗黑模式
  - **Skills Evaluated but Omitted**:
    - `ui-ux-pro-max`: 更偏向设计决策而非实现

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 1)
  - **Blocks**: Task 3
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `frontend-admin/src/views/login/index.vue` — 当前登录页完整代码（314行），需理解现有布局、暗黑模式、表单验证逻辑
  - `frontend-admin/src/styles/index.scss` — 全局样式入口
  - `frontend-admin/src/styles/base/variables` — CSS 变量定义
  - `frontend-admin/src/styles/base/theme` — 暗黑模式主题变量
  - `frontend-admin/vite.config.ts` — Vite 配置（路径别名 @）

  **API/Type References**:
  - `frontend-admin/src/stores/user.ts` — 用户 store（login 方法签名）
  - `frontend-admin/src/api/auth/index.ts` — 认证 API 类型定义

  **External References**:
  - https://github.com/guohaolian/animatedlogin/blob/main/index.html — 参考实现，角色参数的唯一来源
  - 参考实现的 CSS 变量和布局参数

  **WHY Each Reference Matters**:
  - `index.vue`: 必须理解现有的 glass-morphism 效果、暗黑模式 `html.dark` 规则、表单验证规则
  - `_variables.scss`: VisionOS CSS 变量定义（glass tokens），左面板背景直接使用这些变量
  - `_theme.scss`: 暗黑模式下 CSS 变量的变化，左面板背景自动跟随切换
  - `user.ts`: 理解 login 方法的参数和返回值，确保错误触发正确
  - 参考实现：角色尺寸/颜色/动画参数的唯一权威来源

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: VisionOS 布局验证
    Tool: Playwright
    Preconditions: 管理端开发服务器运行在 localhost:3000
    Steps:
      1. 导航到 http://localhost:3000/login
      2. 等待页面加载完成
      3. 检查页面是否为左右两栏布局
      4. 检查左面板是否存在且包含品牌信息和角色
      5. 检查左面板背景是否使用 VisionOS 浅色渐变（var(--brand-bg)）
      6. 检查右面板是否包含登录表单
      7. 截图保存
    Expected Result: VisionOS 风格左右两栏布局，左面板有品牌+角色，右面板有表单
    Failure Indicators: 仍为居中卡片布局、角色不存在
    Evidence: .sisyphus/evidence/task-2-visionos-layout.png

  Scenario: 暗黑模式适配验证
    Tool: Playwright
    Preconditions: 管理端登录页已加载
    Steps:
      1. 添加 `dark` class 到 html 元素
      2. 等待 500ms
      3. 检查左面板背景是否跟随 VisionOS 暗黑变量切换为深色
      4. 检查角色颜色是否仍然清晰可见
      5. 检查白色眼珠在深色背景上的对比度
      6. 截图保存
    Expected Result: 暗黑模式下左面板深色背景，角色清晰可见
    Failure Indicators: 背景不变化、角色不可见、对比度不足
    Evidence: .sisyphus/evidence/task-2-dark-mode.png

  Scenario: 角色交互验证（与 Task 1 相同的 6 种状态）
    Tool: Playwright
    Preconditions: 管理端登录页已加载
    Steps:
      1. 鼠标移动 → 检查瞳孔跟随
      2. 点击用户名 → 检查角色对视
      3. 点击密码（隐藏状态） → 检查角色回避
      4. 点击密码切换 → 检查偷看行为
      5. 空表单提交 → 检查摇头动画
    Expected Result: 所有 6 种交互状态正常工作
    Failure Indicators: 任一状态不触发或行为异常
    Evidence: .sisyphus/evidence/task-2-interactions.png

  Scenario: 响应式验证
    Tool: Playwright
    Preconditions: 管理端登录页已加载
    Steps:
      1. 设置视口宽度为 768px
      2. 刷新页面
      3. 检查左面板是否隐藏
      4. 检查右面板是否居中显示
      5. 检查登录表单是否正常可用
    Expected Result: 移动端隐藏角色，显示简洁登录表单
    Failure Indicators: 左面板仍显示、布局错乱
    Evidence: .sisyphus/evidence/task-2-responsive.png

  Scenario: 构建验证
    Tool: Bash
    Preconditions: 无
    Steps:
      1. cd frontend-admin && pnpm run build
      2. 检查退出码和输出
    Expected Result: 构建成功，无 TypeScript/ESLint 错误
    Failure Indicators: 构建失败
    Evidence: .sisyphus/evidence/task-2-build.txt
  ```

  **Commit**: YES
  - Message: `feat(admin-login): restructure layout and add animated characters`
  - Files: `frontend-admin/src/views/login/AnimatedCharacters.vue`, `frontend-admin/src/views/login/index.vue`
  - Pre-commit: `cd frontend-admin && pnpm run build`

---

- [ ] 3. 跨平台联合验证

  **What to do**:
  - 同时启动两个前端开发服务器
  - 验证两个登录页的 VisionOS 布局风格一致（左右两栏、玻璃效果）
  - 验证两个登录页的角色外观完全一致（颜色、尺寸、形状）
  - 验证两个登录页的交互行为完全一致（6 种状态）
  - 检查两个前端的构建输出
  - 截图对比两个登录页

  **Must NOT do**:
  - 不修改任何代码
  - 不引入新的依赖

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: 跨平台验证任务，需要同时操作两个前端
  - **Skills**: [`playwright`]
    - `playwright`: 需要浏览器自动化来截图和验证 DOM

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (sequential)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 1, Task 2

  **References**:

  **Pattern References**:
  - Task 1 的 `AnimatedCharacters.vue` — 学生端角色实现
  - Task 2 的 `AnimatedCharacters.vue` — 管理端角色实现
  - 两个登录页的 `index.vue` — 布局集成

  **WHY Each Reference Matters**:
  - 需要对比两个实现确保一致性

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: 跨平台一致性验证
    Tool: Playwright
    Preconditions: 两个前端开发服务器均已启动
    Steps:
      1. 打开管理端登录页 http://localhost:3000/login
      2. 截图保存为 task-3-admin-login.png
      3. 打开学生端登录页 http://localhost:8001/login
      4. 截图保存为 task-3-student-login.png
      5. 对比两个截图中的布局风格（VisionOS 两栏）和角色外观
    Expected Result: 两个登录页的 VisionOS 布局风格和角色外观完全一致
    Failure Indicators: 布局风格不一致、角色外观不一致
    Evidence: .sisyphus/evidence/task-3-admin-login.png, .sisyphus/evidence/task-3-student-login.png

  Scenario: 双平台构建验证
    Tool: Bash
    Preconditions: 无
    Steps:
      1. cd frontend-admin && pnpm run build
      2. cd frontend-student && npm run build
    Expected Result: 两个前端均构建成功
    Failure Indicators: 任一构建失败
    Evidence: .sisyphus/evidence/task-3-build-admin.txt, .sisyphus/evidence/task-3-build-student.txt
  ```

  **Commit**: NO

---

## Final Verification Wave

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, check DOM). For each "Must NOT Have": search codebase for forbidden patterns. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `pnpm run lint` (admin) and `npm run lint` (student). Review all changed files for: `as any`/`@ts-ignore`, empty catches, console.log in prod, commented-out code, unused imports. Check AI slop: excessive comments, over-abstraction.
  Output: `Lint [PASS/FAIL] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Real QA Execution** — `unspecified-high` (+ `playwright` skill)
  Start both frontends. Open login pages in browser. Execute EVERY QA scenario from EVERY task — follow exact steps, capture screenshots. Test cross-task integration. Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff. Verify 1:1 — everything in spec was built, nothing beyond spec was built. Check "Must NOT do" compliance. Flag unaccounted changes.
  Output: `Tasks [N/N compliant] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

- **Task 1**: `feat(student-login): add animated characters with eye tracking` — `frontend-student/src/views/login/`
- **Task 2**: `feat(admin-login): restructure layout and add animated characters` — `frontend-admin/src/views/login/`
- **Task 3**: No commit (verification only)

---

## Success Criteria

### Verification Commands
```bash
cd frontend-admin && pnpm run build  # Expected: build success
cd frontend-student && npm run build  # Expected: build success
```

### Final Checklist
- [ ] 4 个角色在两个登录页均可见
- [ ] 两个登录页均为 VisionOS 风格左右两栏布局
- [ ] 眼睛跟随鼠标移动
- [ ] 密码输入时角色回避
- [ ] 登录失败时摇头
- [ ] 暗黑模式正常（管理端）
- [ ] 移动端隐藏角色
- [ ] 无构建错误
- [ ] 无内存泄漏（定时器清理）
