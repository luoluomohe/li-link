<template>
  <div class="user-links" :class="themeStyle">
    <div class="bg-pattern"></div>
    <div class="floating-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
    </div>

    <div class="container">
      <header class="header">
        <div class="header-left">
          <button class="back-btn" @click="$router.push('/')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
          </button>
          <div class="title-group">
            <h1>我的短链接</h1>
            <p class="subtitle">管理您创建的所有短链接</p>
          </div>
        </div>
        <div class="stats-summary">
          <div class="stat-item">
            <span class="stat-value">{{ links.length }}</span>
            <span class="stat-label">活跃链接</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ totalClicks }}</span>
            <span class="stat-label">总点击量</span>
          </div>
        </div>
      </header>

      <div class="links-section" v-if="links.length > 0">
        <div class="links-grid">
          <div 
            v-for="link in links" 
            :key="link.id" 
            class="link-card"
            :class="{ 'disabled': link.status === 0 }"
          >
            <div class="card-header">
              <div class="link-code">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
                  <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
                </svg>
                <span>{{ link.shortCode }}</span>
              </div>
              <div class="status-badge" :class="link.status === 1 ? 'active' : 'inactive'">
                {{ link.status === 1 ? '启用' : '禁用' }}
              </div>
            </div>

            <div class="original-url">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                <polyline points="15 3 21 3 21 9"/>
                <line x1="10" y1="14" x2="21" y2="3"/>
              </svg>
              <span>{{ truncateUrl(link.originalUrl) }}</span>
            </div>

            <div class="card-stats">
              <div class="card-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                <span>{{ link.clickCount }} 次点击</span>
              </div>
              <div class="card-stat">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
                <span>{{ formatDate(link.createTime) }}</span>
              </div>
            </div>

            <div class="card-actions">
              <button class="action-btn copy" @click="copyLink(link.shortUrl)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                  <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                </svg>
                复制
              </button>
              <button class="action-btn stats" @click="viewStats(link)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="20" x2="18" y2="10"/>
                  <line x1="12" y1="20" x2="12" y2="4"/>
                  <line x1="6" y1="20" x2="6" y2="14"/>
                </svg>
                统计
              </button>
              <button class="action-btn toggle" @click="toggleStatus(link)">
                <svg v-if="link.status === 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                  <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
                {{ link.status === 1 ? '禁用' : '启用' }}
              </button>
              <button class="action-btn delete" @click="handleDelete(link)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"/>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
                删除
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
        <h3>暂无短链接</h3>
        <p>点击下方按钮创建您的第一个短链接</p>
        <button class="create-btn" @click="$router.push('/')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          创建短链接
        </button>
      </div>

      <div class="loading-state" v-if="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <transition name="slide-up">
        <div class="deleted-section" v-if="deletedLinks.length > 0">
          <div class="section-header" @click="showDeleted = !showDeleted">
            <div class="section-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
              <span>回收站 ({{ deletedLinks.length }})</span>
            </div>
            <svg class="chevron" :class="{ rotated: showDeleted }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </div>
          <div class="deleted-list" v-show="showDeleted">
            <div v-for="link in deletedLinks" :key="link.id" class="deleted-item">
              <div class="deleted-info">
                <span class="deleted-code">{{ link.shortCode }}</span>
                <span class="deleted-url">{{ truncateUrl(link.originalUrl) }}</span>
              </div>
              <button class="restore-btn" @click="handleRestore(link)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                  <path d="M3 3v5h5"/>
                </svg>
                恢复
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getUserLinks, updateLinkStatus, deleteLink, restoreLink } from '@/api/shortlink'
import { getThemeConfig } from '@/api/config'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const links = ref([])
const deletedLinks = ref([])
const showDeleted = ref(false)
const userId = ref(localStorage.getItem('user_id'))
const primaryColor = ref('#6366f1')
const themeStyle = ref('default')

const totalClicks = computed(() => {
  return links.value.reduce((sum, link) => sum + (link.clickCount || 0), 0)
})

const loadTheme = async () => {
  try {
    const cachedColor = localStorage.getItem('theme_primary_color')
    const cachedStyle = localStorage.getItem('theme_style')
    
    if (cachedColor) primaryColor.value = cachedColor
    if (cachedStyle) themeStyle.value = cachedStyle
    
    const res = await getThemeConfig()
    primaryColor.value = res.data?.primaryColor || cachedColor || '#6366f1'
    themeStyle.value = res.data?.themeStyle || cachedStyle || 'default'
    
    localStorage.setItem('theme_primary_color', primaryColor.value)
    localStorage.setItem('theme_style', themeStyle.value)
    
    await nextTick()
    applyTheme()
  } catch (e) {
    console.error('Failed to load theme', e)
    if (primaryColor.value) {
      await nextTick()
      applyTheme()
    }
  }
}

const applyTheme = () => {
  const userLinksEl = document.querySelector('.user-links')
  if (userLinksEl) {
    userLinksEl.style.setProperty('--primary-color', primaryColor.value)
  }
}

onMounted(() => {
  loadLinks()
  loadTheme()
})

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await getUserLinks(parseInt(userId.value))
    const allLinks = res.data || []
    links.value = allLinks.filter(link => !link.deleted)
    deletedLinks.value = allLinks.filter(link => link.deleted)
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
    await ElMessageBox.confirm('确定要删除这个短链接吗？删除后可以恢复。', '提示', {
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

const handleRestore = async (row) => {
  try {
    await restoreLink(row.id)
    ElMessage.success('恢复成功')
    loadLinks()
  } catch (error) {
    ElMessage.error('恢复失败')
  }
}

const copyLink = (url) => {
  navigator.clipboard.writeText(url)
  ElMessage.success('已复制到剪贴板')
}

const truncateUrl = (url) => {
  if (!url) return ''
  return url.length > 40 ? url.substring(0, 40) + '...' : url
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.user-links {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #0f0f23 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.bg-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, color-mix(in srgb, var(--primary-color) 8%, transparent) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, color-mix(in srgb, var(--primary-color) 60%, #8b5cf6) 0%, transparent 50%);
  pointer-events: none;
}

.floating-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.2;
  animation: float 25s ease-in-out infinite;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  top: -200px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 80%, #8b5cf6), #a855f7);
  bottom: -150px;
  left: -100px;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 32px 24px;
  position: relative;
  z-index: 1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.back-btn svg {
  width: 20px;
  height: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.title-group h1 {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin: 0 0 4px;
}

.subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.stats-summary {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.link-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
}

.link-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
}

.link-card.disabled {
  opacity: 0.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.link-code {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: white;
}

.link-code svg {
  width: 16px;
  height: 16px;
  color: #a5b4fc;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
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

.original-url {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
  margin-bottom: 16px;
}

.original-url svg {
  width: 14px;
  height: 14px;
  color: rgba(255, 255, 255, 0.4);
  flex-shrink: 0;
  margin-top: 2px;
}

.original-url span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  word-break: break-all;
  line-height: 1.4;
}

.card-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.card-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.card-stat svg {
  width: 14px;
  height: 14px;
}

.card-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  font-family: inherit;
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.action-btn.copy:hover {
  border-color: rgba(99, 102, 241, 0.3);
  color: #a5b4fc;
}

.action-btn.stats:hover {
  border-color: rgba(16, 185, 129, 0.3);
  color: #34d399;
}

.action-btn.toggle:hover {
  border-color: rgba(251, 191, 36, 0.3);
  color: #fbbf24;
}

.action-btn.delete:hover {
  border-color: rgba(239, 68, 68, 0.3);
  color: #f87171;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
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
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 24px;
}

.create-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  box-shadow: 0 4px 24px rgba(99, 102, 241, 0.3);
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.4);
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

.deleted-section {
  margin-top: 32px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.section-header:hover {
  background: rgba(255, 255, 255, 0.02);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
}

.section-title svg {
  width: 16px;
  height: 16px;
}

.chevron {
  width: 18px;
  height: 18px;
  color: rgba(255, 255, 255, 0.4);
  transition: transform 0.3s ease;
}

.chevron.rotated {
  transform: rotate(180deg);
}

.deleted-list {
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.deleted-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}

.deleted-item:last-child {
  border-bottom: none;
}

.deleted-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.deleted-code {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  text-decoration: line-through;
}

.deleted-url {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
}

.restore-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #a5b4fc;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.restore-btn svg {
  width: 14px;
  height: 14px;
}

.restore-btn:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: rgba(99, 102, 241, 0.3);
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 640px) {
  .container {
    padding: 20px 16px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-summary {
    width: 100%;
    justify-content: center;
  }

  .links-grid {
    grid-template-columns: 1fr;
  }

  .card-actions {
    flex-direction: column;
  }

  .action-btn {
    justify-content: center;
  }
}

.user-links {
  --primary-color: #6366f1;
}

.user-links.minimal {
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 50%, #bfdbfe 100%);
}

.user-links.minimal .title-group h1,
.user-links.minimal .stat-value,
.user-links.minimal .link-code span,
.user-links.minimal .original-url span,
.user-links.minimal .card-stat span,
.user-links.minimal .deleted-code {
  color: #1e3a5f;
}

.user-links.minimal .subtitle,
.user-links.minimal .stat-label,
.user-links.minimal .info-label {
  color: #3b82f6;
}

.user-links.minimal .back-btn {
  background: rgba(255, 255, 255, 0.7);
  border-color: rgba(59, 130, 246, 0.2);
  color: #2563eb;
}

.user-links.minimal .stats-summary {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.user-links.minimal .link-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.user-links.minimal .status-badge.active {
  background: rgba(34, 197, 94, 0.15);
  color: #16a34a;
}

.user-links.minimal .status-badge.inactive {
  background: rgba(239, 68, 68, 0.15);
  color: #dc2626;
}

.user-links.minimal .action-btn {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.user-links.minimal .action-btn:hover {
  background: rgba(59, 130, 246, 0.1);
}

.user-links.minimal .deleted-section {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.user-links.minimal .deleted-item {
  border-color: rgba(59, 130, 246, 0.1);
}

.user-links.minimal .deleted-url {
  color: #60a5fa;
}

.user-links.minimal .restore-btn {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
  color: #2563eb;
}

.user-links.dark {
  background: #0a0a0a;
}

.user-links.dark .bg-pattern {
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(99, 102, 241, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(139, 92, 246, 0.05) 0%, transparent 50%);
}

.user-links.glass {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.user-links.glass .link-card,
.user-links.glass .stats-summary,
.user-links.glass .deleted-section {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
