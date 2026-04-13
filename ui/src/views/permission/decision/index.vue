<template>
  <div class="permission-decision">
    <el-card>
      <template #header>
        <span>权限决策</span>
      </template>

      <el-form :model="checkForm" label-width="100px" class="check-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="用户ID">
              <el-input v-model="checkForm.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资源类型">
              <el-select v-model="checkForm.resourceType" placeholder="请选择">
                <el-option label="项目" value="project" />
                <el-option label="客户" value="customer" />
                <el-option label="订单" value="order" />
                <el-option label="资产" value="asset" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资源ID">
              <el-input v-model="checkForm.resourceId" placeholder="请输入资源ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="操作类型">
              <el-select v-model="checkForm.operation" placeholder="请选择">
                <el-option label="查看" value="view" />
                <el-option label="新增" value="add" />
                <el-option label="编辑" value="edit" />
                <el-option label="删除" value="delete" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item>
              <el-button type="primary" @click="handleCheck" :loading="loading">权限检查</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card v-if="result" class="result-card">
      <template #header>
        <span>决策结果</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="请求ID">{{ result.requestId }}</el-descriptions-item>
        <el-descriptions-item label="决策">
          <el-tag :type="getDecisionType(result.decision)">
            {{ result.decision }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="决策延迟">{{ result.latencyMs }}ms</el-descriptions-item>
        <el-descriptions-item label="匹配规则">{{ result.matchedRules?.join(', ') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="说明" :span="2">{{ result.explanation || result.denialReason || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="context-card">
      <template #header>
        <span>上下文快照</span>
      </template>
      <el-tabs>
        <el-tab-pane label="组织维度">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="部门ID">{{ contextData.organizational?.deptId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="部门名称">{{ contextData.organizational?.deptName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="数据范围">{{ contextData.organizational?.dataScope || '-' }}</el-descriptions-item>
            <el-descriptions-item label="岗位数量">{{ contextData.organizational?.postIds?.length || 0 }}</el-descriptions-item>
            <el-descriptions-item label="是否负责人">
              <el-tag :type="contextData.organizational?.isDeptLeader ? 'success' : 'info'">
                {{ contextData.organizational?.isDeptLeader ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="时空维度">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="IP地址">{{ contextData.spatiotemporal?.ipAddress || '-' }}</el-descriptions-item>
            <el-descriptions-item label="设备类型">{{ contextData.spatiotemporal?.deviceType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="浏览器">{{ contextData.spatiotemporal?.browserType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ contextData.spatiotemporal?.osType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前小时">{{ contextData.spatiotemporal?.currentHour }}:00</el-descriptions-item>
            <el-descriptions-item label="是否工作时间">
              <el-tag :type="contextData.spatiotemporal?.isWorkingHours ? 'success' : 'warning'">
                {{ contextData.spatiotemporal?.isWorkingHours ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="风险维度">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="风险等级">
              <el-rate :model-value="contextData.risk?.riskLevel || 0" disabled :max="4" />
            </el-descriptions-item>
            <el-descriptions-item label="异常评分">
              <el-progress :percentage="contextData.risk?.anomalyScore || 0" :color="getProgressColor(contextData.risk?.anomalyScore)" />
            </el-descriptions-item>
            <el-descriptions-item label="信任评分">
              <el-progress :percentage="contextData.risk?.trustScore || 0" color="#67c23a" />
            </el-descriptions-item>
            <el-descriptions-item label="数据分级">{{ getDataClassName(contextData.risk?.dataClassification) }}</el-descriptions-item>
            <el-descriptions-item label="是否高风险">
              <el-tag :type="contextData.risk?.isHighRisk ? 'danger' : 'success'">
                {{ contextData.risk?.isHighRisk ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="行为维度">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="是否习惯时段">
              <el-tag :type="contextData.behavioral?.isTypicalAccessTime ? 'success' : 'warning'">
                {{ contextData.behavioral?.isTypicalAccessTime ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="是否常用设备">
              <el-tag :type="contextData.behavioral?.isTypicalDevice ? 'success' : 'warning'">
                {{ contextData.behavioral?.isTypicalDevice ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="权限使用率">
              <el-progress :percentage="(contextData.behavioral?.permissionUsageRate || 0) * 100" />
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { checkPermission } from '@/api/permission/decision';

const loading = ref(false);
const result = ref<any>(null);
const contextData = ref<any>({});

const checkForm = reactive({
  userId: 1,
  resourceType: 'project',
  resourceId: '',
  operation: 'view'
});

const handleCheck = async () => {
  loading.value = true;
  try {
    const res = await checkPermission(checkForm as any);
    result.value = res.data;

    // 模拟上下文数据
    contextData.value = {
      organizational: {
        deptId: 1,
        deptName: '技术部',
        dataScope: '3',
        postIds: [1, 2],
        isDeptLeader: false
      },
      spatiotemporal: {
        ipAddress: '192.168.1.100',
        deviceType: 'PC',
        browserType: 'Chrome',
        osType: 'Windows',
        currentHour: new Date().getHours(),
        isWorkingHours: new Date().getHours() >= 9 && new Date().getHours() <= 18
      },
      risk: {
        riskLevel: 0,
        anomalyScore: 0,
        trustScore: 100,
        dataClassification: 0,
        isHighRisk: false
      },
      behavioral: {
        isTypicalAccessTime: true,
        isTypicalDevice: true,
        permissionUsageRate: 0.5
      }
    };
  } catch (error) {
    ElMessage.error('权限检查失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  checkForm.userId = 1;
  checkForm.resourceType = 'project';
  checkForm.resourceId = '';
  checkForm.operation = 'view';
  result.value = null;
};

const getDecisionType = (decision: string) => {
  switch (decision) {
    case 'ALLOW': return 'success';
    case 'DENY': return 'danger';
    case 'CONDITIONAL': return 'warning';
    default: return 'info';
  }
};

const getProgressColor = (score: number) => {
  if (score > 70) return '#f56c6c';
  if (score > 50) return '#e6a23c';
  return '#67c23a';
};

const getDataClassName = (level?: number) => {
  const names = ['公开', '内部', '机密', '秘密', '绝密'];
  return level !== undefined ? names[level] : '-';
};
</script>

<style scoped>
.permission-decision {
  padding: 20px;
}

.check-form {
  margin-bottom: 20px;
}

.result-card {
  margin-top: 20px;
}

.context-card {
  margin-top: 20px;
}

.mr-2 {
  margin-right: 8px;
}
</style>
