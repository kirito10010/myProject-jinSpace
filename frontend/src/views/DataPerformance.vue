<template>
  <div class="performance-container">
    <!-- 顶部导航栏 -->
    <div class="top-bar">
      <div class="left-section">
        <h2>绩效看板</h2>
        <div class="filter-section">
          <el-select v-model="attendanceFilter" size="small" placeholder="按考勤项目筛选" class="filter-select" multiple>
            <el-option label="全部" value=""></el-option>
            <el-option v-for="item in attendanceItems" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </div>
      </div>
      <div class="button-group">
        <el-button type="primary" @click="showUploadDialog = true">
          <el-icon><Upload /></el-icon>
          上传绩效数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">参与评估人数</div>
          <div class="stat-value">{{ statistics.totalCount }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">平均额外结算人天</div>
          <div class="stat-value">{{ statistics.avgExtraDays.toFixed(2) }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">最高额外结算人天</div>
          <div class="stat-value high">{{ statistics.maxExtraDays.toFixed(2) }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-label">总额外结算人天</div>
          <div class="stat-value">{{ statistics.totalExtraDays.toFixed(2) }}</div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 人员绩效树状图排行 -->
      <el-card shadow="hover" class="chart-card" @click="openChartDialog('tree')">
        <template #header>
          <div class="chart-header">
            <span>人员绩效排行（额外结算人天）</span>
            <el-checkbox v-model="showTop20" size="small">仅显示前20名</el-checkbox>
          </div>
        </template>
        <div class="chart-container">
          <div ref="treeChartRef" class="chart"></div>
        </div>
      </el-card>

      <!-- 考勤项目总绩效树状图 -->
      <el-card shadow="hover" class="chart-card" @click="openChartDialog('attendanceTree')">
        <template #header>
          <div class="chart-header">
            <span>考勤项目总绩效排行</span>
          </div>
        </template>
        <div class="chart-container">
          <div ref="attendanceTreeRef" class="chart"></div>
        </div>
      </el-card>

      <!-- 考勤项目平均绩效柱状图 -->
      <el-card shadow="hover" class="chart-card" @click="openChartDialog('attendanceBar')">
        <template #header>
          <div class="chart-header">
            <span>考勤项目平均绩效</span>
          </div>
        </template>
        <div class="chart-container">
          <div ref="attendanceBarRef" class="chart"></div>
        </div>
      </el-card>

      <!-- 绩效分布直方图 -->
      <el-card shadow="hover" class="chart-card" @click="openChartDialog('distribution')">
        <template #header>
          <div class="chart-header">
            <span>绩效分布情况</span>
          </div>
        </template>
        <div class="chart-container">
          <div ref="distributionChartRef" class="chart"></div>
        </div>
      </el-card>
    </div>

    <!-- 图表弹窗 -->
    <el-dialog
      v-model="showChartDialog"
      :title="chartDialogTitle"
      width="80%"
      height="80%"
    >
      <div class="dialog-chart-container">
        <div ref="dialogChartRef" class="dialog-chart"></div>
      </div>
    </el-dialog>

    <!-- 绩效数据表 -->
    <el-card shadow="hover" class="data-table-card">
      <template #header>
        <div class="chart-header">
          <span>绩效详细数据</span>
        </div>
      </template>
      <div class="table-container">
        <el-table :data="filteredData" style="width: 100%">
          <el-table-column prop="姓名" label="姓名" width="120"></el-table-column>
          <el-table-column prop="考勤项目" label="考勤项目" width="180"></el-table-column>
          <el-table-column prop="部门" label="部门" width="150"></el-table-column>
          <el-table-column prop="人员角色" label="人员角色" width="180"></el-table-column>
          <el-table-column prop="额外结算人天" label="额外结算人天" width="150" align="right">
            <template #default="scope">
              {{ Number(scope.row['额外结算人天'] || 0).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="绩效人天合计" label="绩效人天合计" width="150" align="right">
            <template #default="scope">
              {{ Number(scope.row['绩效人天合计'] || 0).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="工作日+调休补回" label="工作日+调休补回" width="150" align="right">
            <template #default="scope">
              {{ Number(scope.row['工作日+调休补回'] || 0).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传绩效数据"
      width="500px"
    >
      <div class="upload-content">
        <!-- 拖拽上传 -->
        <el-upload
          v-model:file-list="fileList"
          class="drag-upload"
          drag
          action=""
          :auto-upload="false"
          :on-change="handleFileChange"
          accept=".xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              请上传Excel格式的绩效数据文件 (.xlsx, .xls)
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpload">确定上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import * as echarts from 'echarts'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'

// 上传对话框
const showUploadDialog = ref(false)
const fileList = ref<any[]>([])

// 图表弹窗
const showChartDialog = ref(false)
const chartDialogTitle = ref('')
const currentChartType = ref('')

// 筛选条件
const attendanceFilter = ref<string[]>([""])
const showTop20 = ref(true)

// 图表引用
const treeChartRef = ref<HTMLElement | null>(null)
const attendanceTreeRef = ref<HTMLElement | null>(null)
const attendanceBarRef = ref<HTMLElement | null>(null)
const distributionChartRef = ref<HTMLElement | null>(null)
const dialogChartRef = ref<HTMLElement | null>(null)

// 图表实例
let treeChart: echarts.ECharts | null = null
let attendanceTreeChart: echarts.ECharts | null = null
let attendanceBarChart: echarts.ECharts | null = null
let distributionChart: echarts.ECharts | null = null
let dialogChart: echarts.ECharts | null = null

// 原始数据
const rawData = ref<any[]>([])

// 统计数据
const statistics = reactive({
  totalCount: 0,
  avgExtraDays: 0,
  maxExtraDays: 0,
  totalExtraDays: 0
})

// 考勤项目列表
const attendanceItems = ref<string[]>([])

// 计算属性：筛选后的数据
const filteredData = computed(() => {
  let result = rawData.value
  
  if (attendanceFilter.value && attendanceFilter.value.length > 0 && !attendanceFilter.value.includes('')) {
    result = result.filter(item => attendanceFilter.value.includes(item['考勤项目']))
  }
  
  // 按额外结算人天排序
  result.sort((a, b) => Number(b['额外结算人天'] || 0) - Number(a['额外结算人天'] || 0))
  
  return result
})

// 计算属性：按考勤项目分组的数据
const attendanceGroupedData = computed(() => {
  const grouped: { [key: string]: any[] } = {}
  
  filteredData.value.forEach(item => {
    const attendance = item['考勤项目'] || '未知'
    if (!grouped[attendance]) {
      grouped[attendance] = []
    }
    grouped[attendance].push(item)
  })
  
  return grouped
})

// 计算属性：考勤项目总绩效数据
const attendanceTotalData = computed(() => {
  const data: { name: string; value: number }[] = []
  
  Object.entries(attendanceGroupedData.value).forEach(([attendance, items]) => {
    const totalExtraDays = items.reduce((sum, item) => sum + Number(item['额外结算人天'] || 0), 0)
    data.push({ name: attendance, value: totalExtraDays })
  })
  
  // 排序
  data.sort((a, b) => b.value - a.value)
  
  return data
})

// 计算属性：考勤项目平均绩效数据
const attendanceAvgData = computed(() => {
  const data: { name: string; value: number }[] = []
  
  Object.entries(attendanceGroupedData.value).forEach(([attendance, items]) => {
    const totalExtraDays = items.reduce((sum, item) => sum + Number(item['额外结算人天'] || 0), 0)
    const avgExtraDays = totalExtraDays / items.length
    data.push({ name: attendance, value: avgExtraDays })
  })
  
  // 排序
  data.sort((a, b) => b.value - a.value)
  
  return data
})

// 处理文件变化
const handleFileChange = (file: any) => {
  fileList.value = [file as any]
}

// 处理文件上传
const handleUpload = () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }

  const fileItem = fileList.value[0]
  if (!fileItem) {
    ElMessage.warning('文件无效')
    return
  }
  const file = fileItem.raw
  if (!file) {
    ElMessage.warning('文件无效')
    return
  }

  // 解析Excel文件
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      if (!e.target) {
        throw new Error('文件读取失败')
      }
      const data = e.target.result
      const workbook = XLSX.read(data, { type: 'binary' })
      const firstSheetName = workbook.SheetNames[0]
      if (!firstSheetName) {
        throw new Error('Excel文件中无工作表')
      }
      const worksheet = workbook.Sheets[firstSheetName]
      if (!worksheet) {
        throw new Error('工作表不存在')
      }
      const jsonData = XLSX.utils.sheet_to_json(worksheet)
      
      // 处理数据
      processExcelData(jsonData)
      
      // 关闭对话框
      showUploadDialog.value = false
      fileList.value = []
      
      ElMessage.success('绩效数据上传成功')
    } catch (error) {
      console.error('解析Excel文件失败:', error)
      ElMessage.error('解析Excel文件失败，请检查文件格式')
    }
  }
  reader.readAsBinaryString(file)
}

// 处理Excel数据
const processExcelData = (data: any[]) => {
  if (!data || data.length === 0) {
    ElMessage.warning('Excel文件中无数据')
    return
  }

  // 保存原始数据
  rawData.value = data

  // 提取考勤项目列表
  const attendanceSet = new Set<string>()
  data.forEach(item => {
    if (item['考勤项目']) {
      attendanceSet.add(item['考勤项目'])
    }
  })
  attendanceItems.value = Array.from(attendanceSet)
  
  // 默认选中"全部"选项
  attendanceFilter.value = [""]

  // 计算统计数据
  calculateStatistics(data)

  // 更新图表
  updateCharts()
}

// 计算统计数据
const calculateStatistics = (data: any[]) => {
  if (data.length === 0) {
    statistics.totalCount = 0
    statistics.avgExtraDays = 0
    statistics.maxExtraDays = 0
    statistics.totalExtraDays = 0
    return
  }

  const extraDays = data.map(item => Number(item['额外结算人天'] || 0))
  const total = extraDays.reduce((sum, val) => sum + val, 0)
  
  statistics.totalCount = data.length
  statistics.avgExtraDays = total / data.length
  statistics.maxExtraDays = Math.max(...extraDays)
  statistics.totalExtraDays = total
}

// 初始化图表
const initCharts = () => {
  // 初始化人员绩效树状图
  if (treeChartRef.value) {
    treeChart = echarts.init(treeChartRef.value)
  }
  
  // 初始化考勤项目总绩效树状图
  if (attendanceTreeRef.value) {
    attendanceTreeChart = echarts.init(attendanceTreeRef.value)
  }
  
  // 初始化考勤项目平均绩效柱状图
  if (attendanceBarRef.value) {
    attendanceBarChart = echarts.init(attendanceBarRef.value)
  }
  
  // 初始化绩效分布直方图
  if (distributionChartRef.value) {
    distributionChart = echarts.init(distributionChartRef.value)
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    treeChart?.resize()
    attendanceTreeChart?.resize()
    attendanceBarChart?.resize()
    distributionChart?.resize()
  })
}

// 更新所有图表
const updateCharts = () => {
  updateTreeChart()
  updateAttendanceTreeChart()
  updateAttendanceBarChart()
  updateDistributionChart()
}

// 更新人员绩效柱状图
const updateTreeChart = () => {
  if (!treeChart) return
  
  let displayData = filteredData.value
  if (showTop20.value) {
    displayData = displayData.slice(0, 20)
  }
  
  const names = displayData.map(item => item['姓名'])
  const values = displayData.map(item => Number(item['额外结算人天'] || 0))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        const item = displayData[params[0].dataIndex]
        return `${params[0].name}<br/>` +
               `额外结算人天: ${params[0].value.toFixed(2)}<br/>` +
               `考勤项目: ${item['考勤项目']}<br/>` +
               `部门: ${item['部门']}<br/>` +
               `人员角色: ${item['人员角色']}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#69c0ff' }
          ])
        }
      }
    ]
  }
  
  treeChart.setOption(option)
}

// 更新考勤项目总绩效柱状图
const updateAttendanceTreeChart = () => {
  if (!attendanceTreeChart) return
  
  const data = attendanceTotalData.value
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        return `${params[0].name}<br/>` +
               `总额外结算人天: ${params[0].value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '总额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        }
      }
    ]
  }
  
  attendanceTreeChart.setOption(option)
}

// 更新考勤项目平均绩效柱状图
const updateAttendanceBarChart = () => {
  if (!attendanceBarChart) return
  
  const data = attendanceAvgData.value
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        return `${params[0].name}<br/>` +
               `平均额外结算人天: ${params[0].value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '平均额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#69c0ff' }
          ])
        }
      }
    ]
  }
  
  attendanceBarChart.setOption(option)
}

// 更新绩效分布直方图
const updateDistributionChart = () => {
  if (!distributionChart) return
  
  const extraDays = filteredData.value.map(item => Number(item['额外结算人天'] || 0))
  
  // 计算直方图数据
  if (extraDays.length === 0) {
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLabel: {
          rotate: 45
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}人'
        }
      },
      series: [
        {
          name: '人数',
          type: 'bar',
          data: [],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67c23a' },
              { offset: 1, color: '#85ce61' }
            ])
          }
        }
      ]
    }
    distributionChart.setOption(option)
    return
  }
  
  const min = Math.min(...extraDays)
  const max = Math.max(...extraDays)
  const binCount = 10
  const binWidth = (max - min) / binCount
  
  // 使用更安全的方式初始化和填充bins数组
  const bins: number[] = []
  for (let i = 0; i < binCount; i++) {
    bins.push(0)
  }
  
  // 使用for循环替代forEach，提供更明确的类型检查
  for (let i = 0; i < extraDays.length; i++) {
    const day = extraDays[i]
    if (typeof day === 'number' && !isNaN(day)) {
      const binIndex = Math.floor((day - min) / binWidth)
      // 确保binIndex在有效范围内
      const clampedIndex = Math.max(0, Math.min(binIndex, binCount - 1))
      bins[clampedIndex] = (bins[clampedIndex] || 0) + 1
    }
  }
  
  // 生成bin标签
  const binLabels: string[] = []
  for (let i = 0; i < binCount; i++) {
    const start = (min + i * binWidth).toFixed(1)
    const end = (min + (i + 1) * binWidth).toFixed(1)
    binLabels.push(`${start}-${end}`)
  }
  
  // 创建图表选项
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: binLabels,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人'
      }
    },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: bins,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        }
      }
    ]
  }
  
  distributionChart.setOption(option)
}

// 监听筛选条件变化
watch([attendanceFilter, showTop20], () => {
  updateCharts()
})

// 打开图表弹窗
const openChartDialog = (chartType: string) => {
  currentChartType.value = chartType
  
  // 设置弹窗标题
  switch (chartType) {
    case 'tree':
      chartDialogTitle.value = '人员绩效排行（额外结算人天）'
      break
    case 'attendanceTree':
      chartDialogTitle.value = '考勤项目总绩效排行'
      break
    case 'attendanceBar':
      chartDialogTitle.value = '考勤项目平均绩效'
      break
    case 'distribution':
      chartDialogTitle.value = '绩效分布情况'
      break
    default:
      chartDialogTitle.value = '图表详情'
  }
  
  // 显示弹窗
  showChartDialog.value = true
  
  // 延迟初始化弹窗图表，确保DOM已更新
  setTimeout(() => {
    updateDialogChart()
  }, 100)
}

// 更新弹窗图表
const updateDialogChart = () => {
  if (!dialogChartRef.value) return
  
  // 初始化弹窗图表实例
  if (!dialogChart) {
    dialogChart = echarts.init(dialogChartRef.value)
  }
  
  // 根据当前图表类型设置选项
  switch (currentChartType.value) {
    case 'tree':
      updateDialogTreeChart()
      break
    case 'attendanceTree':
      updateDialogAttendanceTreeChart()
      break
    case 'attendanceBar':
      updateDialogAttendanceBarChart()
      break
    case 'distribution':
      updateDialogDistributionChart()
      break
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    dialogChart?.resize()
  })
}

// 更新弹窗人员绩效柱状图
const updateDialogTreeChart = () => {
  if (!dialogChart) return
  
  let displayData = filteredData.value
  if (showTop20.value) {
    displayData = displayData.slice(0, 20)
  }
  
  const names = displayData.map(item => item['姓名'])
  const values = displayData.map(item => Number(item['额外结算人天'] || 0))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        const item = displayData[params[0].dataIndex]
        return `${params[0].name}<br/>` +
               `额外结算人天: ${params[0].value.toFixed(2)}<br/>` +
               `考勤项目: ${item['考勤项目']}<br/>` +
               `部门: ${item['部门']}<br/>` +
               `人员角色: ${item['人员角色']}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#69c0ff' }
          ])
        }
      }
    ]
  }
  
  dialogChart.setOption(option)
}

// 更新弹窗考勤项目总绩效柱状图
const updateDialogAttendanceTreeChart = () => {
  if (!dialogChart) return
  
  const data = attendanceTotalData.value
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        return `${params[0].name}<br/>` +
               `总额外结算人天: ${params[0].value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '总额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        }
      }
    ]
  }
  
  dialogChart.setOption(option)
}

// 更新弹窗考勤项目平均绩效柱状图
const updateDialogAttendanceBarChart = () => {
  if (!dialogChart) return
  
  const data = attendanceAvgData.value
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        return `${params[0].name}<br/>` +
               `平均额外结算人天: ${params[0].value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人/绩效'
      }
    },
    series: [
      {
        name: '平均额外结算人天',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#69c0ff' }
          ])
        }
      }
    ]
  }
  
  dialogChart.setOption(option)
}

// 更新弹窗绩效分布直方图
const updateDialogDistributionChart = () => {
  if (!dialogChart) return
  
  const extraDays = filteredData.value.map(item => Number(item['额外结算人天'] || 0))
  
  // 计算直方图数据
  if (extraDays.length === 0) {
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLabel: {
          rotate: 45
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}人'
        }
      },
      series: [
        {
          name: '人数',
          type: 'bar',
          data: [],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67c23a' },
              { offset: 1, color: '#85ce61' }
            ])
          }
        }
      ]
    }
    dialogChart.setOption(option)
    return
  }
  
  const min = Math.min(...extraDays)
  const max = Math.max(...extraDays)
  const binCount = 10
  const binWidth = (max - min) / binCount
  
  // 使用更安全的方式初始化和填充bins数组
  const bins: number[] = []
  for (let i = 0; i < binCount; i++) {
    bins.push(0)
  }
  
  // 使用for循环替代forEach，提供更明确的类型检查
  for (let i = 0; i < extraDays.length; i++) {
    const day = extraDays[i]
    if (typeof day === 'number' && !isNaN(day)) {
      const binIndex = Math.floor((day - min) / binWidth)
      // 确保binIndex在有效范围内
      const clampedIndex = Math.max(0, Math.min(binIndex, binCount - 1))
      bins[clampedIndex] = (bins[clampedIndex] || 0) + 1
    }
  }
  
  // 生成bin标签
  const binLabels: string[] = []
  for (let i = 0; i < binCount; i++) {
    const start = (min + i * binWidth).toFixed(1)
    const end = (min + (i + 1) * binWidth).toFixed(1)
    binLabels.push(`${start}-${end}`)
  }
  
  // 创建图表选项
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: binLabels,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}人'
      }
    },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: bins,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        }
      }
    ]
  }
  
  dialogChart.setOption(option)
}

// 组件挂载时初始化
onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.performance-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 顶部导航栏 */
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

.filter-section {
  display: flex;
  gap: 10px;
}

.filter-select {
  width: 200px;
}

.button-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 统计卡片 */
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
  color: #409eff;
}

.stat-value.high {
  color: #67c23a;
}

.stat-value.low {
  color: #f56c6c;
}

/* 图表区域 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

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

.chart-container {
  height: 400px;
  margin-top: 10px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 数据表格 */
.data-table-card {
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .left-section {
    align-items: stretch;
  }
  
  .filter-section {
    flex-wrap: wrap;
  }
  
  .button-group {
    justify-content: center;
    margin-top: 10px;
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 350px;
  }
}

@media (max-width: 480px) {
  .performance-container {
    padding: 10px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr 1fr;
  }
  
  .stat-card {
    height: 100px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .chart-container {
    height: 300px;
  }
}

/* 图表弹窗样式 */
.dialog-chart-container {
  width: 100%;
  height: 70vh;
  min-height: 500px;
}

.dialog-chart {
  width: 100%;
  height: 100%;
}

/* 图表卡片悬停效果 */
.chart-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>