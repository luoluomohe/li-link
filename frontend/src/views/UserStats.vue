<template>
  <div class="user-stats" :class="themeStyle">
    <div class="bg-pattern"></div>
    <div class="floating-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
    </div>

    <div class="container">
      <header class="header">
        <div class="header-left">
          <button class="back-btn" @click="$router.back()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
          </button>
          <div class="title-group">
            <h1>访问统计</h1>
            <p class="subtitle">查看短链接的详细访问数据</p>
          </div>
        </div>
      </header>

      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon pv">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalPv || 0 }}</span>
            <span class="stat-label">页面浏览量 (PV)</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon uv">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalUv || 0 }}</span>
            <span class="stat-label">独立访客 (UV)</span>
          </div>
        </div>
      </div>

      <div class="charts-section">
        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                <line x1="8" y1="21" x2="16" y2="21"/>
                <line x1="12" y1="17" x2="12" y2="21"/>
              </svg>
              <span>设备分布</span>
            </div>
          </div>
          <div ref="deviceChart" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <div class="chart-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="2" y1="12" x2="22" y2="12"/>
                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
              </svg>
              <span>浏览器分布</span>
            </div>
          </div>
          <div ref="browserChart" class="chart-container"></div>
        </div>
      </div>

      <div class="loading-state" v-if="loading">
        <div class="loading-spinner"></div>
        <p>加载统计数据...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getLinkStats } from '@/api/shortlink'
import { getThemeConfig } from '@/api/config'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const route = useRoute()
const stats = ref({})
const deviceChart = ref(null)
const browserChart = ref(null)
const loading = ref(false)
const primaryColor = ref('#6366f1')
const themeStyle = ref('default')

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
  const userStatsEl = document.querySelector('.user-stats')
  if (userStatsEl) {
    userStatsEl.style.setProperty('--primary-color', primaryColor.value)
  }
}

onMounted(() => {
  loadStats()
  loadTheme()
})

const loadStats = async () => {
  loading.value = true
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
  } finally {
    loading.value = false
  }
}

const initDeviceChart = (data) => {
  const chart = echarts.init(deviceChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(15, 15, 35, 0.9)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      textStyle: { color: '#fff' }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: 'rgba(255, 255, 255, 0.6)' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: 'rgba(15, 15, 35, 0.8)',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold',
          color: '#fff'
        }
      },
      labelLine: { show: false },
      data: Object.entries(data).map(([name, value], index) => ({
        name,
        value,
        itemStyle: {
          color: ['#6366f1', '#8b5cf6', '#a855f7', '#c084fc'][index % 4]
        }
      }))
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const initBrowserChart = (data) => {
  const chart = echarts.init(browserChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(15, 15, 35, 0.9)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      textStyle: { color: '#fff' }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: 'rgba(255, 255, 255, 0.6)' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: 'rgba(15, 15, 35, 0.8)',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold',
          color: '#fff'
        }
      },
      labelLine: { show: false },
      data: Object.entries(data).map(([name, value], index) => ({
        name,
        value,
        itemStyle: {
          color: ['#10b981', '#34d399', '#6ee7b7', '#a7f3d0'][index % 4]
        }
      }))
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.user-stats {
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
  max-width: 1200px;
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
  color: white;
}

.stat-icon.pv {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.stat-icon.uv {
  background: linear-gradient(135deg, #10b981, #059669);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 24px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.12);
}

.chart-header {
  margin-bottom: 20px;
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.chart-title svg {
  width: 20px;
  height: 20px;
  color: #a5b4fc;
}

.chart-container {
  width: 100%;
  height: 300px;
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

@media (max-width: 768px) {
  .container {
    padding: 20px 16px;
  }

  .stats-overview {
    grid-template-columns: 1fr;
  }

  .charts-section {
    grid-template-columns: 1fr;
  }

  .stat-value {
    font-size: 28px;
  }
}

.user-stats {
  --primary-color: #6366f1;
}

.user-stats.minimal {
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 50%, #bfdbfe 100%);
}

.user-stats.minimal .title-group h1,
.user-stats.minimal .stat-value,
.user-stats.minimal .chart-title {
  color: #1e3a5f;
}

.user-stats.minimal .subtitle,
.user-stats.minimal .stat-label,
.user-stats.minimal .loading-state p {
  color: #3b82f6;
}

.user-stats.minimal .back-btn {
  background: rgba(255, 255, 255, 0.7);
  border-color: rgba(59, 130, 246, 0.2);
  color: #2563eb;
}

.user-stats.minimal .stat-card,
.user-stats.minimal .chart-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.user-stats.minimal .stat-icon {
  background: rgba(59, 130, 246, 0.1);
}

.user-stats.minimal .stat-icon svg {
  color: #3b82f6;
}

.user-stats.dark {
  background: #0a0a0a;
}

.user-stats.dark .bg-pattern {
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(99, 102, 241, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(139, 92, 246, 0.05) 0%, transparent 50%);
}

.user-stats.glass {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.user-stats.glass .stat-card,
.user-stats.glass .chart-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
