<template>
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import { ElConfigProvider } from "element-plus";
</script>

<style>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family:
    "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑",
    Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局移动端优化 */
@media (max-width: 768px) {
  html {
    font-size: 14px;
  }

  /* 优化按钮在移动端的点击区域 */
  :deep(.el-button--small) {
    padding: 8px 15px;
  }

  /* 优化卡片阴影 */
  :deep(.el-card) {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }

  /* 优化表单输入框 */
  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    font-size: 16px; /* 防止iOS缩放 */
  }

  /* 优化弹窗在移动端的显示 */
  :deep(.el-dialog) {
    width: 90% !important;
    margin-top: 5vh !important;
  }

  /* 优化标签页 */
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }

  /* 优化表格 */
  :deep(.el-table) {
    font-size: 13px;
  }

  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 0;
  }
}

/* 触摸优化 */
* {
  -webkit-tap-highlight-color: transparent;
}

/* 防止iOS橡皮筋效果 */
body {
  overscroll-behavior: none;
}

/* 优化滚动 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

@media (max-width: 768px) {
  ::-webkit-scrollbar {
    width: 4px;
  }
}
</style>
