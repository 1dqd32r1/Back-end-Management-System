<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span
            ><el-icon><Setting /></el-icon> 系统设置</span
          >
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="外观设置" name="appearance">
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="系统主题">
              <el-radio-group v-model="currentTheme" @change="handleThemeChange">
                <el-radio-button value="light">
                  <el-icon><Sunny /></el-icon> 浅色
                </el-radio-button>
                <el-radio-button value="dark">
                  <el-icon><Moon /></el-icon> 深色
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="侧边栏Logo">
              <el-switch v-model="settings.showLogo" />
            </el-form-item>

            <el-form-item label="开启页签">
              <el-switch v-model="settings.tagsView" />
            </el-form-item>

            <el-form-item label="固定Header">
              <el-switch v-model="settings.fixedHeader" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="通知设置" name="notifications">
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="邮件通知">
              <el-switch v-model="settings.emailNotify" />
            </el-form-item>
            <el-form-item label="系统消息">
              <el-switch v-model="settings.systemNotify" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="安全设置" name="security">
          <el-form label-width="120px" class="settings-form">
            <el-form-item label="双重认证">
              <el-switch v-model="settings.twoFactor" />
            </el-form-item>
            <el-form-item label="登录超时 (分钟)">
              <el-input-number v-model="settings.timeout" :min="1" :max="1440" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="footer-actions">
        <el-button type="primary" @click="saveSettings">保存配置</el-button>
        <el-button @click="resetSettings">重置默认</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { useAppStore } from "@/store/modules/app";
import { ElMessage } from "element-plus";

const appStore = useAppStore();
const activeTab = ref("appearance");
const currentTheme = ref(appStore.theme);

const settings = reactive({
  showLogo: appStore.showLogo,
  tagsView: appStore.tagsView,
  fixedHeader: appStore.fixedHeader,
  emailNotify: appStore.emailNotify,
  systemNotify: appStore.systemNotify,
  twoFactor: appStore.twoFactor,
  timeout: appStore.timeout,
});

watch(
  () => appStore.theme,
  (val) => {
    currentTheme.value = val;
  },
);

watch(
  () => appStore.showLogo,
  (val) => {
    settings.showLogo = val;
  },
);
watch(
  () => appStore.tagsView,
  (val) => {
    settings.tagsView = val;
  },
);
watch(
  () => appStore.fixedHeader,
  (val) => {
    settings.fixedHeader = val;
  },
);
watch(
  () => appStore.emailNotify,
  (val) => {
    settings.emailNotify = val;
  },
);
watch(
  () => appStore.systemNotify,
  (val) => {
    settings.systemNotify = val;
  },
);
watch(
  () => appStore.twoFactor,
  (val) => {
    settings.twoFactor = val;
  },
);
watch(
  () => appStore.timeout,
  (val) => {
    settings.timeout = val;
  },
);

const handleThemeChange = (val: string | number | boolean | undefined) => {
  const nextTheme = String(val ?? "");
  if ((nextTheme === "light" || nextTheme === "dark") && nextTheme !== appStore.theme) {
    appStore.setTheme(nextTheme as "light" | "dark");
  }
};

const saveSettings = () => {
  appStore.updateSettings({
    showLogo: settings.showLogo,
    tagsView: settings.tagsView,
    fixedHeader: settings.fixedHeader,
    emailNotify: settings.emailNotify,
    systemNotify: settings.systemNotify,
    twoFactor: settings.twoFactor,
    timeout: settings.timeout,
  });
  ElMessage.success("系统设置保存成功");
};

const resetSettings = () => {
  appStore.resetSettings();
  ElMessage.info("已重置为默认配置");
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.settings-form {
  max-width: 600px;
  margin-top: 20px;
}
.footer-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: center;
  gap: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}
:deep(.el-tabs__header) {
  margin: 0 0 8px;
}
:deep(.el-tabs__nav-wrap::after) {
  background-color: var(--el-border-color-light);
}
:deep(.el-tabs__item) {
  color: var(--el-text-color-regular);
  font-weight: 500;
}
:deep(.el-tabs__item.is-active) {
  color: var(--el-color-primary);
}
:deep(.el-tabs__active-bar) {
  background-color: var(--el-color-primary);
}
</style>
