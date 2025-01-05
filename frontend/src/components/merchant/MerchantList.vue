<template>
  <div class="merchant-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商家列表</span>
          <el-button type="primary" @click="handleAdd">添加商家</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待审核" name="pending" />
        <el-tab-pane label="已通过" name="approved" />
        <el-tab-pane label="已拒绝" name="rejected" />
        <el-tab-pane label="已禁用" name="disabled" />
      </el-tabs>

      <el-table :data="merchants" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="商家名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" @click="handleView(row)">查看</el-button>
              <el-button 
                size="small" 
                type="primary" 
                v-if="row.status === 'pending'"
                @click="handleAudit(row)"
              >
                审核
              </el-button>
              <el-button 
                size="small" 
                :type="row.status === 'disabled' ? 'success' : 'warning'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 'disabled' ? '启用' : '禁用' }}
              </el-button>
            </el-button-group>
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="商家审核"
      width="500px"
    >
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="approved">通过</el-radio>
            <el-radio label="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            rows="3"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { Merchant } from '@/types/merchant'
import { getMerchants, getMerchantsByStatus, updateMerchantStatus } from '@/api/merchant'
import { formatDate } from '@/utils/date'

const activeTab = ref('all')
const loading = ref(false)
const merchants = ref<Merchant[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const auditDialogVisible = ref(false)
const auditForm = ref({
  id: 0,
  status: 'approved',
  comment: ''
})

const getStatusType = (status: string) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    disabled: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    disabled: '已禁用'
  }
  return texts[status] || status
}

const loadMerchants = async () => {
  loading.value = true
  try {
    const response = activeTab.value === 'all'
      ? await getMerchants()
      : await getMerchantsByStatus(activeTab.value)
    merchants.value = response.data
    total.value = response.data.length
  } catch (error) {
    ElMessage.error('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabClick = () => {
  currentPage.value = 1
  loadMerchants()
}

const handleAdd = () => {
  // TODO: 实现添加商家功能
}

const handleView = (row: Merchant) => {
  // TODO: 实现查看商家详情功能
}

const handleAudit = (row: Merchant) => {
  auditForm.value.id = row.id
  auditForm.value.status = 'approved'
  auditForm.value.comment = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await updateMerchantStatus(auditForm.value.id, auditForm.value.status)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadMerchants()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleToggleStatus = async (row: Merchant) => {
  const newStatus = row.status === 'disabled' ? 'approved' : 'disabled'
  try {
    await updateMerchantStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadMerchants()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadMerchants()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadMerchants()
}

onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
.merchant-list {
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
