import { reactive } from 'vue'

// 简单的登录态管理（token + user 存 localStorage）
export const auth = reactive({
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user') || 'null'),

  login(token, user) {
    this.token = token
    this.user = user
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
  },
  logout() {
    this.token = ''
    this.user = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  get role() {
    return this.user?.role || ''
  },
  get isLoggedIn() {
    return !!this.token
  }
})
