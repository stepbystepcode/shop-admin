import { defineStore } from 'pinia';
import { login } from '@/api/auth';
import type { UserInfo, LoginForm } from '@/types/user';

interface AuthState {
  user: UserInfo | null;
  token: string | null;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    token: localStorage.getItem('token'),
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'admin',
  },

  actions: {
    async login(loginForm: LoginForm) {
      try {
        const response = await login(loginForm);
        const { accessToken } = response.data;
        
        this.token = accessToken;
        localStorage.setItem('token', accessToken);
        
        // 这里可以添加获取用户信息的逻辑
        this.user = {
          id: 1, // 这里应该从后端获取
          username: loginForm.username,
          role: 'admin', // 这里应该从后端获取
          token: accessToken,
        };
        
        return true;
      } catch (error) {
        console.error('Login failed:', error);
        return false;
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      localStorage.removeItem('token');
    },
  },
});
