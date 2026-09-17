import { createRouter, createWebHashHistory } from 'vue-router'
import { auth } from '../store/auth'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/house/:id', component: () => import('../views/HouseDetail.vue') },
  { path: '/ai-find', component: () => import('../views/AiFind.vue') },
  { path: '/qa', component: () => import('../views/MyQuestions.vue') },
  { path: '/appointments', component: () => import('../views/Appointments.vue') },
  { path: '/landlord', component: () => import('../views/Landlord.vue'), meta: { role: 'LANDLORD' } },
  { path: '/house-edit/:id?', component: () => import('../views/HouseEdit.vue'), meta: { role: 'LANDLORD' } },
  { path: '/admin', component: () => import('../views/Admin.vue'), meta: { role: 'ADMIN' } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const logged = auth.isLoggedIn
  if (to.path !== '/login' && to.path !== '/register' && !logged) {
    next('/login')
  } else if (to.meta.role && auth.role !== to.meta.role) {
    next('/')
  } else {
    next()
  }
})

export default router
