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
        <el-table-column prop="contactName" label="联系人" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="businessType" label="经营类型" />
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
                v-if="row.status === 'PENDING'"
                @click="handleAudit(row)"
              >
                审核
              </el-button>
              <el-button 
                size="small" 
                :type="row.status === 'DISABLED' ? 'success' : 'warning'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 'DISABLED' ? '启用' : '禁用' }}
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
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" v-if="auditForm.status === 'REJECTED'">
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
          <el-button type="primary" @click="submitAudit">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Merchant } from '@/types/merchant'
import { getMerchants, auditMerchant, updateMerchantStatus } from '@/api/merchant'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const merchants = ref<Merchant[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('all')

// 审核相关
const auditDialogVisible = ref(false)
const auditForm = ref({
  id: 0,
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

const loadMerchants = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      status: activeTab.value === 'all' ? '' : activeTab.value.toUpperCase()
    }
    const { data } = await getMerchants(params)
    // 处理后端返回的数组数据
    if (Array.isArray(data)) {
      merchants.value = data
      total.value = data.length
    } else {
      // 如果后端返回的是分页格式
      merchants.value = data.list || []
      total.value = data.total || 0
    }
  } catch (error) {
    console.error('加载商家列表失败:', error)
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
  auditForm.value = {
    id: row.id,
    status: 'APPROVED',
    rejectReason: ''
  }
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditMerchant({
      id: auditForm.value.id,
      status: auditForm.value.status,
      rejectReason: auditForm.value.status === 'REJECTED' ? auditForm.value.rejectReason : undefined
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadMerchants()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const handleToggleStatus = async (row: Merchant) => {
  const newStatus = row.status === 'DISABLED' ? 'APPROVED' : 'DISABLED'
  const action = newStatus === 'DISABLED' ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该商家吗？`, '提示', {
      type: 'warning'
    })
    await updateMerchantStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadMerchants()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
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
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
