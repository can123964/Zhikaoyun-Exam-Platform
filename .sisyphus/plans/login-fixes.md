# 登录/注册页面修复计划

## TL;DR

> **Quick Summary**: 修复两个前端登录/注册页面的 4 类问题：注册页旧样式、登录页缺少玻璃质感、角色动画不流畅、面板比例不一致（管理端改为与学生端一致的左 56.5%/右 43.5%）。
> 
> **Deliverables**:
> - 学生端注册页升级为 VisionOS 玻璃风格
> - 学生端登录页添加卡片容器 + 玻璃输入框
> - 两个前端角色动画流畅度修复（P0/P1 bug）
> - 管理端面板比例改为与学生端一致（左 56.5%/右 43.5%）
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Task 1 (动画修复) → Task 2 (面板比例统一) → Task 3 (玻璃效果) → Task 4 (注册页) → Task 5 (验证)

---

## Context

### 原始问题
用户反馈 4 个严重问题：
1. 学生端注册页面没有修改，还是原来的老旧样式
2. 学生端登录注册页面不是卡片样式，没有 glass 质感，割裂感极其严重
3. 登录页小人动画没有完全复刻，不流畅，输入密码时偶尔不会转头，账号密码框来回切换几乎没反应
4. 两个前端左右侧占比不同，需要统一

### 深度排查发现

**问题 1：注册页仍使用旧版蓝色渐变**
- `frontend-student/src/views/register/index.vue` 使用 `linear-gradient(135deg, #3d5a80, #5b7fa5, #7a9bbd)` 纯蓝渐变
- 缺少：glass backdrop-filter、环境光晕装饰、卡片样式、前缀图标、自定义密码切换
- 输入框高度 42px（登录页 46px），间距偏小

**问题 2：学生端登录页缺少玻璃质感**
- 右侧面板有 `backdrop-filter: blur(40px)` 但输入框使用实心背景 `var(--brand-bg)`（渐变不透明）
- 表单容器 `.form-container` 无圆角、无阴影、无边框、无 glass 背景
- 玻璃 CSS 变量（`--glass-bg`, `--glass-shadow` 等）已在 `index.scss` 定义但未使用
- 每个输入框像实心贴片压在玻璃面板上，破坏整体玻璃感

**问题 3：角色动画不流畅（6 个 bug）**
- **P0**：`.eyes` 的 `transition: all 0.7s ease-in-out` 导致鼠标追踪滞后 700ms
- **P0**：`.bare-pupil` 的 `transition: transform 0.7s ease-in-out` 导致橙/黄色角色瞳孔反应极慢（紫色/黑色是 0.1s）
- **P1**：`setHasPassword()` 未调用 `updateCharacters()`，密码输入时角色不更新
- **P1**：`triggerError()` 清除 `isPasswordFocused` 但 DOM 焦点未同步
- **P2**：管理端 RAF 条件与外部不一致（内部多检查 `isLookingAtEachOther`）

**问题 4：面板比例不一致**
- 学生端：CSS Flex `flex: 1.3` vs `flex: 1`（56.5%/43.5%）✓ 保留此比例
- 管理端：CSS Grid `grid-template-columns: 1fr 1fr`（50%/50%）✗ 需要修改为与学生端一致

---

## Work Objectives

### Core Objective
修复所有 4 类问题，使两个前端的登录/注册页面达到统一的 VisionOS 玻璃风格，角色动画流畅无卡顿。

### Concrete Deliverables
- `frontend-student/src/views/login/AnimatedCharacters.vue` — 修复动画 bug（两个前端）
- `frontend-admin/src/views/login/AnimatedCharacters.vue` — 修复动画 bug
- `frontend-student/src/views/login/index.vue` — 面板比例 + 玻璃卡片样式
- `frontend-admin/src/views/login/index.vue` — 玻璃卡片样式微调
- `frontend-student/src/views/register/index.vue` — 升级为 VisionOS 玻璃风格

### Definition of Done
- [ ] 两个前端面板比例统一（左 56.5% / 右 43.5%）
- [ ] 角色动画流畅，鼠标追踪无延迟
- [ ] 密码输入时角色正确反应
- [ ] 账号/密码框切换时角色有明显反应
- [ ] 学生端登录页有卡片容器 + 玻璃输入框
- [ ] 学生端注册页使用 VisionOS 玻璃风格
- [ ] 两个前端构建成功无错误

### Must Have
- 鼠标追踪时眼睛 transition 为 0 或极短（0.05s）
- `.bare-pupil` transition 为 0.1s（与 `.pupil` 一致）
- `setHasPassword` 调用 `updateCharacters()`
- 表单容器有 border-radius + backdrop-filter + glass 背景
- 输入框背景改为半透明（非实心渐变）
- 管理端面板比例改为与学生端一致（左 56.5% / 右 43.5%）

### Must NOT Have (Guardrails)
- 不修改登录/注册的业务逻辑（API 调用、路由跳转、store 操作）
- 不引入新的 npm 依赖
- 不修改后端代码
- 不"顺便优化"其他页面
- 不改变角色外观（颜色、尺寸、形状）

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES（ESLint）
- **Automated tests**: None（纯视觉/交互效果）
- **Framework**: N/A

### QA Policy
每个任务包含 agent-executed QA 场景。
Evidence 保存到 `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`。

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — 动画 bug 修复):
├── Task 1a: 学生端角色动画修复 [quick]
└── Task 1b: 管理端角色动画修复 [quick]

Wave 2 (After Wave 1 — 布局 + 玻璃效果):
├── Task 2: 学生端面板比例统一 + 玻璃卡片样式 [visual-engineering]
└── Task 3: 管理端玻璃卡片样式微调 [quick]

Wave 3 (After Wave 2 — 注册页):
└── Task 4: 学生端注册页升级为 VisionOS 风格 [visual-engineering]

Wave FINAL (After ALL tasks):
├── F1: Plan compliance audit (oracle)
├── F2: Code quality review (unspecified-high)
├── F3: Real QA execution (unspecified-high + playwright)
└── F4: Scope fidelity check (deep)
→ Present results → Get explicit user okay
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|-----------|--------|
| 1a (学生端动画) | None | 5 |
| 1b (管理端动画) | None | 5 |
| 2 (学生端面板+玻璃) | 1a | 4, 5 |
| 3 (管理端玻璃微调) | 1b | 5 |
| 4 (注册页) | 2 | 5 |
| 5 (联合验证) | 1a, 1b, 2, 3, 4 | F1-F4 |

### Agent Dispatch Summary

- **Wave 1**: 2 tasks — T1a → `quick`, T1b → `quick`
- **Wave 2**: 2 tasks — T2 → `visual-engineering`, T3 → `quick`
- **Wave 3**: 1 task — T4 → `visual-engineering`
- **FINAL**: 4 tasks — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

- [ ] 1a. 学生端角色动画流畅度修复

  **What to do**:
  - 修改 `frontend-student/src/views/login/AnimatedCharacters.vue`
  - **修复 P0 Bug（鼠标追踪滞后）**：
    - 在 `updateCharacters()` 中，鼠标追踪分支（空闲状态）将 `.eyes` 的 `transition` 设为 `'none'`
    - 状态切换分支（error/lookingAway/showingPwd/lookingAtEachOther）恢复 `transition = ''`
    - 具体：遍历所有 `.eyes` 元素，根据当前状态动态设置 transition
  - **修复 P0 Bug（橙/黄瞳孔慢）**：
    - 将 `.bare-pupil` 的 CSS `transition` 从 `0.7s ease-in-out` 改为 `0.1s ease-out`
  - **修复 P1 Bug（密码输入无反应）**：
    - 在 `setHasPassword(val)` 方法末尾添加 `this.updateCharacters()`
  - **修复 P1 Bug（焦点不同步）**：
    - 在 `triggerError()` 中，不清除 `isPasswordFocused`（让 DOM 焦点事件自然管理）
    - 或在错误恢复定时器中检查 `document.activeElement` 同步状态

  **Must NOT do**:
  - 不修改角色外观
  - 不修改业务逻辑
  - 不引入新依赖

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 单文件修改，bug 修复明确
  - **Skills**: []
    - 无需特殊技能

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 1b)
  - **Blocks**: Task 2, Task 5
  - **Blocked By**: None

  **References**:

  **Pattern References**:
  - `frontend-student/src/views/login/AnimatedCharacters.vue` — 当前实现，需要修改的文件
  - 参考实现 `https://github.com/guohaolian/animatedlogin/blob/main/index.html` — 角色交互的唯一标准

  **WHY Each Reference Matters**:
  - AnimatedCharacters.vue：需要理解当前的 `updateCharacters()` 函数结构，找到正确的修改点
  - 参考实现：确认角色交互的预期行为（transition 应该在哪里禁用/恢复）

  **Acceptance Criteria**:

  ```
  Scenario: 鼠标追踪流畅性验证
    Tool: Bash (npm run build)
    Preconditions: 修改完成
    Steps:
      1. cd frontend-student && npm run build
      2. 检查退出码
    Expected Result: 构建成功，无错误
    Evidence: .sisyphus/evidence/task-1a-build.txt
  ```

  **Commit**: YES
  - Message: `fix(student-characters): improve eye tracking smoothness and password focus reactivity`
  - Files: `frontend-student/src/views/login/AnimatedCharacters.vue`

---

- [ ] 1b. 管理端角色动画流畅度修复

  **What to do**:
  - 修改 `frontend-admin/src/views/login/AnimatedCharacters.vue`
  - 修复与 Task 1a 完全相同的 3 个 bug（P0 鼠标追踪、P0 瞳孔延迟、P1 密码输入）
  - **额外修复 P2 Bug（RAF 条件不一致）**：
    - 将 RAF 逻辑改为学生端的"先条件判断再调度 + cancel 旧帧"模式
    - 移除 RAF 回调内部的 `isLookingAtEachOther` 检查（外部已判断）

  **Must NOT do**:
  - 不修改角色外观
  - 不修改业务逻辑
  - 不引入新依赖

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 单文件修改，与 Task 1a 类似
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 1a)
  - **Blocks**: Task 3, Task 5
  - **Blocked By**: None

  **References**:
  - `frontend-admin/src/views/login/AnimatedCharacters.vue` — 需要修改的文件
  - `frontend-student/src/views/login/AnimatedCharacters.vue` — Task 1a 完成后的版本，作为 RAF 修复参考

  **WHY Each Reference Matters**:
  - 管理端 AnimatedCharacters.vue：需要理解当前实现并应用相同的修复
  - 学生端版本：Task 1a 完成后可以作为 RAF 修复的参考实现

  **Acceptance Criteria**:

  ```
  Scenario: 构建验证
    Tool: Bash (pnpm run build)
    Preconditions: 修改完成
    Steps:
      1. cd frontend-admin && pnpm run build
      2. 检查退出码
    Expected Result: 构建成功，无 TypeScript/ESLint 错误
    Evidence: .sisyphus/evidence/task-1b-build.txt
  ```

  **Commit**: YES
  - Message: `fix(admin-characters): improve eye tracking smoothness and RAF efficiency`
  - Files: `frontend-admin/src/views/login/AnimatedCharacters.vue`

---

- [ ] 2. 学生端玻璃卡片样式

  **What to do**:
  - 修改 `frontend-student/src/views/login/index.vue`
  - **玻璃卡片样式**：
    - 给 `.form-container` 添加 glass 样式，**使用现有玻璃透明度值**（不修改透明度参数）
    - 参考管理端已有的 glass 效果：
      ```scss
      background: rgba(255, 255, 255, 0.55);
      backdrop-filter: blur(40px) saturate(200%);
      -webkit-backdrop-filter: blur(40px) saturate(200%);
      border-radius: 24px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid rgba(255, 255, 255, 0.6);
      padding: 40px;
      ```
    - 将 `.form-container` 的 `max-width` 从 360px 改为 400px（匹配管理端）
  - **输入框玻璃化**：
    - 将 `.field >>> .el-input__inner` 的 `background: var(--brand-bg)` 改为 `background: rgba(255, 255, 255, 0.15)`
    - 添加 `backdrop-filter: blur(20px) saturate(150%)`
    - 将 `border-radius` 从 10px 改为 14px
    - 将 `border` 从 `1.5px solid var(--brand-border)` 改为 `1px solid rgba(255, 255, 255, 0.35)`
  - **登录按钮统一**：
    - 将 `border-radius` 从 10px 改为 14px
    - 将按钮 hover shadow 改为 `rgba(0, 122, 255, 0.3)`（Apple 蓝）
  - **聚焦阴影统一**：
    - 将输入框 focus shadow 改为 `rgba(0, 122, 255, 0.1)`（Apple 蓝）
  - **保留现有面板比例**：学生端的 `flex: 1.3` vs `flex: 1` 比例保持不变

  **Must NOT do**:
  - 不修改登录业务逻辑
  - 不修改 Vuex store
  - 不修改路由配置

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 涉及布局重构和视觉样式调整
  - **Skills**: [`frontend-design`]
    - `frontend-design`: 需要精确的 CSS 布局和视觉调整

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Task 3)
  - **Blocks**: Task 4, Task 5
  - **Blocked By**: Task 1a

  **References**:
  - `frontend-student/src/views/login/index.vue` — 需要修改的文件
  - `frontend-admin/src/views/login/index.vue` — 参考实现（已有的正确布局和玻璃效果）
  - `frontend-student/src/styles/index.scss` — 玻璃 CSS 变量定义

  **WHY Each Reference Matters**:
  - 学生端 login/index.vue：需要理解当前的 flex 布局和样式
  - 管理端 login/index.vue：提供正确的 grid 布局和玻璃效果参考
  - index.scss：提供 `--glass-*` 变量定义，确保使用正确的变量

  **Acceptance Criteria**:

  ```
  Scenario: 玻璃卡片验证
    Tool: Playwright
    Preconditions: 学生端开发服务器运行在 localhost:8001
    Steps:
      1. 打开 http://localhost:8001/login
      2. 检查 `.form-container` 是否有 backdrop-filter 属性
      3. 检查输入框背景是否为半透明（非实心渐变）
      4. 截图保存
    Expected Result: 表单容器有玻璃效果，输入框背景半透明
    Evidence: .sisyphus/evidence/task-2-glass.png

  Scenario: 构建验证
    Tool: Bash (npm run build)
    Steps:
      1. cd frontend-student && npm run build
    Expected Result: 构建成功
    Evidence: .sisyphus/evidence/task-2-build.txt
  ```

  **Commit**: YES
  - Message: `feat(student-login): unify panel proportions and add glass card styling`
  - Files: `frontend-student/src/views/login/index.vue`

---

- [ ] 3. 管理端面板比例统一 + 玻璃卡片样式微调

  **What to do**:
  - 修改 `frontend-admin/src/views/login/index.vue`
  - **面板比例统一（改为与学生端一致）**：
    - 将 `.login-page` 从 `display: grid; grid-template-columns: 1fr 1fr` 改为 `display: flex`
    - 给 `.left-panel` 添加 `flex: 1.3`（占 56.5%）
    - 给 `.right-panel` 添加 `flex: 1`（占 43.5%）
    - 删除 `grid-template-columns` 属性
  - **玻璃卡片容器**：
    - 给 `.form-container` 添加 glass 样式，**使用现有玻璃透明度值**（不修改透明度参数）
    - 参考管理端已有的 glass 效果：
      ```scss
      background: rgba(255, 255, 255, 0.55);
      backdrop-filter: blur(40px) saturate(200%);
      -webkit-backdrop-filter: blur(40px) saturate(200%);
      border-radius: 24px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid rgba(255, 255, 255, 0.6);
      padding: 40px;
      ```
  - **表单背景调整**：
    - 确保右侧面板背景为 `rgba(255, 255, 255, 0.55)` + `backdrop-filter: blur(40px) saturate(200%)`
    - 检查暗黑模式下是否正常

  **Must NOT do**:
  - 不修改登录业务逻辑
  - 不修改 Pinia store
  - 不改变面板比例（已经是 1:1）

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 小范围样式微调
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Task 2)
  - **Blocks**: Task 5
  - **Blocked By**: Task 1b

  **References**:
  - `frontend-admin/src/views/login/index.vue` — 需要修改的文件
  - `frontend-student/src/views/login/index.vue` — Task 2 完成后的版本，作为玻璃样式参考

  **WHY Each Reference Matters**:
  - 管理端 login/index.vue：需要理解当前实现并添加 glass 卡片样式
  - 学生端版本：Task 2 完成后提供统一的 glass 样式参考

  **Acceptance Criteria**:

  ```
  Scenario: 面板比例验证
    Tool: Playwright
    Preconditions: 管理端开发服务器运行在 localhost:3000
    Steps:
      1. 打开 http://localhost:3000/login
      2. 检查页面是否为左右不对称布局（左 56.5% / 右 43.5%）
      3. 截图保存
    Expected Result: 管理端面板比例与学生端一致（左宽右窄）
    Evidence: .sisyphus/evidence/task-3-layout.png

  Scenario: 玻璃卡片验证
    Tool: Playwright
    Steps:
      1. 打开 http://localhost:3000/login
      2. 检查 `.form-container` 是否有 backdrop-filter 属性
      3. 暗黑模式验证：添加 `dark` class 到 html
      4. 检查左面板背景是否跟随切换
    Expected Result: 亮色/暗色模式下玻璃效果均正常
    Evidence: .sisyphus/evidence/task-3-glass.png

  Scenario: 构建验证
    Tool: Bash (pnpm run build)
    Steps:
      1. cd frontend-admin && pnpm run build
    Expected Result: 构建成功
    Evidence: .sisyphus/evidence/task-3-build.txt
  ```

  **Commit**: YES
  - Message: `feat(admin-login): add glass card container to form`
  - Files: `frontend-admin/src/views/login/index.vue`

---

- [ ] 4. 学生端注册页升级为 VisionOS 玻璃风格

  **What to do**:
  - 修改 `frontend-student/src/views/register/index.vue`
  - **左面板升级**：
    - 将背景从 `linear-gradient(135deg, #3d5a80, #5b7fa5, #7a9bbd)` 改为 `var(--brand-bg)`
    - 添加 `::before` 和 `::after` 环境光晕模糊装饰（与登录页一致）
    - 删除旧的 `.deco-circle` 装饰
  - **右面板升级**：
    - 添加 glass 背景：`background: rgba(255, 255, 255, 0.55)` + `backdrop-filter: blur(40px) saturate(200%)`
    - 添加玻璃分割线：`border-left: 1px solid rgba(255, 255, 255, 0.6)`
  - **表单容器升级**：
    - 添加 glass 卡片样式（与登录页一致）
  - **输入框升级**：
    - 高度从 42px 改为 46px
    - 间距从 16px 改为 22px
    - 背景改为半透明 glass
    - 添加前缀图标（用户名、密码、姓名、学号、邮箱、手机号）
  - **密码字段**：
    - 用自定义 SVG 眼睛切换按钮替换 Element UI 默认 `show-password`
  - **按钮统一**：
    - border-radius 改为 14px
    - 阴影改为 Apple 蓝
  - **响应式**：
    - ≤768px 隐藏左面板，右面板全宽居中

  **Must NOT do**:
  - 不修改注册业务逻辑（API 调用、表单验证规则）
  - 不修改路由配置
  - 不引入新依赖

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 大范围视觉重构
  - **Skills**: [`frontend-design`]
    - `frontend-design`: 需要精确复刻登录页的 VisionOS 风格

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (sequential)
  - **Blocks**: Task 5
  - **Blocked By**: Task 2

  **References**:
  - `frontend-student/src/views/register/index.vue` — 需要修改的文件
  - `frontend-student/src/views/login/index.vue` — Task 2 完成后的版本，作为 VisionOS 风格参考
  - `frontend-student/src/styles/index.scss` — 玻璃 CSS 变量定义

  **WHY Each Reference Matters**:
  - 注册页：需要理解当前结构和表单字段
  - 登录页：提供完整的 VisionOS 布局和 glass 效果参考
  - index.scss：提供 `--glass-*` 变量定义

  **Acceptance Criteria**:

  ```
  Scenario: 注册页 VisionOS 风格验证
    Tool: Playwright
    Preconditions: 学生端开发服务器运行在 localhost:8001
    Steps:
      1. 打开 http://localhost:8001/register
      2. 检查左面板是否使用 `var(--brand-bg)` 渐变（非蓝色渐变）
      3. 检查右面板是否有 glass 背景
      4. 检查输入框是否有前缀图标
      5. 截图保存
    Expected Result: 注册页与登录页风格完全一致
    Evidence: .sisyphus/evidence/task-4-register.png

  Scenario: 响应式验证
    Tool: Playwright
    Steps:
      1. 设置视口宽度为 768px
      2. 刷新注册页
      3. 检查左面板是否隐藏
    Expected Result: 移动端隐藏左面板，表单全宽居中
    Evidence: .sisyphus/evidence/task-4-responsive.png

  Scenario: 构建验证
    Tool: Bash (npm run build)
    Steps:
      1. cd frontend-student && npm run build
    Expected Result: 构建成功
    Evidence: .sisyphus/evidence/task-4-build.txt
  ```

  **Commit**: YES
  - Message: `feat(student-register): upgrade to VisionOS glass style`
  - Files: `frontend-student/src/views/register/index.vue`

---

- [ ] 5. 跨平台联合验证

  **What to do**:
  - 同时启动两个前端开发服务器
  - 验证两个登录页的面板比例一致（1:1）
  - 验证两个登录页的玻璃效果一致
  - 验证角色动画流畅（鼠标追踪、密码聚焦、字段切换）
  - 验证学生端注册页与登录页风格一致
  - 检查两个前端的构建输出
  - 截图对比

  **Must NOT do**:
  - 不修改任何代码

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: 跨平台验证任务
  - **Skills**: [`playwright`]
    - `playwright`: 需要浏览器自动化来截图和验证

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 4 (sequential)
  - **Blocks**: F1-F4
  - **Blocked By**: Task 1a, 1b, 2, 3, 4

  **References**:
  - 所有修改后的文件
  - 参考实现

  **Acceptance Criteria**:

  ```
  Scenario: 跨平台一致性验证
    Tool: Playwright
    Steps:
      1. 打开管理端登录页 http://localhost:3000/login
      2. 截图保存
      3. 打开学生端登录页 http://localhost:8001/login
      4. 截图保存
      5. 打开学生端注册页 http://localhost:8001/register
      6. 截图保存
      7. 对比三个页面的布局、玻璃效果、面板比例
    Expected Result: 三个页面风格完全一致，面板比例均为 1:1
    Evidence: .sisyphus/evidence/task-5-admin-login.png, task-5-student-login.png, task-5-student-register.png

  Scenario: 角色动画流畅性验证
    Tool: Playwright
    Steps:
      1. 打开学生端登录页
      2. 移动鼠标到页面不同位置
      3. 检查角色眼睛是否跟随鼠标（无延迟）
      4. 点击密码输入框
      5. 检查角色是否转向（回避）
      6. 点击用户名输入框
      7. 检查角色是否转向（对视）
    Expected Result: 所有动画流畅，无卡顿
    Evidence: .sisyphus/evidence/task-5-animation.png

  Scenario: 双平台构建验证
    Tool: Bash
    Steps:
      1. cd frontend-admin && pnpm run build
      2. cd frontend-student && npm run build
    Expected Result: 两个前端均构建成功
    Evidence: .sisyphus/evidence/task-5-build-admin.txt, task-5-build-student.txt
  ```

  **Commit**: NO

---

## Final Verification Wave

> 4 review agents run in PARALLEL. ALL must APPROVE.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists. For each "Must NOT Have": search codebase for forbidden patterns. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run lint commands. Review all changed files for: `as any`, empty catches, console.log in prod, commented-out code, unused imports. Check AI slop.
  Output: `Lint [PASS/FAIL] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Real QA Execution** — `unspecified-high` (+ `playwright` skill)
  Start both frontends. Execute EVERY QA scenario from EVERY task. Test cross-task integration. Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff. Verify 1:1. Check "Must NOT do" compliance. Flag unaccounted changes.
  Output: `Tasks [N/N compliant] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

- **Task 1a**: `fix(student-characters): improve eye tracking smoothness and password focus reactivity` — `frontend-student/src/views/login/AnimatedCharacters.vue`
- **Task 1b**: `fix(admin-characters): improve eye tracking smoothness and RAF efficiency` — `frontend-admin/src/views/login/AnimatedCharacters.vue`
- **Task 2**: `feat(student-login): unify panel proportions and add glass card styling` — `frontend-student/src/views/login/index.vue`
- **Task 3**: `feat(admin-login): add glass card container to form` — `frontend-admin/src/views/login/index.vue`
- **Task 4**: `feat(student-register): upgrade to VisionOS glass style` — `frontend-student/src/views/register/index.vue`
- **Task 5**: No commit (verification only)

---

## Success Criteria

### Verification Commands
```bash
cd frontend-admin && pnpm run build  # Expected: build success
cd frontend-student && npm run build  # Expected: build success
```

### Final Checklist
- [ ] 两个前端面板比例统一（左 56.5% / 右 43.5%）
- [ ] 角色动画流畅，鼠标追踪无延迟
- [ ] 密码输入时角色正确反应
- [ ] 账号/密码框切换时角色有明显反应
- [ ] 学生端登录页有卡片容器 + 玻璃输入框
- [ ] 学生端注册页使用 VisionOS 玻璃风格
- [ ] 无构建错误
