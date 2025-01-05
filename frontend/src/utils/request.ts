import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  (error) => {
    console.error('Response error:', error)
    const message = error.response?.data?.message || '请求失败'
    ElMessage.error(message)
    
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
    }
    
    return Promise.reject(error)
  }
)

export default service
