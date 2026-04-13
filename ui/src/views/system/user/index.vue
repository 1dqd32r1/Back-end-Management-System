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
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          placeholder="请输入手机号码"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户编号" align="center" prop="userId" />
      <el-table-column label="用户名称" align="center" prop="userName" />
      <el-table-column label="用户昵称" align="center" prop="nickName" />
      <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column
        label="操作"
        align="center"
        width="150"
        class-name="small-padding fixed-width"
      >
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

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="form.userId == undefined">
              <el-input
                v-model="form.password"
                placeholder="请输入密码"
                type="password"
                maxlength="20"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option label="男" value="0"></el-option>
                <el-option label="女" value="1"></el-option>
                <el-option label="未知" value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { listUser, getUser, delUser, addUser, updateUser } from "@/api/system/user";

const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const userList = ref<any[]>([]);
const title = ref("");
const open = ref(false);

const data = reactive({
  form: {} as any,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    phonenumber: undefined,
  },
  rules: {
    userName: [{ required: true, message: "用户名称不能为空", trigger: "blur" }],
    nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  },
});

const { queryParams, form, rules } = toRefs(data);
const queryRef = ref();
const userRef = ref();

/** 查询用户列表 */
function getList() {
  loading.value = true;
  listUser(queryParams.value)
    .then((res: any) => {
      userList.value = res.data.records;
      total.value = res.data.total;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加用户";
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  reset();
  const userId = row.userId || ids.value[0];
  getUser(userId).then((res: any) => {
    form.value = res.data;
    open.value = true;
    title.value = "修改用户";
  });
}

/** 提交按钮 */
function submitForm() {
  userRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addUser(form.value).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row: any) {
  const userIds = row.userId || ids.value.join(",");
  ElMessageBox.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？', "系统提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      return delUser(userIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    })
    .catch(() => {});
}

/** 表单重置 */
function reset() {
  form.value = {
    userId: undefined,
    userName: undefined,
    nickName: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: "0",
    status: "0",
  };
  userRef.value?.resetFields();
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
