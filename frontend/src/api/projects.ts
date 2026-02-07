import request from './request'

// 生产项目类型定义
export interface ProductionProject {
  id: number
  projectCode: string
  projectName: string
  uid: number
  workQuotaEfficiency: number
  inspectQuotaEfficiency: number
  status: number
  createdAt: string
  updatedAt: string
}

/**
 * API响应类型
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  success: boolean
  data: T
}

/**
 * 获取用户的所有生产项目
 * @param uid 用户ID
 * @returns 生产项目列表
 */
export const getProjects = async (uid: number) => {
  const response = await request({
    url: '/api/project/list',
    method: 'get',
    params: { uid }
  })
  return response
}

/**
 * 根据ID和用户ID获取生产项目
 * @param id 项目ID
 * @param uid 用户ID
 * @returns 生产项目
 */
export const getProjectById = async (id: number, uid: number) => {
  const response = await request({
    url: `/api/project/${id}`,
    method: 'get',
    params: { uid }
  })
  return response
}

/**
 * 添加生产项目
 * @param project 生产项目
 * @returns 添加结果
 */
export const addProject = async (project: {
  projectCode: string
  projectName: string
  uid: number
  workQuotaEfficiency: number
  inspectQuotaEfficiency: number
  status: number
}) => {
  const response = await request({
    url: '/api/project/add',
    method: 'post',
    data: project
  })
  return response
}

/**
 * 修改生产项目
 * @param project 生产项目
 * @param uid 用户ID
 * @returns 修改结果
 */
export const updateProject = async (project: {
  id: number
  projectCode: string
  projectName: string
  workQuotaEfficiency: number
  inspectQuotaEfficiency: number
  status: number
}, uid: number) => {
  const response = await request({
    url: '/api/project/update',
    method: 'put',
    params: { uid },
    data: project
  })
  return response
}

/**
 * 删除生产项目
 * @param id 项目ID
 * @param uid 用户ID
 * @returns 删除结果
 */
export const deleteProject = async (id: number, uid: number) => {
  const response = await request({
    url: `/api/project/${id}`,
    method: 'delete',
    params: { uid }
  })
  return response
}

/**
 * 批量删除生产项目
 * @param ids 项目ID列表
 * @param uid 用户ID
 * @returns 删除结果
 */
export const batchDeleteProjects = async (ids: number[], uid: number) => {
  const response = await request({
    url: '/api/project/batchDelete',
    method: 'delete',
    data: { ids, uid }
  })
  return response
}
