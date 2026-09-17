import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

// 请求拦截：带上 token（后端要求直接放 Authorization 头，不加 Bearer）
request.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')
  if (token) cfg.headers.Authorization = token
  return cfg
})

// 响应拦截：code===200 直接返回 data；401 跳登录；其它报错弹提示
request.interceptors.response.use(
  resp => {
    const body = resp.data
    if (body && body.code === 200) return body.data
    if (body && body.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error('登录已过期，请重新登录')
      setTimeout(() => { window.location.hash = '#/login'; window.location.reload() }, 500)
      return Promise.reject(body)
    }
    ElMessage.error(body?.message || '请求失败')
    return Promise.reject(body)
  },
  err => {
    ElMessage.error('网络错误或后端服务未启动')
    return Promise.reject(err)
  }
)

// ===== 用户 =====
export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getMe = () => request.get('/user/me')

// ===== 房源 =====
export const listHouses = (params) => request.get('/house', { params })
export const filterHouses = (params) => request.get('/house/filter', { params })
export const searchHouses = (q) => request.get('/house/search', { params: { q } })
export const semanticSearch = (q) => request.get('/house/semantic', { params: { q } })
export const getHouse = (id) => request.get(`/house/${id}`)
export const publishHouse = (data) => request.post('/house/publish', data)
export const updateHouse = (id, data) => request.put(`/house/${id}`, data)
export const deleteHouse = (id) => request.delete(`/house/${id}`)
export const offlineHouse = (id) => request.post(`/house/${id}/offline`)
export const onlineHouse = (id) => request.post(`/house/${id}/online`)
export const myHouses = () => request.get('/house/my')
export const pendingHouses = () => request.get('/house/admin/pending')
export const auditHouse = (id, data) => request.post(`/house/admin/${id}/audit`, data)

// ===== 标签 =====
export const listTags = () => request.get('/tag/list')
export const createTag = (data) => request.post('/tag', data)
export const updateTag = (id, data) => request.put(`/tag/${id}`, data)
export const deleteTag = (id) => request.delete(`/tag/${id}`)
export const aiSuggestTags = (data) => request.post('/tag/ai-suggest', data)

// ===== 知识库 =====
export const listKnowledge = (params) => request.get('/knowledge/list', { params })
export const searchKnowledge = (q) => request.get('/knowledge/search', { params: { q } })
export const createKnowledge = (data) => request.post('/knowledge', data)
export const updateKnowledge = (id, data) => request.put(`/knowledge/${id}`, data)
export const deleteKnowledge = (id) => request.delete(`/knowledge/${id}`)

// ===== AI 问答 =====
export const qaAsk = (data) => request.post('/qa/ask', data)
export const qaMy = () => request.get('/qa/my')
export const qaPending = () => request.get('/qa/pending')
export const qaAnswer = (id, data) => request.post(`/qa/${id}/answer`, data)
export const qaAdminPending = () => request.get('/qa/admin/pending')
export const qaAdminAnswer = (id, data) => request.post(`/qa/admin/${id}/answer`, data)

// ===== 预约看房 =====
export const createAppointment = (data) => request.post('/appointment', data)
export const myAppointments = () => request.get('/appointment/my')
export const receivedAppointments = () => request.get('/appointment/received')
export const confirmAppointment = (id) => request.post(`/appointment/${id}/confirm`)
export const cancelAppointment = (id) => request.post(`/appointment/${id}/cancel`)
