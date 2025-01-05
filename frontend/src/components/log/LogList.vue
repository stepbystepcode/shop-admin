<template>
  <div class="log-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            @change="handleDateChange"
          />
        </div>
      </template>

      <el-table :data="logs" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="操作内容" width="150" />
        <el-table-column prop="method" label="请求方法" width="300" show-overflow-tooltip />
        <el-table-column prop="params" label="请求参数" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip
              effect="dark"
              placement="top"
              :content="row.params"
            >
              <span>{{ row.params }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
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
import type { OperationLog } from '@/types/log'
import { getLogs } from '@/api/log'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const logs = ref<OperationLog[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref<[Date, Date] | null>(null)

const loadLogs = async () => {
  loading.value = true
  try {
    const params: { startTime?: string; endTime?: string } = {}
    if (dateRange.value) {
      params.startTime = dateRange.value[0].toISOString()
      params.endTime = dateRange.value[1].toISOString()
    }

    const response = await getLogs(params)
    logs.value = response.data
    total.value = response.data.length
  } catch (error) {
    ElMessage.error('加载操作日志失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = () => {
  currentPage.value = 1
  loadLogs()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadLogs()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadLogs()
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.log-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
