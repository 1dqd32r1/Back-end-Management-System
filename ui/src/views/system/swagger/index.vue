<template>
  <div class="swagger-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>接口文档</span>
          <el-button type="primary" @click="openSwagger">
            <el-icon><Link /></el-icon>
            新窗口打开 Swagger UI
          </el-button>
        </div>
      </template>
      <div class="swagger-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="Swagger UI">
            <el-link :href="swaggerUrl" target="_blank" type="primary">{{ swaggerUrl }}</el-link>
          </el-descriptions-item>
          <el-descriptions-item label="API Docs">
            <el-link :href="apiDocsUrl" target="_blank" type="primary">{{ apiDocsUrl }}</el-link>
          </el-descriptions-item>
          <el-descriptions-item label="说明">
            点击上方按钮或链接在新窗口中打开接口文档
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Link } from '@element-plus/icons-vue';

const swaggerUrl = ref('http://localhost:8080/swagger-ui/index.html');
const apiDocsUrl = ref('http://localhost:8080/v3/api-docs');

const openSwagger = () => {
  window.open(swaggerUrl.value, '_blank');
};
</script>

<style scoped>
.swagger-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.swagger-info {
  padding: 20px 0;
}
</style>
