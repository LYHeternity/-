<template>
  <div class="login-container">
    <!-- 装饰性图形元素 -->
    <div class="decorative-elements">
      <div class="ellipse ellipse-1"></div>
      <div class="ellipse ellipse-2"></div>
      <div class="subtract-shape"></div>
      <div class="polygon polygon-1"></div>
      <div class="polygon polygon-2"></div>
      <div class="polygon polygon-3"></div>
    </div>

    <!-- 登录表单区域 -->
    <div class="login-form-container">
      <h1 class="login-title">LOGIN</h1>
      
      <el-form ref="form" :model="form" :rules="rules" label-width="0" size="large" class="login-form">
        <el-form-item prop="account">
          <div class="input-label">Username</div>
          <el-input 
            v-model="form.account" 
            placeholder="" 
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-label">Password</div>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="" 
            show-password 
            @keyup.enter.native="submit"
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="input-label">Captcha</div>
          <div class="captcha-container">
            <el-input 
              v-model="form.captcha" 
              placeholder="请输入验证码" 
              class="login-input captcha-input"
              @keyup.enter.native="submit"
            />
            <div class="captcha-image">
              <img :src="captchaUrl" @click="refreshCaptcha" alt="验证码" class="captcha-img" />
            </div>
          </div>
        </el-form-item>
        <div class="form-options">
          <el-checkbox v-model="form.remember" class="remember-checkbox">
            记住我
          </el-checkbox>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-button" 
            :loading="loading" 
            @click="submit"
          >
            login
          </el-button>
        </el-form-item>
        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import request from '@/utils/request'
export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      captchaUrl: '',
      form: { 
        account: '', 
        password: '',
        captcha: '',
        remember: false
      },
      rules: {
        account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.refreshCaptcha()
    // 从localStorage加载记住的账号
    const savedAccount = localStorage.getItem('rememberedAccount')
    if (savedAccount) {
      this.form.account = savedAccount
      this.form.remember = true
    }
  },
  methods: {
    ...mapActions('user', ['login']),
    async refreshCaptcha() {
      try {
        const res = await request({ url: '/auth/captcha', method: 'get' })
        console.log('验证码响应:', res)
        this.captchaUrl = res
      } catch (error) {
        console.error('获取验证码失败:', error)
        // 失败时设置默认验证码图片
        this.captchaUrl = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=='
      }
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await this.login(this.form)
          this.$message.success('登录成功')
          
          // 处理记住我功能
          if (this.form.remember) {
            localStorage.setItem('rememberedAccount', this.form.account)
          } else {
            localStorage.removeItem('rememberedAccount')
          }
          
          // 根据用户角色决定跳转目标
          const userRole = this.$store.state.user.role
          if (userRole === 'admin') {
            this.$router.push('/admin')
          } else {
            this.$router.push('/')
          }
        } catch (e) {
          this.$message.error(e.message || '登录失败')
          // 登录失败时刷新验证码
          this.refreshCaptcha()
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 登录容器 */
.login-container {
  position: relative;
  min-height: 100vh;
  background: #27262C;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
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



/* 登录表单容器 */
.login-form-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 500px;
  padding: 40px;
}

.login-title {
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

.login-form {
  margin-bottom: 32px;
}

.login-input {
  border-radius: 8px !important;
  height: 56px !important;
  font-size: 16px !important;
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  transition: all 0.3s ease !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.login-input:focus {
  border-color: #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(255, 255, 255, 0.1) !important;
}

.login-input::placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 深层选择器确保样式生效 */
.login-input >>> input {
  color: #FFFFFF !important;
  background: #444444 !important;
  box-shadow: none !important;
  border: 1px solid #666666 !important;
}

.login-input >>> input:focus {
  background: #555555 !important;
  box-shadow: none !important;
  border: 1px solid #666666 !important;
}

.login-input >>> input::placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 确保整个输入框组件有立体效果 */
.login-input >>> .el-input__inner {
  border: 1px solid #666666 !important;
  background: #444444 !important;
  color: #FFFFFF !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.login-input >>> .el-input__inner:focus {
  border: 1px solid #666666 !important;
  background: #555555 !important;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3) !important;
}

.captcha-container {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  display: flex;
  align-items: center;
}

.captcha-img {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #666666;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.remember-checkbox {
  font-size: 14px;
  color: #999999;
}

.remember-checkbox >>> .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #4044ED;
  border-color: #4044ED;
}

.forgot-password {
  font-size: 14px;
  color: #999999;
  text-decoration: none;
  font-weight: 500;
}

.forgot-password:hover {
  color: #FFFFFF;
  text-decoration: underline;
}

.login-button {
  width: 100%;
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

.login-button:hover {
  background: #3336C7;
  box-shadow: 0 6px 20px rgba(64, 68, 237, 0.4);
  transform: translateY(-2px);
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #999999;
}

.register-link {
  color: #FFFFFF;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}

.register-link:hover {
  text-decoration: underline;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-container {
    padding: 32px;
  }
  
  .login-title {
    font-size: 72px;
    margin-bottom: 40px;
  }
  
  .input-label {
    font-size: 20px;
  }
  
  .login-input,
  .login-button {
    height: 48px;
  }
  
  .login-button {
    font-size: 20px;
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
  .login-form-container {
    padding: 24px;
  }
  
  .login-title {
    font-size: 48px;
    margin-bottom: 32px;
  }
  
  .input-label {
    font-size: 18px;
  }
  
  .form-options {
    margin-bottom: 32px;
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