<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon links">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalLinks || 0 }}</span>
          <span class="stat-label">总短链数</span>
        </div>
        <div class="stat-trend up">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon clicks">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalClicks || 0 }}</span>
          <span class="stat-label">总访问量</span>
        </div>
        <div class="stat-trend up">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon today">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.todayClicks || 0 }}</span>
          <span class="stat-label">今日访问</span>
        </div>
        <div class="stat-badge">今日</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon new">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.todayLinks || 0 }}</span>
          <span class="stat-label">今日新增</span>
        </div>
        <div class="stat-badge">今日</div>
      </div>
    </div>

    <div class="quick-actions-section">
      <h3>快捷操作</h3>
      <div class="actions-grid">
        <router-link to="/admin/links" class="action-card">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
              <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
            </svg>
          </div>
          <div class="action-info">
            <span class="action-title">管理短链</span>
            <span class="action-desc">查看和管理所有短链接</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </router-link>

        <router-link to="/admin/users" class="action-card">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <div class="action-info">
            <span class="action-title">管理用户</span>
            <span class="action-desc">查看和管理系统用户</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </router-link>

        <router-link to="/admin/config" class="action-card">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
          </div>
          <div class="action-info">
            <span class="action-title">系统配置</span>
            <span class="action-desc">配置系统参数和选项</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLinkStats } from '@/api/link'

const stats = ref({})

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  try {
    const res = await getLinkStats()
    stats.value = res.data || {}
  } catch (error) {
    console.error('Failed to load stats', error)
  }
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
  color: white;
}

.stat-icon.links {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.stat-icon.clicks {
  background: linear-gradient(135deg, #10b981, #34d399);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
}

.stat-icon.today {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.3);
}

.stat-icon.new {
  background: linear-gradient(135deg, #ef4444, #f87171);
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.admin-layout.minimal .stat-value {
  background: linear-gradient(135deg, #1e3a5f, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.admin-layout.minimal .stat-label {
  color: #64748b;
}

.admin-layout.minimal .stat-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .stat-card:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(59, 130, 246, 0.25);
}

.admin-layout.minimal .quick-actions-section h3 {
  color: #1e3a5f;
}

.admin-layout.minimal .action-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .action-card:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(59, 130, 246, 0.3);
}

.admin-layout.minimal .action-title {
  color: #1e3a5f;
}

.admin-layout.minimal .action-desc {
  color: #64748b;
}

.admin-layout.minimal .action-icon {
  background: rgba(59, 130, 246, 0.1);
}

.admin-layout.minimal .action-icon svg {
  color: #3b82f6;
}

.admin-layout.minimal .action-arrow {
  color: #94a3b8;
}

.admin-layout.minimal .action-card:hover .action-arrow {
  color: #2563eb;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
}

.stat-trend svg {
  width: 20px;
  height: 20px;
}

.stat-trend.up {
  color: #34d399;
}

.stat-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 4px 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
}

.quick-actions-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 16px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.action-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(99, 102, 241, 0.3);
  transform: translateX(4px);
}

.action-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.15));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon svg {
  width: 24px;
  height: 24px;
  color: #a5b4fc;
}

.action-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.action-title {
  font-size: 15px;
  font-weight: 600;
  color: white;
}

.action-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.action-arrow {
  width: 20px;
  height: 20px;
  color: rgba(255, 255, 255, 0.3);
  transition: transform 0.2s;
}

.action-card:hover .action-arrow {
  transform: translateX(4px);
  color: #a5b4fc;
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
