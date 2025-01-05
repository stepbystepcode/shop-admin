<template>
  <div class="merchant-detail" v-loading="loading">
    <div class="header">
      <h2>Merchant Details</h2>
      <merchant-status-tag v-if="merchant" :status="merchant.status" />
    </div>

    <el-descriptions v-if="merchant" :column="2" border>
      <el-descriptions-item label="Name">
        {{ merchant.name }}
      </el-descriptions-item>
      <el-descriptions-item label="Contact Person">
        {{ merchant.contactPerson }}
      </el-descriptions-item>
      <el-descriptions-item label="Phone">
        {{ merchant.phone }}
      </el-descriptions-item>
      <el-descriptions-item label="Email">
        {{ merchant.email }}
      </el-descriptions-item>
      <el-descriptions-item label="Address" :span="2">
        {{ merchant.address }}
      </el-descriptions-item>
      <el-descriptions-item label="Business License" :span="2">
        <el-image
          :src="merchant.businessLicense"
          fit="contain"
          :preview-src-list="[merchant.businessLicense]"
        />
      </el-descriptions-item>
      <el-descriptions-item label="Created At">
        {{ new Date(merchant.createdAt).toLocaleString() }}
      </el-descriptions-item>
      <el-descriptions-item label="Updated At">
        {{ new Date(merchant.updatedAt).toLocaleString() }}
      </el-descriptions-item>
    </el-descriptions>

    <div v-if="merchant?.status === 'pending'" class="actions">
      <el-button type="success" @click="handleStatusChange('approved')">
        Approve
      </el-button>
      <el-button type="danger" @click="handleStatusChange('rejected')">
        Reject
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useMerchantStore } from '@/stores/merchant';
import { updateMerchantStatus } from '@/api/merchant';
import MerchantStatusTag from '@/components/merchant/MerchantStatusTag.vue';
import type { Merchant } from '@/types/merchant';

const route = useRoute();
const router = useRouter();
const merchantStore = useMerchantStore();
const loading = ref(false);

const merchant = computed(() => 
  merchantStore.merchants.find(m => m.id === Number(route.params.id))
);

const handleStatusChange = async (status: Merchant['status']) => {
  if (!merchant.value) return;
  
  try {
    await updateMerchantStatus(merchant.value.id, status);
    await merchantStore.fetchMerchants();
    ElMessage.success(`Merchant ${status} successfully`);
    router.push('/merchants');
  } catch (error) {
    ElMessage.error('Failed to update merchant status');
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    if (!merchantStore.merchants.length) {
      await merchantStore.fetchMerchants();
    }
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.merchant-detail {
  padding: 24px;

  .header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      margin: 0;
    }
  }

  .actions {
    margin-top: 24px;
    display: flex;
    gap: 16px;
  }

  :deep(.el-descriptions__cell) {
    padding: 16px;
  }
}
</style>