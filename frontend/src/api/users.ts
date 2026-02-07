import request from './request'
import type { UserInfo, ApiResponse } from './auth'

export interface UpdateUserParams {
  password?: string
  nickname: string
  email: string
  role: number
  status: number
}

export const getUsersApi = async (): Promise<ApiResponse<UserInfo[]>> => {
  return request.get('/api/admin/users')
}

export const updateUserApi = async (id: number, params: UpdateUserParams): Promise<ApiResponse<UserInfo>> => {
  return request.put(`/api/admin/users/${id}`, params)
}

export const deleteUserApi = async (id: number): Promise<ApiResponse> => {
  return request.delete(`/api/admin/users/${id}`)
}

export const batchDeleteUsersApi = async (ids: number[]): Promise<ApiResponse> => {
  return request.delete('/api/admin/users/batch', { data: ids })
}
