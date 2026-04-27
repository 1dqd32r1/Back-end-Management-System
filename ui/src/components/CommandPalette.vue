<template>
  <Teleport to="body">
    <Transition name="fade">
      <div
        v-if="visible"
        class="command-palette-overlay"
        @click.self="close"
      >
        <div class="command-palette">
          <!-- 搜索框 -->
          <div class="command-palette-header">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              ref="inputRef"
              v-model="query"
              type="text"
              class="command-input"
              :placeholder="placeholder"
              @keydown="handleKeydown"
            />
            <span class="shortcut-hint">ESC 关闭</span>
          </div>

          <!-- 命令列表 -->
          <div class="command-palette-body">
            <!-- 最近使用 -->
            <div v-if="!query && recentCommands.length > 0" class="command-section">
              <div class="section-title">最近使用</div>
              <div
                v-for="(cmd, idx) in recentCommands"
                :key="'recent-' + cmd.id"
                class="command-item"
                :class="{ active: activeIndex === idx }"
                @click="executeCommand(cmd)"
                @mouseenter="activeIndex = idx"
              >
                <el-icon v-if="cmd.icon"><component :is="cmd.icon" /></el-icon>
                <span class="command-label">{{ cmd.label }}</span>
                <span v-if="cmd.shortcut" class="command-shortcut">{{ cmd.shortcut }}</span>
              </div>
            </div>

            <!-- 搜索结果 -->
            <div v-if="query" class="command-section">
              <div v-if="filteredCommands.length === 0" class="no-results">
                未找到匹配的命令
              </div>
              <div
                v-for="(cmd, idx) in filteredCommands"
                :key="cmd.id"
                class="command-item"
                :class="{ active: activeIndex === idx }"
                @click="executeCommand(cmd)"
                @mouseenter="activeIndex = idx"
              >
                <el-icon v-if="cmd.icon"><component :is="cmd.icon" /></el-icon>
                <span class="command-label">{{ cmd.label }}</span>
                <span class="command-category">{{ getCategoryLabel(cmd.category) }}</span>
                <span v-if="cmd.shortcut" class="command-shortcut">{{ cmd.shortcut }}</span>
              </div>
            </div>

            <!-- 所有命令（按分类） -->
            <div v-if="!query && recentCommands.length === 0" class="command-section">
              <template v-for="category in categories" :key="category">
                <div v-if="getCommandsByCategory(category).length > 0" class="category-group">
                  <div class="section-title">{{ getCategoryLabel(category) }}</div>
                  <div
                    v-for="(cmd, idx) in getCommandsByCategory(category)"
                    :key="cmd.id"
                    class="command-item"
                    :class="{ active: activeIndex === getCategoryOffset(category) + idx }"
                    @click="executeCommand(cmd)"
                    @mouseenter="activeIndex = getCategoryOffset(category) + idx"
                  >
                    <el-icon v-if="cmd.icon"><component :is="cmd.icon" /></el-icon>
                    <span class="command-label">{{ cmd.label }}</span>
                    <span v-if="cmd.shortcut" class="command-shortcut">{{ cmd.shortcut }}</span>
                  </div>
                </div>
              </template>
            </div>
          </div>

          <!-- 底部提示 -->
          <div class="command-palette-footer">
            <span><kbd>↑</kbd><kbd>↓</kbd> 导航</span>
            <span><kbd>Enter</kbd> 执行</span>
            <span><kbd>Esc</kbd> 关闭</span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { Search, House, User, Setting, Monitor, Document, Bell, Key, Operation, DataLine, TrendCharts, Clock, UserFilled, ChatDotRound, OfficeBuilding, Postcard } from '@element-plus/icons-vue';
import { useCommandRegistry, type Command } from '@/composables/useCommandRegistry';

const props = withDefaults(defineProps<{
  visible: boolean;
  placeholder?: string;
}>(), {
  placeholder: '搜索命令或输入快捷词...'
});

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'command', command: Command): void;
}>();

const {
  search,
  getRecentCommands,
  addRecentCommand,
  getAllCommands
} = useCommandRegistry();

const inputRef = ref<HTMLInputElement>();
const query = ref('');
const activeIndex = ref(0);

const categories = ['navigation', 'action', 'setting', 'api'] as const;

const recentCommands = computed(() => getRecentCommands());

const filteredCommands = computed(() => search(query.value));

const getCategoryLabel = (category: string): string => {
  const labels: Record<string, string> = {
    navigation: '导航',
    action: '操作',
    setting: '设置',
    api: '接口'
  };
  return labels[category] || category;
};

const getCommandsByCategory = (category: string): Command[] => {
  return getAllCommands().filter(cmd => cmd.category === category);
};

const getCategoryOffset = (category: string): number => {
  let offset = 0;
  for (const cat of categories) {
    if (cat === category) break;
    offset += getCommandsByCategory(cat).length;
  }
  return offset;
};

const close = () => {
  emit('update:visible', false);
  query.value = '';
  activeIndex.value = 0;
};

const executeCommand = (command: Command) => {
  addRecentCommand(command.id);
  emit('command', command);
  command.action();
  close();
};

const handleKeydown = (e: KeyboardEvent) => {
  const commands = query.value ? filteredCommands.value : getAllCommands();

  switch (e.key) {
    case 'ArrowDown':
      e.preventDefault();
      activeIndex.value = (activeIndex.value + 1) % commands.length;
      break;
    case 'ArrowUp':
      e.preventDefault();
      activeIndex.value = activeIndex.value === 0 ? commands.length - 1 : activeIndex.value - 1;
      break;
    case 'Enter':
      e.preventDefault();
      if (commands[activeIndex.value]) {
        executeCommand(commands[activeIndex.value]);
      }
      break;
    case 'Escape':
      e.preventDefault();
      close();
      break;
  }
};

// 监听 visible 变化，自动聚焦
watch(() => props.visible, async (newVal) => {
  if (newVal) {
    await nextTick();
    inputRef.value?.focus();
    activeIndex.value = 0;
  }
});

// 全局快捷键
const handleGlobalKeydown = (e: KeyboardEvent) => {
  // Ctrl+K 或 Cmd+K
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault();
    emit('update:visible', !props.visible);
  }
};

onMounted(() => {
  window.addEventListener('keydown', handleGlobalKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleGlobalKeydown);
});
</script>

<style scoped>
.command-palette-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 15vh;
  z-index: 9999;
}

.command-palette {
  width: 560px;
  max-height: 70vh;
  background: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.command-palette-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  gap: 12px;
}

.search-icon {
  font-size: 20px;
  color: var(--el-text-color-placeholder);
}

.command-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  background: transparent;
  color: var(--el-text-color-primary);
}

.command-input::placeholder {
  color: var(--el-text-color-placeholder);
}

.shortcut-hint {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  background: var(--el-fill-color-light);
  padding: 2px 8px;
  border-radius: 4px;
}

.command-palette-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.command-section {
  margin-bottom: 8px;
}

.section-title {
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  color: var(--el-text-color-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.command-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  gap: 12px;
  transition: background-color 0.15s;
}

.command-item:hover,
.command-item.active {
  background: var(--el-fill-color);
}

.command-item.active {
  background: var(--el-color-primary-light-9);
}

.command-item .el-icon {
  font-size: 18px;
  color: var(--el-text-color-secondary);
}

.command-label {
  flex: 1;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.command-category {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  background: var(--el-fill-color-light);
  padding: 2px 8px;
  border-radius: 4px;
}

.command-shortcut {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.no-results {
  padding: 24px;
  text-align: center;
  color: var(--el-text-color-placeholder);
}

.command-palette-footer {
  display: flex;
  gap: 16px;
  padding: 12px 20px;
  border-top: 1px solid var(--el-border-color-light);
  background: var(--el-fill-color-lighter);
}

.command-palette-footer span {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.command-palette-footer kbd {
  background: var(--el-bg-color);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: inherit;
  font-size: 11px;
  border: 1px solid var(--el-border-color);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-active .command-palette,
.fade-leave-active .command-palette {
  transition: transform 0.15s ease, opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-from .command-palette,
.fade-leave-to .command-palette {
  transform: scale(0.95);
  opacity: 0;
}

/* 暗色主题 */
:root.dark .command-palette {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}
</style>
