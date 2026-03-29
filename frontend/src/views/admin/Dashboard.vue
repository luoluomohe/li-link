<template>
  <div class="dashboard">
    <h2>工作台</h2>

    <div class="stats-cards">
      <el-card shadow="hover">
        <div class="stat-item">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><Link /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">总短链数</div>
            <div class="stat-value">{{ stats.totalLinks || 0 }}</div>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover">
        <div class="stat-item">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">总访问量</div>
            <div class="stat-value">{{ stats.totalClicks || 0 }}</div>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover">
        <div class="stat-item">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日访问</div>
            <div class="stat-value">{{ stats.todayClicks || 0 }}</div>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover">
        <div class="stat-item">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日新增</div>
            <div class="stat-value">{{ stats.todayLinks || 0 }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="recent-section">
      <el-card shadow="hover">
        <template #header>
          <span>快捷操作</span>
        </template>
        <div class="quick-actions">
          <el-button type="primary" @click="$router.push('/admin/links')">管理短链</el-button>
          <el-button type="success" @click="$router.push('/admin/users')">管理用户</el-button>
          <el-button type="warning" @click="$router.push('/admin/config')">系统配置</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Link, View, Timer, User } from '@element-plus/icons-vue'
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
.dashboard h2 {
  margin-bottom: 20px;
  color: #333;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.quick-actions {
  display: flex;
  gap: 10px;
}
</style>