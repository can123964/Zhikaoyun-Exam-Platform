<template>
  <div class="wrongbook-page app-contain">
    <el-card class="wrongbook-container" shadow="hover">
      <div slot="header" class="wrongbook-header">
        <span>错题本</span>
        <div class="header-filters">
          <el-select v-model="queryParam.type" placeholder="题型" clearable size="small" style="width: 100px" @change="search">
            <el-option label="单选" :value="0" />
            <el-option label="多选" :value="1" />
            <el-option label="判断" :value="2" />
          </el-select>
          <el-select v-model="queryParam.mastered" placeholder="状态" clearable size="small" style="width: 100px; margin-left: 8px" @change="search">
            <el-option label="未掌握" :value="0" />
            <el-option label="已掌握" :value="1" />
          </el-select>
        </div>
      </div>

      <el-row :gutter="16">
        <el-col :span="14">
          <div class="card-grid" v-loading="listLoading">
            <div
              v-for="item in tableData"
              :key="item.id"
              class="wrong-card"
              :class="{ 'is-selected': selectItem.id === item.id }"
              @click="itemSelect(item)"
            >
              <div class="card-top">
                <el-tag size="mini" :type="questionTypeTag(item.type)">{{ questionTypeText(item.type) }}</el-tag>
                <el-tag v-if="item.mastered" size="mini" type="success" style="margin-left: 4px">已掌握</el-tag>
                <el-tag v-else size="mini" type="danger" style="margin-left: 4px">未掌握</el-tag>
                <span class="wrong-count">错 {{ item.wrongCount }} 次</span>
              </div>
              <div class="card-body">
                <p class="card-content">{{ stripHtml(item.content) }}</p>
              </div>
              <div class="card-actions">
                <el-button type="text" size="mini" @click.stop="markMastered(item.id)" v-if="!item.mastered">标记掌握</el-button>
                <el-button type="text" size="mini" style="color: #ef4444" @click.stop="deleteItem(item.id)">删除</el-button>
              </div>
            </div>
            <el-empty v-if="!listLoading && tableData.length === 0" description="暂无错题" :image-size="80" />
          </div>
          <pagination
            v-show="total > 0"
            :total="total"
            :background="false"
            :page.sync="queryParam.page"
            :limit.sync="queryParam.size"
            @pagination="search"
            style="margin-top: 16px"
          />
        </el-col>

        <el-col :span="10">
          <el-card class="detail-card" shadow="none">
            <div v-if="selectItem.questionId">
              <div class="detail-header">
                <el-tag size="small" :type="questionTypeTag(selectItem.type)">{{ questionTypeText(selectItem.type) }}</el-tag>
                <span style="margin-left: 8px; font-weight: 600; color: var(--brand-dark)">答题详情</span>
              </div>
              <div class="detail-content">{{ selectItem.content }}</div>
              <div class="detail-options" v-if="selectItem.type !== 2">
                <div v-if="selectItem.optionA" class="option-item" :class="{ 'option-correct': (selectItem.correctAnswer || '').indexOf('A') > -1 }">
                  <span class="option-label">A.</span> {{ selectItem.optionA }}
                </div>
                <div v-if="selectItem.optionB" class="option-item" :class="{ 'option-correct': (selectItem.correctAnswer || '').indexOf('B') > -1 }">
                  <span class="option-label">B.</span> {{ selectItem.optionB }}
                </div>
                <div v-if="selectItem.optionC" class="option-item" :class="{ 'option-correct': (selectItem.correctAnswer || '').indexOf('C') > -1 }">
                  <span class="option-label">C.</span> {{ selectItem.optionC }}
                </div>
                <div v-if="selectItem.optionD" class="option-item" :class="{ 'option-correct': (selectItem.correctAnswer || '').indexOf('D') > -1 }">
                  <span class="option-label">D.</span> {{ selectItem.optionD }}
                </div>
              </div>
              <div class="detail-result">
                <div class="result-row">
                  <span class="result-label">你的答案：</span>
                  <span class="result-value result-wrong">{{ selectItem.myAnswer || '未作答' }}</span>
                </div>
                <div class="result-row">
                  <span class="result-label">正确答案：</span>
                  <span class="result-value result-correct">{{ selectItem.correctAnswer }}</span>
                </div>
                <div class="result-row" v-if="selectItem.explanation">
                  <span class="result-label">解析：</span>
                  <span class="result-explain">{{ selectItem.explanation }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="请选择一道错题查看" :image-size="60" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import questionAnswerApi from '@/api/questionAnswer'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        page: 1,
        size: 6,
        type: undefined,
        mastered: undefined
      },
      listLoading: false,
      tableData: [],
      total: 0,
      selectItem: {}
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      let _this = this
      questionAnswerApi.pageList(this.queryParam).then(re => {
        let data = re.data
        _this.tableData = data.records || []
        _this.total = data.total
        _this.listLoading = false
        if (_this.tableData.length > 0) {
          _this.selectItem = _this.tableData[0]
        } else {
          _this.selectItem = {}
        }
      }).catch(() => {
        _this.listLoading = false
        _this.$message.error('获取错题列表失败')
      })
    },
    itemSelect (row) {
      this.selectItem = row
    },
    questionTypeText (type) {
      return { 0: '单选', 1: '多选', 2: '判断' }[type] || '未知'
    },
    questionTypeTag (type) {
      return { 0: '', 1: 'warning', 2: 'success' }[type] || 'info'
    },
    stripHtml (html) {
      if (!html) return ''
      var div = document.createElement('div')
      div.innerHTML = html
      return div.textContent || div.innerText || ''
    },
    markMastered (id) {
      let _this = this
      this.$confirm('确认标记为已掌握？', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        questionAnswerApi.markMastered(id).then(() => {
          _this.$message.success('标记成功')
          _this.search()
        })
      }).catch(() => { /* 用户取消确认框 */ })
    },
    deleteItem (id) {
      let _this = this
      this.$confirm('确认删除该错题记录？', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        questionAnswerApi.deleteById(id).then(() => {
          _this.$message.success('删除成功')
          _this.search()
        })
      }).catch(() => { /* 用户取消确认框 */ })
    }
  }
}
</script>

<style scoped>
.wrongbook-page {
  max-width: 1200px;
  margin: 0 auto;
}

.wrongbook-container {
  min-height: 400px;
}

.wrongbook-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 700;
  color: var(--brand-dark, #0f172a);
}

.header-filters {
  display: flex;
}

.card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  min-height: 320px;
}

.wrong-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 14px 16px;
  animation: fadeInUp 0.35s ease both;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  display: flex;
  flex-direction: column;
  height: 130px;
}

.wrong-card:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
}

.wrong-card:nth-child(1) { animation-delay: 0.02s; }
.wrong-card:nth-child(2) { animation-delay: 0.06s; }
.wrong-card:nth-child(3) { animation-delay: 0.10s; }
.wrong-card:nth-child(4) { animation-delay: 0.14s; }
.wrong-card:nth-child(5) { animation-delay: 0.18s; }
.wrong-card:nth-child(6) { animation-delay: 0.22s; }

.wrong-card.is-selected {
  border-color: #007AFF;
  background: rgba(0, 122, 255, 0.1);
}

.card-top {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.wrong-count {
  margin-left: auto;
  font-size: 12px;
  color: var(--brand-lighter, #cbd5e1);
}

.card-body {
  flex: 1;
  overflow: hidden;
  margin-bottom: 6px;
}

.card-content {
  margin: 0;
  font-size: 13px;
  color: var(--brand-dark, #0f172a);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-actions {
  flex-shrink: 0;
  display: flex;
  gap: 8px;
  border-top: 1px solid var(--brand-border, #e2e8f0);
  padding-top: 6px;
}

.card-actions .el-button--text {
  font-size: 12px;
  padding: 0;
}

.detail-card {
  border-radius: 12px;
}

.detail-header {
  margin-bottom: 12px;
}

.detail-content {
  font-size: 14px;
  color: var(--brand-dark, #0f172a);
  line-height: 1.8;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.detail-options {
  margin-bottom: 12px;
}

.option-item {
  padding: 8px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--brand-muted, #64748b);
  background: #ffffff;
  border: 1px solid var(--brand-border, #e2e8f0);
}

.option-item.option-correct {
  background: rgba(0, 122, 255, 0.08);
  border-color: rgba(0, 122, 255, 0.3);
  color: #007AFF;
}

.option-label {
  font-weight: 600;
  margin-right: 4px;
}

.detail-result {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.result-row {
  margin-bottom: 8px;
  font-size: 13px;
  line-height: 1.6;
}

.result-row:last-child {
  margin-bottom: 0;
}

.result-label {
  color: var(--brand-light, #94a3b8);
  font-weight: 600;
}

.result-value {
  font-weight: 600;
}

.result-wrong {
  color: #b45309;
}

.result-correct {
  color: #3d7a3d;
}

.result-explain {
  color: var(--brand-light, #94a3b8);
  line-height: 1.8;
  display: block;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .wrongbook-page {
    padding: 0 12px;
  }
  .card-grid {
    grid-template-columns: 1fr;
  }
  .wrong-card {
    height: 110px;
  }
  .el-col:last-child {
    display: none;
  }
}
</style>
