<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishHouse, updateHouse, myHouses, listTags, aiSuggestTags } from '../api'

const route = useRoute()
const router = useRouter()
const editId = route.params.id ? Number(route.params.id) : null

const tags = ref([])
const suggested = ref([])
const saving = ref(false)

const form = reactive({
  title: '', description: '', address: '', area: null, roomCount: null, hallCount: null,
  rent: null, rentType: 1, orientation: '', floor: null, totalFloor: null, images: '', tagIds: []
})

async function suggest() {
  if (!form.title && !form.description) { ElMessage.warning('先填写标题或描述'); return }
  suggested.value = await aiSuggestTags({ title: form.title, description: form.description })
}

function addTag(t) {
  if (!form.tagIds.includes(t.id)) form.tagIds.push(t.id)
}

async function save() {
  if (!form.title) { ElMessage.warning('请填写标题'); return }
  saving.value = true
  try {
    if (editId) {
      await updateHouse(editId, form)
      ElMessage.success('已保存')
    } else {
      await publishHouse(form)
      ElMessage.success('已发布，等待审核')
    }
    router.push('/landlord')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  tags.value = await listTags()
  if (editId) {
    const list = await myHouses()
    const h = list.find(x => x.id === editId)
    if (h) {
      Object.assign(form, {
        title: h.title, description: h.description, address: h.address, area: h.area,
        roomCount: h.roomCount, hallCount: h.hallCount, rent: h.rent, rentType: h.rentType || 1,
        orientation: h.orientation, floor: h.floor, totalFloor: h.totalFloor, images: h.images,
        tagIds: (h.tags || []).map(t => t.id)
      })
    }
  }
})
</script>

<template>
  <el-card>
    <h3>{{ editId ? '编辑房源' : '发布房源' }}</h3>
    <el-form label-width="90px" style="max-width:700px">
      <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="如：附近有池塘，可以钓鱼" /></el-form-item>
      <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
      <el-row>
        <el-col :span="8"><el-form-item label="面积(㎡)"><el-input-number v-model="form.area" :min="0" :controls="false" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="室数"><el-input-number v-model="form.roomCount" :min="0" :controls="false" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="厅数"><el-input-number v-model="form.hallCount" :min="0" :controls="false" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="月租(元)"><el-input-number v-model="form.rent" :min="0" :controls="false" /></el-form-item>
      <el-form-item label="租赁方式">
        <el-radio-group v-model="form.rentType">
          <el-radio :value="1">整租</el-radio>
          <el-radio :value="2">合租</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-row>
        <el-col :span="12"><el-form-item label="朝向"><el-input v-model="form.orientation" /></el-form-item></el-col>
        <el-col :span="6"><el-form-item label="楼层"><el-input-number v-model="form.floor" :min="0" :controls="false" /></el-form-item></el-col>
        <el-col :span="6"><el-form-item label="总楼层"><el-input-number v-model="form.totalFloor" :min="0" :controls="false" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="选择标签" style="width:100%">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="suggest">✨ AI 根据标题/描述推荐标签</el-button>
        <div v-if="suggested.length" style="margin-top:8px">
          <span class="tip">AI 推荐（点击添加）：</span>
          <el-tag v-for="t in suggested" :key="t.id" class="sug" @click="addTag(t)">{{ t.name }}</el-tag>
        </div>
      </el-form-item>
      <el-button type="primary" :loading="saving" @click="save">{{ editId ? '保存' : '发布' }}</el-button>
      <el-button @click="router.push('/landlord')">返回</el-button>
    </el-form>
  </el-card>
</template>

<style scoped>
.sug { cursor: pointer; margin: 0 6px 6px 0; }
.tip { color: #999; font-size: 13px; }
</style>
