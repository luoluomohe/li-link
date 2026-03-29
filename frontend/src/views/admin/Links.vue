<template>
  <div class="links-management">
    <div class="page-header">
      <div class="header-left">
        <h2>短链管理</h2>
        <span class="total-count">共 {{ pagination.total }} 条记录</span>
      </div>
      <div class="header-actions">
        <div class="filter-group">
          <select v-model="filters.status" @change="loadLinks" class="filter-select">
            <option :value="null">全部状态</option>
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
        </div>
        <button class="refresh-btn" @click="loadLinks">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M23 4v6h-6"/>
            <path d="M1 20v-6h6"/>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
          刷新
        </button>
      </div>
    </div>

    <div class="table-container" v-if="links.length > 0">
      <div class="table-header">
        <div class="col col-id">ID</div>
        <div class="col col-code">短链码</div>
        <div class="col col-url">原始链接</div>
        <div class="col col-user">用户ID</div>
        <div class="col col-clicks">点击</div>
        <div class="col col-status">状态</div>
        <div class="col col-time">创建时间</div>
        <div class="col col-actions">操作</div>
      </div>

      <div class="table-body">
        <div v-for="link in links" :key="link.id" class="table-row">
          <div class="col col-id">
            <span class="id-badge">{{ link.id }}</span>
          </div>
          <div class="col col-code">
            <a :href="link.shortUrl" target="_blank" class="short-link">
              {{ link.shortCode }}
            </a>
          </div>
          <div class="col col-url">
            <span class="url-text" :title="link.originalUrl">
              {{ truncateUrl(link.originalUrl) }}
            </span>
          </div>
          <div class="col col-user">
            <span class="user-badge">{{ link.userId || '-' }}</span>
          </div>
          <div class="col col-clicks">
            <span class="clicks-count">{{ link.clickCount || 0 }}</span>
          </div>
          <div class="col col-status">
            <span class="status-badge" :class="link.status === 1 ? 'active' : 'inactive'">
              {{ link.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="col col-time">
            <span class="time-text">{{ formatDate(link.createTime) }}</span>
          </div>
          <div class="col col-actions">
            <button class="action-btn toggle" @click="toggleStatus(link)">
              <svg v-if="link.status === 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </button>
            <button class="action-btn delete" @click="handleDelete(link)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
          <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
        </svg>
      </div>
      <h3>暂无短链接数据</h3>
      <p>系统中还没有短链接记录</p>
    </div>

    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div class="pagination" v-if="pagination.total > 0">
      <div class="page-info">
        显示 {{ (pagination.page - 1) * pagination.size + 1 }} - {{ Math.min(pagination.page * pagination.size, pagination.total) }} 条，共 {{ pagination.total }} 条
      </div>
      <div class="page-controls">
        <button 
          class="page-btn" 
          :disabled="pagination.page === 1"
          @click="goToPage(pagination.page - 1)"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
        </button>
        <span class="page-number">{{ pagination.page }} / {{ totalPages }}</span>
        <button 
          class="page-btn" 
          :disabled="pagination.page >= totalPages"
          @click="goToPage(pagination.page + 1)"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>
      <select v-model="pagination.size" @change="loadLinks" class="size-select">
        <option :value="10">10条/页</option>
        <option :value="20">20条/页</option>
        <option :value="50">50条/页</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
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

const totalPages = computed(() => {
  return Math.ceil(pagination.total / pagination.size) || 1
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

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pagination.page = page
    loadLinks()
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

const truncateUrl = (url) => {
  if (!url) return '-'
  return url.length > 40 ? url.substring(0, 40) + '...' : url
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.links-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.total-count {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 14px;
  color: white;
  outline: none;
  cursor: pointer;
  font-family: inherit;
}

.filter-select option {
  background: #1a1a3e;
  color: white;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #a5b4fc;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.refresh-btn svg {
  width: 16px;
  height: 16px;
}

.refresh-btn:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: rgba(99, 102, 241, 0.3);
}

.table-container {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 60px 100px 1fr 80px 60px 80px 140px 100px;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.02);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.table-body {
  max-height: 600px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 60px 100px 1fr 80px 60px 80px 140px 100px;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  transition: background 0.2s;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: rgba(255, 255, 255, 0.02);
}

.col {
  display: flex;
  align-items: center;
}

.id-badge {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
}

.short-link {
  font-size: 14px;
  font-weight: 500;
  color: #a5b4fc;
  text-decoration: none;
  transition: color 0.2s;
}

.short-link:hover {
  color: #c4b5fd;
  text-decoration: underline;
}

.url-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-badge {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.clicks-count {
  font-size: 14px;
  font-weight: 600;
  color: #34d399;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}

.status-badge.inactive {
  background: rgba(239, 68, 68, 0.15);
  color: #f87171;
}

.time-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.col-actions {
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.03);
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.toggle {
  color: rgba(255, 255, 255, 0.5);
}

.action-btn.toggle:hover {
  background: rgba(251, 191, 36, 0.1);
  border-color: rgba(251, 191, 36, 0.2);
  color: #fbbf24;
}

.action-btn.delete {
  color: rgba(255, 255, 255, 0.5);
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
  color: #f87171;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon svg {
  width: 40px;
  height: 40px;
  color: #6366f1;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.loading-state {
  text-align: center;
  padding: 80px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  flex-wrap: wrap;
  gap: 16px;
}

.page-info {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn svg {
  width: 18px;
  height: 18px;
}

.page-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-number {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.size-select {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  font-size: 13px;
  color: white;
  outline: none;
  cursor: pointer;
  font-family: inherit;
}

.size-select option {
  background: #1a1a3e;
  color: white;
}

@media (max-width: 1024px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 100px 1fr 80px;
  }

  .col-user,
  .col-clicks,
  .col-time {
    display: none;
  }
}

@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
  }

  .filter-select {
    flex: 1;
  }

  .table-header {
    display: none;
  }

  .table-row {
    grid-template-columns: 1fr;
    gap: 12px;
    padding: 16px;
  }

  .col {
    justify-content: space-between;
  }

  .col::before {
    content: attr(data-label);
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
    text-transform: uppercase;
  }

  .col-actions {
    justify-content: flex-end;
  }

  .pagination {
    flex-direction: column;
    text-align: center;
  }
}

.admin-layout.minimal .page-header h2 {
  color: #1e3a5f;
}

.admin-layout.minimal .page-header p,
.admin-layout.minimal .total-count {
  color: #64748b;
}

.admin-layout.minimal .filter-select,
.admin-layout.minimal .search-box input {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.2);
  color: #1e3a5f;
}

.admin-layout.minimal .search-box input::placeholder {
  color: #94a3b8;
}

.admin-layout.minimal .search-box svg {
  color: #64748b;
}

.admin-layout.minimal .table-container {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .table-header {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .table-header .col {
  color: #1e3a5f;
}

.admin-layout.minimal .table-row {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.1);
}

.admin-layout.minimal .table-row:hover {
  background: rgba(255, 255, 255, 1);
}

.admin-layout.minimal .id-badge {
  color: #64748b;
}

.admin-layout.minimal .short-link {
  color: #2563eb;
}

.admin-layout.minimal .short-link:hover {
  color: #1d4ed8;
}

.admin-layout.minimal .url-text {
  color: #475569;
}

.admin-layout.minimal .user-badge {
  color: #475569;
}

.admin-layout.minimal .clicks-count {
  color: #1e3a5f;
  font-weight: 600;
}

.admin-layout.minimal .time-text {
  color: #64748b;
}

.admin-layout.minimal .status-badge.active {
  background: rgba(34, 197, 94, 0.15);
  color: #16a34a;
}

.admin-layout.minimal .status-badge.inactive {
  background: rgba(239, 68, 68, 0.15);
  color: #dc2626;
}

.admin-layout.minimal .action-btn {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
  color: #475569;
}

.admin-layout.minimal .action-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  color: #1e3a5f;
}

.admin-layout.minimal .pagination {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .page-info {
  color: #64748b;
}

.admin-layout.minimal .page-btn {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
  color: #475569;
}

.admin-layout.minimal .page-btn:hover:not(:disabled) {
  background: rgba(59, 130, 246, 0.15);
  color: #1e3a5f;
}

.admin-layout.minimal .page-btn.active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-color: #3b82f6;
  color: white;
}

.admin-layout.minimal .empty-state h3 {
  color: #1e3a5f;
}

.admin-layout.minimal .empty-state p {
  color: #64748b;
}
</style>
