import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: '',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 响应拦截器 - 确保返回response.data，而不是AxiosResponse对象
service.interceptors.response.use(
  response => {
    return response.data // 只返回数据部分
  },
  error => {
    return Promise.reject(error)
  }
)

// 扩展axios实例类型，确保它返回的是response.data的类型，而不是AxiosResponse对象
declare module 'axios' {
  interface AxiosInstance {
    get<T = any>(url: string, config?: any): Promise<T>
    post<T = any>(url: string, data?: any, config?: any): Promise<T>
    put<T = any>(url: string, data?: any, config?: any): Promise<T>
    delete<T = any>(url: string, config?: any): Promise<T>
  }
}

// 导出带有正确类型的axios实例
export default service