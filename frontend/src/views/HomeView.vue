<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <div class="sidebar-header">
          <img src="/vite.png" alt="Logo" class="sidebar-logo">
          拾光瓶
        </div>
        <el-menu
          :default-active="route.path"
          class="sidebar-menu"
          router
        >
          <el-sub-menu index="/home/resource">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>资源中心站</span>
            </template>
            <el-menu-item index="/home/resource/url">
              <el-icon><Link /></el-icon>
              <span>网址链接</span>
            </el-menu-item>
            <el-menu-item index="/home/resource/exe">
              <el-icon><Download /></el-icon>
              <span>安装程序</span>
            </el-menu-item>
            <el-menu-item index="/home/resource/zip">
              <el-icon><Folder /></el-icon>
              <span>压缩文件</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/home/tools">
            <template #title>
              <el-icon><Sunny /></el-icon>
              <span>万能工具集</span>
            </template>
            <el-menu-item index="/home/tools/weather">
              <el-icon><Sunny /></el-icon>
              <span>天气查询</span>
            </el-menu-item>
            <el-menu-item index="/home/tools/music">
              <el-icon><Bell /></el-icon>
              <span>音乐解析</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/home/data">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据总控台</span>
            </template>
            <el-menu-item index="/home/data/finance">
              <el-icon><Money /></el-icon>
              <span>财务台账</span>
            </el-menu-item>
            <el-menu-item index="/home/data/statistics">
              <el-icon><DataAnalysis /></el-icon>
              <span>绩效记录</span>
            </el-menu-item>
            <el-menu-item index="/home/data/performance">
              <el-icon><DataAnalysis /></el-icon>
              <span>绩效看板</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 管理员后台 - 只有超级管理员可见 -->
          <el-sub-menu index="/home/admin" v-if="userInfo.role === 1">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>管理员后台</span>
            </template>
            <el-menu-item index="/home/admin/user-management">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/home/admin/feedback">
              <el-icon><ChatDotRound /></el-icon>
              <span>意见反馈</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-right">
            <el-dropdown trigger="click">
              <span class="user-info">
                <el-icon class="user-icon"><User /></el-icon>
                {{ userInfo.nickname || userInfo.username }}
                <el-icon class="arrow-down"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleFeedback">
                    <el-icon><ChatDotRound /></el-icon>
                    问题反馈
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <!-- 反馈对话框 -->
    <el-dialog
      v-model="feedbackDialogVisible"
      title="问题反馈"
      width="500px"
    >
      <el-form
        :model="feedbackForm"
        label-width="80px"
      >
        <el-form-item label="反馈类型">
          <el-select v-model="feedbackForm.feedbackType" placeholder="请选择反馈类型">
            <el-option
              v-for="item in feedbackTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入反馈内容"
          ></el-input>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input
            v-model="feedbackForm.contact"
            placeholder="请输入邮箱或手机号"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Setting, User, ChatDotRound, ArrowDown, SwitchButton, Document, Link, Download, Folder, DataAnalysis, Money, Sunny, Bell } from '@element-plus/icons-vue'
import { submitFeedback as apiSubmitFeedback } from '../api/feedback'

const router = useRouter()
const route = useRoute()

// 获取用户信息
const userInfo = ref({
  username: '',
  nickname: '',
  role: 3 // 默认值为3，普通成员
})

// 反馈对话框
const feedbackDialogVisible = ref(false)
const feedbackForm = ref({
  content: '',
  contact: '',
  feedbackType: 1
})

// 反馈类型选项
const feedbackTypeOptions = [
  { label: '建议', value: 1 },
  { label: '问题', value: 2 },
  { label: '投诉', value: 3 },
  { label: '其他', value: 4 }
]

onMounted(() => {
  const savedUser = localStorage.getItem('userInfo')
  if (savedUser) {
    userInfo.value = JSON.parse(savedUser)
  }
})

// 打开反馈对话框
const handleFeedback = () => {
  feedbackDialogVisible.value = true
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackForm.value.content.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  
  try {
    const savedUser = localStorage.getItem('userInfo')
    if (!savedUser) {
      ElMessage.error('用户信息不存在，请先登录')
      return
    }
    
    const userInfo = JSON.parse(savedUser)
    const feedbackData = {
      uid: userInfo.id,
      content: feedbackForm.value.content,
      contact: feedbackForm.value.contact,
      feedbackType: feedbackForm.value.feedbackType
    }
    
    await apiSubmitFeedback(feedbackData)
    feedbackDialogVisible.value = false
    ElMessage.success('反馈提交成功')
    
    // 重置表单
    feedbackForm.value = {
      content: '',
      contact: '',
      feedbackType: 1
    }
  } catch (error) {
    console.error('反馈提交失败:', error)
    ElMessage.error('反馈提交失败，请稍后重试')
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped>
/* 确保整个布局占满视口 */
.common-layout {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 侧边栏 */
  :deep(.el-aside) {
    width: 60px !important;
    transition: width 0.3s ease;
  }
  
  /* 侧边栏头部 */
  .sidebar-header {
    font-size: 0;
    padding: 15px 0;
  }
  
  .sidebar-header img {
    margin: 0;
  }
  
  /* 侧边栏文字隐藏 */
  .sidebar-menu span {
    display: none;
  }
  
  /* 侧边栏菜单项 */
  .sidebar-menu .el-sub-menu__title,
  .sidebar-menu .el-menu-item {
    padding: 0 20px !important;
    justify-content: center;
  }
  
  /* 侧边栏图标 */
  .sidebar-menu i {
    width: 24px;
    height: 24px;
  }
  
  /* 顶栏 */
  :deep(.el-header) {
    padding: 0 15px;
    height: 50px;
    line-height: 50px;
  }
  
  /* 用户信息 */
  .user-info {
    font-size: 13px;
    padding: 6px 12px;
  }
  
  /* 用户图标 */
  .user-icon {
    margin-right: 6px;
    font-size: 16px;
  }
  
  /* 主要内容区 */
  :deep(.el-main) {
    padding: 15px;
  }
}

/* 侧边栏头部 */
.sidebar-header {
  font-size: 20px;
  font-weight: 700;
  padding: 20px;
  text-align: center;
  background-color: #ffffff;
  color: #2c3e50;
  border-bottom: 1px solid #e4e7ed;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

/* 侧边栏图标 */
.sidebar-logo {
  width: 24px;
  height: 24px;
  object-fit: contain;
  vertical-align: middle;
}

/* 外层容器 */
:deep(.el-container) {
  height: 100%;
  width: 100%;
}

/* 侧边栏 */
:deep(.el-aside) {
  background-color: #ffffff;
  color: #2c3e50;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  border-right: 1px solid #e4e7ed;
}

/* 侧边栏菜单 */
.sidebar-menu {
  background-color: #ffffff;
  border-right: none;
  flex: 1;
}

.sidebar-menu .el-sub-menu__title {
  color: #2c3e50;
  transition: all 0.3s ease;
}

.sidebar-menu .el-sub-menu__title:hover {
  background-color: #f0f2f5;
  color: #3498db;
}

/* 重写所有i标签样式，确保可见性 */
.sidebar-menu i {
  /* 基础样式 */
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  color: inherit !important;
  transition: all 0.3s ease !important;
  vertical-align: middle;
  width: 20px;
  height: 20px;
  line-height: 1;
}

/* 确保i标签内的SVG可见 */
.sidebar-menu i svg {
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  color: currentColor !important;
  fill: currentColor !important;
  width: 100% !important;
  height: 100% !important;
  transition: all 0.3s ease !important;
}

/* 确保SVG路径可见 */
.sidebar-menu i svg path {
  fill: currentColor !important;
  color: currentColor !important;
  transition: all 0.3s ease !important;
}

/* 子菜单标题样式 */
.sidebar-menu .el-sub-menu__title {
  color: #2c3e50;
  transition: all 0.3s ease;
}

.sidebar-menu .el-sub-menu__title:hover {
  background-color: #f0f2f5;
  color: #3498db;
}

/* 子菜单标题下的i标签样式 */
.sidebar-menu .el-sub-menu__title i {
  color: inherit !important;
}

.sidebar-menu .el-sub-menu__title:hover i {
  color: #3498db !important;
}

/* 菜单项样式 */
.sidebar-menu .el-menu-item {
  color: #5a6c7d;
  background-color: transparent;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #f0f2f5;
  color: #3498db;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #e6f7ff;
  color: #3498db;
}

/* 菜单项下的i标签样式 */
.sidebar-menu .el-menu-item i {
  color: inherit !important;
}

.sidebar-menu .el-menu-item:hover i,
.sidebar-menu .el-menu-item.is-active i {
  color: #3498db !important;
}

/* 子菜单箭头样式 */
.sidebar-menu .el-sub-menu__icon-arrow {
  color: inherit !important;
  transition: all 0.3s ease !important;
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

.sidebar-menu .el-sub-menu__icon-arrow svg {
  color: inherit !important;
  fill: currentColor !important;
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

.sidebar-menu .el-sub-menu__title:hover .el-sub-menu__icon-arrow {
  color: #3498db !important;
}

/* 深色模式兼容 */
:deep(.el-menu--dark) {
  background-color: #ffffff;
  color: #2c3e50;
}

:deep(.el-menu--dark .el-menu-item) {
  color: #5a6c7d;
}

:deep(.el-menu--dark .el-menu-item.is-active) {
  background-color: #e6f7ff;
  color: #3498db;
}

:deep(.el-menu--dark .el-sub-menu__title) {
  color: #2c3e50;
}

:deep(.el-menu--dark .el-sub-menu__title:hover) {
  background-color: #f0f2f5;
  color: #3498db;
}

/* 确保激活和展开状态下的图标可见 */
.sidebar-menu .el-menu-item.is-active i,
.sidebar-menu .el-sub-menu.is-opened .el-sub-menu__title i {
  color: #3498db !important;
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* 确保所有状态下i标签都不被隐藏 */
.sidebar-menu i,
.sidebar-menu .el-icon {
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  position: relative !important;
  z-index: 10 !important;
  background: transparent !important;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

/* 顶栏 */
:deep(.el-header) {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
  line-height: 60px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
}

/* 顶栏右侧 */
.header-right {
  display: flex;
  align-items: center;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #f0f2f5;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: #e6f7ff;
  color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.2);
}

/* 用户图标 */
.user-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #3498db;
}

/* 下拉箭头 */
.arrow-down {
  margin-left: 8px;
  font-size: 12px;
  color: #95a5a6;
  transition: all 0.3s ease;
}

.user-info:hover .arrow-down {
  color: #3498db;
  transform: translateY(1px);
}

/* 主要内容区 */
:deep(.el-main) {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>