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
}