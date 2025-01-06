<template>
  <div class="merchant-detail" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <el-button icon="Back" @click="router.back()">返回</el-button>
        <h2>商家详情</h2>
        <merchant-status-tag v-if="merchant" :status="merchant.status" />
      </div>
      <div class="header-right" v-if="merchant">
        <el-button 
          v-if="merchant.status === 'PENDING'" 
          type="primary" 
          @click="handleAudit"
        >审核</el-button>
        <el-button 
          v-else-if="merchant.status === 'APPROVED'" 
          type="warning" 
          @click="handleDisable"
        >禁用</el-button>
        <el-button 
          v-else-if="merchant.status === 'DISABLED'" 
          type="success" 
          @click="handleEnable"
        >启用</el-button>
      </div>
    </div>

    <el-card v-if="merchant" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商家名称">
          {{ merchant.name }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ merchant.contactName }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ merchant.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="电子邮箱">
          {{ merchant.email }}
        </el-descriptions-item>
        <el-descriptions-item label="经营地址" :span="2">
          {{ merchant.address }}
        </el-descriptions-item>
        <el-descriptions-item label="经营类型">
          {{ merchant.businessType }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatDate(merchant.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="merchant" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>资质信息</span>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="营业执照">
          <div class="license-container">
            <el-image
              :src="merchant.businessLicense"
              fit="contain"
              :preview-src-list="[merchant.businessLicense]"
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                  <span>加载中...</span>
                </div>
              </template>
              <template #error>
                <div class="image-placeholder">
                  <el-icon><PictureRound /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </el-descriptions-item>
      </el-descriptions>
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
        <el-form-item v-if="auditForm.status === 'REJECTED'" label="拒绝原因" required>
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
          <el-button type="primary" @click="submitAudit" :loading="submitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Picture, PictureRound, Back } from '@element-plus/icons-vue';
import { useMerchantStore } from '@/stores/merchant';
import { updateMerchantStatus } from '@/api/merchant';
import MerchantStatusTag from '@/components/merchant/MerchantStatusTag.vue';
import { formatDate } from '@/utils/format';
import type { Merchant } from '@/types/merchant';

const route = useRoute();
const router = useRouter();
const merchantStore = useMerchantStore();
const loading = ref(false);
const submitting = ref(false);

const merchant = computed(() => 
  merchantStore.merchants.find(m => m.id === Number(route.params.id))
);

// 审核相关
const auditDialogVisible = ref(false);
const auditForm = ref({
  status: 'APPROVED',
  rejectReason: ''
});

const handleAudit = () => {
  auditForm.value = {
    status: 'APPROVED',
    rejectReason: ''
  };
  auditDialogVisible.value = true;
};

const submitAudit = async () => {
  if (!merchant.value) return;
  
  if (auditForm.value.status === 'REJECTED' && !auditForm.value.rejectReason) {
    ElMessage.warning('请输入拒绝原因');
    return;
  }

  submitting.value = true;
  try {
    await updateMerchantStatus({
      id: merchant.value.id,
      status: auditForm.value.status,
      rejectReason: auditForm.value.rejectReason
    });
    await merchantStore.fetchMerchants();
    ElMessage.success('审核完成');
    auditDialogVisible.value = false;
  } catch (error) {
    console.error('审核失败:', error);
    ElMessage.error('审核失败');
  } finally {
    submitting.value = false;
  }
};

const handleDisable = async () => {
  if (!merchant.value) return;

  try {
    await ElMessageBox.confirm(
      '确定要禁用该商家吗？禁用后商家将无法继续使用系统',
      '禁用确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await updateMerchantStatus({
      id: merchant.value.id,
      status: 'DISABLED'
    });
    await merchantStore.fetchMerchants();
    ElMessage.success('商家已禁用');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败:', error);
      ElMessage.error('禁用失败');
    }
  }
};

const handleEnable = async () => {
  if (!merchant.value) return;

  try {
    await updateMerchantStatus({
      id: merchant.value.id,
      status: 'APPROVED'
    });
    await merchantStore.fetchMerchants();
    ElMessage.success('商家已启用');
  } catch (error) {
    console.error('启用失败:', error);
    ElMessage.error('启用失败');
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    if (!merchantStore.merchants.length) {
      await merchantStore.fetchMerchants();
    }
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.merchant-detail {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    h2 {
      margin: 0;
    }
  }
}

.detail-card {
  margin-bottom: 24px;

  .card-header {
    font-weight: bold;
  }
}

.license-container {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 20px;
  
  .el-image {
    max-width: 400px;
    max-height: 300px;
  }
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  
  .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }
}

:deep(.el-descriptions__cell) {
  padding: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>