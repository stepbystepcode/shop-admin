import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Merchant, MerchantStats } from '@/types/merchant';
import { getMerchants, updateMerchantStatus } from '@/api/merchant';

export const useMerchantStore = defineStore('merchant', () => {
  const merchants = ref<Merchant[]>([]);
  const stats = ref<MerchantStats>({
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0,
    disabled: 0
  });

  const fetchMerchants = async () => {
    try {
      const response = await getMerchants();
      merchants.value = response.data;
      updateStats();
    } catch (error) {
      console.error('Failed to fetch merchants:', error);
    }
  };

  const updateStats = () => {
    stats.value = merchants.value.reduce((acc, merchant) => {
      acc.total++;
      acc[merchant.status]++;
      return acc;
    }, {
      total: 0,
      pending: 0,
      approved: 0,
      rejected: 0,
      disabled: 0
    });
  };

  return {
    merchants,
    stats,
    fetchMerchants
  };
});