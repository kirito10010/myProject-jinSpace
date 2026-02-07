<template>
  <div class="data-statistics">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <h2>绩效记录</h2>
      <div class="button-group">
        <el-button type="primary" @click="showWorkReportDialog = true">
          工作汇报
        </el-button>
        <el-button type="primary" @click="showOvertimeReportDialog = true">
          加班汇报
        </el-button>
        <el-button type="primary" @click="showSalaryRecordDialog = true">
          工资记录
        </el-button>
        <el-button type="primary" @click="showProjectConfigDialog = true">
          生产项目配置
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 时间范围选择器 -->
      <div class="time-range-selector">
        <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
          <el-radio label="week">本周</el-radio>
          <el-radio label="month">本月</el-radio>
          <el-radio label="month_select">选择月份</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="timeRange === 'month_select'" class="month-select-range">
          <el-select
            v-model="selectedMonth"
            placeholder="请选择月份"
            style="width: 200px"
            @change="handleMonthSelectChange"
          >
            <el-option
              v-for="month in monthOptions"
              :key="month.value"
              :label="month.label"
              :value="month.value"
            ></el-option>
          </el-select>
        </div>
        <div v-if="timeRange === 'custom'" class="custom-date-range">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          ></el-date-picker>
        </div>
      </div>

      <!-- 绩效总和展示 -->
      <div class="performance-summary">
        <div class="performance-card">
          <div class="performance-label">当月绩效总和</div>
          <div class="performance-value">{{ performanceSummary.totalPerformance }}</div>
          <div class="performance-desc">绩效人天合计</div>
        </div>
        <div class="performance-card">
          <div class="performance-label">当月考勤天数</div>
          <div class="performance-value">{{ performanceSummary.attendanceDays }}</div>
          <div class="performance-desc">当月出勤天数</div>
        </div>
        <div class="performance-card">
          <div class="performance-label">日均绩效</div>
          <div class="performance-value">{{ performanceSummary.avgPerformance }}</div>
          <div class="performance-desc">平均绩效人天</div>
        </div>
      </div>

      <!-- 绩效图表 -->
      <div class="chart-container">
        <h3>绩效趋势</h3>
        <div id="performanceChart" ref="performanceChartRef" class="chart"></div>
      </div>
    </div>

    <!-- 生产项目配置弹窗 -->
    <el-dialog
      v-model="showProjectConfigDialog"
      title="生产项目配置"
      width="90%"
      max-width="1200px"
    >
      <!-- 表格操作区域 -->
      <div class="table-actions">
        <el-button
          type="primary"
          @click="handleAddProjectClick"
        >
          <template #icon>
            <Plus />
          </template>
          添加项目
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedProjectKeys.length === 0"
          @click="handleBatchDelete"
        >
          <template #icon>
            <Delete />
          </template>
          批量删除
        </el-button>
      </div>

      <!-- 项目表格 -->
      <el-table
        v-loading="projectTableLoading"
        :data="projectList"
        style="width: 100%"
        @selection-change="handleProjectSelectionChange"
      >
        <el-table-column type="selection" min-width="55"></el-table-column>
        <!-- 隐藏ID列 -->
        <el-table-column prop="id" label="ID" min-width="80" v-if="false"></el-table-column>
        <el-table-column prop="projectCode" label="项目编码" min-width="120"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="150"></el-table-column>
        <el-table-column prop="workQuotaEfficiency" label="作业工序额定效率" min-width="140"></el-table-column>
        <el-table-column prop="inspectQuotaEfficiency" label="质检工序额定效率" min-width="140"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditProject(scope.row)"
            >
              <template #icon>
                <Edit />
              </template>
              修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteProject(scope.row.id)"
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
          v-model:current-page="projectPagination.current"
          v-model:page-size="projectPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="projectPagination.total"
          @size-change="handleProjectSizeChange"
          @current-change="handleProjectCurrentChange"
        ></el-pagination>
      </div>

      <!-- 添加项目弹窗 -->
      <el-dialog
        v-model="showAddProjectDialog"
        :title="isEditProject ? '修改项目' : '添加项目'"
        width="500px"
      >
        <el-form
          ref="projectFormRef"
          :model="projectForm"
          :rules="projectFormRules"
          label-width="150px"
        >
          <el-form-item label="项目编码" prop="projectCode">
            <el-input
              v-model="projectForm.projectCode"
              placeholder="请输入项目编码"
            ></el-input>
          </el-form-item>
          <el-form-item label="项目名称" prop="projectName">
            <el-input
              v-model="projectForm.projectName"
              placeholder="请输入项目名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="作业工序额定效率" prop="workQuotaEfficiency">
            <el-input-number
              v-model="projectForm.workQuotaEfficiency"
              :min="0"
              placeholder="请输入作业工序额定效率"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="质检工序额定效率" prop="inspectQuotaEfficiency">
            <el-input-number
              v-model="projectForm.inspectQuotaEfficiency"
              :min="0"
              placeholder="请输入质检工序额定效率"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="projectForm.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showAddProjectDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSaveProject">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-dialog>

    <!-- 工作汇报弹窗 -->
    <el-dialog
      v-model="showWorkReportDialog"
      title="工作汇报"
      width="90%"
      max-width="1000px"
    >
      <!-- 表格操作区域 -->
      <div class="table-actions">
        <el-button
          type="primary"
          @click="handleAddWorkReport"
        >
          <template #icon>
            <Plus />
          </template>
          添加绩效记录
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedWorkReportKeys.length === 0"
          @click="handleBatchDeleteWorkReports"
        >
          <template #icon>
            <Delete />
          </template>
          批量删除
        </el-button>
      </div>

      <!-- 绩效记录表格 -->
      <el-table
        v-loading="workReportTableLoading"
        :data="workReportList"
        style="width: 100%"
        @selection-change="handleWorkReportSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" v-if="false"></el-table-column>
        <el-table-column prop="date" label="绩效日期" min-width="120"></el-table-column>
        <el-table-column prop="project" label="生产项目" min-width="150"></el-table-column>
        <el-table-column prop="process" label="工序" min-width="80"></el-table-column>
        <el-table-column prop="quotaEfficiency" label="额定效率" min-width="100"></el-table-column>
        <el-table-column prop="actualWorkload" label="实际工作量" min-width="120"></el-table-column>
        <el-table-column prop="performanceManDays" label="绩效人天合计" min-width="140"></el-table-column>
        <el-table-column label="操作" min-width="150">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditWorkReport(scope.row)"
            >
              <template #icon>
                <Edit />
              </template>
              修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteWorkReport(scope.row.id)"
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
          v-model:current-page="workReportPagination.current"
          v-model:page-size="workReportPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="workReportPagination.total"
          @size-change="handleWorkReportSizeChange"
          @current-change="handleWorkReportCurrentChange"
        ></el-pagination>
      </div>

      <!-- 添加/修改绩效记录弹窗 -->
      <el-dialog
        v-model="showAddWorkReportDialog"
        :title="isEditWorkReport ? '修改绩效记录' : '添加绩效记录'"
        width="600px"
      >
        <el-form
          ref="workReportFormRef"
          :model="workReportForm"
          :rules="workReportFormRules"
          label-width="120px"
        >
          <el-form-item label="绩效日期" prop="date">
            <el-date-picker
              v-model="workReportForm.date"
              type="date"
              placeholder="请选择绩效日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              required
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="生产项目" prop="project">
            <el-select
              v-model="workReportForm.project"
              placeholder="请选择生产项目"
              style="width: 100%"
              required
              @change="handleProjectChange"
            >
              <el-option
                v-for="project in projectOptions"
                :key="project.id"
                :label="project.projectName"
                :value="project.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="工序" prop="process">
            <el-select
              v-model="workReportForm.process"
              placeholder="请选择工序"
              style="width: 100%"
              required
              @change="handleProcessChange"
            >
              <el-option label="作业" value="作业"></el-option>
              <el-option label="质检" value="质检"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="额定效率" prop="quotaEfficiency">
            <el-input-number
              v-model="workReportForm.quotaEfficiency"
              :min="1"
              placeholder="额定效率"
              style="width: 100%"
              :disabled="true"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="实际工作量" prop="actualWorkload">
            <el-input-number
              v-model="workReportForm.actualWorkload"
              :min="1"
              placeholder="请输入实际工作量"
              style="width: 100%"
              @change="calculatePerformance"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="绩效人天合计" prop="performanceManDays">
            <el-input
              v-model="workReportForm.performanceManDays"
              placeholder="绩效人天合计"
              style="width: 100%"
              :disabled="true"
            ></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showAddWorkReportDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSaveWorkReport">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </el-dialog>

    <!-- 工资记录弹窗 -->
    <el-dialog
      v-model="showSalaryRecordDialog"
      title="工资记录"
      width="90%"
      max-width="1200px"
    >
      <!-- 表格操作区域 -->
      <div class="table-actions">
        <el-button
          type="primary"
          @click="handleAddSalaryRecord"
        >
          <template #icon>
            <Plus />
          </template>
          添加工资记录
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedSalaryRecordKeys.length === 0"
          @click="handleBatchDeleteSalaryRecords"
        >
          <template #icon>
            <Delete />
          </template>
          批量删除
        </el-button>
      </div>

      <!-- 工资记录表格 -->
      <el-table
        v-loading="salaryRecordTableLoading"
        :data="salaryRecordList"
        style="width: 100%"
        @selection-change="handleSalaryRecordSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" v-if="false"></el-table-column>
        <el-table-column prop="yearMonth" label="年月" min-width="100"></el-table-column>
        <el-table-column prop="periodStart" label="周期开始" min-width="120">
          <template #default="scope">
            {{ formatDate(scope.row.periodStart) }}
          </template>
        </el-table-column>
        <el-table-column prop="periodEnd" label="周期结束" min-width="120">
          <template #default="scope">
            {{ formatDate(scope.row.periodEnd) }}
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDays" label="出勤天数" min-width="100"></el-table-column>
        <el-table-column prop="baseSalary" label="基本薪资" min-width="120"></el-table-column>
        <el-table-column prop="performance" label="绩效系数" min-width="100"></el-table-column>
        <el-table-column prop="performanceSalary" label="绩效薪资" min-width="120"></el-table-column>
        <el-table-column prop="totalIncome" label="应发总额" min-width="120"></el-table-column>
        <el-table-column prop="totalDeduction" label="扣除总额" min-width="120"></el-table-column>
        <el-table-column prop="netSalary" label="实发薪资" min-width="120"></el-table-column>
        <el-table-column label="操作" min-width="150">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditSalaryRecord(scope.row)"
            >
              <template #icon>
                <Edit />
              </template>
              修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteSalaryRecord(scope.row.id)"
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
          v-model:current-page="salaryRecordPagination.current"
          v-model:page-size="salaryRecordPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="salaryRecordPagination.total"
          @size-change="handleSalaryRecordSizeChange"
          @current-change="handleSalaryRecordCurrentChange"
        ></el-pagination>
      </div>

      <!-- 添加/修改工资记录弹窗 -->
      <el-dialog
        v-model="showAddSalaryRecordDialog"
        :title="isEditSalaryRecord ? '修改工资记录' : '添加工资记录'"
        width="800px"
      >
        <el-form
          ref="salaryRecordFormRef"
          :model="salaryRecordForm"
          :rules="salaryRecordFormRules"
          label-width="120px"
        >
          <div class="form-row">
            <div class="form-col">
              <el-form-item label="年月" prop="yearMonth">
                <el-date-picker
                  v-model="salaryRecordForm.yearMonth"
                  type="month"
                  placeholder="请选择年月"
                  style="width: 100%"
                  format="YYYY-MM"
                  value-format="YYYY-MM"
                  required
                  @change="handleYearMonthChange"
                ></el-date-picker>
              </el-form-item>
          <el-form-item label="周期开始日期" prop="periodStart">
            <el-date-picker
              v-model="salaryRecordForm.periodStart"
              type="date"
              placeholder="请选择周期开始日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              required
              disabled
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="周期结束日期" prop="periodEnd">
            <el-date-picker
              v-model="salaryRecordForm.periodEnd"
              type="date"
              placeholder="请选择周期结束日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              required
              disabled
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="出勤天数" prop="attendanceDays">
            <el-input-number
              v-model="salaryRecordForm.attendanceDays"
              :min="0"
              placeholder="请输入出勤天数"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="基本薪资" prop="baseSalary">
            <el-input-number
              v-model="salaryRecordForm.baseSalary"
              :min="0"
              :step="0.01"
              placeholder="请输入基本薪资"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="绩效系数/评分" prop="performance">
                <el-input-number
                  v-model="salaryRecordForm.performance"
                  :min="0"
                  :step="0.01"
                  placeholder="请输入绩效系数/评分"
                  style="width: 100%"
                  @change="() => {
                    calculatePerformanceSalary();
                    calculateSalaryTotals();
                  }"
                ></el-input-number>
              </el-form-item>
          <el-form-item label="绩效薪资" prop="performanceSalary">
            <el-input-number
              v-model="salaryRecordForm.performanceSalary"
              :min="0"
              :step="0.01"
              placeholder="请输入绩效薪资"
              style="width: 100%"
              disabled
            ></el-input-number>
          </el-form-item>
          <el-form-item label="岗位绩效" prop="positionPerformance">
            <el-input-number
              v-model="salaryRecordForm.positionPerformance"
              :min="0"
              :step="0.01"
              placeholder="请输入岗位绩效"
              style="width: 100%"
              @change="calculateSalaryTotals"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="餐补" prop="mealAllowance">
            <el-input-number
              v-model="salaryRecordForm.mealAllowance"
              :min="0"
              :step="0.01"
              placeholder="请输入餐补"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="房补" prop="housingAllowance">
            <el-input-number
              v-model="salaryRecordForm.housingAllowance"
              :min="0"
              :step="0.01"
              placeholder="请输入房补"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="全勤奖" prop="fullAttendance">
            <el-input-number
              v-model="salaryRecordForm.fullAttendance"
              :min="0"
              :step="0.01"
              placeholder="请输入全勤奖"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
            </div>
            <div class="form-col">
          <el-form-item label="其他奖金" prop="otherBonus">
            <el-input-number
              v-model="salaryRecordForm.otherBonus"
              :min="0"
              :step="0.01"
              placeholder="请输入其他奖金"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="养老保险" prop="pension">
            <el-input-number
              v-model="salaryRecordForm.pension"
              :min="0"
              :step="0.01"
              placeholder="请输入养老保险"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="医疗保险" prop="medical">
            <el-input-number
              v-model="salaryRecordForm.medical"
              :min="0"
              :step="0.01"
              placeholder="请输入医疗保险"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="失业保险" prop="unemployment">
            <el-input-number
              v-model="salaryRecordForm.unemployment"
              :min="0"
              :step="0.01"
              placeholder="请输入失业保险"
              style="width: 100%"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="迟到分钟" prop="lateDeduction">
            <el-input-number
              v-model="salaryRecordForm.lateDeduction"
              :min="0"
              :step="1"
              placeholder="请输入迟到分钟"
              style="width: 100%"
              @change="calculateSalaryTotals"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="加班时长(小时)" prop="overtimeHours">
                <el-input-number
                  v-model="salaryRecordForm.overtimeHours"
                  :min="0"
                  :step="0.5"
                  placeholder="请输入加班时长"
                  style="width: 100%"
                  @change="() => {
                    calculateOvertimeSalary();
                    calculateSalaryTotals();
                  }"
                ></el-input-number>
              </el-form-item>
          <el-form-item label="加班薪资" prop="overtimeSalary">
            <el-input-number
              v-model="salaryRecordForm.overtimeSalary"
              :min="0"
              :step="0.01"
              placeholder="请输入加班薪资"
              style="width: 100%"
              disabled
            ></el-input-number>
          </el-form-item>
          <el-form-item label="应发总额" prop="totalIncome">
            <el-input-number
              v-model="salaryRecordForm.totalIncome"
              :min="0"
              :step="0.01"
              placeholder="请输入应发总额"
              style="width: 100%"
              disabled
            ></el-input-number>
          </el-form-item>
          <el-form-item label="扣除总额" prop="totalDeduction">
            <el-input-number
              v-model="salaryRecordForm.totalDeduction"
              :min="0"
              :step="0.01"
              placeholder="请输入扣除总额"
              style="width: 100%"
              disabled
            ></el-input-number>
          </el-form-item>
          <el-form-item label="实发薪资" prop="netSalary">
            <el-input-number
              v-model="salaryRecordForm.netSalary"
              :min="0"
              :step="0.01"
              placeholder="请输入实发薪资"
              style="width: 100%"
              disabled
            ></el-input-number>
          </el-form-item>
            </div>
          </div>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showDefaultConfigDialog = true">默认配置</el-button>
            <el-button @click="showAddSalaryRecordDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSaveSalaryRecord">保存</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 默认配置弹窗 -->
      <el-dialog
        v-model="showDefaultConfigDialog"
        title="薪资记录默认配置"
        width="600px"
      >
        <el-form
          :model="defaultConfig"
          label-width="120px"
        >
          <div class="form-row">
            <div class="form-col">
              <el-form-item label="出勤天数">
                <el-input-number
                  v-model="defaultConfig.attendanceDays"
                  :min="0"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="基本薪资">
                <el-input-number
                  v-model="defaultConfig.baseSalary"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="绩效系数/评分">
                <el-input-number
                  v-model="defaultConfig.performance"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="岗位绩效">
                <el-input-number
                  v-model="defaultConfig.positionPerformance"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="餐补">
                <el-input-number
                  v-model="defaultConfig.mealAllowance"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
            </div>
            <div class="form-col">
              <el-form-item label="房补">
                <el-input-number
                  v-model="defaultConfig.housingAllowance"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="全勤奖">
                <el-input-number
                  v-model="defaultConfig.fullAttendance"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="其他奖金">
                <el-input-number
                  v-model="defaultConfig.otherBonus"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="养老保险">
                <el-input-number
                  v-model="defaultConfig.pension"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="医疗保险">
                <el-input-number
                  v-model="defaultConfig.medical"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="失业保险">
                <el-input-number
                  v-model="defaultConfig.unemployment"
                  :min="0"
                  :step="0.01"
                  style="width: 100%"
                ></el-input-number>
              </el-form-item>
            </div>
          </div>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showDefaultConfigDialog = false">取消</el-button>
            <el-button type="primary" @click="saveDefaultConfig">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </el-dialog>

    <!-- 加班汇报弹窗 -->
    <el-dialog
      v-model="showOvertimeReportDialog"
      title="加班汇报"
      width="90%"
      max-width="1000px"
    >
      <!-- 表格操作区域 -->
      <div class="table-actions">
        <el-button
          type="primary"
          @click="handleAddOvertimeReport"
        >
          <template #icon>
            <Plus />
          </template>
          添加加班记录
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedOvertimeReportKeys.length === 0"
          @click="handleBatchDeleteOvertimeReports"
        >
          <template #icon>
            <Delete />
          </template>
          批量删除
        </el-button>
      </div>

      <!-- 加班记录表格 -->
      <el-table
        v-loading="overtimeReportTableLoading"
        :data="overtimeReportList"
        style="width: 100%"
        @selection-change="handleOvertimeReportSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" v-if="false"></el-table-column>
        <el-table-column prop="reportDate" label="汇报日期" min-width="120"></el-table-column>
        <el-table-column prop="overtimeHours" label="加班小时数" min-width="120"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="150"></el-table-column>
        <el-table-column prop="description" label="加班描述" min-width="200"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'pending' ? 'warning' : scope.row.status === 'approved' ? 'success' : 'danger'"
            >
              {{ scope.row.status === 'pending' ? '待审批' : scope.row.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditOvertimeReport(scope.row)"
            >
              <template #icon>
                <Edit />
              </template>
              修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteOvertimeReport(scope.row.id)"
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
          v-model:current-page="overtimeReportPagination.current"
          v-model:page-size="overtimeReportPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="overtimeReportPagination.total"
          @size-change="handleOvertimeReportSizeChange"
          @current-change="handleOvertimeReportCurrentChange"
        ></el-pagination>
      </div>

      <!-- 添加/修改加班记录弹窗 -->
      <el-dialog
        v-model="showAddOvertimeReportDialog"
        :title="isEditOvertimeReport ? '修改加班记录' : '添加加班记录'"
        width="600px"
      >
        <el-form
          ref="overtimeReportFormRef"
          :model="overtimeReportForm"
          :rules="overtimeReportFormRules"
          label-width="120px"
        >
          <el-form-item label="汇报日期" prop="reportDate">
            <el-date-picker
              v-model="overtimeReportForm.reportDate"
              type="date"
              placeholder="请选择汇报日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              required
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="加班小时数" prop="overtimeHours">
            <el-input-number
              v-model="overtimeReportForm.overtimeHours"
              :min="0.5"
              :step="0.5"
              placeholder="请输入加班小时数"
              style="width: 100%"
              required
            ></el-input-number>
          </el-form-item>
          <el-form-item label="项目名称" prop="projectName">
            <el-select
              v-model="overtimeReportForm.projectName"
              placeholder="请选择项目名称（可选）"
              style="width: 100%"
            >
              <el-option
                v-for="project in projectOptions"
                :key="project.id"
                :label="project.projectName"
                :value="project.projectName"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="加班描述" prop="description">
            <el-input
              v-model="overtimeReportForm.description"
              type="textarea"
              placeholder="请输入加班描述（可选）"
              rows="4"
              style="width: 100%"
            ></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showAddOvertimeReportDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSaveOvertimeReport">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProjects, addProject, updateProject, deleteProject, batchDeleteProjects } from '../api/projects'
import { saveDailyPerformance, getDailyPerformances, deleteDailyPerformance, getDailyPerformancesByDateRange } from '../api/performance'
import { saveSalaryRecord, getSalaryRecords, updateSalaryRecord, deleteSalaryRecord, batchDeleteSalaryRecords } from '../api/salary'
import { saveOvertimeReport, getOvertimeReports, updateOvertimeReport, deleteOvertimeReport, batchDeleteOvertimeReports, getOvertimeReportsByDateRange } from '../api/overtime'
import * as echarts from 'echarts'
import type { ProductionProject } from '../api/projects'
import type { SalaryRecord } from '../api/salary'

// 绩效记录组件

// 生产项目配置弹窗显示状态
const showProjectConfigDialog = ref(false)

// 工作汇报弹窗显示状态
const showWorkReportDialog = ref(false)

// 工资记录弹窗显示状态
const showSalaryRecordDialog = ref(false)

// 添加/修改工资记录弹窗显示状态
const showAddSalaryRecordDialog = ref(false)

// 是否为修改工资记录
const isEditSalaryRecord = ref(false)

// 默认配置弹窗显示状态
const showDefaultConfigDialog = ref(false)

// 加班汇报弹窗显示状态
const showOvertimeReportDialog = ref(false)

// 添加/修改加班记录弹窗显示状态
const showAddOvertimeReportDialog = ref(false)

// 是否为修改加班记录
const isEditOvertimeReport = ref(false)

// 加班记录列表
const overtimeReportList = ref<any[]>([])

// 选中的加班记录
const selectedOvertimeReportKeys = ref<number[]>([])
const selectedOvertimeReports = ref<any[]>([])

// 加班记录表格加载状态
const overtimeReportTableLoading = ref(false)

// 加班记录分页信息
const overtimeReportPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 加班记录表单引用
const overtimeReportFormRef = ref()

// 加班记录表单数据
const overtimeReportForm = reactive({
  id: 0,
  reportDate: '',
  overtimeHours: 0,
  projectName: '',
  description: ''
})

// 加班记录表单验证规则
const overtimeReportFormRules = reactive({
  reportDate: [
    { required: true, message: '请选择汇报日期', trigger: 'change' }
  ],
  overtimeHours: [
    { required: true, message: '请输入加班小时数', trigger: 'blur' },
    { type: 'number', min: 0.5, message: '加班小时数必须大于等于0.5', trigger: 'blur' }
  ]
})

// 默认配置表单数据
const defaultConfig = reactive({
  attendanceDays: 25,
  baseSalary: 2000,
  performance: 0,
  positionPerformance: 500,
  mealAllowance: 200,
  housingAllowance: 300,
  fullAttendance: 300,
  otherBonus: 100,
  pension: 360.32,
  medical: 90.08,
  unemployment: 13.51
})

// 加载默认配置
const loadDefaultConfig = () => {
  try {
    const user = getCurrentUser()
    if (!user) return
    
    const savedConfig = localStorage.getItem(`salary_default_config_${user.id}`)
    if (savedConfig) {
      const config = JSON.parse(savedConfig)
      Object.assign(defaultConfig, config)
    }
  } catch (error) {
    console.error('加载默认配置失败：', error)
  }
}

// 保存默认配置
const saveDefaultConfig = () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 保存到localStorage
    localStorage.setItem(`salary_default_config_${user.id}`, JSON.stringify(defaultConfig))
    
    ElMessage.success('默认配置保存成功')
    showDefaultConfigDialog.value = false
  } catch (error) {
    console.error('保存默认配置失败：', error)
    ElMessage.error('保存默认配置失败')
  }
}

// 添加/修改绩效记录弹窗显示状态
const showAddWorkReportDialog = ref(false)

// 是否为修改绩效记录
const isEditWorkReport = ref(false)

// 添加/修改项目弹窗显示状态
const showAddProjectDialog = ref(false)

// 是否为修改项目
const isEditProject = ref(false)

// 项目表格加载状态
const projectTableLoading = ref(false)

// 绩效记录表格加载状态
const workReportTableLoading = ref(false)

// 生产项目选项
const projectOptions = ref<ProductionProject[]>([])

// 绩效记录列表
const workReportList = ref<any[]>([])

// 选中的绩效记录
const selectedWorkReportKeys = ref<number[]>([])
const selectedWorkReports = ref<any[]>([])

// 绩效记录分页信息
const workReportPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 工资记录列表
const salaryRecordList = ref<any[]>([])

// 选中的工资记录
const selectedSalaryRecordKeys = ref<number[]>([])
const selectedSalaryRecords = ref<any[]>([])

// 工资记录表格加载状态
const salaryRecordTableLoading = ref(false)

// 工资记录分页信息
const salaryRecordPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 工资记录表单引用
const salaryRecordFormRef = ref()

// 工资记录表单数据
const salaryRecordForm = reactive({
  id: 0,
  uid: '',
  yearMonth: '',
  periodStart: '',
  periodEnd: '',
  attendanceDays: 0,
  baseSalary: 0,
  performance: 0,
  performanceSalary: 0,
  positionPerformance: 0,
  mealAllowance: 0,
  housingAllowance: 0,
  fullAttendance: 0,
  otherBonus: 0,
  pension: 0,
  medical: 0,
  unemployment: 0,
  lateDeduction: 0,
  overtimeHours: 0,
  overtimeSalary: 0,
  totalIncome: 0,
  totalDeduction: 0,
  netSalary: 0
})

// 工资记录表单验证规则
const salaryRecordFormRules = reactive({
  yearMonth: [
    { required: true, message: '请选择年月', trigger: 'change' }
  ],
  periodStart: [
    { required: true, message: '请选择周期开始日期', trigger: 'change' }
  ],
  periodEnd: [
    { required: true, message: '请选择周期结束日期', trigger: 'change' }
  ],
  attendanceDays: [
    { required: true, message: '请输入出勤天数', trigger: 'blur' },
    { type: 'number', min: 0, message: '出勤天数必须大于等于0', trigger: 'blur' }
  ],
  baseSalary: [
    { required: true, message: '请输入基本薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '基本薪资必须大于等于0', trigger: 'blur' }
  ],
  performance: [
    { required: true, message: '请输入绩效系数/评分', trigger: 'blur' },
    { type: 'number', min: 0, message: '绩效系数必须大于等于0', trigger: 'blur' }
  ],
  performanceSalary: [
    { required: true, message: '请输入绩效薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '绩效薪资必须大于等于0', trigger: 'blur' }
  ],
  positionPerformance: [
    { required: true, message: '请输入岗位绩效', trigger: 'blur' },
    { type: 'number', min: 0, message: '岗位绩效必须大于等于0', trigger: 'blur' }
  ],
  mealAllowance: [
    { required: true, message: '请输入餐补', trigger: 'blur' },
    { type: 'number', min: 0, message: '餐补必须大于等于0', trigger: 'blur' }
  ],
  housingAllowance: [
    { required: true, message: '请输入房补', trigger: 'blur' },
    { type: 'number', min: 0, message: '房补必须大于等于0', trigger: 'blur' }
  ],
  fullAttendance: [
    { required: true, message: '请输入全勤奖', trigger: 'blur' },
    { type: 'number', min: 0, message: '全勤奖必须大于等于0', trigger: 'blur' }
  ],
  otherBonus: [
    { required: true, message: '请输入其他奖金', trigger: 'blur' },
    { type: 'number', min: 0, message: '其他奖金必须大于等于0', trigger: 'blur' }
  ],
  pension: [
    { required: true, message: '请输入养老保险', trigger: 'blur' },
    { type: 'number', min: 0, message: '养老保险必须大于等于0', trigger: 'blur' }
  ],
  medical: [
    { required: true, message: '请输入医疗保险', trigger: 'blur' },
    { type: 'number', min: 0, message: '医疗保险必须大于等于0', trigger: 'blur' }
  ],
  unemployment: [
    { required: true, message: '请输入失业保险', trigger: 'blur' },
    { type: 'number', min: 0, message: '失业保险必须大于等于0', trigger: 'blur' }
  ],
  lateDeduction: [
    { type: 'number', min: 0, message: '迟到分钟必须大于等于0', trigger: 'blur' }
  ],
  overtimeHours: [
    { type: 'number', min: 0, message: '加班时长必须大于等于0', trigger: 'blur' }
  ],
  overtimeSalary: [
    { type: 'number', min: 0, message: '加班薪资必须大于等于0', trigger: 'blur' }
  ],
  totalIncome: [
    { required: true, message: '请输入应发总额', trigger: 'blur' },
    { type: 'number', min: 0, message: '应发总额必须大于等于0', trigger: 'blur' }
  ],
  totalDeduction: [
    { required: true, message: '请输入扣除总额', trigger: 'blur' },
    { type: 'number', min: 0, message: '扣除总额必须大于等于0', trigger: 'blur' }
  ],
  netSalary: [
    { required: true, message: '请输入实发薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '实发薪资必须大于等于0', trigger: 'blur' }
  ]
})

// 时间范围选择
const timeRange = ref('month')
const dateRange = ref<string[]>([])

// 月份选择相关
const selectedMonth = ref('')
const monthOptions = ref<any[]>([])

// 绩效总和数据
const performanceSummary = reactive({
  totalPerformance: '0.00000',
  attendanceDays: 0,
  avgPerformance: '0.00000'
})

// 绩效图表引用
const performanceChartRef = ref()
let performanceChart: echarts.ECharts | null = null

// 绩效数据加载状态
const performanceLoading = ref(false)

// 工作汇报表单引用
const workReportFormRef = ref()

// 工作汇报表单数据
const workReportForm = reactive({
  id: 0,
  date: '',
  project: null as number | null, // 使用项目ID作为v-model值
  process: '',
  quotaEfficiency: 0,
  actualWorkload: 0,
  performanceManDays: '0.00000'
})

// 工作汇报表单验证规则
const workReportFormRules = reactive({
  date: [
    { required: true, message: '请选择绩效日期', trigger: 'change' }
  ],
  project: [
    { required: true, message: '请选择生产项目', trigger: 'change' }
  ],
  process: [
    { required: true, message: '请选择工序', trigger: 'change' }
  ],
  actualWorkload: [
    { required: true, message: '请输入实际工作量', trigger: 'blur' },
    { type: 'number', min: 1, message: '实际工作量必须大于0', trigger: 'blur' }
  ]
})

// 项目列表
const projectList = ref<ProductionProject[]>([])

// 选中的项目
const selectedProjectKeys = ref<number[]>([])
const selectedProjects = ref<ProductionProject[]>([])

// 分页信息
const projectPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 项目表单数据
const projectForm = reactive({
  id: 0,
  projectCode: '',
  projectName: '',
  uid: 0,
  workQuotaEfficiency: 0,
  inspectQuotaEfficiency: 0,
  status: 1
})

// 项目表单引用
const projectFormRef = ref()

// 项目表单验证规则
const projectFormRules = reactive({
  projectCode: [
    { required: true, message: '请输入项目编码', trigger: 'blur' },
    { min: 1, max: 50, message: '项目编码长度不能超过50个字符', trigger: 'blur' }
  ],
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 1, max: 100, message: '项目名称长度不能超过100个字符', trigger: 'blur' }
  ],
  workQuotaEfficiency: [
    { required: false, message: '请输入作业工序额定效率', trigger: 'blur' },
    { type: 'number', min: 0, message: '作业工序额定效率必须大于等于0', trigger: 'blur' }
  ],
  inspectQuotaEfficiency: [
    { required: false, message: '请输入质检工序额定效率', trigger: 'blur' },
    { type: 'number', min: 0, message: '质检工序额定效率必须大于等于0', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取当前用户信息
const getCurrentUser = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    return JSON.parse(userInfo)
  }
  return null
}

// 加载项目列表
const loadProjectList = async () => {
  try {
    projectTableLoading.value = true
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取项目列表
    const response = await getProjects(user.id) as any
    
    if (response.code === 200 && response.success) {
      const allData = response.data
      projectPagination.total = allData.length
      
      // 前端分页处理
      const startIndex = (projectPagination.current - 1) * projectPagination.pageSize
      const endIndex = startIndex + projectPagination.pageSize
      projectList.value = allData.slice(startIndex, endIndex)
    } else {
      ElMessage.error('获取项目列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取项目列表失败：', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    projectTableLoading.value = false
  }
}

// 处理项目选择变化
const handleProjectSelectionChange = (rows: ProductionProject[]) => {
  selectedProjects.value = rows
  selectedProjectKeys.value = rows.map(row => row.id)
}

// 处理添加项目点击
const handleAddProjectClick = () => {
  // 重置表单
  projectForm.id = 0
  projectForm.projectCode = ''
  projectForm.projectName = ''
  projectForm.status = 1
  projectForm.workQuotaEfficiency = 0
  projectForm.inspectQuotaEfficiency = 0
  
  // 设置用户ID
  const user = getCurrentUser()
  if (user) {
    projectForm.uid = user.id
  }
  
  isEditProject.value = false
  showAddProjectDialog.value = true
}



// 处理编辑项目
const handleEditProject = (row: ProductionProject) => {
  // 填充表单
  projectForm.id = row.id
  projectForm.projectCode = row.projectCode
  projectForm.projectName = row.projectName
  projectForm.status = row.status
  projectForm.workQuotaEfficiency = row.workQuotaEfficiency || 0
  projectForm.inspectQuotaEfficiency = row.inspectQuotaEfficiency || 0
  
  isEditProject.value = true
  showAddProjectDialog.value = true
}

// 处理保存项目
const handleSaveProject = async () => {
  if (!projectFormRef.value) return
  
  try {
    await projectFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    if (isEditProject.value) {
        // 修改项目
        const response = await updateProject(projectForm, user.id) as any
        
        if (response.code === 200 && response.success) {
          // 更新本地数据
          const index = projectList.value.findIndex(item => item.id === projectForm.id)
          if (index !== -1) {
            // 保留原有的createdAt和updatedAt字段
            const existingProject = projectList.value[index]
            if (existingProject) {
              projectList.value[index] = {
                ...projectForm,
                uid: existingProject.uid,
                createdAt: existingProject.createdAt,
                updatedAt: new Date().toISOString()
              }
            }
          }
          
          ElMessage.success('项目修改成功')
        } else {
          // 处理错误信息，转换为用户友好的提示
          let errorMessage = response.message
          if (errorMessage.includes('Duplicate entry') && errorMessage.includes('project_code')) {
            errorMessage = '项目编码已存在，请使用其他编码'
          }
          ElMessage.error('项目修改失败：' + errorMessage)
        }
      } else {
          // 添加项目
          // 确保项目有uid字段
          projectForm.uid = user.id
          
          const response = await addProject(projectForm) as any
          
          if (response.code === 200 && response.success) {
            // 重新加载项目列表
            await loadProjectList()
            
            ElMessage.success('项目添加成功')
          } else {
            // 处理错误信息，转换为用户友好的提示
            let errorMessage = response.message
            if (errorMessage.includes('Duplicate entry') && errorMessage.includes('project_code')) {
              errorMessage = '项目编码已存在，请使用其他编码'
            }
            ElMessage.error('项目添加失败：' + errorMessage)
          }
        }
    
    showAddProjectDialog.value = false
  } catch (error) {
    console.error('保存项目失败：', error)
    ElMessage.error('保存项目失败')
  }
}

// 处理删除项目
const handleDeleteProject = (id: number) => {
  ElMessageBox.confirm('确定要删除这个项目吗？', '删除确认', {
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
      
      // 调用API删除项目
      const response = await deleteProject(id, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        projectList.value = projectList.value.filter(item => item.id !== id)
        projectPagination.total--
        
        ElMessage.success('项目删除成功')
      } else {
        ElMessage.error('项目删除失败：' + response.message)
      }
    } catch (error) {
      console.error('删除项目失败：', error)
      ElMessage.error('删除项目失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedProjectKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的项目')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedProjectKeys.value.length} 个项目吗？`, '批量删除确认', {
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
      
      // 调用API批量删除项目
      const response = await batchDeleteProjects(selectedProjectKeys.value, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        projectList.value = projectList.value.filter(item => !selectedProjectKeys.value.includes(item.id))
        projectPagination.total -= selectedProjectKeys.value.length
        
        // 清空选择
        selectedProjectKeys.value = []
        selectedProjects.value = []
        
        ElMessage.success('批量删除成功')
      } else {
        ElMessage.error('批量删除失败：' + response.message)
      }
    } catch (error) {
      console.error('批量删除项目失败：', error)
      ElMessage.error('批量删除项目失败')
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 处理分页大小变化
const handleProjectSizeChange = (size: number) => {
  projectPagination.pageSize = size
  loadProjectList()
}

// 处理当前页变化
const handleProjectCurrentChange = (current: number) => {
  projectPagination.current = current
  loadProjectList()
}

// 监听项目配置弹窗显示状态，加载数据
watch(() => showProjectConfigDialog.value, (newVal) => {
  if (newVal) {
    // 显示弹窗时加载数据
    loadProjectList()
  }
})

// 加载生产项目选项
const loadProjectOptions = async () => {
  try {
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取项目列表
    const response = await getProjects(user.id) as any
    
    if (response.code === 200 && response.success) {
      projectOptions.value = response.data
    } else {
      ElMessage.error('获取生产项目列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取生产项目列表失败：', error)
    ElMessage.error('获取生产项目列表失败')
  }
}

// 处理项目选择变化
const handleProjectChange = () => {
  handleProcessChange()
}

// 处理工序选择变化
const handleProcessChange = () => {
  if (workReportForm.project && workReportForm.process) {
    // 根据项目ID查找项目对象
    const selectedProject = projectOptions.value.find(p => p.id === workReportForm.project)
    if (selectedProject) {
      if (workReportForm.process === '作业') {
        workReportForm.quotaEfficiency = selectedProject.workQuotaEfficiency || 0
      } else if (workReportForm.process === '质检') {
        workReportForm.quotaEfficiency = selectedProject.inspectQuotaEfficiency || 0
      }
      calculatePerformance()
    }
  }
}

// 计算绩效人天
const calculatePerformance = () => {
  if (workReportForm.quotaEfficiency > 0 && workReportForm.actualWorkload > 0) {
    const performance = workReportForm.actualWorkload / workReportForm.quotaEfficiency
    workReportForm.performanceManDays = performance.toFixed(5)
  } else {
    workReportForm.performanceManDays = '0.00000'
  }
}

// 处理保存工作汇报
const handleSaveWorkReport = async () => {
  if (!workReportFormRef.value) return
  
  try {
    await workReportFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 根据项目ID查找项目对象
    const selectedProject = projectOptions.value.find(p => p.id === workReportForm.project)
    if (!selectedProject) {
      ElMessage.error('请选择有效的生产项目')
      return
    }
    
    // 构建工作汇报数据
    const workReportData = {
      uid: user.id,
      date: workReportForm.date,
      project: selectedProject.projectName,
      process: workReportForm.process,
      quotaEfficiency: workReportForm.quotaEfficiency,
      actualWorkload: workReportForm.actualWorkload,
      performanceManDays: parseFloat(workReportForm.performanceManDays)
    }
    
    // 调用API保存工作汇报
    const response = await saveDailyPerformance(workReportData) as any
    
    if (response.code === 200 && response.success) {
      ElMessage.success('工作汇报保存成功')
      showAddWorkReportDialog.value = false
      
      // 重新加载绩效记录列表
      await loadWorkReportList()
      
      // 重置表单
      workReportForm.id = 0
      workReportForm.date = ''
      workReportForm.project = null
      workReportForm.process = ''
      workReportForm.quotaEfficiency = 0
      workReportForm.actualWorkload = 0
      workReportForm.performanceManDays = '0.00000'
    } else {
      // 处理错误信息，转换为用户友好的提示
      let errorMessage = response.message
      if (errorMessage.includes('Duplicate entry') || errorMessage.includes('重复')) {
        errorMessage = '该绩效记录已存在，请检查日期和项目'
      }
      ElMessage.error('工作汇报保存失败：' + errorMessage)
    }
  } catch (error) {
    console.error('保存工作汇报失败：', error)
    ElMessage.error('保存工作汇报失败')
  }
}

// 加载绩效记录列表
const loadWorkReportList = async () => {
  try {
    workReportTableLoading.value = true
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取绩效记录列表
    const response = await getDailyPerformances(user.id) as any
    
    if (response.code === 200 && response.success) {
      const allData = response.data
      workReportPagination.total = allData.length
      
      // 前端分页处理
      const startIndex = (workReportPagination.current - 1) * workReportPagination.pageSize
      const endIndex = startIndex + workReportPagination.pageSize
      workReportList.value = allData.slice(startIndex, endIndex)
    } else {
      ElMessage.error('获取绩效记录列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取绩效记录列表失败：', error)
    ElMessage.error('获取绩效记录列表失败')
  } finally {
    workReportTableLoading.value = false
  }
}

// 处理绩效记录选择变化
const handleWorkReportSelectionChange = (rows: any[]) => {
  selectedWorkReports.value = rows
  selectedWorkReportKeys.value = rows.map(row => row.id)
}

// 处理添加绩效记录
const handleAddWorkReport = () => {
  // 重置表单
  workReportForm.id = 0
  workReportForm.date = ''
  workReportForm.project = null
  workReportForm.process = ''
  workReportForm.quotaEfficiency = 0
  workReportForm.actualWorkload = 0
  workReportForm.performanceManDays = '0.00000'
  
  // 重置表单校验
  if (workReportFormRef.value) {
    workReportFormRef.value.resetFields()
  }
  
  isEditWorkReport.value = false
  showAddWorkReportDialog.value = true
}

// 处理编辑绩效记录
const handleEditWorkReport = async (row: any) => {
  // 重置表单校验
  if (workReportFormRef.value) {
    workReportFormRef.value.resetFields()
  }
  
  // 先设置弹窗显示状态，触发 projectOptions 加载
  isEditWorkReport.value = true
  showAddWorkReportDialog.value = true
  
  // 等待 projectOptions 加载完成
  await loadProjectOptions()
  
  // 填充表单
  workReportForm.id = row.id
  workReportForm.date = row.date
  
  // 查找对应的生产项目并设置ID
  const project = projectOptions.value.find(p => p.projectName === row.project)
  workReportForm.project = project ? project.id : null
  
  workReportForm.process = row.process
  workReportForm.quotaEfficiency = row.quotaEfficiency
  workReportForm.actualWorkload = row.actualWorkload
  workReportForm.performanceManDays = row.performanceManDays.toString()
}

// 处理删除绩效记录
const handleDeleteWorkReport = (id: number) => {
  ElMessageBox.confirm('确定要删除这条绩效记录吗？', '删除确认', {
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
      
      // 调用API删除绩效记录
      const response = await deleteDailyPerformance(id, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        workReportList.value = workReportList.value.filter(item => item.id !== id)
        workReportPagination.total--
        
        ElMessage.success('绩效记录删除成功')
      } else {
        ElMessage.error('绩效记录删除失败：' + response.message)
      }
    } catch (error) {
      console.error('删除绩效记录失败：', error)
      ElMessage.error('删除绩效记录失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除绩效记录
const handleBatchDeleteWorkReports = () => {
  if (selectedWorkReportKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的绩效记录')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedWorkReportKeys.value.length} 条绩效记录吗？`, '批量删除确认', {
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
      
      // 批量删除，一条一条删除
      let successCount = 0
      for (const id of selectedWorkReportKeys.value) {
        const response = await deleteDailyPerformance(id, user.id) as any
        if (response.code === 200 && response.success) {
          successCount++
        }
      }
      
      // 更新本地数据
      workReportList.value = workReportList.value.filter(item => !selectedWorkReportKeys.value.includes(item.id))
      workReportPagination.total -= successCount
      
      // 清空选择
      selectedWorkReportKeys.value = []
      selectedWorkReports.value = []
      
      ElMessage.success(`批量删除成功，共删除 ${successCount} 条绩效记录`)
    } catch (error) {
      console.error('批量删除绩效记录失败：', error)
      ElMessage.error('批量删除绩效记录失败')
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 处理绩效记录分页大小变化
const handleWorkReportSizeChange = (size: number) => {
  workReportPagination.pageSize = size
  loadWorkReportList()
}

// 处理绩效记录当前页变化
const handleWorkReportCurrentChange = (current: number) => {
  workReportPagination.current = current
  loadWorkReportList()
}

// 加载工资记录列表
const loadSalaryRecordList = async () => {
  try {
    salaryRecordTableLoading.value = true
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取工资记录列表
    const response = await getSalaryRecords(user.id) as any
    
    if (response.code === 200 && response.success) {
      const allData = response.data
      salaryRecordPagination.total = allData.length
      
      // 前端分页处理
      const startIndex = (salaryRecordPagination.current - 1) * salaryRecordPagination.pageSize
      const endIndex = startIndex + salaryRecordPagination.pageSize
      salaryRecordList.value = allData.slice(startIndex, endIndex)
    } else {
      ElMessage.error('获取工资记录列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取工资记录列表失败：', error)
    ElMessage.error('获取工资记录列表失败')
  } finally {
    salaryRecordTableLoading.value = false
  }
}

// 处理工资记录选择变化
const handleSalaryRecordSelectionChange = (rows: SalaryRecord[]) => {
  selectedSalaryRecords.value = rows
  selectedSalaryRecordKeys.value = rows.map(row => row.id)
}

// 计算绩效薪资
const calculatePerformanceSalary = () => {
  salaryRecordForm.performanceSalary = 170 * 0.94 * salaryRecordForm.performance
}

// 计算加班薪资
const calculateOvertimeSalary = () => {
  salaryRecordForm.overtimeSalary = salaryRecordForm.overtimeHours * 17
}

// 计算薪资总额
const calculateSalaryTotals = () => {
  // 计算迟到影响的折扣率
  const totalWorkMinutes = salaryRecordForm.attendanceDays * 8 * 60
  const actualWorkMinutes = totalWorkMinutes - salaryRecordForm.lateDeduction
  const discountRate = totalWorkMinutes > 0 ? actualWorkMinutes / totalWorkMinutes : 0
  
  // 应用折扣率计算实际金额
  const actualBaseSalary = salaryRecordForm.baseSalary * discountRate
  const actualPositionPerformance = salaryRecordForm.positionPerformance * discountRate
  const actualMealAllowance = salaryRecordForm.mealAllowance * discountRate
  const actualHousingAllowance = salaryRecordForm.housingAllowance * discountRate
  
  // 计算应发总额
  salaryRecordForm.totalIncome = actualBaseSalary + 
                                salaryRecordForm.performanceSalary + 
                                actualPositionPerformance + 
                                actualMealAllowance + 
                                actualHousingAllowance + 
                                salaryRecordForm.fullAttendance + 
                                salaryRecordForm.otherBonus + 
                                salaryRecordForm.overtimeSalary
  
  // 计算扣除总额
  salaryRecordForm.totalDeduction = salaryRecordForm.pension + 
                                   salaryRecordForm.medical + 
                                   salaryRecordForm.unemployment
  
  // 计算实发薪资
  salaryRecordForm.netSalary = salaryRecordForm.totalIncome - salaryRecordForm.totalDeduction
}

// 处理年月变化，自动计算周期开始和结束日期
const handleYearMonthChange = async () => {
  if (!salaryRecordForm.yearMonth) return
  
  const [yearStr, monthStr] = salaryRecordForm.yearMonth.split('-')
  if (!yearStr || !monthStr) return
  
  const year = parseInt(yearStr)
  const month = parseInt(monthStr)
  
  // 计算周期开始日期（上个月26日）
  let startYear = year
  let startMonth = month - 1
  if (startMonth === 0) {
    startYear--
    startMonth = 12
  }
  const periodStart = `${startYear}-${String(startMonth).padStart(2, '0')}-26`
  
  // 计算周期结束日期（当月25日）
  const periodEnd = `${year}-${monthStr}-25`
  
  // 更新表单数据
  salaryRecordForm.periodStart = periodStart
  salaryRecordForm.periodEnd = periodEnd
  
  // 获取该时间范围内的加班时长总和和绩效系数
  const user = getCurrentUser()
  if (user) {
    try {
      // 获取加班记录
      const overtimeResponse = await getOvertimeReportsByDateRange(user.id, periodStart, periodEnd) as any
      let totalOvertimeHours = 0
      if (overtimeResponse.code === 200 && overtimeResponse.success) {
        const overtimeReports = overtimeResponse.data
        // 计算加班时长总和
        totalOvertimeHours = overtimeReports.reduce((sum: number, item: any) => {
          return sum + parseFloat(item.overtimeHours?.toString() || '0')
        }, 0)
        // 将总和赋值给加班时长字段
        salaryRecordForm.overtimeHours = totalOvertimeHours
      }
      
      // 获取绩效记录，计算绩效系数
      const performanceResponse = await getDailyPerformancesByDateRange(user.id, periodStart, periodEnd) as any
      if (performanceResponse.code === 200 && performanceResponse.success) {
        const performances = performanceResponse.data
        
        // 计算绩效总和
        const totalPerformance = performances.reduce((sum: number, item: any) => {
          return sum + parseFloat(item.performanceManDays.toString())
        }, 0)
        
        // 计算考勤天数（去重后的日期数量）
        const uniqueDates = new Set(performances.map((item: any) => item.date))
        const attendanceDays = uniqueDates.size
        
        // 计算实际绩效（减去考勤天数，因为每天的1是定额）
        let actualPerformance = totalPerformance - attendanceDays
        
        // 计算需要扣除的绩效人天（加班时长总和 ÷ 8）
        const overtimeDeduction = totalOvertimeHours / 8
        
        // 从实际绩效中扣除加班人天
        actualPerformance -= overtimeDeduction
        
        // 将考勤天数和实际绩效作为绩效系数赋值给表单
        salaryRecordForm.attendanceDays = attendanceDays
        salaryRecordForm.performance = actualPerformance
      }
    } catch (error) {
      console.error('获取数据失败：', error)
    }
  }
  
  // 重新计算绩效薪资和加班薪资
  calculatePerformanceSalary()
  calculateOvertimeSalary()
  calculateSalaryTotals()
}

// 处理添加工资记录
const handleAddSalaryRecord = () => {
  // 重置表单
  salaryRecordForm.id = 0
  salaryRecordForm.uid = ''
  salaryRecordForm.yearMonth = ''
  salaryRecordForm.periodStart = ''
  salaryRecordForm.periodEnd = ''
  salaryRecordForm.attendanceDays = defaultConfig.attendanceDays
  salaryRecordForm.baseSalary = defaultConfig.baseSalary
  salaryRecordForm.performance = defaultConfig.performance
  salaryRecordForm.performanceSalary = 0
  salaryRecordForm.positionPerformance = defaultConfig.positionPerformance
  salaryRecordForm.mealAllowance = defaultConfig.mealAllowance
  salaryRecordForm.housingAllowance = defaultConfig.housingAllowance
  salaryRecordForm.fullAttendance = defaultConfig.fullAttendance
  salaryRecordForm.otherBonus = defaultConfig.otherBonus
  salaryRecordForm.pension = defaultConfig.pension
  salaryRecordForm.medical = defaultConfig.medical
  salaryRecordForm.unemployment = defaultConfig.unemployment
  salaryRecordForm.lateDeduction = 0
  salaryRecordForm.overtimeHours = 0
  salaryRecordForm.overtimeSalary = 0
  salaryRecordForm.totalIncome = 0
  salaryRecordForm.totalDeduction = 0
  salaryRecordForm.netSalary = 0
  
  // 重置表单校验
  if (salaryRecordFormRef.value) {
    salaryRecordFormRef.value.resetFields()
  }
  
  isEditSalaryRecord.value = false
  showAddSalaryRecordDialog.value = true
}

// 处理编辑工资记录
const handleEditSalaryRecord = (row: SalaryRecord) => {
  // 重置表单校验
  if (salaryRecordFormRef.value) {
    salaryRecordFormRef.value.resetFields()
  }
  
  // 填充表单
  salaryRecordForm.id = row.id
  salaryRecordForm.uid = row.uid
  salaryRecordForm.yearMonth = row.yearMonth
  salaryRecordForm.periodStart = row.periodStart
  salaryRecordForm.periodEnd = row.periodEnd
  salaryRecordForm.attendanceDays = row.attendanceDays
  salaryRecordForm.baseSalary = row.baseSalary
  salaryRecordForm.performance = row.performance
  salaryRecordForm.performanceSalary = row.performanceSalary
  salaryRecordForm.positionPerformance = row.positionPerformance
  salaryRecordForm.mealAllowance = row.mealAllowance
  salaryRecordForm.housingAllowance = row.housingAllowance
  salaryRecordForm.fullAttendance = row.fullAttendance
  salaryRecordForm.otherBonus = row.otherBonus
  salaryRecordForm.pension = row.pension
  salaryRecordForm.medical = row.medical
  salaryRecordForm.unemployment = row.unemployment
  salaryRecordForm.lateDeduction = row.lateDeduction
  salaryRecordForm.overtimeHours = row.overtimeHours
  salaryRecordForm.overtimeSalary = row.overtimeSalary
  salaryRecordForm.totalIncome = row.totalIncome
  salaryRecordForm.totalDeduction = row.totalDeduction
  salaryRecordForm.netSalary = row.netSalary
  
  isEditSalaryRecord.value = true
  showAddSalaryRecordDialog.value = true
}

// 处理保存工资记录
const handleSaveSalaryRecord = async () => {
  if (!salaryRecordFormRef.value) return
  
  try {
    await salaryRecordFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 设置用户ID
    salaryRecordForm.uid = user.id
    
    if (isEditSalaryRecord.value) {
      // 修改工资记录
      const response = await updateSalaryRecord(salaryRecordForm) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        const index = salaryRecordList.value.findIndex(item => item.id === salaryRecordForm.id)
        if (index !== -1) {
          salaryRecordList.value[index] = { ...salaryRecordForm }
        }
        
        ElMessage.success('工资记录修改成功')
      } else {
        ElMessage.error('工资记录修改失败：' + response.message)
      }
    } else {
      // 添加工资记录
      const response = await saveSalaryRecord(salaryRecordForm) as any
      
      if (response.code === 200 && response.success) {
        // 重新加载工资记录列表
        await loadSalaryRecordList()
        
        ElMessage.success('工资记录添加成功')
      } else {
        // 处理错误信息，转换为用户友好的提示
        let errorMessage = response.message
        if (errorMessage.includes('Duplicate entry') && errorMessage.includes('idx_uid_month')) {
          errorMessage = '该月份的工资记录已存在，请修改或删除原记录后重新添加'
        }
        ElMessage.error('工资记录添加失败：' + errorMessage)
      }
    }
    
    showAddSalaryRecordDialog.value = false
  } catch (error) {
    console.error('保存工资记录失败：', error)
    ElMessage.error('保存工资记录失败')
  }
}

// 处理删除工资记录
const handleDeleteSalaryRecord = (id: number) => {
  ElMessageBox.confirm('确定要删除这条工资记录吗？', '删除确认', {
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
      
      // 调用API删除工资记录
      const response = await deleteSalaryRecord(id, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        salaryRecordList.value = salaryRecordList.value.filter(item => item.id !== id)
        salaryRecordPagination.total--
        
        ElMessage.success('工资记录删除成功')
      } else {
        ElMessage.error('工资记录删除失败：' + response.message)
      }
    } catch (error) {
      console.error('删除工资记录失败：', error)
      ElMessage.error('删除工资记录失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除工资记录
const handleBatchDeleteSalaryRecords = () => {
  if (selectedSalaryRecordKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的工资记录')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedSalaryRecordKeys.value.length} 条工资记录吗？`, '批量删除确认', {
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
      
      // 调用API批量删除工资记录
      const response = await batchDeleteSalaryRecords(selectedSalaryRecordKeys.value, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        salaryRecordList.value = salaryRecordList.value.filter(item => !selectedSalaryRecordKeys.value.includes(item.id))
        salaryRecordPagination.total -= selectedSalaryRecordKeys.value.length
        
        // 清空选择
        selectedSalaryRecordKeys.value = []
        selectedSalaryRecords.value = []
        
        ElMessage.success('批量删除成功')
      } else {
        ElMessage.error('批量删除失败：' + response.message)
      }
    } catch (error) {
      console.error('批量删除工资记录失败：', error)
      ElMessage.error('批量删除工资记录失败')
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 处理工资记录分页大小变化
const handleSalaryRecordSizeChange = (size: number) => {
  salaryRecordPagination.pageSize = size
  loadSalaryRecordList()
}

// 处理工资记录当前页变化
const handleSalaryRecordCurrentChange = (current: number) => {
  salaryRecordPagination.current = current
  loadSalaryRecordList()
}

// 监听工作汇报弹窗显示状态，加载项目选项和绩效记录列表
watch(() => showWorkReportDialog.value, (newVal) => {
  if (newVal) {
    loadProjectOptions()
    loadWorkReportList()
  }
})

// 监听工资记录弹窗显示状态，加载工资记录列表
watch(() => showSalaryRecordDialog.value, (newVal) => {
  if (newVal) {
    loadSalaryRecordList()
  }
})

// 监听加班汇报弹窗显示状态，加载加班记录列表和项目选项
watch(() => showOvertimeReportDialog.value, (newVal) => {
  if (newVal) {
    // 加载项目选项，用于加班记录的项目名称下拉选择
    loadProjectOptions()
    // 加载加班记录列表
    loadOvertimeReportList()
  }
})

// 加载加班记录列表
const loadOvertimeReportList = async () => {
  try {
    overtimeReportTableLoading.value = true
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取加班记录列表
    const response = await getOvertimeReports(user.id) as any
    
    if (response.code === 200 && response.success) {
      const allData = response.data
      overtimeReportPagination.total = allData.length
      
      // 前端分页处理
      const startIndex = (overtimeReportPagination.current - 1) * overtimeReportPagination.pageSize
      const endIndex = startIndex + overtimeReportPagination.pageSize
      overtimeReportList.value = allData.slice(startIndex, endIndex)
    } else {
      ElMessage.error('获取加班记录列表失败：' + response.message)
    }
  } catch (error) {
    console.error('获取加班记录列表失败：', error)
    ElMessage.error('获取加班记录列表失败')
  } finally {
    overtimeReportTableLoading.value = false
  }
}

// 处理加班记录选择变化
const handleOvertimeReportSelectionChange = (rows: any[]) => {
  selectedOvertimeReports.value = rows
  selectedOvertimeReportKeys.value = rows.map(row => row.id)
}

// 处理添加加班记录
const handleAddOvertimeReport = () => {
  // 重置表单
  overtimeReportForm.id = 0
  overtimeReportForm.reportDate = ''
  overtimeReportForm.overtimeHours = 0
  overtimeReportForm.projectName = ''
  overtimeReportForm.description = ''
  
  // 重置表单校验
  if (overtimeReportFormRef.value) {
    overtimeReportFormRef.value.resetFields()
  }
  
  isEditOvertimeReport.value = false
  showAddOvertimeReportDialog.value = true
}

// 处理编辑加班记录
const handleEditOvertimeReport = (row: any) => {
  // 重置表单校验
  if (overtimeReportFormRef.value) {
    overtimeReportFormRef.value.resetFields()
  }
  
  // 填充表单
  overtimeReportForm.id = row.id
  overtimeReportForm.reportDate = row.reportDate
  overtimeReportForm.overtimeHours = row.overtimeHours
  overtimeReportForm.projectName = row.projectName || ''
  overtimeReportForm.description = row.description || ''
  
  isEditOvertimeReport.value = true
  showAddOvertimeReportDialog.value = true
}

// 处理保存加班记录
const handleSaveOvertimeReport = async () => {
  if (!overtimeReportFormRef.value) return
  
  try {
    await overtimeReportFormRef.value.validate()
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 构建加班记录数据
    const overtimeReportData = {
      uid: user.id,
      reportDate: overtimeReportForm.reportDate,
      overtimeHours: overtimeReportForm.overtimeHours,
      projectName: overtimeReportForm.projectName,
      description: overtimeReportForm.description
    }
    
    let response: any
    if (isEditOvertimeReport.value) {
      // 修改加班记录
      response = await updateOvertimeReport({
        ...overtimeReportData,
        id: overtimeReportForm.id
      })
    } else {
      // 添加加班记录
      response = await saveOvertimeReport(overtimeReportData)
    }
    
    if (response.code === 200 && response.success) {
      ElMessage.success(isEditOvertimeReport.value ? '加班记录修改成功' : '加班记录添加成功')
      showAddOvertimeReportDialog.value = false
      
      // 重新加载加班记录列表
      await loadOvertimeReportList()
      
      // 重置表单
      overtimeReportForm.id = 0
      overtimeReportForm.reportDate = ''
      overtimeReportForm.overtimeHours = 0
      overtimeReportForm.projectName = ''
      overtimeReportForm.description = ''
    } else {
      // 处理错误信息，转换为用户友好的提示
      let errorMessage = response.message
      if (errorMessage.includes('Duplicate entry') && errorMessage.includes('unique_report')) {
        errorMessage = '该日期的加班记录已存在，请修改或删除原记录后重新添加'
      }
      ElMessage.error((isEditOvertimeReport.value ? '加班记录修改失败：' : '加班记录添加失败：') + errorMessage)
    }
  } catch (error) {
    console.error('保存加班记录失败：', error)
    ElMessage.error('保存加班记录失败')
  }
}

// 处理删除加班记录
const handleDeleteOvertimeReport = (id: number) => {
  ElMessageBox.confirm('确定要删除这条加班记录吗？', '删除确认', {
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
      
      // 调用API删除加班记录
      const response = await deleteOvertimeReport(id, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        overtimeReportList.value = overtimeReportList.value.filter(item => item.id !== id)
        overtimeReportPagination.total--
        
        ElMessage.success('加班记录删除成功')
      } else {
        ElMessage.error('加班记录删除失败：' + response.message)
      }
    } catch (error) {
      console.error('删除加班记录失败：', error)
      ElMessage.error('删除加班记录失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理批量删除加班记录
const handleBatchDeleteOvertimeReports = () => {
  if (selectedOvertimeReportKeys.value.length === 0) {
    ElMessage.warning('请选择要删除的加班记录')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedOvertimeReportKeys.value.length} 条加班记录吗？`, '批量删除确认', {
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
      
      // 调用API批量删除加班记录
      const response = await batchDeleteOvertimeReports(selectedOvertimeReportKeys.value, user.id) as any
      
      if (response.code === 200 && response.success) {
        // 更新本地数据
        overtimeReportList.value = overtimeReportList.value.filter(item => !selectedOvertimeReportKeys.value.includes(item.id))
        overtimeReportPagination.total -= selectedOvertimeReportKeys.value.length
        
        // 清空选择
        selectedOvertimeReportKeys.value = []
        selectedOvertimeReports.value = []
        
        ElMessage.success('批量删除成功')
      } else {
        ElMessage.error('批量删除失败：' + response.message)
      }
    } catch (error) {
      console.error('批量删除加班记录失败：', error)
      ElMessage.error('批量删除加班记录失败')
    }
  }).catch(() => {
    // 取消批量删除
  })
}

// 处理加班记录分页大小变化
const handleOvertimeReportSizeChange = (size: number) => {
  overtimeReportPagination.pageSize = size
  loadOvertimeReportList()
}

// 处理加班记录当前页变化
const handleOvertimeReportCurrentChange = (current: number) => {
  overtimeReportPagination.current = current
  loadOvertimeReportList()
}

// 计算本月的开始和结束日期（26日到下个月25日）
const getCurrentMonthRange = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  
  let startDate: Date
  let endDate: Date
  
  if (now.getDate() >= 26) {
    startDate = new Date(year, month, 26)
    endDate = new Date(year, month + 1, 25)
  } else {
    startDate = new Date(year, month - 1, 26)
    endDate = new Date(year, month, 25)
  }
  
  // 格式化日期为YYYY-MM-DD，使用本地时区
  const formatDate = (date: Date) => {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }
  
  return {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate)
  }
}

// 计算本周的开始和结束日期（周一到周日）
const getCurrentWeekRange = () => {
  const now = new Date()
  const dayOfWeek = now.getDay() || 7 // 将周日从0改为7
  
  // 计算本周一的日期
  const monday = new Date(now)
  monday.setDate(now.getDate() - dayOfWeek + 1)
  
  // 计算本周日的日期
  const sunday = new Date(monday)
  sunday.setDate(monday.getDate() + 6)
  
  // 格式化日期为YYYY-MM-DD，使用本地时区
  const formatDate = (date: Date) => {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }
  
  return {
    startDate: formatDate(monday),
    endDate: formatDate(sunday)
  }
}

// 加载绩效数据
const loadPerformanceData = async (startDate: string, endDate: string) => {
  try {
    performanceLoading.value = true
    
    const user = getCurrentUser()
    if (!user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 调用API获取绩效记录
    const performanceResponse = await getDailyPerformancesByDateRange(user.id, startDate, endDate) as any
    
    if (performanceResponse.code === 200 && performanceResponse.success) {
      const performances = performanceResponse.data
      
      // 计算绩效总和
      const totalPerformance = performances.reduce((sum: number, item: any) => {
        return sum + parseFloat(item.performanceManDays.toString())
      }, 0)
      
      // 计算考勤天数（去重后的日期数量）
      const uniqueDates = new Set(performances.map((item: any) => item.date))
      const attendanceDays = uniqueDates.size
      
      // 计算实际绩效（减去考勤天数，因为每天的1是定额）
      let actualPerformance = totalPerformance - attendanceDays
      
      // 获取该时间范围内的加班记录
      const overtimeResponse = await getOvertimeReportsByDateRange(user.id, startDate, endDate) as any
      if (overtimeResponse.code === 200 && overtimeResponse.success) {
        const overtimeReports = overtimeResponse.data
        
        // 计算加班时长总和
        const totalOvertimeHours = overtimeReports.reduce((sum: number, item: any) => {
          return sum + parseFloat(item.overtimeHours?.toString() || '0')
        }, 0)
        
        // 计算需要扣除的绩效人天（加班时长总和 ÷ 8）
        const overtimeDeduction = totalOvertimeHours / 8
        
        // 从实际绩效中扣除加班人天
        actualPerformance -= overtimeDeduction
      }
      
      // 计算日均绩效
      const avgPerformance = attendanceDays > 0 ? actualPerformance / attendanceDays : 0
      
      // 更新绩效总和数据
      performanceSummary.totalPerformance = actualPerformance.toFixed(5)
      performanceSummary.attendanceDays = attendanceDays
      performanceSummary.avgPerformance = avgPerformance.toFixed(5)
      
      // 更新图表数据
      updatePerformanceChart(performances, startDate, endDate)
    } else {
      ElMessage.error('获取绩效数据失败：' + performanceResponse.message)
    }
  } catch (error) {
    console.error('获取绩效数据失败：', error)
    ElMessage.error('获取绩效数据失败')
  } finally {
    performanceLoading.value = false
  }
}

// 初始化绩效图表
const initPerformanceChart = () => {
  if (performanceChartRef.value) {
    performanceChart = echarts.init(performanceChartRef.value)
    
    // 设置图表默认选项
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: function(params: any) {
          return params[0].name + '<br/>绩效人天：' + params[0].value.toFixed(5)
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
        boundaryGap: false,
        data: []
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}'
        }
      },
      series: [
        {
          name: '绩效人天',
          type: 'line',
          smooth: true,
          data: [],
          itemStyle: {
            color: '#667eea'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(102, 126, 234, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(102, 126, 234, 0.1)'
              }
            ])
          }
        }
      ]
    }
    
    performanceChart.setOption(option)
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      performanceChart?.resize()
    })
  }
}

// 更新绩效图表数据
const updatePerformanceChart = (performances: any[], startDate: string, endDate: string) => {
  if (!performanceChart) return
  
  // 生成日期数组
  const dates = []
  const start = new Date(startDate)
  const end = new Date(endDate)
  
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    dates.push(d.toISOString().split('T')[0])
  }
  
  // 构建绩效数据映射，同一日期的数据累加
  const performanceMap = new Map()
  performances.forEach(item => {
    const date = item.date
    const performance = parseFloat(item.performanceManDays.toString())
    if (performanceMap.has(date)) {
      performanceMap.set(date, performanceMap.get(date)! + performance)
    } else {
      performanceMap.set(date, performance)
    }
  })
  
  // 构建图表数据
  const chartData = dates.map(date => performanceMap.get(date) || 0)
  
  // 更新图表选项
  performanceChart.setOption({
    xAxis: {
      data: dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}'
      },
      // 添加分界线，标识每天的定额1
      axisLine: {
        show: true
      },
      splitLine: {
        show: true
      }
    },
    series: [
      {
        data: chartData,
        // 添加水平参考线，y=1
        markLine: {
          silent: true,
          data: [
            {
              yAxis: 1,
              label: {
                formatter: '定额',
                position: 'end'
              },
              lineStyle: {
                color: '#ff4d4f',
                width: 1,
                type: 'dashed'
              }
            }
          ]
        }
      }
    ]
  })
}

// 处理时间范围选择变化
const handleTimeRangeChange = () => {
  if (timeRange.value === 'week') {
    const weekRange = getCurrentWeekRange()
    loadPerformanceData(weekRange.startDate as string, weekRange.endDate as string)
  } else if (timeRange.value === 'month') {
    const monthRange = getCurrentMonthRange()
    loadPerformanceData(monthRange.startDate as string, monthRange.endDate as string)
  } else if (timeRange.value === 'month_select') {
    // 加载月份选项
    generateMonthOptions()
    // 默认选择当月
    const currentMonthRange = getCurrentMonthRange()
    const currentMonth = currentMonthRange.startDate.substring(0, 7) // YYYY-MM
    selectedMonth.value = currentMonth
    handleMonthSelectChange()
  } else if (timeRange.value === 'custom') {
    if (dateRange.value.length === 2) {
      loadPerformanceData(dateRange.value[0] as string, dateRange.value[1] as string)
    }
  }
}

// 生成月份选项
const generateMonthOptions = () => {
  const options = []
  const now = new Date()
  
  // 生成最近12个月的选项
  for (let i = 11; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const value = `${year}-${month.toString().padStart(2, '0')}`
    const label = `${year}年${month}月`
    options.push({ value, label })
  }
  
  monthOptions.value = options
}

// 处理月份选择变化
const handleMonthSelectChange = () => {
  if (!selectedMonth.value) return
  
  const [yearStr, monthStr] = selectedMonth.value.split('-')
  const year = Number(yearStr) || 0
  const month = Number(monthStr) || 1
  
  // 计算该月的绩效范围（26日到下个月25日）
  let startDate: Date
  let endDate: Date
  
  startDate = new Date(year, month - 1, 26)
  endDate = new Date(year, month, 25)
  
  // 格式化日期
  const formatDate = (date: Date) => {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }
  
  loadPerformanceData(formatDate(startDate), formatDate(endDate))
}

// 处理自定义日期范围变化
const handleDateRangeChange = () => {
  if (timeRange.value === 'custom' && dateRange.value.length === 2) {
    loadPerformanceData(dateRange.value[0] as string, dateRange.value[1] as string)
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  
  return `${year}-${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateString: string) => {
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

// 页面加载时初始化绩效显示面板
onMounted(() => {
  // 加载用户默认配置
  loadDefaultConfig()
  
  // 初始化绩效图表
  nextTick(() => {
    initPerformanceChart()
    
    // 加载本月绩效数据
    const monthRange = getCurrentMonthRange()
    loadPerformanceData(monthRange.startDate as string, monthRange.endDate as string)
  })
})
</script>

<style scoped>
.data-statistics {
  width: 100%;
  padding: 0;
  background-color: #f5f7fa;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.top-bar h2 {
  margin: 0;
  font-size: 24px;
  color: #2c3e50;
}

.button-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.table-actions {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-start;
  gap: 12px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.content-area {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  min-height: 600px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 时间范围选择器 */
.time-range-selector {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.custom-date-range {
  flex: 1;
  min-width: 300px;
}

/* 绩效总和展示 */
.performance-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.performance-card {
  background-color: #ffffff;
  color: #2c3e50;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.performance-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.performance-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.performance-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.performance-desc {
  font-size: 12px;
  opacity: 0.8;
}

/* 绩效图表 */
.chart-container {
  background-color: #ffffff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: 1px solid #ebeef5;
}

.chart-container h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}

.chart {
  width: 100%;
  height: 400px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .time-range-selector {
    flex-direction: column;
    align-items: stretch;
  }
  
  .custom-date-range {
    min-width: 100%;
  }
  
  .performance-summary {
    grid-template-columns: 1fr;
  }
  
  .chart {
    height: 300px;
  }
}

/* 表单两列布局 */
.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.form-col {
  flex: 1;
  min-width: 300px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .form-col {
    min-width: 100%;
  }
}
</style>