<template>
  <div class="sidebar" :class="{ 'show-logo': appStore.showLogo }">
    <div v-if="appStore.showLogo" class="logo-container">
      <el-icon><Management /></el-icon>
      <span v-if="!isCollapse">Demo Admin</span>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="false"
        :collapse-transition="false"
        mode="vertical"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/system/role">
            <el-icon><Postcard /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item>
          <el-menu-item index="/system/notice">
            <el-icon><Bell /></el-icon>
            <template #title>通知公告</template>
          </el-menu-item>
          <el-menu-item index="/system/swagger">
            <el-icon><Document /></el-icon>
            <template #title>接口文档</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>系统监控</span>
          </template>
          <el-menu-item index="/monitor/server">
            <el-icon><Cpu /></el-icon>
            <template #title>服务监控</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon><Lock /></el-icon>
            <span>智能权限</span>
          </template>
          <el-menu-item index="/permission/decision">
            <el-icon><Checked /></el-icon>
            <template #title>权限决策</template>
          </el-menu-item>
          <el-menu-item index="/permission/rule">
            <el-icon><List /></el-icon>
            <template #title>动态规则</template>
          </el-menu-item>
          <el-menu-item index="/permission/behavior">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>行为分析</template>
          </el-menu-item>
          <el-menu-item index="/permission/entropy">
            <el-icon><TrendCharts /></el-icon>
            <template #title>熵值监控</template>
          </el-menu-item>
          <el-menu-item index="/permission/audit">
            <el-icon><Clock /></el-icon>
            <template #title>审计时光机</template>
          </el-menu-item>
          <el-menu-item index="/permission/team">
            <el-icon><UserFilled /></el-icon>
            <template #title>虚拟团队</template>
          </el-menu-item>
          <el-menu-item index="/permission/sandbox">
            <el-icon><Box /></el-icon>
            <template #title>沙盒模拟</template>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/ai/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI 对话</template>
        </el-menu-item>

        <el-menu-item index="/settings">
          <el-icon><Tools /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useAppStore } from "@/store/modules/app";
import {
  Management,
  House,
  Setting,
  User,
  Postcard,
  Bell,
  Monitor,
  Cpu,
  Lock,
  Checked,
  List,
  DataAnalysis,
  TrendCharts,
  Clock,
  UserFilled,
  ChatDotRound,
  Tools,
  Box,
  Document
} from '@element-plus/icons-vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
});

const appStore = useAppStore();
const route = useRoute();
const isCollapse = computed(() => props.isCollapse);

const activeMenu = computed(() => {
  const { path } = route;
  return path;
});
</script>

<style scoped>
.sidebar {
  width: 100%;
  height: 100%;
}
.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-bg-color);
  font-weight: 600;
}
.el-menu {
  border: none;
  height: 100%;
  width: 100% !important;
}
.scrollbar-wrapper {
  height: 100%;
}
.sidebar.show-logo .scrollbar-wrapper {
  height: calc(100% - 50px);
}
</style>
