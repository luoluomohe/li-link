<template>
  <div class="system-config">
    <h2>系统配置</h2>

    <el-card shadow="hover">
      <el-form :model="config" label-width="150px">
        <el-form-item label="短链长度">
          <el-input-number v-model="config.shortLinkLength" :min="4" :max="10" />
          <span class="tip">（4-10位）</span>
        </el-form-item>

        <el-form-item label="允许自定义后缀">
          <el-switch v-model="config.allowCustomSuffix" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllConfigs, updateConfig, getShortLinkLength, isAllowCustomSuffix } from '@/api/config'
import { ElMessage } from 'element-plus'

const config = reactive({
  shortLinkLength: 6,
  allowCustomSuffix: true
})

const loading = ref(false)

onMounted(() => {
  loadConfig()
})

const loadConfig = async () => {
  try {
    const lengthRes = await getShortLinkLength()
    config.shortLinkLength = lengthRes.data?.shortLinkLength || 6

    const suffixRes = await isAllowCustomSuffix()
    config.allowCustomSuffix = suffixRes.data?.allowCustomSuffix ?? true
  } catch (error) {
    console.error('Failed to load config', error)
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    await updateConfig('short_link_length', config.shortLinkLength.toString())
    await updateConfig('allow_custom_suffix', config.allowCustomSuffix.toString())
    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.system-config h2 {
  margin-bottom: 20px;
  color: #333;
}

.tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style>