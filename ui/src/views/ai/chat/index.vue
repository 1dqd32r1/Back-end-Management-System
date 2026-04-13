<template>
  <div class="chat-wrapper">
    <el-card class="chat-card">
      <template #header>
        <div class="chat-header">
          <div class="header-left">
            <el-avatar :size="32" icon="Service" />
            <span class="header-title">AI 智能助手 (Powered by DeepSeek)</span>
          </div>
          <el-button link type="primary" @click="clearChat">清空对话</el-button>
        </div>
      </template>

      <div class="message-container" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="index" :class="['message-item', msg.role]">
          <el-avatar
            :size="36"
            :icon="msg.role === 'user' ? 'UserFilled' : 'Service'"
            class="avatar"
          />
          <div class="message-content">
            <div class="message-bubble">
              <span v-if="msg.loading" class="typing-dots">
                <span>.</span><span>.</span><span>.</span>
              </span>
              <span v-else>{{ msg.content }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="userInput"
          type="textarea"
          :rows="3"
          placeholder="输入您的问题，按 Enter 发送..."
          @keyup.enter.prevent="handleSend"
          :disabled="isTyping"
        />
        <div class="input-footer">
          <span class="tip">Shift + Enter 换行</span>
          <el-button type="primary" :loading="isTyping" @click="handleSend">发送</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from "vue";

interface Message {
  role: "user" | "assistant";
  content: string;
  loading?: boolean;
}

const messages = ref<Message[]>([
  {
    role: "assistant",
    content:
      "您好！我是您的 AI 智能助手，基于最新的大模型技术。您可以问我关于系统操作、代码编写或日常知识的任何问题。",
  },
]);

const userInput = ref("");
const isTyping = ref(false);
const messageContainer = ref<HTMLElement | null>(null);

const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
};

const handleSend = async () => {
  if (!userInput.value.trim() || isTyping.value) return;

  const question = userInput.value.trim();
  messages.value.push({ role: "user", content: question });
  userInput.value = "";
  scrollToBottom();

  isTyping.value = true;

  // 模拟 AI 思考状态
  const assistantMsg: Message = { role: "assistant", content: "", loading: true };
  messages.value.push(assistantMsg);
  scrollToBottom();

  setTimeout(() => {
    assistantMsg.loading = false;
    simulateStreamingResponse(
      assistantMsg,
      `我已收到您的问题：“${question}”。
这是一条模拟的流式回复。作为一个集成在后台系统中的 AI 助手，我可以帮您：
1. 分析系统运行状态和监控数据；
2. 快速查询和导出用户报表；
3. 提供代码优化建议和技术支持。
目前系统运行正常，CPU 负载 12.5%，内存充足。`,
    );
  }, 1000);
};

const simulateStreamingResponse = (msg: Message, fullText: string) => {
  let index = 0;
  const timer = setInterval(() => {
    if (index < fullText.length) {
      msg.content += fullText[index];
      index++;
      scrollToBottom();
    } else {
      clearInterval(timer);
      isTyping.value = false;
    }
  }, 30);
};

const clearChat = () => {
  messages.value = [{ role: "assistant", content: "对话已清空。有什么我可以帮您的吗？" }];
};

onMounted(scrollToBottom);
</script>

<style scoped>
.chat-wrapper {
  padding: 20px;
  height: calc(100vh - 120px);
}
.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}
:deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0;
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-title {
  font-weight: bold;
  font-size: 16px;
}
.message-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9fafc;
}
.message-item {
  display: flex;
  margin-bottom: 24px;
  gap: 12px;
}
.message-item.user {
  flex-direction: row-reverse;
}
.message-content {
  max-width: 70%;
}
.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
.assistant .message-bubble {
  background-color: #ffffff;
  color: #303133;
  border-top-left-radius: 0;
}
.user .message-bubble {
  background-color: #409eff;
  color: #ffffff;
  border-top-right-radius: 0;
}
.input-area {
  padding: 20px;
  border-top: 1px solid #ebeef5;
  background-color: #fff;
}
.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.tip {
  font-size: 12px;
  color: #909399;
}
.typing-dots span {
  animation: blink 1s infinite;
}
.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}
.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes blink {
  0% {
    opacity: 0.2;
  }
  20% {
    opacity: 1;
  }
  100% {
    opacity: 0.2;
  }
}
</style>
