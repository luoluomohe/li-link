<template>
  <div class="login-container" :class="themeStyle">
    <div class="bg-pattern"></div>
    <div class="floating-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="login-wrapper">
      <div class="login-card">
        <div class="card-header">
          <div class="logo">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="3"/>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
              </svg>
            </div>
            <span class="logo-text">LiLink</span>
          </div>
          <h1>管理后台</h1>
          <p class="subtitle">请登录以访问管理控制台</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group" :class="{ focused: focusedField === 'username', filled: form.username }">
            <label>用户名</label>
            <div class="input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              <input 
                v-model="form.username" 
                type="text" 
                @focus="focusedField = 'username'"
                @blur="focusedField = ''"
                placeholder="请输入用户名"
              />
            </div>
          </div>

          <div class="form-group" :class="{ focused: focusedField === 'password', filled: form.password }">
            <label>密码</label>
            <div class="input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input 
                v-model="form.password" 
                :type="showPassword ? 'text' : 'password'"
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
                placeholder="请输入密码"
              />
              <button type="button" class="toggle-password" @click="showPassword = !showPassword">
                <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-group" :class="{ focused: focusedField === 'captcha', filled: form.captchaCode }">
            <label>验证码</label>
            <div class="captcha-wrapper">
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
                </svg>
                <input 
                  v-model="form.captchaCode" 
                  type="text"
                  maxlength="4"
                  @focus="focusedField = 'captcha'"
                  @blur="focusedField = ''"
                  placeholder="请输入验证码"
                  @keyup.enter="handleLogin"
                />
              </div>
              <div class="captcha-image-wrapper" @click="refreshCaptcha">
                <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                <div v-else class="captcha-loading">
                  <div class="spinner"></div>
                </div>
                <div class="refresh-overlay">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M23 4v6h-6"/>
                    <path d="M1 20v-6h6"/>
                    <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <button type="submit" class="login-btn" :disabled="loading" :class="{ loading: loading }">
            <span v-if="!loading">登录</span>
            <span v-else class="btn-loading">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </span>
          </button>
        </form>

        <div class="card-footer">
          <button class="back-home" @click="$router.push('/')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            返回首页
          </button>
          <div class="demo-account">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="16" x2="12" y2="12"/>
              <line x1="12" y1="8" x2="12.01" y2="8"/>
            </svg>
            <span>默认账号：admin / admin123</span>
          </div>
        </div>
      </div>

      <div class="features">
        <div class="feature">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          <span>安全登录</span>
        </div>
        <div class="feature">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <line x1="3" y1="9" x2="21" y2="9"/>
            <line x1="9" y1="21" x2="9" y2="9"/>
          </svg>
          <span>数据管理</span>
        </div>
        <div class="feature">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10"/>
            <line x1="12" y1="20" x2="12" y2="4"/>
            <line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
          <span>统计分析</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin, getCaptcha } from '@/api/admin'
import { getThemeConfig } from '@/api/config'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const showPassword = ref(false)
const focusedField = ref('')
const captchaId = ref('')
const captchaImage = ref('')
const primaryColor = ref('#6366f1')
const themeStyle = ref('default')

const form = reactive({
  username: '',
  password: '',
  captchaCode: ''
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
  const loginEl = document.querySelector('.login-container')
  if (loginEl) {
    loginEl.style.setProperty('--primary-color', primaryColor.value)
  }
}

onMounted(() => {
  refreshCaptcha()
  loadTheme()
})

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaId.value = res.data.captchaId
    captchaImage.value = res.data.imageBase64
    form.captchaCode = ''
  } catch (e) {
    ElMessage.error('获取验证码失败')
  }
}

const handleLogin = async () => {
  if (!form.username) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!form.password) {
    ElMessage.error('请输入密码')
    return
  }
  if (!form.captchaCode) {
    ElMessage.error('请输入验证码')
    return
  }

  loading.value = true
  try {
    const res = await adminLogin(form, captchaId.value, form.captchaCode)
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_username', res.data.username)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #0f0f23 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  padding: 20px;
}

.bg-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(99, 102, 241, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(139, 92, 246, 0.08) 0%, transparent 50%);
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
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  top: -200px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #8b5cf6, #a855f7);
  bottom: -150px;
  left: -100px;
  animation-delay: -10s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #a855f7, #c084fc);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 40px;
  backdrop-filter: blur(20px);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.3);
}

.logo-icon svg {
  width: 26px;
  height: 26px;
  color: white;
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header h1 {
  font-size: 22px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  position: relative;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper svg {
  position: absolute;
  left: 16px;
  width: 18px;
  height: 18px;
  color: rgba(255, 255, 255, 0.3);
  transition: color 0.2s;
  pointer-events: none;
}

.form-group.focused .input-wrapper svg {
  color: #a5b4fc;
}

.input-wrapper input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  font-size: 15px;
  color: white;
  outline: none;
  transition: all 0.2s;
  font-family: inherit;
}

.input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.input-wrapper input:focus {
  border-color: rgba(99, 102, 241, 0.5);
  background: rgba(255, 255, 255, 0.05);
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.3);
  transition: color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-password:hover {
  color: rgba(255, 255, 255, 0.6);
}

.toggle-password svg {
  position: static;
  width: 18px;
  height: 18px;
}

.captcha-wrapper {
  display: flex;
  gap: 12px;
}

.captcha-wrapper .input-wrapper {
  flex: 1;
}

.captcha-image-wrapper {
  width: 120px;
  height: 50px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.captcha-loading {
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.refresh-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.captcha-image-wrapper:hover .refresh-overlay {
  opacity: 1;
}

.refresh-overlay svg {
  width: 20px;
  height: 20px;
  color: white;
}

.login-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  box-shadow: 0 4px 24px rgba(99, 102, 241, 0.3);
  margin-top: 8px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.dot {
  width: 6px;
  height: 6px;
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

.card-footer {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.back-home {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.back-home svg {
  width: 16px;
  height: 16px;
}

.back-home:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.demo-account {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.demo-account svg {
  width: 14px;
  height: 14px;
}

.features {
  display: flex;
  gap: 32px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.feature svg {
  width: 16px;
  height: 16px;
  color: #a5b4fc;
}

@media (max-width: 480px) {
  .login-card {
    padding: 28px 24px;
  }

  .features {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }

  .captcha-wrapper {
    flex-direction: column;
  }

  .captcha-image-wrapper {
    width: 100%;
    height: 48px;
  }
}

.login-container {
  --primary-color: #6366f1;
}

.login-container.minimal {
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 50%, #bfdbfe 100%);
}

.login-container.minimal .login-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.login-container.minimal .card-header h1,
.login-container.minimal .logo-text {
  color: #1e3a5f;
  -webkit-text-fill-color: #1e3a5f;
}

.login-container.minimal .subtitle {
  color: #64748b;
}

.login-container.minimal .form-group label {
  color: #475569;
}

.login-container.minimal .input-wrapper input {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.2);
  color: #1e3a5f;
}

.login-container.minimal .input-wrapper input::placeholder {
  color: #94a3b8;
}

.login-container.minimal .input-wrapper svg {
  color: #64748b;
}

.login-container.minimal .form-group.focused .input-wrapper svg {
  color: #3b82f6;
}

.login-container.minimal .login-btn {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.login-container.minimal .back-home {
  background: rgba(255, 255, 255, 0.7);
  border-color: rgba(59, 130, 246, 0.2);
  color: #475569;
}

.login-container.minimal .back-home:hover {
  background: rgba(59, 130, 246, 0.1);
  color: #1e3a5f;
}

.login-container.minimal .demo-account {
  color: #64748b;
}

.login-container.minimal .feature {
  color: #64748b;
}

.login-container.minimal .feature svg {
  color: #3b82f6;
}

.login-container.minimal .captcha-image-wrapper {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.2);
}

.login-container.minimal .toggle-password {
  color: #64748b;
}

.login-container.minimal .toggle-password:hover {
  color: #3b82f6;
}

.login-container.dark {
  background: #0a0a0a;
}

.login-container.dark .bg-pattern {
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(99, 102, 241, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(139, 92, 246, 0.05) 0%, transparent 50%);
}

.login-container.glass {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.login-container.glass .login-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.login-container .shape-1 {
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
}

.login-container .shape-2 {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 80%, #8b5cf6), #a855f7);
}

.login-container .shape-3 {
  background: linear-gradient(135deg, color-mix(in srgb, var(--primary-color) 60%, #a855f7), #c084fc);
}

.login-container .logo-icon {
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  box-shadow: 0 8px 32px color-mix(in srgb, var(--primary-color) 30%, transparent);
}

.login-container .login-btn {
  background: linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 80%, #8b5cf6));
  box-shadow: 0 4px 24px color-mix(in srgb, var(--primary-color) 30%, transparent);
}

.login-container .login-btn:hover:not(:disabled) {
  box-shadow: 0 8px 32px color-mix(in srgb, var(--primary-color) 40%, transparent);
}
</style>
