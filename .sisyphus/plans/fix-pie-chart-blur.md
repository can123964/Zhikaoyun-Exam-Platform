# 修复饼图模糊问题

## 问题描述

饼图和柱状图在高 DPI 屏幕上仍然模糊，即使已添加 `devicePixelRatio` 参数。

## 根因分析

1. **`backdrop-filter: blur(40px)`** 在 `.chart-card` 上导致 Canvas 子元素渲染模糊
   - `backdrop-filter` 创建新的合成层，影响 Canvas 的光栅化
   - 在某些浏览器中，这会导致 Canvas 以较低分辨率渲染

2. **ECharts 初始化时机问题**
   - `nextTick()` 只保证 DOM 更新，但 CSS 布局可能还没完全计算好
   - Canvas 尺寸可能不正确

## 修复方案

### 修改文件
`frontend-admin/src/views/dashboard/index.vue`

### 修改内容

1. **给图表容器添加 CSS 类隔离 backdrop-filter**
   - 在 `.chart-card__body` 的图表容器上添加 `isolation: isolate` 样式
   - 或者将 `backdrop-filter` 从 `.chart-card` 移除，只在非图表区域使用

2. **优化 ECharts 初始化时机**
   - 在 `initPieChart()` 和 `initTrendChart()` 中，初始化后立即调用 `resize()`
   - 使用双重 `nextTick` 确保布局完全计算

### 具体代码修改

#### 修改1: 图表容器添加 isolation 样式

在 `<style scoped>` 部分添加：

```scss
// 图表 Canvas 容器 - 防止 backdrop-filter 导致模糊
.chart-canvas-wrap {
  isolation: isolate;
  position: relative;
}
```

#### 修改2: 给图表 div 添加类名

模板中：
```html
<div ref="trendChartRef" class="chart-card__body chart-canvas-wrap"></div>
<div ref="pieChartRef" class="chart-card__body chart-canvas-wrap"></div>
```

#### 修改3: 优化初始化时机

在 `initPieChart()` 末尾添加 resize：
```typescript
// 初始化后确保尺寸正确
setTimeout(() => {
  pieChart?.resize();
}, 0);
```

在 `initTrendChart()` 末尾同样添加：
```typescript
setTimeout(() => {
  trendChart?.resize();
}, 0);
```

## 验证标准

1. 饼图在高 DPI 屏幕上清晰显示
2. 柱状图在高 DPI 屏幕上清晰显示
3. 窗口缩放后图表正常重绘
4. 暗黑模式下图表正常显示

## 相关文件

- `frontend-admin/src/views/dashboard/index.vue` - 仪表盘页面
