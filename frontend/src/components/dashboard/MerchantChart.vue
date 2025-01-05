<template>
  <el-card>
    <div ref="chartRef" class="chart"></div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import type { Merchant } from '@/types/merchant';

const props = defineProps<{
  merchants: Merchant[];
}>();

const chartRef = ref<HTMLElement>();
let chart: echarts.ECharts | null = null;

const initChart = () => {
  if (!chartRef.value) return;
  
  chart = echarts.init(chartRef.value);
  updateChart();
};

const updateChart = () => {
  if (!chart) return;

  const statusCounts = props.merchants.reduce((acc, merchant) => {
    acc[merchant.status] = (acc[merchant.status] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const option = {
    title: {
      text: 'Merchant Status Distribution',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: Object.entries(statusCounts).map(([name, value]) => ({ name, value })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  };

  chart.setOption(option);
};

watch(() => props.merchants, updateChart, { deep: true });

onMounted(() => {
  initChart();
  window.addEventListener('resize', () => chart?.resize());
});
</script>

<style scoped>
.chart {
  height: 400px;
  width: 100%;
}
</style>