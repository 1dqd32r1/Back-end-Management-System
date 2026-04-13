<template>
  <div class="navbar">
    <div class="hamburger-container" @click="toggleClick">
      <el-icon :size="20">
        <component :is="isCollapse ? 'Expand' : 'Fold'" />
      </el-icon>
    </div>
    <div class="right-menu">
      <div class="theme-toggle" @click="toggleTheme">
        <el-icon :size="20">
          <component :is="theme === 'light' ? 'Moon' : 'Sunny'" />
        </el-icon>
      </div>
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar size="small" :src="avatarUrl">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <el-icon class="el-icon-caret-bottom"><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown">
            <router-link to="/">
              <el-dropdown-item>首页</el-dropdown-item>
            </router-link>
            <router-link to="/system/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click.native="logout">
              <span style="display: block">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useUserStore } from "@/store/modules/user";
import { useAppStore } from "@/store/modules/app";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["toggle"]);
const userStore = useUserStore();
const appStore = useAppStore();
const router = useRouter();

const theme = computed(() => appStore.theme);

const avatarUrl = computed(() => {
  if (userStore.avatar) {
    return "http://localhost:8080" + userStore.avatar;
  }
  return "";
});

const toggleTheme = () => {
  appStore.toggleTheme();
};

const toggleClick = () => {
  emit("toggle");
};

const logout = () => {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      if (appStore.twoFactor) {
        ElMessageBox.prompt("请输入 123456 完成二次验证", "安全验证", {
          confirmButtonText: "验证",
          cancelButtonText: "取消",
          inputPattern: /^123456$/,
          inputErrorMessage: "验证码错误",
        })
          .then(() => {
            userStore.logout();
            router.push("/login");
          })
          .catch(() => {});
        return;
      }
      userStore.logout();
      router.push("/login");
    })
    .catch(() => {});
};
</script>

<style scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.hamburger-container {
  line-height: 46px;
  height: 100%;
  float: left;
  cursor: pointer;
  transition: background 0.3s;
  -webkit-tap-highlight-color: transparent;
  padding: 0 15px;
}
.hamburger-container:hover {
  background: var(--el-fill-color-light);
}
.right-menu {
  float: right;
  height: 100%;
  line-height: 50px;
  display: flex;
  align-items: center;
  padding-right: 20px;
  gap: 10px;
}
.theme-toggle {
  padding: 0 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background 0.3s;
  border-radius: 4px;
}
.theme-toggle:hover {
  background: var(--el-fill-color-light);
}
.theme-toggle:active {
  transform: scale(0.95);
}
.avatar-container {
  margin-right: 0;
}
.avatar-wrapper {
  margin-top: 5px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .navbar {
    height: 50px;
  }

  .hamburger-container {
    padding: 0 10px;
  }

  .right-menu {
    padding-right: 10px;
    gap: 5px;
  }

  .theme-toggle {
    padding: 0 8px;
  }

  :deep(.el-avatar) {
    width: 32px !important;
    height: 32px !important;
  }

  .avatar-wrapper .el-icon-caret-bottom {
    font-size: 14px;
  }

  :deep(.el-dropdown-menu) {
    max-width: 200px;
  }
}
</style>
