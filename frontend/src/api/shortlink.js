import request from '../utils/request'

export function createShortLink(data) {
  return request({
    url: '/shortlink/create',
    method: 'post',
    data
  })
}

export function getUserLinks(userId) {
  return request({
    url: `/shortlink/user/${userId}`,
    method: 'get'
  })
}

export function getLinkById(id) {
  return request({
    url: `/shortlink/${id}`,
    method: 'get'
  })
}

export function updateLinkStatus(id, status) {
  return request({
    url: `/shortlink/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteLink(id) {
  return request({
    url: `/shortlink/${id}`,
    method: 'delete'
  })
}

export function restoreLink(id) {
  return request({
    url: `/shortlink/${id}/restore`,
    method: 'put'
  })
}

export function getLinkStats(id, period) {
  return request({
    url: `/shortlink/${id}/stats`,
    method: 'get',
    params: { period }
  })
}