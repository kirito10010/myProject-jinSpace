import request from './request'

// 反馈类型定义
export interface Feedback {
  id?: number
  uid: number
  content: string
  contact?: string
  feedbackType: number
  status?: number
  createTime?: string
  updateTime?: string
}

// 提交反馈
export const submitFeedback = (data: Feedback) => {
  return request.post('/api/feedback', data)
}

// 获取反馈列表（管理员）
export const getFeedbackList = (params?: any) => {
  return request.get('/api/feedback', { params })
}

// 更新反馈（管理员）
export const updateFeedback = (data: Feedback) => {
  return request.put(`/api/feedback/${data.id}`, data)
}

// 更新反馈状态（管理员）
export const updateFeedbackStatus = (id: number, status: number) => {
  return request.put(`/api/feedback/${id}/status`, { status })
}

// 删除反馈（管理员）
export const deleteFeedback = (id: number) => {
  return request.delete(`/api/feedback/${id}`)
}

// 批量删除反馈（管理员）
export const batchDeleteFeedback = (ids: number[]) => {
  return request.delete('/api/feedback/batch', { data: ids })
}
