<template>
  <div class="links-management">
    <h2>短链管理</h2>

    <el-card shadow="hover">
      <div class="filters">
        <el-select v-model="filters.status" placeholder="状态" clearable @change="loadLinks">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button @click="loadLinks">刷新</el-button>
      </div>

      <el-table :data="links" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="shortCode" label="短链码" width="120" />
        <el-table-column prop="shortUrl" label="短链接" min-width="200">
          <template #default="{ row }">
            <a :href="row.shortUrl" target="_blank" class="short-link">{{ row.shortUrl }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="originalUrl" label="原始链接" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="clickCount" label="点击" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadLinks"
        @current-change="loadLinks"
        style="margin-top: 20px"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllLinks, updateLinkStatus, deleteLink } from '@/api/link'
import { ElMessage, ElMessageBox } from 'element-plus'

const links = ref([])
const loading = ref(false)

const filters = reactive({
  status: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

onMounted(() => {
  loadLinks()
})

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await getAllLinks({
      status: filters.status,
      page: pagination.page,
      size: pagination.size
    })
    links.value = res.data?.links || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载短链失败')
  } finally {
    loading.value = false
  }
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
    await ElMessageBox.confirm('确定要删除这个短链接吗？', '提示', { type: 'warning' })
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
.links-management h2 {
  margin-bottom: 20px;
  color: #333;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.short-link {
  color: #409eff;
  text-decoration: none;
}

.short-link:hover {
  text-decoration: underline;
}
</style>