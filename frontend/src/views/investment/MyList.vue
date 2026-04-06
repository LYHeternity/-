<template>
  <el-card>
    <h3>我的投资意向</h3>
    <el-table v-loading="loading" :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="amount" label="投资金额(万)" width="120" />
      <el-table-column prop="duration" label="期限" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'approved' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'info'">
            {{ scope.row.status === 'pending' ? '待审核' : scope.row.status === 'approved' ? '已通过' : scope.row.status === 'rejected' ? '已拒绝' : scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="160" />
    </el-table>
    <el-pagination style="margin-top:12px" :current-page="page.current" :page-size="page.size" :total="page.total" layout="total, prev, pager, next" @current-change="p => { page.current = p; load() }" />
  </el-card>
</template>

<script>
import { listMy } from '@/api/investment'
export default {
  name: 'MyInvestments',
  data() { return { loading: false, list: [], page: { current: 1, size: 10, total: 0 } } },
  created() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await listMy({ current: this.page.current, size: this.page.size })
        this.list = res.records || []
        this.page.total = res.total || 0
      } finally { this.loading = false }
    }
  }
}
</script>
