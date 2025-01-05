import request from '@/utils/request';
import type { Merchant } from '@/types/merchant';

const api = request;

export const getMerchants = () => {
  return api.get<Merchant[]>('/api/merchants');
};

export const updateMerchantStatus = (id: number, status: Merchant['status']) => {
  return api.patch(`/api/merchants/${id}/status`, { status });
};

export const getMerchantsByStatus = (status: string) => {
  return api.get<Merchant[]>(`/api/merchants/status/${status}`);
};

export const getMerchantStatistics = () => {
  return api.get('/api/merchants/statistics');
};

export const createMerchant = (merchantData: Partial<Merchant>) => {
  return api.post<Merchant>('/api/merchants', merchantData);
};

export const updateMerchant = (id: number, merchantData: Partial<Merchant>) => {
  return api.put<Merchant>(`/api/merchants/${id}`, merchantData);
};