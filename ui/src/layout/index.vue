<template>
  <div class="app-wrapper" :class="{ mobile: isMobile, 'hide-sidebar': isCollapse }">
    <div v-if="isMobile && !isCollapse" class="drawer-bg" @click="toggleSideBar" />
    <Sidebar class="sidebar-container" />
    <div class="main-container">
      <div class="fixed-header" :class="{ 'is-fixed': appStore.fixedHeader }">
        <Header @toggle="toggleSideBar" :is-collapse="isCollapse" />
        <TagsView v-if="appStore.tagsView" />
      </div>
      <section class="app-main" :style="{ paddingTop: appMainPaddingTop }">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </section>
    </div>
    <!-- 命令面板 -->
    <CommandPalette v-model:visible="showCommandPalette" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import Sidebar from "./components/Sidebar.vue";
import Header from "./components/Header.vue";
import TagsView from "./components/TagsView.vue";
import CommandPalette from "@/components/CommandPalette.vue";
import { useAppStore } from "@/store/modules/app";
import { useUserStore } from "@/store/modules/user";
import { registerCommands } from "@/commands";

const isCollapse = ref(false);
const isMobile = ref(false);
const showCommandPalette = ref(false);
const appStore = useAppStore();
const userStore = useUserStore();
const router = useRouter();
let inactivityTimer: number | undefined;

const toggleSideBar = () => {
  isCollapse.value = !isCollapse.value;
};

const checkMobile = () => {
  const rect = document.body.getBoundingClientRect();
  const mobile = rect.width - 1 < 992;
  isMobile.value = mobile;
  if (mobile) {
    isCollapse.value = true;
  } else {
    isCollapse.value = false;
  }
};

const appMainPaddingTop = computed(() => {
  if (appStore.fixedHeader) {
    return appStore.tagsView ? "108px" : "70px";
  }
  return "20px";
});

const resetInactivityTimer = () => {
  if (inactivityTimer) {
    window.clearTimeout(inactivityTimer);
  }
  if (!userStore.token) {
    return;
  }
  inactivityTimer = window.setTimeout(
    () => {
      userStore.logout();
      if (appStore.systemNotify) {
        ElMessage.warning("登录超时，请重新登录");
      }
      router.push("/login");
    },
    appStore.timeout * 60 * 1000,
  );
};

onMounted(() => {
  checkMobile();
  window.addEventListener("resize", checkMobile);
  ["mousemove", "keydown", "click", "scroll", "touchstart"].forEach((eventName) => {
    window.addEventListener(eventName, resetInactivityTimer);
  });
  resetInactivityTimer();
  // 页面加载时获取用户信息
  if (userStore.token && !userStore.avatar) {
    userStore.getUserInfo();
  }
  // 注册命令面板命令
  registerCommands();
});

onUnmounted(() => {
  window.removeEventListener("resize", checkMobile);
  ["mousemove", "keydown", "click", "scroll", "touchstart"].forEach((eventName) => {
    window.removeEventListener(eventName, resetInactivityTimer);
  });
  if (inactivityTimer) {
    window.clearTimeout(inactivityTimer);
  }
});

watch(
  () => appStore.timeout,
  () => {
    resetInactivityTimer();
  },
);

watch(
  () => userStore.token,
  () => {
    resetInactivityTimer();
  },
);
</script>

<style scoped>
.app-wrapper {
  position: relative;
  height: 100vh;
  width: 100%;
}
.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}
.sidebar-container {
  transition: width 0.28s;
  width: 210px !important;
  background-color: var(--el-menu-bg-color);
  height: 100%;
  position: fixed;
  font-size: 0px;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  border-right: 1px solid var(--el-border-color-light);
}
.hide-sidebar .sidebar-container {
  width: 64px !important;
}
.mobile.hide-sidebar .sidebar-container {
  pointer-events: none;
  transition: transform 0.28s;
  transform: translate3d(-210px, 0, 0);
}
.main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  margin-left: 210px;
  position: relative;
  background-color: var(--el-bg-color-page);
}
.hide-sidebar .main-container {
  margin-left: 64px;
}
.mobile .main-container {
  margin-left: 0;
}
.fixed-header {
  position: relative;
  right: 0;
  z-index: 9;
  width: calc(100% - 210px);
  transition: width 0.28s;
  background-color: var(--el-bg-color);
}
.fixed-header.is-fixed {
  position: fixed;
  top: 0;
}
.hide-sidebar .fixed-header {
  width: calc(100% - 64px);
}
.mobile .fixed-header {
  width: 100%;
}
.app-main {
  min-height: calc(100vh - 20px);
  width: 100%;
  position: relative;
  overflow: hidden;
  padding: 20px 20px 20px 20px;
  box-sizing: border-box;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .app-main {
    padding: 15px 10px;
  }

  .mobile .app-main {
    padding: 10px;
  }

  /* 优化卡片在手机端的间距 */
  :deep(.el-col) {
    margin-bottom: 10px;
  }

  :deep(.el-card) {
    border-radius: 8px;
  }

  :deep(.el-card__body) {
    padding: 15px;
  }
}
</style>
