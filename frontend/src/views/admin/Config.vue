<template>
  <div class="system-config">
    <div class="config-header">
      <div class="header-content">
        <h2>系统配置</h2>
        <p>管理系统的各项参数和设置</p>
      </div>
      <button class="save-btn" :disabled="loading" @click="handleSave">
        <svg v-if="!loading" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
          <polyline points="17 21 17 13 7 13 7 21"/>
          <polyline points="7 3 7 8 15 8"/>
        </svg>
        <span v-if="!loading">保存配置</span>
        <span v-else class="btn-loading">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
        </span>
      </button>
    </div>

    <div class="config-body">
      <div class="config-sidebar">
        <nav class="config-nav">
          <button 
            v-for="item in navItems" 
            :key="item.id"
            class="nav-item"
            :class="{ active: activeSection === item.id }"
            @click="activeSection = item.id"
          >
            <span class="nav-icon" v-html="item.icon"></span>
            <span class="nav-text">{{ item.name }}</span>
          </button>
        </nav>
      </div>

      <div class="config-content">
        <div v-show="activeSection === 'theme'" class="config-section">
          <div class="section-title">
            <h3>主题设置</h3>
            <p>自定义前端页面的视觉风格</p>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">主题颜色</span>
              <span class="label-desc">选择系统主色调</span>
            </div>
            <div class="color-picker-grid">
              <button 
                v-for="color in colorOptions" 
                :key="color.value"
                class="color-option"
                :class="{ active: config.primaryColor === color.value }"
                :style="{ '--color': color.value }"
                @click="config.primaryColor = color.value"
              >
                <span class="color-preview"></span>
                <span class="color-name">{{ color.name }}</span>
              </button>
            </div>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">主题风格</span>
              <span class="label-desc">选择页面整体风格</span>
            </div>
            <div class="style-options">
              <button 
                v-for="style in styleOptions" 
                :key="style.value"
                class="style-option"
                :class="{ active: config.themeStyle === style.value }"
                @click="config.themeStyle = style.value"
              >
                <div class="style-preview" :class="style.value">
                  <div class="preview-card"></div>
                  <div class="preview-lines">
                    <span></span>
                    <span></span>
                  </div>
                </div>
                <div class="style-info">
                  <span class="style-name">{{ style.name }}</span>
                  <span class="style-desc">{{ style.desc }}</span>
                </div>
              </button>
            </div>
          </div>
        </div>

        <div v-show="activeSection === 'link'" class="config-section">
          <div class="section-title">
            <h3>短链设置</h3>
            <p>配置短链接的基本参数</p>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">短链长度</span>
              <span class="label-desc">生成的短链接字符长度</span>
            </div>
            <div class="setting-control">
              <div class="number-input">
                <button class="num-btn" @click="config.shortLinkLength = Math.max(4, config.shortLinkLength - 1)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                </button>
                <span class="num-value">{{ config.shortLinkLength }}</span>
                <button class="num-btn" @click="config.shortLinkLength = Math.min(10, config.shortLinkLength + 1)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                </button>
              </div>
              <span class="hint">4-10位字符</span>
            </div>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">允许自定义后缀</span>
              <span class="label-desc">用户可自定义短链后缀</span>
            </div>
            <div class="setting-control">
              <button 
                class="toggle-switch" 
                :class="{ active: config.allowCustomSuffix }"
                @click="config.allowCustomSuffix = !config.allowCustomSuffix"
              >
                <span class="toggle-slider"></span>
              </button>
              <span class="status-text">{{ config.allowCustomSuffix ? '已开启' : '已关闭' }}</span>
            </div>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">默认过期天数</span>
              <span class="label-desc">短链接默认有效期限</span>
            </div>
            <div class="setting-control">
              <div class="number-input">
                <button class="num-btn" @click="config.defaultExpireDays = Math.max(1, config.defaultExpireDays - 1)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                </button>
                <span class="num-value">{{ config.defaultExpireDays }}</span>
                <button class="num-btn" @click="config.defaultExpireDays = Math.min(3650, config.defaultExpireDays + 1)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                </button>
              </div>
              <span class="hint">1-3650天</span>
            </div>
          </div>
        </div>

        <div v-show="activeSection === 'limit'" class="config-section">
          <div class="section-title">
            <h3>创建限制</h3>
            <p>限制用户创建短链的频率</p>
          </div>

          <div class="setting-group">
            <div class="setting-label">
              <span class="label-text">启用限制</span>
              <span class="label-desc">开启后将限制用户创建频率</span>
            </div>
            <div class="setting-control">
              <button 
                class="toggle-switch" 
                :class="{ active: config.limitEnabled }"
                @click="config.limitEnabled = !config.limitEnabled"
              >
                <span class="toggle-slider"></span>
              </button>
              <span class="status-text">{{ config.limitEnabled ? '已开启' : '已关闭' }}</span>
            </div>
          </div>

          <template v-if="config.limitEnabled">
            <div class="setting-group">
              <div class="setting-label">
                <span class="label-text">限制数量</span>
                <span class="label-desc">每个周期最多创建数量</span>
              </div>
              <div class="setting-control">
                <div class="number-input">
                  <button class="num-btn" @click="config.limitCount = Math.max(1, config.limitCount - 1)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                  </button>
                  <span class="num-value">{{ config.limitCount }}</span>
                  <button class="num-btn" @click="config.limitCount = Math.min(10000, config.limitCount + 1)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="12" y1="5" x2="12" y2="19"/>
                      <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                  </button>
                </div>
                <span class="hint">次/周期</span>
              </div>
            </div>

            <div class="setting-group">
              <div class="setting-label">
                <span class="label-text">限制周期</span>
                <span class="label-desc">限制重置的时间周期</span>
              </div>
              <div class="setting-control">
                <div class="period-options">
                  <button 
                    v-for="option in periodOptions" 
                    :key="option.value"
                    class="period-option"
                    :class="{ active: config.limitPeriod === option.value }"
                    @click="config.limitPeriod = option.value"
                  >
                    {{ option.label }}
                  </button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getShortLinkLength, isAllowCustomSuffix, getDefaultExpireDays, getLimitConfig, getThemeConfig, updateConfig } from '@/api/config'
import { ElMessage } from 'element-plus'

const activeSection = ref('theme')
const loading = ref(false)

const config = reactive({
  shortLinkLength: 6,
  allowCustomSuffix: true,
  defaultExpireDays: 30,
  limitEnabled: false,
  limitCount: 10,
  limitPeriod: 'day',
  primaryColor: '#6366f1',
  themeStyle: 'default'
})

const navItems = [
  { 
    id: 'theme', 
    name: '主题设置', 
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>'
  },
  { 
    id: 'link', 
    name: '短链设置', 
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>'
  },
  { 
    id: 'limit', 
    name: '创建限制', 
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>'
  }
]

const colorOptions = [
  { name: '靛蓝', value: '#6366f1' },
  { name: '紫罗兰', value: '#8b5cf6' },
  { name: '玫瑰红', value: '#f43f5e' },
  { name: '珊瑚橙', value: '#f97316' },
  { name: '翡翠绿', value: '#10b981' },
  { name: '天青蓝', value: '#0ea5e9' },
  { name: '琥珀黄', value: '#eab308' },
  { name: '青瓷绿', value: '#14b8a6' }
]

const styleOptions = [
  { name: '默认', value: 'default', desc: '经典渐变风格' },
  { name: '简约', value: 'minimal', desc: '清爽简洁风格' },
  { name: '暗黑', value: 'dark', desc: '深色沉浸风格' },
  { name: '玻璃', value: 'glass', desc: '毛玻璃拟态风格' }
]

const periodOptions = [
  { label: '每天', value: 'day' },
  { label: '每月', value: 'month' },
  { label: '每年', value: 'year' }
]

onMounted(() => {
  loadConfig()
})

const loadConfig = async () => {
  try {
    const lengthRes = await getShortLinkLength()
    config.shortLinkLength = lengthRes.data?.shortLinkLength || 6

    const suffixRes = await isAllowCustomSuffix()
    config.allowCustomSuffix = suffixRes.data?.allowCustomSuffix ?? true

    const expireRes = await getDefaultExpireDays()
    config.defaultExpireDays = expireRes.data?.defaultExpireDays || 30

    const limitRes = await getLimitConfig()
    config.limitEnabled = limitRes.data?.limitEnabled ?? false
    config.limitCount = limitRes.data?.limitCount || 10
    config.limitPeriod = limitRes.data?.limitPeriod || 'day'

    const themeRes = await getThemeConfig()
    config.primaryColor = themeRes.data?.primaryColor || '#6366f1'
    config.themeStyle = themeRes.data?.themeStyle || 'default'
  } catch (error) {
    console.error('Failed to load config', error)
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    await updateConfig('short_link_length', config.shortLinkLength.toString())
    await updateConfig('allow_custom_suffix', config.allowCustomSuffix.toString())
    await updateConfig('default_expire_days', config.defaultExpireDays.toString())
    await updateConfig('limit_enabled', config.limitEnabled.toString())
    await updateConfig('limit_count', config.limitCount.toString())
    await updateConfig('limit_period', config.limitPeriod)
    await updateConfig('theme_primary_color', config.primaryColor)
    await updateConfig('theme_style', config.themeStyle)
    
    localStorage.setItem('theme_primary_color', config.primaryColor)
    localStorage.setItem('theme_style', config.themeStyle)
    
    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.system-config {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  margin-bottom: 24px;
}

.header-content h2 {
  font-size: 22px;
  font-weight: 700;
  color: white;
  margin: 0 0 4px;
}

.header-content p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.save-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
}

.save-btn svg {
  width: 16px;
  height: 16px;
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(99, 102, 241, 0.4);
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-loading {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  width: 5px;
  height: 5px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.config-body {
  display: flex;
  gap: 24px;
  flex: 1;
  min-height: 0;
}

.config-sidebar {
  width: 220px;
  flex-shrink: 0;
}

.config-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  text-align: left;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.8);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.15));
  color: #a5b4fc;
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

.nav-text {
  flex: 1;
}

.config-content {
  flex: 1;
  min-width: 0;
}

.config-section {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 28px;
}

.section-title {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.section-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 4px;
}

.section-title p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.setting-group {
  margin-bottom: 28px;
}

.setting-group:last-child {
  margin-bottom: 0;
}

.setting-label {
  margin-bottom: 12px;
}

.label-text {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 2px;
}

.label-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.setting-control {
  display: flex;
  align-items: center;
  gap: 16px;
}

.color-picker-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
  gap: 12px;
}

.color-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 10px;
  background: rgba(255, 255, 255, 0.02);
  border: 2px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.color-option:hover {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.12);
}

.color-option.active {
  border-color: var(--color);
  background: rgba(255, 255, 255, 0.04);
}

.color-preview {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.color-name {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.style-options {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.style-option {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.02);
  border: 2px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  text-align: left;
}

.style-option:hover {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.12);
}

.style-option.active {
  border-color: #6366f1;
  background: rgba(99, 102, 241, 0.05);
}

.style-preview {
  width: 100%;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}

.style-preview.default {
  background: linear-gradient(135deg, #1e1b4b, #312e81);
}

.style-preview.minimal {
  background: #f8fafc;
}

.style-preview.dark {
  background: #0f0f0f;
}

.style-preview.glass {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.3), rgba(139, 92, 246, 0.3));
  backdrop-filter: blur(10px);
}

.preview-card {
  position: absolute;
  top: 6px;
  left: 6px;
  right: 6px;
  height: 18px;
  border-radius: 4px;
}

.default .preview-card {
  background: rgba(255, 255, 255, 0.1);
}

.minimal .preview-card {
  background: #e2e8f0;
}

.dark .preview-card {
  background: rgba(255, 255, 255, 0.05);
}

.glass .preview-card {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
}

.preview-lines {
  position: absolute;
  bottom: 6px;
  left: 6px;
  right: 6px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.preview-lines span {
  height: 3px;
  border-radius: 2px;
}

.default .preview-lines span {
  background: rgba(255, 255, 255, 0.2);
}

.default .preview-lines span:last-child {
  width: 60%;
}

.minimal .preview-lines span {
  background: #cbd5e1;
}

.minimal .preview-lines span:last-child {
  width: 60%;
}

.dark .preview-lines span {
  background: rgba(255, 255, 255, 0.1);
}

.dark .preview-lines span:last-child {
  width: 60%;
}

.glass .preview-lines span {
  background: rgba(255, 255, 255, 0.3);
}

.glass .preview-lines span:last-child {
  width: 60%;
}

.style-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.style-name {
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.style-desc {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.number-input {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  overflow: hidden;
}

.num-btn {
  width: 40px;
  height: 40px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.num-btn svg {
  width: 16px;
  height: 16px;
}

.num-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.num-value {
  width: 60px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.toggle-switch {
  position: relative;
  width: 48px;
  height: 26px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 13px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 0;
}

.toggle-switch.active {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.toggle-slider {
  position: absolute;
  left: 3px;
  top: 3px;
  width: 20px;
  height: 20px;
  background: white;
  border-radius: 50%;
  transition: transform 0.3s;
}

.toggle-switch.active .toggle-slider {
  transform: translateX(22px);
}

.status-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.period-options {
  display: flex;
  gap: 10px;
}

.period-option {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.period-option:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.period-option.active {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.15));
  border-color: rgba(99, 102, 241, 0.3);
  color: #a5b4fc;
}

@media (max-width: 900px) {
  .config-body {
    flex-direction: column;
  }

  .config-sidebar {
    width: 100%;
  }

  .config-nav {
    flex-direction: row;
    overflow-x: auto;
    padding: 8px;
  }

  .nav-item {
    flex-shrink: 0;
    padding: 10px 16px;
  }
}

@media (max-width: 640px) {
  .config-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .color-picker-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .style-options {
    grid-template-columns: repeat(2, 1fr);
  }

  .period-options {
    flex-wrap: wrap;
  }

  .period-option {
    flex: 1;
    min-width: 80px;
    text-align: center;
  }
}

.admin-layout.minimal .config-header {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .header-content h2 {
  color: #1e3a5f;
}

.admin-layout.minimal .header-content p {
  color: #64748b;
}

.admin-layout.minimal .config-nav {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .nav-item {
  color: #475569;
}

.admin-layout.minimal .nav-item:hover {
  background: rgba(59, 130, 246, 0.08);
  color: #1e3a5f;
}

.admin-layout.minimal .nav-item.active {
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
}

.admin-layout.minimal .config-section {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .section-title {
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .section-title h3 {
  color: #1e3a5f;
}

.admin-layout.minimal .section-title p {
  color: #64748b;
}

.admin-layout.minimal .label-text {
  color: #1e3a5f;
}

.admin-layout.minimal .label-desc {
  color: #64748b;
}

.admin-layout.minimal .color-option {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .color-option:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.25);
}

.admin-layout.minimal .color-option.active {
  border-color: #3b82f6;
}

.admin-layout.minimal .color-name {
  color: #475569;
}

.admin-layout.minimal .style-option {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .style-option:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.25);
}

.admin-layout.minimal .style-option.active {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
}

.admin-layout.minimal .style-name {
  color: #1e3a5f;
}

.admin-layout.minimal .style-desc {
  color: #64748b;
}

.admin-layout.minimal .number-input {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.2);
}

.admin-layout.minimal .num-btn {
  color: #64748b;
}

.admin-layout.minimal .num-btn:hover {
  background: rgba(59, 130, 246, 0.1);
  color: #1e3a5f;
}

.admin-layout.minimal .num-value {
  color: #1e3a5f;
  font-weight: 600;
}

.admin-layout.minimal .hint {
  color: #94a3b8;
}

.admin-layout.minimal .toggle-switch {
  background: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .toggle-switch.active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.admin-layout.minimal .status-text {
  color: #64748b;
}

.admin-layout.minimal .period-option {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(59, 130, 246, 0.15);
  color: #475569;
}

.admin-layout.minimal .period-option:hover {
  background: rgba(255, 255, 255, 0.8);
  color: #1e3a5f;
}

.admin-layout.minimal .period-option.active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-color: #3b82f6;
  color: white;
}
</style>
