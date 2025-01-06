<template>
  <div class="stats-grid">
    <el-card v-for="item in statItems" :key="item.key" :class="item.class">
      <div class="stat-item">
        <div class="stat-icon">
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
        </div>
        <div class="stat-content">
          <h3 class="stat-label">{{ item.label }}</h3>
          <p class="stat-value">{{ formatValue(stats[item.key]) }}</p>
          <div class="stat-trend" v-if="item.trend">
            <span :class="{ 'up': item.trend > 0, 'down': item.trend < 0 }">
              {{ Math.abs(item.trend) }}%
              <el-icon>
                <component :is="item.trend > 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </span>
            <span class="trend-label">较上周</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import type { MerchantStats } from '@/types/merchant';
import { 
  Shop, 
  Timer, 
  CircleCheck, 
  CircleClose,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue';

defineProps<{
  stats: MerchantStats;
}>();

const statItems = [
  {
    key: 'total',
    label: '商家总数',
    icon: Shop,
    class: 'total-card',
    trend: 5.2
  },
  {
    key: 'pending',
    label: '待审核商家',
    icon: Timer,
    class: 'pending-card',
    trend: -2.1
  },
  {
    key: 'approved',
    label: '已通过商家',
    icon: CircleCheck,
    class: 'approved-card',
    trend: 8.4
  },
  {
    key: 'rejected',
    label: '已拒绝商家',
    icon: CircleClose,
    class: 'rejected-card',
    trend: -1.5
  }
];

const formatValue = (value: number): string => {
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + 'w';
  }
  return value.toString();
};
</script>

<style scoped lang="scss">
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 8px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  margin-right: 16px;
  
  .el-icon {
    font-size: 24px;
  }
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #606266;
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: normal;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;

  .up {
    color: #67C23A;
  }

  .down {
    color: #F56C6C;
  }

  .trend-label {
    color: #909399;
  }
}

.total-card {
  .stat-icon {
    background-color: #ecf5ff;
    color: #409EFF;
  }
  .stat-value {
    color: #409EFF;
  }
}

.pending-card {
  .stat-icon {
    background-color: #fdf6ec;
    color: #E6A23C;
  }
  .stat-value {
    color: #E6A23C;
  }
}

.approved-card {
  .stat-icon {
    background-color: #f0f9eb;
    color: #67C23A;
  }
  .stat-value {
    color: #67C23A;
  }
}

.rejected-card {
  .stat-icon {
    background-color: #fef0f0;
    color: #F56C6C;
  }
  .stat-value {
    color: #F56C6C;
  }
}
</style>