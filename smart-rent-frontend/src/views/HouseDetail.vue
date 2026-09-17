<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHouse, createAppointment, qaAsk } from '../api'
import { auth } from '../store/auth'

const route = useRoute()
const house = ref(null)
const canBook = computed(() => auth.role === 'TENANT')

const booking = ref({ appointmentTime: '', message: '' })
const bookingDialog = ref(false)

const q = ref('')
const qaResult = ref(null)
const asking = ref(false)

async function load() {
  house.value = await getHouse(route.params.id)
}

async function submitBooking() {
  if (!booking.value.appointmentTime) { ElMessage.warning('请填写预约时间'); return }
  await createAppointment({ houseId: house.value.id, ...booking.value })
  ElMessage.success('预约已提交，等待房东确认')
  bookingDialog.value = false
}

async function ask() {
  if (!q.value.trim()) { ElMessage.warning('请输入问题'); return }
  asking.value = true
  try {
    qaResult.value = await qaAsk({ question: q.value.trim(), houseId: house.value.id })
    q.value = ''
  } finally {
    asking.value = false
  }
}

onMounted(load)
</script>

<template>
  <div v-if="house">
    <el-card>
      <h2>{{ house.title }}</h2>
      <div class="rent">¥{{ house.rent }}/月</div>
      <div class="landlord">👤 房东：{{ house.landlordName || '—' }}</div>
      <el-descriptions :column="3" border style="margin-top:12px">
        <el-descriptions-item label="室/厅">{{ house.roomCount }}室{{ house.hallCount }}厅</el-descriptions-item>
        <el-descriptions-item label="面积">{{ house.area }}㎡</el-descriptions-item>
        <el-descriptions-item label="朝向">{{ house.orientation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ house.floor }} / {{ house.totalFloor }}</el-descriptions-item>
        <el-descriptions-item label="租赁方式">{{ house.rentType === 2 ? '合租' : '整租' }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ house.address }}</el-descriptions-item>
      </el-descriptions>
      <div class="desc">{{ house.description }}</div>
      <div class="tags">
        <el-tag v-for="t in (house.tags || [])" :key="t.id" style="margin-right:6px">{{ t.name }}</el-tag>
      </div>
      <div style="margin-top:16px">
        <el-button v-if="canBook" type="primary" @click="bookingDialog = true">预约看房</el-button>
      </div>
    </el-card>

    <el-card style="margin-top:16px">
      <h3>AI 问答</h3>
      <p class="tip">对这个房源有任何疑问，可以直接问 AI（答不上会转交给房东）。</p>
      <el-input v-model="q" placeholder="如：这个房子能养宠物吗？" style="margin-bottom:10px" @keyup.enter="ask">
        <template #append><el-button :loading="asking" @click="ask">提问</el-button></template>
      </el-input>
      <el-alert v-if="qaResult" :type="qaResult.answeredByKb === 1 ? 'success' : 'warning'" :closable="false" show-icon>
        <template #title>
          <div v-if="qaResult.answeredByKb === 1" style="white-space:pre-line">{{ qaResult.answer }}</div>
          <div v-else>该问题已转交房东，请稍候在「我的提问」中查看回复。</div>
        </template>
      </el-alert>
    </el-card>

    <el-dialog v-model="bookingDialog" title="预约看房" width="420px">
      <el-form label-width="80px">
        <el-form-item label="预约时间"><el-input v-model="booking.appointmentTime" placeholder="如 2026-09-20 14:00" /></el-form-item>
        <el-form-item label="留言"><el-input v-model="booking.message" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialog = false">取消</el-button>
        <el-button type="primary" @click="submitBooking">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.rent { color: #f56c6c; font-size: 26px; font-weight: 700; margin: 8px 0; }
.landlord { color: #409eff; font-size: 14px; margin-bottom: 4px; }
.desc { margin: 14px 0; color: #333; line-height: 1.6; }
.tags { margin: 8px 0; }
.tip { color: #999; font-size: 13px; }
</style>
