import request from './request'

/**
 * 加班记录类型定义
 */
export interface OvertimeReport {
  id?: number
  uid: string
  reportDate: string
  overtimeHours: number
  projectName?: string
  description?: string
  status?: 'pending' | 'approved' | 'rejected'
  createdAt?: string
  updatedAt?: string
}

/**
 * 保存加班记录
 * @param overtimeReport 加班记录
 * @returns 保存结果
 */
export const saveOvertimeReport = async (overtimeReport: OvertimeReport) => {
  const response = await request({
    url: '/api/overtime/add',
    method: 'post',
    data: overtimeReport
  })
  return response
}

/**
 * 获取用户的加班记录
 * @param uid 用户ID
 * @returns 加班记录列表
 */
export const getOvertimeReports = async (uid: string) => {
  const response = await request({
    url: '/api/overtime/list',
    method: 'get',
    params: { uid }
  })
  return response
}

/**
 * 更新加班记录
 * @param overtimeReport 加班记录
 * @returns 更新结果
 */
export const updateOvertimeReport = async (overtimeReport: OvertimeReport) => {
  const response = await request({
    url: '/api/overtime/update',
    method: 'put',
    data: overtimeReport
  })
  return response
}

/**
 * 删除加班记录
 * @param id 记录ID
 * @param uid 用户ID
 * @returns 删除结果
 */
export const deleteOvertimeReport = async (id: number, uid: string) => {
  const response = await request({
    url: `/api/overtime/${id}`,
    method: 'delete',
    params: { uid }
  })
  return response
}

/**
 * 批量删除加班记录
 * @param ids 记录ID列表
 * @param uid 用户ID
 * @returns 删除结果
 */
export const batchDeleteOvertimeReports = async (ids: number[], uid: string) => {
  const response = await request({
    url: '/api/overtime/batch',
    method: 'delete',
    data: { ids, uid }
  })
  return response
}

/**
 * 根据日期范围获取用户的加班记录
 * @param uid 用户ID
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @returns 加班记录列表
 */
export const getOvertimeReportsByDateRange = async (uid: string, startDate: string, endDate: string) => {
  const response = await request({
    url: '/api/overtime/listByDateRange',
    method: 'get',
    params: { uid, startDate, endDate }
  })
  return response
}
