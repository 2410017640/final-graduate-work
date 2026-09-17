<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pendingHouses, auditHouse, listTags, createTag, updateTag, deleteTag, listKnowledge, createKnowledge, updateKnowledge, deleteKnowledge } from '../api'

const active = ref('audit')
const houses = ref([])
const tags = ref([])
const knowledges = ref([])

// 审核拒绝
const rejectDialog = ref(false)
const curHouse = ref(null)
const rejectReason = ref('')

// 标签对话框
const tagDialog = ref(false)
const tagForm = reactive({ id: null, name: '', category: '' })

// 知识对话框
const kbDialog = ref(false)
const kbForm = reactive({ id: null, question: '', answer: '', category: '' })

async function loadHouses() { houses.value = await pendingHouses() }
async function loadTags() { tags.value = await listTags() }
async function loadKb() { knowledges.value = await listKnowledge({}) }

async function pass(id) { await auditHouse(id, { pass: 1 }); ElMessage.success('已通过'); loadHouses() }
function openReject(h) { curHouse.value = h; rejectReason.value = ''; rejectDialog.value = true }
async function submitReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写拒绝原因'); return }
  await auditHouse(curHouse.value.id, { pass: 0, rejectReason: rejectReason.value.trim() })
  ElMessage.success('已拒绝'); rejectDialog.value = false; loadHouses()
}

function openTag(t) { Object.assign(tagForm, t ? { id: t.id, name: t.name, category: t.category } : { id: null, name: '', category: '' }); tagDialog.value = true }
async function saveTag() {
  if (!tagForm.name) { ElMessage.warning('请填写标签名'); return }
  if (tagForm.id) { await updateTag(tagForm.id, tagForm); } else { await createTag(tagForm); }
  ElMessage.success('已保存'); tagDialog.value = false; loadTags()
}
async function delTag(id) {
  await ElMessageBox.confirm('确认删除该标签？', '提示', { type: 'warning' })
  await deleteTag(id); ElMessage.success('已删除'); loadTags()
}

function openKb(k) {
  Object.assign(kbForm, k ? { id: k.id, question: k.question, answer: k.answer, category: k.category }
    : { id: null, question: '', answer: '', category: '' })
  kbDialog.value = true
}
async function saveKb() {
  if (!kbForm.question) { ElMessage.warning('请填写问题'); return }
  if (kbForm.id) { await updateKnowledge(kbForm.id, kbForm); } else { await createKnowledge(kbForm); }
  ElMessage.success('已保存'); kbDialog.value = false; loadKb()
}
async function delKb(id) {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await deleteKnowledge(id); ElMessage.success('已删除'); loadKb()
}

onMounted(() => { loadHouses(); loadTags(); loadKb() })
</script>

<template>
  <el-card>
    <el-tabs v-model="active">
      <el-tab-pane label="房源审核" name="audit">
        <el-table :data="houses" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="rent" label="月租" width="90" />
          <el-table-column prop="address" label="地址" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="pass(row.id)">通过</el-button>
              <el-button size="small" type="danger" @click="openReject(row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!houses.length" description="暂无待审核房源" />
      </el-tab-pane>

      <el-tab-pane label="标签管理" name="tags">
        <el-button type="primary" size="small" style="margin-bottom:12px" @click="openTag(null)">新增标签</el-button>
        <el-table :data="tags" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="category" label="分组" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="openTag(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delTag(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="知识库管理" name="knowledge">
        <el-button type="primary" size="small" style="margin-bottom:12px" @click="openKb(null)">新增知识</el-button>
        <el-table :data="knowledges" border>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="question" label="问题" />
          <el-table-column prop="category" label="分类" width="90" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="openKb(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="delKb(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>

  <el-dialog v-model="rejectDialog" title="拒绝原因" width="420px">
    <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="填写拒绝原因" />
    <template #footer>
      <el-button @click="rejectDialog = false">取消</el-button>
      <el-button type="danger" @click="submitReject">拒绝</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="tagDialog" :title="tagForm.id ? '编辑标签' : '新增标签'" width="420px">
    <el-form label-width="60px">
      <el-form-item label="名称"><el-input v-model="tagForm.name" /></el-form-item>
      <el-form-item label="分组"><el-input v-model="tagForm.category" placeholder="如 交通/户型/设施" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="tagDialog = false">取消</el-button>
      <el-button type="primary" @click="saveTag">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="kbDialog" :title="kbForm.id ? '编辑知识' : '新增知识'" width="520px">
    <el-form label-width="60px">
      <el-form-item label="问题"><el-input v-model="kbForm.question" /></el-form-item>
      <el-form-item label="答案"><el-input v-model="kbForm.answer" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="分类"><el-input v-model="kbForm.category" placeholder="如 押金/合同/退租" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="kbDialog = false">取消</el-button>
      <el-button type="primary" @click="saveKb">保存</el-button>
    </template>
  </el-dialog>
</template>
