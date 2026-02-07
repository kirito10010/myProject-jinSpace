<template>
  <div class="resource-url-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>安装程序</h2>
      <div class="header-buttons">
        <el-button 
          type="danger" 
          :disabled="selectedIds.length === 0" 
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon> 批量删除
        </el-button>
        <el-button type="primary" @click="showUploadDialog = true">
          <template #icon>
            <UploadFilled />
          </template>
          上传安装程序
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        v-if="installerFiles.list.length > 0"
        v-loading="loading"
        :data="installerFiles.list"
        style="width: 100%"
        border
        stripe
        empty-text="暂无数据"
        @selection-change="handleSelectionChange"
      >
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="fileName" label="文件名" width="200" />
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="platform" label="平台" width="120">
          <template #default="scope">
            {{ formatPlatform(scope.row.platform) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="openEditDialog(scope.row)"
              >
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row.id)"
              >
                <el-icon><Delete /></el-icon> 删除
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="downloadFile(scope.row.id)"
              >
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="installerFiles.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 上传弹窗 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传安装程序"
      width="600px"
      destroy-on-close
    >
      <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="uploadForm"
            :file-list="fileList"
            :auto-upload="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :on-change="handleFileChange"
            :limit="1"
            :on-exceed="handleExceed"
          >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖动文件到此处，或<em>点击上传</em>
        </div>
        <template #tip>
              <div class="el-upload__tip">
                支持所有文件类型，单个文件大小不超过1024MB
              </div>
            </template>
      </el-upload>
      
      <!-- 上传表单 -->
      <div class="upload-form">
        <el-form :model="uploadForm" label-width="80px" size="small">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="版本号">
                <el-input v-model="uploadForm.version" placeholder="请输入版本号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="平台">
                <el-select v-model="uploadForm.platform" placeholder="请选择平台">
                  <el-option label="Windows" value="windows" />
                  <el-option label="Linux" value="linux" />
                  <el-option label="macOS" value="macos" />
                  <el-option label="Android" value="android" />
                  <el-option label="iOS" value="ios" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述">
                <el-input
                  v-model="uploadForm.description"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入文件描述"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUploadDialog = false" :disabled="isUploading">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="isUploading">
            <el-icon><upload /></el-icon> 开始上传
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑安装程序"
      width="600px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="80px" size="small">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文件名">
              <el-input v-model="editForm.fileName" disabled placeholder="文件名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号">
              <el-input v-model="editForm.version" placeholder="请输入版本号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平台">
              <el-select v-model="editForm.platform" placeholder="请选择平台">
                <el-option label="Windows" value="windows" />
                <el-option label="Linux" value="linux" />
                <el-option label="macOS" value="macos" />
                <el-option label="Android" value="android" />
                <el-option label="iOS" value="ios" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="editForm.status" placeholder="请选择状态">
                <el-option label="已上传" value="uploaded" />
                <el-option label="已验证" value="verified" />
                <el-option label="活跃" value="active" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input
                v-model="editForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入文件描述"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="isEditing">
            <el-icon><Edit /></el-icon> 保存修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Upload, Download, Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 弹窗控制
const showUploadDialog = ref(false)
const showEditDialog = ref(false)

// 加载状态
const isUploading = ref(false)
const isEditing = ref(false)
const loading = ref(false)

// 上传相关
const fileList = ref<any[]>([])
const uploadUrl = '/api/installer-files/upload'
const uploadHeaders = ref({
  'Content-Type': 'multipart/form-data'
})
const uploadForm = ref({
  userId: 0,
  version: '',
  platform: '',
  description: ''
})

// 编辑相关
const editForm = ref({
  id: 0,
  fileName: '',
  version: '',
  platform: '',
  status: '',
  description: ''
})

// 选择相关
const selectedIds = ref<number[]>([])

// 分页相关
const pagination = reactive({
  page: 1,
  pageSize: 10
})

// 文件列表
const installerFiles = ref({
  total: 0,
  list: [] as any[]
})

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    return JSON.parse(userInfoStr)
  }
  return null
}

// 格式化平台名称
const formatPlatform = (platform: string) => {
  const platformMap: any = {
    'windows': 'Windows',
    'linux': 'Linux',
    'macos': 'macOS',
    'android': 'Android',
    'ios': 'iOS'
  }
  return platformMap[platform] || platform
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: any = {
    'uploaded': 'info',
    'verified': 'success',
    'active': 'primary',
    'archived': 'warning'
  }
  return statusMap[status] || 'info'
}

// 格式化状态
const formatStatus = (status: string) => {
  const statusMap: any = {
    'uploaded': '已上传',
    'verified': '已验证',
    'active': '活跃',
    'archived': '已归档'
  }
  return statusMap[status] || status
}

// 文件超出限制处理
const handleExceed = (_files: any, fileListChange: any[]) => {
  // 只保留最新的一个文件，使用完整的文件列表
  fileList.value = [fileListChange[0]]
}

// 文件变化处理
const handleFileChange = (_file: any, fileListChange: any[]) => {
  // 更新fileList，使用完整的文件列表
  fileList.value = fileListChange
}

// 上传前处理
const beforeUpload = (file: any) => {
  const isLt1024M = file.size / 1024 / 1024 < 1024
  if (!isLt1024M) {
    ElMessage.error('单个文件大小不能超过1024MB!')
    return false
  }
  return true
}

// 提交上传
const submitUpload = () => {
  if (fileList.value.length > 0) {
    // 获取fileList中的第一个文件
    const file = fileList.value[0].raw
    if (file) {
      // 手动触发上传
      const formData = new FormData()
      formData.append('file', file)
      formData.append('userId', uploadForm.value.userId.toString())
      if (uploadForm.value.version) {
        formData.append('version', uploadForm.value.version)
      }
      if (uploadForm.value.platform) {
        formData.append('platform', uploadForm.value.platform)
      }
      if (uploadForm.value.description) {
        formData.append('description', uploadForm.value.description)
      }
      
      // 显示加载状态
      isUploading.value = true
      
      // 使用axios手动上传
      axios.post(uploadUrl, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        handleUploadSuccess(response.data)
      }).catch(error => {
        handleUploadError(error)
      }).finally(() => {
        // 隐藏加载状态
        isUploading.value = false
      })
    } else {
      ElMessage.warning('请先选择要上传的文件')
    }
  } else {
    ElMessage.warning('请先选择要上传的文件')
  }
}

// 上传成功处理
const handleUploadSuccess = (response: any) => {
  if (response.success) {
    ElMessage.success('文件上传成功')
    // 关闭弹窗
    showUploadDialog.value = false
    // 清空文件列表
    fileList.value = []
    // 重置表单
    uploadForm.value.version = ''
    uploadForm.value.platform = ''
    uploadForm.value.description = ''
    // 刷新文件列表
    fetchUserFiles()
  } else {
    ElMessage.error('文件上传失败: ' + response.message)
  }
}

// 上传失败处理
const handleUploadError = (error: any) => {
  ElMessage.error('文件上传失败: ' + error.message)
}

// 获取用户文件列表
const fetchUserFiles = () => {
  const userInfo = getUserInfo()
  if (userInfo) {
    loading.value = true
    axios.get(`/api/installer-files/user/${userInfo.id}`, {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
      .then(response => {
        if (response.data.success) {
          installerFiles.value = {
            total: response.data.total,
            list: response.data.list || []
          }
        } else {
          ElMessage.error('获取文件列表失败: ' + response.data.message)
        }
      })
      .catch(error => {
        ElMessage.error('获取文件列表失败: ' + error.message)
      })
      .finally(() => {
        loading.value = false
      })
  }
}

// 打开编辑对话框
const openEditDialog = (row: any) => {
  // 填充编辑表单
  editForm.value = {
    id: row.id,
    fileName: row.fileName,
    version: row.version || '',
    platform: row.platform || '',
    status: row.status || 'uploaded',
    description: row.description || ''
  }
  showEditDialog.value = true
}

// 提交编辑
const submitEdit = () => {
  isEditing.value = true
  axios.put('/api/installer-files', editForm.value)
    .then(response => {
      if (response.data.success) {
        ElMessage.success('文件信息更新成功')
        showEditDialog.value = false
        fetchUserFiles()
      } else {
        ElMessage.error('文件信息更新失败: ' + response.data.message)
      }
    })
    .catch(error => {
      ElMessage.error('文件信息更新失败: ' + error.message)
    })
    .finally(() => {
      isEditing.value = false
    })
}

// 处理选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 处理单个删除
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除这个安装程序吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    axios.delete(`/api/installer-files/${id}`)
      .then(response => {
        if (response.data.success) {
          ElMessage.success('文件删除成功')
          fetchUserFiles()
        } else {
          ElMessage.error('文件删除失败: ' + response.data.message)
        }
      })
      .catch(error => {
        ElMessage.error('文件删除失败: ' + error.message)
      })
      .finally(() => {
        loading.value = false
      })
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的文件')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个安装程序吗？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    axios.delete('/api/installer-files/batch', {
      data: selectedIds.value
    })
      .then(response => {
        if (response.data.success) {
          ElMessage.success('批量删除成功')
          selectedIds.value = []
          fetchUserFiles()
        } else {
          ElMessage.error('批量删除失败: ' + response.data.message)
        }
      })
      .catch(error => {
        ElMessage.error('批量删除失败: ' + error.message)
      })
      .finally(() => {
        loading.value = false
      })
  }).catch(() => {
    // 取消删除
  })
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchUserFiles()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.page = current
  fetchUserFiles()
}

// 下载文件
const downloadFile = (fileId: number) => {
  window.open(`/api/installer-files/download/${fileId}`, '_blank')
}

// 初始化
onMounted(() => {
  const userInfo = getUserInfo()
  if (userInfo) {
    uploadForm.value.userId = userInfo.id
  }
  fetchUserFiles()
})
</script>

<style scoped>
.resource-url-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.upload-form {
  margin-top: 20px;
}

.upload-btn-col {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-top: 20px;
}

/* 弹窗样式 */
:deep(.el-dialog__body) {
  padding: 20px;
}

/* 分页容器样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-top: 1px solid #ebeef5;
}

/* 表格容器样式 */
.table-container {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 0;
  flex-wrap: nowrap;
}

:deep(.action-buttons .el-button) {
  margin-right: 1px !important;
  margin-left: 0 !important;
}

:deep(.action-buttons .el-button:last-child) {
  margin-right: 0 !important;
}
</style>
