<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="角色状态"
          clearable
          style="width: 240px"
        >
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8" style="margin-bottom: 10px">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          >删除</el-button
        >
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色编号" prop="roleId" width="120" />
      <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="显示顺序" prop="roleSort" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-show="total > 0"
      :total="total"
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList"
      @current-change="getList"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="roleRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item prop="roleKey">
          <template #label>
            <span>
              <el-tooltip
                content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasRole('admin')`)"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              权限字符
            </span>
          </template>
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

// 模拟角色数据，不接真实后端，只做展示
const loading = ref(false);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(2);
const roleList = ref<any[]>([
  {
    roleId: 1,
    roleName: "超级管理员",
    roleKey: "admin",
    roleSort: 1,
    status: "0",
    createTime: "2024-01-01 10:00:00",
    remark: "超级管理员",
  },
  {
    roleId: 2,
    roleName: "普通角色",
    roleKey: "common",
    roleSort: 2,
    status: "0",
    createTime: "2024-01-01 10:00:00",
    remark: "普通角色",
  },
]);
const title = ref("");
const open = ref(false);

const data = reactive({
  form: {} as any,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleKey: undefined,
    status: undefined,
  },
  rules: {
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }],
    roleKey: [{ required: true, message: "权限字符不能为空", trigger: "blur" }],
    roleSort: [{ required: true, message: "角色顺序不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);
const queryRef = ref();
const roleRef = ref();

function getList() {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.roleId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleStatusChange(row: any) {
  let text = row.status === "0" ? "启用" : "停用";
  ElMessage.success(text + "成功 (模拟)");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加角色";
}

function handleUpdate(row: any) {
  reset();
  form.value = { ...row };
  open.value = true;
  title.value = "修改角色";
}

function submitForm() {
  roleRef.value?.validate((valid: boolean) => {
    if (valid) {
      ElMessage.success("保存成功 (模拟)");
      open.value = false;
      getList();
    }
  });
}

function handleDelete(row: any) {
  ElMessageBox.confirm(
    '是否确认删除角色编号为"' + (row.roleId || ids.value) + '"的数据项？',
    "系统提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    },
  )
    .then(() => {
      ElMessage.success("删除成功 (模拟)");
      getList();
    })
    .catch(() => {});
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: "0",
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined,
  };
  roleRef.value?.resetFields();
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}
</style>
