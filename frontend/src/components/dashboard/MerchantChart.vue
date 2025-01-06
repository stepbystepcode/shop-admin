<template>
  <div class="chart-grid">
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>商家状态分布</span>
          <el-radio-group v-model="chartType" size="small">
            <el-radio-button label="pie">饼图</el-radio-button>
            <el-radio-button label="bar">柱状图</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="distributionChartRef" class="chart"></div>
    </el-card>

    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>商家入驻趋势</span>
          <el-select v-model="timeRange" size="small" @change="updateTrendChart">
            <el-option label="最近7天" value="7" />
            <el-option label="最近30天" value="30" />
            <el-option label="最近90天" value="90" />
          </el-select>
        </div>
      </template>
      <div ref="trendChartRef" class="chart"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import type { Merchant } from '@/types/merchant';

const props = defineProps<{
  merchants: Merchant[];
}>();

const chartType = ref('pie');
const timeRange = ref('7');
const distributionChartRef = ref<HTMLElement>();
const trendChartRef = ref<HTMLElement>();
let distributionChart: echarts.ECharts | null = null;
let trendChart: echarts.ECharts | null = null;

const statusMap = {
  'PENDING': '待审核',
  'APPROVED': '已通过',
  'REJECTED': '已拒绝',
  'DISABLED': '已禁用'
};

const colorMap = {
  'PENDING': '#E6A23C',
  'APPROVED': '#67C23A',
  'REJECTED': '#F56C6C',
  'DISABLED': '#909399'
};

const initCharts = () => {
  if (distributionChartRef.value) {
    distributionChart = echarts.init(distributionChartRef.value);
  }
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
  }
  updateCharts();
};

const updateCharts = () => {
  updateDistributionChart();
  updateTrendChart();
};

const updateDistributionChart = () => {
  if (!distributionChart) return;

  const statusCounts = props.merchants.reduce((acc, merchant) => {
    acc[merchant.status] = (acc[merchant.status] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const data = Object.entries(statusCounts).map(([status, value]) => ({
    name: statusMap[status as keyof typeof statusMap] || status,
    value,
    itemStyle: {
      color: colorMap[status as keyof typeof colorMap]
    }
  }));

  const options: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom',
      data: Object.values(statusMap)
    },
    series: [
      chartType.value === 'pie' ? {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data
      } : {
        type: 'bar',
        data: data.map(item => ({
          ...item,
          label: {
            show: true,
            position: 'top'
          }
        })),
        xAxis: {
          type: 'category',
          data: data.map(item => item.name)
        },
        yAxis: {
          type: 'value'
        }
      }
    ]
  };

  distributionChart.setOption(options);
};

const updateTrendChart = () => {
  if (!trendChart) return;

  // 模拟趋势数据
  const days = parseInt(timeRange.value);
  const data = Array.from({ length: days }, (_, i) => {
    const date = new Date();
    date.setDate(date.getDate() - (days - 1 - i));
    return {
      date: date.toLocaleDateString(),
      value: Math.floor(Math.random() * 100)
    };
  });

  const options: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '新增商家数'
    },
    series: [{
      data: data.map(item => item.value),
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  };

  trendChart.setOption(options);
};

watch(() => props.merchants, updateCharts, { deep: true });
watch(() => chartType.value, updateDistributionChart);

onMounted(() => {
  initCharts();
  window.addEventListener('resize', () => {
    distributionChart?.resize();
    trendChart?.resize();
  });
});

onUnmounted(() => {
  window.removeEventListener('resize', () => {
    distributionChart?.resize();
    trendChart?.resize();
  });
  distributionChart?.dispose();
  trendChart?.dispose();
});
</script>

<style scoped lang="scss">
.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 24px;
}

.chart-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.chart {
  height: 400px;
  width: 100%;
}
</style>