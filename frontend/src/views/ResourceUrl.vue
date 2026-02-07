<template>
  <div class="resource-url-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>网址链接</h2>
      <div class="header-buttons">
        <el-button 
          type="danger" 
          :disabled="selectedIds.length === 0" 
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加网址
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="bookmarks.list"
        style="width: 100%"
        border
        stripe
        empty-text="暂无数据"
        @selection-change="handleSelectionChange"
      >
        <!-- 选择列 -->
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="url" label="网址URL" min-width="300">
          <template #default="scope">
            <div class="url-item">
              <!-- 网站图标 -->
              <img 
                v-if="getDomain(scope.row.url)" 
                :src="getFaviconUrl(scope.row.url)" 
                :alt="scope.row.url" 
                class="favicon" 
                @error="(e) => handleFaviconError(e, scope.row.url)"
              />
              <!-- 网址链接 -->
              <a :href="scope.row.url" target="_blank" rel="noopener noreferrer" class="url-link">
                {{ scope.row.url }}
              </a>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="scope">
            {{ scope.row.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="250">
          <template #default="scope">
            {{ scope.row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="openEditDialog(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row.id)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
          :total="bookmarks.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑网址弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑网址' : '添加网址'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="add-form"
      >
        <el-form-item label="网址URL" prop="url">
          <el-input
            v-model="form.url"
            placeholder="请输入网址URL"
            clearable
            @input="handleUrlInput"
          >
            <template #prefix>
              <el-icon><Link /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入标题（选填）"
            clearable
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            placeholder="请输入备注（选填）"
            type="textarea"
            :rows="3"
            resize="vertical"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
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
import { Plus, Link, Document, Edit, Delete } from '@element-plus/icons-vue'
import { getBookmarks, addBookmark, updateBookmark, deleteBookmark, batchDeleteBookmarks } from '@/api/bookmarks'
import type { BookmarkListResponse, BookmarkForm, Bookmark } from '@/api/bookmarks'

// 状态管理
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const selectedIds = ref<number[]>([])
const isEdit = ref(false)
const editingId = ref(0)

// 表单数据
const form = reactive<BookmarkForm>({
  url: '',
  title: '',
  remark: ''
})

// 验证规则
const rules = {
  url: [
    { required: true, message: '请输入网址URL', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL格式', trigger: 'blur' }
  ]
}

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 10 // 默认10条
})

// 书签数据
const bookmarks = ref<BookmarkListResponse>({
  total: 0,
  list: []
})

// 格式化日期
// 将ISO格式时间转换为本地时间格式：yyyy-MM-dd HH:mm:ss
const formatDate = (dateString: string | undefined) => {
  if (!dateString) return '-'
  
  try {
    const date = new Date(dateString)
    
    if (isNaN(date.getTime())) {
      return '-'
    }
    
    // 获取本地时间的年、月、日、时、分、秒
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0') // 月份从0开始
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    // 拼接成本地时间格式
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '-'  
  }
}

// 处理URL输入，自动补全协议
const handleUrlInput = () => {
  if (form.url && !/^https?:\/\//i.test(form.url)) {
    form.url = `https://${form.url}`
  }
}

// 从URL中提取域名，用于获取网站图标
const getDomain = (url: string | undefined): string => {
  if (!url) return ''
  
  try {
    // 创建URL对象解析域名
    const urlObj = new URL(url)
    return urlObj.hostname
  } catch (error) {
    // 如果URL格式不正确，尝试手动提取
    const match = url.match(/^(?:https?:\/\/)?([^:/?#]+)/i)
    return match?.[1] || ''
  }
}

// 获取网站图标URL，尝试多个服务
const getFaviconUrl = (url: string | undefined): string => {
  const domain = getDomain(url)
  if (!domain) return ''
  
  // 尝试使用多个favicon服务
  const services = [
    `https://www.google.com/s2/favicons?domain=${domain}&sz=16`,
    `https://f1.allesedv.com/16/${domain}`,
    `https://icon.horse/icon/${domain}`
  ]
  
  // 随机选择一个服务，增加成功率
  const index = Math.floor(Math.random() * services.length)
  const randomService = services[index] || ''
  return randomService
}

// 处理网站图标加载错误，尝试其他服务
const handleFaviconError = (e: Event, url: string | undefined) => {
  const target = e.target as HTMLImageElement
  if (!target || !url) return
  
  // 尝试使用其他服务
  const domain = getDomain(url)
  if (!domain) {
    target.style.display = 'none'
    return
  }
  
  // 尝试下一个服务
  const services = [
    `https://f1.allesedv.com/16/${domain}`,
    `https://icon.horse/icon/${domain}`
  ]
  
  // 随机选择一个备用服务
  const fallbackIndex = Math.floor(Math.random() * services.length)
  const fallbackService = services[fallbackIndex] || ''
  
  // 尝试加载备用服务
  target.src = fallbackService
  
  // 如果再次失败，显示默认图标
  target.onerror = () => {
    target.style.display = 'none'
  }
}

// 处理选择变化
const handleSelectionChange = (selection: Bookmark[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 打开添加对话框
const openAddDialog = () => {
  isEdit.value = false
  editingId.value = 0
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row: Bookmark) => {
  isEdit.value = true
  editingId.value = row.id
  // 填充表单数据
  form.url = row.url
  form.title = row.title || ''
  form.remark = row.remark || ''
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    let result
    if (isEdit.value) {
      // 编辑模式
      result = await updateBookmark(editingId.value, form)
    } else {
      // 添加模式
      result = await addBookmark(form)
    }
    
    if (result.success) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      fetchBookmarks()
    } else {
      ElMessage.error(result.message || (isEdit.value ? '编辑失败' : '添加失败'))
    }
  } catch (error: any) {
    ElMessage.error((isEdit.value ? '编辑失败' : '添加失败') + '：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 删除单个网址
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个网址吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    const result = await deleteBookmark(id)
    if (result.success) {
      ElMessage.success('删除成功')
      fetchBookmarks()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 批量删除网址
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的网址')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个网址吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    const result = await batchDeleteBookmarks(selectedIds.value)
    if (result.success) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      fetchBookmarks()
    } else {
      ElMessage.error(result.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败：' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.url = ''
  form.title = ''
  form.remark = ''
}

// 获取当前用户ID
const getCurrentUserId = (): number => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      return userInfo.id || 1
    }
    return 1 // 默认用户ID
  } catch (error) {
    console.error('获取用户信息失败：', error)
    return 1 // 默认用户ID
  }
}

// 获取书签列表
const fetchBookmarks = async () => {
  try {
    loading.value = true
    // 获取当前登录用户ID
    const uid = getCurrentUserId()
    const result = await getBookmarks({
      page: pagination.page,
      pageSize: pagination.pageSize,
      uid // 传递用户ID
    })
    
    // 核心问题：table显示数据，但分页显示0条，说明后端返回的total和list不一致
    // 解决方案：如果list有数据但total为0，用list的长度作为total
    bookmarks.value = {
      total: result.total || result.list?.length || 0,
      list: result.list || []
    }
    
    // 确保total和list一致
    if (bookmarks.value.list.length > 0 && bookmarks.value.total === 0) {
      bookmarks.value.total = bookmarks.value.list.length
    }
  } catch (error: any) {
    ElMessage.error('获取数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchBookmarks()
}

// 当前页变化
const handleCurrentChange = (current: number) => {
  pagination.page = current
  fetchBookmarks()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchBookmarks()
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

.table-container {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.url-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.favicon {
  width: 16px;
  height: 16px;
  border-radius: 2px;
  vertical-align: middle;
  display: inline-block;
}

.url-link {
  color: #409eff;
  text-decoration: none;
  transition: all 0.3s;
  vertical-align: middle;
}

.url-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-top: 1px solid #e4e7ed;
}

.add-form {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resource-url-container {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .table-container {
    overflow-x: auto;
  }
  
  .pagination-container {
    justify-content: center;
    padding: 10px;
  }
  
  :deep(.el-table__header-wrapper) {
    min-width: 800px;
  }
  
  :deep(.el-table__body-wrapper) {
    min-width: 800px;
  }
}
</style>
