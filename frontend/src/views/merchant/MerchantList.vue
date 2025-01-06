<template>
  <div class="merchant-list">
    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="商家名称">
          <el-input v-model="queryParams.name" placeholder="商家名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="审核状态" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <el-table :data="merchants" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商家名称" />
        <el-table-column prop="contactName" label="联系人" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="businessType" label="经营类型" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button 
              size="small" 
              type="primary" 
              v-if="scope.row.status === 'PENDING'"
              @click="handleAudit(scope.row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="'商家审核 - ' + currentMerchant?.name"
      width="500px"
      @close="handleClose"
    >
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 'REJECTED'" label="拒绝原因">
          <el-input
            v-model="auditForm.rejectReason"
            type="textarea"
            rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="auditForm.status === 'APPROVED' ? handleApprove() : handleReject()"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Merchant } from '@/types/merchant'
import { getMerchants, auditMerchant } from '@/api/merchant'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const merchants = ref<Merchant[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryParams = ref({
  name: '',
  status: '',
  pageNum: 1,
  pageSize: 10
})

// 审核相关
const auditDialogVisible = ref(false)
const currentMerchant = ref<Merchant | null>(null)
const auditForm = ref({
  status: 'APPROVED',
  rejectReason: ''
})

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'DISABLED': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'DISABLED': '已禁用'
  }
  return texts[status] || status
}

const handleQuery = async () => {
  try {
    loading.value = true
    const params = {
      name: queryParams.value.name,
      status: queryParams.value.status,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const { data } = await getMerchants(params)
    if (Array.isArray(data)) {
      merchants.value = data
      total.value = data.length
    } else {
      merchants.value = data.list || []
      total.value = data.total || 0
    }
  } catch (error) {
    console.error('查询商家列表失败:', error)
    ElMessage.error('查询商家列表失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.value = {
    name: '',
    status: '',
    pageNum: 1,
    pageSize: 10
  }
  handleQuery()
}

const handleView = (row: Merchant) => {
  // TODO: 实现查看详情功能
  console.log('查看商家详情:', row)
}

const handleAudit = (row: Merchant) => {
  currentMerchant.value = row
  auditForm.value = {
    status: 'APPROVED',
    rejectReason: ''
  }
  auditDialogVisible.value = true
}

const handleApprove = async () => {
  if (!currentMerchant.value) return
  
  try {
    loading.value = true
    await auditMerchant({
      id: currentMerchant.value.id,
      status: 'APPROVED',
      rejectReason: ''
    })
    ElMessage.success('审核通过成功')
    auditDialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    loading.value = false
  }
}

const handleReject = async () => {
  if (!currentMerchant.value) return
  if (!auditForm.value.rejectReason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    loading.value = true
    await auditMerchant({
      id: currentMerchant.value.id,
      status: 'REJECTED',
      rejectReason: auditForm.value.rejectReason
    })
    ElMessage.success('审核拒绝成功')
    auditDialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleQuery()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  handleQuery()
}

const handleClose = () => {
  currentMerchant.value = null
  auditForm.value = {
    status: 'APPROVED',
    rejectReason: ''
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.merchant-list {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
