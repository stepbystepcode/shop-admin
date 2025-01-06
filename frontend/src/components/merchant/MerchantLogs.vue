<template>
  <div class="merchant-logs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleDateChange"
            />
            <el-select
              v-model="operationType"
              placeholder="操作类型"
              clearable
              @change="handleFilterChange"
            >
              <el-option label="审核" value="AUDIT" />
              <el-option label="修改" value="UPDATE" />
              <el-option label="状态变更" value="STATUS_CHANGE" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table
        :data="logs"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="timestamp" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="merchantName" label="商家名称" width="180" />
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="details" label="操作详情" />
        <el-table-column prop="ip" label="IP地址" width="140" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { DateRangeValue } from 'element-plus'
import { getMerchantLogs } from '@/api/merchant'
import { formatDate } from '@/utils/date'

interface Log {
  id: number
  timestamp: string
  operator: string
  merchantName: string
  operationType: 'AUDIT' | 'UPDATE' | 'STATUS_CHANGE'
  details: string
  ip: string
}

const logs = ref<Log[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref<DateRangeValue>(null)
const operationType = ref('')

const getOperationTypeTag = (type: string) => {
  const typeMap: Record<string, string> = {
    AUDIT: 'primary',
    UPDATE: 'success',
    STATUS_CHANGE: 'warning'
  }
  return typeMap[type] || 'info'
}

const getOperationTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    AUDIT: '审核',
    UPDATE: '修改',
    STATUS_CHANGE: '状态变更'
  }
  return typeMap[type] || type
}

const fetchLogs = async () => {
  loading.value = true
  try {
    const response = await getMerchantLogs({
      page: currentPage.value,
      pageSize: pageSize.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      operationType: operationType.value || undefined
    })
    logs.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取日志失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  fetchLogs()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchLogs()
}

const handleDateChange = () => {
  currentPage.value = 1
  fetchLogs()
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.merchant-logs {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
