# 修复记录

## 2026-06-12 完成全部修复

### 修改的文件清单

| 文件 | 修改内容 |
|------|----------|
| `_element-plus-override.scss` | 固定列z-index+阴影、斑马纹0.02→0.04、glass-panel 0.55→0.35、glass-panel-heavy 0.78→0.65 |
| `_variables.scss` | light/dark glass-bg/heavy透明度降低、新增`--font-family-primary` |
| `dashboard/index.vue` | 用户图标SVG viewBox 20→24（Feather标准路径）、柱状图+饼图添加devicePixelRatio |
| `index.html` | 添加Inter字体Google Fonts链接 |
| `_reset.scss` | body和--el-font-family引用`var(--font-family-primary)` |

### Glass分层透明度策略

| 区域 | 透明度 | 说明 |
|------|--------|------|
| 导航/侧边栏 | 0.35 | 更通透 |
| 卡片容器 | 0.35 | 适度透明 |
| 下拉框/弹出层 | 0.65 | 保持可读性 |
| 表格 | 0.55 (未改) | 内容密集区域 |
| 对话框/抽屉 | 0.95 (未改) | 最高可读性 |

### 关键发现

- 第4个子agent卡住了，原因是parallel dispatch过多导致部分task未正确执行
- 直接Edit比delegate更高效（简单修改无需subagent）
- TypeScript编译错误均为已有（class/index.vue和exam/index.vue），与本次修改无关
- 用户图标截断根因：SVG从24x24缩小到20x20时未调整路径，导致头部被裁切
- 饼图模糊根因：echarts.init()缺少devicePixelRatio参数
