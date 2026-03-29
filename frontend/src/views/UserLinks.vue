<template>
  <div class="user-links">
    <div class="container">
      <div class="header">
        <h1>我的短链接</h1>
        <div class="header-actions">
          <el-button @click="$router.push('/')">返回首页</el-button>
        </div>
      </div>

      <div class="links-list">
        <el-table :data="links" style="width: 100%" v-loading="loading">
          <el-table-column prop="shortUrl" label="短链接" min-width="200">
            <template #default="{ row }">
              <a :href="row.shortUrl" target="_blank" class="short-link">{{ row.shortUrl }}</a>
            </template>
          </el-table-column>
          <el-table-column prop="originalUrl" label="原始链接" min-width="200" show-overflow-tooltip />
          <el-table-column prop="clickCount" label="点击" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewStats(row)">统计</el-button>
              <el-button size="small" @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserLinks, updateLinkStatus, deleteLink } from '@/api/shortlink'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const links = ref([])
const userId = ref(localStorage.getItem('user_id'))

onMounted(() => {
  loadLinks()
})

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await getUserLinks(parseInt(userId.value))
    links.value = res.data || []
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const viewStats = (row) => {
  router.push(`/user/stats/${row.id}`)
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateLinkStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个短链接吗？', '提示', {
      type: 'warning'
    })
    await deleteLink(row.id)
    ElMessage.success('删除成功')
    loadLinks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.user-links {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  color: #333;
}

.links-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.short-link {
  color: #409eff;
  text-decoration: none;
}

.short-link:hover {
  text-decoration: underline;
}
</style>