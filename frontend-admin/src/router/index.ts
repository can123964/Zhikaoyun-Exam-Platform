import type { App } from "vue";
import { createRouter, createWebHashHistory, type RouteRecordRaw } from "vue-router";

export const Layout = () => import("@/layouts/index.vue");

// 公开路由（无需登录）
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    meta: { hidden: true },
  },
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },
];

// 动态路由（需登录，按角色过滤）
// roles 字段：['ADMIN']、['TEACHER','ADMIN']、不设为全部
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "/",
    component: Layout,
    redirect: "/dashboard",
    meta: { roles: ["TEACHER", "ADMIN"] },
    children: [
      // 错误页
      {
        path: "401",
        component: () => import("@/views/error/401.vue"),
        meta: { hidden: true },
      },
      {
        path: "404",
        component: () => import("@/views/error/404.vue"),
        meta: { hidden: true },
      },
      // 首页
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        meta: { title: "首页", icon: "el-icon-House", affix: true, keepAlive: true, roles: ["TEACHER", "ADMIN"] },
      },
      // 个人中心（全部角色）
      {
        path: "profile",
        name: "Profile",
        component: () => import("@/views/profile/index.vue"),
        meta: { title: "个人中心", icon: "el-icon-User", hidden: true },
      },
      // ----- 人员管理 -----
      {
        path: "users",
        name: "UserManage",
        component: () => import("@/views/system/user/index.vue"),
        meta: { title: "人员管理", icon: "el-icon-SetUp", roles: ["TEACHER", "ADMIN"] },
      },
      {
        path: "class",
        name: "ClassList",
        component: () => import("@/views/business/class/index.vue"),
        meta: { title: "班级管理", icon: "el-icon-OfficeBuilding", roles: ["ADMIN"] },
      },
      // ----- 教师 / 管理员 -----
      {
        path: "question",
        name: "QuestionList",
        component: () => import("@/views/business/question/index.vue"),
        meta: { title: "题库管理", icon: "el-icon-Document", roles: ["TEACHER", "ADMIN"] },
      },
      {
        path: "exam",
        name: "ExamList",
        component: () => import("@/views/business/exam/index.vue"),
        meta: { title: "考试管理", icon: "el-icon-Edit", roles: ["TEACHER", "ADMIN"] },
      },
      {
        path: "score",
        name: "ScoreList",
        component: () => import("@/views/business/score/index.vue"),
        meta: { title: "成绩统计", icon: "el-icon-DataLine", roles: ["TEACHER", "ADMIN"] },
      },
      // ----- 考试记录 -----
      {
        path: "record",
        name: "RecordList",
        component: () => import("@/views/business/record/index.vue"),
        meta: { title: "考试记录", icon: "el-icon-Tickets", roles: ["TEACHER", "ADMIN"] },
      },
      {
        path: "record/:recordId",
        name: "RecordDetail",
        component: () => import("@/views/business/record/detail.vue"),
        meta: { title: "答题详情", hidden: true, roles: ["TEACHER", "ADMIN"] },
      },

    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export function setupRouter(app: App<Element>) {
  app.use(router);
}

export default router;
