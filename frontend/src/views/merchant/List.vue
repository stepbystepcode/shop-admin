<template>
  <div class="merchant-list">
    <div class="header">
      <h2>Merchant List</h2>
      <el-select v-model="statusFilter" placeholder="Filter by status" clearable>
        <el-option
          v-for="status in ['pending', 'approved', 'rejected', 'disabled']"
          :key="status"
          :label="formatStatus(status)"
          :value="status"
        />
      </el-select>
    </div>

    <el-table :data="filteredMerchants" v-loading="loading">
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="status" label="Status">
        <template #default="{ row }">
          <merchant-status-tag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="contactPerson" label="Contact Person" />
      <el-table-column prop="phone" label="Phone" />
      <el-table-column prop="email" label="Email" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button-group>
            <el-button
              type="primary"
              size="small"
              @click="router.push(`/merchants/${row.id}`)"
            >
              View
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="handleStatusChange(row.id, 'approved')"
            >
              Approve
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleStatusChange(row.id, 'rejected')"
            >
              Reject
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useMerchantStore } from '@/stores/merchant';
import { updateMerchantStatus } from '@/api/merchant';
import MerchantStatusTag from '@/components/merchant/MerchantStatusTag.vue';
import type { Merchant } from '@/types/merchant';

const router = useRouter();
const merchantStore = useMerchantStore();
const loading = ref(false);
const statusFilter = ref('');

const filteredMerchants = computed(() => {
  if (!statusFilter.value) return merchantStore.merchants;
  return merchantStore.merchants.filter(m => m.status === statusFilter.value);
});

const formatStatus = (status: string) => {
  return status.charAt(0).toUpperCase() + status.slice(1);
};

const handleStatusChange = async (id: number, status: Merchant['status']) => {
  try {
    await updateMerchantStatus(id, status);
    await merchantStore.fetchMerchants();
    ElMessage.success(`Merchant ${status} successfully`);
  } catch (error) {
    ElMessage.error('Failed to update merchant status');
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    await merchantStore.fetchMerchants();
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.merchant-list {
  padding: 24px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      margin: 0;
    }
  }
}
</style>