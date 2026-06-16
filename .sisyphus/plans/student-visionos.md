# 学生端 VisionOS Spatial UI 风格改写

## TL;DR

> **将学生端前端全面升级为 VisionOS 空间 UI 风格**，完全复刻管理端的毛玻璃设计语言。
> 
> **关键改动**: 更新全局 CSS 变量 → 改写 8 个页面的卡片/容器样式为玻璃态
> 
> **风险**: 极低 — 纯 CSS 改动，不动任何 JS/业务逻辑
> **并行度**: 高 — 各页面可同时改写
> **预估时间**: 1-2 小时（含验证）

---

## 设计规范（来自 ui-ux-pro-max skill）

### VisionOS Spatial UI 核心 Token

```css
/* 毛玻璃卡片 */
--glass-bg: rgba(255, 255, 255, 0.55);
--glass-blur: blur(40px);
--glass-saturate: saturate(200%);
--glass-border: 1px solid rgba(255, 255, 255, 0.6);
--glass-shadow: 
  0 8px 32px rgba(0, 0, 0, 0.1),
  0 2px 8px rgba(0, 0, 0, 0.06),
  inset 0 1px 0 rgba(255, 255, 255, 0.8);
--glass-radius: 24px;
--glass-hover-transform: scale(1.02);
--glass-transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);

/* 配色 */
--brand-accent: #007AFF;      /* 系统蓝 */
--brand-accent-hover: #0066d6;
--brand-dark: #1d1d1f;
--brand-muted: #6e6e73;
--brand-bg: #f5f5f7;          /* Apple 浅灰 */
--brand-surface: #ffffff;
```

### 应用规则

| 元素 | 规则 |
|------|------|
| 卡片容器 | 毛玻璃背景 + 大圆角 24px + 多层阴影 |
| Hover 效果 | transform: scale(1.02) + 阴影加深 |
| 输入框 | 玻璃态内背景 + 聚焦蓝光 |
| 导航栏 | 毛玻璃 + 底部微妙边框 |
| 品牌色 | #007AFF 作为主色 |
| 字体 | Inter + Noto Sans SC |

---

## TODO

- [x] 全局样式: `styles/index.scss` — VisionOS 变量 + 毛玻璃基础
- [x] 导航栏: `layout/index.vue` — 毛玻璃
- [x] 首页: `dashboard/index.vue` — 欢迎卡片 + 考试卡片毛玻璃
- [x] 我的考试: `paper/index.vue` — 考试卡片毛玻璃
- [x] 考试记录: `record/index.vue` — 表格卡片 + 详情毛玻璃
- [x] 错题本: `question-error/index.vue` — 容器 + 卡片网格 + 详情毛玻璃
- [x] 个人中心: `user-info/index.vue` — 两个信息卡片毛玻璃
- [x] 答题页: `exam/paper/do.vue` — 题目标题条 + 题目卡片毛玻璃
- [x] 试卷查看: `exam/paper/read.vue` — 题目标题条 + 题目卡片毛玻璃
- [x] 验证: 启动 dev server 逐页检查效果

---

## 改动清单

### 文件 1: `src/styles/index.scss` — 全局样式变量

**改动内容**:
1. 更新 CSS 变量为 VisionOS 风格
   - `--brand-bg: #f5f5f7` (浅灰底)
   - `--brand-accent: #007AFF` (系统蓝)
   - `--brand-accent-hover: #0066d6`
   - 新增 `--glass-*` 变量族
2. 更新 `.el-card` 样式为毛玻璃
3. 更新 `.el-button--primary` 配色
4. 更新 `.el-table` 圆角
5. 更新 `.exam-card` hover 效果
6. 更新背景网格颜色
7. 更新 `.student-header` 为毛玻璃

### 文件 2: `src/layout/index.vue` — 导航栏

**改动内容**:
1. 导航栏背景改为 `rgba(255,255,255,0.65)` + `backdrop-filter: blur(40px) saturate(200%)`
2. 添加底部边框 `rgba(255,255,255,0.6)`
3. 更新下拉菜单样式

### 文件 3: `src/views/dashboard/index.vue` — 首页

**改动内容**:
1. `.welcome-banner` → 毛玻璃卡片 + hover scale(1.02)
2. `.exam-card` → 毛玻璃卡片 + hover scale(1.02) + 阴影加深
3. `.index-title-h3` → VisionOS 风格标题

### 文件 4: `src/views/paper/index.vue` — 我的考试

**改动内容**:
1. `.exam-card` → 同首页毛玻璃风格
2. `.page-title` → 精简标题样式

### 文件 5: `src/views/record/index.vue` — 考试记录

**改动内容**:
1. `.record-table-card` → 毛玻璃
2. `.record-answer-info` → 毛玻璃侧边卡

### 文件 6: `src/views/question-error/index.vue` — 错题本

**改动内容**:
1. `.wrongbook-container` → 毛玻璃
2. `.wrong-card` → 毛玻璃卡片 + hover scale
3. `.detail-card` → 毛玻璃

### 文件 7: `src/views/user-info/index.vue` — 个人中心

**改动内容**:
1. 两个 `.el-card` → 毛玻璃

### 文件 8: `src/views/exam/paper/do.vue` — 答题页

**改动内容**:
1. 题目标题条 → 毛玻璃顶栏
2. 题目卡片 → 毛玻璃
3. 移除 `background-color: #F5F5DC`

### 文件 9: `src/views/exam/paper/read.vue` — 试卷查看

**改动内容**:
1. 题目卡片 → 毛玻璃
2. 顶栏 → 毛玻璃

---

## 执行顺序

```
Wave 1 (并行 - 基础 + 页面):
├── 文件1: styles/index.scss   [全局变量]
├── 文件2: layout/index.vue    [导航栏]
├── 文件3: dashboard/index.vue [首页]
├── 文件4: paper/index.vue     [我的考试]
├── 文件5: record/index.vue    [考试记录]
├── 文件6: question-error/index.vue [错题本]
├── 文件7: user-info/index.vue [个人中心]
├── 文件8: exam/paper/do.vue   [答题页]
└── 文件9: exam/paper/read.vue [试卷查看]

Wave 2 (验证):
└── 启动前端 dev server，逐页检查样式
```

---

## 验证方式

1. 启动学生端: 在 `frontend-student/` 目录运行 `npm run serve`
2. 逐页截图比对:
   - 登录页 → 玻璃卡片效果
   - 首页 → 欢迎卡片 + 考试卡片 hover
   - 我的考试 → 卡片风格
   - 考试记录 → 双栏玻璃卡
   - 错题本 → 错题卡片
   - 个人中心 → 两个玻璃卡
   - 答题页 → 题目卡片
