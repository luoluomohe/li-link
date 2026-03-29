<template>
  <div class="home" :class="[themeStyle, { 'has-result': result }]">
    <div class="bg-layer"></div>
    <div class="floating-orbs">
      <div class="orb" v-for="i in 5" :key="i" :style="getOrbStyle(i)"></div>
    </div>
    
    <div class="container">
      <header class="header">
        <div class="logo">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
              <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
            </svg>
          </div>
          <h1>LiLink</h1>
        </div>
        <p class="tagline">简洁高效的短链接生成服务</p>
        <nav class="nav-actions">
          <button class="nav-btn" @click="$router.push('/user/links')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
            </svg>
            <span>我的短链</span>
          </button>
          <button class="nav-btn outline" @click="$router.push('/admin/login')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
            <span>管理后台</span>
          </button>
        </nav>
      </header>

      <main class="main-content">
        <div class="create-section">
          <div class="create-card">
            <div class="card-header">
              <h2>创建短链接</h2>
              <p>将长链接转换为简洁易记的短链接</p>
            </div>
            
            <form @submit.prevent="handleCreate" class="create-form">
              <div class="input-group" :class="{ focused: focusedInput === 'url' }">
                <label>原始链接</label>
                <div class="input-wrapper">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
                    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
                  </svg>
                  <input 
                    v-model="form.url" 
                    type="url" 
                    placeholder="https://example.com/very-long-url..."
                    required
                    @focus="focusedInput = 'url'"
                    @blur="focusedInput = ''"
                  />
                </div>
              </div>

              <transition name="slide">
                <div v-if="allowCustomSuffix" class="input-group" :class="{ focused: focusedInput === 'suffix' }">
                  <label>自定义后缀 <span class="optional">可选</span></label>
                  <div class="input-wrapper">
                    <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 20h9"/>
                      <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                    </svg>
                    <input 
                      v-model="form.customSuffix" 
                      type="text" 
                      placeholder="my-custom-link"
                      @focus="focusedInput = 'suffix'"
                      @blur="focusedInput = ''"
                    />
                  </div>
                </div>
              </transition>

              <div class="input-group">
                <label>有效期</label>
                <div class="expire-grid">
                  <button 
                    v-for="option in expireOptions" 
                    :key="option.value"
                    type="button"
                    class="expire-btn"
                    :class="{ active: form.expireDays === option.value }"
                    @click="form.expireDays = option.value"
                  >
                    {{ option.label }}
                  </button>
                </div>
              </div>

              <button type="submit" class="submit-btn" :disabled="loading">
                <span v-if="loading" class="loading-dots">
                  <span></span><span></span><span></span>
                </span>
                <template v-else>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
                  </svg>
                  生成短链接
                </template>
              </button>
            </form>
          </div>

          <transition name="result">
            <div v-if="result" class="result-card">
              <div class="result-content">
                <div class="result-main">
                  <div class="result-header">
                    <div class="success-badge">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="20 6 9 17 4 12"/>
                      </svg>
                    </div>
                    <h3>短链接已生成</h3>
                  </div>
                  
                  <div class="short-link-box">
                    <code>{{ result.shortUrl }}</code>
                    <button class="copy-btn" @click="copyLink">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                      </svg>
                      复制
                    </button>
                  </div>

                  <div class="result-info">
                    <div class="info-row">
                      <span class="info-label">原始链接</span>
                      <span class="info-value">{{ truncateUrl(result.originalUrl) }}</span>
                    </div>
                    <div class="info-grid">
                      <div class="info-item">
                        <span class="info-label">点击</span>
                        <span class="info-value">{{ result.clickCount || 0 }}</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">创建</span>
                        <span class="info-value">{{ formatDate(result.createTime) }}</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">过期</span>
                        <span class="info-value">{{ formatDate(result.expireTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="qr-section">
                  <div class="qr-wrapper">
                    <canvas ref="qrCanvas" class="qr-canvas"></canvas>
                  </div>
                  <p class="qr-hint">扫码访问短链接</p>
                  <button class="download-qr-btn" @click="downloadQR">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="7 10 12 15 17 10"/>
                      <line x1="12" y1="15" x2="12" y2="3"/>
                    </svg>
                    下载二维码
                  </button>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </main>

      <footer class="footer">
        <p>安全 · 快速 · 可靠</p>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { createShortLink } from '@/api/shortlink'
import { isAllowCustomSuffix, getDefaultExpireDays, getThemeConfig } from '@/api/config'
import { ElMessage } from 'element-plus'
import QRCode from 'qrcode'

const loading = ref(false)
const result = ref(null)
const userId = ref(localStorage.getItem('user_id') || null)
const allowCustomSuffix = ref(true)
const focusedInput = ref('')
const qrCanvas = ref(null)
const primaryColor = ref('#6366f1')
const themeStyle = ref('default')

const expireOptions = [
  { label: '7天', value: 7 },
  { label: '30天', value: 30 },
  { label: '90天', value: 90 },
  { label: '180天', value: 180 },
  { label: '365天', value: 365 },
  { label: '永久', value: 3650 }
]

if (!userId.value) {
  userId.value = Math.abs(Date.now() % 1000000)
  localStorage.setItem('user_id', userId.value)
}

onMounted(async () => {
  try {
    const res = await isAllowCustomSuffix()
    const value = res.data?.allowCustomSuffix ?? res.data
    allowCustomSuffix.value = value === 'true' || value === true
  } catch (e) {
    console.error('Failed to load config', e)
  }
  try {
    const res = await getDefaultExpireDays()
    form.expireDays = res.data?.defaultExpireDays ?? res.data ?? 30
  } catch (e) {
    form.expireDays = 30
  }
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
})

const form = reactive({
  url: '',
  customSuffix: '',
  expireDays: 30
})

const applyTheme = () => {
  const homeEl = document.querySelector('.home')
  if (homeEl) {
    homeEl.style.setProperty('--primary-color', primaryColor.value)
  }
}

const getOrbStyle = (index) => {
  const colors = [
    primaryColor.value,
    adjustColor(primaryColor.value, 30),
    adjustColor(primaryColor.value, -30),
    '#8b5cf6',
    '#3b82f6'
  ]
  return {
    background: `linear-gradient(135deg, ${colors[index - 1]}, ${adjustColor(colors[index - 1], 20)})`,
    animationDelay: `${(index - 1) * -4}s`
  }
}

const adjustColor = (hex, amount) => {
  const num = parseInt(hex.slice(1), 16)
  const r = Math.min(255, Math.max(0, (num >> 16) + amount))
  const g = Math.min(255, Math.max(0, ((num >> 8) & 0x00FF) + amount))
  const b = Math.min(255, Math.max(0, (num & 0x0000FF) + amount))
  return `#${((r << 16) | (g << 8) | b).toString(16).padStart(6, '0')}`
}

const handleCreate = async () => {
  if (!form.url) {
    ElMessage.warning('请输入原始链接')
    return
  }

  loading.value = true
  try {
    const res = await createShortLink({
      url: form.url,
      customSuffix: form.customSuffix,
      expireDays: form.expireDays,
      userId: parseInt(userId.value)
    })
    result.value = res.data
    ElMessage.success('短链接生成成功')
    
    await nextTick()
    generateQRCode()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '生成失败')
  } finally {
    loading.value = false
  }
}

const generateQRCode = async () => {
  if (!qrCanvas.value || !result.value) return
  
  try {
    await QRCode.toCanvas(qrCanvas.value, result.value.shortUrl, {
      width: 180,
      margin: 2,
      color: {
        dark: '#1a1a2e',
        light: '#ffffff'
      },
      errorCorrectionLevel: 'M'
    })
  } catch (error) {
    console.error('QR Code generation failed:', error)
  }
}

const downloadQR = () => {
  if (!qrCanvas.value) return
  
  const link = document.createElement('a')
  link.download = `qrcode-${Date.now()}.png`
  link.href = qrCanvas.value.toDataURL('image/png')
  link.click()
}

const copyLink = () => {
  navigator.clipboard.writeText(result.value.shortUrl)
  ElMessage.success('已复制到剪贴板')
}

const truncateUrl = (url) => {
  if (!url) return ''
  return url.length > 50 ? url.substring(0, 50) + '...' : url
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

watch(result, () => {
  if (result.value) {
    nextTick(() => generateQRCode())
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap');

.home {
  --primary-color: #6366f1;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.home.default {
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #0f0f23 100%);
}

.home.minimal {
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 50%, #bfdbfe 100%);
}

.home.minimal .logo h1,
.home.minimal .card-header h2,
.home.minimal .result-header h3,
.home.minimal .info-value {
  color: #1e3a5f;
  -webkit-text-fill-color: #1e3a5f;
}

.home.minimal .tagline,
.home.minimal .card-header p,
.home.minimal .info-label,
.home.minimal .qr-hint,
.home.minimal .footer p {
  color: #3b82f6;
}

.home.minimal .create-card,
.home.minimal .result-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
  box-shadow: 0 4px 24px rgba(59, 130, 246, 0.08);
}

.home.minimal .input-wrapper input {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.2);
  color: #1e3a5f;
}

.home.minimal .input-wrapper input::placeholder {
  color: #60a5fa;
}

.home.minimal .input-group label {
  color: #2563eb;
}

.home.minimal .input-icon {
  color: #60a5fa;
}

.home.minimal .input-group.focused .input-icon {
  color: #3b82f6;
}

.home.minimal .optional {
  color: #60a5fa;
}

.home.minimal .expire-btn {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.home.minimal .expire-btn.active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border-color: #3b82f6;
}

.home.minimal .nav-btn {
  background: rgba(255, 255, 255, 0.7);
  color: #2563eb;
}

.home.minimal .nav-btn.outline {
  background: transparent;
  border-color: rgba(59, 130, 246, 0.3);
}

.home.dark {
  background: #0a0a0a;
}

.home.glass {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.home.glass .create-card,
.home.glass .result-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.bg-layer {
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(circle at 20% 20%, color-mix(in srgb, var(--primary-color) 15%, transparent) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, color-mix(in srgb, var(--primary-color) 10%, transparent) 0%, transparent 50%);
  pointer-events: none;
}

.floating-orbs {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: orbFloat 20s ease-in-out infinite;
}

.orb:nth-child(1) { width: 400px; height: 400px; top: -10%; right: -5%; }
.orb:nth-child(2) { width: 300px; height: 300px; bottom: -5%; left: -5%; }
.orb:nth-child(3) { width: 250px; height: 250px; top: 40%; left: 10%; }
.orb:nth-child(4) { width: 200px; height: 200px; top: 20%; right: 15%; }
.orb:nth-child(5) { width: 180px; height: 180px; bottom: 20%; right: 25%; }

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.4; }
  25% { transform: translate(20px, -30px) scale(1.05); opacity: 0.5; }
  50% { transform: translate(-10px, 20px) scale(0.95); opacity: 0.35; }
  75% { transform: translate(15px, 10px) scale(1.02); opacity: 0.45; }
}

.container {
  max-width: 680px;
  margin: 0 auto;
  padding: 48px 24px;
  position: relative;
  z-index: 1;
}

.header {
  text-align: center;
  margin-bottom: 48px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-bottom: 12px;
}

.logo-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px color-mix(in srgb, var(--primary-color) 40%, transparent);
}

.logo-icon svg {
  width: 26px;
  height: 26px;
  color: white;
}

.logo h1 {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, color-mix(in srgb, var(--primary-color) 50%, #fff));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -1px;
}

.tagline {
  color: rgba(255, 255, 255, 0.5);
  font-size: 15px;
  margin: 0 0 28px;
}

.nav-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: white;
  backdrop-filter: blur(10px);
}

.nav-btn svg {
  width: 16px;
  height: 16px;
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
}

.nav-btn.outline {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.nav-btn.outline:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.25);
}

.create-section {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.create-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 24px 24px 0 0;
  padding: 36px;
  backdrop-filter: blur(20px);
}

.card-header {
  margin-bottom: 32px;
  text-align: center;
}

.card-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.card-header p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.45);
  margin: 0;
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-group label {
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.65);
}

.optional {
  font-weight: 400;
  color: rgba(255, 255, 255, 0.35);
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: rgba(255, 255, 255, 0.25);
  pointer-events: none;
  transition: color 0.25s;
}

.input-wrapper input {
  width: 100%;
  padding: 16px 16px 16px 50px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  font-size: 15px;
  color: white;
  transition: all 0.25s ease;
  font-family: inherit;
}

.input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.25);
}

.input-wrapper input:focus {
  outline: none;
  border-color: color-mix(in srgb, var(--primary-color) 60%, transparent);
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--primary-color) 15%, transparent);
}

.input-group.focused .input-icon {
  color: var(--primary-color);
}

.expire-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.expire-btn {
  padding: 12px 8px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.expire-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.8);
}

.expire-btn.active {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 25%, transparent), color-mix(in srgb, var(--primary-color) 15%, transparent));
  border-color: color-mix(in srgb, var(--primary-color) 50%, transparent);
  color: white;
  font-weight: 500;
}

.submit-btn {
  padding: 18px 24px;
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-family: inherit;
  box-shadow: 0 4px 24px color-mix(in srgb, var(--primary-color) 35%, transparent);
}

.submit-btn svg {
  width: 18px;
  height: 18px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px color-mix(in srgb, var(--primary-color) 45%, transparent);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-dots {
  display: flex;
  gap: 6px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  animation: dotPulse 1.2s ease-in-out infinite;
}

.loading-dots span:nth-child(2) { animation-delay: 0.15s; }
.loading-dots span:nth-child(3) { animation-delay: 0.3s; }

@keyframes dotPulse {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.result-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-top: none;
  border-radius: 0 0 24px 24px;
  padding: 32px;
}

.result-content {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 32px;
  align-items: start;
}

.result-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.success-badge {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-badge svg {
  width: 18px;
  height: 18px;
  color: white;
}

.result-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.short-link-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: color-mix(in srgb, var(--primary-color) 12%, transparent);
  border: 1px solid color-mix(in srgb, var(--primary-color) 25%, transparent);
  border-radius: 14px;
}

.short-link-box code {
  flex: 1;
  font-family: 'JetBrains Mono', monospace;
  font-size: 15px;
  color: color-mix(in srgb, var(--primary-color) 70%, #a5b4fc);
  word-break: break-all;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.copy-btn svg {
  width: 14px;
  height: 14px;
}

.copy-btn:hover {
  background: rgba(255, 255, 255, 0.12);
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.info-value {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.qr-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.qr-wrapper {
  padding: 12px;
  background: white;
  border-radius: 12px;
}

.qr-canvas {
  display: block;
  border-radius: 8px;
}

.qr-hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
}

.download-qr-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.download-qr-btn svg {
  width: 14px;
  height: 14px;
}

.download-qr-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.footer {
  text-align: center;
  margin-top: 48px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
}

.footer p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.25);
  margin: 0;
  letter-spacing: 2px;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.result-enter-active,
.result-leave-active {
  transition: all 0.4s ease;
}

.result-enter-from,
.result-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 640px) {
  .container {
    padding: 32px 16px;
  }

  .logo h1 {
    font-size: 30px;
  }

  .nav-actions {
    flex-direction: column;
  }

  .nav-btn {
    justify-content: center;
  }

  .create-card {
    padding: 28px 20px;
    border-radius: 20px 20px 0 0;
  }

  .result-card {
    padding: 24px 20px;
    border-radius: 0 0 20px 20px;
  }

  .result-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .qr-section {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .expire-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}
</style>
