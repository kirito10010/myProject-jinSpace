<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1 class="register-title">创建账号</h1>
        <p class="register-subtitle">注册您的账号开始使用</p>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="0" class="register-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              class="custom-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              class="custom-input"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              class="custom-input"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="nickname">
          <div class="input-wrapper">
            <el-icon class="input-icon"><UserFilled /></el-icon>
            <el-input
              v-model="registerForm.nickname"
              placeholder="昵称"
              class="custom-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="email">
          <div class="input-wrapper">
            <el-icon class="input-icon"><Message /></el-icon>
            <el-input
              v-model="registerForm.email"
              type="email"
              placeholder="邮箱"
              class="custom-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" class="register-btn">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <span>已有账号？</span>
        <el-button link class="login-link" @click="goToLogin">立即登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Message } from '@element-plus/icons-vue'
import { registerApi } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_: any, value: string, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    const result = await registerApi({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname,
      email: registerForm.email
    })
    
    if (result.success) {
      ElMessage.success('注册成功')
      router.push('/login')
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error: any) {
    ElMessage.error('注册失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 注册容器 */
.register-container {
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
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 21, 41, 0.7), rgba(0, 30, 60, 0.9));
}

/* 注册盒子 */
.register-box {
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
  /* 注册盒子 */
  .register-box {
    width: 90%;
    max-width: 350px;
    padding: 30px 25px;
  }
  
  /* 注册标题 */
  .register-title {
    font-size: 26px;
  }
  
  /* 注册副标题 */
  .register-subtitle {
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
  
  /* 注册按钮 */
  .register-btn {
    height: 50px;
    font-size: 16px;
  }
  
  /* 底部链接 */
  .register-footer {
    font-size: 14px;
  }
}

.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.4);
}

/* 注册头部 */
.register-header {
  text-align: center;
  margin-bottom: 35px;
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #3498db, #9b59b6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
}

/* 注册表单 */
.register-form {
  margin-bottom: 25px;
  width: 100%;
}

/* 确保表单项内容居中 */
.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
}

.register-form :deep(.el-form-item__content) {
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

/* 注册按钮 */
.register-btn {
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

.register-btn:hover {
  background: linear-gradient(135deg, #2980b9, #8e44ad);
  box-shadow: 0 12px 35px rgba(52, 152, 219, 0.6);
  transform: translateY(-2px);
}

.register-btn:active {
  transform: translateY(0);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
}

.register-btn:disabled {
  background: #bdc3c7;
  box-shadow: none;
  transform: none;
}

/* 底部链接 */
.register-footer {
  text-align: center;
  font-size: 15px;
  color: #7f8c8d;
}

.login-link {
  color: #3498db !important;
  font-weight: 600;
  transition: all 0.3s ease;
  padding: 0 5px;
}

.login-link:hover {
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