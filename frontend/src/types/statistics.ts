export interface MerchantStatistics {
  total: number;
  pending: number;
  approved: number;
  rejected: number;
  disabled: number;
  approvalRate: number;
  rejectionRate: number;
}

export interface MerchantTrend {
  date: string;
  count: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISABLED';
}
