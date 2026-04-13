<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <el-avatar :size="120" :src="avatarUrl">
                <el-icon :size="60"><UserFilled /></el-icon>
              </el-avatar>
              <el-upload
                :show-file-list="false"
                :before-upload="beforeUpload"
                :http-request="handleUpload"
                accept=".jpg,.jpeg,.png,.gif"
                class="avatar-uploader"
              >
                <el-button type="primary" size="small" class="upload-btn">修改头像</el-button>
              </el-upload>
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <div class="item-label">
                  <el-icon><User /></el-icon> 用户名称
                </div>
                <div class="item-value">{{ user.userName || '-' }}</div>
              </li>
              <li class="list-group-item">
                <div class="item-label">
                  <el-icon><Iphone /></el-icon> 手机号码
                </div>
                <div class="item-value">{{ user.phonenumber || '-' }}</div>
              </li>
              <li class="list-group-item">
                <div class="item-label">
                  <el-icon><Message /></el-icon> 用户邮箱
                </div>
                <div class="item-value">{{ user.email || '-' }}</div>
              </li>
              <li class="list-group-item">
                <div class="item-label">
                  <el-icon><Calendar /></el-icon> 创建日期
                </div>
                <div class="item-value">{{ user.createTime || '-' }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <el-form ref="userRef" :model="user" label-width="80px">
                <el-form-item label="用户昵称" prop="nickName">
                  <el-input v-model="user.nickName" maxlength="30" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phonenumber">
                  <el-input v-model="user.phonenumber" maxlength="11" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="user.email" maxlength="50" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="user.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submit">保存</el-button>
                  <el-button type="danger" @click="close">关闭</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <el-form ref="pwdRef" :model="pwd" label-width="80px">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="pwd.oldPassword"
                    placeholder="请输入旧密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwd.newPassword"
                    placeholder="请输入新密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="pwd.confirmPassword"
                    placeholder="请确认新密码"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitPwd">保存</el-button>
                  <el-button type="danger" @click="close">关闭</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import { useUserStore } from "@/store/modules/user";

const userStore = useUserStore();
const activeTab = ref("userinfo");
const user = ref({
  userId: 0,
  userName: "",
  nickName: "",
  phonenumber: "",
  email: "",
  sex: "0",
  avatar: "",
  createTime: "",
});

const pwd = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const avatarUrl = computed(() => {
  if (user.value.avatar) {
    return "http://localhost:8080" + user.value.avatar;
  }
  return "";
});

function getUserInfo() {
  request({
    url: "/common/getInfo",
    method: "get",
  }).then((res: any) => {
    user.value = res.data;
    userStore.setAvatar(res.data.avatar);
  });
}

function beforeUpload(file: File) {
  const isImage = ["image/jpeg", "image/png", "image/gif"].includes(file.type);
  const isLt10M = file.size / 1024 / 1024 < 10;

  if (!isImage) {
    ElMessage.error("只能上传 JPG/PNG/GIF 格式的图片!");
    return false;
  }
  if (!isLt10M) {
    ElMessage.error("图片大小不能超过 10MB!");
    return false;
  }
  return true;
}

function handleUpload(options: any) {
  const formData = new FormData();
  formData.append("file", options.file);
  formData.append("userId", String(user.value.userId));

  request({
    url: "/common/uploadAvatar",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  }).then((res: any) => {
    user.value.avatar = res.data.imgUrl;
    userStore.setAvatar(res.data.imgUrl);
    ElMessage.success("头像修改成功");
  }).catch(() => {
    ElMessage.error("头像上传失败");
  });
}

function submit() {
  request({
    url: "/system/user",
    method: "put",
    data: user.value,
  }).then(() => {
    ElMessage.success("修改成功");
    userStore.setNickName(user.value.nickName);
    getUserInfo();
  });
}

function submitPwd() {
  if (!pwd.value.oldPassword || !pwd.value.newPassword || !pwd.value.confirmPassword) {
    ElMessage.error("请填写完整密码信息");
    return;
  }
  if (pwd.value.newPassword !== pwd.value.confirmPassword) {
    ElMessage.error("两次输入的密码不一致");
    return;
  }
  ElMessage.success("密码修改成功");
  pwd.value = { oldPassword: "", newPassword: "", confirmPassword: "" };
}

function close() {
  // logic to close or go back
}

onMounted(() => {
  getUserInfo();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .app-container {
    padding: 10px;
  }

  :deep(.el-col) {
    margin-bottom: 15px;
  }

  :deep(.el-card) {
    margin: 0;
  }

  :deep(.el-avatar) {
    width: 80px !important;
    height: 80px !important;
  }

  .upload-btn {
    width: 100%;
    margin-top: 15px;
  }

  .list-group-item {
    padding: 12px 8px;
    font-size: 14px;
    flex-wrap: wrap;
  }

  .item-label {
    margin-bottom: 4px;
  }

  .list-group-item .el-icon {
    font-size: 16px;
    margin-right: 6px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
  }

  :deep(.el-tabs__item) {
    font-size: 14px;
    padding: 0 10px;
  }
}

.list-group {
  padding-left: 0;
  margin-bottom: 20px;
  list-style: none;
}

.list-group-item {
  border-bottom: 1px solid var(--el-border-color-lighter);
  border-top: 1px solid var(--el-border-color-lighter);
  margin-bottom: -1px;
  padding: 11px 12px;
  font-size: 13px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.list-group-item .el-icon {
  flex-shrink: 0;
}

.item-label {
  display: flex;
  align-items: center;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.item-label .el-icon {
  margin-right: 6px;
}

.item-value {
  text-align: right;
  word-break: break-all;
  color: var(--el-text-color-primary);
  flex-shrink: 1;
  min-width: 0;
}

.text-center {
  text-align: center;
  margin-bottom: 20px;
  padding: 20px 0;
}

.avatar-uploader {
  margin-top: 10px;
  display: block;
  text-align: center;
}

.upload-btn {
  margin-top: 10px;
}

/* 修复表单样式 */
:deep(.el-form) {
  max-width: 500px;
}

@media (max-width: 768px) {
  :deep(.el-form) {
    max-width: 100%;
  }
}
</style>
