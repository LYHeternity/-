<template>
  <el-card>
    <h3>服务商工作台</h3>
    <p>欢迎回来，{{ $store.state.user.realName || $store.state.user.username }}。</p>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div>我的服务</div>
          <el-button type="text" @click="$router.push('/my-services')">查看</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div>发布服务</div>
          <el-button type="text" @click="$router.push('/service-publish')">去发布</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div>服务订单</div>
          <el-button type="text" @click="$router.push('/provider-orders')">查看</el-button>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
export default { name: 'ProviderDashboard' }
</script>
