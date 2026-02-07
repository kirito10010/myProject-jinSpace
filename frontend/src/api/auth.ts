import request from './request'

export interface RegisterParams {
  username: string
  password: string
  nickname: string
  email: string
}

export interface LoginParams {
  usernameOrEmail: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  password: string | null
  nickname: string
  email: string
  role: number
  status: number
  createTime: string
  updateTime: string
  lastLoginTime: string | null
}

export interface ApiResponse<T = UserInfo> {
  code: number
  data: T
  success: boolean
  message: string
}

export const registerApi = async (params: RegisterParams): Promise<ApiResponse> => {
  return request.post('/api/auth/register', params)
}

export const loginApi = async (params: LoginParams): Promise<ApiResponse> => {
  return request.post('/api/auth/login', params)
}