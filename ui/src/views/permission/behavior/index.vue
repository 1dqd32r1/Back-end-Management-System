<template>
  <div class="behavior-analysis">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalEvents || 0 }}</div>
          <div class="stat-label">总事件数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value danger">{{ stats.anomalyEvents || 0 }}</div>
          <div class="stat-label">异常事件</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.anomalyRate || '0%' }}</div>
          <div class="stat-label">异常率</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value success">{{ stats.todayEvents || 0 }}</div>
          <div class="stat-label">今日事件</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>事件类型分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>事件趋势（近7天）</span>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>行为事件列表</span>
          <el-form :inline="true">
            <el-form-item>
              <el-input v-model="queryParams.userId" placeholder="用户ID" clearable style="width: 120px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.eventType" placeholder="事件类型" clearable style="width: 150px">
                <el-option label="登录" value="LOGIN" />
                <el-option label="登出" value="LOGOUT" />
                <el-option label="访问" value="ACCESS" />
                <el-option label="修改" value="MODIFY" />
                <el-option label="导出" value="EXPORT" />
                <el-option label="删除" value="DELETE" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table :data="eventList" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="eventType" label="事件类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getEventTypeTag(row.eventType)">{{ row.eventType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceType" label="资源类型" width="100" />
        <el-table-column prop="operation" label="操作" width="80" />
        <el-table-column prop="ipAddress" label="IP地址" width="130" />
        <el-table-column prop="isAnomaly" label="是否异常" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAnomaly ? 'danger' : 'success'">
              {{ row.isAnomaly ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="eventTime" label="事件时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="事件详情" width="600px">
      <el-descriptions :column="2" border v-if="currentEvent">
        <el-descriptions-item label="事件ID">{{ currentEvent.eventId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentEvent.userId }}</el-descriptions-item>
        <el-descriptions-item label="事件类型">{{ currentEvent.eventType }}</el-descriptions-item>
        <el-descriptions-item label="事件来源">{{ currentEvent.eventSource }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ currentEvent.resourceType }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentEvent.operation }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentEvent.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="地理位置">{{ currentEvent.geoLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="是否异常">
          <el-tag :type="currentEvent.isAnomaly ? 'danger' : 'success'">
            {{ currentEvent.isAnomaly ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="事件时间">{{ currentEvent.eventTime }}</el-descriptions-item>
        <el-descriptions-item label="User-Agent" :span="2">{{ currentEvent.userAgent }}</el-descriptions-item>
        <el-descriptions-item label="事件数据" :span="2">
          <pre class="event-data">{{ formatJson(currentEvent.eventData) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { getBehaviorEvents, getBehaviorStats } from '@/api/permission/behavior';
import * as echarts from 'echarts';

const loading = ref(false);
const eventList = ref<any[]>([]);
const total = ref(0);
const stats = ref<any>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  userId: '',
  eventType: ''
});

const detailVisible = ref(false);
const currentEvent = ref<any>(null);

const pieChartRef = ref<HTMLElement>();
const lineChartRef = ref<HTMLElement>();
let pieChart: echarts.ECharts | null = null;
let lineChart: echarts.ECharts | null = null;

const getList = async () => {
  loading.value = true;
  try {
    const params: any = { ...queryParams };
    if (params.userId) {
      params.userId = parseInt(params.userId);
    }
    const res = await getBehaviorEvents(params);
    eventList.value = res?.data?.records || [];
    total.value = res?.data?.total || 0;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const getStats = async () => {
  try {
    const res = await getBehaviorStats(1);
    stats.value = res?.data || {};
  } catch (error) {
    console.error(error);
  }
};

const viewDetail = (row: any) => {
  currentEvent.value = row;
  detailVisible.value = true;
};

const formatJson = (jsonStr: string) => {
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2);
  } catch {
    return jsonStr;
  }
};

const getEventTypeTag = (type: string) => {
  const map: Record<string, string> = {
    LOGIN: 'success',
    LOGOUT: 'info',
    ACCESS: 'primary',
    MODIFY: 'warning',
    EXPORT: '',
    DELETE: 'danger'
  };
  return map[type] || '';
};

const initCharts = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value);
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '事件类型',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: 'LOGIN', itemStyle: { color: '#67c23a' } },
          { value: 20, name: 'LOGOUT', itemStyle: { color: '#909399' } },
          { value: 25, name: 'ACCESS', itemStyle: { color: '#409eff' } },
          { value: 10, name: 'MODIFY', itemStyle: { color: '#e6a23c' } },
          { value: 5, name: 'EXPORT', itemStyle: { color: '#9b59b6' } },
          { value: 5, name: 'DELETE', itemStyle: { color: '#f56c6c' } }
        ]
      }]
    });
  }

  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value);
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['正常事件', '异常事件'] },
      xAxis: { type: 'category', data: days },
      yAxis: { type: 'value' },
      series: [
        {
          name: '正常事件',
          type: 'line',
          smooth: true,
          data: [120, 132, 101, 134, 90, 30, 45],
          itemStyle: { color: '#67c23a' },
          areaStyle: { color: 'rgba(103, 194, 58, 0.3)' }
        },
        {
          name: '异常事件',
          type: 'line',
          smooth: true,
          data: [5, 8, 3, 6, 2, 1, 2],
          itemStyle: { color: '#f56c6c' },
          areaStyle: { color: 'rgba(245, 108, 108, 0.3)' }
        }
      ]
    });
  }
};

const handleResize = () => {
  pieChart?.resize();
  lineChart?.resize();
};

onMounted(() => {
  getList();
  getStats();
  initCharts();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  pieChart?.dispose();
  lineChart?.dispose();
});
</script>

<style scoped>
.behavior-analysis {
  padding: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.success {
  color: #67c23a;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.chart-row {
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

.event-data {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
}
</style>
