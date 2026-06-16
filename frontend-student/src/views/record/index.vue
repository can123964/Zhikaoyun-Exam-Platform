<template>
  <div class="app-contain">
    <el-row :gutter="24">
      <el-col :span="24" :md="18">
        <el-card class="record-table-card">
          <div slot="header" class="record-card-header">
            <span>考试记录</span>
          </div>
          <el-table v-loading="listLoading" :data="tableData" fit highlight-current-row style="width: 100%" @row-click="itemSelect">
            <el-table-column prop="examTitle" label="考试名称" />
            <el-table-column label="状态" prop="status" width="120px">
              <template slot-scope="{row}">
                <el-tag :type="statusTagFormatter(row.status)">
                  {{ statusTextFormatter(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalScore" label="得分" width="80px" />
            <el-table-column prop="startTime" label="考试时间" width="170" />
            <el-table-column align="right" width="100">
              <template slot-scope="{row}">
                <el-button v-if="row.status === 1 || row.status === 2" type="text" size="small" @click.stop="viewDetail(row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :background="false" :page.sync="queryParam.page" :limit.sync="queryParam.size"
                      @pagination="search" style="margin-top: 20px"/>
        </el-card>
      </el-col>
      <el-col :span="24" :md="6" class="detail-col">
        <el-card class="record-answer-info">
          <div slot="header" class="record-card-header">
            <span>答题详情</span>
          </div>
          <div class="detail-list">
            <div class="detail-row">
              <span class="detail-label">考试名称</span>
              <span class="detail-value">{{ selectItem.examTitle }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">状态</span>
              <span class="detail-value">
                <el-tag :type="statusTagFormatter(selectItem.status)" size="small">
                  {{ statusTextFormatter(selectItem.status) }}
                </el-tag>
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">得分</span>
              <span class="detail-value">{{ selectItem.totalScore }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">及格分</span>
              <span class="detail-value">{{ selectItem.passScore }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">考试时长</span>
              <span class="detail-value">{{ selectItem.duration }}分钟</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">开始时间</span>
              <span class="detail-value">{{ selectItem.startTime }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">提交时间</span>
              <span class="detail-value">{{ selectItem.submitTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import examPaperAnswerApi from '@/api/examPaperAnswer'
export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        page: 1,
        size: 10
      },
      listLoading: false,
      tableData: [],
      total: 0,
      selectItem: {
        examTitle: '',
        status: null,
        totalScore: '',
        passScore: '',
        duration: '',
        startTime: '',
        submitTime: ''
      }
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      let _this = this
      examPaperAnswerApi.pageList(this.queryParam).then(re => {
        let data = re.data
        _this.tableData = data.records || []
        _this.total = data.total
        _this.listLoading = false
      }).catch(() => {
        _this.listLoading = false
        _this.$message.error('获取考试记录失败')
      })
    },
    itemSelect (row, column, event) {
      this.selectItem = row
    },
    viewDetail (recordId) {
      this.$router.push({ path: '/read', query: { recordId: recordId } })
    },
    statusTagFormatter (status) {
      let map = { 0: 'warning', 1: 'success', 2: 'danger' }
      return map[status] || 'info'
    },
    statusTextFormatter (status) {
      let map = { 0: '进行中', 1: '已提交', 2: '超时自动交卷' }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.app-contain {
  padding-top: 20px;
}

.record-table-card {
  border-radius: var(--glass-radius);
  overflow: hidden;
  backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(var(--glass-saturate));
}

.record-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 15px;
  color: var(--brand-slate);
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-label {
  font-size: 12px;
  color: var(--brand-light);
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: var(--brand-dark);
  font-weight: 600;
}

@media (max-width: 768px) {
  .detail-col {
    display: none;
  }
}
</style>
