<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { auth } from './store/auth'

const router = useRouter()
const route = useRoute()

const isLoggedIn = computed(() => auth.isLoggedIn)
const role = computed(() => auth.role)
const username = computed(() => auth.user?.nickname || auth.user?.username || '')

const roleName = { ADMIN: '管理员', LANDLORD: '房东', TENANT: '租客' }

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout">
    <el-header class="header">
      <div class="logo" @click="router.push('/')">🏠 智能租房系统</div>
      <el-menu v-if="isLoggedIn" mode="horizontal" :default-active="route.path" :ellipsis="false" class="menu" router>
        <el-menu-item index="/">找房</el-menu-item>
        <el-menu-item index="/ai-find">AI找房</el-menu-item>
        <el-menu-item index="/qa">AI问答</el-menu-item>
        <el-menu-item index="/appointments">我的预约</el-menu-item>
        <el-menu-item v-if="role === 'LANDLORD'" index="/landlord">房东中心</el-menu-item>
        <el-menu-item v-if="role === 'ADMIN'" index="/admin">管理后台</el-menu-item>
      </el-menu>
      <div v-if="isLoggedIn" class="user">
        <el-tag size="small">{{ roleName[role] }}</el-tag>
        <span class="uname">{{ username }}</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </div>
</template>

<style>
* { box-sizing: border-box; }
body { margin: 0; font-family: "Helvetica Neue", Arial, "PingFang SC", "Microsoft YaHei", sans-serif; background: #f5f7fa; }
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.header { display: flex; align-items: center; background: #fff; border-bottom: 1px solid #e4e7ed; padding: 0 20px; }
.logo { font-size: 18px; font-weight: 700; cursor: pointer; margin-right: 30px; white-space: nowrap; }
.menu { flex: 1; border-bottom: none !important; }
.user { display: flex; align-items: center; gap: 8px; }
.uname { font-size: 14px; color: #333; }
.main { flex: 1; padding: 20px; }
</style>
