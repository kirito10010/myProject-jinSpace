<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1 class="login-title">欢迎回来</h1>
        <p class="login-subtitle">登录您的账号继续访问</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0" class="login-form">
        <el-form-item prop="usernameOrEmail">
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input
              v-model="loginForm.usernameOrEmail"
              placeholder="用户名或邮箱"
              class="custom-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              class="custom-input"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <el-button link class="register-link" @click="goToRegister">立即注册</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginApi } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  usernameOrEmail: '',
  password: ''
})

const rules = {
  usernameOrEmail: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const result = await loginApi({
      usernameOrEmail: loginForm.usernameOrEmail,
      password: loginForm.password
    })
    
    if (result.success) {
      ElMessage.success('登录成功')
      localStorage.setItem('userInfo', JSON.stringify(result.data))
      router.push('/home')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error: any) {
    ElMessage.error('登录失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* 登录容器 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/login-background/login-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;
}

/* 背景遮罩层 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 21, 41, 0.7), rgba(0, 30, 60, 0.9));
}

/* 登录盒子 */
.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 登录盒子 */
  .login-box {
    width: 90%;
    max-width: 350px;
    padding: 30px 25px;
  }
  
  /* 登录标题 */
  .login-title {
    font-size: 26px;
  }
  
  /* 登录副标题 */
  .login-subtitle {
    font-size: 14px;
  }
  
  /* 输入框容器 */
  .input-wrapper :deep(.el-input__wrapper) {
    height: 50px;
    font-size: 15px;
  }
  
  /* 输入框图标 */
  .input-icon {
    font-size: 18px;
    left: 15px;
  }
  
  /* 登录按钮 */
  .login-btn {
    height: 50px;
    font-size: 16px;
  }
  
  /* 底部链接 */
  .login-footer {
    font-size: 14px;
  }
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.4);
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #3498db, #9b59b6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
}

/* 登录表单 */
.login-form {
  margin-bottom: 25px;
  width: 100%;
}

/* 确保表单项内容居中 */
.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
}

.login-form :deep(.el-form-item__content) {
  margin-left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  width: 100%;
  max-width: 100%;
}

/* 输入框图标 */
.input-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  color: #3498db;
  font-size: 20px;
  z-index: 10;
}

/* 自定义输入框 */
.custom-input {
  width: 100%;
}

.input-wrapper :deep(.el-input) {
  width: 100%;
}

.input-wrapper :deep(.el-input__wrapper) {
  width: 100%;
  height: 55px;
  padding-left: 55px;
  border-radius: 12px;
  border: 2px solid #ecf0f1;
  font-size: 16px;
  color: #2c3e50;
  background-color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.input-wrapper :deep(.el-input__wrapper):focus-within {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  background-color: #fff;
}

.input-wrapper :deep(.el-input__inner) {
  width: 100%;
  height: 100%;
  border: none;
  padding: 0;
  font-size: 16px;
  color: #2c3e50;
  background-color: transparent;
}

.input-wrapper :deep(.el-input__inner:focus) {
  box-shadow: none;
  outline: none;
}

.input-wrapper :deep(.el-input__inner::placeholder) {
  color: #bdc3c7;
  font-size: 15px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 55px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3498db, #9b59b6);
  border: none;
  color: white;
  box-shadow: 0 8px 25px rgba(52, 152, 219, 0.4);
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: linear-gradient(135deg, #2980b9, #8e44ad);
  box-shadow: 0 12px 35px rgba(52, 152, 219, 0.6);
  transform: translateY(-2px);
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.login-btn:disabled {
  background: #bdc3c7;
  box-shadow: none;
  transform: none;
}

/* 底部链接 */
.login-footer {
  text-align: center;
  font-size: 15px;
  color: #7f8c8d;
}

.register-link {
  color: #3498db !important;
  font-weight: 600;
  transition: all 0.3s ease;
  padding: 0 5px;
}

.register-link:hover {
  color: #2980b9 !important;
}

/* Element Plus 样式覆盖 */
:deep(.el-input__wrapper) {
  border: none;
  box-shadow: none;
}

:deep(.el-input__inner) {
  border: none;
  background: transparent;
  padding-left: 0;
}

:deep(.el-input__inner:focus) {
  box-shadow: none;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #3498db, #9b59b6);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2980b9, #8e44ad);
}
</style>