# 智考云考试平台 — 前端深度研究报告

---

## 一、项目整体架构

```
Zhikaoyun-Exam-Platform/
├── frontend-admin/        # 管理端前端 (Vue 3 技术栈)
├── frontend-student/      # 学生端前端 (Vue 2 技术栈)
├── Backend/               # 后端 (Spring Boot 3.2)
│   └── src/main/java/org/example/zhikaoyunexamplatform/
│       ├── config/        # SecurityConfig, CorsConfig, JWT过滤器
│       ├── common/        # 统一响应、异常处理、枚举、AOP日志
│       ├── user/          # 用户模块 (登录/注册/CRUD)
│       ├── classroom/     # 班级管理
│       ├── question/      # 题库 (含Excel导入)
│       ├── exam/          # 考试管理 (组卷/发布/结束)
│       ├── record/        # 考试记录 (核心: 进入考试/答题/交卷/判分)
│       ├── score/         # 成绩统计 (含Excel导出)
│       ├── dashboard/     # 数据看板
│       └── wrongbook/     # 错题本
├── pom.xml                # Maven 父项目
└── start-frontend.bat     # 前端启动脚本
```

---

## 二、管理端前端 (frontend-admin) — 深度分析

### 2.1 技术栈

| 类别 | 选型 | 版本 | 评价 |
|------|------|------|------|
| 框架 | Vue 3 (Composition API + `<script setup>`) | 3.5 | 最新稳定版 |
| 构建 | Vite | 8.0 | 前沿，已使用 Rolldown + Oxc 压缩 |
| 语言 | TypeScript | 5.9 | 全量类型覆盖 |
| UI 库 | Element Plus | 2.13 | 深度定制样式 |
| 状态管理 | Pinia | 3.0 | Vue 3 官方推荐 |
| 路由 | Vue Router | 5.0 | Hash 模式 |
| HTTP | Axios | 1.13 | 统一拦截器 |
| 图表 | ECharts | 6.0 | 用于 Dashboard |
| CSS 方案 | SCSS + UnoCSS (原子化) | — | 混合使用 |
| 代码规范 | ESLint + Prettier + Stylelint + Husky + Commitlint | — | 工程化完整 |

### 2.2 目录结构 (src/)

```
api/            — API 接口层 (auth/business/system/file)
assets/         — 静态资源
components/     — 公共组件 (CURD通用表格/ECharts/Upload/Pagination)
composables/    — 组合式函数
constants/      — 常量
directives/     — 自定义指令 (v-hasPerm权限指令)
enums/          — 枚举
layouts/        — 布局组件 (Left/Top/Mix三种布局)
plugins/        — 插件
router/         — 路由 + 路由守卫
stores/         — Pinia 状态管理 (user/app/settings/tags-view/permission)
styles/         — 全局样式
types/          — 类型定义
utils/          — 工具 (request/auth/storage)
views/          — 页面
  ├── login/            — 登录页
  ├── dashboard/        — 数据看板 (首页)
  ├── profile/          — 个人中心
  ├── error/            — 401/404
  ├── system/user/      — 人员管理
  └── business/         — 业务页面
      ├── class/        — 班级管理
      ├── question/     — 题库管理
      ├── exam/         — 考试管理
      ├── record/       — 考试记录
      └── score/        — 成绩统计
```

### 2.3 路由与权限

| 路径 | 页面 | 角色权限 |
|------|------|----------|
| `/login` | 登录页 | 公开 |
| `/dashboard` | 数据看板首页 | 教师/管理员 |
| `/users` | 人员管理 | 教师/管理员 |
| `/class` | 班级管理 | 仅管理员 |
| `/question` | 题库管理 | 教师/管理员 |
| `/exam` | 考试管理 | 教师/管理员 |
| `/score` | 成绩统计 | 教师/管理员 |
| `/record` | 考试记录 | 教师/管理员 |
| `/profile` | 个人中心 | 全部 |

### 2.4 核心设计亮点

**1. 登录页 — Apple 风格毛玻璃设计**
- backdrop-filter: blur(40px) 毛玻璃效果
- 多重阴影层级，hover 放大动画
- 深色模式适配
- 自绘 SVG 品牌 Logo

**2. Dashboard — 角色感知数据看板**
- 管理员: 4 个统计卡片 (总用户数/题目数/考试数/记录数)
- 教师: 3 个个人数据卡片 (题目数/考试数/学生数)
- ECharts 近7天考试趋势柱状图 + 用户角色饼图
- 最近活动分页列表
- 问候语根据时段动态变化

**3. 题库管理 — 完整 CRUD + Excel 导入**
- 3 种题型: 单选(0)/多选(1)/判断(2)
- 3 级难度: 简单/中等/困难
- 动态选项 (判断题自动填充"对/错")
- 多选答案排序合并
- Excel 批量导入 (EasyExcel)

**4. 考试管理 — 生命周期完整**
- 状态机: 未发布(0) → 进行中(1) → 已结束(2)
- 完整配置: 类型(正式/模拟/练习)、时长、起止时间、总分、及格分
- 防作弊: 显示答案开关、允许重考、题目/选项随机、切屏次数限制
- 班级关联 (指定可见范围)
- 组卷: 手动选题 + 随机组卷 (按题型/难度/分类多条规则)

### 2.5 工程化水平 (很高)

- Vite 8 构建 (Rolldown + Oxc 压缩)
- UnoCSS 原子化 CSS
- AutoImport + Components 自动导入
- ESLint + Prettier + Stylelint 完整代码规范
- Husky + lint-staged + Commitlint (cz-git)
- mock-dev-server 支持

---

## 三、学生端前端 (frontend-student) — 深度分析

### 3.1 技术栈

| 类别 | 选型 | 版本 | 评价 |
|------|------|------|------|
| 框架 | Vue 2 (Options API) | 2.7 | 已停止维护 |
| 构建 | Vue CLI | 4.5 | Webpack 4，较旧 |
| 语言 | JavaScript | — | 无类型检查 |
| UI 库 | Element UI | 2.15 | 已停更 |
| 状态管理 | Vuex 3 | 3.6 | Vue 2 时代 |
| 路由 | Vue Router 3 | 3.6 | Hash 模式 |
| HTTP | Axios | 0.19 | 较旧版本 |
| CSS | SCSS | — | 无原子化方案 |
| 代码规范 | ESLint 5 + Standard | — | 基础配置 |

### 3.2 目录结构 (src/)

```
api/            — 6个接口文件 (login/register/user/examPaperAnswer/questionAnswer/subject)
layout/         — 布局 (顶部导航)
router.js       — 单文件路由
store/          — Vuex 状态 (user/enumItem)
utils/          — 工具 (request/index/validate/scroll-to)
views/          — 8个页面
  ├── login/            — 登录页
  ├── register/         — 注册页
  ├── dashboard/        — 首页 (可参加考试列表)
  ├── paper/            — 我的考试
  ├── exam/             — 答题
  │   ├── paper/do.vue  — 答题主页面 (核心)
  │   ├── paper/read.vue— 试卷查看
  │   └── components/   — QuestionEdit / QuestionAnswerShow
  ├── record/           — 考试记录
  ├── question-error/   — 错题本
  └── user-info/        — 个人中心
```

### 3.3 路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | 登录 | 公开 |
| `/register` | 注册 | 公开 |
| `/index` | 首页 (可参加考试列表) | 需登录 |
| `/paper/index` | 我的考试 | 需登录 |
| `/record/index` | 考试记录 | 需登录 |
| `/question/index` | 错题本 | 需登录 |
| `/user/index` | 个人中心 | 需登录 |
| `/do?examId=` | 答题页面 | 需登录 |
| `/read?recordId=` | 试卷查看 | 需登录 |

### 3.4 核心答题流程 (exam/paper/do.vue)

1. **进入考试**: `POST /api/record/{examId}/enter` → 获取试卷数据
2. **防切屏提醒**: 如有切屏限制，弹窗告知
3. **倒计时**: 前端 setInterval 每秒递减，归零自动交卷
4. **逐题保存**: QuestionEdit 组件选择答案后自动保存到后端
5. **切屏上报**: `document.visibilitychange` + `beforeunload` (fetch+keepalive)
6. **交卷**: `POST /api/record/{recordId}/submit` → 返回分数
7. **交卷前 flush**: 所有防抖保存完成后提交

### 3.5 工程化水平 (较低)

- Vue CLI 4 (Webpack 4)
- node-sass (需要 Python 编译环境)
- ESLint 5 (较旧)
- 无 TypeScript
- 无自动导入
- 无单元测试
- 无 CI/CD

---

## 四、两个前端核心差异对比

| 维度 | 管理端 (frontend-admin) | 学生端 (frontend-student) |
|------|------------------------|---------------------------|
| **Vue 版本** | Vue 3 (Composition API) | Vue 2 (Options API) |
| **构建工具** | Vite 8 (Rolldown) | Vue CLI 4 (Webpack 4) |
| **语言** | TypeScript 5.9 | JavaScript (无 TS) |
| **UI 库** | Element Plus 2.13 | Element UI 2.15 |
| **状态管理** | Pinia 3 | Vuex 3 |
| **CSS 方案** | SCSS + UnoCSS | 纯 SCSS |
| **代码规范** | ESLint+Prettier+Stylelint+Husky | 基础 ESLint |
| **请求封装** | 统一 axios + 拦截器自动解包 | axios + 手动 loading |
| **自动导入** | 有 (unplugin) | 无 |
| **开发端口** | 3000 | 8001 |
| **目标用户** | 教师/管理员 | 学生 |
| **页面数量** | ~15 页面 | ~10 页面 |
| **UI 设计质量** | 高 (Apple 毛玻璃) | 基础 (Element UI 默认) |
| **工程成熟度** | 高 (生产级) | 低 (基础) |

---

## 五、前后端对接分析

### 5.1 共同后端接口

两个前端共享同一后端，认证体系完全一致:

| 项目 | 详情 |
|------|------|
| 登录接口 | `POST /api/user/login` |
| 返回格式 | `{ token, userId, username, realName, role }` |
| Token 存储 | `localStorage` 的 `exam_token` key |
| 认证方式 | `Authorization: Bearer <token>` |
| 响应格式 | `{ code: 200, msg: "success", data: {...} }` |
| 角色体系 | 0=学生, 1=教师, 2=管理员 |

### 5.2 管理端特有接口

| 模块 | 路径 | 用途 |
|------|------|------|
| Dashboard | `GET /api/dashboard` | 数据看板 |
| Class | `GET/POST/PUT/DELETE /api/class` | 班级 CRUD |
| Question | `GET/POST/PUT/DELETE /api/question/**` | 题库管理 |
| Exam | `GET/POST/PUT/DELETE /api/exam/**` | 考试管理 |
| Record | `GET /api/record/exam/{examId}` | 查看记录 |
| Score | `GET /api/score/exam/{examId}/**` | 成绩统计/导出 |
| User | `GET/POST/PUT/DELETE /api/user/**` | 用户管理 |

### 5.3 学生端特有接口

| 路径 | 用途 |
|------|------|
| `GET /api/record/available` | 可参加考试列表 |
| `POST /api/record/{examId}/enter` | 进入考试 |
| `POST /api/record/{recordId}/answer` | 保存单题答案 |
| `POST /api/record/{recordId}/submit` | 交卷 |
| `POST /api/record/{recordId}/tab-switch` | 切屏上报 |
| `GET /api/record/my` | 我的考试记录 |
| `GET /api/record/detail/{recordId}` | 答题详情 |
| `POST /api/user/register` | 注册 |

---

## 六、后端概要

### 6.1 技术栈
Spring Boot 3.2.5 + Java 21 + MyBatis (手写SQL) + Spring Security (JWT) + MySQL 8 + EasyExcel + Lombok

### 6.2 全部10个模块均已✅完成

| 模块 | 功能 | 难度 |
|------|------|------|
| `config/` | Security/CORS/JWT/异常处理 | ⭐⭐ |
| `common/` | 统一响应/枚举/AOP日志 | ⭐⭐ |
| `user/` | 用户 CRUD + 登录/注册 | ⭐⭐ |
| `classroom/` | 班级管理 (示范模板) | ⭐⭐ |
| `question/` | 题库 CRUD + Excel导入 | ⭐⭐⭐ |
| `exam/` | 考试管理 + 组卷/发布/结束 | ⭐⭐⭐ |
| `record/` | 考试记录 (核心业务) | ⭐⭐⭐⭐ |
| `score/` | 成绩统计/排名/Excel导出 | ⭐⭐⭐ |
| `dashboard/` | 数据看板 | ⭐⭐ |
| `wrongbook/` | 错题本 | ⭐⭐ |

### 6.3 关键设计
- **乐观锁防重复提交**: `t_exam_record.version`
- **判分**: 单选/判断直接比较，多选排序后比较
- **超时**: API懒检查 + `@Scheduled` 定时自动交卷
- **逻辑删除**: 所有表 `is_deleted` 字段
- **多选答案**: 入库前排序 (防止 "BA" ≠ "AB")

---

## 七、总体评价

### 7.1 项目定位
课程设计/毕业设计级别的在线考试平台，功能完整度很高，前后端全链路闭环。

### 7.2 亮点
1. **管理端 UI 质量高** — Apple 风格毛玻璃设计，远超一般课设水平
2. **功能覆盖完整** — 题库→组卷→考试→答题→判分→成绩→错题本，全链路闭环
3. **防作弊机制** — 切屏检测 + 题目/选项随机 + 乐观锁
4. **工程化程度高(管理端)** — 自动化工具链齐全
5. **角色权限体系** — 管理员/教师/学生 三级控制

### 7.3 潜在问题
1. **学生端技术栈陈旧** — Vue 2 + Vue CLI 4 + Element UI (均已停止维护)
2. **学生端工程化薄弱** — 无 TypeScript、无自动导入
3. **两端技术栈不一致** — 维护成本高
4. **学生端无单元测试**
5. **两端代码风格差异大** — Composition API vs Options API, TS vs JS

### 7.4 项目性质
> **实训项目**，能演示即可，无需升级改造。
> 当前功能完整度已满足演示需求，维护现状就好。

---

*报告生成时间: 2026-06-12*
