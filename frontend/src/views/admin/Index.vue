<template>
  <div class="admin-layout">
    <div class="sidebar">
      <div class="logo">
        <h2>短链管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/links">
          <el-icon><Link /></el-icon>
          <span>短链管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/config">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="main-content">
      <div class="topbar">
        <div class="breadcrumb">
          <span>欢迎，{{ username }}</span>
        </div>
        <div class="actions">
          <el-button @click="handleLogout" type="danger" size="small">退出登录</el-button>
        </div>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataAnalysis, User, Link, Setting } from '@element-plus/icons-vue'
import { adminLogout } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const username = ref(localStorage.getItem('admin_username') || 'Admin')

const activeMenu = computed(() => route.path)

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
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #304156;
  position: fixed;
  height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border-bottom: 1px solid #3d4a5c;
}

.logo h2 {
  font-size: 16px;
}

.main-content {
  flex: 1;
  margin-left: 200px;
}

.topbar {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.breadcrumb {
  font-size: 14px;
}

.content {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

:deep(.el-menu) {
  border-right: none;
}
</style>