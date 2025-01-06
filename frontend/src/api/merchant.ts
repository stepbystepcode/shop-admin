import request from '@/utils/request';
import type { Merchant } from '@/types/merchant';

const api = request;

interface QueryParams {
  page?: number;
  pageSize?: number;
  status?: string;
  name?: string;
  startDate?: string;
  endDate?: string;
}

interface PaginatedResponse<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

// 获取商家列表（支持分页和筛选）
export const getMerchants = (params: QueryParams = {}) => {
  return api.get<PaginatedResponse<Merchant>>('/merchants', { params });
};

// 获取商家详情
export const getMerchantDetail = (id: number) => {
  return api.get<Merchant>(`/merchants/${id}`);
};

// 更新商家状态
export const updateMerchantStatus = (params: {
  id: number;
  status: Merchant['status'];
  rejectReason?: string;
}) => {
  return api.patch(`/merchants/${params.id}/status`, {
    status: params.status,
    rejectReason: params.rejectReason
  });
};

// 按状态获取商家
export const getMerchantsByStatus = (status: string) => {
  return api.get<Merchant[]>(`/merchants/status/${status}`);
};

// 获取商家统计数据
export const getMerchantStats = () => {
  return api.get('/statistics/merchants');
};

// 获取商家入驻趋势数据
export const getMerchantTrends = (days: number = 7) => {
  return api.get('/statistics/merchants/trend', { params: { days } });
};

// 创建商家
export const createMerchant = (merchantData: Partial<Merchant>) => {
  return api.post<Merchant>('/merchants', merchantData);
};

// 更新商家信息
export const updateMerchant = (id: number, merchantData: Partial<Merchant>) => {
  return api.put<Merchant>(`/merchants/${id}`, merchantData);
};

// 审核商家
export const auditMerchant = (params: {
  id: number;
  status: 'APPROVED' | 'REJECTED';
  rejectReason?: string;
}) => {
  return api.patch(`/merchants/${params.id}/status`, {
    status: params.status,
    rejectReason: params.rejectReason
  });
};

// 获取操作日志
export const getMerchantLogs = (params: QueryParams = {}) => {
  return api.get('/logs', { params });
};

// 获取审核统计数据
export const getAuditStats = (params: { startDate?: string; endDate?: string } = {}) => {
  return api.get('/statistics/merchants/audit', { params });
};