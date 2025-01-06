<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    :title="title"
    width="60%"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      :disabled="mode === 'view'"
    >
      <el-form-item label="商家名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="form.contactPerson" />
      </el-form-item>
      
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      
      <el-form-item label="电子邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      
      <el-form-item label="详细地址" prop="address">
        <el-input
          v-model="form.address"
          type="textarea"
          :rows="2"
        />
      </el-form-item>
      
      <el-form-item label="营业执照" prop="businessLicense">
        <el-upload
          v-if="mode !== 'view'"
          class="upload-demo"
          action="/api/upload"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
        >
          <el-button type="primary">点击上传</el-button>
        </el-upload>
        <div v-if="form.businessLicense">
          <el-image
            style="width: 200px"
            :src="form.businessLicense"
            :preview-src-list="[form.businessLicense]"
          />
        </div>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          v-if="mode !== 'view'"
          type="primary"
          @click="handleSubmit"
        >
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { Merchant } from '@/types/merchant'
import { createMerchant, updateMerchant } from '@/api/merchant'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit' | 'view'
  merchantData?: Merchant
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'success': []
}>()

const title = computed(() => {
  const titleMap = {
    create: '新增商家',
    edit: '编辑商家',
    view: '商家详情'
  }
  return titleMap[props.mode]
})

const formRef = ref<FormInstance>()
const form = ref<Partial<Merchant>>({
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  businessLicense: ''
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  businessLicense: [
    { required: true, message: '请上传营业执照', trigger: 'change' }
  ]
}

const handleClose = () => {
  emit('update:visible', false)
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        if (props.mode === 'create') {
          await createMerchant(form.value)
          ElMessage.success('创建成功')
        } else {
          await updateMerchant(props.merchantData!.id, form.value)
          ElMessage.success('更新成功')
        }
        emit('success')
        handleClose()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleUploadSuccess = (response: any) => {
  form.value.businessLicense = response.data.url
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('请上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 当编辑模式下，初始化表单数据
if (props.merchantData && props.mode !== 'create') {
  form.value = { ...props.merchantData }
}
</script>

<style scoped>
.dialog-footer {
  padding: 20px 0;
  text-align: right;
}

.upload-demo {
  margin-bottom: 10px;
}
</style>
