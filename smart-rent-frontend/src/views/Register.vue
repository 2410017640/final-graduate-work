<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api'

const router = useRouter()
const form = reactive({ username: '', password: '', nickname: '', phone: '', role: 'TENANT' })
const loading = ref(false)

async function submit() {
  if (!form.username || form.password.length < 6) {
    ElMessage.warning('请填写用户名，密码至少 6 位')
    return
  }
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2 class="title">注册账号</h2>
      <el-form label-width="80px" @submit.prevent>
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="TENANT">租客</el-radio>
            <el-radio value="LANDLORD">房东</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="submit">注册</el-button>
        <div class="link"><router-link to="/login">已有账号？去登录</router-link></div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.auth-wrap { display: flex; justify-content: center; align-items: center; min-height: 90vh; }
.auth-card { width: 420px; }
.title { text-align: center; margin-bottom: 20px; }
.link { text-align: center; margin-top: 12px; font-size: 14px; }
</style>
