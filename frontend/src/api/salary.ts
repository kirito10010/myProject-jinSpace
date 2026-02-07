import request from './request'

// 工资记录接口
export interface SalaryRecord {
  id: number
  uid: string
  yearMonth: string
  periodStart: string
  periodEnd: string
  attendanceDays: number
  baseSalary: number
  performance: number
  performanceSalary: number
  positionPerformance: number
  mealAllowance: number
  housingAllowance: number
  fullAttendance: number
  otherBonus: number
  pension: number
  medical: number
  unemployment: number
  lateDeduction: number
  overtimeHours: number
  overtimeSalary: number
  totalIncome: number
  totalDeduction: number
  netSalary: number
  createdAt?: string
  updatedAt?: string
}

// 保存工资记录
export const saveSalaryRecord = async (salaryRecord: Omit<SalaryRecord, 'id' | 'createdAt' | 'updatedAt'>) => {
  return request({
    url: '/api/salary/add',
    method: 'post',
    data: salaryRecord
  })
}

// 更新工资记录
export const updateSalaryRecord = async (salaryRecord: SalaryRecord) => {
  return request({
    url: '/api/salary/update',
    method: 'put',
    data: salaryRecord
  })
}

// 获取工资记录列表
export const getSalaryRecords = async (uid: string) => {
  return request({
    url: '/api/salary/list',
    method: 'get',
    params: { uid }
  })
}

// 删除工资记录
export const deleteSalaryRecord = async (id: number, uid: string) => {
  return request({
    url: `/api/salary/${id}`,
    method: 'delete',
    params: { uid }
  })
}

// 批量删除工资记录
export const batchDeleteSalaryRecords = async (ids: number[], uid: string) => {
  return request({
    url: '/api/salary/batch',
    method: 'delete',
    data: { ids, uid }
  })
}
