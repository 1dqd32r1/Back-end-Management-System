<template>
  <div class="dynamic-rule">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>动态权限规则</span>
          <el-button type="primary" @click="handleAdd">新增规则</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="规则类型">
          <el-select v-model="queryParams.ruleType" placeholder="全部" clearable>
            <el-option label="时间规则" value="TIME" />
            <el-option label="地理规则" value="GEO" />
            <el-option label="行为规则" value="BEHAVIOR" />
            <el-option label="风险规则" value="RISK" />
            <el-option label="自定义" value="CUSTOM" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 规则列表 -->
      <el-table :data="ruleList" v-loading="loading" stripe>
        <el-table-column prop="ruleName" label="规则名称" min-width="150" />
        <el-table-column prop="ruleCode" label="规则编码" min-width="120" />
        <el-table-column prop="ruleType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.ruleType)">{{ getTypeName(row.ruleType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dimension" label="维度" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.dimension }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="actionType" label="动作" width="100">
          <template #default="{ row }">
            <el-tag :type="getActionTag(row.actionType)">{{ row.actionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" sortable />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleTest(row)">测试</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则编码" prop="ruleCode">
              <el-input v-model="form.ruleCode" placeholder="请输入规则编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规则类型" prop="ruleType">
              <el-select v-model="form.ruleType" placeholder="请选择">
                <el-option label="时间规则" value="TIME" />
                <el-option label="地理规则" value="GEO" />
                <el-option label="行为规则" value="BEHAVIOR" />
                <el-option label="风险规则" value="RISK" />
                <el-option label="自定义" value="CUSTOM" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作用维度" prop="dimension">
              <el-select v-model="form.dimension" placeholder="请选择">
                <el-option label="组织" value="ORGANIZATIONAL" />
                <el-option label="实体" value="ENTITY" />
                <el-option label="时空" value="SPATIOTEMPORAL" />
                <el-option label="风险" value="RISK" />
                <el-option label="行为" value="BEHAVIORAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="条件表达式" prop="conditionExpression">
          <el-input
            v-model="form.conditionExpression"
            type="textarea"
            :rows="4"
            placeholder="SpEL表达式，如: #time.currentHour < 9 or #time.currentHour > 18"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="动作类型" prop="actionType">
              <el-select v-model="form.actionType" placeholder="请选择">
                <el-option label="允许" value="ALLOW" />
                <el-option label="拒绝" value="DENY" />
                <el-option label="升级" value="ELEVATE" />
                <el-option label="降级" value="DEGRADE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="1" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规则描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入规则描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 测试对话框 -->
    <el-dialog v-model="testDialogVisible" title="规则测试" width="500px">
      <el-form :model="testForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="testForm.testContext.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-input v-model="testForm.testContext.resourceType" placeholder="如: project" />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="testForm.testContext.operation" placeholder="如: view" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="executeTest" :loading="testLoading">执行测试</el-button>
      </template>
      <el-divider v-if="testResult" />
      <el-descriptions v-if="testResult" :column="1" border>
        <el-descriptions-item label="测试结果">
          <el-tag :type="testResult.decision === 'ALLOW' ? 'success' : 'danger'">
            {{ testResult.decision }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="匹配规则">{{ testResult.matchedRules?.join(', ') || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getRulePage, createRule, updateRule, deleteRule, enableRule, disableRule, testRule } from '@/api/permission/rule';
import type { FormInstance } from 'element-plus';

const loading = ref(false);
const ruleList = ref<any[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ruleType: ''
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref<FormInstance>();
const form = reactive({
  ruleId: 0,
  ruleName: '',
  ruleCode: '',
  ruleType: '',
  dimension: '',
  conditionExpression: '',
  actionType: '',
  priority: 100,
  description: ''
});

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleCode: [{ required: true, message: '请输入规则编码', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  dimension: [{ required: true, message: '请选择作用维度', trigger: 'change' }],
  conditionExpression: [{ required: true, message: '请输入条件表达式', trigger: 'blur' }],
  actionType: [{ required: true, message: '请选择动作类型', trigger: 'change' }]
};

const testDialogVisible = ref(false);
const testLoading = ref(false);
const testForm = reactive({
  ruleId: 0,
  testContext: {
    userId: 1,
    resourceType: 'project',
    operation: 'view'
  }
});
const testResult = ref<any>(null);

function getList() {
  loading.value = true;
  getRulePage(queryParams).then((res: any) => {
    ruleList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  }).finally(() => {
    loading.value = false;
  });
}

function resetQuery() {
  queryParams.ruleType = '';
  queryParams.pageNum = 1;
  getList();
}

function handleAdd() {
  resetForm();
  dialogTitle.value = '新增规则';
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  resetForm();
  Object.assign(form, row);
  dialogTitle.value = '编辑规则';
  dialogVisible.value = true;
}

function resetForm() {
  form.ruleId = 0;
  form.ruleName = '';
  form.ruleCode = '';
  form.ruleType = '';
  form.dimension = '';
  form.conditionExpression = '';
  form.actionType = '';
  form.priority = 100;
  form.description = '';
}

async function submitForm() {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  try {
    if (form.ruleId) {
      await updateRule(form.ruleId, form);
      ElMessage.success('修改成功');
    } else {
      await createRule(form);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    getList();
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败');
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定要删除该规则吗？', '提示', { type: 'warning' });
  await deleteRule(row.ruleId);
  ElMessage.success('删除成功');
  getList();
}

async function handleStatusChange(row: any) {
  try {
    if (row.status === 1) {
      await enableRule(row.ruleId);
      ElMessage.success('启用成功');
    } else {
      await disableRule(row.ruleId);
      ElMessage.success('禁用成功');
    }
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1;
  }
}

function handleTest(row: any) {
  testForm.ruleId = row.ruleId;
  testResult.value = null;
  testDialogVisible.value = true;
}

async function executeTest() {
  testLoading.value = true;
  try {
    const res = await testRule(testForm);
    testResult.value = res.data;
  } catch (error: any) {
    ElMessage.error(error.message || '测试失败');
  } finally {
    testLoading.value = false;
  }
}

function getTypeTag(type: string) {
  const map: Record<string, string> = {
    TIME: 'primary',
    GEO: 'success',
    BEHAVIOR: 'warning',
    RISK: 'danger',
    CUSTOM: 'info'
  };
  return map[type] || 'info';
}

function getTypeName(type: string) {
  const map: Record<string, string> = {
    TIME: '时间',
    GEO: '地理',
    BEHAVIOR: '行为',
    RISK: '风险',
    CUSTOM: '自定义'
  };
  return map[type] || type;
}

function getActionTag(action: string) {
  const map: Record<string, string> = {
    ALLOW: 'success',
    DENY: 'danger',
    ELEVATE: 'warning',
    DEGRADE: 'warning'
  };
  return map[action] || 'info';
}

// 初始化
getList();
</script>

<style scoped>
.dynamic-rule {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
