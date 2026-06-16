# AGENTS.md

> **AI 开发助手指引 — 智考云考试平台前端项目**

本文件为 AI 编程工具（Codex / Claude Code 等）提供前端项目上下文，帮助精准理解项目结构和技术决策。

---

## ⚠️ 最高优先级规则

> **所有代码改动、文件创建/修改/删除，必须先向技术经理汇报方案，等确认后再动手。严禁擅自执行。**

---

## 项目概述

智考云在线考试平台前端 — 基于 `vue3-element-template`（有来技术）精简模板二次开发，Vue 3 + Vite + TypeScript + Element Plus。

- **后端项目**：上级目录 `Zhikaoyun-Exam-Platform/`（Spring Boot 3.2，端口 8080）
- **后端 API 文档**：`../docs/智考云API接口文档-V3.md`
- **数据库设计**：`../docs/DataBase.sql`

---

## 常用命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器（端口 3000，自动代理 /api → localhost:8080）
pnpm run dev

# 生产构建
pnpm run build

# 代码检查
pnpm run lint
```

---

## 技术栈

| 类别 | 选型 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API + `<script setup>`) | 3.5 |
| 构建 | Vite | 8 |
| 语言 | TypeScript | 5.9 |
| UI 库 | Element Plus | 2.14 |
| 状态管理 | Pinia | 3 |
| 路由 | Vue Router | 5 |
| HTTP | Axios | 1.17 |
| 图表 | ECharts | 6 |
| CSS | SCSS + UnoCSS（原子化） | — |
| 图标 | @element-plus/icons-vue | 2.3 |

---

## 目录结构

```
frontend/
├── src/
│   ├── api/                    # 接口请求层
│   │   ├── auth/               # 认证接口（login / logout）
│   │   │   ├── index.ts        # AuthAPI 对象
│   │   │   └── types.ts        # LoginRequest / LoginResponse
│   │   ├── system/             # 用户管理接口（保留模板原有）
│   │   │   └── user/
│   │   ├── common.ts           # 通用类型（OptionItem 等）
│   │   └── file/               # 文件上传接口
│   │
│   ├── assets/                 # 静态资源（图标、图片、样式）
│   ├── components/             # 公共组件
│   │   ├── CURD/               # 🔥 通用表格组件（PageContent/PageModal/PageSearch）
│   │   ├── ECharts/            # ECharts 图表封装
│   │   ├── Upload/             # 文件上传（单图/多图/文件）
│   │   ├── Pagination/         # 分页组件
│   │   └── ...                 # 其他组件（暗黑切换、全屏、面包屑等）
│   │
│   ├── composables/            # 组合式函数
│   │   └── table/              # 表格选择 Hook
│   │
│   ├── constants/              # 常量定义（存储键名等）
│   ├── directives/             # 自定义指令
│   │   └── permission/         # v-hasPerm（按钮权限指令）
│   │
│   ├── enums/                  # 枚举
│   │   ├── api.ts              # ApiCodeEnum（SUCCESS=200 / UNAUTHORIZED=401）
│   │   ├── business.ts
│   │   ├── common.ts
│   │   └── settings.ts
│   │
│   ├── layouts/                # 布局组件
│   │   ├── index.vue           # 布局入口（按配置切换 Left/Top/Mix）
│   │   ├── LeftLayout.vue      # 🔥 左侧菜单布局（当前使用）
│   │   ├── TopLayout.vue       # 顶部菜单布局
│   │   ├── MixLayout.vue       # 混合布局
│   │   ├── useLayout.ts        # 布局 Composable（菜单数据来源）
│   │   └── components/         # 布局子组件
│   │       ├── LayoutSidebar.vue       # 侧边栏菜单
│   │       ├── LayoutSidebarItem.vue   # 菜单项递归组件
│   │       ├── LayoutNavbar.vue        # 顶部导航栏
│   │       ├── LayoutTagsView.vue      # 标签页导航
│   │       ├── LayoutMain.vue          # 主内容区
│   │       └── LayoutSettings.vue      # 布局设置抽屉
│   │
│   ├── router/
│   │   ├── index.ts            # 路由配置（constantRoutes + asyncRoutes）
│   │   └── guards/
│   │       └── permission.ts   # 路由守卫（登录校验 + 角色过滤）
│   │
│   ├── stores/                 # Pinia 状态管理
│   │   ├── index.ts
│   │   ├── user.ts             # 🔥 用户状态（登录/登出/用户信息）
│   │   ├── app.ts              # 应用状态（侧边栏折叠、设备类型）
│   │   ├── settings.ts         # 设置状态（主题、布局模式）
│   │   ├── tags-view.ts        # 标签页状态
│   │   ├── permission.ts       # 权限状态（精简 stub，从静态路由取菜单）
│   │   └── dict.ts             # 字典状态（精简 stub，占位）
│   │
│   ├── styles/                 # 全局样式
│   ├── utils/
│   │   ├── request.ts          # 🔥 Axios 实例 + 拦截器（JWT 附加 / 401 跳转）
│   │   ├── auth.ts             # 🔥 Token 存取 + 角色工具
│   │   ├── storage.ts          # localStorage/sessionStorage 封装
│   │   └── ...
│   │
│   ├── views/                  # 页面
│   │   ├── login/              # 登录页
│   │   ├── dashboard/          # 首页（数据看板）
│   │   ├── profile/            # 个人中心
│   │   ├── error/              # 401 / 404 错误页
│   │   ├── system/             # 系统管理（仅保留 user/）
│   │   │   └── user/           # 用户管理（管理员）
│   │   └── business/           # 🔥 考试业务页面（待开发）
│   │       ├── class/          # 班级管理
│   │       ├── question/       # 题库管理
│   │       ├── exam/           # 考试管理
│   │       ├── exam-student/   # 学生考试端
│   │       ├── record/         # 考试记录
│   │       ├── score/          # 成绩统计
│   │       └── wrongbook/      # 错题本
│   │
│   ├── App.vue
│   ├── main.ts                 # 入口文件
│   └── settings.ts             # 应用配置
│
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── vite.config.ts              # Vite 配置（代理 /api → 8080）
├── uno.config.ts               # UnoCSS 配置
└── package.json
```

---

## 核心代码模式

### 1. 接口调用

所有业务接口调用必须通过 `src/api/` 下的模块，**不要在页面里直接 import axios**。

```ts
// 定义接口（src/api/question/index.ts）
import request from "@/utils/request";

export function getQuestionList(params: any) {
  return request.get("/api/question/list", { params });
}

// 页面中调用
const res = await getQuestionList({ page: 1, size: 10 });
// res = { code: 200, msg: "success", data: { total, pages, current, size, records } }
const { records, total } = res.data;
```

### 2. 统一响应格式

后端返回 `{ code, msg, data }`，**axios 拦截器不解包**，业务代码通过 `res.data` 取数据：

| code | 含义 | 触发场景 |
|------|------|----------|
| 200 | 成功 | 正常返回 |
| 400 | 参数/业务错误 | @Valid 校验失败 |
| 401 | 未登录 | Token 缺失或过期（拦截器自动跳登录） |
| 403 | 无权限 | 角色不匹配 |
| 500 | 服务器错误 | 未处理异常 |

### 3. 分页请求

```ts
// 所有列表接口统一参数
const query = ref({ page: 1, size: 10, keyword: "" });
const res = await getList(query.value);
// res.data.records  → 数据行
// res.data.total    → 总记录数
```

### 4. Token 与认证

- Token 存储在 `localStorage`，key 为 `exam_token`
- 登录成功后自动写入，axios 请求拦截器自动附加 `Authorization: Bearer <token>`
- 401 响应时拦截器自动清除 Token 并跳转登录页
- 用户信息缓存在 `localStorage`，key 为 `exam_user`，格式 `{ role, username, realName }`

### 5. 角色权限

| 角色值 | 角色名 | 路由标识 |
|--------|--------|----------|
| 0 | 学生 | STUDENT |
| 1 | 教师 | TEACHER |
| 2 | 管理员 | ADMIN |

**路由级权限**：在 `router/index.ts` 的 `meta.roles` 数组中指定允许的角色。

**按钮级权限**：使用 `v-hasPerm` 指令或 `hasRole()` 函数。

```vue
<!-- 仅管理员可见 -->
<el-button v-hasPerm="2">删除</el-button>

<!-- 脚本中判断 -->
<script setup>
import { hasRole } from "@/utils/auth";
const canEdit = hasRole(1, 2); // 教师或管理员
</script>
```

### 6. 侧边栏菜单

菜单数据来源于 `router/index.ts` 的 `asyncRoutes`，`useLayout.ts` 自动提取 `hidden: false` 的路由生成菜单。**新增页面后只需在 router 中加路由即可自动显示菜单**。

### 7. CURD 表格组件

模板自带了强大的 `CURD/` 组件库（`PageContent` / `PageModal` / `PageSearch`），可大幅简化列表页开发。**新手也可以直接用 Element Plus 原生表格**，两者均可。

### 8. UnoCSS（原子化 CSS）

模板内置 UnoCSS，可直接在模板中使用原子化类名：

```vue
<div flex items-center gap-10px>  <!-- display:flex; align-items:center; gap:10px -->
  <span text-red font-bold>红色加粗文字</span>
</div>
```

---

## 页面开发指南

### 新增一个业务列表页（标准步骤）

1. **在 `src/router/index.ts` 中添加路由**
2. **在 `src/api/` 下创建接口文件**
3. **在 `src/views/business/xxx/` 创建页面组件**

### 待开发页面清单（按优先级）

| 页面 | 路由 | 角色 | 对应后端接口 |
|------|------|------|-------------|
| 用户管理（优化） | `/user` | 管理员 | `GET/POST/PUT/DELETE /api/user` |
| 班级管理 | `/class` | 管理员 | `GET/POST/PUT/DELETE /api/class` |
| 题库管理 | `/question` | 教师/管理员 | `GET/POST/PUT/DELETE /api/question` + Excel 导入 |
| 考试管理 | `/exam` | 教师/管理员 | `GET/POST/PUT /api/exam` + 发布/结束/随机组卷 |
| 成绩统计 | `/score` | 教师/管理员 | `GET /api/score/exam/{id}` + stat/rank/export |
| 数据看板 | `/dashboard` | 教师/管理员 | `GET /api/dashboard/overview`（ECharts 图表） |
| 可参加考试 | `/my-exams` | 学生 | `GET /api/record/available` |
| 答题页面 | `/exam/:id/take` | 学生 | `POST /api/record/{id}/enter` + answer/submit |
| 考试记录 | `/my-records` | 学生 | `GET /api/record/my` |
| 答题详情 | `/record/:id` | 学生/教师 | `GET /api/record/{id}/detail` |
| 错题本 | `/wrong-book` | 学生 | `GET /api/wrong-book/list` |
| 个人中心 | `/profile` | 全部 | `PUT /api/user/password` |

### 接口文档速查

所有接口详见 `../docs/智考云API接口文档-V3.md`，速查表在文档第二章。

---

## 开发规范

### 必须遵守

- **使用 `<script setup lang="ts">`** 语法，不要写 Options API
- **所有接口调用通过 `src/api/` 模块**，不在页面直接写 axios
- **错误提示由拦截器统一处理**，页面只需 try/catch 防止阻塞
- **组件和页面用 PascalCase 命名**，目录和工具文件用 kebab-case
- **新增页面必须放在 `src/views/business/` 对应目录下**
- **不要引入新的 UI 库**，优先使用 Element Plus 和项目已有组件

### 代码风格

- 模板已配置 ESLint + Prettier，保存时自动格式化
- UnoCSS 优先于自定义 SCSS（原子化类名更简洁）
- 页面样式使用 `<style scoped lang="scss">`

---

## 后端默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |
| teacher01 | admin123 | 教师 |
| stu001 ~ stu010 | admin123 | 学生 |

---

## 与后端的差异（已适配）

本前端基于 `vue3-element-template` 做了以下适配，新增页面时注意：

| 适配项 | 模板原值 | 改为 |
|--------|---------|------|
| 成功响应码 | `"00000"` | `200` |
| Token 机制 | 双 Token（access + refresh） | 单 Token |
| 登录接口 | `POST /api/v1/auth/login` | `POST /api/user/login` |
| 菜单来源 | 后端动态路由 | 前端静态路由 `asyncRoutes` |
| 角色模式 | 多角色 + 按钮权限 ID | 单角色（0/1/2） |
| 字典功能 | 有 | 精简为 stub（占位） |
| 代码生成器 | 有 | 已删除 |

---

## 常见问题

### Q: 前端启动后无法登录？
先确认后端已启动（`mvn spring-boot:run`），端口 8080。Vite 已配置 `/api` 代理到 `localhost:8080`。

### Q: 接口报 401？
登录获取 Token 后自动存储。如果长时间未操作 Token 过期（24h），需重新登录。

### Q: 新增页面没出现在侧边栏？
检查路由的 `meta.hidden` 是否为 `false` 或未设置，以及 `meta.roles` 是否包含当前用户角色。

### Q: UnoCSS 类名不生效？
检查 `uno.config.ts` 是否已配置对应规则。常用原子类直接可用（flex / gap / text-center 等）。
