<template>
  <el-card>
    <h3>投资人工作台</h3>
    <p>欢迎回来，{{ $store.state.user.realName || $store.state.user.username }}。</p>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div>浏览项目</div>
          <el-button type="text" @click="$router.push('/projects')">去浏览</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div>我的投资意向</div>
          <el-button type="text" @click="$router.push('/my-investments')">查看</el-button>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
export default { name: 'InvestorDashboard' }
</script>
