import request from './request'

/**
 * 每日绩效记录类型定义
 */
export interface DailyPerformance {
  id?: number
  uid: number
  date: string
  project: string
  process: string
  quotaEfficiency: number
  actualWorkload: number
  performanceManDays: number
  createdAt?: string
  updatedAt?: string
}

/**
 * 保存每日绩效记录
 * @param performance 每日绩效记录
 * @returns 保存结果
 */
export const saveDailyPerformance = async (performance: DailyPerformance) => {
  const response = await request({
    url: '/api/performance/add',
    method: 'post',
    data: performance
  })
  return response
}

/**
 * 获取用户的每日绩效记录
 * @param uid 用户ID
 * @param date 日期（可选）
 * @returns 每日绩效记录列表
 */
export const getDailyPerformances = async (uid: number, date?: string) => {
  const response = await request({
    url: '/api/performance/list',
    method: 'get',
    params: { uid, date }
  })
  return response
}

/**
 * 获取用户指定日期范围内的每日绩效记录
 * @param uid 用户ID
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @returns 每日绩效记录列表
 */
export const getDailyPerformancesByDateRange = async (uid: number, startDate: string, endDate: string) => {
  const response = await request({
    url: '/api/performance/range',
    method: 'get',
    params: { uid, startDate, endDate }
  })
  return response
}

/**
 * 删除每日绩效记录
 * @param id 记录ID
 * @param uid 用户ID
 * @returns 删除结果
 */
export const deleteDailyPerformance = async (id: number, uid: number) => {
  const response = await request({
    url: `/api/performance/${id}`,
    method: 'delete',
    params: { uid }
  })
  return response
}
