<template>
  <div class="audit-time-machine">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>权限快照详情</span>
              <el-button type="primary" size="small" @click="createSnapshot">创建快照</el-button>
            </div>
          </template>

          <el-slider v-model="timeSlider" :max="Math.max(0, timelineData.length - 1)" :format-tooltip="formatTooltip" @change="handleTimeChange" />

          <el-descriptions v-if="currentSnapshot" :column="3" border class="snapshot-detail">
            <el-descriptions-item label="快照时间" :span="3">{{ currentSnapshot.snapshotTime }}</el-descriptions-item>
            <el-descriptions-item label="变更类型">{{ currentSnapshot.changeType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="变更原因" :span="2">{{ currentSnapshot.changeReason || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色列表" :span="3">
              <el-tag v-for="role in parseJson(currentSnapshot?.roles)" :key="role" class="mr-2">{{ role }}</el-tag>
              <span v-if="!parseJson(currentSnapshot?.roles).length">-</span>
            </el-descriptions-item>
            <el-descriptions-item label="权限列表" :span="3">
              <el-tag v-for="perm in parseJson(currentSnapshot?.permissions).slice(0, 10)" :key="perm" type="info" class="mr-2">{{ perm }}</el-tag>
              <span v-if="parseJson(currentSnapshot?.permissions).length > 10">...等{{ parseJson(currentSnapshot?.permissions).length }}项</span>
            </el-descriptions-item>
            <el-descriptions-item label="数据范围">{{ currentSnapshot?.dataScope || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>变更时间线</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="event in timelineData"
              :key="event.snapshotId"
              :timestamp="event.snapshotTime"
              placement="top"
            >
              <el-card shadow="hover" @click="selectSnapshot(event)" class="timeline-card">
                <div class="timeline-event">
                  <el-tag size="small" :type="getChangeTag(event.changeType)">{{ event.changeType }}</el-tag>
                  <span class="ml-2">{{ event.changeReason || '无描述' }}</span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 权限对比 -->
    <el-card class="compare-card">
      <template #header>
        <span>权限状态对比</span>
      </template>
      <el-form :inline="true">
        <el-form-item label="时间点1">
          <el-date-picker
              v-model="compareParams.timestamp1"
              type="datetime"
              placeholder="选择时间点"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="时间点2">
          <el-date-picker
              v-model="compareParams.timestamp2"
              type="datetime"
              placeholder="选择时间点"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCompare">开始对比</el-button>
        </el-form-item>
      </el-form>

      <el-row v-if="compareResult" :gutter="20">
        <el-col :span="12">
          <el-card shadow="never">
            <template #header>新增权限</template>
            <el-tag v-for="perm in compareResult.diff?.addedPermissions" :key="perm" type="success" class="mr-2 mb-2">{{ perm }}</el-tag>
            <el-empty v-if="!compareResult.diff?.addedPermissions?.length" description="无新增" :image-size="60" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <template #header>移除权限</template>
            <el-tag v-for="perm in compareResult.diff?.removedPermissions" :key="perm" type="danger" class="mr-2 mb-2">{{ perm }}</el-tag>
            <el-empty v-if="!compareResult.diff?.removedPermissions?.length" description="无移除" :image-size="60" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- What-If 模拟 -->
    <el-card class="whatif-card">
      <template #header>
        <span>What-If 模拟</span>
      </template>
      <el-form :model="whatIfForm" :inline="true">
        <el-form-item label="变更类型">
          <el-select v-model="whatIfForm.changeType" placeholder="请选择">
            <el-option label="添加角色" value="addRole" />
            <el-option label="移除角色" value="removeRole" />
            <el-option label="添加权限" value="addPermission" />
            <el-option label="移除权限" value="removePermission" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标值">
          <el-input v-model="whatIfForm.targetValue" placeholder="角色名或权限标识" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="executeWhatIf">执行模拟</el-button>
        </el-form-item>
      </el-form>

      <el-alert v-if="whatIfResult" :title="whatIfResult.description" :type="whatIfResult.impactLevel === 'HIGH' ? 'warning' : 'success'" show-icon>
        <template #default>
          <div>影响用户数: {{ whatIfResult.affectedUsers?.length || 0 }}</div>
        </template>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getSnapshots, getTimeline, compare, whatIf, createSnapshot as createSnapshotApi } from '@/api/permission/audit';

const timelineData = ref<any[]>([]);
const currentSnapshot = ref<any>(null);
const timeSlider = ref(0);

const compareParams = reactive({
  timestamp1: '',
  timestamp2: ''
});
const compareResult = ref<any>(null);

const whatIfForm = reactive({
  changeType: 'addRole',
  targetValue: ''
});
const whatIfResult = ref<any>(null);

const getTimelineData = async () => {
  try {
    const res = await getTimeline(1);
    timelineData.value = res.data || [];
    if (timelineData.value.length > 0) {
      currentSnapshot.value = timelineData.value[0];
    }
  } catch (error) {
    console.error(error);
  }
};

const handleTimeChange = (value: number) => {
  currentSnapshot.value = timelineData.value[value];
};

const formatTooltip = (value: number) => {
  const item = timelineData.value[value];
  return item?.snapshotTime || '';
};

const selectSnapshot = (event: any) => {
  currentSnapshot.value = event;
  timeSlider.value = timelineData.value.findIndex(e => e.snapshotId === event.snapshotId);
};

const handleCompare = async () => {
  if (!compareParams.timestamp1 || !compareParams.timestamp2) {
    ElMessage.warning('请选择两个时间点');
    return;
  }
  try {
    const res = await compare({
      userId: 1,
      timestamp1: compareParams.timestamp1,
      timestamp2: compareParams.timestamp2
    });
    compareResult.value = res.data;
  } catch (error) {
    console.error(error);
  }
};

const executeWhatIf = async () => {
  if (!whatIfForm.targetValue) {
    ElMessage.warning('请输入目标值');
    return;
  }
  try {
    const res = await whatIf({
      userId: 1,
      changeType: whatIfForm.changeType,
      changeData: { [whatIfForm.changeType]: whatIfForm.targetValue }
    });
    whatIfResult.value = res.data;
  } catch (error) {
    console.error(error);
  }
};

const createSnapshot = async () => {
  try {
    await createSnapshotApi({
      userId: 1,
      changeType: 'MANUAL',
      changeReason: '手动创建快照'
    });
    ElMessage.success('快照创建成功');
    getTimelineData();
  } catch (error) {
    console.error(error);
  }
};

const parseJson = (jsonStr: string) => {
  try {
    return JSON.parse(jsonStr || '[]');
  } catch {
    return [];
  }
};

const getChangeTag = (type: string) => {
  const map: Record<string, string> = {
    GRANT: 'success',
    REVOKE: 'danger',
    MODIFY: 'warning',
    MANUAL: 'info'
  };
  return map[type] || 'info';
};

onMounted(() => {
  getTimelineData();
});
</script>

<style scoped>
.audit-time-machine {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.snapshot-detail {
  margin-top: 20px;
}

.timeline-card {
  cursor: pointer;
}

.timeline-event {
  display: flex;
  align-items: center;
}

.mr-2 {
  margin-right: 8px;
}

.ml-2 {
  margin-left: 8px;
}

.mb-2 {
  margin-bottom: 8px;
}

.compare-card, .whatif-card {
  margin-top: 20px;
}
</style>
