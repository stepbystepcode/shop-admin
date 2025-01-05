import request from '@/utils/request';
import type { OperationLog } from '@/types/log';

export const getLogs = (params?: { startTime?: string; endTime?: string }) => {
  return request.get<OperationLog[]>('/api/logs', { params });
};

export const getUserLogs = (username: string) => {
  return request.get<OperationLog[]>(`/api/logs/user/${username}`);
};
