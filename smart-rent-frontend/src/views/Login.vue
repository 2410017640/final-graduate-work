<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import { auth } from '../store/auth'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    auth.login(res.token, res.user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已由拦截器弹提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2 class="title">🏠 智能租房系统</h2>
      <el-form label-width="0" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="submit">登录</el-button>
        <div class="link"><router-link to="/register">没有账号？去注册</router-link></div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.auth-wrap { display: flex; justify-content: center; align-items: center; min-height: 90vh; }
.auth-card { width: 380px; }
.title { text-align: center; margin-bottom: 20px; }
.link { text-align: center; margin-top: 12px; font-size: 14px; }
</style>
