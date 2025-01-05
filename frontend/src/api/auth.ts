import request from '@/utils/request'

export interface LoginData {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
}

export const login = (data: LoginData) => {
  return request<LoginResponse>({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

export const getUserInfo = () => {
  return request({
    url: '/api/auth/user',
    method: 'get'
  })
}