import request from './request'
import type { ApiResponse } from './auth'

// 交易类型
export type TransactionType = 'income' | 'expense'

// 交易记录接口
export interface Transaction {
  id: number
  userId: number
  amount: number
  type: TransactionType
  category: string
  description: string
  transactionDate: string
  createdAt: string
}

// 记账表单接口
export interface TransactionForm {
  type: TransactionType
  amount: number
  category: string
  description: string
  transactionDate: string
}

// 交易记录列表响应
export interface TransactionListResponse {
  records: Transaction[]
  total: number
  size: number
  current: number
}

// 添加交易记录
export const addTransaction = async (data: TransactionForm, userId: number): Promise<ApiResponse<Transaction>> => {
  return request.post('/api/transactions', { ...data, userId })
}

// 获取交易记录列表
export const getTransactions = async (params: {
  page: number
  pageSize: number
  startDate?: string
  endDate?: string
  type?: TransactionType
  category?: string
  userId: number
  sort?: string
  order?: string
}): Promise<ApiResponse<TransactionListResponse>> => {
  return request.get('/api/transactions', { params })
}

// 更新交易记录
export const updateTransaction = async (id: number, data: TransactionForm): Promise<ApiResponse<Transaction>> => {
  return request.put(`/api/transactions/${id}`, data)
}

// 删除交易记录
export const deleteTransaction = async (id: number): Promise<ApiResponse> => {
  return request.delete(`/api/transactions/${id}`)
}

// 批量删除交易记录
export const batchDeleteTransactions = async (ids: number[]): Promise<ApiResponse> => {
  return request.delete('/api/transactions/batch', { data: ids })
}

// 获取交易统计数据
export const getTransactionStats = async (params: {
  startDate?: string
  endDate?: string
  userId: number
}): Promise<ApiResponse<{
  totalIncome: number
  totalExpense: number
  balance: number
}>> => {
  return request.get('/api/transactions/stats', { params })
}

// 获取交易图表数据
export const getTransactionChartData = async (params: {
  timeRange: 'week' | 'month' | 'custom'
  type: 'income' | 'expense' | 'both'
  userId: number
  startDate?: string
  endDate?: string
}): Promise<ApiResponse<{
  dates: string[]
  incomeData: number[]
  expenseData: number[]
}>> => {
  return request.get('/api/transactions/chart', { params })
}
