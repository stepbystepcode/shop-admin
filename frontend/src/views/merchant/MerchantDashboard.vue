<template>
  <div class="merchant-dashboard">
    <!-- 商家统计卡片 -->
    <merchant-stats />

    <!-- 商家列表和操作 -->
    <el-tabs v-model="activeTab" class="merchant-tabs">
      <el-tab-pane label="商家列表" name="list">
        <merchant-list />
      </el-tab-pane>
      <el-tab-pane label="操作日志" name="logs">
        <merchant-logs />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MerchantStats from '@/components/merchant/MerchantStats.vue'
import MerchantList from '@/views/merchant/MerchantList.vue'
import MerchantLogs from '@/components/merchant/MerchantLogs.vue'

const activeTab = ref('list')
</script>

<style scoped>
.merchant-dashboard {
  padding: 20px;
}

.merchant-tabs {
  margin-top: 20px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>
