<template>
  <div class="entropy-monitor">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">当前权限熵值</div>
          <div class="stat-value">{{ entropyData.permissionEntropy?.toFixed(4) || '-' }}</div>
          <div class="stat-desc">熵值越高，权限分布越分散</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">权限漂移评分</div>
          <div class="stat-value" :class="{ danger: entropyData.driftScore > 50 }">
            {{ entropyData.driftScore?.toFixed(2) || '-' }}
          </div>
          <div class="stat-desc">评分越高，漂移越严重</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">权限使用率</div>
          <div class="stat-value success">
            {{ getUsageRate() }}%
          </div>
          <div class="stat-desc">已使用/有效权限: {{ entropyData.usedPermissions || 0 }}/{{ entropyData.effectivePermissions || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <template #header>
        <span>熵值趋势</span>
      </template>
      <div ref="chartRef" class="chart-container"></div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>权限漂移报告</span>
          <el-button type="primary" @click="generateReport">生成回收建议</el-button>
        </div>
      </template>

      <el-table :data="driftList" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="driftScore" label="漂移评分" width="100" sortable>
          <template #default="{ row }">
            <el-tag :type="getDriftTag(row.driftScore)">
              {{ row.driftScore?.toFixed(2) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissionEntropy" label="权限熵值" width="120" />
        <el-table-column prop="effectivePermissions" label="有效权限" width="80" />
        <el-table-column prop="usedPermissions" label="已使用" width="80" />
        <el-table-column prop="unusedPermissions" label="未使用" width="80">
          <template #default="{ row }">
            <el-tag type="warning">{{ row.unusedPermissions }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recommendedRevocations" label="建议回收">
          <template #default="{ row }">
            <el-text truncated>{{ row.recommendedRevocations }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="periodStart" label="统计周期" width="200">
          <template #default="{ row }">
            {{ row.periodStart }} ~ {{ row.periodEnd }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="getDriftReport"
        @current-change="getDriftReport"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getEntropy, getEntropyHistory, getDriftReport as fetchDriftReportApi, recommendRevocation } from '@/api/permission/entropy';
import * as echarts from 'echarts';

const loading = ref(false);
const entropyData = ref<any>({});
const driftList = ref<any[]>([]);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  threshold: 30
});

const chartRef = ref<HTMLElement>();
let chartInstance: echarts.ECharts | null = null;

const getEntropyData = async () => {
  try {
    const res = await getEntropy(1);
    entropyData.value = res?.data || {};
  } catch (error) {
    console.error(error);
  }
};

const getDriftReport = async () => {
  loading.value = true;
  try {
    const res = await fetchDriftReportApi(queryParams);
    driftList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const generateReport = async () => {
  try {
    const res = await recommendRevocation(1);
    ElMessage.success(res?.data?.recommendation || '生成成功');
    getEntropyData();
    getDriftReport();
  } catch (error) {
    console.error(error);
  }
};

const getUsageRate = () => {
  if (!entropyData.value.effectivePermissions) return 0;
  return ((entropyData.value.usedPermissions / entropyData.value.effectivePermissions) * 100).toFixed(1);
};

const getDriftTag = (score: number) => {
  if (score >= 70) return 'danger';
  if (score >= 50) return 'warning';
  return 'success';
};

const initChart = async () => {
  if (!chartRef.value) return;

  chartInstance = echarts.init(chartRef.value);

  try {
    const res = await getEntropyHistory(1, 30);
    const data = res?.data || [];

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['熵值', '漂移评分']
      },
      xAxis: {
        type: 'category',
        data: data.map((item: any) => item.periodStart?.substring(0, 10))
      },
      yAxis: [
        {
          type: 'value',
          name: '熵值',
          position: 'left'
        },
        {
          type: 'value',
          name: '漂移评分',
          position: 'right'
        }
      ],
      series: [
        {
          name: '熵值',
          type: 'line',
          data: data.map((item: any) => item.permissionEntropy),
          smooth: true,
          itemStyle: { color: '#409eff' }
        },
        {
          name: '漂移评分',
          type: 'bar',
          yAxisIndex: 1,
          data: data.map((item: any) => item.driftScore),
          itemStyle: { color: '#67c23a' }
        }
      ]
    };

    chartInstance.setOption(option);
  } catch (error) {
    console.error(error);
  }
};

const handleResize = () => {
  chartInstance?.resize();
};

onMounted(() => {
  getEntropyData();
  getDriftReport();
  initChart();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  chartInstance?.dispose();
});
</script>

<style scoped>
.entropy-monitor {
  padding: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.success {
  color: #67c23a;
}

.stat-desc {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 10px;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
