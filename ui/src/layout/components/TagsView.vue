<template>
  <div class="tags-view-container">
    <el-scrollbar>
      <div class="tags-list">
        <el-tag
          v-for="tag in visitedViews"
          :key="tag.path"
          :closable="tag.path !== '/dashboard'"
          :effect="isActive(tag.path) ? 'dark' : 'plain'"
          class="tag-item"
          @click="goTag(tag.path)"
          @close="closeTag(tag.path)"
        >
          {{ tag.title }}
        </el-tag>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

type VisitedTag = {
  title: string;
  path: string;
};

const route = useRoute();
const router = useRouter();

const visitedViews = ref<VisitedTag[]>([{ title: "首页", path: "/dashboard" }]);

const addTag = () => {
  const title = (route.meta?.title as string) || "未命名";
  const path = route.path;
  if (!visitedViews.value.some((item) => item.path === path)) {
    visitedViews.value.push({ title, path });
  }
};

const isActive = (path: string) => {
  return route.path === path;
};

const goTag = (path: string) => {
  if (route.path !== path) {
    router.push(path);
  }
};

const closeTag = (path: string) => {
  const index = visitedViews.value.findIndex((item) => item.path === path);
  if (index < 0) {
    return;
  }
  visitedViews.value.splice(index, 1);
  if (route.path === path) {
    const fallback =
      visitedViews.value[index - 1] || visitedViews.value[index] || visitedViews.value[0];
    router.push(fallback?.path || "/dashboard");
  }
};

watch(
  () => route.path,
  () => {
    addTag();
  },
  { immediate: true },
);
</script>

<style scoped>
.tags-view-container {
  height: 38px;
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-bg-color);
}
.tags-list {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  min-width: max-content;
}
.tag-item {
  cursor: pointer;
}
</style>
