<script setup>
import { ref, onMounted } from 'vue'
import { myAppointments, cancelAppointment } from '../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)

const statusMap = { 0: '待确认', 1: '已确认', 2: '已取消', 3: '已完成' }
const statusType = { 0: 'warning', 1: 'success', 2: 'info', 3: 'primary' }

async function load() {
  loading.value = true
  try {
    list.value = await myAppointments()
  } finally {
    loading.value = false
  }
}

async function cancel(id) {
  await cancelAppointment(id)
  ElMessage.success('已取消')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <el-card v-loading="loading">
      <h3>我的预约</h3>
      <el-table :data="list" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="houseId" label="房源ID" width="80" />
        <el-table-column prop="appointmentTime" label="预约时间" />
        <el-table-column prop="message" label="留言" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" @click="cancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无预约" />
    </el-card>
  </div>
</template>
