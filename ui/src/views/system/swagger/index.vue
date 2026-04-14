<template>
  <div class="swagger-container">
    <iframe
      :src="swaggerUrl"
      class="swagger-iframe"
      frameborder="0"
    ></iframe>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const swaggerUrl = ref('http://localhost:8080/swagger-ui/index.html');
</script>

<style scoped>
.swagger-container {
  width: 100%;
  height: calc(100vh - 84px);
  padding: 0;
  overflow: hidden;
}

.swagger-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
