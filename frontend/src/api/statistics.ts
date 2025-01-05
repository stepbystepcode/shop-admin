import request from '@/utils/request';
import type { MerchantStatistics, MerchantTrend } from '@/types/statistics';

export const getMerchantStatistics = () => {
  return request.get<MerchantStatistics>('/api/statistics/merchants');
};

export const getMerchantTrend = (days: number = 7) => {
  return request.get<MerchantTrend[]>('/api/statistics/merchants/trend', {
    params: { days }
  });
};
