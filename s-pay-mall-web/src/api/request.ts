import axios from 'axios'
import { ElMessage } from 'element-plus'

export interface ApiResponse<T> {
  code: string
  info: string
  data: T
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use((response) => {
  const body = response.data as ApiResponse<unknown>
  if (typeof body === 'object' && body && 'code' in body && body.code !== '0000') {
    ElMessage.error(body.info || '请求失败')
    return Promise.reject(new Error(body.info || '请求失败'))
  }
  return response
}, (error: unknown) => {
  if (axios.isAxiosError(error)) {
    const responseData = error.response?.data as { info?: string } | undefined
    const message = responseData?.info || error.message || '网络异常'
    ElMessage.error(message)
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      const current = `${location.pathname}${location.search}`
      if (!location.pathname.startsWith('/login')) {
        location.href = `/login?redirect=${encodeURIComponent(current)}`
      }
    }
  } else {
    ElMessage.error('请求失败，请稍后重试')
  }
  return Promise.reject(error)
})

export default request
