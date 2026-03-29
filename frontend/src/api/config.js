import request from '../utils/request'

export function getAllConfigs() {
  return request({
    url: '/admin/config',
    method: 'get'
  })
}

export function updateConfig(key, value) {
  return request({
    url: '/admin/config',
    method: 'put',
    params: { key, value }
  })
}

export function getShortLinkLength() {
  return request({
    url: '/admin/config/short-link-length',
    method: 'get'
  })
}

export function isAllowCustomSuffix() {
  return request({
    url: '/admin/config/allow-custom-suffix',
    method: 'get'
  })
}

export function getDefaultExpireDays() {
  return request({
    url: '/admin/config/default-expire-days',
    method: 'get'
  })
}

export function getLimitConfig() {
  return request({
    url: '/admin/config/limit-config',
    method: 'get'
  })
}

export function getThemeConfig() {
  return request({
    url: '/admin/config/theme',
    method: 'get'
  })
}