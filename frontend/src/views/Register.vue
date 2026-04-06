<template>
  <div class="register-page">
    <!-- 装饰性图形元素 -->
    <div class="decorative-elements">
      <div class="ellipse ellipse-1"></div>
      <div class="ellipse ellipse-2"></div>
      <div class="subtract-shape"></div>
      <div class="polygon polygon-1"></div>
      <div class="polygon polygon-2"></div>
      <div class="polygon polygon-3"></div>
    </div>

    <!-- 注册表单区域 -->
    <div class="register-form-container">
      <h1 class="register-title">REGISTER</h1>
      
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" size="large" class="register-form">
        <el-form-item prop="role">
          <div class="input-label">角色</div>
          <el-select v-model="form.role" placeholder="请选择角色" style="width:100%" class="register-select">
            <el-option label="创业者" value="entrepreneur" />
            <el-option label="投资人" value="investor" />
            <el-option label="服务商" value="provider" />
          </el-select>
        </el-form-item>
        <el-form-item prop="username">
          <div class="input-label">用户名</div>
          <el-input v-model="form.username" placeholder="唯一标识" class="register-input" />
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-label">密码</div>
          <el-input v-model="form.password" type="password" placeholder="≥8位，含大小写+数字+特殊字符" show-password class="register-input" />
        </el-form-item>
        <el-form-item prop="realName">
          <div class="input-label">真实姓名</div>
          <el-input v-model="form.realName" class="register-input" />
        </el-form-item>
        <el-form-item prop="phone">
          <div class="input-label">手机号</div>
          <el-input v-model="form.phone" placeholder="选填" class="register-input" />
        </el-form-item>
        <el-form-item prop="email">
          <div class="input-label">邮箱</div>
          <el-input v-model="form.email" placeholder="选填" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'entrepreneur' || form.role === 'investor'">
          <div class="input-label">领域</div>
          <el-input v-model="form.industry" placeholder="创业/投资领域" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'investor'">
          <div class="input-label">投资额度</div>
          <el-input v-model="form.investmentAmount" placeholder="如 100-500万" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'provider'">
          <div class="input-label">企业名称</div>
          <el-input v-model="form.companyName" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'provider'">
          <div class="input-label">联系人</div>
          <el-input v-model="form.contactPerson" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'provider'">
          <div class="input-label">服务类型</div>
          <el-input v-model="form.serviceType" placeholder="如 技术开发" class="register-input" />
        </el-form-item>
        <el-form-item v-if="form.role === 'provider'">
          <div class="input-label">服务介绍</div>
          <el-input v-model="form.serviceIntro" type="textarea" rows="3" class="register-input register-textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-button" :loading="loading" @click="submit">
            注册
          </el-button>
          <router-link to="/login" class="back-login">已有账号？去登录</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'Register',
  data() {
    return {
      loading: false,
      form: {
        username: '', password: '', realName: '', phone: '', email: '', role: 'entrepreneur',
        industry: '', investmentAmount: '', companyName: '', contactPerson: '', serviceType: '', serviceIntro: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 8, message: '至少8位', trigger: 'blur' },
          { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/, message: '需含大小写字母、数字、特殊字符', trigger: 'blur' }
        ],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  mounted() {
    // 动态添加下拉菜单样式，只应用到注册页面的下拉框
    const style = document.createElement('style')
    style.textContent = `
      /* 只应用到注册页面的下拉框 */
      .register-page .el-select-dropdown {
        background: #444444 !important;
        border: 1px solid #666666 !important;
        z-index: 9999 !important;
        border-radius: 8px !important;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
      }
      .register-page .el-select-dropdown__item {
        color: #FFFFFF !important;
        background: #444444 !important;
        padding: 16px 20px !important;
        transition: background-color 0.3s ease !important;
        text-align: center !important;
        font-size: 16px !important;
        line-height: 1.2 !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        height: 56px !important;
      }
      .register-page .el-select-dropdown__item.hover, 
      .register-page .el-select-dropdown__item:hover {
        background: #555555 !important;
      }
      .register-page .el-select-dropdown__item.selected {
        background: #555555 !important;
        color: #FFFFFF !important;
      }
      .register-page .el-popper {
        background: #444444 !important;
        border: 1px solid #666666 !important;
        border-radius: 8px !important;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
      }
      .register-page .el-popper .el-select-dropdown {
        background: #444444 !important;
        border: none !important;
        border-radius: 8px !important;
      }
      .register-page .el-popper .el-select-dropdown__item {
        color: #FFFFFF !important;
        background: #444444 !important;
        padding: 16px 20px !important;
        transition: background-color 0.3s ease !important;
        text-align: center !important;
        font-size: 16px !important;
        line-height: 1.2 !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        height: 56px !important;
      }
      .register-page .el-popper .el-select-dropdown__item.hover, 
      .register-page .el-popper .el-select-dropdown__item:hover {
        background: #555555 !important;
      }
      .register-page .el-popper .el-select-dropdown__item.selected {
        background: #555555 !important;
        color: #FFFFFF !important;
      }
    `
    document.head.appendChild(style)
    this.styleElement = style
  },
  beforeUnmount() {
    // 清理动态添加的样式
    if (this.styleElement) {
      document.head.removeChild(this.styleElement)
    }
  },
  methods: {
    ...mapActions('user', ['register']),
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await this.register(this.form)
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          this.$message.error(e.message || '注册失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 注册页面容器 */
.register-page {
  position: relative;
  min-height: 100vh;
  background: #27262C;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 24px;
}

/* 装饰性图形元素 */
.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.ellipse {
  border-radius: 50%;
  position: absolute;
}

.ellipse-1 {
  width: 914px;
  height: 914px;
  background: #4044ED;
  top: 102px;
  left: -585px;
}

.ellipse-2 {
  width: 76px;
  height: 76px;
  background: #93DFFF;
  top: 167px;
  left: 118px;
}

.subtract-shape {
  position: absolute;
  width: 1471.67px;
  height: 1471.67px;
  background: #4044ED;
  top: -898px;
  left: 1098px;
  border-radius: 50%;
  clip-path: polygon(0 0, 100% 0, 100% 50%, 0 50%);
}

.polygon {
  position: absolute;
  width: 475.81px;
  height: 475.81px;
  background: rgba(64, 68, 237, 0.5);
  clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
}

.polygon-1 {
  top: 685px;
  left: 1248px;
}

.polygon-2 {
  top: 779.07px;
  left: 1353.14px;
}

.polygon-3 {
  top: 871.89px;
  left: 1462.24px;
}



/* 注册表单容器 */
.register-form-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 700px;
  padding: 40px;
}

.register-title {
  font-family: 'JK Abode', sans-serif;
  font-size: 96px;
  font-weight: 400;
  color: #FFFFFF;
  margin-bottom: 60px;
  line-height: 1em;
}

.input-label {
  font-family: 'IRANSansXFaNum', sans-serif;
  font-size: 24px;
  font-weight: 400;
  color: #999999;
  margin-bottom: 8px;
  line-height: 1.5em;
}

.register-form {
  margin-bottom: 32px;
}

.register-input {
  border-radius: 8px !important;
  height: 56px !important;
  font-size: 16px !important;
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  transition: all 0.3s ease !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-input:focus {
  border-color: #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-input::placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 深层选择器确保样式生效 */
.register-input >>> input {
  color: #FFFFFF !important;
  background: #444444 !important;
  box-shadow: none !important;
  border: 1px solid #666666 !important;
}

.register-input >>> input:focus {
  background: #555555 !important;
  box-shadow: none !important;
  border: 1px solid #666666 !important;
}

.register-input >>> input::placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 确保整个输入框组件有立体效果 */
.register-input >>> .el-input__inner {
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.register-input >>> .el-input__inner:focus {
  border: 1px solid #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

/* 确保下拉选择框有立体效果 */
.register-select /deep/ .el-select__inner {
  border-radius: 8px !important;
  height: 56px !important;
  font-size: 16px !important;
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  transition: all 0.3s ease !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-select /deep/ .el-select__inner:focus {
  border-color: #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

/* 确保选择器的输入框部分样式正确 */
.register-select /deep/ .el-input {
  border-radius: 8px !important;
  height: 56px !important;
  font-size: 16px !important;
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  transition: all 0.3s ease !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-select /deep/ .el-input:focus {
  border-color: #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

/* 确保选择器的输入框内部元素样式正确 */
.register-select /deep/ .el-input__inner {
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.register-select /deep/ .el-input__inner:focus {
  border: 1px solid #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.register-select /deep/ .el-select__placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

.register-select /deep/ .el-select__caret {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 为下拉菜单添加更高的z-index */
.el-select-dropdown {
  background: #444444 !important;
  border: 1px solid #666666 !important;
  z-index: 9999 !important;
}

.el-select-dropdown__item {
  color: #FFFFFF !important;
  background: #444444 !important;
}

.el-select-dropdown__item.hover, .el-select-dropdown__item:hover {
  background: #555555 !important;
}

.register-select {
  border-radius: 8px !important;
  height: 56px !important;
  font-size: 16px !important;
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  transition: all 0.3s ease !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-select:focus {
  border-color: #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.register-textarea {
  height: 120px !important;
  resize: vertical;
}

/* 全局样式，确保下拉菜单样式正确 */
:global(.el-select-dropdown) {
  background: #444444 !important;
  border: 1px solid #666666 !important;
  z-index: 9999 !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
}

:global(.el-select-dropdown__item) {
  color: #FFFFFF !important;
  background: #444444 !important;
  padding: 12px 16px !important;
  transition: background-color 0.3s ease !important;
}

:global(.el-select-dropdown__item.hover), 
:global(.el-select-dropdown__item:hover) {
  background: #555555 !important;
}

:global(.el-select-dropdown__item.selected) {
  background: #555555 !important;
  color: #FFFFFF !important;
}

/* 确保下拉菜单的容器样式正确 */
:global(.el-popper) {
  background: #444444 !important;
  border: 1px solid #666666 !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
}

:global(.el-popper .el-select-dropdown) {
  background: #444444 !important;
  border: none !important;
  border-radius: 8px !important;
}

:global(.el-popper .el-select-dropdown__item) {
  color: #FFFFFF !important;
  background: #444444 !important;
}

:global(.el-popper .el-select-dropdown__item.hover), 
:global(.el-popper .el-select-dropdown__item:hover) {
  background: #555555 !important;
}

:global(.el-popper .el-select-dropdown__item.selected) {
  background: #555555 !important;
  color: #FFFFFF !important;
}

.register-button {
  width: 200px;
  height: 56px;
  font-size: 24px;
  font-weight: 400;
  border-radius: 8px;
  background: #4044ED;
  border: none;
  color: #FFFFFF;
  transition: all 0.3s ease;
  font-family: 'IRANSansXFaNum', sans-serif;
}

.register-button:hover {
  background: #3336C7;
  box-shadow: 0 6px 20px rgba(64, 68, 237, 0.4);
  transform: translateY(-2px);
}

.back-login {
  display: inline-block;
  margin-left: 24px;
  color: #FFFFFF;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  line-height: 56px;
}

.back-login:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form-container {
    padding: 32px;
  }
  
  .register-title {
    font-size: 72px;
    margin-bottom: 40px;
  }
  
  .input-label {
    font-size: 20px;
  }
  
  .register-input,
  .register-select,
  .register-button {
    height: 48px;
  }
  
  .register-button {
    font-size: 20px;
    width: 160px;
  }
  
  .back-login {
    font-size: 14px;
    line-height: 48px;
    margin-left: 16px;
  }
  
  .ellipse-1 {
    width: 700px;
    height: 700px;
    top: 50px;
    left: -400px;
  }
  
  .polygon {
    width: 300px;
    height: 300px;
  }
  
  .polygon-1 {
    top: 600px;
    left: 700px;
  }
  
  .polygon-2 {
    top: 650px;
    left: 800px;
  }
  
  .polygon-3 {
    top: 700px;
    left: 900px;
  }
}

@media (max-width: 480px) {
  .register-page {
    padding: 24px;
  }
  
  .register-form-container {
    padding: 24px;
  }
  
  .register-title {
    font-size: 48px;
    margin-bottom: 32px;
  }
  
  .input-label {
    font-size: 18px;
  }
  
  .register-input,
  .register-select,
  .register-button {
    height: 44px;
  }
  
  .register-button {
    font-size: 18px;
    width: 140px;
  }
  
  .back-login {
    font-size: 14px;
    line-height: 44px;
    margin-left: 12px;
  }
  
  .ellipse-1 {
    width: 500px;
    height: 500px;
    top: 30px;
    left: -300px;
  }
  
  .polygon {
    width: 200px;
    height: 200px;
  }
  
  .polygon-1 {
    top: 500px;
    left: 300px;
  }
  
  .polygon-2 {
    top: 550px;
    left: 350px;
  }
  
  .polygon-3 {
    top: 600px;
    left: 400px;
  }
  

}
</style>
