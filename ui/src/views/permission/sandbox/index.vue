<template>
  <div class="sandbox-simulator">
    <el-row :gutter="20">
      <!-- What-If 模拟 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>What-If 模拟</span>
          </template>
          <el-form :model="whatIfForm" label-width="100px">
            <el-form-item label="用户ID">
              <el-input v-model="whatIfForm.userId" placeholder="输入用户ID" />
            </el-form-item>
            <el-form-item label="变更类型">
              <el-select v-model="whatIfForm.changeType" placeholder="请选择">
                <el-option label="添加角色" value="addRole" />
                <el-option label="移除角色" value="removeRole" />
                <el-option label="添加权限" value="addPermission" />
                <el-option label="移除权限" value="removePermission" />
              </el-select>
            </el-form-item>
            <el-form-item label="目标值">
              <el-input v-model="whatIfForm.targetValue" placeholder="角色名或权限标识" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="executeWhatIf" :loading="whatIfLoading">执行模拟</el-button>
            </el-form-item>
          </el-form>

          <el-divider v-if="whatIfResult" />

          <div v-if="whatIfResult" class="result-section">
            <el-alert :title="whatIfResult.message" :type="whatIfResult.success ? 'success' : 'error'" show-icon />
            <div class="mt-3">
              <strong>受影响用户:</strong> {{ whatIfResult.affectedUsers?.length || 0 }} 人
            </div>
            <div class="mt-2">
              <strong>潜在冲突:</strong> {{ whatIfResult.conflictPredictions?.length || 0 }} 个
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 压力测试 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>压力测试</span>
          </template>
          <el-form :model="stressForm" label-width="120px">
            <el-form-item label="并发用户数">
              <el-input-number v-model="stressForm.concurrentUsers" :min="1" :max="10000" />
            </el-form-item>
            <el-form-item label="请求/秒">
              <el-input-number v-model="stressForm.requestsPerSecond" :min="1" :max="10000" />
            </el-form-item>
            <el-form-item label="持续时间(秒)">
              <el-input-number v-model="stressForm.durationSeconds" :min="1" :max="3600" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="executeStress" :loading="stressLoading">执行测试</el-button>
            </el-form-item>
          </el-form>

          <el-divider v-if="stressResult" />

          <div v-if="stressResult" class="result-section">
            <el-alert :title="stressResult.message" :type="stressResult.success ? 'success' : 'error'" show-icon />
            <el-row :gutter="20" class="mt-3">
              <el-col :span="12">
                <el-statistic title="总请求数" :value="stressResult.impactHeatmap?.totalRequests || 0" />
              </el-col>
              <el-col :span="12">
                <el-statistic title="成功率" :value="stressResult.impactHeatmap?.successRate || 0" suffix="%" />
              </el-col>
            </el-row>
            <el-row :gutter="20" class="mt-3">
              <el-col :span="12">
                <el-statistic title="平均延迟" :value="stressResult.impactHeatmap?.avgLatencyMs || 0" suffix="ms" />
              </el-col>
              <el-col :span="12">
                <el-statistic title="P99延迟" :value="stressResult.impactHeatmap?.p99LatencyMs || 0" suffix="ms" />
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 模拟历史 -->
    <el-card class="history-card">
      <template #header>
        <span>模拟历史</span>
      </template>
      <el-table :data="historyList" stripe>
        <el-table-column prop="simulationName" label="模拟名称" min-width="150" />
        <el-table-column prop="simulationType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.simulationType)">{{ row.simulationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executionTime" label="执行时间" width="180" />
        <el-table-column prop="executionDurationMs" label="耗时" width="100">
          <template #default="{ row }">{{ row.executionDurationMs }}ms</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewHeatmap(row)">查看热力图</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 热力图对话框 -->
    <el-dialog v-model="heatmapVisible" title="影响面热力图" width="800px">
      <div v-if="heatmapData" class="heatmap-container">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>权限分布</template>
              <div v-for="(count, perm) in heatmapData.permissionDistribution" :key="perm" class="heatmap-item">
                <span>{{ perm }}</span>
                <el-progress :percentage="count * 10" :stroke-width="15" />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>用户影响度</template>
              <div v-for="(level, user) in heatmapData.userImpactLevel" :key="user" class="heatmap-item">
                <span>{{ user }}</span>
                <el-tag :type="getImpactTag(level)">{{ level }}级</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { executeWhatIf as execWhatIf, executeStressTest, getSimulationHistory, getHeatmap } from '@/api/permission/sandbox';

const whatIfForm = reactive({
  userId: 1,
  changeType: 'addRole',
  targetValue: ''
});
const whatIfLoading = ref(false);
const whatIfResult = ref<any>(null);

const stressForm = reactive({
  concurrentUsers: 100,
  requestsPerSecond: 1000,
  durationSeconds: 60
});
const stressLoading = ref(false);
const stressResult = ref<any>(null);

const historyList = ref<any[]>([]);
const heatmapVisible = ref(false);
const heatmapData = ref<any>(null);

const executeWhatIf = async () => {
  if (!whatIfForm.targetValue) {
    ElMessage.warning('请输入目标值');
    return;
  }
  whatIfLoading.value = true;
  try {
    const changeData = { [whatIfForm.changeType]: whatIfForm.targetValue };
    const res = await execWhatIf({
      userId: whatIfForm.userId,
      changeType: whatIfForm.changeType,
      changeData,
      simulationName: 'What-If模拟'
    });
    whatIfResult.value = res.data;
    loadHistory();
  } catch (error) {
    console.error(error);
  } finally {
    whatIfLoading.value = false;
  }
};

const executeStress = async () => {
  stressLoading.value = true;
  try {
    const res = await executeStressTest(stressForm);
    stressResult.value = res.data;
    loadHistory();
  } catch (error) {
    console.error(error);
  } finally {
    stressLoading.value = false;
  }
};

const loadHistory = async () => {
  try {
    const res = await getSimulationHistory({ limit: 10 });
    historyList.value = res.data || [];
  } catch (error) {
    console.error(error);
  }
};

const viewHeatmap = async (row: any) => {
  try {
    const res = await getHeatmap(row.simulationId);
    heatmapData.value = res.data;
    heatmapVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const getTypeTag = (type: string) => {
  const map: Record<string, string> = {
    WHAT_IF: 'primary',
    TIME_TRAVEL: 'success',
    STRESS: 'warning'
  };
  return map[type] || 'info';
};

const getImpactTag = (level: number) => {
  if (level >= 3) return 'danger';
  if (level >= 2) return 'warning';
  return 'success';
};

onMounted(() => {
  loadHistory();
});
</script>

<style scoped>
.sandbox-simulator {
  padding: 20px;
}

.history-card {
  margin-top: 20px;
}

.result-section {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.mt-2 {
  margin-top: 8px;
}

.mt-3 {
  margin-top: 12px;
}

.heatmap-container {
  min-height: 300px;
}

.heatmap-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
</style>
