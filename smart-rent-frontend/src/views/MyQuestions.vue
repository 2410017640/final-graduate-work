<script setup>
import { ref, onMounted } from 'vue'
import { qaAsk, qaMy } from '../api'
import { ElMessage } from 'element-plus'

const q = ref('')
const list = ref([])
const asking = ref(false)
const loading = ref(false)

async function ask() {
  if (!q.value.trim()) { ElMessage.warning('请输入问题'); return }
  asking.value = true
  try {
    await qaAsk({ question: q.value.trim() })
    q.value = ''
    load()
  } finally {
    asking.value = false
  }
}

async function load() {
  loading.value = true
  try {
    list.value = await qaMy()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-card>
      <h3>AI 问答</h3>
      <el-input v-model="q" placeholder="问我租房相关问题，如：押金怎么退？" @keyup.enter="ask">
        <template #append><el-button type="primary" :loading="asking" @click="ask">提问</el-button></template>
      </el-input>
    </el-card>

    <el-card style="margin-top:16px" v-loading="loading">
      <h4>我的提问</h4>
      <el-timeline v-if="list.length">
        <el-timeline-item v-for="item in list" :key="item.id" :timestamp="item.createTime" placement="top">
          <div class="q">问：{{ item.question }}</div>
          <div v-if="item.status === 1" class="a">
            <el-tag v-if="item.answeredByKb === 1" size="small" type="success">AI自动回答</el-tag>
            <el-tag v-else size="small" type="primary">房东回答</el-tag>
            <div style="white-space:pre-line; margin-top:6px">{{ item.answer }}</div>
          </div>
          <div v-else class="a pending">待回答（已转交房东）</div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else-if="!loading" description="还没有提问" />
    </el-card>
  </div>
</template>

<style scoped>
.q { font-weight: 600; }
.a { margin-top: 6px; color: #333; }
.pending { color: #e6a23c; }
</style>
