<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listHouses, filterHouses, searchHouses, listTags } from '../api'

const router = useRouter()
const houses = ref([])
const tags = ref([])
const loading = ref(false)

// 手动筛选条件
const filters = reactive({
  keyword: '', tagIds: [], minRent: null, maxRent: null, roomCount: null
})

const nlQuery = ref('')

async function load() {
  loading.value = true
  try {
    // 有筛选条件用 filter，否则用列表
    const hasFilter = filters.keyword || (filters.tagIds && filters.tagIds.length) ||
      filters.minRent != null || filters.maxRent != null || filters.roomCount != null
    const params = {}
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.tagIds && filters.tagIds.length) params.tagIds = filters.tagIds.join(',')
    if (filters.minRent != null) params.minRent = filters.minRent
    if (filters.maxRent != null) params.maxRent = filters.maxRent
    if (filters.roomCount != null) params.roomCount = filters.roomCount
    houses.value = hasFilter ? await filterHouses(params) : await listHouses({})
  } finally {
    loading.value = false
  }
}

async function nlSearch() {
  if (!nlQuery.value.trim()) { load(); return }
  loading.value = true
  try {
    houses.value = await searchHouses(nlQuery.value.trim())
  } finally {
    loading.value = false
  }
}

function reset() {
  filters.keyword = ''; filters.tagIds = []; filters.minRent = null
  filters.maxRent = null; filters.roomCount = null; nlQuery.value = ''
  load()
}

onMounted(async () => {
  tags.value = await listTags()
  load()
})
</script>

<template>
  <div>
    <el-card class="search-bar" shadow="never">
      <el-input v-model="nlQuery" placeholder="自然语言找房，如：近地铁的两居室 4000以内" clearable @keyup.enter="nlSearch">
        <template #append><el-button @click="nlSearch">搜索</el-button></template>
      </el-input>
      <div class="filters">
        <el-select v-model="filters.tagIds" multiple collapse-tags placeholder="标签筛选" clearable style="width:240px">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
        <el-input-number v-model="filters.minRent" :min="0" placeholder="最低租金" :controls="false" />
        <span>-</span>
        <el-input-number v-model="filters.maxRent" :min="0" placeholder="最高租金" :controls="false" />
        <el-select v-model="filters.roomCount" placeholder="室数" clearable style="width:120px">
          <el-option v-for="n in [1,2,3,4,5]" :key="n" :label="n+'室'" :value="n" />
        </el-select>
        <el-input v-model="filters.keyword" placeholder="关键词" clearable style="width:160px" />
        <el-button type="primary" @click="load">筛选</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col v-for="h in houses" :key="h.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="house-card" shadow="hover" :body-style="{ padding: '0' }" @click="router.push('/house/' + h.id)">
          <div class="cover">{{ h.title.slice(0, 1) }}</div>
          <div class="card-body">
            <h3 class="htitle">{{ h.title }}</h3>
            <div class="rent">¥{{ h.rent }}<span class="per">/月</span></div>
            <div class="meta">{{ h.roomCount }}室{{ h.hallCount }}厅 · {{ h.area }}㎡ · {{ h.rentType === 2 ? '合租' : '整租' }}</div>
            <div class="addr">📍 {{ h.address }}</div>
            <div class="landlord">👤 房东：{{ h.landlordName || '—' }}</div>
            <div class="tags">
              <el-tag v-for="t in (h.tags || [])" :key="t.id" size="small" effect="plain">{{ t.name }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && !houses.length" description="暂无房源" />
  </div>
</template>

<style scoped>
.search-bar { margin-bottom: 16px; }
.filters { display: flex; align-items: center; gap: 10px; margin-top: 12px; flex-wrap: wrap; }
.house-card { cursor: pointer; margin-bottom: 16px; overflow: hidden; }
.cover { height: 90px; background: linear-gradient(135deg, #409eff, #67c23a); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 36px; font-weight: 700; }
.card-body { padding: 12px; }
.htitle { margin: 0 0 6px; font-size: 16px; }
.rent { color: #f56c6c; font-size: 20px; font-weight: 700; }
.per { font-size: 12px; font-weight: 400; color: #999; }
.meta { color: #666; font-size: 13px; margin: 4px 0; }
.addr { color: #999; font-size: 12px; margin-bottom: 4px; }
.landlord { color: #409eff; font-size: 13px; margin-bottom: 6px; }
.tags { margin-top: 4px; }
</style>
