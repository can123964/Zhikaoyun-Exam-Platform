# 主页活动列表改成分页形式

## 当前实现

- 使用 "加载更多" 按钮，每次追加 10 条数据
- 状态变量：`activityPage`, `activityTotal`, `activityLoading`, `activityPageSize`, `activityHasMore`
- API：`DashboardAPI.getActivity(page, pageSize)` 已支持分页

## 目标

将 "加载更多" 改为 Element Plus 分页组件，支持页码切换。

## 修改文件

`frontend-admin/src/views/dashboard/index.vue`

## 修改内容

### 1. 模板修改

替换活动列表底部的 "加载更多" 区域（第164-169行）为分页组件：

```html
<!-- 替换前 -->
<div v-if="activityHasMore" class="activity-load-more">
  <button class="load-more-btn" :disabled="activityLoading" @click="loadMoreActivity">
    {{ activityLoading ? '加载中...' : '加载更多' }}
  </button>
  <span class="activity-count">已显示 {{ activities.length }} / {{ activityTotal }} 条</span>
</div>

<!-- 替换后 -->
<div v-if="activityTotal > 0" class="activity-pagination">
  <el-pagination
    v-model:current-page="activityPage"
    v-model:page-size="activityPageSize"
    :page-sizes="[10, 20, 30]"
    :total="activityTotal"
    layout="total, sizes, prev, pager, next"
    background
    @current-change="handleActivityPageChange"
    @size-change="handleActivitySizeChange"
  />
</div>
```

### 2. 脚本修改

#### 2.1 删除不再需要的变量和函数

删除：
- `activityHasMore` computed 属性
- `loadMoreActivity()` 函数

#### 2.2 修改 `fetchActivity()` 函数

```typescript
async function fetchActivity(): Promise<void> {
  try {
    const res = await DashboardAPI.getActivity(activityPage.value, activityPageSize.value);
    activities.value = res.list || [];
    activityTotal.value = res.total || 0;
  } catch {
    activities.value = [];
  }
}
```

#### 2.3 新增分页处理函数

```typescript
function handleActivityPageChange(page: number) {
  activityPage.value = page;
  fetchActivity();
}

function handleActivitySizeChange(size: number) {
  activityPageSize.value = size;
  activityPage.value = 1;
  fetchActivity();
}
```

### 3. 样式修改

替换 `.activity-load-more` 样式为 `.activity-pagination`：

```scss
.activity-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  margin-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}
```

## 验证标准

1. 分页组件正确显示
2. 点击页码切换正常加载对应页数据
3. 切换每页条数后回到第1页
4. 总条数正确显示
5. 暗黑模式下分页样式正常

## 相关文件

- `frontend-admin/src/views/dashboard/index.vue` - 仪表盘页面
- `frontend-admin/src/components/Pagination/index.vue` - 分页组件（参考用）
