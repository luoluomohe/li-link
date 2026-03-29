import request from '../utils/request'

export function getCaptcha() {
  return request({
    url: '/admin/captcha',
    method: 'get'
  })
}

export function adminLogin(data, captchaId, captchaCode) {
  return request({
    url: '/admin/login',
    method: 'post',
    data,
    params: { captchaId, captchaCode }
  })
}

export function adminLogout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

export function getAllUsers() {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function getUserInfo() {
  return request({
    url: '/admin/user/info',
    method: 'get'
  })
}

export function resetUserPassword(id, data) {
  return request({
    url: `/admin/user/${id}/reset-password`,
    method: 'post',
    data
  })
}