import axios from 'axios';
import type { AxiosInstance } from 'axios';
import { useUserStore } from '@/stores/user';

export const createAPI = (baseURL: string): AxiosInstance => {
  const api = axios.create({ baseURL });

  api.interceptors.request.use((config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    return config;
  });

  return api;
};