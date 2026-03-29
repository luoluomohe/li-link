<template>
  <div class="admin-layout" :class="themeStyle">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
          </svg>
        </div>
        <span class="logo-text">LiLink</span>
      </div>

      <nav class="nav-menu">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: activeMenu === item.path }"
        >
          <span class="nav-icon" v-html="item.icon"></span>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <main class="main-content">
      <header class="topbar">
        <div class="page-title">
          <h1>{{ currentPageTitle }}</h1>
        </div>
        <div class="user-info">
          <div class="user-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <span class="username">{{ username }}</span>
        </div>
      </header>

      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminLogout } from '@/api/admin'
import { getThemeConfig } from '@/api/config'

const route = useRoute()
const router = useRouter()
const username = ref(localStorage.getItem('admin_username') || 'Admin')
const primaryColor = ref('#6366f1')
const themeStyle = ref('default')

const menuItems = [
  {
    path: '/admin/dashboard',
    label: '工作台',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>'
  },
  {
    path: '/admin/users',
    label: '用户管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>'
  },
  {
    path: '/admin/links',
    label: '短链管理',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>'
  },
  {
    path: '/admin/config',
    label: '系统配置',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>'
  }
]

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item?.label || '管理后台'
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
  const layoutEl = document.querySelector('.admin-layout')
  if (layoutEl) {
    layoutEl.style.setProperty('--primary-color', primaryColor.value)
  }
}

onMounted(() => {
  loadTheme()
})

watch(() => route.path, () => {
  loadTheme()
})

const handleLogout = async () => {
  try {
    await adminLogout()
  } catch (e) {}
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_username')
  router.push('/admin/login')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.admin-layout {
  --primary-color: #6366f1;
  display: flex;
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.admin-layout.default {
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #0f0f23 100%);
}

.admin-layout.minimal {
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 50%, #bfdbfe 100%);
}

.admin-layout.minimal .sidebar {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .logo-text,
.admin-layout.minimal .page-title h1,
.admin-layout.minimal .username,
.admin-layout.minimal .nav-item {
  color: #1e3a5f;
}

.admin-layout.minimal .nav-item:hover {
  background: rgba(59, 130, 246, 0.08);
  color: #1e3a5f;
}

.admin-layout.minimal .nav-item.active {
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
  box-shadow: inset 0 0 0 1px rgba(59, 130, 246, 0.25);
}

.admin-layout.minimal .topbar {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .user-avatar {
  background: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .user-avatar svg {
  color: #3b82f6;
}

.admin-layout.dark {
  background: #0a0a0a;
}

.admin-layout.dark .sidebar {
  background: rgba(20, 20, 20, 0.95);
  border-color: rgba(255, 255, 255, 0.05);
}

.admin-layout.dark .topbar {
  background: rgba(20, 20, 20, 0.95);
  border-color: rgba(255, 255, 255, 0.05);
}

.admin-layout.glass {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.admin-layout.glass .sidebar,
.admin-layout.glass .topbar {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-color: rgba(255, 255, 255, 0.1);
}

.sidebar {
  width: 260px;
  background: rgba(255, 255, 255, 0.02);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.admin-layout.minimal .logo {
  border-color: rgba(0, 0, 0, 0.08);
}

.admin-layout.dark .logo,
.admin-layout.glass .logo {
  border-color: rgba(255, 255, 255, 0.05);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px color-mix(in srgb, var(--primary-color) 30%, transparent);
}

.logo-icon svg {
  width: 22px;
  height: 22px;
  color: white;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, color-mix(in srgb, var(--primary-color) 50%, #fff));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.admin-layout.minimal .logo-text {
  background: linear-gradient(135deg, #1e293b, var(--primary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  transition: all 0.2s;
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon :deep(svg) {
  width: 20px;
  height: 20px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.8);
}

.nav-item.active {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 15%, transparent), color-mix(in srgb, var(--primary-color) 10%, transparent));
  color: color-mix(in srgb, var(--primary-color) 70%, #a5b4fc);
  box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--primary-color) 20%, transparent);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.admin-layout.minimal .sidebar-footer {
  border-color: rgba(0, 0, 0, 0.08);
}

.admin-layout.dark .sidebar-footer,
.admin-layout.glass .sidebar-footer {
  border-color: rgba(255, 255, 255, 0.05);
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #f87171;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.logout-btn svg {
  width: 18px;
  height: 18px;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.3);
}

.main-content {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 72px;
  background: rgba(255, 255, 255, 0.02);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(20px);
}

.page-title h1 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 20%, transparent), color-mix(in srgb, var(--primary-color) 15%, transparent));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar svg {
  width: 20px;
  height: 20px;
  color: color-mix(in srgb, var(--primary-color) 70%, #a5b4fc);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
}

.admin-layout.minimal .username {
  color: #475569;
}

.content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    width: 72px;
    padding: 0;
  }

  .logo {
    justify-content: center;
    padding: 0;
  }

  .logo-text {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 12px;
  }

  .nav-label {
    display: none;
  }

  .logout-btn span {
    display: none;
  }

  .main-content {
    margin-left: 72px;
  }

  .content {
    padding: 20px;
  }
}
</style>
