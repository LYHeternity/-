<template>
  <el-card>
    <h3>创业者工作台</h3>
    <p>欢迎回来，{{ $store.state.user.realName || $store.state.user.username }}。</p>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div>我的项目</div>
          <el-button type="text" @click="$router.push('/my-projects')">查看</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div>发布项目</div>
          <el-button type="text" @click="$router.push('/project-publish')">去发布</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div>我的订单</div>
          <el-button type="text" @click="$router.push('/my-orders')">查看</el-button>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
export default { name: 'EntrepreneurDashboard' }
</script>
