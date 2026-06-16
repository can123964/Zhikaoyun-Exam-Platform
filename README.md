# 📌 智考云考试平台 (Zhikaoyun Exam Platform)

> **智考云平台** 是一款基于最新技术栈构建的、高效、稳定的前后端分离在线考试系统。旨在为学校、企业及培训机构提供全流程的智能化考核解决方案。

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue.js-3.x-4fc08d?style=flat-square&logo=vue.js)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.x-646CFF?style=flat-square&logo=vite)](https://vitejs.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)

---

## 📖 项目简介
智考云是一套 *全功能在线考试平台* ，支持三种用户角色：

| 角色 | 能力 |
| ----- | ----- |
| 学生 | 参加考试、查看成绩、错题本复习、个人中心 |
| 教师 | 题库管理、出卷组卷（手动/随机）、发布考试、成绩统计分析 |
| 管理员 | 用户管理、班级管理、全局数据看板、系统日志审计 |

核心业务流程： *题库 → 组卷 → 考试 → 答题 → 自动判分 → 成绩分析 → 错题本闭环* 。

---

## ✨ 核心特性 (Features)

- 🔒 **全方位权限管理**：采用前后端分离的路由动态鉴权与权限控制，确保考试数据的安全性。
- 📝 **多元化题型支持**：支持单选、多选、判断、简答等多种题型，满足不同场景的考核需求。
- ⚡ **顺畅的答题体验**：前端采用 Vue 3 组合式 API + 状态管理，提供响应极快的无刷新交卷与倒计时体验。
- 📊 **智能成绩统计**：客观题自动批改，后台提供直观的图表与数据分析，方便快速掌握考核结果。

---

## 🛠️ 技术栈 (Tech Stack)

### 🖥️ 前端 (Frontend)
- **核心框架**：Vue 3 (Composition API)
- **构建工具**：Vite
- **路由/状态管理**：Vue Router & Pinia
- **组件库**：Element Plus / Ant Design Vue (根据实际采用的修改)
- **网络请求**：Axios
- **样式预处理**：Sass

### ⚙️ 后端 (Backend)
- **核心框架**：Spring Boot 3.x
- **持久层框架**：MyBatis / MyBatis-Plus
- **数据库**：MySQL 8.0+
- **构建管理**：Maven

---

## 📂 项目结构 (Project Structure)

建议在本地使用 IDEA 打开时，保持以下规范的目录分布：

```text
Zhikaoyun-Exam-Platform/
├── backend/                  # 后端源码目录
│   ├── src/main/java/        # Java 业务代码
│   ├── src/main/resources/   # 配置文件与 SQL 脚本
│   └── pom.xml               # Maven 依赖配置
├── frontend/                 # 前端源码目录
│   ├── src/                  # Vue 源码 (views, components, store)
│   ├── package.json          # 前端依赖管理
│   └── vite.config.js        # Vite 配置文件
└── README.md                 # 项目说明文档
