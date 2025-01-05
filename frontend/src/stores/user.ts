import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { UserInfo, LoginForm } from '@/types/user';
import { login as loginApi } from '@/api/auth';
import type { LoginResponse } from '@/api/auth';

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null);
  const token = ref<string>(localStorage.getItem('token') || '');

  const login = async (loginForm: LoginForm) => {
    try {
      const { data } = await loginApi(loginForm);
      token.value = data.accessToken;
      localStorage.setItem('token', data.accessToken);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  const logout = () => {
    userInfo.value = null;
    token.value = '';
    localStorage.removeItem('token');
  };

  const isLoggedIn = () => {
    return !!token.value;
  };

  return {
    userInfo,
    token,
    login,
    logout,
    isLoggedIn
  };
});