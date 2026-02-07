<template>
  <div class="admin-feedback">
    <h1>意见反馈</h1>
    
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button 
        type="danger" 
        :disabled="selectedIds.length === 0" 
        @click="handleBatchDelete"
        :loading="batchDeleteLoading"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>
    
    <!-- 反馈列表表格 -->
    <el-table 
      :data="feedbackList" 
      style="width: 100%" 
      @selection-change="handleSelectionChange"
      :loading="tableLoading"
      stripe
      border
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="uid" label="用户昵称" width="120">
        <template #default="scope">
          {{ getUserNickname(scope.row.uid) }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="反馈内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="contact" label="联系方式" width="180" show-overflow-tooltip />
      <el-table-column prop="feedbackType" label="反馈类型" width="120">
        <template #default="scope">
          <el-tag :type="getFeedbackTypeColor(scope.row.feedbackType)">
            {{ getFeedbackTypeText(scope.row.feedbackType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusColor(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleEdit(scope.row)"
            :loading="editLoading && editingId === scope.row.id"
          >
            <el-icon><Edit /></el-icon>
            修改
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
            :loading="deleteLoading && deletingId === scope.row.id"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
      </el-pagination>
    </div>
    
    <!-- 编辑反馈对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '修改反馈' : '添加反馈'" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        :model="editForm" 
        :rules="rules" 
        ref="editFormRef" 
        label-width="80px"
      >
        <el-form-item label="用户ID" prop="uid">
          <el-input v-model="editForm.uid" placeholder="用户ID" />
        </el-form-item>
        <el-form-item label="反馈内容" prop="content">
          <el-input 
            v-model="editForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="反馈内容" 
          />
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="editForm.contact" placeholder="联系方式" />
        </el-form-item>
        <el-form-item label="反馈类型" prop="feedbackType">
          <el-select v-model="editForm.feedbackType" placeholder="请选择反馈类型">
            <el-option label="建议" :value="1" />
            <el-option label="问题" :value="2" />
            <el-option label="投诉" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="待处理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已处理" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdate" :loading="editLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { getFeedbackList, updateFeedback, deleteFeedback, batchDeleteFeedback } from '../api/feedback'
import { getUsersApi } from '../api/users'
import type { Feedback } from '../api/feedback'
import type { UserInfo } from '../api/auth'

// 反馈列表数据
const feedbackList = ref<Feedback[]>([])

// 用户列表数据
const userList = ref<UserInfo[]>([])

// 表格加载状态
const tableLoading = ref(false)

// 用户列表加载状态
const userListLoading = ref(false)

// 编辑表单引用
const editFormRef = ref<FormInstance>()

// 编辑对话框可见性
const dialogVisible = ref(false)

// 是否为编辑模式
const isEdit = ref(false)

// 编辑加载状态
const editLoading = ref(false)

// 正在编辑的反馈ID
const editingId = ref<number>(0)

// 删除加载状态
const deleteLoading = ref(false)

// 正在删除的反馈ID
const deletingId = ref<number>(0)

// 批量删除加载状态
const batchDeleteLoading = ref(false)

// 选中的反馈ID列表
const selectedIds = ref<number[]>([])

// 分页信息
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 编辑表单数据
const editForm = reactive({
  id: 0,
  uid: 0,
  content: '',
  contact: '',
  feedbackType: 1,
  status: 1
})

// 表单验证规则
const rules = reactive({
  uid: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '反馈内容长度在 1 到 1000 个字符', trigger: 'blur' }
  ],
  feedbackType: [
    { required: true, message: '请选择反馈类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取反馈类型文本
const getFeedbackTypeText = (type: number): string => {
  const typeMap: Record<number, string> = {
    1: '建议',
    2: '问题',
    3: '投诉',
    4: '其他'
  }
  return typeMap[type] || '未知类型'
}

// 获取反馈类型颜色
const getFeedbackTypeColor = (type: number): string => {
  const colorMap: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info'
  }
  return colorMap[type] || 'info'
}

// 获取状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    1: '待处理',
    2: '处理中',
    3: '已处理',
    4: '已关闭'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态颜色
const getStatusColor = (status: number): string => {
  const colorMap: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 根据用户ID获取用户昵称
const getUserNickname = (uid: number): string => {
  const user = userList.value.find(u => u.id === uid)
  return user ? user.nickname : uid.toString()
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    userListLoading.value = true
    const result = await getUsersApi()
    if (result.success) {
      userList.value = result.data
    } else {
      ElMessage.error(result.message || '获取用户列表失败')
    }
  } catch (error: any) {
    ElMessage.error('获取用户列表失败：' + error.message)
  } finally {
    userListLoading.value = false
  }
}

// 获取反馈列表
const fetchFeedbackList = async () => {
  try {
    tableLoading.value = true
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize
    }
    const result = await getFeedbackList(params)
    if (result.success) {
      feedbackList.value = result.data
      // 这里需要根据实际返回的数据结构设置total
      pagination.total = result.data.length
    } else {
      ElMessage.error(result.message || '获取反馈列表失败')
    }
  } catch (error: any) {
    ElMessage.error('获取反馈列表失败：' + error.message)
  } finally {
    tableLoading.value = false
  }
}

// 选择反馈变化
const handleSelectionChange = (selection: Feedback[]) => {
  selectedIds.value = selection.map(item => item.id!)
}

// 编辑反馈
const handleEdit = (row: Feedback) => {
  // 填充表单数据
  editForm.id = row.id!
  editForm.uid = row.uid
  editForm.content = row.content
  editForm.contact = row.contact || ''
  editForm.feedbackType = row.feedbackType
  editForm.status = row.status || 1
  
  isEdit.value = true
  editingId.value = row.id!
  dialogVisible.value = true
}

// 更新反馈
const handleUpdate = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    const result = await updateFeedback(editForm)
    
    if (result.success) {
      ElMessage.success('更新反馈成功')
      dialogVisible.value = false
      fetchFeedbackList() // 重新获取反馈列表
    } else {
      ElMessage.error(result.message || '更新反馈失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('更新反馈失败：' + error.message)
    }
  } finally {
    editLoading.value = false
  }
}

// 删除反馈
const handleDelete = (row: Feedback) => {
  ElMessageBox.confirm('确定要删除这条反馈吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      deletingId.value = row.id!
      deleteLoading.value = true
      
      const result = await deleteFeedback(row.id!)
      
      if (result.success) {
        ElMessage.success('删除反馈成功')
        fetchFeedbackList() // 重新获取反馈列表
      } else {
        ElMessage.error(result.message || '删除反馈失败')
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error('删除反馈失败：' + error.message)
      }
    } finally {
      deleteLoading.value = false
      deletingId.value = 0
    }
  }).catch(() => {
    // 取消删除
  })
}

// 批量删除反馈
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的反馈')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条反馈吗？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      batchDeleteLoading.value = true
      
      const result = await batchDeleteFeedback(selectedIds.value)
      
      if (result.success) {
        ElMessage.success('批量删除反馈成功')
        fetchFeedbackList() // 重新获取反馈列表
        selectedIds.value = [] // 清空选择
      } else {
        ElMessage.error(result.message || '批量删除反馈失败')
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error('批量删除反馈失败：' + error.message)
      }
    } finally {
      batchDeleteLoading.value = false
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchFeedbackList()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchFeedbackList()
}

// 页面加载时获取反馈列表和用户列表
onMounted(async () => {
  // 先获取用户列表，再获取反馈列表
  await fetchUserList()
  await fetchFeedbackList()
})
</script>

<style scoped>
.admin-feedback {
  width: 100%;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.admin-feedback h1 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>