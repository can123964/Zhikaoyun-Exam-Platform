# 修复前端显示问题工作计划

## 问题描述

前端页面存在四个显示问题 + 两个样式优化：

1. **表格固定列遮挡问题**：人员管理页面，操作列会遮挡其他列内容
2. **表格隔行变色问题**：隔行变色的表格行，文字与背景重叠
3. **仪表盘图标截断问题**：首页总用户数图标显示不全，头部被截断
4. **饼状图模糊问题**：首页饼状图在高DPI屏幕上渲染模糊
5. **【优化】Glass效果更透明**：导航栏、侧边栏等区域玻璃效果更通透
6. **【优化】字体更换为Inter**：使用更清晰的现代字体

## 问题分析

### 问题1：固定列遮挡

**位置**：`frontend-admin/src/styles/vendors/_element-plus-override.scss` 第 153-157 行

```scss
// 固定列阴影
.el-table-fixed-column--left,
.el-table-fixed-column--right {
  background: var(--brand-surface-solid) !important;
}
```

**原因**：
- `--brand-surface-solid` 在亮色模式下是 `#ffffff`（纯白色，不透明）
- 但是固定列的 z-index 设置可能有问题
- 表格的 overflow 处理可能有问题

### 问题2：隔行变色

**位置**：`frontend-admin/src/styles/vendors/_element-plus-override.scss` 第 142-145 行

```scss
// 斑马纹行
&--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(0, 0, 0, 0.02) !important;
  background-color: rgba(0, 0, 0, 0.02) !important;
}
```

**原因**：
- 背景色 `rgba(0, 0, 0, 0.02)` 太透明
- 文字与背景的对比度不足

### 问题3：仪表盘图标截断

**位置**：`frontend-admin/src/views/dashboard/index.vue` 第 23-26 行

```svg
<svg width="20" height="20" viewBox="0 0 20 20" fill="none">
  <path d="M13 17v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M5 5a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" .../>
  <path d="M19 17v-2a4 4 0 0 0-3-3.87M15 5a4 4 0 1 0 0-8" .../>
</svg>
```

**原因**：
- viewBox 设置为 `0 0 20 20`，但图标路径超出了这个范围
- 第一个path中有 `M5 5a4 4 0 1 0 0-8`，y=-8 超出了viewBox的范围
- 第二个path中有 `M19 17`，x=19 接近viewBox边界
- 导致图标头部被截断

### 问题4：饼状图模糊

**位置**：`frontend-admin/src/views/dashboard/index.vue` 第 305 行和第 241 行

```typescript
pieChart = echarts.init(pieChartRef.value);
trendChart = echarts.init(trendChartRef.value);
```

**原因**：
- ECharts 初始化时没有设置 `devicePixelRatio` 参数
- 在高 DPI 屏幕（如 Retina 显示屏）上，Canvas 渲染分辨率不足
- 导致图表文字和图形边缘模糊

## 修复方案

### 修复1：固定列遮挡

**修改文件**：`frontend-admin/src/styles/vendors/_element-plus-override.scss`

**修改内容**：
```scss
// 固定列阴影
.el-table-fixed-column--left,
.el-table-fixed-column--right {
  background: var(--brand-surface-solid) !important;
  z-index: 10;
  box-shadow: -4px 0 8px rgba(0, 0, 0, 0.05);
}
```

### 修复2：隔行变色

**修改文件**：`frontend-admin/src/styles/vendors/_element-plus-override.scss`

**修改内容**：
```scss
// 斑马纹行
&--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(0, 0, 0, 0.04) !important;
  background-color: rgba(0, 0, 0, 0.04) !important;
}
```

### 修复3：仪表盘图标截断

**修改文件**：`frontend-admin/src/views/dashboard/index.vue`

**修改内容**：
将总用户数图标的viewBox从 `0 0 20 20` 改为 `0 0 24 24`，并调整width和height：

```svg
<svg width="24" height="24" viewBox="0 0 24 24" fill="none">
  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
```

同时需要调整 `.stat-icon` 的样式，确保图标居中显示：

```scss
.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  svg {
    width: 24px;
    height: 24px;
  }
}
```

### 修复4：饼状图模糊

**修改文件**：`frontend-admin/src/views/dashboard/index.vue`

**修改内容**：
在 ECharts 初始化时添加 `devicePixelRatio` 配置：

```typescript
// 修改饼图初始化（第 305 行）
pieChart = echarts.init(pieChartRef.value, undefined, {
  devicePixelRatio: window.devicePixelRatio || 2,
});

// 修改柱状图初始化（第 241 行）
trendChart = echarts.init(trendChartRef.value, undefined, {
  devicePixelRatio: window.devicePixelRatio || 2,
});
```

## 执行步骤

1. 打开 `frontend-admin/src/styles/vendors/_element-plus-override.scss` 文件
2. 修改固定列样式（第 153-157 行）
3. 修改斑马纹行样式（第 142-145 行）
4. 打开 `frontend-admin/src/views/dashboard/index.vue` 文件
5. 修改总用户数图标的viewBox和尺寸
6. 调整 `.stat-icon` 样式确保图标居中
7. 修改饼图和柱状图初始化，添加 devicePixelRatio 配置
8. 保存文件
9. 测试修复效果

## 验证标准

1. 固定列不再遮挡其他列的内容
2. 隔行变色的表格行，文字清晰可读
3. 仪表盘总用户数图标完整显示，不再被截断
4. 饼状图和柱状图在高DPI屏幕上清晰显示
5. 整体样式保持一致

## 相关文件

- `frontend-admin/src/styles/vendors/_element-plus-override.scss` - 表格样式修改
- `frontend-admin/src/views/dashboard/index.vue` - 仪表盘页面图标和图表修复
- `frontend-admin/src/views/system/user/index.vue` - 人员管理页面（测试用）
- `frontend-admin/src/styles/base/_variables.scss` - CSS 变量定义

---

## 样式优化

### 优化1：Glass效果分层透明度

**修改文件**：`frontend-admin/src/styles/vendors/_element-plus-override.scss`

**修改内容**：

```scss
// 基础 glass mixin（用于导航栏、侧边栏等轻量区域）— 更透明
@mixin glass-panel {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.35);  // 从 0.55 降到 0.35，更通透
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

// 重 glass mixin（用于表格、弹窗等需要可读性的区域）— 保持较高不透明度
@mixin glass-panel-heavy {
  backdrop-filter: blur(40px) saturate(200%);
  -webkit-backdrop-filter: blur(40px) saturate(200%);
  background: rgba(255, 255, 255, 0.65);  // 保持 0.65，确保可读性
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.12),
    0 4px 12px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}
```

**应用区域**：

| 区域 | 使用 mixin | 透明度 | 理由 |
|------|------------|--------|------|
| 侧边栏菜单 | `glass-panel` | 0.35 | 菜单项有间距，可以更透明 |
| 卡片容器 | `glass-panel` | 0.35 | 适度透明，保持可读性 |
| 下拉框/弹出层 | `glass-panel-heavy` | 0.65 | 需要高对比度 |
| 表格 | 保持现有 | 0.55 | 内容密集，不能太透明 |
| 对话框/抽屉 | 保持现有 | 0.95 | 需要最高可读性 |

**暗黑模式同步修改**：

```scss
html.dark {
  --glass-bg: rgba(44, 44, 46, 0.55);  // 从 0.72 降到 0.55
  --glass-bg-heavy: rgba(44, 44, 46, 0.78);  // 保持较高
}
```

### 优化2：字体更换为Inter

**修改文件**：`frontend-admin/src/styles/base/_variables.scss`

**修改内容**：

```scss
// 在 :root 中添加字体变量
:root {
  // ... 现有变量 ...
  
  // 字体
  --font-family-primary: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}
```

**修改文件**：`frontend-admin/index.html`

**修改内容**：

```html
<head>
  <!-- 添加 Inter 字体引入 -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
```

**修改文件**：`frontend-admin/src/styles/base/_reset.scss`

**修改内容**：

```scss
// 在全局样式中应用字体
body {
  font-family: var(--font-family-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
```

**同时更新暗黑模式字体颜色**：

```scss
html.dark {
  // ... 现有变量 ...
  --glass-bg: rgba(44, 44, 46, 0.55);
  --glass-bg-heavy: rgba(44, 44, 46, 0.78);
}
```

## 更新后的执行步骤

1. 打开 `frontend-admin/src/styles/vendors/_element-plus-override.scss` 文件
2. 修改固定列样式（第 153-157 行）
3. 修改斑马纹行样式（第 142-145 行）
4. 修改 glass-panel mixin 透明度（第 10-19 行）
5. 打开 `frontend-admin/src/views/dashboard/index.vue` 文件
6. 修改总用户数图标的viewBox和尺寸
7. 调整 `.stat-icon` 样式确保图标居中
8. 修改饼图和柱状图初始化，添加 devicePixelRatio 配置
9. 打开 `frontend-admin/index.html` 添加 Inter 字体引入
10. 打开 `frontend-admin/src/styles/base/_variables.scss` 添加字体变量
11. 打开 `frontend-admin/src/styles/base/_reset.scss` 应用字体
12. 保存所有文件
13. 测试修复效果

## 更新后的验证标准

1. 固定列不再遮挡其他列的内容
2. 隔行变色的表格行，文字清晰可读
3. 仪表盘总用户数图标完整显示，不再被截断
4. 饼状图和柱状图在高DPI屏幕上清晰显示
5. 导航栏、侧边栏玻璃效果更通透（0.35透明度）
6. 表格、弹窗等密集内容区域保持可读性（0.65+透明度）
7. 字体显示更清晰现代（Inter字体）
8. 暗黑模式下玻璃效果正常
9. 整体样式保持一致