<template>
  <div class="data-finance">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="left-section">
        <h2>财务台账</h2>
        <div class="time-range-selector">
          <el-radio-group v-model="statsTimeRange" size="small">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="custom">自定义</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-if="statsTimeRange === 'custom'"
            v-model="statsDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="YYYY-MM-DD"
            @change="loadTransactionStats"
            style="margin-left: 10px"
          />
        </div>
      </div>
      <div class="button-group">
        <el-button type="primary" @click="showAddDialog = true">
          <template #icon>
            <Plus />
          </template>
          记账
        </el-button>
        <el-button type="success" @click="showManageDialog = true">
          <template #icon>
            <Edit />
          </template>
          管理记账记录
        </el-button>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-cards">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">总收入</div>
          <div class="stat-value income">¥{{ totalIncome.toFixed(2) }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">总支出</div>
          <div class="stat-value expense">¥{{ totalExpense.toFixed(2) }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">结余</div>
          <div class="stat-value balance">¥{{ balance.toFixed(2) }}</div>
        </div>
      </el-card>
    </div>

    <!-- 图表配置区域 -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>收支趋势</span>
          <div class="chart-controls">
            <el-radio-group v-model="chartConfig.timeRange" size="small" @change="handleTimeRangeChange">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="custom">自定义</el-radio-button>
            </el-radio-group>
            <el-date-picker
              v-if="chartConfig.timeRange === 'custom'"
              v-model="chartConfig.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              value-format="YYYY-MM-DD"
              @change="handleDateRangeChange"
              style="margin-left: 10px"
            />
          </div>
        </div>
      </template>
      <div class="chart-container">
        <div ref="chartRef" class="chart"></div>
      </div>
    </el-card>

    <!-- 收支记录时间线 -->
    <div class="timeline-container">
      <!-- 支出记录 -->
      <el-card shadow="hover" class="timeline-card">
        <template #header>
          <div class="timeline-header">
            <el-icon><DocumentRemove /></el-icon>
            <span>支出记录</span>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in expenseRecords"
            :key="index"
            :timestamp="item.date"
            placement="top"
          >
            <div class="timeline-content">
              <div class="timeline-title">{{ item.category }}</div>
              <div class="timeline-amount expense">-¥{{ item.amount.toFixed(2) }}</div>
              <div class="timeline-desc">{{ item.description || '无描述' }}</div>
            </div>
          </el-timeline-item>
          <el-timeline-item v-if="expenseRecords.length === 0" timestamp="暂无数据">
            <div class="timeline-empty">暂无支出记录</div>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 收入记录 -->
      <el-card shadow="hover" class="timeline-card">
        <template #header>
          <div class="timeline-header">
            <el-icon><DocumentAdd /></el-icon>
            <span>收入记录</span>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in incomeRecords"
            :key="index"
            :timestamp="item.date"
            placement="top"
          >
            <div class="timeline-content">
              <div class="timeline-title">{{ item.category }}</div>
              <div class="timeline-amount income">+¥{{ item.amount.toFixed(2) }}</div>
              <div class="timeline-desc">{{ item.description || '无描述' }}</div>
            </div>
          </el-timeline-item>
          <el-timeline-item v-if="incomeRecords.length === 0" timestamp="暂无数据">
            <div class="timeline-empty">暂无收入记录</div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <!-- 记账弹窗 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加记账记录"
      width="500px"
    >
      <el-form
        ref="addFormRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <!-- 交易类型 -->
        <el-form-item label="交易类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio label="income">收入</el-radio>
            <el-radio label="expense">支出</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 金额 -->
        <el-form-item label="金额" prop="amount">
          <el-input
            v-model="formData.amount"
            placeholder="请输入金额"
            type="number"
            :precision="2"
            :step="0.01"
          >
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>

        <!-- 分类 -->
        <el-form-item label="分类" prop="category">
          <el-select
            v-model="formData.category"
            placeholder="请选择分类"
            clearable
          >
            <el-option
              v-for="item in filteredCategories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 描述 -->
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            placeholder="请输入描述（可选）"
            type="textarea"
            :rows="2"
          ></el-input>
        </el-form-item>

        <!-- 交易日期 -->
        <el-form-item label="交易日期" prop="transactionDate">
          <el-date-picker
            v-model="formData.transactionDate"
            type="date"
            placeholder="选择交易日期"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleAddTransaction">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 记账记录管理弹窗 -->
    <el-dialog
      v-model="showManageDialog"
      title="管理记账记录"
      width="90%"
      max-width="1200px"
    >
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="分类">
            <el-select
            v-model="filterForm.category"
            placeholder="请选择分类"
            clearable
            @change="handleFilterChange"
            style="width: 150px;"
          >
              <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleFilterChange"
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleFilterChange">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格操作区域 -->
      <div class="table-actions">
        <el-button
          type="danger"
          :disabled="selectedRowKeys.length === 0"
          @click="handleBatchDelete"
        >
          <template #icon>
            <Delete />
          </template>
          批量删除
        </el-button>
      </div>

      <!-- 记账记录表格 -->
      <el-table
        v-loading="tableLoading"
        :data="transactionList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="transactionDate" label="交易日期" sortable="custom" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.transactionDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="交易类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'income' ? 'success' : 'danger'">
              {{ scope.row.type === 'income' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100"></el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            <span :class="scope.row.type === 'income' ? 'income-amount' : 'expense-amount'">
              {{ scope.row.type === 'income' ? '+' : '-' }}¥{{ scope.row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
        <!-- 隐藏创建时间列 -->
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              <template #icon>
                <Edit />
              </template>
              修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row.id)"
            >
              <template #icon>
                <Delete />
              </template>
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
        ></el-pagination>
      </div>

      <!-- 编辑记账记录弹窗（嵌套在管理弹窗内） -->
      <el-dialog
        v-model="showEditDialog"
        title="修改记账记录"
        width="500px"
      >
        <el-form
          ref="editFormRef"
          :model="editFormData"
          :rules="formRules"
          label-width="100px"
        >
          <!-- 交易类型 -->
          <el-form-item label="交易类型" prop="type">
            <el-radio-group v-model="editFormData.type">
              <el-radio label="income">收入</el-radio>
              <el-radio label="expense">支出</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 金额 -->
          <el-form-item label="金额" prop="amount">
            <el-input
              v-model="editFormData.amount"
              placeholder="请输入金额"
              type="number"
              :precision="2"
              :step="0.01"
            >
              <template #prepend>¥</template>
            </el-input>
          </el-form-item>

          <!-- 分类 -->
          <el-form-item label="分类" prop="category">
            <el-select
              v-model="editFormData.category"
              placeholder="请选择分类"
              clearable
            >
              <el-option
                v-for="item in filteredEditCategories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>

          <!-- 描述 -->
          <el-form-item label="描述" prop="description">
            <el-input
              v-model="editFormData.description"
              placeholder="请输入描述（可选）"
              type="textarea"
              :rows="2"
            ></el-input>
          </el-form-item>

          <!-- 交易日期 -->
          <el-form-item label="交易日期" prop="transactionDate">
            <el-date-picker
              v-model="editFormData.transactionDate"
              type="date"
              placeholder="选择交易日期"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </el-form>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showEditDialog = false">取消</el-button>
            <el-button type="primary" @click="handleUpdateTransaction">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { Plus, DocumentRemove, DocumentAdd, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { addTransaction, getTransactionStats, getTransactionChartData, getTransactions, updateTransaction, deleteTransaction, batchDeleteTransactions } from '../api/transactions'

// 记账弹窗显示状态
const showAddDialog = ref(false)

// 记账记录管理弹窗显示状态
const showManageDialog = ref(false)

// 编辑记账记录弹窗显示状态
const showEditDialog = ref(false)

// 表单引用
const addFormRef = ref()
const editFormRef = ref()

// 交易类型
interface FormData {
  type: 'income' | 'expense'
  amount: string | number
  category: string
  description: string
  transactionDate: string
}

// 记账记录类型
interface TransactionRecord {
  id: number
  type: 'income' | 'expense'
  amount: number
  category: string
  description: string
  transactionDate: string
  createdAt: string
}

// 表单数据
const formData = reactive<FormData>({
  type: 'income',
  amount: '',
  category: '',
  description: '',
  transactionDate: (new Date().toISOString().split('T')[0] || '') as string // 默认今天
})

// 编辑表单数据
const editFormData = reactive<FormData & { id: number }>({
  id: 0,
  type: 'income',
  amount: '',
  category: '',
  description: '',
  transactionDate: (new Date().toISOString().split('T')[0] || '') as string
})

// 分类选项
const categories = [
  // 收入分类
  { value: '工资', label: '工资', type: 'income' },
  { value: '奖金', label: '奖金', type: 'income' },
  { value: '投资收益', label: '投资收益', type: 'income' },
  { value: '股票收益', label: '股票收益', type: 'income' },
  { value: '基金收益', label: '基金收益', type: 'income' },
  { value: '兼职收入', label: '兼职收入', type: 'income' },
  { value: '礼金收入', label: '礼金收入', type: 'income' },
  { value: '报销收入', label: '报销收入', type: 'income' },
  { value: '其他收入', label: '其他收入', type: 'income' },
  // 支出分类
  { value: '餐饮', label: '餐饮', type: 'expense' },
  { value: '早餐', label: '早餐', type: 'expense' },
  { value: '午餐', label: '午餐', type: 'expense' },
  { value: '晚餐', label: '晚餐', type: 'expense' },
  { value: '外卖', label: '外卖', type: 'expense' },
  { value: '聚餐', label: '聚餐', type: 'expense' },
  { value: '交通', label: '交通', type: 'expense' },
  { value: '公交地铁', label: '公交地铁', type: 'expense' },
  { value: '打车', label: '打车', type: 'expense' },
  { value: '加油', label: '加油', type: 'expense' },
  { value: '停车费', label: '停车费', type: 'expense' },
  { value: '购物', label: '购物', type: 'expense' },
  { value: '日用品', label: '日用品', type: 'expense' },
  { value: '服装', label: '服装', type: 'expense' },
  { value: '电子产品', label: '电子产品', type: 'expense' },
  { value: '娱乐', label: '娱乐', type: 'expense' },
  { value: '电影', label: '电影', type: 'expense' },
  { value: '游戏', label: '游戏', type: 'expense' },
  { value: '旅游', label: '旅游', type: 'expense' },
  { value: '医疗', label: '医疗', type: 'expense' },
  { value: '药品', label: '药品', type: 'expense' },
  { value: '检查', label: '检查', type: 'expense' },
  { value: '教育', label: '教育', type: 'expense' },
  { value: '学费', label: '学费', type: 'expense' },
  { value: '书籍', label: '书籍', type: 'expense' },
  { value: '房租', label: '房租', type: 'expense' },
  { value: '水电费', label: '水电费', type: 'expense' },
  { value: '网费', label: '网费', type: 'expense' },
  { value: '物业费', label: '物业费', type: 'expense' },
  { value: '其他支出', label: '其他支出', type: 'expense' }
]

// 筛选分类的计算属性
const filteredCategories = computed(() => {
  return categories.filter(category => category.type === formData.type)
})

// 编辑表单的筛选分类计算属性
const filteredEditCategories = computed(() => {
  return categories.filter(category => category.type === editFormData.type)
})

// 格式化日期函数
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 表单验证规则
const formRules = reactive({
  type: [
    { required: true, message: '请选择交易类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    {
      validator: (_rule: any, value: any, callback: (error?: Error) => void) => {
        // 处理字符串类型的数字输入
        const numValue = typeof value === 'string' ? parseFloat(value) : value
        // 确保值是有效数字且大于0
        if (!isNaN(numValue) && isFinite(numValue) && numValue > 0) {
          callback()
        } else {
          callback(new Error('金额必须大于0'))
        }
      },
      trigger: 'blur, change'
    }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  transactionDate: [
    { required: true, message: '请选择交易日期', trigger: 'change' }
  ]
})

// 筛选表单
const filterForm = reactive({
  category: '',
  dateRange: [] as string[]
})

// 记账记录列表
const transactionList = ref<TransactionRecord[]>([])

// 表格加载状态
const tableLoading = ref(false)

// 分页信息
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 选中的行
const selectedRowKeys = ref<number[]>([])
const selectedRows = ref<TransactionRecord[]>([])

// 排序信息
const sortInfo = reactive({
  prop: 'createdAt',
  order: 'descending' // 默认从新到旧
})

// 交易统计数据
const totalIncome = ref(0)
const totalExpense = ref(0)
const balance = ref(0)

// 时间线数据
const expenseRecords = ref([] as Array<{
  date: string
  category: string
  amount: number
  description: string
}>) 

const incomeRecords = ref([] as Array<{
  date: string
  category: string
  amount: number
  description: string
}>)

// 图表相关
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

// 图表配置
const chartConfig = reactive({
  timeRange: 'week' as 'week' | 'month' | 'custom',
  dateRange: [] as string[] // [startDate, endDate] or empty array
})

// 统计数据时间范围
const statsTimeRange = ref('month' as 'week' | 'month' | 'custom')
const statsDateRange = ref([] as string[])

// 图表数据
const chartData = reactive({
  dates: [] as string[],
  incomeData: [] as number[],
  expenseData: [] as number[]
})

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  // 初始化图表实例
  chartInstance = echarts.init(chartRef.value)
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
  
  // 加载图表数据
  loadChartData()
}

// 处理时间范围变化
const handleTimeRangeChange = () => {
  if (chartConfig.timeRange !== 'custom') {
    // 非自定义时间范围，清空日期范围
    chartConfig.dateRange = []
    loadChartData()
  }
}

// 处理自定义日期范围变化
const handleDateRangeChange = () => {
  if (chartConfig.dateRange && chartConfig.dateRange.length === 2) {
    loadChartData()
  }
}

// 加载图表数据
const loadChartData = async () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 构建请求参数
    const params: any = {
      type: 'both', // 固定显示收支对比
      userId: user.id
    }
    
    if (chartConfig.timeRange === 'custom') {
      // 自定义日期范围
      if (chartConfig.dateRange && chartConfig.dateRange.length === 2) {
        params.timeRange = 'custom'
        params.startDate = chartConfig.dateRange[0]
        params.endDate = chartConfig.dateRange[1]
        console.log('Date range:', chartConfig.dateRange)
        console.log('Params:', params)
      } else {
        // 未选择日期范围，不加载数据
        return
      }
    } else {
      // 预设时间范围
      params.timeRange = chartConfig.timeRange
    }
    
    const response = await getTransactionChartData(params)
    
    if (response.code === 200 && response.success) {
      chartData.dates = response.data.dates
      chartData.incomeData = response.data.incomeData
      chartData.expenseData = response.data.expenseData
      
      // 更新图表
      updateChart()
    } else {
      ElMessage.error('获取图表数据失败：' + response.message)
    }
  } catch (error) {
    console.error('获取图表数据失败：', error)
    ElMessage.error('获取图表数据失败')
  }
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return
  
  // 图表选项配置
  const option: echarts.EChartsOption = {
    title: {
      text: '',
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params: any) {
        let result = params[0].name + '<br/>'
        params.forEach((param: any) => {
          result += `${param.marker} ${param.seriesName}: ¥${param.value.toFixed(2)}<br/>`
        })
        return result
      }
    },
    legend: {
      data: [] as string[],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: chartData.dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [] as echarts.SeriesOption[]
  }
  
  // 明确类型断言，解决TypeScript编译错误
  const legend = option.legend as echarts.LegendComponentOption
  const series = option.series as echarts.SeriesOption[]
  
  // 直接显示收入和支出两条曲线
  legend.data!.push('收入')
  series.push({
    name: '收入',
    type: 'line',
    data: chartData.incomeData,
    smooth: true,
    itemStyle: {
      color: '#67c23a'
    },
    areaStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
        { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
      ])
    }
  })
  
  legend.data!.push('支出')
  series.push({
    name: '支出',
    type: 'line',
    data: chartData.expenseData,
    smooth: true,
    itemStyle: {
      color: '#f56c6c'
    },
    areaStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(245, 108, 108, 0.5)' },
        { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
      ])
    }
  })
  
  // 设置图表选项
  chartInstance.setOption(option)
}

// 加载交易统计数据
const loadTransactionStats = async () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 构建请求参数
    const params: any = {
      userId: user.id
    }
    
    // 根据时间范围计算开始和结束日期
    if (statsTimeRange.value === 'custom') {
      // 自定义日期范围
      if (statsDateRange.value && statsDateRange.value.length === 2) {
        params.startDate = statsDateRange.value[0]
        params.endDate = statsDateRange.value[1]
      } else {
        // 未选择日期范围，不加载数据
        return
      }
    } else if (statsTimeRange.value === 'week') {
      // 本周：从周一开始到周日结束
      const now = new Date()
      const startOfWeek = new Date(now)
      const day = now.getDay()
      const diff = now.getDate() - day + (day === 0 ? -6 : 1) // 调整为周一开始
      startOfWeek.setDate(diff)
      startOfWeek.setHours(0, 0, 0, 0)
      
      const endOfWeek = new Date(startOfWeek)
      endOfWeek.setDate(startOfWeek.getDate() + 6)
      endOfWeek.setHours(23, 59, 59, 999)
      
      params.startDate = startOfWeek.toISOString().split('T')[0]
      params.endDate = endOfWeek.toISOString().split('T')[0]
    } else if (statsTimeRange.value === 'month') {
      // 本月：从1号到月底
      const now = new Date()
      const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      
      params.startDate = startOfMonth.toISOString().split('T')[0]
      params.endDate = endOfMonth.toISOString().split('T')[0]
    }
    
    console.log('Stats params:', params)
    
    const response = await getTransactionStats(params)
    if (response.code === 200 && response.success) {
      totalIncome.value = response.data.totalIncome
      totalExpense.value = response.data.totalExpense
      balance.value = response.data.balance
      console.log('Stats data:', response.data)
    } else {
      ElMessage.error('获取交易统计数据失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取交易统计数据失败：', error)
    // 改进错误处理，提取错误信息
    const errorMessage = error instanceof Error ? error.message : '网络错误'
    ElMessage.error('获取交易统计数据失败：' + errorMessage)
  }
}

// 加载时间线数据
const loadTimelineData = async () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 获取最近的交易记录，按日期倒序排列
    const response = await getTransactions({
      page: 1,
      pageSize: 20,
      type: undefined, // 获取所有类型
      userId: user.id
    })
    
    if (response.code === 200 && response.success) {
      // 分离收入和支出记录
      const records = response.data.records
      
      // 转换记录格式
      expenseRecords.value = records
        .filter(record => record.type === 'expense')
        .map(record => ({
          date: formatDate(record.transactionDate),
          category: record.category,
          amount: record.amount,
          description: record.description
        }))
      // 去掉reverse()，最新的记录显示在前面
      
      incomeRecords.value = records
        .filter(record => record.type === 'income')
        .map(record => ({
          date: formatDate(record.transactionDate),
          category: record.category,
          amount: record.amount,
          description: record.description
        }))
      // 去掉reverse()，最新的记录显示在前面
    } else {
      ElMessage.error('获取交易记录失败：' + response.message)
    }
  } catch (error) {
    console.error('获取交易记录失败：', error)
    ElMessage.error('获取交易记录失败')
  }
}

// 处理添加记账记录
const handleAddTransaction = async () => {
  if (!addFormRef.value) return
  
  try {
    await addFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 将amount转换为数字类型
    const transactionData = {
      ...formData,
      amount: parseFloat(formData.amount as string)
    }
    
    // 调用API保存数据
    const response = await addTransaction(transactionData, user.id)
    
    if (response.code === 200 && response.success) {
      // 关闭弹窗
      showAddDialog.value = false
      
      // 重置表单
      addFormRef.value.resetFields()
      
      // 刷新数据
      loadTransactionStats()
      loadTimelineData()
      
      // 显示成功消息
      ElMessage.success('记账记录添加成功')
    } else {
      ElMessage.error('记账失败：' + response.message)
    }
  } catch (error) {
    console.error('记账失败：', error)
    ElMessage.error('记账失败，请稍后重试')
  }
}

// 加载记账记录列表
const loadTransactionList = async () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    tableLoading.value = true
    
    // 构建查询参数
    const params: any = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      userId: user.id
    }
    
    // 添加筛选条件
    if (filterForm.category) {
      params.category = filterForm.category
    }
    
    if (filterForm.dateRange && filterForm.dateRange.length === 2) {
      params.startDate = filterForm.dateRange[0]
      params.endDate = filterForm.dateRange[1]
    }
    
    // 添加排序条件
    if (sortInfo.prop && sortInfo.order) {
      params.sort = sortInfo.prop
      params.order = sortInfo.order === 'ascending' ? 'asc' : 'desc'
    }
    
    const response = await getTransactions(params)
    
    if (response.code === 200 && response.success) {
      transactionList.value = response.data.records
      pagination.total = response.data.total
    } else {
      ElMessage.error('获取记账记录失败：' + response.message)
    }
  } catch (error) {
    console.error('获取记账记录失败：', error)
    ElMessage.error('获取记账记录失败')
  } finally {
    tableLoading.value = false
  }
}

// 处理筛选条件变化
const handleFilterChange = () => {
  pagination.current = 1 // 筛选条件变化时，重置到第一页
  loadTransactionList()
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.category = ''
  filterForm.dateRange = []
  pagination.current = 1
  loadTransactionList()
}

// 处理排序变化
const handleSortChange = (sort: any) => {
  sortInfo.prop = sort.prop
  sortInfo.order = sort.order
  loadTransactionList()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  loadTransactionList()
}

// 处理当前页变化
const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadTransactionList()
}

// 处理选择变化
const handleSelectionChange = (rows: TransactionRecord[]) => {
  selectedRows.value = rows
  selectedRowKeys.value = rows.map(row => row.id)
}

// 处理编辑
const handleEdit = (row: TransactionRecord) => {
  // 填充编辑表单
  editFormData.id = row.id
  editFormData.type = row.type
  editFormData.amount = row.amount
  editFormData.category = row.category
  editFormData.description = row.description
  editFormData.transactionDate = row.transactionDate
  
  // 显示编辑弹窗
  showEditDialog.value = true
}

// 处理删除
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除这条记账记录吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const user = getCurrentUser()
      if (!user) {
        ElMessage.error('请先登录')
        return
      }
      
      // 调用删除API
      const response = await deleteTransaction(id)
      
      if (response.code === 200 && response.success) {
        // 刷新列表
        loadTransactionList()
        
        // 刷新统计数据和时间线
        loadTransactionStats()
        loadTimelineData()
        
        ElMessage.success('删除成功')
      } else {
        ElMessage.error('删除失败：' + response.message)
      }
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRowKeys.value.length} 条记账记录吗？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const user = getCurrentUser()
      if (!user) {
        ElMessage.error('请先登录')
        return
      }
      
      // 调用批量删除API
      const response = await batchDeleteTransactions(selectedRowKeys.value)
      
      if (response.code === 200 && response.success) {
        // 刷新列表
        loadTransactionList()
        
        // 刷新统计数据和时间线
        loadTransactionStats()
        loadTimelineData()
        
        // 清空选择
        selectedRowKeys.value = []
        selectedRows.value = []
        
        ElMessage.success('批量删除成功')
      } else {
        ElMessage.error('批量删除失败：' + response.message)
      }
    } catch (error) {
      console.error('批量删除失败：', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 处理更新记账记录
const handleUpdateTransaction = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用更新API
    const response = await updateTransaction(editFormData.id, {
      type: editFormData.type,
      amount: parseFloat(editFormData.amount as string),
      category: editFormData.category,
      description: editFormData.description,
      transactionDate: editFormData.transactionDate
    })
    
    if (response.code === 200 && response.success) {
      // 关闭弹窗
      showEditDialog.value = false
      
      // 刷新列表
      loadTransactionList()
      
      // 刷新统计数据和时间线
      loadTransactionStats()
      loadTimelineData()
      
      // 显示成功消息
      ElMessage.success('记账记录更新成功')
    } else {
      ElMessage.error('更新失败：' + response.message)
    }
  } catch (error) {
    console.error('更新失败：', error)
    ElMessage.error('更新失败，请稍后重试')
  }
}

// 监听管理弹窗显示状态，加载数据
watch(() => showManageDialog.value, (newVal) => {
  if (newVal) {
    // 显示弹窗时加载数据
    loadTransactionList()
  }
})

// 获取当前用户信息
const getCurrentUser = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    return JSON.parse(userInfo)
  }
  return null
}

// 页面加载时获取数据
onMounted(() => {
  loadTransactionStats()
  loadTimelineData()
  
  // 等待DOM渲染完成后初始化图表
  nextTick(() => {
    initChart()
  })
})

// 监听图表配置变化，重新加载数据
watch(() => chartConfig.timeRange, () => {
  loadChartData()
}, { deep: true })

// 监听交易类型变化，清空分类选择
watch(() => formData.type, () => {
  formData.category = ''
})

// 监听编辑表单交易类型变化，清空分类选择
watch(() => editFormData.type, () => {
  editFormData.category = ''
})

// 监听统计时间范围变化，重新加载数据
watch(() => statsTimeRange.value, () => {
  if (statsTimeRange.value !== 'custom') {
    // 非自定义时间范围，清空日期范围
    statsDateRange.value = []
    loadTransactionStats()
  }
})

// 监听统计日期范围变化，重新加载数据
watch(() => statsDateRange.value, () => {
  if (statsTimeRange.value === 'custom' && statsDateRange.value.length === 2) {
    loadTransactionStats()
  }
}, { deep: true })
</script>

<style scoped>
.data-finance {
  width: 100%;
  padding: 0;
  background-color: #f5f7fa;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 20px;
}

.left-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-bar h2 {
  margin: 0;
  font-size: 24px;
  color: #2c3e50;
}

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.button-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

@media (max-width: 768px) {
  .top-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .left-section {
    align-items: stretch;
  }
  
  .time-range-selector {
    flex-wrap: wrap;
  }
  
  .button-group {
    justify-content: center;
    margin-top: 10px;
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
}

.stat-value.income {
  color: #67c23a;
}

.stat-value.expense {
  color: #f56c6c;
}

.stat-value.balance {
  color: #409eff;
}

/* 图表卡片样式 */
.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.chart-controls {
  display: flex;
  gap: 10px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 时间线容器样式 */
.timeline-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
  margin-bottom: 20px;
}

/* 时间线卡片样式 */
.timeline-card {
  height: 400px;
  display: flex;
  flex-direction: column;
}

/* 时间线滚动容器 */
.timeline-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

/* 滚动条样式优化 */
.timeline-card :deep(.el-card__body::-webkit-scrollbar) {
  width: 6px;
}

.timeline-card :deep(.el-card__body::-webkit-scrollbar-track) {
  background: #f1f1f1;
  border-radius: 3px;
}

.timeline-card :deep(.el-card__body::-webkit-scrollbar-thumb) {
  background: #c1c1c1;
  border-radius: 3px;
}

.timeline-card :deep(.el-card__body::-webkit-scrollbar-thumb:hover) {
  background: #a8a8a8;
}

.timeline-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.timeline-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

/* 时间线内容样式 */
.timeline-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.timeline-amount {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.timeline-amount.income {
  color: #67c23a;
}

.timeline-amount.expense {
  color: #f56c6c;
}

.timeline-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

/* 空状态样式 */
.timeline-empty {
  color: #909399;
  font-size: 14px;
  padding: 10px;
}

/* 管理记账记录弹窗样式 */
.filter-section {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.filter-form {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-actions {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-start;
  gap: 12px;
}

.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 确保嵌套弹窗正常显示 */
.el-dialog__body {
  max-height: 70vh;
  overflow-y: auto;
}

/* 表格操作列样式，确保按钮在一行显示 */
:deep(.el-table .el-table__cell .cell) {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap;
  align-items: center;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-table__header th) {
  font-weight: 600;
  background-color: #f5f7fa;
}

/* 筛选表单样式 */
:deep(.filter-form .el-form-item) {
  margin-bottom: 0;
}</style>