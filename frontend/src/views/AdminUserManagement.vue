<template>
  <div class="admin-user-management">
    <h1>用戶管理</h1>
    
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
    
    <!-- 用户列表表格 -->
    <el-table 
      :data="users" 
      style="width: 100%" 
      @selection-change="handleSelectionChange"
      :loading="tableLoading"
      stripe
      border
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="username" label="登录账号" width="180" />
      <el-table-column prop="nickname" label="用户昵称" width="180" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="role" label="用户权限" width="120">
        <template #default="scope">
          <el-tag 
            :type="scope.row.role === 1 ? 'danger' : scope.row.role === 2 ? 'warning' : 'success'"
          >
            {{ getRoleText(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="账号状态" width="120">
        <template #default="scope">
          <el-tag 
            :type="scope.row.status === 1 ? 'success' : 'danger'"
          >
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleEdit(scope.row)"
            :loading="editLoading && editingUserId === scope.row.id"
          >
            <el-icon><Edit /></el-icon>
            修改
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
            :loading="deleteLoading && deletingUserId === scope.row.id"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 编辑用户对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="编辑用户" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        :model="editForm" 
        :rules="rules" 
        ref="editFormRef" 
        label-width="80px"
      >
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="editForm.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="editForm.password" 
            type="password" 
            placeholder="不修改密码请留空" 
            show-password
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="用户昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="用户邮箱" type="email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择用户角色">
            <el-option label="超级管理员" :value="1" />
            <el-option label="管理员" :value="2" />
            <el-option label="普通成员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择账号状态">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
import { getUsersApi, updateUserApi, deleteUserApi, batchDeleteUsersApi } from '@/api/users'
import type { UserInfo } from '@/api/auth'

// 用户列表数据
const users = ref<UserInfo[]>([])

// 表格加载状态
const tableLoading = ref(false)

// 编辑表单引用
const editFormRef = ref<FormInstance>()

// 编辑对话框可见性
const dialogVisible = ref(false)

// 编辑加载状态
const editLoading = ref(false)

// 正在编辑的用户ID
const editingUserId = ref<number>(0)

// 删除加载状态
const deleteLoading = ref(false)

// 正在删除的用户ID
const deletingUserId = ref<number>(0)

// 批量删除加载状态
const batchDeleteLoading = ref(false)

// 选中的用户ID列表
const selectedIds = ref<number[]>([])

// 编辑表单数据
const editForm = reactive({
  id: 0,
  username: '',
  password: '',
  nickname: '',
  email: '',
  role: 3,
  status: 1
})

// 表单验证规则
const rules = reactive({
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 50, message: '登录账号长度在 3 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '登录账号只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 1, max: 50, message: '昵称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择账号状态', trigger: 'change' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 获取角色文本
const getRoleText = (role: number): string => {
  const roleMap: Record<number, string> = {
    1: '超级管理员',
    2: '管理员',
    3: '普通成员'
  }
  return roleMap[role] || '未知角色'
}

// 获取状态文本
const getStatusText = (status: number): string => {
  return status === 1 ? '正常' : '禁用'
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    tableLoading.value = true
    const result = await getUsersApi()
    if (result.success) {
      users.value = result.data
    } else {
      ElMessage.error(result.message || '获取用户列表失败')
    }
  } catch (error: any) {
    ElMessage.error('获取用户列表失败：' + error.message)
  } finally {
    tableLoading.value = false
  }
}

// 选择用户变化
const handleSelectionChange = (selection: UserInfo[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 编辑用户
const handleEdit = (row: UserInfo) => {
  // 填充表单数据
  editForm.id = row.id
  editForm.username = row.username
  editForm.nickname = row.nickname
  editForm.email = row.email
  editForm.role = row.role
  editForm.status = row.status
  editForm.password = ''
  
  editingUserId.value = row.id
  dialogVisible.value = true
}

// 更新用户
const handleUpdate = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    const params = {
      username: editForm.username,
      nickname: editForm.nickname,
      email: editForm.email,
      role: editForm.role,
      status: editForm.status
    }
    
    // 如果密码不为空，则包含密码
    if (editForm.password) {
      (params as any).password = editForm.password
    }
    
    const result = await updateUserApi(editForm.id, params)
    if (result.success) {
      ElMessage.success('更新用户成功')
      dialogVisible.value = false
      fetchUsers() // 重新获取用户列表
    } else {
      ElMessage.error(result.message || '更新用户失败')
    }
  } catch (error: any) {
    ElMessage.error('更新用户失败：' + error.message)
  } finally {
    editLoading.value = false
  }
}

// 删除用户
const handleDelete = async (row: UserInfo) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    deletingUserId.value = row.id
    deleteLoading.value = true
    
    const result = await deleteUserApi(row.id)
    if (result.success) {
      ElMessage.success('删除用户成功')
      fetchUsers() // 重新获取用户列表
    } else {
      ElMessage.error(result.message || '删除用户失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败：' + error.message)
    }
  } finally {
    deleteLoading.value = false
    deletingUserId.value = 0
  }
}

// 批量删除用户
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除选中的用户吗？', '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    batchDeleteLoading.value = true
    
    const result = await batchDeleteUsersApi(selectedIds.value)
    if (result.success) {
      ElMessage.success('批量删除用户成功')
      fetchUsers() // 重新获取用户列表
      selectedIds.value = [] // 清空选择
    } else {
      ElMessage.error(result.message || '批量删除用户失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除用户失败：' + error.message)
    }
  } finally {
    batchDeleteLoading.value = false
  }
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-user-management {
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

.admin-user-management h1 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>