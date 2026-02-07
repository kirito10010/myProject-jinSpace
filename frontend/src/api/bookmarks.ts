import request from './request'

// 定义接口类型
export interface Bookmark {
  id: number
  uid: number
  url: string
  title?: string
  remark?: string
  createdTime: string
}

export interface BookmarkForm {
  url: string
  title?: string
  remark?: string
}

export interface BookmarkListResponse {
  total: number
  list: Bookmark[]
}

// 获取用户网址列表
export const getBookmarks = (params: { page: number; pageSize: number; uid?: number }) => {
  return request.get<BookmarkListResponse>('/api/bookmarks', { params })
}

// 添加网址
export const addBookmark = (data: BookmarkForm) => {
  return request.post<{ success: boolean; message: string }>('/api/bookmarks', data)
}

// 更新网址
export const updateBookmark = (id: number, data: BookmarkForm) => {
  return request.put<{ success: boolean; message: string }>(`/api/bookmarks/${id}`, data)
}

// 删除网址
export const deleteBookmark = (id: number) => {
  return request.delete<{ success: boolean; message: string }>(`/api/bookmarks/${id}`)
}

// 批量删除网址
export const batchDeleteBookmarks = (ids: number[]) => {
  return request.delete<{ success: boolean; message: string }>('/api/bookmarks/batch', { data: ids })
}
