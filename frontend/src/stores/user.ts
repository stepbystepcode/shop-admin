import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { UserInfo, LoginForm } from '@/types/user';
import { login as loginApi } from '@/api/auth';

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null);
  const token = ref<string>('');

  const login = async (loginForm: LoginForm) => {
    try {
      const response = await loginApi(loginForm);
      userInfo.value = response.data;
      token.value = response.data.token;
      localStorage.setItem('token', response.data.token);
      return true;
    } catch (error) {
      return false;
    }
  };

  const logout = () => {
    userInfo.value = null;
    token.value = '';
    localStorage.removeItem('token');
  };

  return {
    userInfo,
    token,
    login,
    logout
  };
});