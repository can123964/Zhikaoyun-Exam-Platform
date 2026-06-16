# 修复表格固定列和滚动阻尼问题

## 问题1：表格固定列遮挡

### 根因
- 右侧固定列背景 `--brand-surface-solid` 是 `#ffffff`
- 在玻璃效果下，白色背景不够不透明，内容透出来了

### 修复方案
修改 `frontend-admin/src/styles/vendors/_element-plus-override.scss`

```scss
// 固定列阴影
.el-table-fixed-column--left,
.el-table-fixed-column--right {
  background: rgba(255, 255, 255, 0.95) !important;  // 更高不透明度
  z-index: 10;
}

.el-table-fixed-column--right {
  box-shadow: -4px 0 8px rgba(0, 0, 0, 0.08);  // 加深阴影
}
```

暗黑模式：
```scss
html.dark {
  .el-table-fixed-column--left,
  .el-table-fixed-column--right {
    background: rgba(44, 44, 46, 0.95) !important;
  }
}
```

## 问题2：滚动阻尼感

### 根因
- 侧边栏 `.layout-sidebar` 使用 `backdrop-filter: blur(40px)`
- 内容区滚动时，浏览器需要重新计算模糊效果，导致卡顿
- Windows 上 Chrome 的 backdrop-filter 性能尤其差

### 修复方案
修改 `frontend-admin/src/layouts/LeftLayout.vue`

将侧边栏的 `backdrop-filter: blur(40px)` 改为更轻量的效果：
```scss
.layout-sidebar {
  backdrop-filter: blur(20px) saturate(150%);  // 降低模糊半径
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  background: rgba(255, 255, 255, 0.72);  // 提高不透明度补偿
}
```

或者完全移除侧边栏的 backdrop-filter（推荐）：
```scss
.layout-sidebar {
  background: rgba(255, 255, 255, 0.85);  // 直接用高不透明度
  // 移除 backdrop-filter
}
```

## 修改文件清单

| 文件 | 修改内容 |
|------|----------|
| `_element-plus-override.scss` | 固定列背景改为 `rgba(255,255,255,0.95)` |
| `LeftLayout.vue` | 侧边栏 backdrop-filter 降低或移除 |

## 验证标准

1. 表格固定列不再透出后面的内容
2. 页面滚动流畅，无阻尼感
3. 暗黑模式下效果正常
