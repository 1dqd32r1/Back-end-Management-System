<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="data-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <div class="card-body">
            <div class="card-icon" style="color: #40c9c6">
              <el-icon><User /></el-icon>
            </div>
            <div class="card-desc">
              <div class="card-title">总用户数</div>
              <div class="card-number">102,400</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <div class="card-body">
            <div class="card-icon" style="color: #36a3f7">
              <el-icon><ChatDotSquare /></el-icon>
            </div>
            <div class="card-desc">
              <div class="card-title">消息数量</div>
              <div class="card-number">81,212</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <div class="card-body">
            <div class="card-icon" style="color: #f4516c">
              <el-icon><Money /></el-icon>
            </div>
            <div class="card-desc">
              <div class="card-title">交易金额</div>
              <div class="card-number">9,280</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <div class="card-body">
            <div class="card-icon" style="color: #34bfa3">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="card-desc">
              <div class="card-title">订单总数</div>
              <div class="card-number">13,600</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span
              ><el-icon><TrendCharts /></el-icon> 系统访问量趋势</span
            >
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="visitOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
// 不需要手动 use，因为 main.ts 已经全量引入 echarts 并注册了 v-chart
const visitOption = ref({
  backgroundColor: "transparent",
  tooltip: {
    trigger: "axis",
  },
  legend: {
    data: ["昨日访问", "今日访问"],
  },
  grid: {
    left: "3%",
    right: "4%",
    bottom: "3%",
    containLabel: true,
  },
  xAxis: {
    type: "category",
    boundaryGap: false,
    data: ["00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00"],
  },
  yAxis: {
    type: "value",
  },
  series: [
    {
      name: "昨日访问",
      type: "line",
      smooth: true,
      data: [120, 132, 101, 134, 90, 230, 210],
      itemStyle: { color: "#E6A23C" },
    },
    {
      name: "今日访问",
      type: "line",
      smooth: true,
      data: [220, 182, 191, 234, 290, 330, 310],
      itemStyle: { color: "#409EFF" },
    },
  ],
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.data-row {
  margin-bottom: 20px;
}
.data-card {
  height: 108px;
  cursor: pointer;
  overflow: hidden;
}
:deep(.el-card__body) {
  padding: 0;
  height: 100%;
}
.card-body {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 20px;
}
.card-icon {
  font-size: 48px;
  display: flex;
  align-items: center;
}
.card-desc {
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;
}
.card-title {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 4px;
}
.card-number {
  font-size: 20px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}
.chart-container {
  height: 350px;
  width: 100%;
}
.chart {
  height: 100%;
  width: 100%;
}
</style>
