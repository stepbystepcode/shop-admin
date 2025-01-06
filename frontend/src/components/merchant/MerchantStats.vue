<template>
  <div class="merchant-stats">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>总商家数</span>
            </div>
          </template>
          <div class="stats-number">{{ stats.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card pending">
          <template #header>
            <div class="card-header">
              <span>待审核商家</span>
            </div>
          </template>
          <div class="stats-number">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card approved">
          <template #header>
            <div class="card-header">
              <span>已通过商家</span>
            </div>
          </template>
          <div class="stats-number">{{ stats.approved }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rejected">
          <template #header>
            <div class="card-header">
              <span>已拒绝商家</span>
            </div>
          </template>
          <div class="stats-number">{{ stats.rejected }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>商家状态分布</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>商家入驻趋势</span>
              <el-select v-model="trendDays" @change="loadTrendData">
                <el-option label="最近7天" :value="7" />
                <el-option label="最近30天" :value="30" />
                <el-option label="最近90天" :value="90" />
              </el-select>
            </div>
          </template>
          <div ref="lineChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>审核情况统计</span>
            </div>
          </template>
          <div class="audit-stats">
            <div class="audit-item">
              <div class="audit-label">审核通过率</div>
              <div class="audit-value">{{ stats.approvalRate.toFixed(2) }}%</div>
            </div>
            <div class="audit-item">
              <div class="audit-label">审核拒绝率</div>
              <div class="audit-value">{{ stats.rejectionRate.toFixed(2) }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { MerchantStats } from '@/types/merchant'
import { getMerchantStats, getMerchantTrends } from '@/api/merchant'

const stats = ref<MerchantStats>({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0,
  disabled: 0,
  approvalRate: 0,
  rejectionRate: 0
})

const trendDays = ref(7)
const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null

const initPieChart = () => {
  if (!pieChartRef.value) return

  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        data: [
          { value: stats.value.pending, name: '待审核' },
          { value: stats.value.approved, name: '已通过' },
          { value: stats.value.rejected, name: '已拒绝' },
          { value: stats.value.disabled, name: '已禁用' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  pieChart.setOption(option)
}

const updateLineChart = (trends: any[]) => {
  if (!lineChartRef.value || !lineChart) return

  const dates = Array.from(new Set(trends.map(t => t.date)))
  const statuses = ['PENDING', 'APPROVED', 'REJECTED', 'DISABLED']
  const series = statuses.map(status => ({
    name: status === 'PENDING' ? '待审核' :
          status === 'APPROVED' ? '已通过' :
          status === 'REJECTED' ? '已拒绝' : '已禁用',
    type: 'line',
    data: dates.map(date => {
      const trend = trends.find(t => t.date === date && t.status === status)
      return trend ? trend.count : 0
    })
  }))

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['待审核', '已通过', '已拒绝', '已禁用']
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series
  }
  lineChart.setOption(option)
}

const loadStats = async () => {
  try {
    const response = await getMerchantStats()
    stats.value = response.data
    initPieChart()
  } catch (error) {
    console.error('Failed to load merchant stats:', error)
  }
}

const loadTrendData = async () => {
  try {
    const response = await getMerchantTrends(trendDays.value)
    if (!lineChart) {
      lineChart = echarts.init(lineChartRef.value!)
    }
    updateLineChart(response.data)
  } catch (error) {
    console.error('Failed to load trend data:', error)
  }
}

onMounted(() => {
  loadStats()
  loadTrendData()

  window.addEventListener('resize', () => {
    pieChart?.resize()
    lineChart?.resize()
  })
})

onUnmounted(() => {
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.merchant-stats {
  padding: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-number {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  padding: 10px 0;
}

.charts-row {
  margin-top: 20px;
}

.pending :deep(.el-card__header) {
  background-color: #e6a23c;
  color: white;
}

.approved :deep(.el-card__header) {
  background-color: #67c23a;
  color: white;
}

.rejected :deep(.el-card__header) {
  background-color: #f56c6c;
  color: white;
}

.audit-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px;
}

.audit-item {
  text-align: center;
}

.audit-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.audit-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style>
