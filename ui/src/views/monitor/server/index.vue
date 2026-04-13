<template>
  <div class="app-container">
    <!-- 图表行 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="8" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><Cpu /></el-icon> CPU负载监控</span
            ></template
          >
          <div class="chart-container">
            <v-chart class="chart" :option="cpuOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><DataBoard /></el-icon> 内存使用监控 (柱状图)</span
            ></template
          >
          <div class="chart-container">
            <v-chart class="chart" :option="memoryBarOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><DataAnalysis /></el-icon> 内存使用占比 (饼图)</span
            ></template
          >
          <div class="chart-container">
            <v-chart class="chart" :option="memoryPieOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二个图表行 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="12" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><Odometer /></el-icon> 磁盘状态监控</span
            ></template
          >
          <div class="chart-container">
            <v-chart class="chart" :option="diskGaugeOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><Connection /></el-icon> 网络带宽监控</span
            ></template
          >
          <div class="chart-container">
            <v-chart class="chart" :option="networkLineOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 服务器信息表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24" class="card-box">
        <el-card>
          <template #header
            ><span
              ><el-icon><Tickets /></el-icon> 服务器信息</span
            ></template
          >
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table cellspacing="0" style="width: 100%">
              <tbody>
                <tr>
                  <td class="el-table__cell"><div class="cell">服务器名称</div></td>
                  <td class="el-table__cell">
                    <div class="cell">{{ serverInfo.serverName }}</div>
                  </td>
                  <td class="el-table__cell"><div class="cell">操作系统</div></td>
                  <td class="el-table__cell">
                    <div class="cell">{{ serverInfo.osName }}</div>
                  </td>
                </tr>
                <tr>
                  <td class="el-table__cell"><div class="cell">服务器IP</div></td>
                  <td class="el-table__cell">
                    <div class="cell">{{ serverInfo.ip }}</div>
                  </td>
                  <td class="el-table__cell"><div class="cell">系统架构</div></td>
                  <td class="el-table__cell">
                    <div class="cell">{{ serverInfo.osArch }}</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { getServerMonitor } from "@/api/monitor/server";

const serverInfo = ref({
  serverName: "-",
  osName: "-",
  ip: "-",
  osArch: "-",
});

const netTime = ref<string[]>([]);
const netUpData = ref<number[]>([]);
const netDownData = ref<number[]>([]);
let timer: number | undefined;

// CPU 饼图配置
const cpuOption = ref({
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c}%",
  },
  legend: {
    bottom: 10,
    left: "center",
    data: ["用户使用率", "系统使用率", "当前空闲率"],
  },
  series: [
    {
      name: "CPU 状态",
      type: "pie",
      radius: ["40%", "70%"],
      center: ["50%", "45%"],
      itemStyle: {
        borderRadius: 10,
        borderColor: "#fff",
        borderWidth: 2,
      },
      data: [
        { value: 0, name: "用户使用率", itemStyle: { color: "#409EFF" } },
        { value: 0, name: "系统使用率", itemStyle: { color: "#E6A23C" } },
        { value: 0, name: "当前空闲率", itemStyle: { color: "#67C23A" } },
      ],
    },
  ],
});

// 内存柱状图配置
const memoryBarOption = ref({
  tooltip: {
    trigger: "axis",
    axisPointer: { type: "shadow" },
  },
  grid: {
    left: "3%",
    right: "4%",
    bottom: "3%",
    containLabel: true,
  },
  xAxis: {
    type: "category",
    data: ["总内存", "已用内存", "剩余内存"],
    axisTick: { alignWithLabel: true },
  },
  yAxis: {
    type: "value",
    name: "GB",
  },
  series: [
    {
      name: "内存 (GB)",
      type: "bar",
      barWidth: "40%",
      data: [
        { value: 0, itemStyle: { color: "#409EFF" } },
        { value: 0, itemStyle: { color: "#F56C6C" } },
        { value: 0, itemStyle: { color: "#67C23A" } },
      ],
    },
  ],
});

// 内存占比饼图
const memoryPieOption = ref({
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} GB ({d}%)",
  },
  legend: {
    bottom: 10,
    left: "center",
    data: ["已用内存", "剩余内存"],
  },
  series: [
    {
      name: "内存占比",
      type: "pie",
      radius: "60%",
      center: ["50%", "45%"],
      data: [
        { value: 0, name: "已用内存", itemStyle: { color: "#F56C6C" } },
        { value: 0, name: "剩余内存", itemStyle: { color: "#67C23A" } },
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
      },
    },
  ],
});

// 磁盘使用率仪表盘
const diskGaugeOption = ref({
  tooltip: {
    formatter: "{a} <br/>{b} : {c}%",
  },
  series: [
    {
      name: "系统盘",
      type: "gauge",
      progress: {
        show: true,
      },
      detail: {
        valueAnimation: true,
        formatter: "{value}%",
      },
      data: [
        {
          value: 0,
          name: "磁盘已用",
          itemStyle: { color: "#E6A23C" },
        },
      ],
    },
  ],
});

// 网络带宽折线图
const networkLineOption = ref({
  tooltip: {
    trigger: "axis",
  },
  legend: {
    data: ["上行带宽", "下行带宽"],
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
    data: [],
  },
  yAxis: {
    type: "value",
    name: "Mbps",
  },
  series: [
    {
      name: "上行带宽",
      type: "line",
      smooth: true,
      data: [],
      itemStyle: { color: "#67C23A" },
    },
    {
      name: "下行带宽",
      type: "line",
      smooth: true,
      data: [],
      itemStyle: { color: "#409EFF" },
    },
  ],
});

const fetchMonitor = async () => {
  const res = await getServerMonitor();
  const data = res.data;

  serverInfo.value = {
    serverName: data.serverName,
    osName: data.osName,
    ip: data.ip,
    osArch: data.osArch,
  };

  const cpuSeries = cpuOption.value.series[0].data;
  cpuSeries[0].value = data.cpuUser;
  cpuSeries[1].value = data.cpuSystem;
  cpuSeries[2].value = data.cpuIdle;
  cpuOption.value = { ...cpuOption.value };

  const memoryData = memoryBarOption.value.series[0].data;
  memoryData[0].value = data.memoryTotalGb;
  memoryData[1].value = data.memoryUsedGb;
  memoryData[2].value = data.memoryFreeGb;
  memoryBarOption.value = { ...memoryBarOption.value };

  memoryPieOption.value.series[0].data[0].value = data.memoryUsedGb;
  memoryPieOption.value.series[0].data[1].value = data.memoryFreeGb;
  memoryPieOption.value = { ...memoryPieOption.value };

  diskGaugeOption.value.series[0].data[0].value = data.diskUsage;
  diskGaugeOption.value = { ...diskGaugeOption.value };

  const now = new Date();
  const timeLabel = `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}:${String(now.getSeconds()).padStart(2, "0")}`;
  netTime.value.push(timeLabel);
  netUpData.value.push(data.netUpMbps);
  netDownData.value.push(data.netDownMbps);
  if (netTime.value.length > 12) {
    netTime.value.shift();
    netUpData.value.shift();
    netDownData.value.shift();
  }
  networkLineOption.value.xAxis.data = [...netTime.value];
  networkLineOption.value.series[0].data = [...netUpData.value];
  networkLineOption.value.series[1].data = [...netDownData.value];
  networkLineOption.value = { ...networkLineOption.value };
};

onMounted(() => {
  fetchMonitor();
  timer = window.setInterval(fetchMonitor, 5000);
});

onUnmounted(() => {
  if (timer) {
    window.clearInterval(timer);
  }
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.card-box {
  margin-bottom: 20px;
}
.chart-container {
  height: 300px;
  width: 100%;
}
.chart {
  height: 100%;
  width: 100%;
}
table {
  border-collapse: collapse;
}
td,
th {
  padding: 12px 0;
  min-width: 0;
  box-sizing: border-box;
  text-overflow: ellipsis;
  vertical-align: middle;
  position: relative;
  text-align: left;
}
.el-table__cell {
  border-bottom: 1px solid #ebeef5;
}
</style>
