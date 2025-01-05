<template>
  <div class="stats-grid">
    <el-card v-for="(value, key) in stats" :key="key">
      <div class="stat-item">
        <h3 class="stat-label">{{ formatLabel(key) }}</h3>
        <p class="stat-value">{{ value }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import type { MerchantStats } from '@/types/merchant';

defineProps<{
  stats: MerchantStats;
}>();

const formatLabel = (key: string): string => {
  return key.charAt(0).toUpperCase() + key.slice(1);
};
</script>

<style scoped lang="scss">
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  color: #666;
  margin: 0 0 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #409EFF;
}
</style>