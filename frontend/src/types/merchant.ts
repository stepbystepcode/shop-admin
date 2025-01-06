export interface Merchant {
  id: number;
  name: string;
  status: 'pending' | 'approved' | 'rejected' | 'disabled';
  contactPerson: string;
  phone: string;
  email: string;
  address: string;
  businessLicense: string;
  createdAt: string;
  updatedAt: string;
}

export interface MerchantStats {
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
  total: number;
  pending: number;
  approved: number;
  rejected: number;
}

export interface MerchantLog {
  id: number;
  timestamp: string;
  operator: string;
  merchantId: number;
  merchantName: string;
  operationType: 'AUDIT' | 'UPDATE' | 'STATUS_CHANGE';
  details: string;
  ip: string;
}