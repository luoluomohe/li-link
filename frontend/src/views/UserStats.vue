<template>
  <div class="user-stats">
    <div class="container">
      <div class="header">
        <h1>访问统计</h1>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <div class="stats-cards">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">总 PV</div>
            <div class="stat-value">{{ stats.totalPv || 0 }}</div>
          </div>
        </el-card>
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">总 UV</div>
            <div class="stat-value">{{ stats.totalUv || 0 }}</div>
          </div>
        </el-card>
      </div>

      <div class="charts-section">
        <el-card shadow="hover">
          <div ref="deviceChart" class="chart-container"></div>
        </el-card>
        <el-card shadow="hover">
          <div ref="browserChart" class="chart-container"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { getLinkStats } from '@/api/shortlink'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const route = useRoute()
const stats = ref({})
const deviceChart = ref(null)
const browserChart = ref(null)

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  try {
    const res = await getLinkStats(route.params.id, '7')
    stats.value = res.data || {}

    if (stats.value.deviceDistribution) {
      initDeviceChart(stats.value.deviceDistribution)
    }
    if (stats.value.browserDistribution) {
      initBrowserChart(stats.value.browserDistribution)
    }
  } catch (error) {
    ElMessage.error('加载统计失败')
  }
}

const initDeviceChart = (data) => {
  const chart = echarts.init(deviceChart.value)
  const option = {
    title: { text: '设备分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: Object.entries(data).map(([name, value]) => ({ name, value }))
    }]
  }
  chart.setOption(option)
}

const initBrowserChart = (data) => {
  const chart = echarts.init(browserChart.value)
  const option = {
    title: { text: '浏览器分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: Object.entries(data).map(([name, value]) => ({ name, value }))
    }]
  }
  chart.setOption(option)
}
</script>

<style scoped>
.user-stats {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>