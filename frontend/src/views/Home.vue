<template>
  <div class="home">
    <div class="container">
      <div class="header">
        <h1>短链生成系统</h1>
        <div class="header-actions">
          <el-button type="primary" @click="$router.push('/user/links')">我的短链</el-button>
          <el-button @click="$router.push('/admin/login')">管理后台</el-button>
        </div>
      </div>

      <div class="create-section">
        <el-card shadow="hover">
          <h2>创建短链接</h2>
          <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleCreate">
            <el-form-item prop="url">
              <el-input
                v-model="form.url"
                placeholder="请输入原始URL"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Link /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item v-if="allowCustomSuffix">
              <el-input
                v-model="form.customSuffix"
                placeholder="可选：自定义短链后缀"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Edit /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleCreate" style="width: 100%">
                生成短链接
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <div v-if="result" class="result-section">
        <el-card shadow="hover">
          <h3>短链接已生成</h3>
          <div class="short-link-box">
            <el-input v-model="result.shortUrl" readonly size="large">
              <template #append>
                <el-button @click="copyLink">复制</el-button>
              </template>
            </el-input>
          </div>
          <div class="link-info">
            <p><strong>原始链接：</strong>{{ result.originalUrl }}</p>
            <p><strong>点击次数：</strong>{{ result.clickCount }}</p>
            <p><strong>创建时间：</strong>{{ result.createTime }}</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Link, Edit } from '@element-plus/icons-vue'
import { createShortLink } from '@/api/shortlink'
import { isAllowCustomSuffix } from '@/api/config'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const result = ref(null)
const userId = ref(localStorage.getItem('user_id') || null)
const allowCustomSuffix = ref(true)

if (!userId.value) {
  userId.value = Math.abs(Date.now() % 1000000);
  localStorage.setItem('user_id', userId.value)
}

onMounted(async () => {
  try {
    const res = await isAllowCustomSuffix()
    allowCustomSuffix.value = res.data === 'true' || res.data === true
  } catch (e) {
    console.error('Failed to load config', e)
  }
})

const form = reactive({
  url: '',
  customSuffix: ''
})

const rules = {
  url: [
    { required: true, message: '请输入原始URL', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ]
}

const handleCreate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await createShortLink({
      url: form.url,
      customSuffix: form.customSuffix,
      userId: parseInt(userId.value)
    })
    result.value = res.data
    ElMessage.success('短链接生成成功')
  } catch (error) {
    ElMessage.error('生成失败')
  } finally {
    loading.value = false
  }
}

const copyLink = () => {
  navigator.clipboard.writeText(result.value.shortUrl)
  ElMessage.success('已复制到剪贴板')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.container {
  max-width: 600px;
  margin: 0 auto;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 36px;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.create-section {
  margin-bottom: 20px;
}

.create-section h2 {
  margin-bottom: 20px;
  color: #333;
}

.result-section {
  margin-top: 20px;
}

.result-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.short-link-box {
  margin-bottom: 20px;
}

.link-info p {
  margin: 8px 0;
  color: #666;
}

:deep(.el-card) {
  border-radius: 12px;
}
</style>