<template>
  <div class="virtual-team">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>虚拟团队管理</span>
          <el-button type="primary" @click="handleAdd">新建团队</el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="团队类型">
          <el-select v-model="queryParams.teamType" placeholder="全部" clearable style="width: 150px">
            <el-option label="项目团队" value="PROJECT" />
            <el-option label="跨部门团队" value="CROSS_DEPT" />
            <el-option label="任务团队" value="TASK" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="teamList" v-loading="loading" stripe>
        <el-table-column prop="teamName" label="团队名称" min-width="150" />
        <el-table-column prop="teamCode" label="团队编码" width="120" />
        <el-table-column prop="teamType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.teamType)">{{ getTypeName(row.teamType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leaderId" label="负责人" width="100">
          <template #default="{ row }">
            {{ row.leaderId ? `用户${row.leaderId}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="memberCount" label="成员数" width="80" />
        <el-table-column prop="effectiveStart" label="生效时间" width="110">
          <template #default="{ row }">
            {{ row.effectiveStart?.substring(0, 10) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="effectiveEnd" label="失效时间" width="110">
          <template #default="{ row }">
            {{ row.effectiveEnd?.substring(0, 10) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '有效' : '已解散' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewMembers(row)">成员</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="configPermissions(row)">权限</el-button>
            <el-button link type="danger" @click="handleDissolve(row)">解散</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑团队对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="团队编码" prop="teamCode">
          <el-input v-model="form.teamCode" placeholder="请输入团队编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="团队类型" prop="teamType">
          <el-select v-model="form.teamType" placeholder="请选择" style="width: 100%">
            <el-option label="项目团队" value="PROJECT" />
            <el-option label="跨部门团队" value="CROSS_DEPT" />
            <el-option label="任务团队" value="TASK" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生效时间">
              <el-date-picker v-model="form.effectiveStart" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失效时间">
              <el-date-picker v-model="form.effectiveEnd" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 成员管理对话框 -->
    <el-dialog v-model="memberDialogVisible" title="团队成员" width="600px">
      <div class="member-header" style="margin-bottom: 10px">
        <el-button type="primary" size="small" @click="showAddMember">添加成员</el-button>
      </div>
      <el-table :data="memberList" stripe>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="memberRole" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.memberRole)">{{ row.memberRole }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-if="row.memberRole !== 'LEADER'" link type="primary" @click="setAsLeader(row)">设为负责人</el-button>
            <el-button link type="danger" @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加成员对话框 -->
    <el-dialog v-model="addMemberDialogVisible" title="添加成员" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户">
          <el-select v-model="newMemberIds" multiple placeholder="选择用户" style="width: 100%">
            <el-option v-for="i in 10" :key="i" :label="`用户${i}`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="newMemberRole" placeholder="选择角色" style="width: 100%">
            <el-option label="成员" value="MEMBER" />
            <el-option label="访客" value="GUEST" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addMemberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addMembers">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限配置对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="团队权限配置" width="600px">
      <el-form label-width="100px">
        <el-form-item label="继承权限">
          <el-select v-model="permissionForm.inheritedPermissions" multiple filterable allow-create placeholder="输入权限标识" style="width: 100%">
          </el-select>
        </el-form-item>
        <el-form-item label="独有权限">
          <el-select v-model="permissionForm.exclusivePermissions" multiple filterable allow-create placeholder="输入权限标识" style="width: 100%">
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getTeamList,
  createTeam,
  dissolveTeam,
  getTeamMembers,
  addMembers as addMembersApi,
  removeMembers,
  setLeader,
  configurePermissions
} from '@/api/permission/team';
import type { FormInstance } from 'element-plus';

const loading = ref(false);
const teamList = ref<any[]>([]);
const queryParams = reactive({
  teamType: ''
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const isEdit = ref(false);
const formRef = ref<FormInstance>();
const form = reactive({
  teamId: 0,
  teamName: '',
  teamCode: '',
  teamType: '',
  effectiveStart: '',
  effectiveEnd: ''
});

const rules = {
  teamName: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
  teamCode: [{ required: true, message: '请输入团队编码', trigger: 'blur' }],
  teamType: [{ required: true, message: '请选择团队类型', trigger: 'change' }]
};

const memberDialogVisible = ref(false);
const memberList = ref<any[]>([]);
const currentTeamId = ref(0);

const addMemberDialogVisible = ref(false);
const newMemberIds = ref<number[]>([]);
const newMemberRole = ref('MEMBER');

const permissionDialogVisible = ref(false);
const permissionForm = reactive({
  inheritedPermissions: [] as string[],
  exclusivePermissions: [] as string[]
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getTeamList(queryParams);
    teamList.value = res.data || [];
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '新建团队';
  Object.assign(form, {
    teamId: 0,
    teamName: '',
    teamCode: '',
    teamType: '',
    effectiveStart: '',
    effectiveEnd: ''
  });
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEdit.value = true;
  dialogTitle.value = '编辑团队';
  Object.assign(form, row);
  dialogVisible.value = true;
};

const submitForm = async () => {
  await formRef.value?.validate();
  try {
    await createTeam(form);
    ElMessage.success(isEdit.value ? '修改成功' : '创建成功');
    dialogVisible.value = false;
    getList();
  } catch (error) {
    console.error(error);
  }
};

const handleDissolve = async (row: any) => {
  await ElMessageBox.confirm('确定要解散该团队吗？', '提示', { type: 'warning' });
  await dissolveTeam(row.teamId);
  ElMessage.success('团队已解散');
  getList();
};

const viewMembers = async (row: any) => {
  currentTeamId.value = row.teamId;
  try {
    const res = await getTeamMembers(row.teamId);
    memberList.value = res.data || [];
    memberDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const showAddMember = () => {
  newMemberIds.value = [];
  newMemberRole.value = 'MEMBER';
  addMemberDialogVisible.value = true;
};

const addMembers = async () => {
  if (newMemberIds.value.length === 0) {
    ElMessage.warning('请选择成员');
    return;
  }
  try {
    await addMembersApi(currentTeamId.value, {
      userIds: newMemberIds.value,
      role: newMemberRole.value
    });
    ElMessage.success('添加成功');
    addMemberDialogVisible.value = false;
    const res = await getTeamMembers(currentTeamId.value);
    memberList.value = res.data || [];
  } catch (error) {
    console.error(error);
  }
};

const removeMember = async (row: any) => {
  await ElMessageBox.confirm('确定要移除该成员吗？', '提示', { type: 'warning' });
  await removeMembers(currentTeamId.value, { userIds: [row.userId] });
  ElMessage.success('移除成功');
  const res = await getTeamMembers(currentTeamId.value);
  memberList.value = res.data || [];
};

const setAsLeader = async (row: any) => {
  await setLeader(currentTeamId.value, row.userId);
  ElMessage.success('已设为负责人');
  const res = await getTeamMembers(currentTeamId.value);
  memberList.value = res.data || [];
};

const configPermissions = (row: any) => {
  currentTeamId.value = row.teamId;
  try {
    const inherited = JSON.parse(row.inheritedPermissions || '[]');
    const exclusive = JSON.parse(row.exclusivePermissions || '[]');
    permissionForm.inheritedPermissions = inherited;
    permissionForm.exclusivePermissions = exclusive;
  } catch {
    permissionForm.inheritedPermissions = [];
    permissionForm.exclusivePermissions = [];
  }
  permissionDialogVisible.value = true;
};

const savePermissions = async () => {
  await configurePermissions(currentTeamId.value, {
    inheritedPermissions: permissionForm.inheritedPermissions,
    exclusivePermissions: permissionForm.exclusivePermissions
  });
  ElMessage.success('权限配置保存成功');
  permissionDialogVisible.value = false;
};

const getTypeTag = (type: string) => {
  const map: Record<string, string> = {
    PROJECT: 'primary',
    CROSS_DEPT: 'success',
    TASK: 'warning'
  };
  return map[type] || 'info';
};

const getTypeName = (type: string) => {
  const map: Record<string, string> = {
    PROJECT: '项目',
    CROSS_DEPT: '跨部门',
    TASK: '任务'
  };
  return map[type] || type;
};

const getRoleTag = (role: string) => {
  const map: Record<string, string> = {
    LEADER: 'danger',
    MEMBER: 'primary',
    GUEST: 'info'
  };
  return map[role] || 'info';
};

getList();
</script>

<style scoped>
.virtual-team {
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

.member-header {
  margin-bottom: 10px;
}
</style>
