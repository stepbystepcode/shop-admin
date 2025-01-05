<template>
  <el-tag :type="tagType">{{ formatStatus(status) }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { Merchant } from '@/types/merchant';

const props = defineProps<{
  status: Merchant['status'];
}>();

const tagType = computed(() => {
  const types: Record<Merchant['status'], '' | 'success' | 'danger' | 'info'> = {
    pending: '',
    approved: 'success',
    rejected: 'danger',
    disabled: 'info'
  };
  return types[props.status];
});

const formatStatus = (status: string) => {
  return status.charAt(0).toUpperCase() + status.slice(1);
};
</script>