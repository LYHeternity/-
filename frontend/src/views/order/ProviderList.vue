<template>
  <el-card>
    <h3>服务订单</h3>
    <el-table v-loading="loading" :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="amount" label="金额" width="100" />
      <el-table-column prop="description" label="需求说明" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'completed' ? 'success' : 'info'">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 'pending'" type="text" size="small" @click="audit(scope.row.id, true)">通过</el-button>
          <el-button v-if="scope.row.status === 'pending'" type="text" size="small" @click="audit(scope.row.id, false)">拒绝</el-button>
          <el-button v-if="scope.row.status === 'confirmed'" type="text" size="small" @click="setStatus(scope.row.id, 'serving')">开始服务</el-button>
          <el-button v-if="scope.row.status === 'serving'" type="text" size="small" @click="setStatus(scope.row.id, 'completed')">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:12px" :current-page="page.current" :page-size="page.size" :total="page.total" layout="total, prev, pager, next" @current-change="p => { page.current = p; load() }" />
  </el-card>
</template>

<script>
import { listProvider, audit as auditApi, setStatus } from '@/api/order'
export default {
  name: 'ProviderOrders',
  data() { return { loading: false, list: [], page: { current: 1, size: 10, total: 0 } } },
  created() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await listProvider({ current: this.page.current, size: this.page.size })
        this.list = res.records || []
        this.page.total = res.total || 0
      } finally { this.loading = false }
    },
    async audit(id, approved) {
      try {
        await auditApi(id, { approved, rejectReason: approved ? '' : '暂不接单' })
        this.$message.success(approved ? '已通过' : '已拒绝')
        this.load()
      } catch (e) { this.$message.error(e.response?.data?.message || '操作失败') }
    },
    async setStatus(id, status) {
      try {
        await setStatus(id, status)
        this.$message.success('已更新')
        this.load()
      } catch (e) { this.$message.error(e.response?.data?.message || '操作失败') }
    }
  }
}
</script>
