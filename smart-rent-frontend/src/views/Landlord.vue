<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { myHouses, offlineHouse, onlineHouse, deleteHouse, qaPending, qaAnswer, receivedAppointments, confirmAppointment } from '../api'

const router = useRouter()
const active = ref('houses')
const houses = ref([])
const questions = ref([])
const appointments = ref([])

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' }
const statusType = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
const apptMap = { 0: '待确认', 1: '已确认', 2: '已取消', 3: '已完成' }

// 回答问题对话框
const answerDialog = ref(false)
const curQuestion = ref(null)
const answerText = ref('')

async function loadHouses() { houses.value = await myHouses() }
async function loadQuestions() { questions.value = await qaPending() }
async function loadAppointments() { appointments.value = await receivedAppointments() }

async function off(id) { await offlineHouse(id); ElMessage.success('已下架'); loadHouses() }
async function on(id) { await onlineHouse(id); ElMessage.success('已重新上架'); loadHouses() }
async function del(id) {
  await ElMessageBox.confirm('确认删除该房源？', '提示', { type: 'warning' })
  await deleteHouse(id); ElMessage.success('已删除'); loadHouses()
}

function openAnswer(q) { curQuestion.value = q; answerText.value = ''; answerDialog.value = true }
async function submitAnswer() {
  if (!answerText.value.trim()) { ElMessage.warning('请输入回答'); return }
  await qaAnswer(curQuestion.value.id, { answer: answerText.value.trim() })
  ElMessage.success('已回复'); answerDialog.value = false; loadQuestions()
}

async function confirm(id) { await confirmAppointment(id); ElMessage.success('已确认'); loadAppointments() }

onMounted(() => { loadHouses(); loadQuestions(); loadAppointments() })
</script>

<template>
  <el-card>
    <el-tabs v-model="active">
      <el-tab-pane label="我的房源" name="houses">
        <el-button type="primary" style="margin-bottom:12px" @click="router.push('/house-edit')">发布房源</el-button>
        <el-table :data="houses" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="rent" label="月租" width="90" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }"><el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="240">
            <template #default="{ row }">
              <el-button size="small" @click="router.push('/house-edit/' + row.id)">编辑</el-button>
              <el-button v-if="row.status === 1" size="small" @click="off(row.id)">下架</el-button>
              <el-button v-if="row.status === 3" size="small" type="success" @click="on(row.id)">上架</el-button>
              <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="待回答问题" name="questions">
        <el-table :data="questions" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="question" label="问题" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }"><el-button size="small" type="primary" @click="openAnswer(row)">回答</el-button></template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!questions.length" description="暂无待回答的问题" />
      </el-tab-pane>

      <el-tab-pane label="收到的预约" name="appointments">
        <el-table :data="appointments" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="houseId" label="房源ID" width="80" />
          <el-table-column prop="appointmentTime" label="预约时间" />
          <el-table-column prop="message" label="留言" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }"><el-tag>{{ apptMap[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" size="small" type="success" @click="confirm(row.id)">确认</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!appointments.length" description="暂无预约" />
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-dialog v-model="answerDialog" title="回答问题" width="460px">
    <p>{{ curQuestion?.question }}</p>
    <el-input v-model="answerText" type="textarea" :rows="3" placeholder="输入回答" />
    <template #footer>
      <el-button @click="answerDialog = false">取消</el-button>
      <el-button type="primary" @click="submitAnswer">提交</el-button>
    </template>
  </el-dialog>
</template>
