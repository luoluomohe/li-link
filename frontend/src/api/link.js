import request from '../utils/request'

export function getAllLinks(params) {
  return request({
    url: '/admin/links',
    method: 'get',
    params
  })
}

export function getLinkStats() {
  return request({
    url: '/admin/links/stats',
    method: 'get'
  })
}

export function updateLinkStatus(id, status) {
  return request({
    url: `/admin/links/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteLink(id) {
  return request({
    url: `/admin/links/${id}`,
    method: 'delete'
  })
}