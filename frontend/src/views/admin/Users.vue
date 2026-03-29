<template>
  <div class="users-management">
    <div class="page-header">
      <div class="header-left">
        <h2>用户管理</h2>
        <span class="total-count">共 {{ users.length }} 位用户</span>
      </div>
      <button class="refresh-btn" @click="loadUsers">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M23 4v6h-6"/>
          <path d="M1 20v-6h6"/>
          <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
        刷新
      </button>
    </div>

    <div class="users-grid" v-if="users.length > 0">
      <div v-for="user in users" :key="user.id" class="user-card">
        <div class="user-header">
          <div class="user-avatar" :class="{ admin: user.isAdmin === 1 }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="user-info">
            <div class="user-name">{{ user.username }}</div>
            <div class="user-email">{{ user.email || '-' }}</div>
          </div>
          <div class="role-badge" :class="{ admin: user.isAdmin === 1 }">
            {{ user.isAdmin === 1 ? '管理员' : '用户' }}
          </div>
        </div>

        <div class="user-stats">
          <div class="stat">
            <span class="stat-label">ID</span>
            <span class="stat-value">{{ user.id }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">状态</span>
            <span class="status-badge" :class="user.status === 1 ? 'active' : 'inactive'">
              {{ user.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="stat">
            <span class="stat-label">注册时间</span>
            <span class="stat-value">{{ formatDate(user.createTime) }}</span>
          </div>
        </div>

        <div class="user-actions">
          <button 
            class="action-btn password" 
            @click="openPasswordDialog(user)"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            重置密码
          </button>
          <button 
            class="action-btn" 
            :class="user.status === 1 ? 'disable' : 'enable'"
            @click="toggleStatus(user)"
            :disabled="user.isAdmin === 1"
          >
            <svg v-if="user.status === 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            {{ user.status === 1 ? '禁用' : '启用' }}
          </button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
      </div>
      <h3>暂无用户数据</h3>
      <p>系统中还没有用户记录</p>
    </div>

    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div class="modal-overlay" v-if="showPasswordModal" @click.self="closePasswordDialog">
      <div class="modal-content">
        <div class="modal-header">
          <h3>重置密码</h3>
          <button class="close-btn" @click="closePasswordDialog">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <div class="user-info-row">
            <span class="label">用户：</span>
            <span class="value">{{ selectedUser?.username }}</span>
          </div>

          <div class="form-group">
            <label>新密码</label>
            <div class="input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input 
                v-model="passwordForm.newPassword" 
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入新密码"
                minlength="6"
              />
              <button type="button" class="toggle-btn" @click="showPassword = !showPassword">
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
            <span class="hint">密码长度至少 6 位</span>
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <div class="input-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input 
                v-model="passwordForm.confirmPassword" 
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入新密码"
              />
              <button type="button" class="toggle-btn" @click="showConfirmPassword = !showConfirmPassword">
                <svg v-if="showConfirmPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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
        </div>

        <div class="modal-footer">
          <button class="cancel-btn" @click="closePasswordDialog">取消</button>
          <button class="confirm-btn" @click="handleResetPassword" :disabled="passwordLoading">
            <span v-if="!passwordLoading">确认重置</span>
            <span v-else class="btn-loading">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllUsers, updateUserStatus, resetUserPassword } from '@/api/admin'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const showPasswordModal = ref(false)
const selectedUser = ref(null)
const passwordLoading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    users.value = res.data || []
  } catch (error) {
    ElMessage.error('加载用户失败')
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const openPasswordDialog = (user) => {
  selectedUser.value = user
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPassword.value = false
  showConfirmPassword.value = false
  showPasswordModal.value = true
}

const closePasswordDialog = () => {
  showPasswordModal.value = false
  selectedUser.value = null
}

const handleResetPassword = async () => {
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }

  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }

  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  passwordLoading.value = true
  try {
    await resetUserPassword(selectedUser.value.id, {
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码重置成功')
    closePasswordDialog()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '重置失败')
  } finally {
    passwordLoading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric' 
  })
}
</script>

<style scoped>
.users-management {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.total-count {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #a5b4fc;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.refresh-btn svg {
  width: 16px;
  height: 16px;
}

.refresh-btn:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: rgba(99, 102, 241, 0.3);
}

.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.user-card {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
}

.user-card:hover {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2), rgba(139, 92, 246, 0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar.admin {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.2), rgba(245, 158, 11, 0.2));
}

.user-avatar svg {
  width: 24px;
  height: 24px;
  color: #a5b4fc;
}

.user-avatar.admin svg {
  color: #f87171;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: white;
  margin-bottom: 2px;
}

.user-email {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}

.role-badge.admin {
  background: rgba(239, 68, 68, 0.15);
  color: #f87171;
}

.user-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  margin-bottom: 16px;
}

.stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.stat-value {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.status-badge {
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
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

.user-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
  font-family: inherit;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.password {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.2);
  color: #a5b4fc;
}

.action-btn.password:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: rgba(99, 102, 241, 0.3);
}

.action-btn.disable {
  background: rgba(251, 191, 36, 0.1);
  border-color: rgba(251, 191, 36, 0.2);
  color: #fbbf24;
}

.action-btn.disable:hover:not(:disabled) {
  background: rgba(251, 191, 36, 0.2);
  border-color: rgba(251, 191, 36, 0.3);
}

.action-btn.enable {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.2);
  color: #34d399;
}

.action-btn.enable:hover:not(:disabled) {
  background: rgba(16, 185, 129, 0.2);
  border-color: rgba(16, 185, 129, 0.3);
}

.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
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
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
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

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal-content {
  width: 100%;
  max-width: 420px;
  background: rgba(15, 15, 35, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.close-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn svg {
  width: 18px;
  height: 18px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.modal-body {
  padding: 24px;
}

.user-info-row {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
  margin-bottom: 20px;
}

.user-info-row .label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.user-info-row .value {
  font-size: 14px;
  font-weight: 500;
  color: white;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
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
  left: 14px;
  width: 18px;
  height: 18px;
  color: rgba(255, 255, 255, 0.3);
  pointer-events: none;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 48px 12px 44px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 14px;
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

.toggle-btn {
  position: absolute;
  right: 10px;
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

.toggle-btn:hover {
  color: rgba(255, 255, 255, 0.6);
}

.toggle-btn svg {
  position: static;
  width: 18px;
  height: 18px;
}

.hint {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 6px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  padding: 12px 20px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.confirm-btn {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  color: white;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
}

.confirm-btn:hover:not(:disabled) {
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
}

.confirm-btn:disabled {
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

@media (max-width: 640px) {
  .users-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .user-actions {
    flex-direction: column;
  }
}

.admin-layout.minimal .user-card {
  background: rgba(255, 255, 255, 0.85);
  border-color: rgba(59, 130, 246, 0.15);
}

.admin-layout.minimal .user-name {
  color: #1e3a5f;
}

.admin-layout.minimal .user-email,
.admin-layout.minimal .stat-label {
  color: #64748b;
}

.admin-layout.minimal .stat-value {
  color: #1e3a5f;
  font-weight: 600;
}

.admin-layout.minimal .user-stats {
  background: rgba(59, 130, 246, 0.05);
}

.admin-layout.minimal .status-badge.active {
  background: rgba(34, 197, 94, 0.15);
  color: #16a34a;
}

.admin-layout.minimal .status-badge.inactive {
  background: rgba(239, 68, 68, 0.15);
  color: #dc2626;
}

.admin-layout.minimal .action-btn {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
  color: #475569;
}

.admin-layout.minimal .action-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  color: #1e3a5f;
}

.admin-layout.minimal .modal-content {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(59, 130, 246, 0.2);
}

.admin-layout.minimal .modal-header h3 {
  color: #1e3a5f;
}

.admin-layout.minimal .close-btn {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
  color: #64748b;
}

.admin-layout.minimal .close-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  color: #1e3a5f;
}

.admin-layout.minimal .user-info-row {
  background: rgba(59, 130, 246, 0.05);
}

.admin-layout.minimal .user-info-row .label {
  color: #64748b;
}

.admin-layout.minimal .user-info-row .value {
  color: #1e3a5f;
}

.admin-layout.minimal .form-group label {
  color: #475569;
}

.admin-layout.minimal .input-wrapper input {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(59, 130, 246, 0.2);
  color: #1e3a5f;
}

.admin-layout.minimal .input-wrapper input::placeholder {
  color: #94a3b8;
}

.admin-layout.minimal .input-wrapper svg {
  color: #64748b;
}

.admin-layout.minimal .hint {
  color: #94a3b8;
}

.admin-layout.minimal .cancel-btn {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
  color: #475569;
}

.admin-layout.minimal .cancel-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  color: #1e3a5f;
}

.admin-layout.minimal .page-header h2 {
  color: #1e3a5f;
}

.admin-layout.minimal .page-header p {
  color: #64748b;
}

.admin-layout.minimal .empty-state h3 {
  color: #1e3a5f;
}

.admin-layout.minimal .empty-state p {
  color: #64748b;
}
</style>
