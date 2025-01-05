<template>
  <div class="dashboard">
    <merchant-stats :stats="merchantStore.stats" />
    <merchant-chart :merchants="merchantStore.merchants" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useMerchantStore } from '@/stores/merchant';
import MerchantStats from '@/components/dashboard/MerchantStats.vue';
import MerchantChart from '@/components/dashboard/MerchantChart.vue';

const merchantStore = useMerchantStore();

onMounted(() => {
  merchantStore.fetchMerchants();
});
</script>

<style scoped lang="scss">
.dashboard {
  display: grid;
  gap: 24px;
  padding: 24px;
}
</style>