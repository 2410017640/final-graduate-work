<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchHouses, semanticSearch } from '../api'

const router = useRouter()
const q = ref('')
const mode = ref('nl') // nl=自然语言筛选, semantic=语义检索
const results = ref([])
const loading = ref(false)

async function search() {
  if (!q.value.trim()) return
  loading.value = true
  try {
    if (mode.value === 'nl') {
      results.value = await searchHouses(q.value.trim())
    } else {
      results.value = await semanticSearch(q.value.trim())
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <el-card>
      <h3>AI 找房</h3>
      <el-radio-group v-model="mode" style="margin-bottom:12px">
        <el-radio-button value="nl">自然语言筛选</el-radio-button>
        <el-radio-button value="semantic">语义检索（识别非预制特征）</el-radio-button>
      </el-radio-group>
      <el-input v-model="q" size="large" placeholder="试试：附近有湖的房子 / 近地铁的两居室4000以内" clearable @keyup.enter="search">
        <template #append><el-button type="primary" :loading="loading" @click="search">找房</el-button></template>
      </el-input>
    </el-card>

    <div v-if="mode === 'semantic'" style="margin-top:12px">
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="it in results" :key="it.house.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="card" shadow="hover" @click="router.push('/house/' + it.house.id)">
            <h4>{{ it.house.title }}</h4>
            <div class="rent">¥{{ it.house.rent }}/月</div>
            <div class="addr">{{ it.house.address }}</div>
            <div v-if="it.matchedFeatures && it.matchedFeatures.length" class="match">
              <el-tag v-for="f in it.matchedFeatures" :key="f" size="small" type="warning" style="margin-right:4px">
                {{ f }}<span v-if="it.nonPresetMatched">（AI识别·非预制）</span>
              </el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loading && !results.length" description="无匹配房源" />
    </div>

    <div v-else style="margin-top:12px">
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="h in results" :key="h.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="card" shadow="hover" @click="router.push('/house/' + h.id)">
            <h4>{{ h.title }}</h4>
            <div class="rent">¥{{ h.rent }}/月</div>
            <div class="addr">{{ h.address }}</div>
            <div>
              <el-tag v-for="t in (h.tags || [])" :key="t.id" size="small" style="margin-right:4px">{{ t.name }}</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loading && !results.length" description="无匹配房源" />
    </div>
  </div>
</template>

<style scoped>
.card { cursor: pointer; margin-bottom: 16px; }
.rent { color: #f56c6c; font-weight: 700; }
.addr { color: #999; font-size: 12px; margin: 4px 0; }
.match { margin-top: 6px; }
</style>
