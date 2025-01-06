import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { Merchant, MerchantStats } from '@/types/merchant';
import { 
  getMerchants, 
  updateMerchantStatus, 
  getMerchantStats,
  auditMerchant,
  updateMerchant
} from '@/api/merchant';

export const useMerchantStore = defineStore('merchant', () => {
  const merchants = ref<Merchant[]>([]);
  const stats = ref<MerchantStats>({
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0,
    disabled: 0
  });
  const loading = ref(false);
  const currentPage = ref(1);
  const pageSize = ref(10);
  const totalItems = ref(0);
  const searchQuery = ref('');
  const statusFilter = ref<string>('');

  // 计算属性：过滤后的商家列表
  const filteredMerchants = computed(() => {
    return merchants.value.filter(merchant => {
      const matchesSearch = searchQuery.value ? 
        merchant.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        merchant.contactPerson.toLowerCase().includes(searchQuery.value.toLowerCase()) :
        true;
      
      const matchesStatus = statusFilter.value ? 
        merchant.status === statusFilter.value :
        true;

      return matchesSearch && matchesStatus;
    });
  });

  // 获取商家列表
  const fetchMerchants = async (params = {}) => {
    loading.value = true;
    try {
      const response = await getMerchants({
        page: currentPage.value,
        pageSize: pageSize.value,
        ...params
      });
      merchants.value = response.data.list;
      totalItems.value = response.data.total;
      await fetchStats();
    } catch (error) {
      console.error('Failed to fetch merchants:', error);
    } finally {
      loading.value = false;
    }
  };

  // 获取统计数据
  const fetchStats = async () => {
    try {
      const response = await getMerchantStats();
      stats.value = response.data;
    } catch (error) {
      console.error('Failed to fetch stats:', error);
    }
  };

  // 审核商家
  const audit = async (merchantId: number, status: 'approved' | 'rejected', reason?: string) => {
    try {
      await auditMerchant({
        id: merchantId,
        status,
        rejectReason: reason
      });
      await fetchMerchants();
      return true;
    } catch (error) {
      console.error('Failed to audit merchant:', error);
      return false;
    }
  };

  // 更新商家信息
  const update = async (merchantId: number, data: Partial<Merchant>) => {
    try {
      await updateMerchant(merchantId, data);
      await fetchMerchants();
      return true;
    } catch (error) {
      console.error('Failed to update merchant:', error);
      return false;
    }
  };

  // 更新商家状态
  const updateStatus = async (merchantId: number, status: Merchant['status']) => {
    try {
      await updateMerchantStatus(merchantId, status);
      await fetchMerchants();
      return true;
    } catch (error) {
      console.error('Failed to update merchant status:', error);
      return false;
    }
  };

  // 设置分页
  const setPage = (page: number) => {
    currentPage.value = page;
    fetchMerchants();
  };

  const setPageSize = (size: number) => {
    pageSize.value = size;
    currentPage.value = 1; // Reset to first page
    fetchMerchants();
  };

  // 设置搜索和筛选
  const setSearchQuery = (query: string) => {
    searchQuery.value = query;
    currentPage.value = 1; // Reset to first page
    fetchMerchants();
  };

  const setStatusFilter = (status: string) => {
    statusFilter.value = status;
    currentPage.value = 1; // Reset to first page
    fetchMerchants();
  };

  return {
    merchants,
    stats,
    loading,
    currentPage,
    pageSize,
    totalItems,
    searchQuery,
    statusFilter,
    filteredMerchants,
    fetchMerchants,
    fetchStats,
    audit,
    update,
    updateStatus,
    setPage,
    setPageSize,
    setSearchQuery,
    setStatusFilter
  };
});